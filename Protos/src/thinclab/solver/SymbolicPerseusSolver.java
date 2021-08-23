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
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;
import thinclab.model_ops.belief_exploration.ExplorationStrategy;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class SymbolicPerseusSolver<M extends PBVISolvablePOMDPBasedModel>
		implements PointBasedSolver<M, ReachabilityGraph, AlphaVectorPolicy> {

	private int usedBeliefs = 0;

	private static final Logger LOGGER = LogManager.getLogger(SymbolicPerseusSolver.class);

	/*
	 * Perform backups and get the next value function for a given explored belief
	 * region
	 */

	protected AlphaVectorPolicy solveForB(final M m, List<DD> B, AlphaVectorPolicy Vn, final ReachabilityGraph g) {

		List<Tuple<Integer, DD>> newVn = new ArrayList<>(10);
		this.usedBeliefs = 0;

		while (B.size() > 0) {

			DD b = B.remove(Global.random.nextInt(B.size()));
			var newAlpha = m.backup(b, Vn.aVecs.stream().map(a -> a._1()).collect(Collectors.toList()), g);

			// Construct V_{n+1}(b)
			var Vnb = Vn.aVecs.stream().map(a -> Tuple.of(DDOP.dotProduct(b, a._1(), m.i_S()), a._0(), a._1())).reduce(
					Tuple.of(Float.NEGATIVE_INFINITY, -1, DDleaf.zero), (v1, v2) -> v1._0() >= v2._0() ? v1 : v2);

			var newAlphab = DDOP.dotProduct(b, newAlpha._1(), m.i_S());
			// LOGGER.debug(String.format("new Alpha is %s Vnb is %s and new Alphab is %s",
			// newAlpha, Vnb, newAlphab));

			// If new \alpha.b > Vn(b) add it to new V
			if (newAlphab > Vnb._0())
				newVn.add(newAlpha);

			else
				newVn.add(Tuple.of(Vnb._1(), Vnb._2()));

			B = B.stream().filter(_b -> DDOP.value_b(Vn.aVecs, _b, m.i_S()) > DDOP.value_b(newVn, _b, m.i_S()))
					.collect(Collectors.toList());

			this.usedBeliefs++;
		}

		return new AlphaVectorPolicy(newVn);
	}

	@Override
	public AlphaVectorPolicy solve(final M m, int I, int H, ReachabilityGraph g, AlphaVectorPolicy Vn) {

		for (int i = 0; i < I; i++) {

			// expand belief region
			var B = new ArrayList<DD>(g.getParents());
			long then = System.nanoTime();

			// new value function after backups
			var Vn_p = solveForB(m, B, Vn, g);

			float backupT = (System.nanoTime() - then) / 1000000.0f;
			float bellmanError = B.stream()
					.map(_b -> Math.abs(DDOP.value_b(Vn.aVecs, _b, m.i_S()) - DDOP.value_b(Vn_p.aVecs, _b, m.i_S())))
					.reduce(Float.NEGATIVE_INFINITY, (v1, v2) -> v1 > v2 ? v1 : v2);

			// prepare for next iter
			Vn.aVecs.clear();
			Vn.aVecs.addAll(Vn_p.aVecs);

			LOGGER.info(
					String.format("iter: %s | bell err: %.5f | time: %.3f msec | num vectors: %s | beliefs used: %s/%s",
							i, bellmanError, backupT, Vn_p.aVecs.size(), this.usedBeliefs, B.size()));

			if (bellmanError < 0.001 && i > 5) {

				LOGGER.info(String.format("Declaring solution at Bellman error %s and iteration %s", bellmanError, i));
				break;
			}

		}

		return Vn;
	}

}
