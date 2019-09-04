/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import java.util.HashMap;
import java.util.logging.Logger;

import thinclab.ipomdpsolver.InteractiveBelief.LookAheadTree;
import thinclab.utils.LoggerFactory;

/*
 * @author adityas
 *
 */
public class FiniteHorizonLookAheadValueIterationSolver {
	/*
	 * Value iteration solver for IPOMDPs with finite look ahead horizon
	 * 
	 * 
	 */
	
	private Logger logger = LoggerFactory.getNewLogger("FHLAVISolver: ");
	
	// --------------------------------------------------------------------------------------
	
	public static void finiteLookAheadVI(IPOMDP ipomdp) {
		/*
		 * Perform full value iteration for the given look ahead starting from
		 * current root belief
		 */
		
		Logger logger = LoggerFactory.getNewLogger("FHLAVISolver: ");
		
		logger.info("Starting VI for " + ipomdp.mjLookAhead 
				+ " horizons starting from " + ipomdp.lookAheadRootInitBeliefs);
		
		LookAheadTree laTree = ipomdp.getLookAheadTree();
	}
	
	public static HashMap<String, Float> computeUtility(
			IPOMDP ipomdp, DD currentBelief) {
		/*
		 * Computes the utility of being in the current belief state recursively and maps 
		 * it to the action taken
		 */
		
		/* First compute immediate reward */
	}
	
	// --------------------------------------------------------------------------------------
	
}
