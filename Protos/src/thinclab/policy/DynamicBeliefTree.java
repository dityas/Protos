/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.belief.Belief;
import thinclab.belief.InteractiveBelief;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.policyhelper.PolicyNode;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class DynamicBeliefTree extends StaticBeliefTree {
	
	/*
	 * Belief Tree structure which expands online
	 */
	
	private static final long serialVersionUID = -3964769935114420809L;

	/* keep a track of recent leaf nodes */
	public HashSet<Integer> leafNodes = new HashSet<Integer>();
	
	private static final Logger logger = Logger.getLogger(DynamicBeliefTree.class);
	
	// -------------------------------------------------------------------------------------
	
	public DynamicBeliefTree(BaseSolver solver, int lookAhead) {
		/*
		 * Initialize Static belief tree with short look aheads 
		 */
		
		super(solver, lookAhead);
		logger.debug("Dynamic Belief Tree initialized with look ahead of " + lookAhead);
		
		/* Add initial beliefs to leaf nodes */
		for (int i = 0; i < this.f.getInitialBeliefs().size(); i++) {
			this.leafNodes.add(i);
			
			PolicyNode node = new PolicyNode();
			node.id = i;
			node.belief = this.f.getInitialBeliefs().get(i);
			node.H = 0;
			
			if (this.f instanceof IPOMDP)
				node.sBelief = 
					InteractiveBelief.toStateMap(
							(IPOMDP) this.f, 
							node.belief).toString();
			
			else 
				node.sBelief =
					Belief.toStateMap((POMDP) this.f, node.belief).toString();
			
			if (this.solver != null)
				node.actName = this.solver.getActionForBelief(node.belief);
			
			else 
				node.actName = "";
				
			this.idToNodeMap.put(i, node);
			
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
		
		for (int t = 1; t < this.maxT; t++) {
			
			List<Integer> nextNodes = this.getNextPolicyNodes(prevNodes, t);
			prevNodes = nextNodes;
			
			if (t == 1) {
				this.leafNodes.clear();
				this.leafNodes.addAll(prevNodes);
			}
		}
	}
	
	// ----------------------------------------------------------------------------------------
	
	public void pruneZeroProbabilityLeaves(Collection<String> nonZeroLeaves) {
		
		List<Integer> nonZeroIds = MJ.getNodeIds(nonZeroLeaves);
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
		logger.debug("Previous roots are: " + this.leafNodes);
		
		if (this.leafNodes.containsAll(nonZeroLeafIds))
			this.leafNodes.retainAll(nonZeroLeafIds);
		
		else {
			logger.error("Mj and IPOMDP sync lost: current non zero belief " + nonZeroLeafIds
				+ " not in leafs tracked by belief tree " + this.leafNodes);
			
			System.exit(-1);
		}
		
		/* prune leaves from the maps */
		this.pruneNodeAndEdgeMaps();
		this.idToNodeMap.values().forEach(n -> n.H = 0);
		
		logger.debug("After pruning, non zero roots are: " + this.leafNodes);
	}
	
	public void pruneNodeAndEdgeMaps() {
		/*
		 * Removes older nodes from the node and edge maps
		 */
		
		this.edgeMap.clear();

		for (int nodeId : new ArrayList<Integer>(this.idToNodeMap.keySet()))
			if (!this.leafNodes.contains(nodeId))
				this.idToNodeMap.remove(nodeId);
	}
}
