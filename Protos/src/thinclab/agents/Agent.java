/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.agents;

import java.util.List;
import java.util.stream.Collectors;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.PointBasedSolver;
import thinclab.utils.Graphable;
import thinclab.utils.Jsonable;

/*
 * @author adityas
 *
 */
public abstract class Agent implements Jsonable, Graphable {

	public PBVISolvablePOMDPBasedModel m;
	public DD b;
	public AlphaVectorPolicy Vn;
	public int optA;
	public PointBasedSolver<PBVISolvablePOMDPBasedModel, AlphaVectorPolicy> solver;
	public int backups;
	public int H;

	public abstract Agent step(List<Integer> obs);

	@Override
	public String toString() {

		return new GsonBuilder().setPrettyPrinting().create().toJson(toJson());
	}

	@Override
	public String toDot() {

		return new GsonBuilder().setPrettyPrinting().create().toJson(toJson());
	}

	@Override
	public JsonObject toJson() {

		var gson = new GsonBuilder().setPrettyPrinting().create();

		var json = new JsonObject();
		json.add("name", gson.toJsonTree(m.getName()));
		json.add("belief", gson.toJsonTree(DDOP.toJson(b, m.i_S())));

		if (m instanceof IPOMDP) {

			var _m = (IPOMDP) m;

			json.add("opponent frame",
					DDOP.toJson(DDOP.getFrameBelief(b, _m.PThetajGivenEC, _m.i_EC, _m.i_S()), _m.i_Thetaj));

			var PAj = DDOP.addMultVarElim(List.of(_m.PAjGivenEC, b), _m.i_S());
			json.add("opponent action", DDOP.toJson(PAj, _m.i_Aj));
		}

		json.add("optA", gson.toJsonTree(m.A().get(optA)));

		return json;
	}

	@Override
	public String toLabel() {

		return null;
	}
}
