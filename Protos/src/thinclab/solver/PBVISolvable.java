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
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public interface PBVISolvable {

	/*
	 * Implementing class should solve \Gamma_{a, o}^i(s). For POMDP backups, this
	 * is
	 * 
	 * g_{a, o}^i(s) = \Sum_{s'} p(o | s', a) p(s' | s, a) \alpha^i(s')
	 */
	public Tuple<Float, DD> Gaoi(final DD b, final int a, List<DD> alphaPrimes);
}
