/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.symbolicperseus;

import java.util.Arrays;

import thinclab.ipomdpsolver.IPOMDP;

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
		
		nextBelStates = new DD[numValidObs][this.ipomdp.nStateVars + 1];
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
			
			if (this.ipomdp == null)
				obsProb = nextBelStates[obsPtr][this.pomdp.nStateVars].getVal();
			
			else obsProb = nextBelStates[obsPtr][this.ipomdp.nStateVars].getVal();
			
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
}
