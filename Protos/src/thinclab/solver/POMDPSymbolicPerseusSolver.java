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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
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

	protected DD[] GammaAOa(POMDP m, DD aPrime) {

		/*
		 * For a given \alpha(S) vector, compute
		 * 
		 * gaoa = Sumout[S'] P(O'|S', a) P(S'|S, a) \alpha(S') for each a in A
		 */
		DD[] factors = new DD[m.Svars.length + m.Ovars.length + 1];
		DD[] GAOa = new DD[m.A.size()];

		for (int a = 0; a < m.A.size(); a++) {

			factors[0] = aPrime;
			System.arraycopy(m.TF[a], 0, factors, 1, m.Svars.length);
			System.arraycopy(m.OF[a], 0, factors, 1 + m.Svars.length, m.Ovars.length);

			GAOa[a] = OP.addMultVarElim(factors, Arrays.stream(m.Svars).map(i -> i + (Global.NUM_VARS / 2)).toArray());
		}

		return GAOa;
	}

	protected List<DD[]> Gao(final POMDP m, final List<List<Integer>> ao, List<DD> aPrimes) {

		var GAO = aPrimes.stream().parallel().map(aP -> this.GammaAOa(m, aP)).collect(Collectors.toList());
		return GAO;
	}

	protected List<DD[]> Gab(final DD b, final List<List<Integer>> ao, final POMDP m, List<DD> aPrimes) {

		var OvarsP = Arrays.stream(m.Ovars).map(o -> o + (Global.NUM_VARS / 2)).toArray();
		var bestGammaAO = this.Gao(m, aPrimes).stream().parallel()
				.map(gao -> IntStream.range(0, gao.length)
						.mapToObj(i -> new Tuple<Integer, Float>(i, OP.dotProduct(b, gao[i], m.Svars)))
						.collect(Collectors.toList()))
				.collect(Collectors.toList());

		LOGGER.debug(String.format("GammaAO is %s", bestGammaAO));

		return null;
	}

	protected void backup(final DD b, final List<List<Integer>> ao, final POMDP m, List<DD> aVectors) {

		// compute \alpha'(S)
		var aPrimes = aVectors.stream().map(a -> OP.primeVars(a, (Global.NUM_VARS / 2))).collect(Collectors.toList());
		var dummy = this.Gab(b, m, aPrimes);
	}

	protected void solveForBeliefs(final POMDP m, final List<List<Integer>> ao, List<DD> beliefRegion,
			final List<DD> prevAVecs) {

		while (beliefRegion.size() > 0) {

			DD b = beliefRegion.remove(Global.random.nextInt(beliefRegion.size()));
			backup(b, ao, m, prevAVecs);
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

		var ao = OP.cartesianProd(aoIndices);

		// get first set of alpha vectors
		var alphas = AlphaVectorPolicy.fromR(m.R).aVecs.stream().map(a -> a.second()).collect(Collectors.toList());

		// perform backups
		this.solveForBeliefs(m, ao, new ArrayList<DD>(RG.getAllNodes()), alphas);

		// TODO Auto-generated method stub
		return null;
	}
}
