/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.belief.InteractiveBelief;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.frameworks.IPOMDP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.NextBelState;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public class OnlineValueIterationSolver extends OnlineSolver {
	
	
	private IPOMDP ipomdp;
	
	/*
	 * Cache belief updates onces they are done to save time
	 */
	private HashMap<DD, HashMap<List<List<String>>, DD>> beliefUpdateCache = 
			new HashMap<DD, HashMap<List<List<String>>, DD>>();
	
	private final static Logger logger = Logger.getLogger(OnlineValueIterationSolver.class);
	
	public OnlineValueIterationSolver(IPOMDP ipomdp) {
		
		super(ipomdp, null);
		
		this.ipomdp = ipomdp;
	}
	
	
	@Override
	public void solveForBeliefs(List<DD> beliefs) {
		
	}

	@Override
	public String getBestActionAtCurrentBelief() {
		
		return null;
	}
	
	// ---------------------------------------------------------------------------------
	
	public HashMap<String, Double> computeUtilityRecursive(DD belief, int depth) {
		/*
		 * 
		 */
		
		HashMap<String, Double> utility = new HashMap<String, Double>();
		
		/* if this is the boundary of the look ahead, just compute ER(S, Ai) */
		if (depth >= this.ipomdp.mjLookAhead) {
			
			for (String Ai : this.ipomdp.getActions()) {
				
				/* Compute ER(S, Ai) */
				DD[] ER = new DD[] {this.ipomdp.currentRi.get(Ai), belief};
				
				
				
				
				/* Final utility */
				utility.put(
						Ai, 
						OP.addMultVarElim(
								new DD[] {
										this.ipomdp.currentRi.get(Ai),
										belief}, 
								ArrayUtils.subarray(
										this.ipomdp.stateVarIndices,
										0,
										this.ipomdp.stateVarIndices.length - 1)).getVal());
			}
			
		}
		
		else {
			
			try {
				HashMap<String, NextBelState> nextBeliefs = 
						oneStepNZPrimeBelStates(
								InteractiveBelief.factorInteractiveBelief(ipomdp, belief), 
								true, 0.000001);
				
				System.out.println(nextBeliefs);
			} 
			
			catch (ZeroProbabilityObsException | VariableNotFoundException e) {
				/* do nothing */
			}
		}
		
		return utility;
	}

	public HashMap<String, NextBelState> oneStepNZPrimeBelStates(
			DD[] belState,
			boolean normalize, 
			double smallestProb) throws ZeroProbabilityObsException, VariableNotFoundException {
		/*
		 * Computes the next belief states and the observation probabilities that lead to them
		 */
		
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
		
//		NextBelState[] nextBelStates = new NextBelState[this.ipomdp.Ai.size()];
		HashMap<String, NextBelState> nextBelStates = new HashMap<String, NextBelState>();
		/*
		 * TODO: iterate over actNames instead of IDs
		 */
		for (String Ai: this.ipomdp.Ai) {
		
			/* Assuming factored belief was normalized */
			dd_obsProbs = 
					InteractiveBelief.getL1BeliefUpdateNorm(
							this.ipomdp, 
							OP.reorder(OP.multN(belState)), Ai);

			obsProbs = OP.convert2array(dd_obsProbs, this.ipomdp.obsIVarPrimeIndices);
			nextBelStates.put(Ai, new NextBelState(this.ipomdp, obsProbs, smallestProb));
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
											this.ipomdp, 
											belState, 
											Ai), 
									ArrayUtils.subarray(
											this.ipomdp.stateVarPrimeIndices, 
											0, 
											this.ipomdp.stateVarIndices.length - 1),
									ArrayUtils.subarray(
											this.ipomdp.stateVarIndices, 
											0, 
											this.ipomdp.stateVarIndices.length - 1));
					
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
	
	// ---------------------------------------------------------------------------------
	
	@Override
	public void nextStep(String action, List<String> obs) {
		// TODO Auto-generated method stub

	}

}
