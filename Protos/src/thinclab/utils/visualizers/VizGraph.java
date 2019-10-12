/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils.visualizers;

import java.util.HashMap;
import java.util.List;

import cern.colt.Arrays;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import thinclab.policyhelper.PolicyEdge;
import thinclab.policyhelper.PolicyGraph;
import thinclab.policyhelper.PolicyNode;

/*
 * @author adityas
 *
 */
public class VizGraph {
	/*
	 * A generic graph wrapper for visualizing directed graphs
	 */
	
	public DirectedOrderedSparseMultigraph<VizNode, VizEdge> graph = 
			new DirectedOrderedSparseMultigraph<VizNode, VizEdge>();
	
	// --------------------------------------------------------------------------------------------
	
	public void addDirectedEdge(VizNode from, VizEdge edge, VizNode to) {
		/*
		 * Just calls the graphs addEdge
		 */
		this.graph.addEdge(edge, from, to, EdgeType.DIRECTED);
	}
	
	// --------------------------------------------------------------------------------------------
	
	public static VizGraph getVizGraphFromLATreeTriples(List<String[]> triples) {
		/*
		 * Constructs a VizGraph from LookAheadTree triples
		 */
		
		/* maintain a temporary index for nodes */
		HashMap<String, VizNode> nodeIndex = new HashMap<String, VizNode>();
		
		/* Create empty graph */
		VizGraph vizGraph = new VizGraph();
		
		int nodeCounter = 0;
		int edgeCounter = 0;
		
		for (String[] triple : triples) {
			
			/* create from VizNode */
			if (!nodeIndex.containsKey(triple[0])) {
				nodeIndex.put(triple[0], new VizNode(nodeCounter, triple[0]));
				nodeCounter++;
			}
			
			VizNode fromNode = nodeIndex.get(triple[0]);
			
			/* create to node */
			if (!nodeIndex.containsKey(triple[2])) {
				nodeIndex.put(triple[2], new VizNode(nodeCounter, triple[2]));
				nodeCounter++;
			}
			
			VizNode toNode = nodeIndex.get(triple[2]);
			
			/* create edge */
			VizEdge edge = new VizEdge(fromNode.nodeId, triple[1], toNode.nodeId);
			
			/* add to the graph */
			vizGraph.addDirectedEdge(fromNode, edge, toNode);
		}
		
		return vizGraph;
	}
	
	public static VizGraph getVizGraphFromPolicyGraph(PolicyGraph pg) {
		/*
		 * Converts a policy graph into a vizgraph for better visualization
		 */
		
		/* maintain a temporary index for nodes */
		HashMap<String, VizNode> nodeIndex = new HashMap<String, VizNode>();
		
		/* PolicyGraph's own nodeIndex */
		HashMap<Integer, PolicyNode> nodeMap = pg.getNodeHashMap();
		
		VizGraph graph = new VizGraph();
		int nodeCounter = 0;
		
		for (PolicyEdge edge : pg.getGraph().getEdges()) {
			
			PolicyNode toPNode = nodeMap.get(edge.from);
			
			String toLabel = toPNode.factoredBelief.toString().replace(",", "<br>");
			
			/* create from VizNode */
			if (!nodeIndex.containsKey(toLabel)) {
				nodeIndex.put(toLabel, new VizNode(nodeCounter, toLabel));
				nodeCounter++;
			}
			
			VizNode toNode = nodeIndex.get(toLabel);
					
			PolicyNode fromPNode = nodeMap.get(edge.from);
			String fromLabel = fromPNode.factoredBelief.toString().replace(",", "<br>");
			/* create from VizNode */
			if (!nodeIndex.containsKey(fromLabel)) {
				nodeIndex.put(fromLabel, new VizNode(nodeCounter, fromLabel));
				nodeCounter++;
			}
			
			VizNode fromNode = nodeIndex.get(fromLabel);
			VizEdge vEdge = new VizEdge(fromNode.nodeId, edge.observation.toString(), toNode.nodeId);
			
			graph.addDirectedEdge(fromNode, vEdge, toNode);
		}
		
		return graph;
	}

}
