/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.env;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import thinclab.DDOP;
import thinclab.agents.Agent;
import thinclab.legacy.DD;
import thinclab.utils.Graphable;
import thinclab.utils.Jsonable;

/*
 * @author adityas
 *
 */
public class SimState implements Jsonable, Graphable {
	
	public final int t;
	public final JsonElement s;
	public final List<JsonObject> agents;
	
	private static final Logger LOGGER = LogManager.getLogger(SimState.class);
	
	public SimState(int t, DD s, List<Integer> i_S, List<Agent> agents) {

		this.t = t;
		this.s = DDOP.toJson(s, i_S);
		this.agents = agents.stream().map(a -> a.toJson()).collect(Collectors.toList());
	}
	
	@Override
	public String toString() {
		
		return new GsonBuilder().setPrettyPrinting().create().toJson(toJson());
	}

	@Override
	public String toDot() {

		var builder = new StringBuilder();
		builder.append("subgraph cluster_").append(this.hashCode()).append(" {\r\n");
		builder.append("\t node [shape=record];\r\n");
		builder.append("\t label=\" Time step: ").append(t).append("\";\r\n");
		
		builder.append("\t ").append(s.hashCode()).append(t);
		builder.append(" [label=\"{Sim State | ").append(s).append("}\"];\r\n");
		
		builder.append("}\r\n");
		
		return builder.toString();
	}

	@Override
	public JsonObject toJson() {

		var gson = new GsonBuilder().setPrettyPrinting().create();
		var json = new JsonObject();
		
		json.add("time step", gson.toJsonTree(t));
		json.add("state", s);
		
		var agentsJson = new JsonArray();
		agents.forEach(a -> agentsJson.add(a));
		
		json.add("agents", agentsJson);
		
		return json;
	}

	@Override
	public String toLabel() {

		// TODO Auto-generated method stub
		return null;
	}

}
