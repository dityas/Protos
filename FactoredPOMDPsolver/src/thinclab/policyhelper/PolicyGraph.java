package thinclab.policyhelper;

import java.util.*;
import javax.swing.JFrame;
import java.awt.Dimension;

import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.visualization.VisualizationImageServer;

public class PolicyGraph {
	
	private HashMap<Integer, PolicyNode> policyNodeHashMap = new HashMap<Integer, PolicyNode>();
	private HashMap<Integer, Map<List<String>, Integer>> successorMap = new HashMap<Integer, Map<List<String>, Integer>>();
	private DirectedOrderedSparseMultigraph<PolicyNode, PolicyEdge> policyGraph = new DirectedOrderedSparseMultigraph<PolicyNode, PolicyEdge>();
	public DirectedOrderedSparseMultigraph<PolicyNode, PolicyEdge> prettyPolicyGraph = 
			new DirectedOrderedSparseMultigraph<PolicyNode, PolicyEdge>();
	
	public PolicyGraph(List<PolicyNode> policyNodes) {
		/*
		 * Make policy graph from the list nodes given by PolicyExtractor
		 */
		
		// Populate nodeHashMap
		Iterator<PolicyNode> nodeIter = policyNodes.iterator();
		
		while(nodeIter.hasNext()) {
			PolicyNode nodeCurr = nodeIter.next(); 
//			System.out.println(nodeCurr);
			this.policyNodeHashMap.put(new Integer(nodeCurr.alphaId), nodeCurr);
			
			// compress observations for pretty graph
			nodeCurr.compressNextNodes();
			
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
	
	public void printPrettyPolicyGraph() {
		System.out.println("POLICY GRAPH: ");
		System.out.println(this.policyGraph);
	} // public void printPolicyGraph()
	
	public void visualizePolicyGraph() {
		VisualizationImageServer vs = new VisualizationImageServer(new CircleLayout(this.policyGraph),
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
	
}
