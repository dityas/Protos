/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.AlphaVector;
import thinclab.legacy.DD;
import thinclab.legacy.OP;
import thinclab.utils.NextBelStateCache;

/*
 * @author adityas
 *
 */
public class OfflinePBVISolver extends AlphaVectorPolicySolver {
	
	/*
	 * Offline PBVI solver for POMDPs
	 */
	
	private static final long serialVersionUID = -8890386097993230541L;
	
	/* Variables to hold point based values */
	double[][] currentPointBasedValues;
	double[][] newPointBasedValues;
	double bestImprovement;
	double worstDecline;

	/* maintain a reference to the POMDP */
	POMDP p;
	
	/* PBVI rounds and dpBackups */
	int numRounds;
	int numDpBackups;
	
	private static final Logger LOGGER = Logger.getLogger(OfflinePBVISolver.class);
	
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
	
	@Override
	public void solveCurrentStep() {
		LOGGER.error("This method is not applicable for an offline solver.");
	}
	
	@Override
	public void solve() {
		
		/* set initial policy */
		if (this.expansionStrategy instanceof SSGABeliefExpansion) {
			LOGGER.debug("Setting default policy for belief expansion");
			((SSGABeliefExpansion) this.expansionStrategy).setRecentPolicy(
					this.alphaVectors, this.policy);
		}
				
		for (int r = 0; r < this.numRounds; r++) {
			
			/* Explore belief space */
			this.expansionStrategy.expand();
			List<DD> beliefs = this.expansionStrategy.getBeliefPoints();
			
			/* Solve for the explored beliefs */
			this.solveForBeliefs(beliefs);
			
			/* break if max beliefs reached */
			if (this.expansionStrategy.maxBeliefsReached()) {
				LOGGER.info("Reached maximum number of beliefs. Breaking...");
				break;
			}
			
			if (this.expansionStrategy instanceof SSGABeliefExpansion) {
				LOGGER.debug("Updating expansion policy");
				((SSGABeliefExpansion) this.expansionStrategy).setRecentPolicy(
						this.alphaVectors, this.policy);
			}
		}
		
		if (NextBelStateCache.cachingAllowed())
			NextBelStateCache.clearCache();
	}
	
	@Override
	public void solveForBeliefs(List<DD> beliefs) {
		
		/*
		 * Use online PBVI to get solution for given beliefs
		 */

		LOGGER.debug("Solving for " + beliefs.size() + " belief points.");

		DD[][] factoredBeliefRegion = this.p.factorBeliefRegion(beliefs);
		
		/* try running IPBVI */
		try {
			this.PBVI(100, 0, this.numDpBackups, factoredBeliefRegion);
		}

		catch (Exception e) {
			LOGGER.error("While running solver: " + e.getMessage());
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
		currentPointBasedValues = OP.factoredExpectationSparse(belRegion,
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

			this.computeMaxMinImprovement(belRegion.length);

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
			
			LOGGER.info("STEP: " + stepId 
					+ " \tB ERROR: " + String.format(Locale.US, "%.03f", bellmanErr)
					+ " \tUSED/BELIEF POINTS: " + numUsed + "/" + belRegion.length
					+ " \tA VECTORS: " + alphaVectors.length);
			
			if (stepId % 100 < 1) continue;
			
			if (bellmanErr < 0.01) {
				LOGGER.warn("BELLMAN ERROR LESS THAN 0.01. PROBABLY CONVERGED.");
				break;
			}
			
			if (stepId > 20 && errorVar < 0.0001) {
				LOGGER.warn("DECLARING APPROXIMATE CONVERGENCE AT ERROR: " + bellmanErr
						+ " BECAUSE OF LOW ERROR VARIANCE " + errorVar);
				break;
			}
		}

	}
	
	public void computeMaxMinImprovement(int belRegionSize) {

		double imp;
		this.bestImprovement = Double.NEGATIVE_INFINITY;
		this.worstDecline = Double.POSITIVE_INFINITY;

		for (int j = 0; j < belRegionSize; j++) {
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

	@Override
	public String getActionAtCurrentBelief() {
		LOGGER.error("This method is not applicable for this type of solver");
		return null;
	}

	@Override
	public void nextStep(String action, List<String> obs) throws ZeroProbabilityObsException {
		
		try {
			this.f.step(this.f.getCurrentBelief(), action, obs.stream().toArray(String[]::new));
		} 
		
		catch (Exception e) {
			LOGGER.error("While stepping: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
