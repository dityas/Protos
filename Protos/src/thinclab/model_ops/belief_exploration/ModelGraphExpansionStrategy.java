/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import java.util.List;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.ModelGraph;
import thinclab.models.datastructures.ReachabilityNode;
import thinclab.policy.AlphaVectorPolicy;

/*
 * @author adityas
 *
 */
public interface ModelGraphExpansionStrategy<N extends ReachabilityNode, M extends PBVISolvablePOMDPBasedModel, P extends AlphaVectorPolicy> {

	public ModelGraph<N> expand(List<N> startNodes, ModelGraph<N> G,
			M m, int T, P p);
}
