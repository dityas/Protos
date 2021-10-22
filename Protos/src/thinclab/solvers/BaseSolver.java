/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.io.Serializable;
import java.util.List;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public abstract class BaseSolver implements Serializable {
	
	private static final long serialVersionUID = 1521369802284824797L;
	
	/* reference to the framework */
	public DecisionProcess f;
	
	public abstract String getActionForBelief(DD belief);
	
	/* return the status of the solver */
	public abstract boolean hasSolution();
	
	/* Actual solution method */
	public abstract void solveForBeliefs(List<DD> beliefs);
	public abstract void solve();
	
	/* evaluate policy */
	public abstract float evaluatePolicy(int trials, int evalDepth, boolean verbose);
	
	/* Update belief after taking action and observing for online solvers */
	public abstract void nextStep(String action, List<String> obs) 
			throws ZeroProbabilityObsException;
	
	/* Find best action for current belief for online solvers */
	public abstract String getActionAtCurrentBelief();
}
