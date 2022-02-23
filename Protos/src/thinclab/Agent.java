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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.IPOMDP.IPOMDP;
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
		return Agent.of(A.m, b_n, A.solver, A.backups, A.H);
	}

	@Override
	public String toString() {

		return new GsonBuilder().setPrettyPrinting().create().toJson(toJson());
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
	public JsonObject toJson() {

		var gson = new GsonBuilder().setPrettyPrinting().create();
		
		var json = new JsonObject();
		json.add("name", gson.toJsonTree(m.getName()));
		json.add("belief", gson.toJsonTree(DDOP.toJson(b, m.i_S())));
		
		var builder = new StringBuilder();
		builder.append(" { ").append(" \"name\" : \"").append(m.getName()).append("\" , ");
		builder.append(" \"belief\" : ").append(DDOP.toJson(b, m.i_S())).append(" , ");
		
		if (m instanceof IPOMDP) {
			var _m = (IPOMDP) m;
			builder.append(" \"opponent frame\" : ")
				.append(DDOP.toJson(DDOP.getFrameBelief(b, _m.PThetajGivenEC, _m.i_Mj, _m.i_S()), _m.i_Thetaj)).append(" , ");
			
			var PAj = DDOP.addMultVarElim(List.of(_m.PAjGivenEC, b), _m.i_S());
			builder.append(" \"opponent action\" : ").append(DDOP.toJson(PAj, _m.i_Aj)).append(" , ");
		}
		
		json.add("optA", gson.toJsonTree(m.A().get(optA)));
		
		return json;
	}

	@Override
	public String toLabel() {

		// TODO Auto-generated method stub
		return null;
	}
}
