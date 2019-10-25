/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.legacy;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.belief.InteractiveBelief;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;

/*
 * @author adityas
 *
 */
public class NextBelState {
	
	public DD[][] nextBelStates;
	public int[] nzObsIds;
	public double[][] obsVals;
	public int numValidObs;
	public int[] obsStrat;
	public double[] obsValues;
	public double sumObsValues;
	public IPOMDP ipomdp;
	public POMDP pomdp;
	
	private static final Logger logger = Logger.getLogger(NextBelState.class);
	
	public NextBelState(POMDP p, double[] obsProbs, double smallestProb) {
		/*
		 * For POMDPs
		 * 
		 * Same as Hoey's implementation with a POMDP passed explicitly as an argument
		 */
		
		this.pomdp = p;
		numValidObs = 0;
		
		for (int i = 0; i < obsProbs.length; i++)
			if (obsProbs[i] > smallestProb)
				numValidObs++;
		
		nextBelStates = new DD[numValidObs][this.pomdp.nStateVars + 1];
		nzObsIds = new int[numValidObs];
		obsStrat = new int[this.pomdp.nObservations];
		obsValues = new double[numValidObs];
		int j = 0;
		for (int i = 0; i < obsProbs.length; i++)
			if (obsProbs[i] > smallestProb)
				nzObsIds[j++] = i;
	}
	
	public NextBelState(IPOMDP ip, double[] obsProbs, double smallestProb) {
		
		this.ipomdp = ip;
		numValidObs = 0;
		
		for (int i = 0; i < obsProbs.length; i++)
			if (obsProbs[i] > smallestProb)
				numValidObs++;
		
		nextBelStates = new DD[numValidObs][this.ipomdp.nStateVars - 1];
		nzObsIds = new int[numValidObs];
		obsStrat = new int[this.ipomdp.obsCombinations.size()];
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
		
		if (a.ipomdp == null) this.pomdp = a.pomdp;
		
		else this.ipomdp = a.ipomdp;
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
		
		if (this.ipomdp == null) {
			for (int obsPtr = 0; obsPtr < numValidObs; obsPtr++) {
				
				obsId = nzObsIds[obsPtr];
				
				nextBelStates[obsPtr] = OP.restrictN(marginals,
						POMDP.stackArray(this.pomdp.primeObsIndices, 
								obsConfig[obsId]));
			}
		}
		
		else {
			for (int obsPtr = 0; obsPtr < numValidObs; obsPtr++) {
				
				obsId = nzObsIds[obsPtr];
				
				nextBelStates[obsPtr] = OP.restrictN(marginals,
						IPOMDP.stackArray(this.ipomdp.obsIVarPrimeIndices, 
								obsConfig[obsId]));
//				System.out.println("OP marginals are " + Arrays.toString(nextBelStates[obsPtr]));
			}
		}
	}

	 /*
	  * get the observation values obsVals[i][j] is the value expected if we see observation i
	  * and then follow the conditional plan j
	  */
	public void getObsVals(DD[] primedV) {
		
		if (!isempty()) {
			obsVals = new double[numValidObs][primedV.length];
			obsVals = OP.factoredExpectationSparseNoMem(nextBelStates,
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
		
		if (this.ipomdp == null) {
			
			for (int obsPtr = 0; obsPtr < this.pomdp.nObservations; obsPtr++)
				obsStrat[obsPtr] = 0;
		}
		
		else {
			for (int obsPtr = 0; obsPtr < this.ipomdp.obsCombinations.size(); obsPtr++)
				obsStrat[obsPtr] = 0;
		}
		
		for (int obsPtr = 0; obsPtr < numValidObs; obsPtr++) {
			
			obsId = nzObsIds[obsPtr];
			
			if (this.ipomdp == null) {
				obsProb = nextBelStates[obsPtr][this.pomdp.nStateVars].getVal();
//				logger.debug("NextBelStates[obsPtr] = " + (nextBelStates[obsPtr][this.pomdp.nStateVars]));
			}
				
			
			else {
				obsProb = nextBelStates[obsPtr][this.ipomdp.nStateVars - 1].getVal();
//				logger.debug("NextBelStates[obsPtr] = " + (nextBelStates[obsPtr][this.ipomdp.nStateVars - 1]));
			}
			
//			System.out.println("NextBelStates[obsPtr][nS] = " 
//					+ nextBelStates[obsPtr][this.ipomdp.nStateVars - 1]);}
			
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
	
	public static HashMap<String, NextBelState> oneStepNZPrimeBelStates(
			IPOMDP ipomdp,
			DD[] belState,
			boolean normalize, 
			double smallestProb) throws ZeroProbabilityObsException, VariableNotFoundException {
		/*
		 * Computes the next belief states and the observation probabilities that lead to them
		 */
		
		int[][] obsConfig = 
				new int[ipomdp.obsCombinations.size()][ipomdp.obsIVarIndices.length];
		
		double[] obsProbs;
		DD[] marginals = new DD[ipomdp.stateVarIndices.length + 1];
		DD dd_obsProbs;
		
		for (int obsId = 0; obsId < ipomdp.obsCombinations.size(); obsId++)
			obsConfig[obsId] = 
				ipomdp.statedecode(
						obsId + 1,
						ipomdp.obsIVarIndices.length, 
						ArrayUtils.subarray(
								ipomdp.obsVarsArity, 
								0, 
								ipomdp.obsIVarIndices.length));
		
		Global.newHashtables();
		
		HashMap<String, NextBelState> nextBelStates = new HashMap<String, NextBelState>();
		
		for (String Ai: ipomdp.getActions()) {
		
			/* Assuming factored belief was normalized */
			dd_obsProbs = 
					InteractiveBelief.getL1BeliefUpdateNorm(
							ipomdp, 
							OP.reorder(OP.multN(belState)), Ai);

			obsProbs = OP.convert2array(dd_obsProbs, ipomdp.obsIVarPrimeIndices);
			nextBelStates.put(Ai, new NextBelState(ipomdp, obsProbs, smallestProb));
//			logger.debug("Obs Probs are " + Arrays.toString(obsProbs));
//			logger.debug("Obs Config is " + Arrays.deepToString(obsConfig));
			
			/*
			 * Compute marginals
			 */
			try {
				if (!nextBelStates.get(Ai).isempty()) {
					marginals = 
							OP.marginals(
									InteractiveBelief.getCpts(
											ipomdp, 
											belState, 
											Ai), 
									ArrayUtils.subarray(
											ipomdp.stateVarPrimeIndices, 
											0, 
											ipomdp.stateVarIndices.length - 1),
									ArrayUtils.subarray(
											ipomdp.stateVarIndices, 
											0, 
											ipomdp.stateVarIndices.length - 1));
					
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
			obsConfig[obsId] = pomdp.statedecode(obsId + 1, pomdp.nObsVars, pomdp.obsVarsArity);

		Global.newHashtables();
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
