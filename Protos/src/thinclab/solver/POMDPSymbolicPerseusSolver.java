/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solver;

import java.util.Collection;
import java.util.stream.Collectors;
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

	protected Tuple<Integer, DD> backup(DD b) {

		return null;
	}
	
	protected void solverForBeliefs(Collection<DD> beliefRegion) {

		
	}

	@Override
	public AlphaVectorPolicy solve(POMDP m, BeliefUpdate<POMDP> BU, ReachabilityGraph RG,
			ExplorationStrategy<POMDP> ES) {

		// expand reachability graph to generate beliefs
		RG = ES.expandRG(m, BU, RG);

		// get initial policy
		var P = AlphaVectorPolicy.fromR(m.R);

		// compute point based values
		var PBVs = OP.dotProd(RG.getAllNodes(), P.aVecs.stream().map(a -> a.second()).collect(Collectors.toList()),
				m.Svars);

		LOGGER.debug(String.format("PBVs are: %s", PBVs));

		for (int i = 0; i < this.backups; i++) {

			// compute Vprime
			var Vprime = P.aVecs.stream().map(a -> OP.primeVars(a.second(), (Global.NUM_VARS / 2)))
					.collect(Collectors.toList());

		}

		// TODO Auto-generated method stub
		return null;
	}
}
