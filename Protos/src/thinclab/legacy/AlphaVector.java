package thinclab.legacy;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.lang3.ArrayUtils;

import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.utils.Diagnostics;

public class AlphaVector implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1357799705845487274L;

	public DD alphaVector;
	public double value;
	public int actId;
	public int witness;
	public int[] obsStrat;

	public AlphaVector(DD a, double v, int act, int[] os) {
		alphaVector = a;
		value = v;
		actId = act;
		obsStrat = os;
	}

	public AlphaVector(AlphaVector a) {
		alphaVector = a.alphaVector;
		value = a.value;
		actId = a.actId;
		witness = a.witness;
		obsStrat = a.obsStrat;
	}

	public void setWitness(int i) {
		witness = i;
	}
	
	// ---------------------------------------------------------------------------------------
	
	public static AlphaVector dpBackup(
			IPOMDP ipomdp,
			DD belState,
			DD[] factoredBelState,
			DD[] primedV, 
			double maxAbsVal,
			int numAlpha) throws ZeroProbabilityObsException, VariableNotFoundException {
		
		HashMap<String, NextBelState> nextBelStates;
		
		/* get next unnormalised belief states */
		double smallestProb;
		
		/* measure time constructing for NZ primes */
		long beforeNZPrimes = System.nanoTime();
		
		smallestProb = ipomdp.tolerance / maxAbsVal;
		nextBelStates = 
				NextBelState.oneStepNZPrimeBelStatesCached(
						ipomdp, 
						belState, 
						true, 
						smallestProb);

		/* precompute obsVals */
		nextBelStates.values().forEach(n -> n.getObsVals(primedV));
		
		long afterNZPrimes = System.nanoTime();
		Diagnostics.NZ_PRIME_TIME.add((afterNZPrimes - beforeNZPrimes));

		double bestValue = Double.NEGATIVE_INFINITY;
		double actValue;
		String bestAction = null;
		
		int nObservations = ipomdp.obsCombinations.size();
		
		int[] bestObsStrat = new int[nObservations];
		
		/* measure time for Immediate R */
		long beforeR = System.nanoTime();

		for (String Ai : ipomdp.getActions()) {
			
			actValue = 0.0;
			
			/* compute immediate rewards */
			actValue = 
					actValue + OP.factoredExpectationSparseNoMem(
							factoredBelState, 
							ipomdp.currentRi.get(Ai));

			/* compute observation strategy */
			nextBelStates.get(Ai).getObsStrat();
			actValue = actValue + ipomdp.discFact
					* nextBelStates.get(Ai).getSumObsValues();
			
			if (actValue > bestValue) {
				
				bestValue = actValue;
				bestAction = Ai;
				bestObsStrat = nextBelStates.get(Ai).obsStrat;
			}
		}
		
		long afterR = System.nanoTime();
		Diagnostics.IMMEDIATE_R_TIME.add((afterR - beforeR));
		
		/* measure time for constructing A Vec */
		long beforeAVec = System.nanoTime();
		
		/* construct corresponding alpha vector */
		DD newAlpha = DD.zero;
		DD nextValFn = DD.zero;
		DD obsDd;
		int[] obsConfig = new int[ipomdp.obsIVarIndices.length];
		
		for (int alphaId = 0; alphaId < numAlpha; alphaId++) {
		
			if (MySet.find(bestObsStrat, alphaId) >= 0) {

				obsDd = DD.zero;

				for (int obsId = 0; obsId < nObservations; obsId++) {
					
					if (bestObsStrat[obsId] == alphaId) {
						
						obsConfig = 
								IPOMDP.statedecode(
										obsId + 1, 
										ipomdp.obsIVarIndices.length, 
										ArrayUtils.subarray(
												ipomdp.obsVarsArity, 
												0, 
												ipomdp.obsIVarIndices.length));

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
												DDleaf.myNew(ipomdp.discFact), 
												obsDd, 
												primedV[alphaId])));
			}
		}
		
		/* include Mj's transition for computing value function */
		DD mjTransition = ipomdp.currentTau;
		
		/* Add P(AJ | MJ) to Ti */
		DD[] Ti = 
				ArrayUtils.add(
						ipomdp.currentTi.get(bestAction), 
						ipomdp.currentAjGivenMj);
		
		Ti = ArrayUtils.add(Ti, ipomdp.currentThetajGivenMj);
		
		DD[] valFnArray = 
				ArrayUtils.addAll(
						ArrayUtils.addAll(
								ipomdp.currentOi.get(bestAction),
								Ti), 
						new DD[] {mjTransition, nextValFn});
		
		DD V = 
				OP.addMultVarElim(
						valFnArray, 
						new int[] {ipomdp.thetaVarPosition + 1, ipomdp.AjVarStartPosition + 1});

		newAlpha = 
				OP.addMultVarElim(
						V, 
						ArrayUtils.addAll(
								ArrayUtils.subarray(
										ipomdp.stateVarPrimeIndices,
										0, ipomdp.thetaVarPosition),
								ipomdp.obsIVarPrimeIndices));
		
//		newAlpha = 
//				OP.addMultVarElim(newAlpha, new int[] {ipomdp.thetaVarPosition + 1, ipomdp.AjVarStartPosition + 1});

		newAlpha = 
				OP.addN(
						IPOMDP.concatenateArray(
								newAlpha, 
								ipomdp.currentRi.get(bestAction)));
		
		long afterAVec = System.nanoTime();
		Diagnostics.AVEC_TIME.add((afterAVec - beforeAVec));
		
		bestValue = OP.factoredExpectationSparse(factoredBelState, newAlpha);

		/* package up to return */
		AlphaVector returnAlpha = 
				new AlphaVector(
						newAlpha, 
						bestValue, 
						ipomdp.getActions().indexOf(bestAction), 
						bestObsStrat);
		
		return returnAlpha;
	}
	
	public static AlphaVector dpBackup2(
			IPOMDP ipomdp,
			DD belState,
			DD[] factoredBelState,
			DD[] primedV, 
			double maxAbsVal,
			int numAlpha) throws ZeroProbabilityObsException, VariableNotFoundException {
		
		/*
		 * A more efficient version of the backup
		 * 
		 * The efficiency comes from performing operations on smaller factors
		 */
		
		HashMap<String, NextBelState> nextBelStates;
		
		/* get next unnormalised belief states */
		double smallestProb;
		
		/* measure time constructing for NZ primes */
		long beforeNZPrimes = System.nanoTime();
		
		smallestProb = ipomdp.tolerance / maxAbsVal;
		nextBelStates = 
				NextBelState.oneStepNZPrimeBelStatesCached(
						ipomdp, 
						belState, 
						true, 
						smallestProb);

		/* precompute obsVals */
		nextBelStates.values().forEach(n -> n.getObsVals(primedV));
		
		long afterNZPrimes = System.nanoTime();
		Diagnostics.NZ_PRIME_TIME.add((afterNZPrimes - beforeNZPrimes));

		double bestValue = Double.NEGATIVE_INFINITY;
		double actValue;
		String bestAction = null;
		
		int nObservations = ipomdp.obsCombinations.size();
		
		int[] bestObsStrat = new int[nObservations];
		
		/* measure time for Immediate R */
		long beforeR = System.nanoTime();

		for (String Ai : ipomdp.getActions()) {
			
			actValue = 0.0;
			
			/* compute immediate rewards */
			actValue = 
					actValue + OP.factoredExpectationSparseNoMem(
							factoredBelState, 
							ipomdp.currentRi.get(Ai));

			/* compute observation strategy */
			nextBelStates.get(Ai).getObsStrat();
			actValue = actValue + ipomdp.discFact
					* nextBelStates.get(Ai).getSumObsValues();
			
			if (actValue > bestValue) {
				
				bestValue = actValue;
				bestAction = Ai;
				bestObsStrat = nextBelStates.get(Ai).obsStrat;
			}
		}
		
		long afterR = System.nanoTime();
		Diagnostics.IMMEDIATE_R_TIME.add((afterR - beforeR));
		
		/* measure time for constructing A Vec */
		long beforeAVec = System.nanoTime();
		
		/* construct corresponding alpha vector */
		DD newAlpha = DD.zero;
		DD nextValFn = DD.zero;
		DD obsDd;
		int[] obsConfig = new int[ipomdp.obsIVarIndices.length];
		
		long beforeNextValFn = System.nanoTime();
		
		for (int alphaId = 0; alphaId < numAlpha; alphaId++) {
		
			if (MySet.find(bestObsStrat, alphaId) >= 0) {

				obsDd = DD.zero;

				for (int obsId = 0; obsId < nObservations; obsId++) {
					
					if (bestObsStrat[obsId] == alphaId) {
						
						obsConfig = 
								IPOMDP.statedecode(
										obsId + 1, 
										ipomdp.obsIVarIndices.length, 
										ArrayUtils.subarray(
												ipomdp.obsVarsArity, 
												0, 
												ipomdp.obsIVarIndices.length));

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
												DDleaf.myNew(ipomdp.discFact), 
												obsDd, 
												primedV[alphaId])));
			}
		}
		
		long afterNextValFn = System.nanoTime();
		Diagnostics.NEXT_VAL_FN_TIME.add((afterNextValFn - beforeNextValFn));
		
		int[] sumoutIndices = new int[] {ipomdp.thetaVarPosition + 1, ipomdp.AjVarStartPosition + 1};
		
		int[] allVarIndices = 
				ArrayUtils.addAll(
						ArrayUtils.subarray(
								ipomdp.stateVarPrimeIndices, 
								0, 
								ipomdp.thetaVarPosition), 
						ipomdp.obsIVarPrimeIndices);
		
		sumoutIndices = ArrayUtils.addAll(sumoutIndices, allVarIndices);
		
//		DD valFnArray = 
//				OP.multN(
//						ArrayUtils.addAll(
//								ipomdp.currentOi.get(bestAction), 
//								ipomdp.currentTi.get(bestAction)));
//		
//		valFnArray = 
//				OP.mult(ipomdp.currentTauXPAjGivenMjXPThetajGivenMj, 
//						valFnArray);
//		
//		valFnArray = OP.mult(valFnArray, nextValFn);
//		System.out.println("valFnArray has " + valFnArray.getNumLeaves() + " DD nodes");
		
		DD[] valFnArray = 
				ArrayUtils.addAll(
						ipomdp.currentTi.get(bestAction), 
						ipomdp.currentOi.get(bestAction));
		
		valFnArray = 
				ArrayUtils.addAll(
						valFnArray, 
						new DD[] {
								ipomdp.currentTauXPAjGivenMjXPThetajGivenMj, 
								nextValFn});
		
		newAlpha = 
				OP.addMultVarElim(
						valFnArray, 
						sumoutIndices);

		newAlpha = 
				OP.addN(
						IPOMDP.concatenateArray(
								newAlpha, 
								ipomdp.currentRi.get(bestAction)));
		
		long afterAVec = System.nanoTime();
		Diagnostics.AVEC_TIME.add((afterAVec - beforeAVec));
		
		bestValue = OP.factoredExpectationSparse(factoredBelState, newAlpha);

		/* package up to return */
		AlphaVector returnAlpha = 
				new AlphaVector(
						newAlpha, 
						bestValue, 
						ipomdp.getActions().indexOf(bestAction), 
						bestObsStrat);
		
		return returnAlpha;
	}
	
	
	public static AlphaVector dpBackup(
			POMDP pomdp, DD[] belState, DD[] primedV, double maxAbsVal, int numAlphas) {
		/*
		 * Backup operation for POMDPs
		 */
		
		NextBelState[] nextBelStates;
		
		double smallestProb;
		smallestProb = pomdp.tolerance / maxAbsVal;
		
		nextBelStates = 
				NextBelState.oneStepNZPrimeBelStates(pomdp, belState, true, smallestProb);

		/* precompute obsVals */
		for (int actId = 0; actId < pomdp.nActions; actId++) {
			nextBelStates[actId].getObsVals(primedV);
		}
		
		double bestValue = Double.NEGATIVE_INFINITY;
		double actValue;
		
		int bestActId = 0;
		int[] bestObsStrat = new int[pomdp.nObservations];

		for (int actId = 0; actId < pomdp.nActions; actId++) {
			actValue = 0.0;
			
			/* compute immediate rewards */
			actValue = 
					actValue 
						+ OP.factoredExpectationSparse(
								belState, 
								pomdp.actions[actId].rewFn);

			/* compute observation strategy */
			nextBelStates[actId].getObsStrat();
			actValue = 
					actValue + pomdp.discFact * nextBelStates[actId].getSumObsValues();

			if (actValue > bestValue) {
				bestValue = actValue;
				bestActId = actId;
				bestObsStrat = nextBelStates[actId].obsStrat;
			}
		}

		/* construct the alpha vector */
		DD newAlpha = DD.zero;
		DD nextValFn = DD.zero;
		DD obsDd;
		
		int[] obsConfig = new int[pomdp.nObsVars];
		for (int alphaId = 0; alphaId < numAlphas; alphaId++) {
			
			if (MySet.find(bestObsStrat, alphaId) >= 0) {
				
				obsDd = DD.zero;
				
				for (int obsId = 0; obsId < pomdp.nObservations; obsId++) {
					
					if (bestObsStrat[obsId] == alphaId) {
						obsConfig = 
								POMDP.statedecode(
										obsId + 1, 
										pomdp.nObsVars, 
										pomdp.obsVarsArity);

						obsDd = 
								OP.add(
										obsDd, 
										Config.convert2dd(
												POMDP.stackArray(
														pomdp.primeObsIndices, 
														obsConfig)));

					}
				}
				
				nextValFn = 
						OP.add(nextValFn, 
								OP.multN(ArrayUtils.addAll(
										new DD[] {DDleaf.myNew(pomdp.discFact)}, 
										new DD[] {obsDd, primedV[alphaId]})));
			}
		}
		
		newAlpha = 
				OP.addMultVarElim(
						ArrayUtils.addAll(
								pomdp.actions[bestActId].transFn, 
								ArrayUtils.addAll(pomdp.actions[bestActId].obsFn, nextValFn)), 
						ArrayUtils.addAll(
								pomdp.primeVarIndices, 
								pomdp.primeObsIndices));

		newAlpha = 
				OP.addN(
						ArrayUtils.addAll(
								new DD[] {newAlpha}, 
								pomdp.actions[bestActId].rewFn));

		bestValue = OP.factoredExpectationSparse(belState, newAlpha);

		/* package up to return */
		AlphaVector returnAlpha = 
				new AlphaVector(
						newAlpha, 
						bestValue, 
						bestActId, 
						bestObsStrat);
		
		return returnAlpha;
	}

}