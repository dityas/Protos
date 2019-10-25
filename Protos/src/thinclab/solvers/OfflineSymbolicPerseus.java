/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.belief.Belief;
import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.POMDP;
import thinclab.legacy.AlphaVector;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.legacy.RandomPermutation;

/*
 * @author adityas
 *
 */
public class OfflineSymbolicPerseus extends OfflinePBVISolver {

	/*
	 * Jesse Hoey's symbolic perseus for POMDPs
	 */
	
	private static final long serialVersionUID = 3643319647660633983L;
	private static final Logger logger = Logger.getLogger(OfflineSymbolicPerseus.class);
	
	// -------------------------------------------------------------------------------------
	
	public OfflineSymbolicPerseus(
			POMDP pomdp, 
			BeliefRegionExpansionStrategy be,
			int numRounds,
			int numDpBackups) {
		
		/* call super */
		super(pomdp, be, numRounds, numDpBackups);
		
		logger.info("OfflineSymbolicPerseus initialized for " + this.numRounds + " rounds and "
				+ this.numDpBackups + " backup iterations");
	}
	
	// ----------------------------------------------------------------------------------------
	
	@Override
	public void solveForBeliefs(List<DD> beliefs) {
		
		/*
		 * Use online PBVI to get solution for given beliefs
		 */

		logger.debug("Solving for " + beliefs.size() + " belief points.");

		DD[][] factoredBeliefRegion = Belief.factorBeliefRegion(this.p, beliefs);

		/* try running IPBVI */
		try {
			this.SymbolicPerseus(100, 0, this.numDpBackups, factoredBeliefRegion);
		}

		catch (Exception e) {
			logger.error("While running solver: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void SymbolicPerseus(
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
			
			int count = 0;
			int choice;
			int nDpBackups = 0;
			RandomPermutation permutedIds = 
					new RandomPermutation(Global.random, belRegion.length, false);

			AlphaVector newVector;
			double[] newValues;
			double[] diff = new double[belRegion.length];
			double[] maxcurrpbv;
			double[] maxnewpbv;
			double improvement;

			/*
			 * we allow the number of new alpha vectors to get one bigger than the 
			 * maximum allowed size, since we may be able to cull more than one alpha 
			 * vector when trimming, bringing us back below the cutoff
			 */
			int numUsed = 0;
			
			while (numNewAlphaVectors < maxAlphaSetSize && !permutedIds.isempty()) {
				
				if (nDpBackups >= 2 * alphaVectors.length) {
					
					this.computeMaxMinImprovement(belRegion);
					if (bestImprovement > this.p.tolerance 
							&& bestImprovement > -2 * worstDecline) 
						break;
				}
				
				Global.newHashtables();
				count = count + 1;

				if (numNewAlphaVectors == 0) {
					choice = 0;
				} 
				
				else {

					maxcurrpbv = OP.getMax(currentPointBasedValues,
							permutedIds.permutation);
					maxnewpbv = OP.getMax(newPointBasedValues,
							numNewAlphaVectors, permutedIds.permutation);
					permutedIds.getNewDoneIds(maxcurrpbv, maxnewpbv,
							steptolerance);
					diff = permutedIds.getDiffs(maxcurrpbv, maxnewpbv,
							steptolerance);
					
					if (permutedIds.isempty())
						break;
					
					choice = OP.sampleMultinomial(diff);
				}

				int i = permutedIds.getSetDone(choice);

				if (numNewAlphaVectors < 1
						|| (OP.max(newPointBasedValues[i], numNewAlphaVectors)
								- OP.max(currentPointBasedValues[i]) < steptolerance)) {
				
					Global.newHashtables();
	
					/* perform the backup operation */
					newVector = 
							AlphaVector.dpBackup(
									this.p, 
									belRegion[i], 
									primedV, 
									maxAbsVal, 
									this.alphaVectors.length);
					
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
					+ " \tBELLMAN ERROR: " + bellmanErr
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
	
	public static OfflineSymbolicPerseus createSolverWithSSGAExpansion(
			DecisionProcess DP,
			int searchDepth,
			int searchIterations,
			int rounds,
			int backups) {
		/*
		 * Creates a Offline Symbolic Perseus Solver with given params
		 */
		
		return new OfflineSymbolicPerseus(
				(POMDP) DP, 
				new SSGABeliefExpansion((POMDP) DP, searchDepth, searchIterations), 
				rounds, 
				backups);
	}
}
