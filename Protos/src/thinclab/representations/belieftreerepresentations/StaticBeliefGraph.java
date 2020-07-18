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

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.legacy.DD;
import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class StaticBeliefGraph extends StaticBeliefTree {

	/*
	 * Represents beliefs as a graph instead of a tree
	 * 
	 * Note: you can no longer track T once you use graphs
	 */
	
	public HashMap<DD, Integer> nodeToIdMap = new HashMap<DD, Integer>();
	
	private static final long serialVersionUID = 5529948904505648028L;
	
	// --------------------------------------------------------------------------
	
	public StaticBeliefGraph(DecisionProcess f, int maxH) {
		super(f, maxH);
	}
	
	public StaticBeliefGraph(BaseSolver solver, int maxH) {
		super(solver, maxH);
	}
	
	// --------------------------------------------------------------------------
	
	@Override
	public List<Integer> getNextPolicyNodes(List<Integer> previousNodes, int T) {
		/*
		 * Compute the next PolicyNode from the list of previous PolicyNodes
		 */
		
		List<Integer> newNodes = new ArrayList<Integer>();
		
		/* For each previous Node */
		for (int parentId : previousNodes) {
			
			/* For all combinations */
			for (List<String> obs : this.observations) {
				
				for (String action : this.f.getActions()) {
					
					DD belief = this.getPolicyNode(parentId).getBelief();
					
					this.makeNextBeliefNode(
							parentId, 
							belief, f, action, this.solver, obs, this.nodeToIdMap, newNodes, T);
			
				} /* for all actions */
			} /* for all observations */
		} /* for all parents */
		
		return newNodes;
	}
	
	@Override
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
			node.setsBelief(this.f.getBeliefString(node.getBelief()));
			
			/* record start node */
			node.setStartNode();
			
			if (this.solver != null)
				node.setActName(this.solver.getActionForBelief(node.getBelief()));
			
			else 
				node.setActName("");
				
			this.putPolicyNode(i, node);
			this.nodeToIdMap.put(node.getBelief(), node.getId());
			
			this.currentPolicyNodeCounter += 1;
		}
		
		for (int t = 0; t < this.maxT; t++) {
			
			List<Integer> nextNodes = this.getNextPolicyNodes(prevNodes, t);
			prevNodes = nextNodes;
		}
	}

}
