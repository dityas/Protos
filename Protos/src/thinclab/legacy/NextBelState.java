/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.legacy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.belief.IBeliefOps;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.utils.NextBelStateCache;

/*
 * @author adityas
 *
 */
public class NextBelState implements Serializable {
	
	private static final long serialVersionUID = 8843718772195892772L;
	
	public DD[][] nextBelStates;
	public int[] nzObsIds;
	public double[][] obsVals;
	public int numValidObs;
	public int[] obsStrat;
	public double[] obsValues;
	public double sumObsValues;
	
	public String fType;
	public int[] primeObsIndices;
	public int nObs;
	public int obsProbIndex;
	
	private static final Logger LOGGER = Logger.getLogger(NextBelState.class);
	
	public NextBelState(POMDP p, double[] obsProbs, double smallestProb) {
		/*
		 * For POMDPs
		 * 
		 * Same as Hoey's implementation with a POMDP passed explicitly as an argument
		 */
		
		this.fType = p.getType();
		this.primeObsIndices = p.primeObsIndices;
		this.nObs = p.nObservations;
		this.obsProbIndex = p.nStateVars;
		numValidObs = 0;
		
		for (int i = 0; i < obsProbs.length; i++)
			if (obsProbs[i] > smallestProb)
				numValidObs++;
		
		nextBelStates = new DD[numValidObs][p.nStateVars + 1];
		nzObsIds = new int[numValidObs];
		obsStrat = new int[p.nObservations];
		obsValues = new double[numValidObs];
		int j = 0;
		for (int i = 0; i < obsProbs.length; i++)
			if (obsProbs[i] > smallestProb)
				nzObsIds[j++] = i;
	}
	
	public NextBelState(IPOMDP ip, double[] obsProbs, double smallestProb) {
		
		this.fType = ip.getType();
		this.primeObsIndices = ip.obsIVarPrimeIndices;
		this.nObs = ip.obsCombinations.size();
		this.obsProbIndex = ip.thetaVarPosition;
		numValidObs = 0;
		
		for (int i = 0; i < obsProbs.length; i++)
			if (obsProbs[i] > smallestProb)
				numValidObs++;
		
		nextBelStates = new DD[numValidObs][ip.nStateVars - 1];
		nzObsIds = new int[numValidObs];
		obsStrat = new int[ip.obsCombinations.size()];
		obsValues = new double[numValidObs];
		
		int j = 0;
		
		for (int i = 0; i < obsProbs.length; i++)
			if (obsProbs[i] > smallestProb)
				nzObsIds[j++] = i;
	}

	public NextBelState(NextBelState a) {
		nextBelStates = new DD[a.nextBelStates.length][a.nextBelStates[0].length];
		for (int i = 0; i < a.nextBelStates.length; i++) {
			for (int j = 0; j < a.nextBelStates[i].length; j++)
				nextBelStates[i][j] = a.nextBelStates[i][j];
		}
		obsVals = new double[a.obsVals.length][];
		for (int i = 0; i < a.obsVals.length; i++)
			obsVals[i] = a.obsVals[i];
		obsStrat = a.obsStrat;

		nzObsIds = a.nzObsIds;
		numValidObs = a.numValidObs;
		obsValues = a.obsValues;
		sumObsValues = a.sumObsValues;
		
	}
	
	public void populateNextBelStates(DD[][] knownBeliefs) {
		nextBelStates = new DD[knownBeliefs.length][knownBeliefs[0].length];
		for (int i = 0; i < knownBeliefs.length; i++) {
			for (int j = 0; j < knownBeliefs[i].length; j++)
				nextBelStates[i][j] = knownBeliefs[i][j];
		}
	}
	
	@Override
	public String toString() {
		return Arrays.deepToString(nextBelStates);
	}

	public boolean isempty() {
		return (numValidObs == 0);
	}

	public void restrictN(DD[] marginals, int[][] obsConfig) {
		/*
		 * Changed this to use different indices for IPOMDPs
		 */
		
		int obsId;
		
		for (int obsPtr = 0; obsPtr < numValidObs; obsPtr++) {
			
			obsId = nzObsIds[obsPtr];
			
			nextBelStates[obsPtr] = OP.restrictN(marginals,
					POMDP.stackArray(this.primeObsIndices, 
							obsConfig[obsId]));
		}
	}

	 /*
	  * get the observation values obsVals[i][j] is the value expected if we see observation i
	  * and then follow the conditional plan j
	  */
	public void getObsVals(DD[] primedV) {
		
		if (!isempty()) {
			obsVals = new double[numValidObs][primedV.length];
			obsVals = OP.factoredExpectationSparse(nextBelStates,
					primedV);
		}
	}

	public double getSumObsValues() {
		return sumObsValues;
	}

	/*
	 * get observation strategy obsStrat[i] is the best conditional to plan to follow if 
	 * observation i is seen this is just the index that maximizes over the obsVals and 
	 * alphaValue is the value of that conditional plan given than observation
	 */
	public void getObsStrat() {
		
		double alphaValue = 0;
		sumObsValues = 0;
		int obsId;
		double obsProb;
		
		for (int obsPtr = 0; obsPtr < this.nObs; obsPtr++)
			obsStrat[obsPtr] = 0;

		for (int obsPtr = 0; obsPtr < numValidObs; obsPtr++) {
			
			obsId = nzObsIds[obsPtr];
			
			obsProb = nextBelStates[obsPtr][this.obsProbIndex].getVal();
			alphaValue = obsVals[obsPtr][0];
			
			for (int i = 1; i < obsVals[obsPtr].length; i++) {
				if (obsVals[obsPtr][i] > alphaValue) {
					alphaValue = obsVals[obsPtr][i];
					obsStrat[obsId] = i;
				}
			}
			
			obsValues[obsPtr] = obsProb * alphaValue;
			sumObsValues += obsValues[obsPtr];
		}
	}
	
	// -----------------------------------------------------------------------------------------
	/*
	 * Functions to get single step NextBelStates 
	 */
	
	public static HashMap<String, NextBelState> oneStepNZPrimeBelStatesCached(
			IPOMDP ipomdp,
			DD belState,
			boolean normalize, 
			double smallestProb) throws ZeroProbabilityObsException, VariableNotFoundException {
		
		/* if already computed, recover from cache */
		if (NextBelStateCache.cachingAllowed()) {
			HashMap<String, NextBelState> cachedEntry = 
					NextBelStateCache.getCachedEntry(belState);
			
			if (cachedEntry != null) return cachedEntry;
		}
		
		/* else compute and cache */
		HashMap<String, NextBelState> nzPrimes = 
				oneStepNZPrimeBelStates2(ipomdp, belState, normalize, smallestProb);
		
		if (NextBelStateCache.cachingAllowed()) {
			NextBelStateCache.cacheNZPrime(belState, nzPrimes);
		}
		
		return nzPrimes;
		
	}
	
	public static HashMap<String, NextBelState> oneStepNZPrimeBelStates2(
			IPOMDP ipomdp,
			DD belState,
			boolean normalize, 
			double smallestProb) throws ZeroProbabilityObsException, VariableNotFoundException {
		
		/*
		 * Computes NextBelStates according to the IBeliefOps methods instead of
		 * the original implementation
		 */
		
		HashMap<String, NextBelState> nextBelStates = new HashMap<String, NextBelState>();
		
		for (String act: ipomdp.getActions()) {
			
			List<DD[]> nextBelStatesForAct = new ArrayList<DD[]>();
			
			List<List<String>> allObs = ipomdp.getAllPossibleObservations();
			DD obsDist = ipomdp.getObsDist(belState, act);
			double[] obsProbs = OP.convert2array(obsDist, ipomdp.obsIVarPrimeIndices);
			
			for (int o = 0; o < allObs.size(); o++) {
				
				DD nextBelief = 
						ipomdp.beliefUpdate(
								belState, 
								act, 
								allObs.get(o).stream().toArray(String[]::new));
				
				DD[] factoredNextBel = ipomdp.factorBelief(nextBelief);
				factoredNextBel = 
						OP.primeVarsN(factoredNextBel, ipomdp.S.size() + ipomdp.Omega.size());
				
				factoredNextBel = 
						ArrayUtils.add(
								factoredNextBel, 
								DDleaf.myNew(obsProbs[o]));
				
				nextBelStatesForAct.add(factoredNextBel);
			}
			
			DD[][] nextBelStatesFactors = nextBelStatesForAct.stream().toArray(DD[][]::new);
			
			NextBelState nbState = new NextBelState(ipomdp, obsProbs, 1e-8);
			nbState.nextBelStates = nextBelStatesFactors;
			nextBelStates.put(act, nbState);
		}
		
		return nextBelStates;
	}
	
	public static HashMap<String, NextBelState> oneStepNZPrimeBelStates(
			IPOMDP ipomdp,
			DD belState,
			boolean normalize, 
			double smallestProb) throws ZeroProbabilityObsException, VariableNotFoundException {
		/*
		 * Computes the next belief states and the observation probabilities that lead to them
		 */
		
		int[][] obsConfig = ipomdp.obsCombinationsIndices;
		
		double[] obsProbs;
		DD[] marginals = new DD[ipomdp.stateVarIndices.length + 1];
		DD dd_obsProbs;

		HashMap<String, NextBelState> nextBelStates = new HashMap<String, NextBelState>();
		
		for (String Ai: ipomdp.getActions()) {
			
			/* Assuming factored belief was normalized */
			dd_obsProbs = ipomdp.getObsDist(belState, Ai); 
			
			obsProbs = OP.convert2array(dd_obsProbs, ipomdp.obsIVarPrimeIndices);
			nextBelStates.put(Ai, new NextBelState(ipomdp, obsProbs, smallestProb));

			
			/* Compute marginals */
			try {
				if (!nextBelStates.get(Ai).isempty()) {
					marginals = 
							OP.marginals(
									((IBeliefOps) ipomdp.bOPs).getCpts2( 
											belState,
											Ai), 
									ArrayUtils.subarray(
											ipomdp.stateVarPrimeIndices, 
											0, 
											ipomdp.thetaVarPosition),
									ArrayUtils.subarray(
											ipomdp.stateVarIndices, 
											0, 
											/*ipomdp.thetaVarPosition*/ipomdp.stateVarIndices.length));
					
//					logger.debug("Marginals are " + Arrays.toString(marginals));
					nextBelStates.get(Ai).restrictN(marginals, obsConfig);
//					logger.debug("After computing marginals " + nextBelStates[actId]);
					
				}
				
			}
			
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return nextBelStates;
	}
	
	public static HashMap<String, NextBelState> oneStepNZPrimeBelStates(
			IPOMDP ipomdp,
			DD[] belState,
			boolean normalize, 
			double smallestProb) throws ZeroProbabilityObsException, VariableNotFoundException {
		/*
		 * Computes the next belief states and the observation probabilities for factored belstate
		 */
		
		int[][] obsConfig = ipomdp.obsCombinationsIndices;
		
		double[] obsProbs;
		DD[] marginals = new DD[ipomdp.stateVarIndices.length + 1];
		DD dd_obsProbs;

		HashMap<String, NextBelState> nextBelStates = new HashMap<String, NextBelState>();
		
		IBeliefOps bOps = (IBeliefOps) ipomdp.bOPs;
		int[] primeVarSubArray = 
				ArrayUtils.subarray(ipomdp.stateVarPrimeIndices, 0, ipomdp.thetaVarPosition);
		
		for (String Ai: ipomdp.getActions()) {
			
			/* Assuming factored belief was normalized */
			dd_obsProbs = bOps.getObsDist2(belState, Ai);
			
			obsProbs = OP.convert2array(dd_obsProbs, ipomdp.obsIVarPrimeIndices);
			nextBelStates.put(Ai, new NextBelState(ipomdp, obsProbs, smallestProb));

			
			/* Compute marginals */
			try {
				if (!nextBelStates.get(Ai).isempty()) {
					marginals = 
							OP.marginals(
									bOps.getCpts2( 
											belState,
											Ai), 
									primeVarSubArray,
									ipomdp.stateVarIndices);
					
//					logger.debug("Marginals are " + Arrays.toString(marginals));
					nextBelStates.get(Ai).restrictN(marginals, obsConfig);
//					logger.debug("After computing marginals " + nextBelStates[actId]);
					
				}
				
			}
			
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return nextBelStates;
	}
	
	public static NextBelState[] oneStepNZPrimeBelStates(
			POMDP pomdp, DD[] belState, boolean normalize, double smallestProb) {
		

		int[][] obsConfig = new int[pomdp.nObservations][pomdp.nObsVars];
		double[] obsProbs;
		DD[] marginals = new DD[pomdp.nStateVars + 1];
		DD dd_obsProbs;
		
		for (int obsId = 0; obsId < pomdp.nObservations; obsId++)
			obsConfig[obsId] = POMDP.statedecode(obsId + 1, pomdp.nObsVars, pomdp.obsVarsArity);

		NextBelState[] nextBelStates = new NextBelState[pomdp.nActions];
		
		for (int actId = 0; actId < pomdp.nActions; actId++) {
			
			dd_obsProbs = 
					OP.addMultVarElim(
							ArrayUtils.addAll(
									belState, 
									ArrayUtils.addAll(
											pomdp.actions[actId].transFn, 
											pomdp.actions[actId].obsFn)), 
							ArrayUtils.addAll(pomdp.varIndices, pomdp.primeVarIndices));

			obsProbs = OP.convert2array(dd_obsProbs, pomdp.primeObsIndices);
			nextBelStates[actId] = new NextBelState(pomdp, obsProbs, smallestProb);

			/* Compute marginals */
			if (!nextBelStates[actId].isempty()) {
				marginals = 
						OP.marginals(
								ArrayUtils.addAll(
										belState, 
										ArrayUtils.addAll(
												pomdp.actions[actId].transFn, 
												pomdp.actions[actId].obsFn)), 
								pomdp.primeVarIndices, 
								pomdp.varIndices);
				
				nextBelStates[actId].restrictN(marginals, obsConfig);

			}

		}
		
		return nextBelStates;
	}
}
