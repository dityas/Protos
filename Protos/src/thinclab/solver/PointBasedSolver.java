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
import thinclab.model_ops.belief_exploration.ExplorationStrategy;
import thinclab.models.POSeqDecMakingModel;
import thinclab.models.datastructures.AbstractAOGraph;
import thinclab.policy.Policy;

/*
 * @author adityas
 *
 */
public interface PointBasedSolver<M extends POSeqDecMakingModel<DD>, G extends AbstractAOGraph<DD, ?, ?>, P extends Policy<DD>> {

	public P solve(List<DD> bs, final M m, int I, int H, ExplorationStrategy<DD, M, G, P> ES, P Vn);
}
