/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.policy.Policy;
import thinclab.solver.PointBasedSolver;
import thinclab.utils.Graphable;
import thinclab.utils.Jsonable;

/*
 * @author adityas
 *
 */
public class Agent implements Jsonable, Graphable {

	public final PBVISolvablePOMDPBasedModel m;
	public final DD b;
	public final Policy<DD> Vn;
	public final int optA;
	public final PointBasedSolver<PBVISolvablePOMDPBasedModel, AlphaVectorPolicy> solver;
	public final int backups;
	public final int H;

	private static final Logger LOGGER = LogManager.getLogger(Agent.class);

	public Agent(PBVISolvablePOMDPBasedModel m, DD b,
			PointBasedSolver<PBVISolvablePOMDPBasedModel, AlphaVectorPolicy> solver, int backups, int H) {

		LOGGER.info(String.format("[*] Constructing agent wrapper for model %s", m.getName()));

		this.m = m;
		this.b = b;
		this.backups = backups;
		this.solver = solver;
		this.H = H;

		LOGGER.error(String.format("aVecs have childs %s",
				AlphaVectorPolicy.fromR(this.m.R()).aVecs.stream()
						.map(_a -> _a._1().getVar() == 0 ? String.format("Leaf, %s", this.m.A().get(_a._0())) : String.format("%s, %s", Global.varNames.get(_a._1().getVar() - 1), this.m.A().get(_a._0())))
						.collect(Collectors.toList())));
		
		this.Vn = this.solver.solve(List.of(this.b), this.m, this.backups, this.H, AlphaVectorPolicy.fromR(this.m.R()));
		this.optA = this.Vn.getBestActionIndex(this.b, this.m.i_S());

		LOGGER.info(String.format("Initialized agent for model %s", this.m.getName()));

	}

	public static Agent of(PBVISolvablePOMDPBasedModel m, DD b,
			PointBasedSolver<PBVISolvablePOMDPBasedModel, AlphaVectorPolicy> solver, int backups, int H) {

		return new Agent(m, b, solver, backups, H);
	}

	public static Agent step(Agent A, List<Integer> obs) {

		DD b_n = A.m.step(A.b, A.optA, obs);
		LOGGER.warn(String.format("b %s is %s", b_n.getChildren().length, DDOP.factors(b_n, A.m.i_S())));
		return Agent.of(A.m, b_n, A.solver, A.backups, A.H);
	}

	@Override
	public String toString() {

		return new StringBuilder().append("Agent: [ ").append(m.getName()).append(" ]").toString();
	}

	@Override
	public String toDot() {

		var builder = new StringBuilder();

		builder.append("agent_").append(this.hashCode()).append(" ");
		builder.append(" [label=\"").append(" Name: ").append(this.m.getName()).append(" ");

		var b = String.join(" | ",
				DDOP.factors(this.b, m.i_S()).stream().map(d -> d.toLabel()).collect(Collectors.toList()));
		builder.append(" | ").append("{ belief | ").append(b).append(" }");
		builder.append(" | ").append("{ opt_a(b) | ").append(this.m.A().get(this.optA)).append(" }");
		builder.append("\"];");

		return builder.toString();
	}

	@Override
	public String toJson() {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toLabel() {

		// TODO Auto-generated method stub
		return null;
	}
}
