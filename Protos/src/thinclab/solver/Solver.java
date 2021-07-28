/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solver;

import thinclab.models.Model;
import thinclab.policy.Policy;

/*
 * @author adityas
 *
 */
public interface Solver<M extends Model, P extends Policy> {

	public P solve(M m);
}
