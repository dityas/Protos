/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import java.util.List;
import java.util.Vector;

/*
 * @author adityas
 *
 */
public class InteractiveBelief {
	/*
	 * Represents a single interactive belief
	 * 
	 * This may be a little inefficient especially during belief updates. During the actual
	 * computation, the actual belief vector will have to be accessed through the object 
	 * attributes.
	 */
	
	public List<InteractiveStateVar> IS;
	public Vector<Float> beliefPoint = new Vector<Float>();
	
	public InteractiveBelief(List<InteractiveStateVar> IS) {
		this.IS = IS;
	}
	
	public InteractiveBelief(List<InteractiveStateVar> IS, Vector<Float> beliefPoint) {
		this.IS = IS;
		this.beliefPoint = beliefPoint;
	}

	// -----------------------------------------------------------------------------------
	
	public void makeUniformBelief() {
		/*
		 * Populates this.beliefPoint as a uniform distribution over all IS
		 */
		float beliefDimension = (float) this.IS.size();
		
		for (int i=0; i < this.IS.size(); i++) 
			this.beliefPoint.add((float) 1.0 / beliefDimension);
	}
	
	// -------------------------------------------------------------------------------------
	
	
}
