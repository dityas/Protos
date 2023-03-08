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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
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
public class BreadthFirstMjExpansion <M extends PBVISolvablePOMDPBasedModel, P extends AlphaVectorPolicy>
		implements ModelGraphExpansionStrategy<ReachabilityNode, M, P> {
	
	private static final Logger LOGGER = LogManager.getLogger(BreadthFirstMjExpansion.class);

	@Override
	public ModelGraph<ReachabilityNode> expand(List<ReachabilityNode> startNodes, ModelGraph<ReachabilityNode> G, M m,
			int T, P p) {

		
		startNodes.stream().forEach(n -> {
			
			var _b = n.beliefs.stream().findFirst().get();
			if(!DDOP.verifyJointProbabilityDist(_b, m.i_S())) {
				
				LOGGER.error(String.format("%s is not a valid probability distribution", DDOP.factors(_b, m.i_S())));
				System.exit(-1);
			}
		});
		
		var newEdges = new ArrayList<Tuple3<ReachabilityNode, Tuple<Integer, List<Integer>>, ReachabilityNode>>();
		var edges = G.edgeIndexMap.entrySet();
		
		startNodes.stream().forEach(n ->
			{

				n.beliefs.stream().forEach(b ->
					{

						int bestAct = p.getBestActionIndex(b);

						edges.stream().filter(k -> k.getKey()._0() == bestAct).forEach(k ->
							{

								var b_next = m.beliefUpdate(b, bestAct, k.getKey()._1());

								// if belief is valid, add it to the set of unexplored beliefs in next nodes
								if (!b_next.equals(DD.zero)) {

									int bestAlpha = DDOP.bestAlphaIndex(p, b_next);
									ReachabilityNode _node = new ReachabilityNode(bestAlpha,
											p.get(bestAlpha).getActId());
									
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
		
		return G;
	}

}
