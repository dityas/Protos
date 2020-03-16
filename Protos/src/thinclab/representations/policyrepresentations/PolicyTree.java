/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations.policyrepresentations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import thinclab.legacy.DD;
import thinclab.representations.belieftreerepresentations.StaticBeliefTree;
import thinclab.solvers.OfflinePBVISolver;

/*
 * @author adityas
 *
 */
public class PolicyTree extends StaticBeliefTree {

	/*
	 * Constructs a policy tree for a fixed horizon 
	 */
	
	/* policy vars */
	public DD[] alphas;
	public int[] actions;
	
	public double MEU = Double.NEGATIVE_INFINITY;
	
	private static final long serialVersionUID = -3134912845660452376L;
	private static final Logger LOGGER = Logger.getLogger(PolicyTree.class);
	
	// -------------------------------------------------------------------------------------
	
	public PolicyTree(OfflinePBVISolver solver, int maxH) {
		
		super(solver, maxH);
		
		/* set policy attributes */
		this.alphas = solver.getAlphaVectors();
		this.actions = solver.getPolicy();
	}
	
	// -------------------------------------------------------------------------------------
	
	public void computeEU() {
		/*
		 * Computes the expected utility for the graph representing the policy
		 */
		this.MEU = 
				this.solver.f.evaluatePolicy(
						this.alphas, 
						this.actions, 
						10000, 
						((OfflinePBVISolver) this.solver).expansionStrategy.getHBound(), 
						false);
	}
	
	@Override
	public List<Integer> getNextPolicyNodes(List<Integer> previousNodes, int T) {
		/*
		 * Compute the next PolicyNode from the list of previous PolicyNodes
		 */
		
		HashMap<DD, Integer> nodeMap = new HashMap<DD, Integer>();
		
		/* For each previous Node */
		for (int parentId : previousNodes) {
			
			/* For all combinations */
			for (List<String> obs : this.observations) {
				
				DD belief = this.getPolicyNode(parentId).getBelief();
				String action = this.solver.getActionForBelief(belief);
				
				this.makeNextBeliefNode(
						parentId, 
						belief, f, action, this.solver, obs, nodeMap, null, T);
			} /* for all observations */
		} /* for all parents */
		
		return new ArrayList<Integer>(nodeMap.values());
	}
	
	public void buildTree() {
		/*
		 * Builds the full OnlinePolicyTree upto maxT
		 */
		
		List<Integer> prevNodes = new ArrayList<Integer>();
		
		for (int i = 0; i < this.f.getInitialBeliefs().size(); i++) {
			prevNodes.add(i);
			
			PolicyNode node = new PolicyNode();
			node.setId(i);
			node.setBelief(this.f.getInitialBeliefs().get(i));
			node.setH(0);
			node.setsBelief(this.f.toMap(node.getBelief()).toString());
			
			/* record start node */
			node.setStartNode();
			
			if (this.solver != null)
				node.setActName(this.solver.getActionForBelief(node.getBelief()));
			
			else 
				node.setActName("");
				
			this.putPolicyNode(i, node);
			
			this.currentPolicyNodeCounter += 1;
		}
		
		for (int t = 1; t < this.maxT + 1; t++) {
			
			List<Integer> nextNodes = this.getNextPolicyNodes(prevNodes, t);
			prevNodes = nextNodes;
		}
		
		/* reset node counter */
		this.currentPolicyNodeCounter = 0;
		
		/* strip beliefs from the policy nodes */
		this.stripBeliefInfo();
		this.mergeCommonSubTrees();
	}
	
	private void stripBeliefInfo() {
		/*
		 * Removes the information about beliefs from the policy nodes
		 */
		
		for (int nodeId: this.getAllNodeIds()) {
			
			PolicyNode node = this.getPolicyNode(nodeId);
			this.removeNode(nodeId);
			node.setsBelief("{\"N/A\":\"N/A\"}");
			node.setBelief(null);
			this.putPolicyNode(nodeId, node);
		}
	}
	
	private void deleteSubTree(int nodeId) {
		/*
		 * Delete the entire subtree rooted at nodeId
		 */
		
		this.removeNode(nodeId);
		this.removeEdgeWithDestId(nodeId);
		this.removeEdge(nodeId);
	}
	
	private void mergeCommonSubTrees() {
		/*
		 * Merges the common subtrees in the policy tree to keep it compact
		 */
		
		for (int t = 0; t < this.maxT + 1; t++) {
			
			ArrayList<Integer> nodes = new ArrayList<Integer>(this.getAllNodesAtHorizon(t));
			
			if (nodes.size() < 2) continue;
			
			/* compare each node with other nodes */
			while (nodes.size() > 1) {
				
				int nodeId = nodes.remove(0);
				
				for (int otherNodeId: nodes) {
					
					/*
					 * delete the subtree of otherNodeId if node is equal and point all
					 * parents to the orignal node
					 */
					if(this.isSubTreeEqual(nodeId, otherNodeId)) {
						
						HashMap<Integer, HashMap<List<String>, Integer>> edges = 
								this.getEdgesEndingAt(otherNodeId);
						
						for (int src: edges.keySet()) {
							for (List<String> label: edges.get(src).keySet()) {
								this.updateEdgeDest(src, label, otherNodeId, nodeId);
							}
						}
						
						this.deleteSubTree(otherNodeId);
					}
				}
			}
		}
		
		this.commitChanges();
	}
	
	private boolean isSubTreeEqual(int nodeId, int otherNodeId) {
		/*
		 * Checks if given nodes are roots for the same subtree
		 */
		
		PolicyNode node = this.getPolicyNode(nodeId);
		PolicyNode otherNode = this.getPolicyNode(otherNodeId);
		
		if (node == null || otherNode == null) return false;
		
		if (!node.getActName().contentEquals(otherNode.getActName())) {
			return false;
		}
		
		HashMap<List<String>, Integer> nodeChildren = this.getEdges(nodeId);
		HashMap<List<String>, Integer> otherNodeChildren = this.getEdges(otherNodeId);
		
		if (nodeChildren == null || 
				otherNodeChildren == null || 
				nodeChildren.size() != otherNodeChildren.size()) {
			LOGGER.debug("children are null or unequal");
			return false;
		}
		
		if (!nodeChildren.isEmpty()) {
			
			ArrayList<Boolean> childrenEqual = new ArrayList<Boolean>();
			
			for (List<String> edge: nodeChildren.keySet()) {
				
				if (!otherNodeChildren.containsKey(edge)) {
					return false;
				}
				
				boolean childsEqual = 
						this.isSubTreeEqual(nodeChildren.get(edge), otherNodeChildren.get(edge));
				
				if (!childsEqual) return false;
				
				childrenEqual.add(childsEqual);
			}
			
			return childrenEqual.stream().reduce(true, (a, b) -> a & b);
		}
		
		/* declare nodes are equal if actions are same */
		if (node.getActName().contentEquals(otherNode.getActName()))
			return true;
		
		else return false;
	}
	
	// -----------------------------------------------------------------------------------------------
	
	public String getDotStringForPersistent() {
		/*
		 * Converts to graphviz compatible dot string
		 */
		String endl = "\r\n";
		String dotString = "digraph G{ " + endl;
		
		dotString += "graph [ranksep=3];" + endl;
		
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
