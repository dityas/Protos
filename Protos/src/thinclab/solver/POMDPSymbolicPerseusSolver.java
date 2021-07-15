/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.model_ops.belief_exploration.ExplorationStrategy;
import thinclab.model_ops.belief_update.BeliefUpdate;
import thinclab.models.POMDP;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class POMDPSymbolicPerseusSolver implements PBVIBasedSolver<POMDP> {

	public final int backups;

	private int usedBeliefs = 0;

	// Internal variables used during backup operations
	private int[] SPVars = null;

	// -------------------------------------------------------------------------------------------------------

	private static final Logger LOGGER = LogManager.getLogger(POMDPSymbolicPerseusSolver.class);

	public POMDPSymbolicPerseusSolver(int backups) {

		this.backups = backups;
		LOGGER.info(String.format("Initialized %s for %s backups", POMDPSymbolicPerseusSolver.class, this.backups));
	}

	public POMDPSymbolicPerseusSolver() {

		this.backups = 10;
		LOGGER.info(String.format("Initialized %s for %s backups", POMDPSymbolicPerseusSolver.class, this.backups));
	}

	// ---------------------------------------------------------------------------------------------------------

	protected Tuple<Float, DD> Gaoi(POMDP m, final DD b, final int actIndex, final List<Integer> OPVars,
			final List<List<Integer>> o, List<DD> aPrimes) {

		/*
		 * For a given \alpha(S) vector, compute
		 * 
		 * g_{ao}i = \Sum_{S'} P(O'|S', a) P(S'|S, a) \alpha(S') for each a in A
		 */


		DD[] factors = new DD[m.Svars.length + m.Ovars.length + 1];
		List<Tuple<Float, DD>> Gaoi = new ArrayList<>(o.size());

		for (int obs = 0; obs < o.size(); obs++) {

			List<Tuple<Float, DD>> _Gaoi = new ArrayList<>(aPrimes.size());

			for (int i = 0; i < aPrimes.size(); i++) {

				factors[0] = aPrimes.get(i);

				System.arraycopy(m.TF[actIndex], 0, factors, 1, m.Svars.length);
				System.arraycopy(OP.restrict(m.OF[actIndex], OPVars, o.get(obs)), 0, factors, 1 + m.Svars.length,
						m.Ovars.length);

				DD gaoi = OP.mult(DDleaf.getDD(m.discount), OP.addMultVarElim(factors, this.SPVars));
				_Gaoi.add(new Tuple<Float, DD>(OP.dotProduct(b, gaoi, m.Svars), gaoi));
			}

			Gaoi.add(_Gaoi.stream().reduce((a1, a2) -> a1.first() > a2.first() ? a1 : a2)
					.orElseGet(() -> new Tuple<Float, DD>(Float.NaN, DDleaf.getDD(Float.NaN))));
		}

		return Gaoi.stream().reduce((t1, t2) -> new Tuple<>(t1.first() + t2.first(), OP.add(t1.second(), t2.second())))
				.orElseGet(() -> new Tuple<>(Float.NaN, DDleaf.getDD(Float.NaN)));
	}

	protected Tuple<Integer, DD> Gab(final POMDP m, final DD b, List<DD> aPrimes) {

		// Compute indexes for primed observation variables to perform addouts later
		var OPVars = Arrays.stream(m.Ovars).boxed().map(o -> o + (Global.NUM_VARS / 2)).collect(Collectors.toList());
		var oList = OP.cartesianProd(Arrays.stream(m.Ovars).mapToObj(
				o -> IntStream.range(1, Global.valNames.get(o - 1).size() + 1).boxed().collect(Collectors.toList()))
				.collect(Collectors.toList()));

		var actionStream = IntStream.range(0, m.A.size());
		if (aPrimes.size() > 2 && (m.Svars.length + m.Ovars.length) > 5)
			actionStream = actionStream.parallel();

		// For each action a and each observation o and each \alpha(s'), compute the dot
		// product of Gao with b, argmax the best \alpha, and sum observations
		var Gao = actionStream.mapToObj(_a -> this.Gaoi(m, b, _a, OPVars, oList, aPrimes)).collect(Collectors.toList());
		var Ga = Arrays.stream(m.R).map(r -> new Tuple<>(OP.dotProduct(b, r, m.Svars), r)).collect(Collectors.toList());

		// compute best Ga + Gao and construct \alpha vector
		int bestA = -1;
		float bestVal = Float.NEGATIVE_INFINITY;
		DD bestAlpha = DDleaf.getDD(Float.NaN);

		for (int i = 0; i < m.A.size(); i++) {

			var val = Ga.get(i).first() + Gao.get(i).first();
			if (val > bestVal) {

				bestA = i;
				bestVal = val;
				bestAlpha = OP.add(Ga.get(i).second(), Gao.get(i).second());
			}
		}

		return new Tuple<>(bestA, bestAlpha);
	}

	protected Tuple<Integer, DD> backup(final POMDP m, final DD b, List<DD> aVectors) {

		// compute \alpha'(S)
		var aPrimes = aVectors.stream().map(a -> OP.primeVars(a, (Global.NUM_VARS / 2))).collect(Collectors.toList());
		return this.Gab(m, b, aPrimes);
	}

	protected List<Tuple<Integer, DD>> solveForBeliefs(final POMDP m, List<DD> beliefRegion,
			List<Tuple<Integer, DD>> prevAVecs) {

		List<Tuple<Integer, DD>> newVn = new ArrayList<>(10);
		this.usedBeliefs = 0;

		while (beliefRegion.size() > 0) {

			DD b = beliefRegion.remove(Global.random.nextInt(beliefRegion.size()));
			var newAlpha = backup(m, b, prevAVecs.stream().map(a -> a.second()).collect(Collectors.toList()));

			// Construct V_{n+1}(b)
			var Vnb = prevAVecs.stream()
					.map(a -> new Tuple<Float, Tuple<Integer, DD>>(OP.dotProduct(b, a.second(), m.Svars), a))
					.reduce((v1, v2) -> v1.first() >= v2.first() ? v1 : v2)
					.orElseGet(() -> new Tuple<>(Float.NaN, new Tuple<>(-1, DDleaf.getDD(Float.NaN))));

			var newAlphab = OP.dotProduct(b, newAlpha.second(), m.Svars);

			// If new \alpha.b > Vn(b) add it to new V
			if (newAlphab > Vnb.first())
				newVn.add(newAlpha);

			else
				newVn.add(Vnb.second());

			beliefRegion = beliefRegion.stream()
					.filter(_b -> OP.value_b(prevAVecs, _b, m.Svars) > OP.value_b(newVn, _b, m.Svars))
					.collect(Collectors.toList());

			this.usedBeliefs++;
		}

		return newVn;
	}

	@Override
	public AlphaVectorPolicy solve(POMDP m, BeliefUpdate<POMDP> BU, ReachabilityGraph RG,
			ExplorationStrategy<POMDP> ES) {
		
		// Prepare primed var indices and all that to avoid wasting time on it during backups
		this.SPVars = Arrays.stream(m.Svars).map(s -> s + (Global.NUM_VARS / 2)).toArray();

		List<Tuple<Integer, DD>> Vn = AlphaVectorPolicy.fromR(m.R).aVecs;

		for (int i = 0; i < this.backups; i++) {

			// expand reachability graph to grenerate new belief points
			//if (i % 10 == 0)
			RG = ES.expandRG(m, BU, RG);
			
			var beliefRegion = new ArrayList<DD>(RG.getAllNodes());
			
			long then = System.nanoTime();

			// perform backups
			var newVn = this.solveForBeliefs(m, new ArrayList<DD>(beliefRegion), Vn);
			
			float backupT = (System.nanoTime() - then) / 1000000.0f;
			float bellmanError = beliefRegion.stream()
					.map(_b -> Math.abs(OP.value_b(Vn, _b, m.Svars) - OP.value_b(newVn, _b, m.Svars)))
					.reduce((v1, v2) -> v1 > v2 ? v1 : v2).orElseGet(() -> Float.NaN);

			// prepare for next iter
			Vn.clear();
			Vn.addAll(newVn);

			LOGGER.info(String.format("iter: %s | bell err: %.5f | time: %.3f msec | num vectors: %s | beliefs used: %s/%s", i,
					bellmanError, backupT, newVn.size(), this.usedBeliefs, beliefRegion.size()));
			
			if (bellmanError < 0.001 && i > 5) {
				
				LOGGER.info(String.format("Declaring solution at Bellman error %s and iteration %s", bellmanError, i));
				break;
			}
		}

		// TODO Auto-generated method stub
		return new AlphaVectorPolicy(Vn);
	}
}
