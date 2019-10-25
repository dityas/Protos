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
import thinclab.decisionprocesses.DecisionProcess;
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
		
		/* Expand the belief space */
		this.expansionStrategy.expand();
		List<DD> exploredBeliefs = this.expansionStrategy.getBeliefPoints();
		
		/* solve for explored beliefs */
		this.solveForBeliefs(exploredBeliefs);
	}
	
	// ------------------------------------------------------------------------------------------
	
	/* Actual solution method */
	public abstract void solveForBeliefs(List<DD> beliefs);
	
	/* Find best action for current belief */
	public abstract String getActionAtCurrentBelief();
	
	/* Update belief after taking action and observing */
	public abstract void nextStep(String action, List<String> obs);
	
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

}
