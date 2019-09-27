/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.Belief;

import java.util.List;

import thinclab.symbolicperseus.DD;

/*
 * @author adityas
 *
 */
public abstract class BeliefRegionExpansionStrategy {
	
	/*
	 * Provides a common API for using different expansion strategies to search the
	 * belief region
	 */
	
	/* Bound on the depth of the search in the belief space */
	private int H;
	
	// -----------------------------------------------------------------------------------
	
	public BeliefRegionExpansionStrategy(int maxH) {
		/*
		 * Set required properties
		 */
		this.H = maxH;
	}
	
	// -----------------------------------------------------------------------------------
	
	public int getHBound() {
		/*
		 * Get the maximum exploration depth for which the object was initialized
		 */
		return this.H;
	}
	
	// ------------------------------------------------------------------------------------
	
	/* run the expansion algorithm */
	public abstract void expand();
	
	/* Get a list of all explored beliefs for the solver */
	public abstract List<DD> getBeliefPoints();

}
