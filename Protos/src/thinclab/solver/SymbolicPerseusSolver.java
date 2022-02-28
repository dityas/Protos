/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.model_ops.belief_exploration.BreadthFirstExploration;
import thinclab.model_ops.belief_exploration.SSGAExploration;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class SymbolicPerseusSolver<M extends PBVISolvablePOMDPBasedModel>
		implements PointBasedSolver<M, AlphaVectorPolicy> {

	private int usedBeliefs = 0;

	private static final Logger LOGGER = LogManager.getLogger(SymbolicPerseusSolver.class);

	/*
	 * Perform backups and get the next value function for a given explored belief
	 * region
	 */

	protected AlphaVectorPolicy solveForB(final M m, List<DD> B, AlphaVectorPolicy Vn, ReachabilityGraph g) {

		List<Tuple<Integer, DD>> newVn = new ArrayList<>(10);
		this.usedBeliefs = 0;

		while (B.size() > 0) {

			DD b = B.remove(Global.random.nextInt(B.size()));

			var newAlpha = m.backup(b, Vn.aVecs.stream().map(a -> a._1()).collect(Collectors.toList()), g);

			// Construct V_{n+1}(b)
			float bestVal = Float.NEGATIVE_INFINITY;
			int bestA = -1;
			DD bestDD = DD.zero;
			for (int a = 0; a < Vn.aVecs.size(); a++) {

				float val = DDOP.dotProduct(b, Vn.aVecs.get(a)._1(), m.i_S());

				if (val >= bestVal) {

					bestVal = val;
					bestA = Vn.aVecs.get(a)._0();
					bestDD = Vn.aVecs.get(a)._1();
				}
			}

			var newAlphab = DDOP.dotProduct(b, newAlpha._1(), m.i_S());

			// If new \alpha.b > Vn(b) add it to new V
			if (newAlphab > bestVal)
				newVn.add(newAlpha);

			else
				newVn.add(Tuple.of(bestA, bestDD));

			B = B.parallelStream().filter(_b -> DDOP.value_b(Vn.aVecs, _b, m.i_S()) > DDOP.value_b(newVn, _b, m.i_S()))
					.collect(Collectors.toList());

			this.usedBeliefs++;
		}

		return new AlphaVectorPolicy(newVn);
	}

	public float computeBellmanError(List<DD> B, final M m, AlphaVectorPolicy Vn, AlphaVectorPolicy Vn_p) {

		float bellmanError = B.parallelStream()
				.map(_b -> Math.abs(DDOP.value_b(Vn.aVecs, _b, m.i_S()) - DDOP.value_b(Vn_p.aVecs, _b, m.i_S())))
				.max((v1, v2) -> v1.compareTo(v2)).get();

		return bellmanError;
	}

	@Override
	public AlphaVectorPolicy solve(final List<DD> b_is, final M m, int I, int H, AlphaVectorPolicy Vn) {

		if (b_is.size() < 1)
			return Vn;

		LOGGER.info(String.format("[*] Launching symbolic Perseus solver for model %s", m.getName()));
		var g = ReachabilityGraph.fromDecMakingModel(m);

		var ES = new SSGAExploration<M, ReachabilityGraph, AlphaVectorPolicy>(0.1f);
		var b_i = new ArrayList<>(b_is);

		b_i.forEach(g::addNode);

		// initial belief exploration
		var _then = System.nanoTime();

		g = new BreadthFirstExploration<M, ReachabilityGraph, AlphaVectorPolicy>(150).expand(b_i, g, m, 5, Vn);

		LOGGER.info(String.format("Found %s beliefs after first step", g.getAllChildren().size()));

		var _ES = new SSGAExploration<M, ReachabilityGraph, AlphaVectorPolicy>(0.3f);
		g = _ES.expand(b_i, g, m, H - 1, Vn);

		var _now = System.nanoTime();
		LOGGER.info(String.format("Initial belief exploration for %s took %s msecs", m.getName(),
				((_now - _then) / 1000000.0)));

		b_i.stream().forEach(_b ->
			{

				if (!DDOP.verifyJointProbabilityDist(_b, m.i_S())) {

					LOGGER.error(String.format("Belief %s is not a valid probability distribution",
							DDOP.factors(_b, m.i_S())));
					System.exit(-1);
				}
			});

		int convergenceCount = 0;

		for (int i = 0; i < I; i++) {

			var B = new ArrayList<DD>(g.getAllNodes());
			long then = System.nanoTime();

			// new value function after backups
			var Vn_p = solveForB(m, B, Vn, g);

			float backupT = (System.nanoTime() - then) / 1000000.0f;

			long bErrThen = System.nanoTime();
			float bellmanError = computeBellmanError(B, m, Vn, Vn_p);

			if (Global.DEBUG) {

				float bErrT = (System.nanoTime() - bErrThen) / 1000000.0f;
				LOGGER.debug(String.format("Computing bellman error took %s msec", bErrT));
			}

			// prepare for next iter
			Vn.aVecs.clear();
			Vn.aVecs.addAll(Vn_p.aVecs);

			LOGGER.info(
					String.format("iter: %s | bell err: %.5f | num vectors: %s | beliefs used: %s/%s | time: %.3f msec",
							i, bellmanError, Vn_p.aVecs.size(), this.usedBeliefs, B.size(), backupT));

			if (bellmanError < 0.01 && i > 9) {

				convergenceCount += 1;

				if (convergenceCount > 3) {

					LOGGER.info(
							String.format("Declaring solution at Bellman error %s and iteration %s", bellmanError, i));
					LOGGER.info(
							"Convergence, software version 7.0, looking at life through the eyes of a tired heart.");
					LOGGER.info("Eating seeds as a past time activity, the toxicity of my city of my city.");
					break;
				}
			}

			Global.clearHashtablesIfFull();

			if (bellmanError < 0.1f) {

				if (i > 5) {

					g.removeAllNodes();
					b_is.forEach(g::addNode);
				}

				long expandThen = System.nanoTime();

				g = ES.expand(b_is, g, m, H, Vn);

				if (Global.DEBUG) {

					float expandT = (System.nanoTime() - expandThen) / 1000000.0f;
					LOGGER.debug(String.format("Belief expansion took %s msecs", expandT));
				}
			}

		}

		if (Global.DEBUG)
			Global.logCacheSizes();

		g.removeAllNodes();
		Global.clearHashtablesIfFull();
		m.clearBackupCache();
		ES.clearCaches();
		System.gc();

		if (Global.DEBUG)
			Global.logCacheSizes();

		var solnSet = Vn.aVecs.parallelStream().map(a -> m.A().get(a._0())).collect(Collectors.toSet());
		LOGGER.info(String.format("Optimal policy contains actions: %s", solnSet));

		LOGGER.info(String.format("[*] Finished solving %s", m.getName()));

		return Vn;
	}

}
