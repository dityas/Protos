/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policyhelper;

import java.util.ArrayList;
import java.util.List;

import thinclab.symbolicperseus.POMDP;

/*
 * @author adityas
 *
 */
public class PolicyTree {
	/*
	 * Constructs the policy tree for a solved POMDP
	 * 
	 * Similar to policy graph. But in this case, it computes the policy up to a given horizon
	 */
	
	public POMDP pomdp;
	public List<List<String>> allObsCombinations = new ArrayList<List<String>>();
	
	public PolicyTree(POMDP p, int horizon) {
		/*
		 * Constructor makes the policy tree for the given horizon
		 */
		this.pomdp = p;
		
		/*
		 * Make a list of all possible observation variable combinations.
		 */
		this.allObsCombinations = this.pomdp.getAllObservationsList();
	}
	
	public List<PolicyNode> expandForSingleStep(List<PolicyNode> previousLeaves) {
		/*
		 * Expands the policy tree for a single horizon
		 */
		List<PolicyNode> newLeaves = new ArrayList<PolicyNode>();
		
		previousLeaves.stream().map(n -> n.belief);
		
		return newLeaves;
	}
	
	public void expandForHorizons(int horizons) {
		/*
		 * Expands the policy tree for given number of horizons
		 */
		List<DD> 
	}
	
}
