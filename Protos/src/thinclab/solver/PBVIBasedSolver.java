/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solver;

import thinclab.model_ops.belief_exploration.ExplorationStrategy;
import thinclab.model_ops.belief_update.BeliefUpdate;
import thinclab.models.Model;
import thinclab.models.datastructures.AbstractAOGraph;
import thinclab.policy.Policy;

/*
 * @author adityas
 *
 */
public interface PBVIBasedSolver<M extends Model, G extends AbstractAOGraph<?, ?>, E extends ExplorationStrategy<M>, P extends Policy>
		extends Solver {

	public P solve(M m, BeliefUpdate<M> BU, G RG, E ES);
}
