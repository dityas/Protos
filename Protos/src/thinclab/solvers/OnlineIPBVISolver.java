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
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.belief.InteractiveBelief;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.frameworks.IPOMDP;
import thinclab.legacy.AlphaVector;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public class OnlineIPBVISolver extends OnlineInteractiveSymbolicPerseus {
	
	/* Variables to hold point based values */
	double[][] currentPointBasedValues;
	double[][] newPointBasedValues;
	
	/* Variables to hold AlphaVectors */
//	DD[] alphaVectors;
	AlphaVector[] newAlphaVectors;
	int numNewAlphaVectors;
	
	/* policy holders */
	int[] policy;
	double[] policyvalue;
	boolean[] uniquePolicy;
	
	private static final Logger logger = Logger.getLogger(OnlineIPBVISolver.class);
	
	// -----------------------------------------------------------------------------------------
	
	public OnlineIPBVISolver(
			IPOMDP ipomdp, 
			BeliefRegionExpansionStrategy b,
			int maxRounds,
			int dpBackups) {
		
		/* Call super */
		super(ipomdp, b, maxRounds, dpBackups);
		
		/* initialize unique policy */
		this.uniquePolicy = new boolean[this.ipomdp.Ai.size()];
	}
	
	// -------------------------------------------------------------------------------------------
	
	@Override
	public void solveForBeliefs(List<DD> beliefs) {
		/*
		 * Use online symbolic perseus to get solution for given beliefs
		 */
		
		logger.debug("Solving for " + beliefs.size() + " belief points.");
		
		DD[][] factoredBeliefRegion = 
				InteractiveBelief.factorInteractiveBeliefRegion(
						(IPOMDP) this.f, 
						beliefs);
		
		/* Make a default alphaVectors as rewards to start with */
		this.alphaVectors = 
				this.ipomdp.currentRi.values().stream()
					.map(a -> OP.reorder(a)).collect(Collectors.toList()).toArray(
						new DD[this.ipomdp.currentRi.size()]);
		
		/* try running interactive symbolic perseus */
		try {
			
			for (int r = 0; r < this.maxRounds; r++) {
				
				this.IPBVI(
						100, 
						r * this.dpBackups, 
						this.dpBackups, 
						factoredBeliefRegion);
				
			}
		}
		
		catch (Exception e) {
			logger.error("While running solver: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	@Override
	public void computeMaxMinImprovement(DD[][] beliefRegion) {
		
		double imp;
		ipomdp.bestImprovement = Double.NEGATIVE_INFINITY;
		ipomdp.worstDecline = Double.POSITIVE_INFINITY;
		
		for (int j = 0; j < beliefRegion.length; j++) {
			/*
			 * find biggest improvement at this belief point
			 */
			imp = OP.max(
					this.newPointBasedValues[j], 
					this.numNewAlphaVectors) 
					- OP.max(this.currentPointBasedValues[j]);
			
			if (imp > ipomdp.bestImprovement)
				ipomdp.bestImprovement = imp;
			if (imp < ipomdp.worstDecline)
				ipomdp.worstDecline = imp;
		}
	}
	
	@Override
	public String getBestActionAtCurrentBelief() {
		
		int alphaId = 
				this.ipomdp.policyBestAlphaMatch(
						this.ipomdp.getInitialBeliefs().get(0), 
						this.alphaVectors, 
						this.policy);
		
		return this.ipomdp.Ai.get(this.policy[alphaId]);
	}
	
	// --------------------------------------------------------------------------------------------
	
	public void IPBVI(
			int maxAlpha, 
			int firstStep,
			int nSteps,
			DD[][] beliefRegion) throws ZeroProbabilityObsException, VariableNotFoundException {
		
		double bellmanErr;
		double[] onezero = { 0 };
		double steptolerance;

		int maxAlphaSetSize = maxAlpha;

		bellmanErr = 20 * this.ipomdp.tolerance;
		
		this.currentPointBasedValues = 
				OP.factoredExpectationSparseNoMem(
						beliefRegion, this.alphaVectors);
		
		DD[] primedV;
		double maxAbsVal = 0;
		
		for (int stepId = firstStep; stepId < firstStep + nSteps; stepId++) {
			logger.debug("STEP:=====================================================================");
//			logger.debug("A vecs are: " + Arrays.toString(this.alphaVectors));
			steptolerance = ipomdp.tolerance;

			primedV = new DD[this.alphaVectors.length];
			
			for (int i = 0; i < this.alphaVectors.length; i++) {
				primedV[i] = 
						OP.primeVars(
								this.alphaVectors[i], 
								ipomdp.S.size() + ipomdp.Omega.size());
			}
			
			maxAbsVal = 
					Math.max(
							OP.maxabs(
									IPOMDP.concatenateArray(
											OP.maxAllN(this.alphaVectors), 
											OP.minAllN(this.alphaVectors))), 1e-10);

			int nDpBackups = 0;
			
			/*
			 * could be one more than the maximum number at most
			 */
			this.newAlphaVectors = new AlphaVector[maxAlphaSetSize + 1];
			this.newPointBasedValues = new double[beliefRegion.length][maxAlphaSetSize + 1];
			this.numNewAlphaVectors = 0;
						
			AlphaVector newVector;

			double[] newValues;
			double improvement;

			/*
			 * we allow the number of new alpha vectors to get one bigger than the maximum allowed size, 
			 * since we may be able to cull more than one alpha vector when trimming, bringing us back 
			 * below the cutoff
			 */
			
			for (int i = 0; i < beliefRegion.length; i++) {
				Global.newHashtables();
				/*
				 * dpBackup
				 */
				
				newVector = dpBackup(beliefRegion[i], primedV, maxAbsVal);

//				newVector.alphaVector = OP.approximate(
//						newVector.alphaVector, bellmanErr * (1 - ipomdp.discFact)
//								/ 2.0, onezero);
				newVector.setWitness(i);

				nDpBackups = nDpBackups + 1;
				
				/*
				 * merge and trim
				 */
				newValues = 
						OP.factoredExpectationSparseNoMem(
								beliefRegion, 
								newVector.alphaVector);

				if (this.numNewAlphaVectors < 1)
					improvement = Double.POSITIVE_INFINITY; 
				
				else
					improvement = 
						OP.max(
								OP.sub(
										newValues, 
										OP.getMax(
												this.newPointBasedValues, 
												this.numNewAlphaVectors)));
				
				if (improvement > ipomdp.tolerance) {
					for (int belid = 0; belid < beliefRegion.length; belid++) {
						this.newPointBasedValues[belid][this.numNewAlphaVectors] = 
							newValues[belid];
					}
					logger.debug("improvement over previous is " + improvement);
					this.newAlphaVectors[this.numNewAlphaVectors] = newVector;
					this.numNewAlphaVectors++;
				}
			}

			computeMaxMinImprovement(beliefRegion);

			/*
			 * save data and copy over new to old
			 */
			this.alphaVectors = new DD[this.numNewAlphaVectors];
			this.currentPointBasedValues = 
					new double[this.newPointBasedValues.length][this.numNewAlphaVectors];

			this.policy = new int[this.numNewAlphaVectors];
			this.policyvalue = new double[this.numNewAlphaVectors];
			
			for (int j = 0; j < ipomdp.Ai.size(); j++)
				this.uniquePolicy[j] = false;

			for (int j = 0; j < this.numNewAlphaVectors; j++) {
				
				this.alphaVectors[j] = this.newAlphaVectors[j].alphaVector;
				this.policy[j] = this.newAlphaVectors[j].actId;
				this.policyvalue[j] = this.newAlphaVectors[j].value;
				this.uniquePolicy[this.policy[j]] = true;
			}

			for (int j = 0; j < beliefRegion.length; j++) {
				System.arraycopy(
						this.newPointBasedValues[j], 
						0, 
						this.currentPointBasedValues[j], 
						0, 
						this.numNewAlphaVectors);
			}

			bellmanErr = Math.min(10, Math.max(ipomdp.bestImprovement, -ipomdp.worstDecline));
			
			logger.info("I: " + stepId 
					+ " \tB ERROR: " + Double.toString(bellmanErr).substring(0, 5)
					+ " \tUSED/TOTAL BELIEFS: " + nDpBackups + "/" + beliefRegion.length
					+ " \tA VECTORS: " + this.alphaVectors.length);
			
			if (stepId % 100 < 5)
				continue;
			
			if (bellmanErr < 0.01) {
				logger.warn("BELLMAN ERROR LESS THAN 0.01. COVERGENCE! SOFTWARE VERSION 7.0... "
						+ "LOOKING AT LIFE THROUGH THE EYES OF A TIRED HEART.");
				break;
			}
			
		}

	}

}
