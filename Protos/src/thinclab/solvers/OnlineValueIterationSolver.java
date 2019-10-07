/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.frameworks.IPOMDP;
import thinclab.legacy.DD;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public class OnlineValueIterationSolver extends OnlineSolver {
	
	
	private IPOMDP ipomdp;
	
	/*
	 * Cache belief updates onces they are done to save time
	 */
	private HashMap<DD, HashMap<List<List<String>>, DD>> beliefUpdateCache = 
			new HashMap<DD, HashMap<List<List<String>>, DD>>();
	
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
	
	public HashMap<String, Double> computeUtilityRecursive(DD belief, int depth) {
		/*
		 * 
		 */
		
		HashMap<String, Double> utility = new HashMap<String, Double>();
		
		/* if this is the boundary of the look ahead, just compute ER(S, Ai) */
		if (depth >= this.ipomdp.mjLookAhead) {
			
			for (String Ai : this.ipomdp.getActions()) {
				
				/* Compute ER(S, Ai) */
				DD[] ER = new DD[] {this.ipomdp.currentRi.get(Ai), belief};
				
				
				
				
				/* Final utility */
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
			
		}
		
		else {
			
		}
		
		return utility;
	}

	@Override
	public void nextStep(String action, List<String> obs) {
		// TODO Auto-generated method stub

	}

}
