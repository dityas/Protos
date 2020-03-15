/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations.belieftreerepresentations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.legacy.DD;
import thinclab.representations.PersistentStructuredTree;
import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class OptimalDynamicBeliefTree extends DynamicBeliefTree {

	/*
	 * Represents a policy tree starting from a depth 1 full belief expansion
	 * to cover Mj completely
	 */
	
	private static final long serialVersionUID = -8284860199891317543L;
	private static final Logger LOGGER = Logger.getLogger(OptimalDynamicBeliefTree.class);
	
	// ----------------------------------------------------------------------------------------

	public OptimalDynamicBeliefTree(BaseSolver solver, int lookAhead) {
		super(solver, lookAhead);
		LOGGER.info("Initializing OptimalDynamicBeliefTree for lookahead " + lookAhead);
	}
	
	// -----------------------------------------------------------------------------------------
	
	public List<Integer> getOptimalNextPolicyNodes(List<Integer> previousNodes, int T) {
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
		prevNodes.addAll(leafNodes);
		
		/*
		 * build lookahead tree for first step
		 */
		
		this.leafNodes.clear();
		this.leafNodes.addAll(this.getNextPolicyNodes(prevNodes, 1));
		prevNodes.clear();
		prevNodes.addAll(this.leafNodes);
		
		for (int t = 2; t < this.maxT + 1; t++) {
			
			List<Integer> nextNodes = 
					this.getOptimalNextPolicyNodes(prevNodes, t);
			prevNodes = nextNodes;
		}
		
		/* strip beliefs from the policy nodes */
		this.stripBeliefInfo();
		this.mergeCommonSubTrees();
		
		if (this instanceof PersistentStructuredTree)
			this.commitChanges();
	}
	
	private void stripBeliefInfo() {
		/*
		 * Removes the information about beliefs from the policy nodes
		 */
		
		for (int nodeId: this.getAllNodeIds()) {
			
			PolicyNode node = this.getPolicyNode(nodeId);
			if (node.getH() < 2) continue;
			
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
		
		HashMap<List<String>, Integer> children = this.getEdges(nodeId);
		
		/* delete children */
		if (children != null && children.size() > 0) {
			for (int childId: children.values()) this.deleteSubTree(childId);
		}
		
		this.removeNode(nodeId);
		this.removeEdgeWithDestId(nodeId);
		this.removeEdge(nodeId);
	}
	
	private void mergeCommonSubTrees() {
		/*
		 * Merges the common subtrees in the policy tree to keep it compact
		 */
		
		for (int t = 2; t < this.maxT + 1; t++) {
			
			ArrayList<Integer> nodes = new ArrayList<Integer>(this.getAllNodesAtHorizon(t));
			LOGGER.debug("Looking for common subtrees in " + nodes);
			
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
						
						LOGGER.debug("Pointing " + edges + " to " + nodeId);
						
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
		LOGGER.debug("Checking " + node + " and " + otherNode);
		
		if (node == null || otherNode == null) return false;
		
		if (!node.getActName().contentEquals(otherNode.getActName())) {
			LOGGER.debug(node.getActName() + " != " + otherNode.getActName());
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
					LOGGER.debug("Children maps don't match");
					return false;
				}
				
				boolean childsEqual = 
						this.isSubTreeEqual(nodeChildren.get(edge), otherNodeChildren.get(edge));
				
				if (!childsEqual) return false;
				
				childrenEqual.add(childsEqual);
			}
			
			LOGGER.debug("Children equality check " + childrenEqual);
			return childrenEqual.stream().reduce(true, (a, b) -> a & b);
		}
		
		LOGGER.debug(nodeId + " and " + otherNodeId + " seem to be equal");
		return true;
	}

}
