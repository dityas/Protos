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
import java.util.List;
import java.util.logging.Logger;

import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
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
		this.expand();
	}
	
	// -----------------------------------------------------------------------------------
	
	public HashSet<DD> step(HashSet<DD> previousBeliefs) {
		/*
		 * Performs interactive static belief update starting from previousBelief and returns
		 * new beliefs. 
		 */
		HashSet<DD> nextBeliefs = new HashSet<DD>();
		
		for (DD belief : previousBeliefs) {
			
			for(String actName : this.ipomdp.Ai) {
				for (List<String> obs : this.ipomdp.obsCombinations) {
					
					/* Try performing the belief update */
					try {
						DD nextBelief = 
								InteractiveBelief.staticL1BeliefUpdate(
										this.ipomdp, 
										belief, 
										actName, 
										obs.toArray(new String[obs.size()]));
						
						nextBeliefs.add(nextBelief);
					}
					
					catch (ZeroProbabilityObsException e) {
						continue;
					}
					
					catch (VariableNotFoundException e) {
						this.logger.severe(e.getMessage());
						System.exit(-1);
					}
				} /* for all observations */
			} /* for all actions */
		}
		
		return nextBeliefs;
	}
	
	public void expand() {
		/*
		 * Build the look ahead tree for the IPOMDP starting from the current roots.
		 */
		
		HashSet<DD> currentIBeliefSet = new HashSet<DD>();
		
		/* Add starting beliefs to the belief set */
		currentIBeliefSet.addAll(this.ipomdp.lookAheadRootInitBeliefs);
		iBeliefPoints.addAll(currentIBeliefSet);
		
		for (int h = 0; h < this.horizon; h++) {
			
			HashSet<DD> nextBeliefs = this.step(currentIBeliefSet);
			
			iBeliefPoints.addAll(nextBeliefs);
			currentIBeliefSet = nextBeliefs;
		}
		
		this.logger.info("Finish look ahead expansion. The tree has " 
				+ this.iBeliefPoints.size() + " beliefs.");
	}
}
