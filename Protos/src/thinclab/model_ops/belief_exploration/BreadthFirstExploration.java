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
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.models.POSeqDecMakingModel;
import thinclab.models.datastructures.AbstractAOGraph;
import thinclab.policy.Policy;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;

/*
 * @author adityas
 *
 */
public class BreadthFirstExploration<M extends POSeqDecMakingModel<DD>, G extends AbstractAOGraph<DD, Integer, List<Integer>>, P extends Policy<DD>>
		implements ExplorationStrategy<DD, M, G, P> {

	private final int maxB;
	private boolean done = false;

	private static final Logger LOGGER = LogManager.getLogger(BreadthFirstExploration.class);

	public BreadthFirstExploration(int maxCachedB) {

		this.maxB = maxCachedB;
	}

	@Override
	public void expand(List<DD> bs, G g, M m, int T, P Vn) {

		bs.forEach(g::addNode);

		if (g.getAllNodes().size() < maxB && !done) {

			while (T > 0) {

				if (g.getAllNodes().size() > maxB)
					break;

				LOGGER.debug(String.format("Expanding belief region from %s beliefs", g.getAllChildren().size()));

				for (var b : g.getAllChildren()) {

					if (g.getAllNodes().size() >= maxB)
						break;

					var _triples = g.edgeIndexMap.keySet().parallelStream().filter(_t -> g.getNodeAtEdge(b, _t) == null)
							.map(_t ->
								{

									var b_n = m.beliefUpdate(b, _t._0(), _t._1());
									return Tuple.of(b, _t, b_n);

								})
							.filter(_t -> !_t._2().equals(DD.zero)).collect(Collectors.toList());

					for (var triple : _triples) {

						if (g.getAllNodes().size() < maxB)
							g.addEdge(triple._0(), triple._1(), triple._2());
					}
				}

				// ).filter(t -> !t._2().equals(DD.zero)).collect(Collectors.toList());

				T--;
			}
		}

		// return g;
	}

	@Override
	public int getMaxB() {

		return maxB;
	}
}
