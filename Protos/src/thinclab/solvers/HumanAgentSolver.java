/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.List;

import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public class HumanAgentSolver extends BaseSolver {

	/*
	 * Wrapper for a human agent 
	 */
	
	private static final long serialVersionUID = -4240111977003656558L;

	@Override
	public String getActionForBelief(DD belief) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasSolution() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void solveForBeliefs(List<DD> beliefs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void solve() {
		// TODO Auto-generated method stub

	}

	@Override
	public double evaluatePolicy(int trials, int evalDepth, boolean verbose) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void nextStep(String action, List<String> obs) throws ZeroProbabilityObsException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getActionAtCurrentBelief() {
		// TODO Auto-generated method stub
		return null;
	}

}
