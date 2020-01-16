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

import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.decisionprocesses.DecisionProcess;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.utils.PolicyCache;

/*
 * @author adityas
 *
 */
public abstract class OnlineSolver extends BaseSolver {
	
	/*
	 * Defines the basic skeleton and structure for implementing Online Solvers
	 */

	private static final long serialVersionUID = -2064622541038073651L;

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

	// ------------------------------------------------------------------------------------------
	
	public OnlineSolver(DecisionProcess f, BeliefRegionExpansionStrategy b) {
		/*
		 * Set properties and all that
		 */
		
		this.f = f;
		this.expansionStrategy = b;
	}
	
	// ------------------------------------------------------------------------------------------
	
	public void solveCurrentStep() {
		/*
		 * Solves for the look ahead starting from current belief
		 */
		
		for (int r = 0; r < this.maxRounds; r++) {
		
			/* reset approx convergence patience counter */
			this.numSimilar = 0;
			this.minError = Float.POSITIVE_INFINITY;
			this.errorPatience = 0;
			
			/* if SSGA with IPBVI, set expansion policy */
			/* update belief strategy policy */
			if (this.expansionStrategy instanceof SSGABeliefExpansion && 
					this instanceof OnlineIPBVISolver) {

				((SSGABeliefExpansion) this.expansionStrategy).setRecentPolicy(
						((OnlineIPBVISolver) this).alphaVectors, 
						((OnlineIPBVISolver) this).policy);
			}
			
			/* Expand the belief space */
			this.expansionStrategy.expand();
			List<DD> exploredBeliefs = this.expansionStrategy.getBeliefPoints();
			
			/* solve for explored beliefs */
			this.solveForBeliefs(exploredBeliefs);
		}
	}
	
	// ------------------------------------------------------------------------------------------
	
	/* Actual solution method */
	public abstract void solveForBeliefs(List<DD> beliefs);
	
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
			if (this.numSimilar >= 3) {
				
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
			
			if (this.errorPatience >= 9) {
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

}
