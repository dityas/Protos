/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import thinclab.belief.Belief;
import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.solvers.OfflinePBVISolver;

/*
 * @author adityas
 *
 */
public class PolicyGraph extends StructuredTree {

	/*
	 * Makes a policy graph with actions as nodes from an alpha vector policy
	 */

	private static final long serialVersionUID = 2533632217752542090L;

	/* Solver reference */
	public OfflinePBVISolver solver;

	/* policy vars */
	public DD[] alphas;
	public int[] actions;

	private static final Logger LOGGER = Logger.getLogger(PolicyGraph.class);

	// ------------------------------------------------------------------------------------

	public PolicyGraph(OfflinePBVISolver solver) {

		/* set solver reference */
		this.solver = solver;

		/* set policy attributes */
		this.alphas = this.solver.getAlphaVectors();
		this.actions = this.solver.getPolicy();

		LOGGER.info("Initializing policy graph for " + this.alphas.length + " A vectors");
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
			node.belief = startBelief;
			node.alphaId = DecisionProcess.getBestAlphaIndex(DP, startBelief, this.alphas);
			node.actName = DP.getActions().get(this.actions[node.alphaId]);
			
			this.idToNodeMap.put(node.alphaId, node);
			
			leafNodes.add(node.alphaId);
		}
		
		/* branch for all possible observations */
		List<List<String>> obs = DP.getAllPossibleObservations();
		
		/* Do till there are no terminal policy leaves */
		while(!leafNodes.isEmpty()) {
			
			PolicyNode node = this.idToNodeMap.get(leafNodes.remove(0));
			List<Integer> newLeaves = new ArrayList<Integer>();
			
			/*
			 * For all observations, perform belief updates and get best action nodes
			 */
			for (List<String> theObs : obs) {
				
				try {
					
					DD nextBel = 
							Belief.beliefUpdate(
									(POMDP) DP, 
									node.belief, 
									DP.getActions().indexOf(node.actName), 
									theObs.toArray(new String[0]));
					
					/* get best next node */
					int alphaId = 
							DecisionProcess.getBestAlphaIndex(DP, nextBel, this.alphas);
					
					if (!this.idToNodeMap.containsKey(alphaId)) {
						
						PolicyNode nexNode = new PolicyNode();
						
						nexNode.belief = nextBel;
						nexNode.alphaId = alphaId;
						nexNode.actName = DP.getActions().get(this.actions[alphaId]);
						
						this.idToNodeMap.put(alphaId, nexNode);
						newLeaves.add(alphaId);
					}
					
					if (!this.edgeMap.containsKey(node.alphaId))
						this.edgeMap.put(
								node.alphaId, 
								new HashMap<List<String>, Integer>());
					
					this.edgeMap.get(node.alphaId).put(theObs, alphaId);
					
				}
				
				catch (ZeroProbabilityObsException e) {
					continue;
				}
			}
			
			leafNodes.addAll(newLeaves);
			
		}
			
	}
	
	// ---------------------------------------------------------------------------------------

	@Override
	public String getDotString() {
		/*
		 * Converts to graphviz compatible dot string
		 */
		String endl = "\r\n";
		String dotString = "digraph G{ " + endl;
		
		dotString += "graph [ranksep=1];" + endl;
		
		/* Make nodes */
		for (Entry<Integer, PolicyNode> entry : this.idToNodeMap.entrySet()) {
			dotString += " " + entry.getKey() + " [shape=record, label=\"{"
					+ "Ai=" + entry.getValue().actName
					+ "}\"];" + endl;
		}
		
		dotString += endl;
		
		for (Entry<Integer, HashMap<List<String>, Integer>> edges : this.edgeMap.entrySet()) {
			
			String from = edges.getKey().toString();
			
			for (Entry<List<String>, Integer> ends : edges.getValue().entrySet()) {
				
				dotString += " " + from + " -> " + ends.getValue()
					+ " [label=\"" + ends.getKey().toString() + "\"]" + endl;
			}
		}
		
		dotString += "}" + endl;
		
		return dotString;
	}
}
