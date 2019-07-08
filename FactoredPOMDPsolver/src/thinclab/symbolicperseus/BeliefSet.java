/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.symbolicperseus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/*
 * @author adityas
 *
 */
public class BeliefSet {
	/*
	 * Contains a set of belief points
	 * 
	 * Useful for creating belief regions while solving POMDPs
	 */
	
	/*
	 * Store the starting points for the belief trees
	 */
	public List<DD> initialBeliefs = new ArrayList<DD>();
	
	/*
	 * Also make a HashSet of beliefs computed so far.
	 */
	public HashSet<DD> beliefSet = new HashSet<DD>();
	
	public BeliefSet(List<DD> initialBeliefs) {
		/*
		 * Add initial beliefs and populated the belief set with them
		 */
		this.initialBeliefs.addAll(initialBeliefs);
		this.beliefSet.addAll(initialBeliefs);
	}
	
	// --------------------------------------------------------------------------------
	
	public boolean checkBeliefPointExists(DD point) {
		/*
		 * Check if point exists in the current belief region
		 */
		
		return this.beliefSet.contains(point); 
	}
	
	public boolean checkBeliefPointExists(DD[] point) {
		/*
		 * Check if point exists in the current belief region
		 */
		
		return this.beliefSet.contains(OP.multN(point)); 
	}
	
	// --------------------------------------------------------------------------------
}
