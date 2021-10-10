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
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.ModelGraph;
import thinclab.models.datastructures.ReachabilityNode;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;
import thinclab.DDOP;
import thinclab.legacy.DDleaf;

/*
 * @author adityas
 *
 */
public class MjSpaceExpansion<M extends PBVISolvablePOMDPBasedModel, P extends AlphaVectorPolicy>
		implements ModelGraphExpansionStrategy<ReachabilityNode, M, P> {

	/*
	 * For building models in Mj in IPOMDPs, instead of building a tree of
	 * individual belief points, it might be better to build the first step as
	 * breadth first and then expand using PolicyGraphExpansion for the rest.
	 */

	private PolicyGraphExpansion<M, P> expansion = new PolicyGraphExpansion<>();

	@Override
	public ModelGraph<ReachabilityNode> expand(List<ReachabilityNode> startNodes,
			ModelGraph<ReachabilityNode> G, M m, int T, P p) {

		var newEdges = new ArrayList<Tuple3<ReachabilityNode, Tuple<Integer, List<Integer>>, ReachabilityNode>>();
		var edges = G.edgeIndexMap.entrySet();
		
		startNodes.stream().forEach(n ->
			{

				n.beliefs.stream().forEach(b ->
					{

						int bestAct = p.getBestActionIndex(b, m.i_S());

						edges.stream().filter(k -> k.getKey()._0() == bestAct).forEach(k ->
							{

								var b_next = m.beliefUpdate(b, bestAct, k.getKey()._1());

								// if belief is valid, add it to the set of unexplored beliefs in next nodes
								if (!b_next.equals(DDleaf.getDD(Float.NaN))) {

									int bestAlpha = DDOP.bestAlphaIndex(p.aVecs, b_next, m.i_S());
									ReachabilityNode _node = new ReachabilityNode(bestAlpha,
											p.aVecs.get(bestAlpha)._0());
									
									_node.h = 1;

									_node.beliefs.add(b_next);
									newEdges.add(Tuple.of(n, k.getKey(), _node));

									// G.addEdge(n, k.getKey(), _node);
								}

							});
					});

			});
	
		for (var e : newEdges)
			G.addEdge(e._0(), e._1(), e._2());

		T = T - 1;

		G = expansion.expand(new ArrayList<>(G.getAllChildren()), G, m, T, p);

		return G;
	}

}
