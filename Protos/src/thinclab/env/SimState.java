/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.env;

import java.util.List;
import thinclab.Agent;
import thinclab.legacy.DD;
import thinclab.utils.Graphable;
import thinclab.utils.Jsonable;

/*
 * @author adityas
 *
 */
public class SimState implements Jsonable, Graphable {
	
	public final int t;
	public final DD s;
	public final List<Agent> agents;
	
	public SimState(int t, DD s, List<Agent> agents) {

		this.t = t;
		this.s = s;
		this.agents = agents;
	}

	@Override
	public String toDot() {

		var builder = new StringBuilder();
		builder.append("subgraph cluster_").append(this.hashCode()).append(" {\r\n");
		builder.append("\t node [shape=record];\r\n");
		builder.append("\t label=\" Time step: ").append(t).append("\";\r\n");
		
		builder.append("\t ").append(s.hashCode()).append(t);
		builder.append(" [label=\"{Sim State | ").append(s.toLabel()).append("}\"];\r\n");
		
		for (var a : agents)
			builder.append("\t ").append(a.toDot()).append("\r\n");
		
		builder.append("}\r\n");
		
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