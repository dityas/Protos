/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.Solvers;

import java.util.List;

import thinclab.Belief.BeliefRegionExpansionStrategy;
import thinclab.frameworks.Framework;
import thinclab.symbolicperseus.DD;

/*
 * @author adityas
 *
 */
public abstract class OnlineSolver {
	
	/*
	 * Defines the basic skeleton and structure for implementing Online Solvers
	 */
	
	public Framework f;
	private BeliefRegionExpansionStrategy expansionStrategy;

	// ------------------------------------------------------------------------------------------
	
	public OnlineSolver(Framework f, BeliefRegionExpansionStrategy b) {
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
	public abstract String getBestActionAtCurrentBelief();
	
	/* Update belief after taking action and observing */
	public abstract void nextStep(String action, List<String> obs);

}
