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
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVector;

/*
 * @author adityas
 *
 */
public interface PBVISolvable {

	/*
	 * Implementing class should perform backup operation of alpha vectors at a
	 * given belief point
	 */
	public AlphaVector backup(final DD b, 
            final List<DD> alphas, final ReachabilityGraph g);

    public List<DD> MDPValueIteration(List<DD> QFn);
}
