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
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;

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
import thinclab.utils.LoggerFactory;

/*
 * @author adityas
 *
 */
public class FiniteHorizonLookAheadValueIterationSolver {
	/*
	 * Value iteration solver for IPOMDPs with finite look ahead horizon
	 * 
	 * 
	 */
	
	private Logger logger = LoggerFactory.getNewLogger("FHLAVISolver: ");
	
	public IPOMDP ipomdp;
	
	// --------------------------------------------------------------------------------------
	
//	public static void finiteLookAheadVI(IPOMDP ipomdp) {
//		/*
//		 * Perform full value iteration for the given look ahead starting from
//		 * current root belief
//		 */
//		
//		Logger logger = LoggerFactory.getNewLogger("FHLAVISolver: ");
//		
//		logger.info("Starting VI for " + ipomdp.mjLookAhead 
//				+ " horizons starting from " + ipomdp.lookAheadRootInitBeliefs);
//		
//		LookAheadTree laTree = ipomdp.getLookAheadTree();
//		
//		DD startBelief = ipomdp.lookAheadRootInitBeliefs.get(0);
//		
//		System.out.println(
//				FiniteHorizonLookAheadValueIterationSolver.computeUtility(ipomdp, startBelief));
//	}
	
//	public static HashMap<String, DD> computeUtility(
//			IPOMDP ipomdp, DD currentBelief) {
//		/*
//		 * Computes the utility of being in the current belief state recursively and maps 
//		 * it to the action taken
//		 */
//		
//		HashMap<String, DD> U = new HashMap<String, DD>();
//		
//		LookAheadTree laTree = ipomdp.getLookAheadTree();
//		
//		for (String Ai : ipomdp.Ai) {
//			
//			/* Fetch immediate reward for this action */
//			DD ER = ipomdp.currentRi.get(Ai);
//			
//			/* Sumout[Sp] P (Oip | Sp) */
//			DD POipSp = OP.addMultVarElim(ipomdp.currentOi.get(Ai), ipomdp.stateVarPrimeIndices);
//			
//			/* Sumout[S] P (Sp | S) x b(S) */
//			DD PSpSB = OP.addMultVarElim(
//					ArrayUtils.add(
//							ipomdp.currentTi.get(Ai), currentBelief), 
//					ipomdp.stateVarIndices);
//			
//			/* 
//			 * For each valid Oi = oi, compute
//			 * 
//			 * Sumout[Oi] ( Sumout[Sp] P (Oip | Sp) x Sumout[S] P (Sp | S) x b(S) ) 
//			 */
//			Set<List<String>> validObs = laTree.iBeliefTree.get(currentBelief).get(Ai).keySet();
//			
//			DD POiB = OP.mult(POipSp, PSpSB);
//			
//			
//			
//			/* Sumout[S] B x ER */
//			DD BEr = OP.addMultVarElim(new DD[] {currentBelief, ER}, ipomdp.stateVarIndices);
//			
//			/* U = max{ BEr + Gamma*POib } */
//			DD uDD = OP.add(BEr, OP.mult(ipomdp.ddDiscFact, POiB));
//			
//			/*  */
//			U.put(Ai, uDD);
//		}
//		
//		return U;
//	}
	
	// --------------------------------------------------------------------------------------
	/*
	 * Trying to adapt symbolic perseus's dpBackUp here
	 */
	public FiniteHorizonLookAheadValueIterationSolver(IPOMDP ipomdp) {
		/*
		 * Initialize with an IPOMDP
		 */
		this.ipomdp = ipomdp;
	}
	
	public void solvePBVI(int rounds, int numDpBackups) {
		
//		expandBeliefRegion(100);
		List<DD> iBeliefRegion = new ArrayList<DD>();
//		initBeliefList.addAll(ipomdp.lookAheadRootInitBeliefs);
		
//		for (int i=0; i < this.nAdjuncts; i++) initBeliefList.add(this.adjuncts[i]);
		
//		BeliefSet beliefSet = new BeliefSet(initBeliefList);
		
//		beliefSet.expandBeliefRegionBF(this, 2);
//		this.belRegion = beliefSet.getFactoredBeliefRegionArray(this);
		
		iBeliefRegion.addAll(ipomdp.getCurrentLookAheadBeliefs());
		
		
		/*
		 * Initialize alpha vectors
		 */
		DD[] alphaVectors = new DD[1];
		alphaVectors[0] = DD.one;
		
		this.ipomdp.alphaVectors = alphaVectors;
		
		for (int r=0; r < rounds; r++) {
//			this.pCache.resetOscillationTracking();
//			this.pCache.resetAlphaVecsMap();
//
			try {
				boundedPerseusStartFromCurrent(
						100, 
						r * numDpBackups, 
						numDpBackups, 
						InteractiveBelief.factorInteractiveBeliefRegion(ipomdp, iBeliefRegion));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//
//			beliefSet.expandBeliefRegionSSGA(this, 100);
//			this.belRegion = beliefSet.getFactoredBeliefRegionArray(this);
//
		}
		
//		this.alphaVectors = this.pCache.getMaxAlphaVecs();
//		this.policy = this.pCache.getMaxPolicy();
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
//		System.out.println("DEBUG IS " + ipomdp.debug);
//		System.out.println("currentPointBasedValues " + Arrays.deepToString(ipomdp.currentPointBasedValues));
		
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
			
//			System.out.println("After init");
//			System.out.println(Arrays.deepToString(ipomdp.newPointBasedValues));
			
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

//				System.out.println("\r\n NEW LOOP:");
				
				if (nDpBackups >= 2 * ipomdp.alphaVectors.length) {
					computeMaxMinImprovement(beliefRegion);
					if (ipomdp.bestImprovement > ipomdp.tolerance
							&& ipomdp.bestImprovement > -2 * ipomdp.worstDecline) {
//						System.out.println("Breaking loop");
						break;
					}
				}

				Global.newHashtables();
				count = count + 1;
//				System.out.println("count is " + count);
				if (ipomdp.numNewAlphaVectors == 0)
					choice = 0;
				
				else {
//					System.out.println(Arrays.deepToString(ipomdp.currentPointBasedValues));
//					System.out.println(Arrays.toString(permutedIds.permutation));
//					System.out.println(Arrays.deepToString(ipomdp.newPointBasedValues));
//					System.out.println(Arrays.toString(permutedIds.permutation));
					maxcurrpbv = OP.getMax(ipomdp.currentPointBasedValues,
							permutedIds.permutation);
					maxnewpbv = OP.getMax(ipomdp.newPointBasedValues,
							ipomdp.numNewAlphaVectors, permutedIds.permutation);
					permutedIds.getNewDoneIds(maxcurrpbv, maxnewpbv,
							steptolerance);
					diff = permutedIds.getDiffs(maxcurrpbv, maxnewpbv,
							steptolerance);
					
//					System.out.println("maxcurrpbv=" + Arrays.toString(maxcurrpbv) 
//					+ "maxnewpbv=" + Arrays.toString(maxnewpbv)
//					+ "diff=" + Arrays.toString(diff)
//					+ "permutedIds.empty()=" + permutedIds.isempty());
					
					if (permutedIds.isempty())
						break;
					
//					System.out.println("NOT EMPTY");
					choice = OP.sampleMultinomial(diff);
				}

				int i = permutedIds.getSetDone(choice);

				if (ipomdp.numNewAlphaVectors < 1
						|| (OP.max(ipomdp.newPointBasedValues[i], ipomdp.numNewAlphaVectors)
								- OP.max(ipomdp.currentPointBasedValues[i]) < steptolerance)) {
					
					/*
					 * dpBackup
					 */
//					System.out.println("++++++++++++++++++++++++++++++++++++++++++");
//					System.out.println("numNewAlphaVectors=" + ipomdp.numNewAlphaVectors);
////					System.out.println("newPointBasedValues " + Arrays.deepToString(ipomdp.newPointBasedValues));
//					System.out.println("New dpBackup, " 
//							+ (OP.max(ipomdp.newPointBasedValues[i], ipomdp.numNewAlphaVectors)
//								- OP.max(ipomdp.currentPointBasedValues[i])));
					newVector = dpBackup(beliefRegion[i], primedV, maxAbsVal);

					newVector.alphaVector = OP.approximate(
							newVector.alphaVector, bellmanErr * (1 - ipomdp.discFact)
									/ 2.0, onezero);
					newVector.setWitness(i);

					nDpBackups = nDpBackups + 1;
					
//					System.out.println("New alpha vector is " + newVector.alphaVector);
					
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
					
//					System.out.println("improvement is " + improvement);
//					System.out.println("tolerance is " + ipomdp.tolerance);
					
					if (improvement > ipomdp.tolerance) {
						
						for (int belid = 0; belid < beliefRegion.length; belid++) {
							ipomdp.newPointBasedValues[belid][ipomdp.numNewAlphaVectors] = 
								newValues[belid];
						}
						
						ipomdp.newAlphaVectors[ipomdp.numNewAlphaVectors] = newVector;
						ipomdp.numNewAlphaVectors++;
					}
				}
				
//				System.out.println("numNewAlphaVectors=" + ipomdp.numNewAlphaVectors);
//				System.out.println("newPointBasedValues " + Arrays.deepToString(ipomdp.newPointBasedValues));
//				System.out.println("dpBackUp value, " 
//						+ (OP.max(ipomdp.newPointBasedValues[i], ipomdp.numNewAlphaVectors)
//							- OP.max(ipomdp.currentPointBasedValues[i])));
//				System.out.println((ipomdp.numNewAlphaVectors < maxAlphaSetSize && !permutedIds.isempty()));
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
			
//			System.out.println("Unique policy is " + Arrays.toString(ipomdp.uniquePolicy));
			
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
			
			this.logger.info("STEP: " + stepId 
					+ " \tBELLMAN ERROR: " + bellmanErr
					+ " \tBELIEF POINTS: " + beliefRegion.length
					+ " \tA VECTORS: " + ipomdp.alphaVectors.length);
			
			if (stepId % 100 < 5)
				continue;
				
//			else {
//				this.pCache.cachePolicy(this.alphaVectors.length,
//										this.alphaVectors,
//										this.policy);
//				
//				if (this.pCache.isOscillating(new Float(bellmanErr))) {
//					this.logger.warning(
//							"BELLMAN ERROR " + bellmanErr + " OSCILLATING. PROBABLY CONVERGED.");
//					break;
//				}
//			} // else
			
			if (bellmanErr < 0.001) {
				this.logger.warning("BELLMAN ERROR LESS THAN 0.001. PROBABLY CONVERGED.");
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
//			System.out.println("ActVal is " + actValue);
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
				// System.out.println("alphaId is "+alphaId);
				obsDd = DD.zero;
				// for (int obsId = 0; obsId < bestObsStrat.length; obsId++) {
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
		
//		System.out.println("nextValFn is " + nextValFn);
		
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
		
//		newAlpha = 
//				OP.addMultVarElim(
//						IPOMDP.concatenateArray(
//								this.ipomdp.currentTi.get(this.ipomdp.Ai.get(bestActId)), 
//								this.ipomdp.currentOi.get(this.ipomdp.Ai.get(bestActId)), 
//								nextValFn),
//				IPOMDP.concatenateArray(
//						this.ipomdp.stateVarPrimeIndices, 
//						this.ipomdp.obsIVarPrimeIndices));
		newAlpha = 
				OP.addMultVarElim(
						valFnArray, 
						ArrayUtils.addAll(
								this.ipomdp.stateVarPrimeIndices,
								this.ipomdp.obsIVarPrimeIndices));
		
//		System.out.println("function is " + Arrays.deepToString(IPOMDP.concatenateArray(
//								this.ipomdp.currentTi.get(this.ipomdp.Ai.get(bestActId)), 
//								this.ipomdp.currentOi.get(this.ipomdp.Ai.get(bestActId)), 
//								nextValFn)));
//		System.out.println("Summing out over " + Arrays.toString(IPOMDP.concatenateArray(
//						this.ipomdp.stateVarPrimeIndices, 
//						this.ipomdp.obsIVarPrimeIndices)));
//		System.out.println("newAlpha is " + newAlpha);
		newAlpha = 
				OP.addN(
						IPOMDP.concatenateArray(
								newAlpha, 
								this.ipomdp.currentRi.get(this.ipomdp.Ai.get(bestActId))));
		
//		System.out.println("newAlpha is " + newAlpha);
		bestValue = OP.factoredExpectationSparse(belState, newAlpha);
		// package up to return
		AlphaVector returnAlpha = new AlphaVector(newAlpha, bestValue,
				bestActId, bestObsStrat);
		
		return returnAlpha;
	}

	public NextBelState[] oneStepNZPrimeBelStates(
			DD[] belState,
			boolean normalize, 
			double smallestProb) throws ZeroProbabilityObsException, VariableNotFoundException {
		
//		System.out.println("Starting from " + InteractiveBelief.toStateMap(ipomdp, OP.multN(belState)));
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
		
//		System.out.println("possible observations are " + Arrays.deepToString(obsConfig));
		
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
//			System.out.println("obsProbs are " + Arrays.toString(obsProbs));
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
					
//					System.out.println(
//							"Marginals for actId " + actId + " are " + Arrays.toString(marginals));
					
					nextBelStates[actId].restrictN(marginals, obsConfig);
//					System.out.println("After computing marginals " + nextBelStates[actId]);
				}
			}
			
			catch (Exception e) {
				e.printStackTrace();
			}
			
			/* DEBUG */
//			for (int i = 0; i < nextBelStates[actId].nextBelStates.length; i++)
//				System.out.println(
//						"NextBelStates for actId " 
//						+ actId + " are " 
//						+ InteractiveBelief.toStateMap(
//								ipomdp, OP.multN(nextBelStates[actId].nextBelStates[i])));
			
//			System.out.println("NextBelStates for actId " + actId + " are " + nextBelStates[actId]);
		}
		
		return nextBelStates;
	}
}
