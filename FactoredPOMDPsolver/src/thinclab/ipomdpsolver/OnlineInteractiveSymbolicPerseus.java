/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.ipomdpsolver.InteractiveBelief.InteractiveBelief;
import thinclab.ipomdpsolver.InteractiveBelief.LookAheadTree;
import thinclab.symbolicperseus.AlphaVector;
import thinclab.symbolicperseus.Config;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.DDleaf;
import thinclab.symbolicperseus.Global;
import thinclab.symbolicperseus.MySet;
import thinclab.symbolicperseus.OP;
import thinclab.symbolicperseus.RandomPermutation;
import thinclab.symbolicperseus.Belief.BeliefSet;
import thinclab.symbolicperseus.NextBelState;

/*
 * @author adityas
 *
 */
public class OnlineInteractiveSymbolicPerseus {
	/*
	 * Value iteration solver for IPOMDPs with finite look ahead horizon
	 * 
	 * 
	 */
	
	private static final Logger logger = 
			Logger.getLogger(OnlineInteractiveSymbolicPerseus.class);
	
	public IPOMDP ipomdp;
		
	// --------------------------------------------------------------------------------------
	
	public HashMap<DD, String> getBestActionsMap() {
		/*
		 * Maps the initial beliefs of the current look ahead tree to the most optimal actions
		 */
		HashMap<DD, String> bestActionMap = new HashMap<DD, String>();
		
		for (DD belief : this.ipomdp.lookAheadRootInitBeliefs) {
			/*
			 * For each initial belief, get best action using the alphavectors and policy containers
			 * defined in the super class in the same way as symbolic perseus solver
			 * 
			 * best action can be found by POMDP.policy[POMDP.bestAlphaVector] for current celief
			 */
			int alphaId = 
					this.ipomdp.policyBestAlphaMatch(
							belief, 
							this.ipomdp.alphaVectors, 
							this.ipomdp.policy);

			bestActionMap.put(belief, this.ipomdp.Ai.get(this.ipomdp.policy[alphaId]));
		}
		
		return bestActionMap;
	}
	
	// --------------------------------------------------------------------------------------
	/*
	 * Trying to adapt symbolic perseus's dpBackUp here
	 */
	public OnlineInteractiveSymbolicPerseus(IPOMDP ipomdp) {
		/*
		 * Initialize with an IPOMDP
		 */
		this.ipomdp = ipomdp;
	}
	
	// ---------------------------------------------------------------------------------------
	
	public void solvePBVI(int rounds, int numDpBackups) {
		
		List<DD> iBeliefRegion = new ArrayList<DD>();

		/*
		 * Add all initial ipomdp beliefs to the belief region
		 * 
		 * Alternatively, use an expansion strategy like in symbolic perseus.
		 * And alternate expansions with dp backups
		 */
		iBeliefRegion.addAll(ipomdp.getCurrentLookAheadBeliefs());
		
		
		/*
		 * Initialize alpha vectors
		 */
		DD[] alphaVectors = new DD[1];
		alphaVectors[0] = DD.one;
		
		this.ipomdp.alphaVectors = alphaVectors;
		
		for (int r=0; r < rounds; r++) {

			try {
				
				/*Try modified symbolic perseus for IPOMDP */
				
				boundedPerseusStartFromCurrent(
						100, 
						r * numDpBackups, 
						numDpBackups, 
						InteractiveBelief.factorInteractiveBeliefRegion(ipomdp, iBeliefRegion));
				
			} 
			
			catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	public void boundedPerseusStartFromCurrent(
			int maxAlpha, 
			int firstStep,
			int nSteps,
			DD[][] beliefRegion) throws ZeroProbabilityObsException, VariableNotFoundException {
		
		double bellmanErr;
		double[] onezero = { 0 };
		double steptolerance;

		int maxAlphaSetSize = maxAlpha;

		bellmanErr = 20 * this.ipomdp.tolerance;

		this.ipomdp.currentPointBasedValues = 
				OP.factoredExpectationSparseNoMem(
						beliefRegion, ipomdp.alphaVectors);
		
		DD[] primedV;
		double maxAbsVal = 0;
		
		for (int stepId = firstStep; stepId < firstStep + nSteps; stepId++) {

			steptolerance = ipomdp.tolerance;

			primedV = new DD[ipomdp.alphaVectors.length];
			
			for (int i = 0; i < ipomdp.alphaVectors.length; i++) {
				primedV[i] = OP.primeVars(ipomdp.alphaVectors[i], ipomdp.S.size() + ipomdp.Omega.size());
			}
			
			maxAbsVal = 
					Math.max(
							OP.maxabs(
									IPOMDP.concatenateArray(
											OP.maxAllN(ipomdp.alphaVectors), 
											OP.minAllN(ipomdp.alphaVectors))), 1e-10);

			int count = 0;
			int choice;
			int nDpBackups = 0;
			
			RandomPermutation permutedIds = new RandomPermutation(
					Global.random, beliefRegion.length, false);
			
			/*
			 * could be one more than the maximum number at most
			 */
			ipomdp.newAlphaVectors = new AlphaVector[maxAlphaSetSize + 1];
			ipomdp.newPointBasedValues = new double[beliefRegion.length][maxAlphaSetSize + 1];
			ipomdp.numNewAlphaVectors = 0;
						
			AlphaVector newVector;
			double[] diff = new double[beliefRegion.length];
			double[] maxcurrpbv;
			double[] maxnewpbv;
			double[] newValues;
			double improvement;

			/*
			 * we allow the number of new alpha vectors to get one bigger than the maximum allowed size, 
			 * since we may be able to cull more than one alpha vector when trimming, bringing us back 
			 * below the cutoff
			 */
			while (ipomdp.numNewAlphaVectors < maxAlphaSetSize
					&& !permutedIds.isempty()) {
				
				if (nDpBackups >= 2 * ipomdp.alphaVectors.length) {
					computeMaxMinImprovement(beliefRegion);
					if (ipomdp.bestImprovement > ipomdp.tolerance
							&& ipomdp.bestImprovement > -2 * ipomdp.worstDecline) {

						break;
					}
				}

				Global.newHashtables();
				count = count + 1;

				if (ipomdp.numNewAlphaVectors == 0)
					choice = 0;
				
				else {

					maxcurrpbv = OP.getMax(ipomdp.currentPointBasedValues,
							permutedIds.permutation);
					maxnewpbv = OP.getMax(ipomdp.newPointBasedValues,
							ipomdp.numNewAlphaVectors, permutedIds.permutation);
					permutedIds.getNewDoneIds(maxcurrpbv, maxnewpbv,
							steptolerance);
					diff = permutedIds.getDiffs(maxcurrpbv, maxnewpbv,
							steptolerance);
					
					if (permutedIds.isempty())
						break;
					
					choice = OP.sampleMultinomial(diff);
				}

				int i = permutedIds.getSetDone(choice);

				if (ipomdp.numNewAlphaVectors < 1
						|| (OP.max(ipomdp.newPointBasedValues[i], ipomdp.numNewAlphaVectors)
								- OP.max(ipomdp.currentPointBasedValues[i]) < steptolerance)) {
					
					/*
					 * dpBackup
					 */
					
					newVector = dpBackup(beliefRegion[i], primedV, maxAbsVal);

					newVector.alphaVector = OP.approximate(
							newVector.alphaVector, bellmanErr * (1 - ipomdp.discFact)
									/ 2.0, onezero);
					newVector.setWitness(i);

					nDpBackups = nDpBackups + 1;
					
					/*
					 * merge and trim
					 */
					newValues = 
							OP.factoredExpectationSparseNoMem(
									beliefRegion, 
									newVector.alphaVector);
					
					if (ipomdp.numNewAlphaVectors < 1)
						improvement = Double.POSITIVE_INFINITY; 
					
					else
						improvement = 
							OP.max(
									OP.sub(
											newValues, 
											OP.getMax(
													ipomdp.newPointBasedValues, 
													ipomdp.numNewAlphaVectors)));
					
					if (improvement > ipomdp.tolerance) {
						
						for (int belid = 0; belid < beliefRegion.length; belid++) {
							ipomdp.newPointBasedValues[belid][ipomdp.numNewAlphaVectors] = 
								newValues[belid];
						}
						
						ipomdp.newAlphaVectors[ipomdp.numNewAlphaVectors] = newVector;
						ipomdp.numNewAlphaVectors++;
					}
				}
			}

			computeMaxMinImprovement(beliefRegion);

			/*
			 * save data and copy over new to old
			 */
			ipomdp.alphaVectors = new DD[ipomdp.numNewAlphaVectors];
			ipomdp.currentPointBasedValues = 
					new double[ipomdp.newPointBasedValues.length][ipomdp.numNewAlphaVectors];

			ipomdp.policy = new int[ipomdp.numNewAlphaVectors];
			ipomdp.policyvalue = new double[ipomdp.numNewAlphaVectors];
			
			for (int j = 0; j < ipomdp.Ai.size(); j++)
				ipomdp.uniquePolicy[j] = false;

			for (int j = 0; j < ipomdp.numNewAlphaVectors; j++) {
				
				ipomdp.alphaVectors[j] = ipomdp.newAlphaVectors[j].alphaVector;
				ipomdp.policy[j] = ipomdp.newAlphaVectors[j].actId;
				ipomdp.policyvalue[j] = ipomdp.newAlphaVectors[j].value;
				ipomdp.uniquePolicy[ipomdp.policy[j]] = true;
			}

			for (int j = 0; j < beliefRegion.length; j++) {
				System.arraycopy(
						ipomdp.newPointBasedValues[j], 
						0, 
						ipomdp.currentPointBasedValues[j], 
						0, 
						ipomdp.numNewAlphaVectors);
			}

			bellmanErr = Math.min(10, Math.max(ipomdp.bestImprovement, -ipomdp.worstDecline));
			
			logger.info("STEP: " + stepId 
					+ " \tBELLMAN ERROR: " + bellmanErr
					+ " \tBELIEF POINTS: " + beliefRegion.length
					+ " \tA VECTORS: " + ipomdp.alphaVectors.length);
			
			if (stepId % 100 < 5)
				continue;
			
			if (bellmanErr < 0.001) {
				logger.warn("BELLMAN ERROR LESS THAN 0.001. PROBABLY CONVERGED.");
				break;
			}
		}

	}

	public void computeMaxMinImprovement(DD[][] beliefRegion) {
		
		double imp;
		ipomdp.bestImprovement = Double.NEGATIVE_INFINITY;
		ipomdp.worstDecline = Double.POSITIVE_INFINITY;
		
		for (int j = 0; j < beliefRegion.length; j++) {
			/*
			 * find biggest improvement at this belief point
			 */
			imp = OP.max(
					ipomdp.newPointBasedValues[j], 
					ipomdp.numNewAlphaVectors) 
					- OP.max(ipomdp.currentPointBasedValues[j]);
			
			if (imp > ipomdp.bestImprovement)
				ipomdp.bestImprovement = imp;
			if (imp < ipomdp.worstDecline)
				ipomdp.worstDecline = imp;
		}
	}
	
	public AlphaVector dpBackup(
			DD[] belState, 
			DD[] primedV, 
			double maxAbsVal) throws ZeroProbabilityObsException, VariableNotFoundException {
		
		NextBelState[] nextBelStates;
		
		/*
		 * get next unnormalised belief states
		 */
		
//		System.out.println("In dpBackup with primedV " + Arrays.toString(primedV));
		double smallestProb;
		
		smallestProb = ipomdp.tolerance / maxAbsVal;
//		System.out.println("BelState is " + Arrays.deepToString(belState));
//		System.out.println("\r\n=================================================");
		nextBelStates = oneStepNZPrimeBelStates(belState, true, smallestProb);
//		System.out.println("nextBelState are " + nextBelStates.length);
		
		/*
		 * precompute obsVals
		 */
		for (int actId = 0; actId < ipomdp.Ai.size(); actId++)
			nextBelStates[actId].getObsVals(primedV);

		double bestValue = Double.NEGATIVE_INFINITY;
		double actValue;
		int bestActId = 0;
		
		int nObservations = ipomdp.obsCombinations.size();
		
		int[] bestObsStrat = new int[ipomdp.nObservations];

		/*
		 * TODO: replace this with Ai name iteration
		 */
		for (int actId = 0; actId < ipomdp.Ai.size(); actId++) {
			
			actValue = 0.0;
			
			/*
			 * compute immediate rewards
			 */
			actValue = 
					actValue + OP.factoredExpectationSparseNoMem(
							belState, 
							ipomdp.currentRi.get(ipomdp.Ai.get(actId)));

			/*
			 * compute observation strategy
			 */
			nextBelStates[actId].getObsStrat();
			actValue = actValue + ipomdp.discFact
					* nextBelStates[actId].getSumObsValues();

			if (actValue > bestValue) {
				
				bestValue = actValue;
				bestActId = actId;
				bestObsStrat = nextBelStates[actId].obsStrat;
			}
		}
		
		/*
		 * construct corresponding alpha vector
		 */
		DD newAlpha = DD.zero;
		DD nextValFn = DD.zero;
		DD obsDd;
		int[] obsConfig = new int[this.ipomdp.obsIVarIndices.length];
		
		for (int alphaId = 0; alphaId < this.ipomdp.alphaVectors.length; alphaId++) {
		
			if (MySet.find(bestObsStrat, alphaId) >= 0) {

				obsDd = DD.zero;

				for (int obsId = 0; obsId < nObservations; obsId++) {
					
					if (bestObsStrat[obsId] == alphaId) {
						
						obsConfig = 
								this.ipomdp.statedecode(
										obsId + 1, 
										this.ipomdp.obsIVarIndices.length, 
										ArrayUtils.subarray(
												this.ipomdp.obsVarsArity, 
												0, 
												this.ipomdp.obsIVarIndices.length));
						
						obsDd = 
								OP.add(
										obsDd, 
										Config.convert2dd(
												IPOMDP.stackArray(
														ipomdp.obsIVarPrimeIndices, 
														obsConfig)));
					}
				}
				
				nextValFn = 
						OP.add(
								nextValFn, 
								OP.multN(
										IPOMDP.concatenateArray(
												DDleaf.myNew(this.ipomdp.discFact), 
												obsDd, 
												primedV[alphaId])));
			}
		}
		
		/* include Mj's transition for computing value function */
		DD mjTransition = 
				OP.addMultVarElim(
						ArrayUtils.add(ipomdp.currentOj, ipomdp.currentMjTfn), 
						ipomdp.obsJVarPrimeIndices);
		
		DD[] valFnArray = 
				ArrayUtils.addAll(
						ArrayUtils.addAll(
								this.ipomdp.currentTi.get(this.ipomdp.Ai.get(bestActId)),
								this.ipomdp.currentOi.get(this.ipomdp.Ai.get(bestActId))), 
						new DD[] {mjTransition, nextValFn}); 

		newAlpha = 
				OP.addMultVarElim(
						valFnArray, 
						ArrayUtils.addAll(
								this.ipomdp.stateVarPrimeIndices,
								this.ipomdp.obsIVarPrimeIndices));
		
		newAlpha = 
				OP.addN(
						IPOMDP.concatenateArray(
								newAlpha, 
								this.ipomdp.currentRi.get(this.ipomdp.Ai.get(bestActId))));
		
		bestValue = OP.factoredExpectationSparse(belState, newAlpha);
		
		/*
		 * package up to return
		 */
		AlphaVector returnAlpha = new AlphaVector(newAlpha, bestValue,
				bestActId, bestObsStrat);
		
		return returnAlpha;
	}

	public NextBelState[] oneStepNZPrimeBelStates(
			DD[] belState,
			boolean normalize, 
			double smallestProb) throws ZeroProbabilityObsException, VariableNotFoundException {
		
		int[][] obsConfig = 
				new int[this.ipomdp.obsCombinations.size()][this.ipomdp.obsIVarIndices.length];
		
		double[] obsProbs;
		DD[] marginals = new DD[this.ipomdp.stateVarIndices.length + 1];
		DD dd_obsProbs;
		
		for (int obsId = 0; obsId < this.ipomdp.obsCombinations.size(); obsId++)
			obsConfig[obsId] = 
				this.ipomdp.statedecode(
						obsId + 1,
						this.ipomdp.obsIVarIndices.length, 
						ArrayUtils.subarray(
								this.ipomdp.obsVarsArity, 
								0, 
								this.ipomdp.obsIVarIndices.length));
		
		Global.newHashtables();
		
		NextBelState[] nextBelStates = new NextBelState[this.ipomdp.Ai.size()];
		
		/*
		 * TODO: iterate over actNames instead of IDs
		 */
		for (int actId = 0; actId < this.ipomdp.Ai.size(); actId++) {
		
			dd_obsProbs = 
					InteractiveBelief.getL1BeliefUpdateNorm(
							this.ipomdp, 
							OP.multN(belState), this.ipomdp.Ai.get(actId));

			obsProbs = OP.convert2array(dd_obsProbs, this.ipomdp.obsIVarPrimeIndices);

			nextBelStates[actId] = new NextBelState(this.ipomdp, obsProbs, smallestProb);
			
			/*
			 * Compute marginals
			 */
			try {
				if (!nextBelStates[actId].isempty()) {
					marginals = 
							OP.marginals(
									InteractiveBelief.getCpts(
											this.ipomdp, 
											OP.multN(belState), 
											this.ipomdp.Ai.get(actId)), 
									this.ipomdp.stateVarPrimeIndices,
									this.ipomdp.stateVarIndices);
							
					nextBelStates[actId].restrictN(marginals, obsConfig);

				}
			}
			
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return nextBelStates;
	}
}
