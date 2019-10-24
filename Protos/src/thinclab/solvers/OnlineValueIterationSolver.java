/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.belief.InteractiveBelief;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.frameworks.Framework;
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
	
	/*
	 * Computes the utility for all actions for a fixed look ahead horizon.
	 * 
	 * Note that the policy obtained using this solver is not optimal for infinite horizon.
	 */
	
	/* store IPOMDP reference */
	private IPOMDP ipomdp;
	
	/* Store utilities for current step */
	private HashMap<String, Double> currentUtilities;
	
	private final static Logger logger = Logger.getLogger(OnlineValueIterationSolver.class);
	
	// --------------------------------------------------------------------------------------
	
	public OnlineValueIterationSolver(IPOMDP ipomdp) {
		
		super(ipomdp, null);
		
		this.ipomdp = ipomdp;
	}

	// --------------------------------------------------------------------------------------
	
	@Override
	public void solveCurrentStep() {
		/*
		 * Override this method because VI does not need the belief expansion separately.
		 */
		DD currentBelief = ipomdp.getInitialBeliefs().get(0); 
		
		this.currentUtilities = 
				this.computeUtilityRecursive(
						currentBelief, 0);
		
		logger.info("Utilities for belief " + InteractiveBelief.toStateMap(ipomdp, currentBelief)
				+ " for a look ahead of " + ipomdp.mjLookAhead + " are " + this.currentUtilities);
		
	}
	
	@Override
	public void solveForBeliefs(List<DD> beliefs) {
		/* 
		 * Value iteration does not do belief expansion separately. So don't implement
		 * this method
		 */
	}

	@Override
	public String getActionAtCurrentBelief() {
		/*
		 * Return the action with the highest utility value from the current utilities
		 */
		Entry<String, Double> entry = this.currentUtilities.entrySet().stream()
			.max((i, j) -> i.getValue().compareTo(j.getValue()))
			.get();
		
		return entry.getKey();
	}
	
	@Override
	public String getActionForBelief(DD belief) {
		/*
		 * Not applicable for value iteration solver for finite horizon
		 */
		return null;
	}
	
	@Override
	public boolean hasSolution() {
		return (this.currentUtilities != null);
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
		
		try {
			/*
			 * Transition the IPOMDP to a new belief by taking the specified action
			 * and observing obs.
			 */
			this.ipomdp.step(
					this.ipomdp.getInitialBeliefs().get(0), 
					action, 
					obs.toArray(new String[obs.size()]));
		} 
		
		catch (ZeroProbabilityObsException | VariableNotFoundException e) {
			logger.error("While taking action " 
					+ action + " and observing " + obs 
					+ " got error: " + e.getMessage());
			System.exit(-1);
		}
	}
	
	@Override
	public void resetBeliefExpansion() {
		/*
		 * Leave this empty because VI does not use belief expansion
		 */
	}
	
	@Override
	public void setFramework(Framework f) {
		/*
		 * Setter for the framework object
		 */
		this.f = f;
		this.ipomdp = (IPOMDP) f;
	}

}
