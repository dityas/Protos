/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.List;

import org.apache.commons.collections15.buffer.CircularFifoBuffer;
import org.apache.log4j.Logger;

import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.decisionprocesses.DecisionProcess;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.AlphaVector;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.utils.NextBelStateCache;
import thinclab.utils.PolicyCache;

/*
 * @author adityas
 *
 */
public abstract class AlphaVectorPolicySolver extends BaseSolver {

	/*
	 * Base class for solvers which represent policies as a set of alpha vectors
	 */
	
	private static final long serialVersionUID = 7488141502214152405L;

	/* belief region exploration */
	public BeliefRegionExpansionStrategy expansionStrategy;
	
	PolicyCache pCache = new PolicyCache(5);
	CircularFifoBuffer<Float> bErrorVals = new CircularFifoBuffer<Float>(5);
	
	/* for checking used beliefs and num alpha vectors */
	float minError = Float.POSITIVE_INFINITY;
	int numSimilar = 0;
	int errorPatience = 0;
	int numAlphas = -1;
	int numBeliefs = -1;
	int maxRounds = 1;
	
	private boolean solverConverged = false;
	
	/* policy representation */
	public DD[] alphaVectors;
	public AlphaVector[] newAlphaVectors;
	int numNewAlphaVectors;

	public int[] policy;
	double[] policyvalue;
	boolean[] uniquePolicy;
	
	public int[] bestPolicy = null;
	public DD[] bestAlphaVectors = null;
	public double bestBellmanError = Double.MAX_VALUE;
	
	private static Logger LOGGER = Logger.getLogger(AlphaVectorPolicySolver.class);
	
	// --------------------------------------------------------------------------------------
	
	public AlphaVectorPolicySolver(DecisionProcess DP, BeliefRegionExpansionStrategy BE) {
		
		this.f = DP;
		this.expansionStrategy = BE;
		
		this.setInitPolicy();
	}
	
	// --------------------------------------------------------------------------------------
	
	@Override
	public boolean hasSolution() {return this.solverConverged;}
	
	@Override
	public double evaluatePolicy(int trials, int evalDepth, boolean verbose) {
		/*
		 * Run policy evaluation for IPOMDPs
		 */
		return this.f.evaluatePolicy(
				this.getAlphaVectors(), this.getPolicy(), trials, evalDepth, verbose);
	}
	
	public void setInitPolicy() {
		/*
		 * Sets initial policy and alpha vectors
		 */
		
		/* Make a default alphaVectors as rewards to start with */
		this.alphaVectors = 
				this.f.getActions().stream()
					.map(a -> OP.reorder(this.f.getRewardFunctionForAction(a)))
					.toArray(DD[]::new);
		
		/* default policy */
		this.policy = new int[this.f.getActions().size()];
		for (int i = 0; i < this.f.getActions().size(); i++)
			this.policy[i] = this.f.getActions().indexOf(this.f.getActions().get(i));
		
		/* update belief strategy policy */
		if (this.expansionStrategy instanceof SSGABeliefExpansion) {
			LOGGER.debug("Updating expansion policy");
			((SSGABeliefExpansion) this.expansionStrategy).setRecentPolicy(
					this.alphaVectors, this.policy);
		}
		
	}
	
	public void solveCurrentStep() {
		/*
		 * Solves for the look ahead starting from current belief
		 */
		NextBelStateCache.clearCache();
		
		for (int r = 0; r < this.maxRounds; r++) {
			
			this.resetConvergenceIndicator();
		
			/* reset approx convergence patience counter */
			this.numSimilar = 0;
			this.minError = Float.POSITIVE_INFINITY;
			this.errorPatience = 0;
			
			/* if SSGA with IPBVI, set expansion policy */
			/* update belief strategy policy */
			if (this.expansionStrategy instanceof SSGABeliefExpansion) {

				((SSGABeliefExpansion) this.expansionStrategy).setRecentPolicy(
						this.getAlphaVectors(), 
						this.getPolicy());
			}
			
			/* Expand the belief space */
			this.expansionStrategy.expand();
			List<DD> exploredBeliefs = this.expansionStrategy.getBeliefPoints();
			
			Global.clearHashtables();
			
			/* solve for explored beliefs */
			this.solveForBeliefs(exploredBeliefs);
			
			if (this.isConverged() && r > 0) break;
		}
		
		this.expansionStrategy.clearMem();
		this.bestBellmanError = Double.MAX_VALUE;
		NextBelStateCache.clearCache();
	}
	
	// ------------------------------------------------------------------------------------------
	
	/* Actual solution method */
	public abstract void solveForBeliefs(List<DD> beliefs);
	public abstract void solve();
	
	/* Find best action for current belief */
	public abstract String getActionAtCurrentBelief();
	
	/* Update belief after taking action and observing */
	public abstract void nextStep(String action, List<String> obs) 
			throws ZeroProbabilityObsException;
	
	// -----------------------------------------------------------------------------------------
	
	public void resetBeliefExpansion() {
		/*
		 * Reset the belief search to th current belief of the framework
		 */
		this.expansionStrategy.resetToNewInitialBelief();
	}
	
	public DecisionProcess getFramework() {
		/*
		 * Getter for the framework object
		 */
		return this.f;
	}
	
	public void setFramework(DecisionProcess f) {
		/*
		 * Setter for the framework object
		 */
		this.f.setGlobals();
		this.f = f;
		this.expansionStrategy.setFramework(f);
	}
	
	// -------------------------------------------------------------------------------------------
	
	public float getErrorVariance(float bellManError) {
		/*
		 * Computes the variance of the last 5 iterations of the solver and
		 * returns the variance
		 */
		bErrorVals.add(bellManError);
		
		float mean = 
				(float) bErrorVals.stream()
					.mapToDouble(Double::valueOf)
					.average()
					.getAsDouble();
		
		float variance = 
				(float) (bErrorVals.stream()
					.map(v -> Math.pow((v - mean), 2))
					.mapToDouble(Double::valueOf)
					.sum() / (float) bErrorVals.size());
		
		return variance;
					
	}
	
	public boolean isConverged() {
		return this.solverConverged;
	}
	
	public void declareConvergence() {
		this.solverConverged = true;
	}
	
	public void resetConvergenceIndicator() {
		this.solverConverged = false;
	}
	
	public boolean isNumAlphaConstant(int numAlphas) {
		/*
		 * Checks if the number of alpha vectors hasn't changed in the last 5 iterations
		 */
		
		if (this.numAlphas != numAlphas) {
			 this.numAlphas = numAlphas;
			 return false;
		}
		
		else return true;
	}
	
	public boolean isNumUsedBeliefsConstant(int numUsedBeliefs, int numBeliefs) {
		/*
		 * Checks if number of used beliefs is same as number of total beliefs
		 * for the last 5 iterations. This could mean convergence 
		 */
		
		if (numUsedBeliefs == numBeliefs) {
			
			if (this.numBeliefs != numUsedBeliefs) {
				this.numBeliefs = numUsedBeliefs;
				return false;
			}
			
			else {
				return true;
			}
		}
		
		else return false;
	}
	
	public boolean declareApproxConvergenceForAlphaVectors(
			int numAlphas, int numUsedBeliefs, int numBeliefs) {
		/*
		 * Checks if the number of alpha vectors and the number of used beliefs is the
		 * same for the last few iterations.
		 */
		
		if (this.isNumAlphaConstant(numAlphas) && 
				this.isNumUsedBeliefsConstant(numUsedBeliefs, numBeliefs)) {
			
			this.numSimilar += 1;
			
			/* set all trackers to initial values if declaring convergence */
			if (this.numSimilar >= 5) {
				
				this.numAlphas = -1;
				this.numBeliefs = -1;
				this.numSimilar = 0;
				
				return true;
			}
			
			else return false;
		}
		
		else {
			
			this.numSimilar = 0;
			return false;
		}
	}
	
	public boolean isErrorNonDecreasing(float bError) {
		/*
		 * To declare approximate convergence if bellman error is non decreasing after
		 * many iterations
		 */
		
		/* error is decreasing, reset patience */
		if (bError < this.minError) {
			this.errorPatience = 0;
			this.minError = bError;
			
			return false;
		}
		
		else {
			
			if (this.errorPatience >= 20) {
				this.errorPatience = 0;
				this.minError = Float.POSITIVE_INFINITY;
				
				return true;
			}
			
			else {
				this.errorPatience += 1;
				return false;
			}
		}
		
	}
	
	// -----------------------------------------------------------------------------------------
	
	public DD[] getAlphaVectors() {
		return this.alphaVectors;
	}
	
	public int[] getPolicy() {
		return this.policy;
	}
}
