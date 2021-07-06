/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.model_ops.belief_exploration.POMDPBreadthFirstBeliefExploration;
import thinclab.model_ops.belief_update.BeliefUpdate;
import thinclab.models.POMDP;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVectorPolicy;

/*
 * @author adityas
 *
 */
public class POMDPSymbolicPerseusSolver
		implements PBVIBasedSolver<POMDP, ReachabilityGraph, POMDPBreadthFirstBeliefExploration, AlphaVectorPolicy> {

	private static final Logger LOGGER = LogManager.getLogger(POMDPSymbolicPerseusSolver.class);

	@Override
	public AlphaVectorPolicy solve(POMDP m, BeliefUpdate<POMDP> BU, ReachabilityGraph RG,
			POMDPBreadthFirstBeliefExploration ES) {

		// expand reachability graph to generate beliefs
		RG = ES.expandRG(m, BU, RG);
		
		// get initial policy
		var P = AlphaVectorPolicy.fromR(m.R);
		
		// compute point based values
		var PBVs = OP.do
		
		// TODO Auto-generated method stub
		return null;
	}
}
