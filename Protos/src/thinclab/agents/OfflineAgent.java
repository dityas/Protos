/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.agents;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.PolicyGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.PointBasedSolver;

/*
 * @author adityas
 *
 */
public class OfflineAgent extends Agent {

	private static final Logger LOGGER = LogManager.getLogger(OfflineAgent.class);
	
	public OfflineAgent(PBVISolvablePOMDPBasedModel m, DD b,
			PointBasedSolver<PBVISolvablePOMDPBasedModel, AlphaVectorPolicy> solver, int backups, int H) {

		LOGGER.info(String.format("[*] Constructing agent wrapper for model %s", m.getName()));

		this.m = m;
		this.b = b;
		this.backups = backups;
		this.solver = solver;
		this.H = H;

		this.Vn = this.solver.solve(List.of(this.b), this.m, this.backups, this.H, AlphaVectorPolicy.fromR(this.m.R()));
		this.optA = this.Vn.getBestActionIndex(this.b, this.m.i_S());

		LOGGER.info(String.format("Initialized agent for model %s", this.m.getName()));
		LOGGER.info(String.format("Policy Graph is %s", PolicyGraph.makePolicyGraph(List.of(this.b), this.m, this.Vn)));

	}
	
	public OfflineAgent(
			PBVISolvablePOMDPBasedModel m,
			DD b,
			AlphaVectorPolicy Vn, int H) {

		this.m = m;
		this.b = b;
		this.H = H;
		
		this.Vn = Vn;
		this.optA = this.Vn.getBestActionIndex(this.b, this.m.i_S());
		
	}

	@Override
	public Agent step(List<Integer> obs) {

		return new OfflineAgent(m, this.m.beliefUpdate(b, optA, obs), Vn, H);
	}

}
