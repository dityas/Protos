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
import thinclab.DDOP;
import thinclab.legacy.Global;
import thinclab.legacy.DD;
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
	public G expand(G g, M m, int T, P Vn) {

		float Pa = 1 - e;

		if (Pa > 1 || Pa < 0) {

			LOGGER.error(String.format(
					"Exploration prob for SSGA exploration is %s which makes greedy action selection prob %s", e,
					(1 - e)));
			System.exit(-1);
		}

		for (int n = 0; n < 30; n++) {

			DD b = m.b_i();
			for (int i = 0; i < T; i++) {

				if (g.getAllNodes().size() >= maxB)
					break;

				var usePolicy = DDOP.sampleIndex(List.of(e, Pa));
				int a = -1;

				if (usePolicy == 1)
					a = Vn.getBestActionIndex(b, m.i_S());

				else if (usePolicy == 0)
					a = Global.random.nextInt(m.A().size());

				else {

					LOGGER.error("Error while sampling exploration probability");
					System.exit(-1);
				}
				
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
		}

		return g;
	}

}
