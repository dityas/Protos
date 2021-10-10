/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DDleaf;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.ModelGraph;
import thinclab.models.datastructures.ReachabilityNode;
import thinclab.policy.AlphaVectorPolicy;

/*
 * @author adityas
 *
 */
public class PolicyGraphExpansion<M extends PBVISolvablePOMDPBasedModel, P extends AlphaVectorPolicy>
		implements ModelGraphExpansionStrategy<ReachabilityNode, M, P> {

	private static final Logger LOGGER = LogManager.getLogger(PolicyGraphExpansion.class);

	public HashMap<Integer, ReachabilityNode> expandReachabilityNode(ReachabilityNode node, M m, P p,
			ModelGraph<ReachabilityNode> g, int h) {

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
							int bestAlpha = DDOP.bestAlphaIndex(p.aVecs, b_next, m.i_S());

							var __node = nextNodes.values().stream().filter(n -> n.alphaId == bestAlpha).findFirst();

							if (__node.isPresent())
								_node = __node.get();

							else if (nextNodes.containsKey(e.getValue()))
								_node = nextNodes.get(e.getValue());

							else {

								_node = new ReachabilityNode(bestAlpha, p.aVecs.get(bestAlpha)._0());
								_node.h = h;
							}

							_node.beliefs.add(b_next);
							nextNodes.put(e.getValue(), _node);
						}

					});

			});

		// node.beliefs.clear();

		return nextNodes;
	}

	public HashMap<Integer, ReachabilityNode> mergePaths(HashMap<Integer, ReachabilityNode> a,
			HashMap<Integer, ReachabilityNode> b) {

		return null;
	}

	@Override
	public ModelGraph<ReachabilityNode> expand(List<ReachabilityNode> startNodes,
			ModelGraph<ReachabilityNode> G, M m, int T, P p) {

		startNodes.stream().forEach(G::addNode);
		int i = 0;

		while (i < T) {

			int t = i + 1;
			var _sNodes = G.getAllChildren();
			LOGGER.debug(String.format("Expanding from %s nodes containing %s beliefs in total", _sNodes.size(),
					_sNodes.stream().map(__n -> __n.beliefs.size()).reduce(0, (a, b) -> a + b)));

			_sNodes.stream().forEach(n ->
				{

					var nextNodes = expandReachabilityNode(n, m, p, G, t);
					G.edgeIndexMap.entrySet().stream().forEach(e ->
						{

							if (nextNodes.containsKey(e.getValue())) {
								
								// if model node already exists, fetch that, 
								// insert all new beliefs and put it back in
								
								var _nextNode = G.getNodeAtEdge(n, e.getKey());
								
								if (_nextNode != null) {
									
									var newNode = nextNodes.get(e.getValue());
									newNode.beliefs.addAll(_nextNode.beliefs);
									
									G.replaceNode(_nextNode, newNode);
								}
								
								else
									G.addEdge(n, e.getKey(), nextNodes.get(e.getValue()));
							}

						});
				});
			i++;
		}
		
		//G.connections.keySet().forEach(k -> k.beliefs.clear());
		return G;
	}

}
