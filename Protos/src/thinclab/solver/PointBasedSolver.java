/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solver;

import thinclab.legacy.DD;
import thinclab.models.POSeqDecMakingModel;
import thinclab.models.datastructures.AbstractAOGraph;
import thinclab.policy.Policy;

/*
 * @author adityas
 *
 */
public interface PointBasedSolver<M extends POSeqDecMakingModel<DD>, G extends AbstractAOGraph<DD, ?, ?>, P extends Policy<DD>> {

	public P solve(final M m, int I, int H, P Vn);
}
