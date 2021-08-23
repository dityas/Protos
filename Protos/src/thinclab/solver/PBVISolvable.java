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
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public interface PBVISolvable {

	/*
	 * Implementing class should perform backup operation of alpha vectors at a
	 * given belief point
	 */
	public Tuple<Integer, DD> backup(final DD b, final List<DD> alphas, final ReachabilityGraph g);
}
