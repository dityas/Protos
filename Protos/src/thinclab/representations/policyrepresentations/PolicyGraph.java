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

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.representations.StructuredTree;
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
	
	public double MEU = Double.NEGATIVE_INFINITY;

	private static final Logger LOGGER = Logger.getLogger(PolicyGraph.class);

	// ------------------------------------------------------------------------------------

	public PolicyGraph(OfflinePBVISolver solver) {

		/* set solver reference */
		this.solver = solver;

		/* set policy attributes */
		this.alphas = this.solver.getAlphaVectors();
		this.actions = this.solver.getPolicy();
		
		this.MEU = 
				this.solver.getFramework().evaluatePolicy(
						this.alphas, this.actions, 10000, this.solver.expansionStrategy.getHBound(), false);

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
			node.setBelief(startBelief);
			node.alphaId = DecisionProcess.getBestAlphaIndex(DP, startBelief, this.alphas);
			node.setActName(DP.getActions().get(this.actions[node.alphaId]));
			node.setStartNode();
			
			this.putPolicyNode(node.alphaId, node);
			
			leafNodes.add(node.alphaId);
		}
		
		this.makeGraph(leafNodes);
	}
	
	public void makeGraph(List<Integer> leafNodes) {
		/*
		 * Makes policy graph from the leafNodes given
		 */
		
		DecisionProcess DP = this.solver.getFramework();
		
		/* branch for all possible observations */
		List<List<String>> obs = DP.getAllPossibleObservations();
		
		/* Do till there are no terminal policy leaves */
		while(!leafNodes.isEmpty()) {
			
			PolicyNode node = this.getPolicyNode(leafNodes.remove(0));
			List<Integer> newLeaves = new ArrayList<Integer>();
			
			/*
			 * For all observations, perform belief updates and get best action nodes
			 */
			for (List<String> theObs : obs) {
				
				try {
					
					DD nextBel = 
							DP.beliefUpdate( 
									node.getBelief(), 
									node.getActName(), 
									theObs.stream().toArray(String[]::new));
					
					/* get best next node */
					int alphaId = 
							DecisionProcess.getBestAlphaIndex(DP, nextBel, this.alphas);
					
					if (!this.containsNode(alphaId)) {
						
						PolicyNode nexNode = new PolicyNode();
						
						nexNode.setBelief(nextBel);
						nexNode.alphaId = alphaId;
						nexNode.setActName(DP.getActions().get(this.actions[alphaId]));
						
						this.putPolicyNode(alphaId, nexNode);
						newLeaves.add(alphaId);
					}
					
					this.putEdge(node.alphaId, theObs, alphaId);
					
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
		dotString += (this.getNumNodes() + 2) 
				+ " [shape=Mrecord, label=\"{ Expected Utility=" + this.MEU + "}\"];" + endl;
		
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
}
