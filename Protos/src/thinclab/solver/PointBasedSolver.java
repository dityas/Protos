/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solver;

import java.util.List;
import thinclab.legacy.DD;
import thinclab.policy.Policy;

/*
 * @author adityas
 *
 */
public interface PointBasedSolver<P extends Policy<DD>> {

	public P solve(final List<DD> b_is, int I, int H);
}
