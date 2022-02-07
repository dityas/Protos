/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.env;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.Agent;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.utils.Graphable;
import thinclab.utils.Jsonable;

/*
 * @author adityas
 *
 */
public class SimState implements Jsonable, Graphable {
	
	public final int t;
	public final List<DD> s;
	public final List<Agent> agents;
	
	private static final Logger LOGGER = LogManager.getLogger(SimState.class);
	
	public SimState(int t, List<DD> s, List<Agent> agents) {

		this.t = t;
		this.s = s;
		this.agents = agents;
	}
	
	@Override
	public String toString() {
		
		var builder = new StringBuilder();
		
		builder.append("[+] Time step: ").append(t).append("\r\n");
		builder.append("[:] State: ").append("\r\n");
		
		s.forEach(_s -> {
			
			var sVar = _s.getVar();
			
			if (sVar == 0) {
				
				LOGGER.error(String.format("[!] Fatal error! Sim state %s is not valid", s));
				System.exit(-1);
			}
			
			builder.append("\t").append(Global.varNames.get(sVar - 1)).append(" : ").append(_s).append("\r\n");
		});
		
		agents.forEach(a -> {
			
			builder.append(a).append("\r\n");
		});
		
		builder.append("[-] End time step");
		
		return builder.toString();
	}

	@Override
	public String toDot() {

		var builder = new StringBuilder();
		builder.append("subgraph cluster_").append(this.hashCode()).append(" {\r\n");
		builder.append("\t node [shape=record];\r\n");
		builder.append("\t label=\" Time step: ").append(t).append("\";\r\n");
		
		builder.append("\t ").append(s.hashCode()).append(t);
		builder.append(" [label=\"{Sim State | ").append(s).append("}\"];\r\n");
		
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
