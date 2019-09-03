/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import java.util.logging.Logger;

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
	
	public void finiteLookAheadVI(IPOMDP ipomdp) {
		/*
		 * Perform full value iteration for the given look ahead starting from
		 * current root belief
		 */
		this.logger.info("Starting VI for " + ipomdp.mjLookAhead 
				+ " horizons starting from " + ipomdp.lookAheadRootInitBeliefs);
	}
}
