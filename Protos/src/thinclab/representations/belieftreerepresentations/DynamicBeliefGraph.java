/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations.belieftreerepresentations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import thinclab.decisionprocesses.IPOMDP;
import thinclab.representations.PersistentStructuredTree;
import thinclab.representations.modelrepresentations.MJ;
import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class DynamicBeliefGraph extends StaticBeliefGraph {
	
	private static final long serialVersionUID = 6931228966081437919L;

	/* keep a track of recent leaf nodes */
	public HashSet<Integer> leafNodes = new HashSet<Integer>();
	
	private static final Logger LOGGER = Logger.getLogger(DynamicBeliefGraph.class);
	
	// -------------------------------------------------------------------------------------
	
	public DynamicBeliefGraph(BaseSolver solver, int lookAhead) {
		/*
		 * Initialize Static belief tree with short look aheads 
		 */
		
		super(solver, lookAhead);
		LOGGER.debug("Dynamic Belief Graph initialized with look ahead of " + lookAhead);
		
		/* Add initial beliefs to leaf nodes */
		for (int i = 0; i < this.f.getInitialBeliefs().size(); i++) {
			this.leafNodes.add(i);
			
			PolicyNode node = new PolicyNode();
			node.setId(i);
			node.setBelief(this.f.getInitialBeliefs().get(i));
			node.setH(0);
			
			node.setsBelief(this.f.getBeliefString(node.getBelief()));
			
			if (this.solver != null)
				node.setActName(this.solver.getActionForBelief(node.getBelief()));
			
			else 
				node.setActName("");
				
			this.putPolicyNode(i, node);
			this.nodeToIdMap.put(node.getBelief(), node.getId());
			
			this.currentPolicyNodeCounter += 1;
		}
		
	}
	
	// --------------------------------------------------------------------------------------
	
	@Override
	public void buildTree() {
		/*
		 * Builds the full OnlinePolicyTree upto maxT
		 */
		
		List<Integer> prevNodes = new ArrayList<Integer>();
		prevNodes.addAll(leafNodes);
		
		/*
		 * if one step look ahead, don't add the next leaves to models
		 * but store them for computing the next step
		 */
		if (this.maxT == 1) {
			this.leafNodes.clear();
			this.leafNodes.addAll(this.getNextPolicyNodes(prevNodes, 1));
		}
		
		else {
			for (int t = 1; t < this.maxT; t++) {
				
				List<Integer> nextNodes = this.getNextPolicyNodes(prevNodes, t);
				prevNodes = nextNodes;
				
				if (t == 1) {
					this.leafNodes.clear();
					this.leafNodes.addAll(prevNodes);
				}
				
				LOGGER.debug("Expansion done for level " + t);
			}
		}
		
		if (this instanceof PersistentStructuredTree)
			this.commitChanges();
		
		/* clear out unique nodes map to save mem */
		for (int nodeId : new ArrayList<Integer>(this.getAllNodeIds())) {
			if (!this.leafNodes.contains(nodeId)) {
				this.nodeToIdMap.remove(this.getPolicyNode(nodeId).getBelief());
			}
		}
	}
	
	// ----------------------------------------------------------------------------------------
	
	public void pruneZeroProbabilityLeaves(Collection<String> nonZeroLeaves) {
		
		List<Integer> nonZeroIds = 
				MJ.getNodeIds(nonZeroLeaves.stream()
								.filter(n -> IPOMDP.getFrameIDFromVarName(n) == this.f.frameID)
								.collect(Collectors.toList()));
		this.pruneZeroProbabilityLeaves(nonZeroIds);
	}
	
	public void pruneZeroProbabilityLeaves(List<Integer> nonZeroLeafIds) {
		/*
		 * Removes those leaves from this.leafNodes which have zero probability beliefs 
		 */
		
		/* 
		 * check if all nonZeroLeafs are in the super set of the current leafs.
		 * Else the expansion function and the IPOMDP are not in sync
		 */
		LOGGER.debug("Previous roots are: " + this.leafNodes);
		
		if (this.leafNodes.containsAll(nonZeroLeafIds))
			this.leafNodes.retainAll(nonZeroLeafIds);
		
		else {
			LOGGER.error("Mj and IPOMDP sync lost: current non zero belief " + nonZeroLeafIds
				+ " not in leafs tracked by belief tree " + this.leafNodes);
			
			for (int nodeId: this.leafNodes)
				if (nonZeroLeafIds.contains(nodeId))
					nonZeroLeafIds.remove(Integer.valueOf(nodeId));
			
			LOGGER.error("Unknown leaf IDs: " + nonZeroLeafIds);
			
			System.exit(-1);
		}
		
		/* prune leaves from the maps */
		this.pruneNodeAndEdgeMaps();
		this.setAllAsRoots();
		
		LOGGER.debug("After pruning, non zero roots are: " + this.leafNodes);
	}
	
	public void pruneNodeAndEdgeMaps() {
		/*
		 * Removes older nodes from the node and edge maps
		 */
		
		this.clearAllEdges();

		for (int nodeId : new ArrayList<Integer>(this.getAllNodeIds())) {
			if (!this.leafNodes.contains(nodeId)) {
				this.nodeToIdMap.remove(this.getPolicyNode(nodeId).getBelief());
				this.removeNode(nodeId);
			}
		}
	}
}
