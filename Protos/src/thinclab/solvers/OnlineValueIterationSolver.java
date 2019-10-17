/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.Arrays;
import java.util.Comparator;
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
	
		/* compute ER(S, Ai) */
		for (String Ai : this.ipomdp.getActions()) {
			
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
		
		if (depth < ipomdp.mjLookAhead) {
			
			try {
				HashMap<String, NextBelState> nextBeliefs = 
						oneStepNZPrimeBelStates(
								InteractiveBelief.factorInteractiveBelief(ipomdp, belief), 
								false, 0.000001);
				
				for (String Ai : nextBeliefs.keySet()) {
					
					/* Compute the next belief state */
					double utilityAi = 0.0;
					DD[][] nextBelStates = nextBeliefs.get(Ai).nextBelStates;
					
					for (DD[] nextBelState : nextBelStates) {
						
						/* Unfactor the belief point */
						DD nextBelief = 
								OP.primeVars(
										OP.multN(
												ArrayUtils.subarray(
														nextBelState, 
														0, nextBelState.length - 1)), 
										-(ipomdp.S.size() + ipomdp.Omega.size()));
						
						double obsProb = nextBelState[nextBelState.length - 1].getVal();
						
						if (obsProb < 0.00001) continue;
						
						/* Get utilities for nextBelief */
						HashMap<String, Double> nextUtils = 
								computeUtilityRecursive(nextBelief, depth + 1);
						
						/* Find max utility */
						double maxUtil = 
								nextUtils.values().stream()
									.max(Comparator.comparing(Double::valueOf)).get();
						
						/* compute P(Oi=o | Ai, b) * U(SE(b, Ai, o)) */
						utilityAi += (obsProb * maxUtil);
						
					} /* for all possible o in Oi */
					
					/* gamma * utilityAi */
					double ER = utility.get(Ai);
					utility.put(Ai, ER + (ipomdp.discFact * utilityAi));
				}
			} 
			
			catch (ZeroProbabilityObsException | VariableNotFoundException e) {
				/* do nothing */
			}
		}
		
//		logger.debug("At lookahead=" + depth + " utility is " + utility);
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
