/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

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
	private Scanner consoleReader;
	public List<String> allowedActions = new ArrayList<String>();
	public HashMap<String, String> helpStringsMap = new HashMap<String, String>();
	
	//------------------------------------------------------------------------------
	
	public HumanAgentSolver(
			HashMap<String, String> humanActionstoPOMDPActionsMap,
			HashMap<String, String> helpStringsMap) {
		this.consoleReader = new Scanner(System.in);
		this.allowedActions.addAll(humanActionstoPOMDPActionsMap.keySet());
		this.helpStringsMap.putAll(helpStringsMap);
	}
	
	//------------------------------------------------------------------------------

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
	
	public void showObservation(String observation) {
		/*
		 * Print observation from performing previous action to stdout
		 */
		System.out.println("---- OUTPUT ----");
		System.out.println(observation);
		System.out.println("---- END OUTPUT ----");
	}
	
	public String getHumanAction() {
		System.out.println("---- HUMAN INPUT ----\r\n");
		
		System.out.println("Allowed actions (Case insensitive)\r\n");
		for (String action: this.allowedActions) {
			System.out.println(action.toLowerCase() + " -- " + this.helpStringsMap.get(action));
		}
		
		String humanAction = "";
		
		while (!this.allowedActions.contains(humanAction)) {
			System.out.print(">>> ");
			humanAction = this.consoleReader.nextLine().toUpperCase();
		}
		
		System.out.println("Performing action: " + humanAction);
		System.out.println("---- END HUMAN INPUT AND WAIT NEXT ROUND ----");
		return humanAction;
	}

}
