/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.MySet;
import thinclab.symbolicperseus.OP;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.Belief.Belief;

/*
 * @author adityas
 *
 */
public class InteractiveBelief extends Belief {
	/*
	 * Represents a single interactive belief
	 * 
	 * This may be a little inefficient especially during belief updates. During the actual
	 * computation, the actual belief vector will have to be accessed through the object 
	 * attributes.
	 */
	
	public List<InteractiveStateVar> IS;
	public Vector<Float> beliefPoint = new Vector<Float>();
	
	public DD[] factoredBelief;
	
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
	
	public static DD staticL1BeliefUpdate(
			IPOMDP ipomdp,
			DD startBelief, 
			String actName, 
			String[] observations) throws ZeroProbabilityObsException {
		/*
		 * Performs a static L1 IPOMDP belief update given action and observations
		 * 
		 * Performs a query over the following DBN:
		 *      .---.                    .----.
		 *      |M_j|----------.-------->|M_j'|<----------------------.
		 *      '---'          |_______. '----'                       |
		 *      .---.                  '-->.---.______________.       |	
		 *      | S |--------------------->| S'|--,           |       |
		 *      '---'                      '---'  |           |       |
		 *                                        V           V       | 		 
		 *                                      .---.       .---.     |
		 *                                      |Oi'|       |Oj'|_____|
		 *                                      '---'       '---'
		 * 	 P_hat(S', M_j' | Oi'=o) = 
		 * 			Sigma[S] (P(S)) x Sigma[M_j] (P(M_j) x P(S', S, M_j) x P(Oi'=o, S', M_j))
		 * 		 		x	Sigma[Oj'] (P(Oj', S', M_j) x P(M_j', M_j, Oj')) 
		 */
		
		
	}
	
}
