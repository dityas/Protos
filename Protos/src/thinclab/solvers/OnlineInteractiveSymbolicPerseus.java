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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.belief.InteractiveBelief;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.frameworks.IPOMDP;
import thinclab.legacy.AlphaVector;
import thinclab.legacy.Config;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;
import thinclab.legacy.MySet;
import thinclab.legacy.NextBelState;
import thinclab.legacy.OP;
import thinclab.legacy.RandomPermutation;

/*
 * @author adityas
 *
 */
public class OnlineInteractiveSymbolicPerseus extends OnlineSolver {
	/*
	 * Value iteration solver for IPOMDPs with finite look ahead horizon
	 * 
	 * 
	 */
	
	/* max rounds and dpBackups for symbolic perseus */
	private int maxRounds;
	private int dpBackups;
	
	/* Store reference to the IPOMDP for use later */
	private IPOMDP ipomdp;
	
	/*
	 * Keep alphaVectors inside the solver instead of in the IPOMDP objects
	 */
	private DD[] alphaVectors;
	
	private static final Logger logger = 
			Logger.getLogger(OnlineInteractiveSymbolicPerseus.class);
	
	// --------------------------------------------------------------------------------------

	public OnlineInteractiveSymbolicPerseus(
			IPOMDP ipomdp, 
			BeliefRegionExpansionStrategy b,
			int maxRounds,
			int dpBackups) {
		/*
		 * Initialize with an IPOMDP
		 */
		super(ipomdp, b);
		
		this.maxRounds = maxRounds;
		this.dpBackups = dpBackups;
		
		this.ipomdp = (IPOMDP) this.f;
	}
	
	// ---------------------------------------------------------------------------------------
	
	@Override
	public void nextStep(String action, List<String> obs) {
		
		try {
			/*
			 * Transition the IPOMDP to a new belief by taking the specified action
			 * and observing obs.
			 */
			this.ipomdp.step(
					this.ipomdp.getInitialBeliefs().get(0), 
					action, 
					obs.toArray(new String[obs.size()]));
			
			/* Reset the search to new initial beliefs */
			this.expansionStrategy.resetToNewInitialBelief();
		} 
		
		catch (ZeroProbabilityObsException | VariableNotFoundException e) {
			logger.error("While taking action " 
					+ action + " and observing " + obs 
					+ " got error: " + e.getMessage());
			System.exit(-1);
		}
	}
	
	@Override
	public String getBestActionAtCurrentBelief() {
		
		int alphaId = 
				this.ipomdp.policyBestAlphaMatch(
						this.ipomdp.getInitialBeliefs().get(0), 
						this.alphaVectors, 
						this.ipomdp.policy);
		
		return this.ipomdp.Ai.get(this.ipomdp.policy[alphaId]);
	}
	
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
				
				boundedPerseusStartFromCurrent(
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
						beliefRegion, this.alphaVectors);
		
//		logger.debug("printing belief region");
//		for (int i = 0; i < beliefRegion.length; i++) {
//			logger.debug("Belief:" + i+ " " 
//					+ InteractiveBelief.toStateMap(this.ipomdp, OP.reorder(OP.multN(beliefRegion[i]))));
//		}
		
//		logger.debug("Current PBVs are " + Arrays.deepToString(this.ipomdp.currentPointBasedValues));
		DD[] primedV;
		double maxAbsVal = 0;
		
		for (int stepId = firstStep; stepId < firstStep + nSteps; stepId++) {
//			logger.debug("STEP:=====================================================================");
//			logger.debug("A vecs are: " + Arrays.toString(this.alphaVectors));
//			logger.debug("Ri : " + this.ipomdp.currentRi);
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
			int numIter = 0;
			int numNew = 0;
//			int b = 0;
			while (ipomdp.numNewAlphaVectors < maxAlphaSetSize
					&& !permutedIds.isempty()) {
				
//			while (ipomdp.numNewAlphaVectors < maxAlphaSetSize
//					&& b < beliefRegion.length) {
				
				if (nDpBackups >= 2 * this.alphaVectors.length) {
					computeMaxMinImprovement(beliefRegion);
					if (ipomdp.bestImprovement > ipomdp.tolerance
							&& ipomdp.bestImprovement > -2 * ipomdp.worstDecline) {
//						logger.warn("Breaking because bestImprovement " + ipomdp.bestImprovement + ""
//								+ "> tolerance " + ipomdp.tolerance + " && bestImprovement " +
//								ipomdp.bestImprovement + " > -2 * worstDecline " + 
//								(-2 * ipomdp.worstDecline));
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
					
					if (permutedIds.isempty()) {
//						logger.warn("Breaking because no belief points...");
						break;
					}
					
					choice = OP.sampleMultinomial(diff);
				}

				int i = permutedIds.getSetDone(choice);
				
				/* count belief point used */
				numIter += 1;
				
				if (ipomdp.numNewAlphaVectors < 1
						|| (OP.max(ipomdp.newPointBasedValues[i], ipomdp.numNewAlphaVectors)
								- OP.max(ipomdp.currentPointBasedValues[i]) < steptolerance)) {
					
					/*
					 * dpBackup
					 */
					
					newVector = dpBackup(beliefRegion[i], primedV, maxAbsVal);
					numIter += 1;
					newVector.alphaVector = OP.approximate(
							newVector.alphaVector, bellmanErr * (1 - ipomdp.discFact)
									/ 2.0, onezero);
					newVector.setWitness(i);
//					b += 1;
					nDpBackups = nDpBackups + 1;
					
					/*
					 * merge and trim
					 */
					newValues = 
							OP.factoredExpectationSparseNoMem(
									beliefRegion, 
									newVector.alphaVector);
//					logger.debug("New Values computed");
//					logger.debug("New PBVs were " + Arrays.deepToString(ipomdp.newPointBasedValues));
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
//						logger.debug("Improvement after backup is " + improvement 
//								+ " with previous max " + OP.getMax(this.ipomdp.currentPointBasedValues, 1)[0]);
//						logger.debug("Adding the new Alpha Vector with vars " 
//								+ Arrays.toString(newVector.alphaVector.getVarSet()));
						numNew += 1;
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
			this.alphaVectors = new DD[ipomdp.numNewAlphaVectors];
			ipomdp.currentPointBasedValues = 
					new double[ipomdp.newPointBasedValues.length][ipomdp.numNewAlphaVectors];

			ipomdp.policy = new int[ipomdp.numNewAlphaVectors];
			ipomdp.policyvalue = new double[ipomdp.numNewAlphaVectors];
			
			for (int j = 0; j < ipomdp.Ai.size(); j++)
				ipomdp.uniquePolicy[j] = false;

			for (int j = 0; j < ipomdp.numNewAlphaVectors; j++) {
				
				this.alphaVectors[j] = ipomdp.newAlphaVectors[j].alphaVector;
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
					+ " \tB ERROR: " + bellmanErr
					+ " \tUSED|TOTAL BELIEFS: " + numIter + "|" + beliefRegion.length
					+ " \tA VECTORS: " + this.alphaVectors.length);
			
			if (stepId % 100 < 5)
				continue;
			
			if (bellmanErr < 0.001) {
				logger.warn("BELLMAN ERROR LESS THAN 0.001. COVERGENCE! SOFTWARE VERSION 7.0... "
						+ "LOOKING AT LIFE THROUGH THE EYES OF A TIRED HEART.");
				this.logAlphaVectors();
				break;
			}
			
//			logger.debug("END STEP:==================================================================");
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
		double smallestProb;
//		logger.debug("=================================================");
		smallestProb = ipomdp.tolerance / maxAbsVal;
		nextBelStates = oneStepNZPrimeBelStates(belState, true, smallestProb);
//		logger.debug("nextBelState are " + nextBelStates.length);
		/*
		 * precompute obsVals
		 */
		for (int actId = 0; actId < ipomdp.getActions().size(); actId++)
			nextBelStates[actId].getObsVals(primedV);

		double bestValue = Double.NEGATIVE_INFINITY;
		double actValue;
		int bestActId = 0;
		
		int nObservations = ipomdp.getAllPossibleObservations().size();
		
		int[] bestObsStrat = new int[nObservations];

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
//			logger.debug("Reward function is " 
//							+ ipomdp.currentRi.get(ipomdp.Ai.get(actId)));
			/*
			 * compute observation strategy
			 */
			nextBelStates[actId].getObsStrat();
			actValue = actValue + ipomdp.discFact
					* nextBelStates[actId].getSumObsValues();
			
//			logger.debug(" actId " + actId +" " + this.ipomdp.getActions().get(actId) + " actValue " + 
//					actValue + " sumobsvalues " + nextBelStates[actId].getSumObsValues());
			
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
		
		for (int alphaId = 0; alphaId < this.alphaVectors.length; alphaId++) {
		
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
//						logger.debug(Arrays.toString(obsConfig));
//						logger.debug(Arrays.deepToString(IPOMDP.stackArray(
//								ipomdp.obsIVarPrimeIndices, 
//								obsConfig)));
						obsDd = 
								OP.add(
										obsDd, 
										Config.convert2dd(
												IPOMDP.stackArray(
														ipomdp.obsIVarPrimeIndices, 
														obsConfig)));
//						logger.debug(Config.convert2dd(
//								IPOMDP.stackArray(
//										ipomdp.obsIVarPrimeIndices, 
//										obsConfig)));
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
		DD mjTransition = ipomdp.currentTau;
		
		/* Add P(AJ | MJ) to Ti */
		DD[] Ti = 
				ArrayUtils.add(
						this.ipomdp.currentTi.get(this.ipomdp.Ai.get(bestActId)), 
						this.ipomdp.currentAjGivenMj);
		
//		DD[] Ti = this.ipomdp.currentTi.get(this.ipomdp.Ai.get(bestActId));
		
		DD[] valFnArray = 
				ArrayUtils.addAll(
						ArrayUtils.addAll(
								this.ipomdp.currentOi.get(this.ipomdp.Ai.get(bestActId)),
								Ti), 
						new DD[] {mjTransition, nextValFn});
		
		DD V = 
				OP.addMultVarElim(
						valFnArray, 
						this.ipomdp.AjIndex);
//		logger.debug("V has vars " + Arrays.toString(V.getVarSet()));
		newAlpha = 
				OP.addMultVarElim(
						V, 
						ArrayUtils.addAll(
								ArrayUtils.subarray(
										this.ipomdp.stateVarPrimeIndices,
										0, this.ipomdp.stateVarPrimeIndices.length - 1),
								this.ipomdp.obsIVarPrimeIndices));
//		logger.debug("New alpha has vars " + Arrays.toString(newAlpha.getVarSet()));
		newAlpha = 
				OP.addN(
						IPOMDP.concatenateArray(
								newAlpha, 
								this.ipomdp.currentRi.get(this.ipomdp.Ai.get(bestActId))));
		
		bestValue = OP.factoredExpectationSparse(belState, newAlpha);
//		logger.debug("Best Value is " + bestValue);
//		logger.debug("New Alpha has vars " + Arrays.toString(newAlpha.getVarSet()) 
//			+ " with value " + bestValue);
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
		
			/* Assuming factored belief was normalized */
			dd_obsProbs = 
					InteractiveBelief.getL1BeliefUpdateNorm(
							this.ipomdp, 
							OP.reorder(OP.multN(belState)), this.ipomdp.Ai.get(actId));

			obsProbs = OP.convert2array(dd_obsProbs, this.ipomdp.obsIVarPrimeIndices);
			nextBelStates[actId] = new NextBelState(this.ipomdp, obsProbs, smallestProb);
//			logger.debug("Obs Probs are " + Arrays.toString(obsProbs));
//			logger.debug("Obs Config is " + Arrays.deepToString(obsConfig));
			
			/*
			 * Compute marginals
			 */
			try {
				if (!nextBelStates[actId].isempty()) {
					marginals = 
							OP.marginals(
									InteractiveBelief.getCpts(
											this.ipomdp, 
											belState, 
											this.ipomdp.Ai.get(actId)), 
									ArrayUtils.subarray(
											this.ipomdp.stateVarPrimeIndices, 
											0, 
											this.ipomdp.stateVarIndices.length - 1),
									ArrayUtils.subarray(
											this.ipomdp.stateVarIndices, 
											0, 
											this.ipomdp.stateVarIndices.length - 1));
					
//					logger.debug("Marginals are " + Arrays.toString(marginals));
					nextBelStates[actId].restrictN(marginals, obsConfig);
//					logger.debug("After computing marginals " + nextBelStates[actId]);
				}
			}
			
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return nextBelStates;
	}
	
	// ---------------------------------------------------------------------------------------------
	
	public IPOMDP getIPOMDP() {
		/*
		 * Getter to access the IPOMDP
		 */
		return this.ipomdp;
	}
	
	public DD[] getAlphaVectors() {
		/*
		 * Returns the alpha vectors
		 */
		return this.alphaVectors;
	}
	
	public void logAlphaVectors() {
		
		for (int i = 0; i < this.ipomdp.policy.length; i++) {
			logger.debug("Alpha Vector " + i + " is " + this.alphaVectors[i] +
					" for action " + this.ipomdp.getActions().get(this.ipomdp.policy[i]));
		}
	}
}
