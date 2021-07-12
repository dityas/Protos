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
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
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

	// Internal variables used during backup operations
	// private List<Integer> _OvarsList;
	// private List<List<Integer>> _ao;

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
			final List<List<Integer>> o, final List<DD> aPrimes) {

		/*
		 * For a given \alpha(S) vector, compute
		 * 
		 * g_{ao}i = \Sum_{S'} P(O'|S', a) P(S'|S, a) \alpha(S') for each a in A
		 */

		var SPVars = Arrays.stream(m.Svars).map(s -> s + (Global.NUM_VARS / 2)).toArray();

		DD[] factors = new DD[m.Svars.length + m.Ovars.length + 1];
		List<Tuple<Float, DD>> Gaoi = new ArrayList<>(o.size());

		for (int obs = 0; obs < o.size(); obs++) {

			List<Tuple<Float, DD>> _Gaoi = new ArrayList<>(aPrimes.size());

			for (int i = 0; i < aPrimes.size(); i++) {

				factors[0] = aPrimes.get(i);

				System.arraycopy(m.TF[actIndex], 0, factors, 1, m.Svars.length);
				System.arraycopy(OP.restrict(m.OF[actIndex], OPVars, o.get(obs)), 0, factors, 1 + m.Svars.length,
						m.Ovars.length);

				DD gaoi = OP.mult(DDleaf.getDD(m.discount), OP.addMultVarElim(factors, SPVars));
				_Gaoi.add(new Tuple<Float, DD>(OP.dotProduct(b, gaoi, m.Svars), gaoi));
			}

			Gaoi.add(_Gaoi.stream().reduce((a1, a2) -> a1.first() > a2.first() ? a1 : a2)
					.orElseGet(() -> new Tuple<Float, DD>(Float.NaN, DDleaf.getDD(Float.NaN))));
		}

		return Gaoi.stream().reduce((t1, t2) -> new Tuple<>(t1.first() + t2.first(), OP.add(t1.second(), t2.second())))
				.orElseGet(() -> new Tuple<>(Float.NaN, DDleaf.getDD(Float.NaN)));
	}

	protected Tuple<Float, DD> Gab(final POMDP m, final DD b, List<DD> aPrimes) {

		// Compute indexes for primed observation variables to do perform addouts later
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

		var gab = IntStream.range(0, m.A.size()).boxed().map(i -> new Tuple<>(Ga.get(i).first() + Gao.get(i).first(),
				OP.add(Ga.get(i).second(), Gao.get(i).second())));
		
		return gab.reduce((a1, a2) -> a1.first() >= a2.first() ? a1 : a2).orElseGet(() -> new Tuple<>(Float.NaN, DDleaf.getDD(Float.NaN)));
	}

	protected void backup(final POMDP m, final DD b, List<DD> aVectors) {

		// compute \alpha'(S)
		LOGGER.debug(String.format("Belief is %s", b));
		var aPrimes = aVectors.stream().map(a -> OP.primeVars(a, (Global.NUM_VARS / 2))).collect(Collectors.toList());
		var newAlpha = this.Gab(m, b, aPrimes);
		LOGGER.debug(String.format("new alpha is %s", newAlpha));
	}

	protected void solveForBeliefs(final POMDP m, final List<List<Integer>> ao, List<DD> beliefRegion,
			final List<DD> prevAVecs) {

		while (beliefRegion.size() > 0) {

			DD b = beliefRegion.remove(Global.random.nextInt(beliefRegion.size()));
			backup(m, b, prevAVecs);
			//backup(m, b, prevAVecs);
			break;
		}

	}

	@Override
	public AlphaVectorPolicy solve(POMDP m, BeliefUpdate<POMDP> BU, ReachabilityGraph RG,
			ExplorationStrategy<POMDP> ES) {

		// expand reachability graph to generate beliefs
		RG = ES.expandRG(m, BU, RG);

		// generate A x O values to compute \Gamma_{a, o}. Contrary to the usual
		// cartesian product of values, here I used indices to pass to OP.restrict
		var aoIndices = new ArrayList<List<Integer>>(m.Ovars.length + 1);
		aoIndices.add(IntStream.range(0, m.A.size()).boxed().collect(Collectors.toList()));
		aoIndices.addAll(Arrays.stream(m.Ovars).mapToObj(i -> IntStream.range(0, Global.valNames.get(i - 1).size())
				.mapToObj(o -> o + 1).collect(Collectors.toList())).collect(Collectors.toList()));

		var ao = OP.cartesianProd(aoIndices); // .stream().map(p -> p.stream().toArray()).collect(Collectors.toList());

		// get first set of alpha vectors
		var alphas = AlphaVectorPolicy.fromR(m.R).aVecs.stream().map(a -> a.second()).collect(Collectors.toList());

		// perform backups
		this.solveForBeliefs(m, ao, new ArrayList<DD>(RG.getAllNodes()), alphas);

		// TODO Auto-generated method stub
		return null;
	}
}
