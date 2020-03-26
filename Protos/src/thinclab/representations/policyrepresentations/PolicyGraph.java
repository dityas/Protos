/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations.policyrepresentations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.legacy.DD;
import thinclab.representations.PersistentStructuredTree;
import thinclab.solvers.AlphaVectorPolicySolver;

/*
 * @author adityas
 *
 */
public class PolicyGraph extends PersistentStructuredTree {

	/*
	 * Makes a policy graph with actions as nodes from an alpha vector policy
	 */

	private static final long serialVersionUID = 2533632217752542090L;

	/* Solver reference */
	public AlphaVectorPolicySolver solver;

	/* policy vars */
	public DD[] alphas;
	public int[] actions;
	
	public double MEU = Double.NEGATIVE_INFINITY;
	public int maxT;

	private static final Logger LOGGER = Logger.getLogger(PolicyGraph.class);

	// ------------------------------------------------------------------------------------

	public PolicyGraph(AlphaVectorPolicySolver solver, int maxT) {

		/* set solver reference */
		this.solver = solver;

		/* set policy attributes */
		this.alphas = this.solver.getAlphaVectors();
		this.actions = this.solver.getPolicy();
		
		this.maxT = maxT;

		LOGGER.info("Initializing policy graph for " + this.alphas.length + " A vectors");
	}
	
	public void computeEU() {
		/*
		 * Computes the expected utility for the graph representing the policy
		 */
		this.MEU = 
				this.solver.getFramework().evaluatePolicy(
						this.alphas, 
						this.actions, 
						10000, 
						this.solver.expansionStrategy.getHBound(), 
						false);
	}

	public void makeGraph() {
		/*
		 * Constructs the policy graph from alpha vectors
		 */
		
		/* create a list of leaf nodes */
		List<Integer> leafNodes = new ArrayList<Integer>();
		
		/* Start with initial beliefs */
		DecisionProcess DP = this.solver.getFramework();
		
		for (DD startBelief : DP.getInitialBeliefs()) {
			
			PolicyNode node = new PolicyNode();
			node.setBelief(startBelief);
			node.setId(this.currentPolicyNodeCounter++);
			node.alphaId = DecisionProcess.getBestAlphaIndex(DP, startBelief, this.alphas);
			node.setActName(DP.getActions().get(this.actions[node.alphaId]));
			node.setStartNode();
			
			this.putPolicyNode(node.getId(), node);
			
			leafNodes.add(node.getId());
		}
		
		this.makeGraph(leafNodes);
	}
	
	public void makeGraph(List<Integer> leafNodes) {
		/*
		 * Makes policy graph from the leafNodes given
		 */
		
		for (int t = 0; t < this.maxT; t++) {
			leafNodes = this.expandPolicyGraph(leafNodes, this.solver, t);
		}
	}
	
	// ---------------------------------------------------------------------------------------
	
	@Override
	public String getJSONString() {
		
		Gson gsonHandler = 
				new GsonBuilder()
					.disableHtmlEscaping()
					.setPrettyPrinting()
					.create();
		
		String jsonString = super.getJSONString();
		
		JsonObject policyGraphJSON = JsonParser.parseString(jsonString).getAsJsonObject();
		policyGraphJSON.add("discounted reward", new JsonPrimitive(this.MEU));
		
		return gsonHandler.toJson(policyGraphJSON);
	}

	@Override
	public String getDotString() {
		/*
		 * Converts to graphviz compatible dot string
		 */
		String endl = "\r\n";
		String dotString = "digraph G{ " + endl;
		
		dotString += "graph [ranksep=1];" + endl;
		
		/* Make nodes */
		for (int nodeId : this.getAllNodeIds()) {
			
			PolicyNode node = this.getPolicyNode(nodeId);
			
			if (node.isStartNode())
				dotString += " " + nodeId + " [shape=Mrecord, label=\"{";
			else
				dotString += " " + nodeId + " [shape=record, label=\"{";
			
			dotString += "Ai=" + node.getActName()
					+ "}\"];" + endl;
		}
		
		/* write MEU */
		dotString += -1 
				+ " [shape=record, label=\"{Expected Utility=" + this.MEU + "}\"];" + endl;
		
		dotString += endl;
		
		for (int edgeSrcs : this.getAllEdgeIds()) {
			
			String from = Integer.valueOf(edgeSrcs).toString();
			
			for (Entry<List<String>, Integer> ends : this.getEdges(edgeSrcs).entrySet()) {
				
				dotString += " " + from + " -> " + ends.getValue()
					+ " [label=\"" + ends.getKey().toString() + "\"]" + endl;
			}
		}
		
		dotString += "}" + endl;
		
		return dotString;
	}
	
	@Override
	public String getDotStringForPersistent() {
		/*
		 * Converts to graphviz compatible dot string
		 */
		String endl = "\r\n";
		String dotString = "digraph G{ " + endl;
		
		dotString += "graph [ranksep=1];" + endl;
		
		/* Make nodes */
		for (int id : this.getAllNodeIds()) {
			
			PolicyNode node = this.getPolicyNode(id);
			
			if (node.isStartNode())
				dotString += " " + id + " [shape=Mrecord, label=\"";
			else
				dotString += " " + id + " [shape=record, label=\"";
			
			dotString += node.getActName()
					+ "\"];" + endl;
		}
		
		/* write MEU */
		dotString += -1 
				+ " [shape=record, label=\"{Avg. discounted reward=" + this.MEU + "}\"];" + endl;
		
		dotString += endl;
		
		for (int edgeSource: this.getAllEdgeIds()) {
			
			for (Entry<List<String>, Integer> ends : this.getEdges(edgeSource).entrySet()) {
				
				dotString += " " + edgeSource + " -> " + ends.getValue()
					+ " [label=\"" + ends.getKey().subList(1, ends.getKey().size()).toString() 
					+ "\"]" + endl;
			}
		}
		
		dotString += "}" + endl;
		
		return dotString;
	}
}
