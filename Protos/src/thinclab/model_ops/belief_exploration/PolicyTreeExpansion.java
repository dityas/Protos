/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import java.util.ArrayList;
import java.util.List;
import thinclab.DDOP;
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
public class PolicyTreeExpansion<M extends PBVISolvablePOMDPBasedModel, P extends AlphaVectorPolicy>
		implements ModelGraphExpansionStrategy<ReachabilityNode, M, P> {

	@Override
	public ModelGraph<ReachabilityNode> expand(List<ReachabilityNode> startNodes, ModelGraph<ReachabilityNode> G, M m,
			int T, P p) {

		var newEdges = new ArrayList<Tuple3<ReachabilityNode, Tuple<Integer, List<Integer>>, ReachabilityNode>>();
		var edges = G.edgeIndexMap.entrySet();
	
		// populate optimal action and alphaId
		startNodes.forEach(n -> {
			var _b = n.beliefs.stream().findFirst().get();
			int i_a = p.getBestActionIndex(_b);
			int alphaId = DDOP.bestAlphaIndex(p, _b);
			
			n.i_a = i_a;
			n.alphaId = alphaId;
		});
		
		startNodes.stream().forEach(G::addNode);

		for (int i = 0; i < T; i++) {
			
			final int t = i;
			newEdges.clear();

			G.getAllChildren().stream().forEach(n ->
				{

					n.beliefs.stream().forEach(b ->
						{

							int bestAct = p.getBestActionIndex(b);

							edges.stream().filter(k -> k.getKey()._0() == bestAct).forEach(k ->
								{

									var b_next = m.beliefUpdate(b, bestAct, k.getKey()._1());

									// if belief is valid, add it to the set of unexplored beliefs in next nodes
									if (!b_next.equals(DDleaf.getDD(0.0f))) {

										int bestAlpha = DDOP.bestAlphaIndex(p, b_next);
										ReachabilityNode _node = new ReachabilityNode(bestAlpha,
												p.get(bestAlpha).getActId());

										_node.h = t + 1;

										_node.beliefs.add(b_next);
										newEdges.add(Tuple.of(n, k.getKey(), _node));

										// G.addEdge(n, k.getKey(), _node);
									}

								});
						});

				});

			for (var e : newEdges)
				G.addEdge(e._0(), e._1(), e._2());
		}

		return G;
	}

}
