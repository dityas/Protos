/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver.InteractiveBelief;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import thinclab.ipomdpsolver.IPOMDP;
import thinclab.symbolicperseus.DD;
import thinclab.utils.LoggerFactory;

/*
 * @author adityas
 *
 */
public class LookAheadTree {
	/*
	 * Defines a tree of interactive beliefs for the look ahead horizon
	 */
	
	private Logger logger = LoggerFactory.getNewLogger("LATree: ");
	
	/*
	 * Store reference to ipomdp object
	 */
	private IPOMDP ipomdp;
	
	/* horizon of the look ahead tree */
	public int horizon;
	
	/* set of reachable belief points */
	public HashSet<DD> iBeliefPoints = new HashSet<DD>();
	
	// ----------------------------------------------------------------------------------
	
	public LookAheadTree(IPOMDP ipomdp) {
		/*
		 * Store reference to IPOMDP and look ahead horizon
		 */
		this.logger.info("Initializing new LookAheadTree");
		
		this.ipomdp = ipomdp;
		this.horizon = ipomdp.mjLookAhead;
		
		this.logger.info("Expanding for " + this.horizon + " look ahead horizon");
	}
	
	public void expand() {
		/*
		 * Build the look ahead tree for the IPOMDP starting from the current roots.
		 */
		
		HashSet<DD> currentIBeliefSet = new HashSet<DD>();
		currentIBeliefSet.addAll(this.ipomdp.lookAheadRootInitBelief);
		iBeliefPoints.addAll()
	}
}
