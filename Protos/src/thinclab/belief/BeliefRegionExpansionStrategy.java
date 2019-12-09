/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.belief;

import java.io.Serializable;
import java.util.List;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public abstract class BeliefRegionExpansionStrategy implements Serializable {
	
	/*
	 * Provides a common API for using different expansion strategies to search the
	 * belief region
	 */
	
	private static final long serialVersionUID = 8644971870780595982L;

	/* Bound on the depth of the search in the belief space */
	private int H;
	
	/* Store reference to framework */
	DecisionProcess f;
	
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
	
	/* Reset search to new roots if the framework transitions to a different belief */
	public abstract void resetToNewInitialBelief();
	
	/* Clear all beliefs to save memory */
	public abstract void clearMem();
	
	// -------------------------------------------------------------------------------------
	
	public void setFramework(DecisionProcess f) {
		/*
		 * Setter for the framework member
		 */
		this.f = f;
	}
}
