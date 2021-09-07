/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.ModelGraph;
import thinclab.models.datastructures.ReachabilityNode;
import thinclab.policy.AlphaVectorPolicy;

/*
 * @author adityas
 *
 */
public class PolicyGraphExpansion<N extends ReachabilityNode, M extends PBVISolvablePOMDPBasedModel, P extends AlphaVectorPolicy>
		implements ModelGraphExpansionStrategy<N, M, P> {

	private static final Logger LOGGER = LogManager.getLogger(PolicyGraphExpansion.class);

	public HashMap<Integer, ReachabilityNode> expandReachabilityNode(N node, M m, P p,
			ModelGraph<N, Integer, List<Integer>> g) {

		var nextNodes = new HashMap<Integer, ReachabilityNode>(10);

		// Expand for each belief in the node
		node.beliefs.forEach(b ->
			{

				int bestAct = p.getBestActionIndex(b, m.i_S());

				// expand for each possible observation
				g.edgeIndexMap.entrySet().stream().filter(e -> e.getKey()._0() == bestAct).forEach(e ->
					{

						var b_next = m.beliefUpdate(b, bestAct, e.getKey()._1());

						// if belief is valid, add it to the set of unexplored beliefs in next nodes
						if (!b_next.equals(DDleaf.getDD(Float.NaN))) {

							ReachabilityNode _node = null;

							if (nextNodes.containsKey(e.getValue()))
								_node = nextNodes.get(e.getValue());

							else
								_node = new ReachabilityNode(DDOP.bestAlphaIndex(p.aVecs, b_next, m.i_S()));

							_node.beliefs.add(b_next);
							nextNodes.put(e.getValue(), _node);
						}

					});

			});

		return nextNodes;
	}

	public HashMap<Integer, ReachabilityNode> mergePaths(HashMap<Integer, ReachabilityNode> a,
			HashMap<Integer, ReachabilityNode> b) {

		return null;
	}

	@Override
	public ModelGraph<N, Integer, List<Integer>> expand(List<N> startNodes, ModelGraph<N, Integer, List<Integer>> G,
			M m, P p) {

		startNodes.stream().forEach(n ->
			{

				var nextNodes = expandReachabilityNode(n, m, p, G);
			});
		LOGGER.debug(String.format("Next nodes are %s", nextNodes));

		return null;
	}

}
