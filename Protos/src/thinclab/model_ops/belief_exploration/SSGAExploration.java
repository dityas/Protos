/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
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
	public final int maxB;

	private HashMap<Tuple<DD, Integer>, DD> likelihoodsCache = new HashMap<>();

	private static final Logger LOGGER = LogManager.getLogger(SSGAExploration.class);

	public SSGAExploration(float explorationProb) {

		this.e = explorationProb;
		this.maxB = 100;
		LOGGER.debug(String.format("Initialized SSGA exploration for exploration probability %s", e));
	}

	/*
	 * This has to be done here only because java's stream API makes everything 3
	 * times slower. So for low latency operations, it is better to write everything
	 * in traditional loops. If you are at a party and someone tells you how awesome
	 * java is and all that. You should point them to this particular file in this
	 * repository and let them know that you absolutely hate them. Then you should
	 * proceed to throw your beer in their face and leave. And you should never ever
	 * hang out with such people.
	 */
	public float getMinDistance(DD b, Collection<DD> beliefs) {

		// g.getAllNodes().stream().filter(_b -> DDOP.maxAll(DDOP.abs(DDOP.sub(_b,
		// b_n))) < 0.1f).findFirst();

		float minDist = Float.POSITIVE_INFINITY;

		for (var _b : beliefs) {

			float dist = DDOP.maxAll(DDOP.abs(DDOP.sub(_b, b)));
			if (dist < minDist)
				minDist = dist;
		}

		return minDist;
	}

	public boolean isUniqueBelief(DD b, Collection<DD> beliefs, float minDist) {

		if (getMinDistance(b, beliefs) < minDist)
			return true;

		else
			return false;
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

		if (g.getAllNodes().size() >= maxB)
			return g;

		int startSize = g.getAllNodes().size();

		for (int n = 0; n < 100; n++) {

			if (g.getAllNodes().size() >= maxB)
				break;

			for (int init_b = 0; init_b < bs.size(); init_b++) {

				DD b = bs.get(init_b);

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

							if (isUniqueBelief(b_n, g.getAllNodes(), 0.1f))
								g.addEdge(b, _edge, b_n);

							b = b_n;
						}

						else
							b = b_;

					}

					// exploratory action
					else if (usePolicy == 0) {

						int _a = Global.random.nextInt(m.A().size());

						// var nextBs = new ArrayList<Tuple<Tuple<Integer, List<Integer>>, DD>>();
						// for (int _a = 0; _a < m.A().size(); _a++) {

						if (!likelihoodsCache.containsKey(Tuple.of(b, _a)))
							likelihoodsCache.put(Tuple.of(b, _a), m.obsLikelihoods(b, _a));

						var l = likelihoodsCache.get(Tuple.of(b, _a));

						var oSampled = DDOP.sample(List.of(l), m.i_Om_p());
						var _edge = Tuple.of(_a, oSampled._1());
						var b_ = g.getNodeAtEdge(b, _edge);

						if (b_ == null)
							b_ = m.beliefUpdate(b, _edge._0(), _edge._1());

						// else
						// nextBs.add(Tuple.of(_edge, b_));

						// }

						// var distances = new ArrayList<Float>();
						// int maxIndex = -1;
						// float maxDist = Float.NEGATIVE_INFINITY;

						// for (int i_b = 0; i_b < nextBs.size(); i_b++) {

						var dist = getMinDistance(b_, g.getAllNodes());

						// if (dist > maxDist) {

						// maxIndex = i_b;
						// maxDist = dist;
						// }
						// }

						// var distances = nextBs.stream()
						// .map(b_s -> g.getAllNodes().stream()
						// .map(b_c -> DDOP.maxAll(DDOP.abs(DDOP.sub(b_s._1(), b_c))))
						// .reduce(1.0f, (x, y) -> x < y ? x : y))
						// .collect(Collectors.toList());

						// int maxIndex = IntStream.range(0, nextBs.size()).reduce(0,
						// (p, q) -> distances.get(p) > distances.get(q) ? p : q);

						// var maxDist = distances.get(maxIndex);
						// var b_n = nextBs.get(maxIndex);

						// LOGGER.debug(String.format("Randomized Distance is %s", maxDist));
						if (dist > 0.01f)
							g.addEdge(b, _edge, b_);

						b = b_;
					}

					else {

						LOGGER.error("Error while sampling exploration probability");
						System.exit(-1);
					}

				}
			}
		}

		// likelihoodsCache.clear();
		return g;
	}

	public void clearCaches() {

		likelihoodsCache.clear();
	}

}
