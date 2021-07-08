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
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVectorPolicy;

/*
 * @author adityas
 *
 */
public interface PBVIBasedSolver<M extends Model> {

	public AlphaVectorPolicy solve(M m, BeliefUpdate<M> BU, ReachabilityGraph RG, ExplorationStrategy<M> ES);
}
