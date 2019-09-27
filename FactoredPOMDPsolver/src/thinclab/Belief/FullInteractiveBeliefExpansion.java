/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.Belief;

import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.ipomdpsolver.IPOMDP;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.POMDP;

/*
 * @author adityas
 *
 */
public class FullInteractiveBeliefExpansion extends FullBeliefExpansion {
	/*
	 * Search the full interactive belief space for IPOMDPs
	 */
	
	/* reference to the POMDP */
	private IPOMDP ip;
	
	/* All possible combinations of observations */
	public List<List<String>> allPossibleObs;
	
	/* Currently explored beliefs and leaves for expansion */
	public List<DD> leaves;
	public HashSet<DD> exploredBeliefs;
	
	private static final Logger logger = Logger.getLogger(FullInteractiveBeliefExpansion.class);
	
	// --------------------------------------------------------------------------------------------
	
	public FullInteractiveBeliefExpansion(IPOMDP ip, int maxDepth) {
		/*
		 * Set properties and all that
		 */
		super(maxDepth);
		
		this.ip = ip;
		this.allPossibleObs = ip.obsCombinations;
	}
	
	// --------------------------------------------------------------------------------------------

}
