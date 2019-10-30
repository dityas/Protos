package thinclab.policyhelper;

import java.util.*;
import javax.swing.JFrame;
import java.awt.Dimension;

import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import thinclab.ddinterface.DDMaker;
import thinclab.ddinterface.DDTree;
import thinclab.decisionprocesses.POMDP;

public class PolicyGraph {
	
	private HashMap<Integer, PolicyNode> policyNodeHashMap = new HashMap<Integer, PolicyNode>();
	private HashMap<Integer, Map<List<String>, Integer>> successorMap = 
			new HashMap<Integer, Map<List<String>, Integer>>();
	private DirectedOrderedSparseMultigraph<PolicyNode, PolicyEdge> policyGraph = 
			new DirectedOrderedSparseMultigraph<PolicyNode, PolicyEdge>();
	public DirectedOrderedSparseMultigraph<PolicyNode, PolicyEdge> prettyPolicyGraph = 
			new DirectedOrderedSparseMultigraph<PolicyNode, PolicyEdge>();
	
	// -----------------------------------------------------------------------------------------------
	
	public PolicyGraph(List<PolicyNode> policyNodes) {
		/*
		 * Make policy graph from the list nodes given by PolicyExtractor
		 */
		
		// Populate nodeHashMap
		Iterator<PolicyNode> nodeIter = policyNodes.iterator();
		
		while(nodeIter.hasNext()) {
			PolicyNode nodeCurr = nodeIter.next(); 

			this.policyNodeHashMap.put(new Integer(nodeCurr.alphaId), nodeCurr);
			
			// compress observations for pretty graph
//			nodeCurr.compressNextNodes();
			
			// Add nextNode IDs to succerorMap
			successorMap.put(new Integer(nodeCurr.alphaId), nodeCurr.nextNode);
		} // while(nodeIter.hasNext())
		
		// populate the graph
		nodeIter = policyNodes.iterator();
		while(nodeIter.hasNext()) {
			PolicyNode nodeCurr = nodeIter.next();
			// Add edges for current policy node to policy graph
			Iterator<Map.Entry<List<String>, Integer>> nextNodeIter = nodeCurr.nextNode.entrySet().iterator();
			while(nextNodeIter.hasNext()) {
				Map.Entry<List<String>, Integer> nextNodeEntry = nextNodeIter.next();
				policyGraph.addEdge(
						new PolicyEdge(nodeCurr.alphaId, 
									   nextNodeEntry.getKey(),
									   nextNodeEntry.getValue()),
						this.policyNodeHashMap.get(nodeCurr.alphaId),
						this.policyNodeHashMap.get(nextNodeEntry.getValue()),
						EdgeType.DIRECTED);
				
			} // while(nextNodeIter.hasNext())
			
			// Do the same for prettyPolicy Graph
			Iterator<Map.Entry<List<String>, Integer>> prettyNextNodeIter = 
					nodeCurr.compressedNextNode.entrySet().iterator();
			
			while(prettyNextNodeIter.hasNext()) {
				Map.Entry<List<String>, Integer> nextNodeEntry = prettyNextNodeIter.next();
				prettyPolicyGraph.addEdge(
						new PolicyEdge(nodeCurr.alphaId, 
									   nextNodeEntry.getKey(),
									   nextNodeEntry.getValue()),
						this.policyNodeHashMap.get(nodeCurr.alphaId),
						this.policyNodeHashMap.get(nextNodeEntry.getValue()),
						EdgeType.DIRECTED);
				
			} // while(prettyNextNodeIter.hasNext())
			
		} // while(nodeIter.hasNext())
	} // public PolicyGraph(List<PolicyNode> policyNodes)
	
	public PolicyGraph(PolicyTree policyTree) {
		/*
		 * Make policy graph from the list nodes given by PolicyTree
		 */
		
		/*
		 * Populate nodeHashMap
		 */
//		policyTree.indexNodes(0);
		for (PolicyNode node : policyTree.policyNodes) {
			this.policyNodeHashMap.put(node.id, node);
		}

		/*
		 *  populate the tree
		 */
		for (PolicyNode nodeCurr : policyTree.policyNodes) {

			/*
			 * Add edges for current policy node to policy graph
			 */
			Iterator<Map.Entry<List<String>, Integer>> nextNodeIter = 
					nodeCurr.nextNode.entrySet().iterator();
			while(nextNodeIter.hasNext()) {
				Map.Entry<List<String>, Integer> nextNodeEntry = nextNodeIter.next();
				policyGraph.addEdge(
						new PolicyEdge(nodeCurr.id, 
									   nextNodeEntry.getKey(),
									   nextNodeEntry.getValue()),
						this.policyNodeHashMap.get(nodeCurr.id),
						this.policyNodeHashMap.get(nextNodeEntry.getValue()),
						EdgeType.DIRECTED);
				
			} // while(nextNodeIter.hasNext())
			
		} // while(nodeIter.hasNext())
		
	} // public PolicyGraph(List<PolicyNode> policyNodes)
	
	// ----------------------------------------------------------------------------------------------------
	
	public void printPrettyPolicyGraph() {
		System.out.println("POLICY GRAPH: ");
		System.out.println(this.policyGraph);
	} // public void printPolicyGraph()
	
	public void visualizePolicyGraph() {
		VisualizationImageServer vs = 
				new VisualizationImageServer(new CircleLayout(this.policyGraph),
											 new Dimension(500, 500));
			 
	    JFrame frame = new JFrame();
	    frame.getContentPane().add(vs);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.pack();
	    frame.setVisible(true);
	} // public void visualizePolicyGraph()
	
	public DirectedOrderedSparseMultigraph<PolicyNode, PolicyEdge> getGraph() {
		/*
		 * Gets the JUNG defined graph
		 */
		return this.policyGraph;
	} // public DirectedOrderedSparseMultigraph<PolicyNode, PolicyEdge> getGraph()
	
	public String[][] getGraphAsDDPaths() {
		/*
		 * Gets policy graph as triples
		 */
		List<String[]> graphTriples = new ArrayList<String[]>();
		Iterator<PolicyEdge> edgeIter = this.policyGraph.getEdges().iterator();
		
		while (edgeIter.hasNext()) {
			PolicyEdge policyEdge = edgeIter.next();
			List<String> tripleList = new ArrayList<String>();
			
			/* Trying to mark init nodes here */

			tripleList.add("node-" 
					+ policyEdge.from + "-"
					+ this.policyNodeHashMap.get(policyEdge.from).actName);

			tripleList.addAll(policyEdge.observation);
			tripleList.add("node-" 
					+ policyEdge.to + "-"
					+ this.policyNodeHashMap.get(policyEdge.to).actName);
			tripleList.add("1.0");
			
			graphTriples.add(tripleList.toArray(new String[tripleList.size()]));
			
		}
		
		return graphTriples.toArray(new String[graphTriples.size()][]);
	} // public String[][] getGraphAsDDPaths
	
	public String[] getGraphNodeVarVals() {
		/*
		 * Gets variable values for the graph nodes
		 */
		List<String> vals = new ArrayList<String>();
		Iterator<PolicyNode> nodeIter = this.policyGraph.getVertices().iterator();
		while (nodeIter.hasNext()) {
			PolicyNode node = nodeIter.next();
			vals.add("node-" + node.alphaId + "-" + node.actName);

		}
		
		return vals.toArray(new String[vals.size()]);
	} // public String[] getGraphNodeVarVals
	
	public DDTree getGraphAsDD(POMDP p) {
		/*
		 * Gets Policy DD
		 */
		DDMaker ddmaker = new DDMaker();
		List<String> obsNames = new ArrayList<String>();
		obsNames.add("OPP_POLICY");

		for (int i=0; i < p.obsVars.length; i++) {

			ddmaker.addVariable("OPP_" + p.obsVars[i].name, p.obsVars[i].valNames);
			obsNames.add("OPP_" + p.obsVars[i].name);
		}
		
		obsNames.add("OPP_POLICY'");
		
		ddmaker.addVariable("OPP_POLICY", this.getGraphNodeVarVals());
		ddmaker.primeVariables();
		
		DDTree policyDD = ddmaker.getDDTreeFromSequence(
				obsNames.toArray(new String[obsNames.size()]),
				this.getGraphAsDDPaths());
		
		return policyDD;
	} // public DDTree getGraphAsDD
	
	public HashMap<Integer, PolicyNode> getNodeHashMap() {
		/*
		 * Returns the policy node hash map.
		 * Use full for finding init nodes in the policy for higher level domains.
		 */
		return this.policyNodeHashMap;
	}
	
}
