/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import java.util.List;

import thinclab.legacy.DD;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.ReachabilityGraph;

/*
 * @author adityas
 *
 */
public interface ExplorationStrategy<M extends PBVISolvablePOMDPBasedModel> {

	public ReachabilityGraph explore(List<DD> bs, M m, int T, int maxI);
}
