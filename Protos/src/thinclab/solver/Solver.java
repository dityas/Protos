/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solver;

import thinclab.legacy.DD;
import thinclab.policy.Policy;
import thinclab.models.PBVISolvablePOMDPBasedModel;

/*
 * @author adityas
 *
 */
public interface Solver {

	public Policy<DD> solve(final PBVISolvablePOMDPBasedModel m);
}
