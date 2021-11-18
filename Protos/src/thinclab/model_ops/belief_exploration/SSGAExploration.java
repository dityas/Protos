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
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.Global;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.models.POSeqDecMakingModel;
import thinclab.models.datastructures.AbstractAOGraph;
import thinclab.policy.Policy;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class SSGAExploration<M extends POSeqDecMakingModel<DD>, G extends AbstractAOGraph<DD, Integer, List<Integer>>, P extends Policy<DD>>
		implements ExplorationStrategy<DD, M, G, P> {

	private final float e;
	private final int maxB;

	private static final Logger LOGGER = LogManager.getLogger(SSGAExploration.class);

	public SSGAExploration(float explorationProb) {

		this.e = explorationProb;
		this.maxB = 100;
		LOGGER.debug(String.format("Initialized SSGA exploration for exploration probability %s", e));
	}

	@Override
	public G expand(List<DD> bs, G g, M m, int T, P Vn) {

		float Pa = 1 - e;

		if (Pa > 1 || Pa < 0) {

			LOGGER.error(String.format(
					"Exploration prob for SSGA exploration is %s which makes greedy action selection prob %s", e,
					(1 - e)));
			System.exit(-1);
		}

		int startSize = g.getAllNodes().size();

		for (int n = 0; n < 100; n++) {

			for (DD b : bs) {

				for (int i = 0; i < T; i++) {

					if ((g.getAllNodes().size() - startSize) >= 20 || g.getAllNodes().size() >= maxB)
						break;

					var usePolicy = DDOP.sampleIndex(List.of(e, Pa));
					int a = -1;

					// greedy action
					if (usePolicy == 1) {

						a = Vn.getBestActionIndex(b, m.i_S());
						var oSampled = DDOP.sample(List.of(m.obsLikelihoods(b, a)), m.i_Om_p());

						var _edge = Tuple.of(a, oSampled._1());
						var b_ = g.getNodeAtEdge(b, _edge);

						if (b_ == null) {

							var b_n = m.beliefUpdate(b, _edge._0(), _edge._1());
							g.addEdge(b, _edge, b_n);
							b = b_n;
						}

						else
							b = b_;

					}

					// exploratory action
					else if (usePolicy == 0) {

						// a = Global.random.nextInt(m.A().size());

						var nextBs = new ArrayList<Tuple<Tuple<Integer, List<Integer>>, DD>>();
						for (int _a = 0; _a < m.A().size(); _a++) {

							var oSampled = DDOP.sample(List.of(m.obsLikelihoods(b, _a)), m.i_Om_p());

							var _edge = Tuple.of(_a, oSampled._1());
							var b_ = g.getNodeAtEdge(b, _edge);

							if (b_ == null) {

								var b_n = m.beliefUpdate(b, _edge._0(), _edge._1());
								nextBs.add(Tuple.of(_edge, b_n));
							}
							
							else
								nextBs.add(Tuple.of(_edge, b_));

						}

						var distances = nextBs.stream()
								.map(b_s -> g.getAllNodes().stream()
										.map(b_c -> DDOP.maxAll(DDOP.abs(DDOP.sub(b_s._1(), b_c))))
										.reduce(0.0f, (x, y) -> x > y ? x : y))
								.collect(Collectors.toList());

						int maxIndex = IntStream.range(0, nextBs.size()).reduce(0,
								(p, q) -> distances.get(p) > distances.get(q) ? p : q);

						var b_n = nextBs.get(maxIndex);
						g.addEdge(b, b_n._0(), b_n._1());
						b = b_n._1();
					}

					else {

						LOGGER.error("Error while sampling exploration probability");
						System.exit(-1);
					}

				}
			}
		}

		return g;
	}

}
