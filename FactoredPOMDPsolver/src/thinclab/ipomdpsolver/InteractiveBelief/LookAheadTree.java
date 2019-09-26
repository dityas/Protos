/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver.InteractiveBelief;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import cern.colt.Arrays;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.ipomdpsolver.IPOMDP;
import thinclab.symbolicperseus.DD;

/*
 * @author adityas
 *
 */
public class LookAheadTree {
	/*
	 * Defines a tree of interactive beliefs for the look ahead horizon
	 */
	
	private static final Logger logger = Logger.getLogger(LookAheadTree.class);
	
	/*
	 * Store reference to ipomdp object
	 */
	private IPOMDP ipomdp;
	
	/* horizon of the look ahead tree */
	public int horizon;
	
	/* set of reachable belief points */
	public HashSet<DD> iBeliefPoints = new HashSet<DD>();
	
	/* Belief tree as a map of { (start DD) : { (action) : { (obs) : (end DD) } } } */
	public HashMap<DD, HashMap<String, HashMap<List<String>, DD>>> iBeliefTree = 
			new HashMap<DD, HashMap<String, HashMap<List<String>, DD>>>();
	
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
		
		/* Connect to in memory table for storage and all that */
	}
	
	// -----------------------------------------------------------------------------------
	
	public HashSet<DD> step(HashSet<DD> previousBeliefs) {
		/*
		 * Performs interactive static belief update starting from previousBelief and returns
		 * new beliefs. 
		 */
		
		/* Store unique DDs in a set */
		HashSet<DD> nextBeliefs = new HashSet<DD>();
		
		for (DD belief : previousBeliefs) { 
			
			/* Create map for { (action) : { (obs) : (end DD) } } */
			HashMap<String, HashMap<List<String>, DD>> actionTree =
					new HashMap<String, HashMap<List<String>, DD>>();
			
			for(String actName : this.ipomdp.Ai) {
				
				/* Create map for { (obs) : (end DD) } */
				HashMap<List<String>, DD> children = new HashMap<List<String>, DD>();
				
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
						children.put(obs, nextBelief);
					}
					
					catch (ZeroProbabilityObsException e) {
						continue;
					}
					
					catch (VariableNotFoundException e) {
						this.logger.error(e.getMessage());
						System.exit(-1);
					}
				} /* for all observations */
				
				actionTree.put(actName, children);
				
			} /* for all actions */
			
			this.iBeliefTree.put(belief, actionTree);
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
	
	public String toDot() {
		/*
		 * Makes a graphviz plottable dot format string
		 */
		
		String dotString = "";
		String endl = "\r\n";
		
		dotString += "digraph G {" + endl;
		
		for (DD startB : this.iBeliefTree.keySet()) {
			dotString += " " + startB.getAddress()  
				+ " " + InteractiveBelief.toDot(this.ipomdp, startB) + endl;
		}
		
		dotString += "}" + endl;
		
		for (DD start : this.iBeliefTree.keySet()) {
			for (String actName : this.iBeliefTree.get(start).keySet()) {
				for (List<String> obs : this.iBeliefTree.get(start).get(actName).keySet()) {
					dotString += start.getAddress() + "->" 
							+ this.iBeliefTree.get(start).get(actName).get(obs).getAddress()
							+ " [label=\"" + actName + obs + " \"]" + endl;
				}
			}
		}
		
		dotString += "}";
		return dotString;
	}
	
	public List<String[]> toStringTriples() {
		/*
		 * Converts the belief tree into triples of <DD> - <act, obs> - <DD>
		 */
		
		List<String[]> triples = new ArrayList<String[]>();
		
		for (DD start : this.iBeliefTree.keySet()) {
			for (String actName : this.iBeliefTree.get(start).keySet()) {
				for (List<String> obs : this.iBeliefTree.get(start).get(actName).keySet()) {
					
					/* Insert starting belief */
					String[] triple = new String[3];
					triple[0] = InteractiveBelief.getBeliefNodeLabel(this.ipomdp, start).toString();

					/* add edge */
					triple[1] = 
							Arrays.toString(
									ArrayUtils.addAll(
											new String[] {actName}, 
											obs.toArray(new String[obs.size()])));
					
					/* add updated belief */
					triple[2] = 
							InteractiveBelief.getBeliefNodeLabel(
									this.ipomdp, 
									this.iBeliefTree.get(start).get(actName).get(obs)).toString();
					
					triples.add(triple);
				}
			}
		}
		
		return triples;
	}
}
