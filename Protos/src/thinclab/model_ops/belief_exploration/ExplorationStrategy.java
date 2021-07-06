/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import thinclab.model_ops.belief_update.BeliefUpdate;
import thinclab.models.Model;
import thinclab.models.datastructures.ReachabilityGraph;

/*
 * @author adityas
 *
 */
public interface ExplorationStrategy<M extends Model> {

	public ReachabilityGraph expandRG(M m, BeliefUpdate<M> BE, ReachabilityGraph RG);
}
