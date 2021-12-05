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
		implements PointBasedSolver<M, ReachabilityGraph, AlphaVectorPolicy> {

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
			
			//LOGGER.debug(String.format("B: %s", B.stream().map(b -> DDOP.factors(b, m.i_S())).collect(Collectors.toList())));
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

			B = B.stream().filter(_b -> DDOP.value_b(Vn.aVecs, _b, m.i_S()) > DDOP.value_b(newVn, _b, m.i_S()))
					.collect(Collectors.toList());
			
			this.usedBeliefs++;
		}

		return new AlphaVectorPolicy(newVn);
	}

	@Override
	public AlphaVectorPolicy solve(final List<DD> b_is, final M m, int I, int H, AlphaVectorPolicy Vn) {

		var g = ReachabilityGraph.fromDecMakingModel(m);
	
		var ES = new SSGAExploration<M, ReachabilityGraph, AlphaVectorPolicy>(0.1f);
		var b_i = new ArrayList<>(b_is);
				
		b_is.forEach(g::addNode);
		
		LOGGER.info(String.format("Starting symbolic Perseus iterations from starting beliefs %s",
				b_is.stream().map(_b -> DDOP.factors(_b, m.i_S())).collect(Collectors.toList())));
		for (int i = 0; i < I; i++) {

			// expand belief region
			if (i % 2 == 0) {
				
				//if (g.getAllNodes().size() >= ES.maxB) {
				//	g.getAllChildren().forEach(g::removeNode);
					//b_is.forEach(g::addNode);
				//}
					
				g = ES.expand(b_i, g, m, H, Vn);
			}
			
			var B = new ArrayList<DD>(g.getAllNodes());
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
			
			if (bellmanError != 0.0f && bellmanError < 0.01 && i > 5) {
								
				LOGGER.info(String.format("Declaring solution at Bellman error %s and iteration %s", bellmanError, i));
				LOGGER.info("Convergence, software version 7.0, looking at life through the eyes of a tired heart.");
				LOGGER.info("Eating seeds as a past time activity, the toxicity of my city of my city.");
				break;
			}

		}

		g.removeAllNodes();
		m.clearBackupCache();
		System.gc();

		return Vn;
	}

}
