/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.List;

import org.apache.log4j.Logger;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public class DefaultActionPolicySolver extends BaseSolver {
	
	/*
	 * Returns a single action for every belief. 
	 */
	
	private String defaultAction;
	
	private static final long serialVersionUID = 3084918300335447297L;
	private static final Logger LOGGER = Logger.getLogger(DefaultActionPolicySolver.class);
	
	// ---------------------------------------------------------------------------------------
	
	public DefaultActionPolicySolver(DecisionProcess DP, String defaultAction) {
		
		this.f = DP;
		
		if (this.f.getActions().contains(defaultAction))
			this.defaultAction = defaultAction;
		
		else {
			LOGGER.error("Action " + defaultAction + " is not a valid action");
			System.exit(-1);
		}
		
		LOGGER.info("DefaultAction solver initialised with action " + this.defaultAction);
	}
	
	// ---------------------------------------------------------------------------------------

	@Override
	public String getActionForBelief(DD belief) {
		return this.defaultAction;
	}

	@Override
	public boolean hasSolution() {
		return true;
	}

	@Override
	public void solveForBeliefs(List<DD> beliefs) {
		LOGGER.warn("Calling a solve method on DefaultPolicySolver. Nothing will happen");
	}

	@Override
	public void solve() {
		LOGGER.warn("Calling a solve method on DefaultPolicySolver. Nothing will happen");
	}

	@Override
	public double evaluatePolicy(int trials, int evalDepth, boolean verbose) {
		return this.f.evaluateDefaultPolicy(this.defaultAction, trials, evalDepth, verbose);
	}

	@Override
	public void nextStep(String action, List<String> obs) throws ZeroProbabilityObsException {
		
		try {
			this.f.step(this.f.getCurrentBelief(), action, obs.stream().toArray(String[]::new));
		} 
		
		catch (Exception e) {
			LOGGER.error("While stepping the solver " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public String getActionAtCurrentBelief() {
		return this.defaultAction;
	}

}
