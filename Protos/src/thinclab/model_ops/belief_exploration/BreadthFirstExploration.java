/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DDleaf;
import thinclab.models.POSeqDecMakingModel;
import thinclab.models.datastructures.AbstractAOGraph;
import thinclab.policy.Policy;

/*
 * @author adityas
 *
 */
public class BreadthFirstExploration<B, M extends POSeqDecMakingModel<B>, G extends AbstractAOGraph<B, Integer, List<Integer>>, P extends Policy<B>>
		implements ExplorationStrategy<B, M, G, P> {

	private final int maxB;
	private boolean done = false;

	private static final Logger LOGGER = LogManager.getLogger(BreadthFirstExploration.class);

	public BreadthFirstExploration(int maxCachedB) {

		this.maxB = maxCachedB;
	}

	@Override
	public G expand(List<B> bs, G g, M m, int T, P Vn) {

		bs.forEach(g::addNode);
		
		if (g.getAllNodes().size() < maxB && !done) {

			while (T > 0) {

				g.getAllChildren().stream().forEach(b ->
					{

						g.edgeIndexMap.keySet().stream().forEach(_t ->
							{

								if (g.getAllNodes().size() < maxB) {

									if (g.getNodeAtEdge(b, _t) == null) {
										
										var b_n = m.beliefUpdate(b, _t._0(), _t._1());
										
										if (!b_n.equals(DDleaf.getDD(0.0f)))
											g.addEdge(b, _t, b_n);
									}
								}

							});
					});
				
				T--;
			}
		}

		done = true;
		return g;
	}
}
