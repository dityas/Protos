/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.POMDP;
import thinclab.legacy.AlphaVector;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public class OfflinePBVISolver extends OfflineSolver {
	
	/*
	 * Offline PBVI solver for POMDPs
	 */
	
	private static final long serialVersionUID = -8890386097993230541L;
	
	/* Variables to hold point based values */
	double[][] currentPointBasedValues;
	double[][] newPointBasedValues;
	double bestImprovement;
	double worstDecline;

	/* Variables to hold AlphaVectors */
	public DD[] alphaVectors;
	AlphaVector[] newAlphaVectors;
	int numNewAlphaVectors;

	/* policy holders */
	int[] policy;
	double[] policyvalue;
	boolean[] uniquePolicy;
	
	/* maintain a reference to the POMDP */
	POMDP p;
	
	/* PBVI rounds and dpBackups */
	int numRounds;
	int numDpBackups;
	
	private static final Logger logger = Logger.getLogger(OfflinePBVISolver.class);
	
	// -----------------------------------------------------------------------------------
	
	public OfflinePBVISolver(
			POMDP pomdp, 
			BeliefRegionExpansionStrategy be,
			int numRounds,
			int numDpBackups) {
		
		/* call super */
		super(pomdp, be);
		
		/* populate attributes */
		this.p = pomdp;
		this.numRounds = numRounds;
		this.numDpBackups = numDpBackups;
		
		/* initialize unique policy */
		this.uniquePolicy = new boolean[this.p.getActions().size()];
	}
	
	// ----------------------------------------------------------------------------------
	/*
	 * super class overrides.
	 */
	
	@Override
	public void solve() {
		
		/* Make a default alphaVectors as rewards to start with */
		this.alphaVectors = 
				Arrays.stream(this.p.actions)
					.map(a -> OP.reorder(a.rewFn))
					.collect(Collectors.toList())
					.toArray(new DD[this.p.actions.length]);
		
		/* default policy */
		this.policy = new int[this.p.getActions().size()];
		for (int i = 0; i < this.p.actions.length; i++)
			this.policy[i] = this.p.getActions().indexOf(this.p.actions[i].name);
		
		/* set initial policy */
		if (this.expansionStrategy instanceof SSGABeliefExpansion) {
			logger.debug("Setting default policy for belief expansion");
			((SSGABeliefExpansion) this.expansionStrategy).setRecentPolicy(
					this.alphaVectors, this.policy);
		}
				
		for (int r = 0; r < this.numRounds; r++) {
			
			/* Explore belief space */
			this.expansionStrategy.expand();
			List<DD> beliefs = this.expansionStrategy.getBeliefPoints();
			
			/* Solve for the explored beliefs */
			this.solveForBeliefs(beliefs);
			
			if (this.expansionStrategy instanceof SSGABeliefExpansion) {
				logger.debug("Updating expansion policy");
				((SSGABeliefExpansion) this.expansionStrategy).setRecentPolicy(
						this.alphaVectors, this.policy);
			}
		}
	}
	
	@Override
	public void solveForBeliefs(List<DD> beliefs) {
		
		/*
		 * Use online PBVI to get solution for given beliefs
		 */

		logger.debug("Solving for " + beliefs.size() + " belief points.");

		DD[][] factoredBeliefRegion = this.p.factorBeliefRegion(beliefs);
		
		/* try running IPBVI */
		try {
			this.PBVI(100, 0, this.numDpBackups, factoredBeliefRegion);
		}

		catch (Exception e) {
			logger.error("While running solver: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public String getActionForBelief(DD belief) {
		/*
		 * Return the best action at belief using the offline policy
		 */
		
		return DecisionProcess.getActionFromPolicy(
				(POMDP) f, belief, this.alphaVectors, this.policy);
	}
	
	@Override
	public boolean hasSolution() {
		return (this.alphaVectors != null);
	}
	
	// --------------------------------------------------------------------------------------
	/*
	 * Backup and PBVI functions
	 */
	
	@SuppressWarnings("unused")
	public void PBVI(
			int maxAlpha, 
			int firstStep,
			int nSteps,
			DD[][] belRegion) {
		
		double bellmanErr;
		double[] onezero = { 0 };
		double steptolerance;

		int maxAlphaSetSize = maxAlpha;

		bellmanErr = 20 * this.p.tolerance;

		/* compute point based values using current alpha vectors */
		currentPointBasedValues = OP.factoredExpectationSparseNoMem(belRegion,
				alphaVectors);
		
		DD[] primedV;
		double maxAbsVal = 0;

		for (int stepId = firstStep; stepId < firstStep + nSteps; stepId++) {

			steptolerance = this.p.tolerance;

			primedV = new DD[alphaVectors.length];
			for (int i = 0; i < alphaVectors.length; i++) {
				primedV[i] = OP.primeVars(alphaVectors[i], this.p.nVars);
			}
			
			maxAbsVal = 
					Math.max(
							OP.maxabs(
									ArrayUtils.addAll(
											OP.maxAllN(alphaVectors), 
											OP.minAllN(alphaVectors))), 
							1e-10);

			/* could be one more than the maximum number at most */
			newAlphaVectors = new AlphaVector[maxAlphaSetSize + 1];
			newPointBasedValues = new double[belRegion.length][maxAlphaSetSize + 1];
			numNewAlphaVectors = 0;

			AlphaVector newVector;
			double[] newValues;
			double improvement;

			/*
			 * we allow the number of new alpha vectors to get one bigger than the 
			 * maximum allowed size, since we may be able to cull more than one alpha 
			 * vector when trimming, bringing us back below the cutoff
			 */
			int numUsed = 0;
			for (int i = 0; i < belRegion.length; i++) {
				
				Global.newHashtables();

				/* perform the backup operation */
				newVector = 
						AlphaVector.dpBackup(
								this.p, belRegion[i], primedV, maxAbsVal, this.alphaVectors.length);
				
				numUsed += 1;
				newVector.alphaVector = OP.approximate(
						newVector.alphaVector, bellmanErr * (1 - this.p.discFact)
								/ 2.0, onezero);
				newVector.setWitness(i);

				/* merge and trim */
				newValues = OP.factoredExpectationSparseNoMem(belRegion,
						newVector.alphaVector);

				if (numNewAlphaVectors < 1) {
					improvement = Double.POSITIVE_INFINITY;
				} 
				
				else {
					improvement = OP.max(OP.sub(newValues, OP.getMax(
							newPointBasedValues, numNewAlphaVectors)));
				}

				if (improvement > this.p.tolerance) {

					for (int belid = 0; belid < belRegion.length; belid++)
						newPointBasedValues[belid][numNewAlphaVectors] = newValues[belid];

					newAlphaVectors[numNewAlphaVectors] = newVector;
					numNewAlphaVectors++;
				}
				
			}

			this.computeMaxMinImprovement(belRegion);

			/* save data and copy over new to old */
			alphaVectors = new DD[numNewAlphaVectors];
			currentPointBasedValues = new double[newPointBasedValues.length][numNewAlphaVectors];

			policy = new int[numNewAlphaVectors];
			policyvalue = new double[numNewAlphaVectors];
			for (int j = 0; j < this.p.nActions; j++)
				uniquePolicy[j] = false;

			for (int j = 0; j < numNewAlphaVectors; j++) {
				
				alphaVectors[j] = newAlphaVectors[j].alphaVector;
				policy[j] = newAlphaVectors[j].actId;
				policyvalue[j] = newAlphaVectors[j].value;
				uniquePolicy[policy[j]] = true;
			}

			for (int j = 0; j < belRegion.length; j++) {
				System.arraycopy(newPointBasedValues[j], 0,
						currentPointBasedValues[j], 0, numNewAlphaVectors);
			}

			bellmanErr = Math.min(10, Math.max(bestImprovement, -worstDecline));
			float errorVar = this.getErrorVariance((float) bellmanErr);
			
			logger.info("STEP: " + stepId 
					+ " \tB ERROR: " + String.format(Locale.US, "%.03f", bellmanErr)
					+ " \tUSED/BELIEF POINTS: " + numUsed + "/" + belRegion.length
					+ " \tA VECTORS: " + alphaVectors.length);
			
			if (stepId % 100 < 5) continue;
			
			if (bellmanErr < 0.01) {
				logger.warn("BELLMAN ERROR LESS THAN 0.01. PROBABLY CONVERGED.");
				break;
			}
			
			if (stepId > 15 && errorVar < 0.0000001) {
				logger.warn("DECLARING APPROXIMATE CONVERGENCE AT ERROR: " + bellmanErr
						+ " BECAUSE OF LOW ERROR VARIANCE " + errorVar);
				break;
			}
		}

	}
	
	public void computeMaxMinImprovement(DD[][] beliefRegion) {

		double imp;
		this.bestImprovement = Double.NEGATIVE_INFINITY;
		this.worstDecline = Double.POSITIVE_INFINITY;

		for (int j = 0; j < beliefRegion.length; j++) {
			/*
			 * find biggest improvement at this belief point
			 */
			imp = OP.max(this.newPointBasedValues[j], this.numNewAlphaVectors)
					- OP.max(this.currentPointBasedValues[j]);

			if (imp > this.bestImprovement)
				this.bestImprovement = imp;
			if (imp < this.worstDecline)
				this.worstDecline = imp;
		}
	}
	
	// ----------------------------------------------------------------------------------
	/*
	 * Getters for policy variables
	 */
	
	public DD[] getAlphaVectors() {
		return this.alphaVectors;
	}
	
	public int[] getPolicy() {
		return this.policy;
	}

}
