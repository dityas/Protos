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
import thinclab.models.POSeqDecMakingModel;
import thinclab.policy.Policy;

/*
 * @author adityas
 *
 */
public interface PointBasedSolver<M extends POSeqDecMakingModel<DD>, P extends Policy> {

	public P solve(List<DD> bs, final M m, int H);
}
