/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.ModelGraph;
import thinclab.models.datastructures.ReachabilityNode;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;

/*
 * @author adityas
 *
 */
public class PolicyGraphExpansion<M extends PBVISolvablePOMDPBasedModel, P extends AlphaVectorPolicy>
		implements ModelGraphExpansionStrategy<ReachabilityNode, M, P> {

	private int initT = 0;

	private static final Logger LOGGER = LogManager.getLogger(PolicyGraphExpansion.class);

	public PolicyGraphExpansion(int startT) {

		this.initT = startT;
	}

	public PolicyGraphExpansion() {

	}

	public ModelGraph<ReachabilityNode> expandLevel(List<ReachabilityNode> nodes, M m, P p, ModelGraph<ReachabilityNode> g, int h) {
		
		var nextNodes = new ReachabilityNode[p.size()];
		var newEdges = new ArrayList<Tuple3<ReachabilityNode, Tuple<Integer, List<Integer>>, Integer>>();
		
		// for each node in the level
		nodes.forEach(n -> {
			
			// for each belief
			n.beliefs.forEach(b -> {
				
				var bestAct = p.getBestActionIndex(b);
				
				// for each edge with best action
				g.edgeIndexMap.entrySet().stream().filter(e -> e.getKey()._0() == bestAct).forEach(e -> {
					
					var b_n = m.beliefUpdate(b, bestAct, e.getKey()._1());
					
					if (!b_n.equals(DD.zero)) {
						
						int bestAlpha = DDOP.bestAlphaIndex(p, b_n);
						
						var _node = nextNodes[bestAlpha];
						
						if (_node == null) {
							_node = new ReachabilityNode(bestAlpha, bestAct);
							_node.h = h;
							
							_node.beliefs.add(b_n);
						}
						
						//_node.beliefs.add(b_n);
						nextNodes[bestAlpha] = _node;
						
						newEdges.add(Tuple.of(n, e.getKey(), bestAlpha));
					}
				});
				
			});
		});
		
		newEdges.forEach(e -> {
			g.addEdge(e._0(), e._1(), nextNodes[e._2()]);
		});
		
		return g;
	}

	public HashMap<Integer, ReachabilityNode> expandReachabilityNode(ReachabilityNode node, M m, P p,
			ModelGraph<ReachabilityNode> g, int h) {

		var nextNodes = new HashMap<Integer, ReachabilityNode>(10);

		// Expand for each belief in the node
		node.beliefs.forEach(b ->
			{

				int bestAct = p.getBestActionIndex(b);

				// expand for each possible observation
				g.edgeIndexMap.entrySet().stream().filter(e -> e.getKey()._0() == bestAct).forEach(e ->
					{

						var b_next = m.beliefUpdate(b, bestAct, e.getKey()._1());

						// if belief is valid, add it to the set of unexplored beliefs in next nodes
						if (!b_next.equals(DDleaf.getDD(Float.NaN))) {

							ReachabilityNode _node = null;
							int bestAlpha = DDOP.bestAlphaIndex(p, b_next);

							var __node = nextNodes.values().stream().filter(n -> n.alphaId == bestAlpha).findFirst();

							if (__node.isPresent())
								_node = __node.get();

							else if (nextNodes.containsKey(e.getValue()))
								_node = nextNodes.get(e.getValue());

							else {

								_node = new ReachabilityNode(bestAlpha, p.get(bestAlpha).getActId());
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
	public ModelGraph<ReachabilityNode> expand(List<ReachabilityNode> startNodes, ModelGraph<ReachabilityNode> G, M m,
			int T, P p) {

		startNodes.stream().forEach(G::addNode);
		int i = this.initT;

		while (i < T) {

			int t = i + 1;
			var _sNodes = G.getAllChildren();
			LOGGER.debug(String.format("Expanding from %s nodes containing %s beliefs in total", _sNodes.size(),
					_sNodes.stream().map(__n -> __n.beliefs.size()).reduce(0, (a, b) -> a + b)));
			
			G = expandLevel(new ArrayList<>(_sNodes), m, p, G, t);

			/*
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
				}); */
			i++;
		}

		
		var _sNodes = G.getAllNodes();
		LOGGER.debug(String.format("Model graph contains %s nodes containing %s beliefs in total", 
				_sNodes.size(), _sNodes.stream().map(__n -> __n.beliefs.size()).reduce(0, (a, b) -> a + b)));

		return G;
	}

}
