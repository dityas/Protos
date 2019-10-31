/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.belief.Belief;
import thinclab.belief.InteractiveBelief;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.legacy.DD;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class ConditionalPlanTree extends StaticBeliefTree {
	
	/*
	 * Defines a policy tree using an infinite horizon policy
	 */

	private static final long serialVersionUID = 1455811940849589344L;

	/* reference to the solver */
	BaseSolver solver;
	
	private static final Logger logger = Logger.getLogger(ConditionalPlanTree.class);
	
	// ----------------------------------------------------------------------------------
	
	public ConditionalPlanTree(BaseSolver solver, int maxH) {
		
		super(solver.f, maxH);
		this.solver = solver;
		
		logger.debug("Initializing StaticBeliefTree for maxT " + this.maxT);
	}
	
	// -----------------------------------------------------------------------------------
	
	@Override
	public List<Integer> getNextPolicyNodes(List<Integer> previousNodes, int T) {
		/*
		 * Compute the next PolicyNode from the list of previous PolicyNodes
		 */
		
		HashMap<String, Integer> nodeMap = new HashMap<String, Integer>();
		
		/* For each previous Node */
		for (int parentId : previousNodes) {
			
			/* For all combinations */
			for (List<String> obs : this.observations) {
				
				DD belief = this.idToNodeMap.get(parentId).belief;
				
				this.makeNextPolicyNode(
						parentId, 
						belief, 
						this.solver, 
						this.solver.getActionForBelief(belief), 
						obs, 
						nodeMap, T);
			
			} /* for all observations */
		} /* for all parents */
		
		return new ArrayList<Integer>(nodeMap.values());
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
			node.id = i;
			node.belief = this.f.getInitialBeliefs().get(i);
			node.actName = this.solver.getActionForBelief(node.belief);
			
			if (this.f.getType().contentEquals("IPOMDP"))
				node.sBelief = ((IPOMDP) this.f).getBeliefString(node.belief);
			
			else 
				node.sBelief = this.f.getBeliefString(node.belief);
			
			this.idToNodeMap.put(i, node);
			
			this.currentPolicyNodeCounter += 1;
		}
		
		for (int t = 0; t < this.maxT; t++) {
			
			List<Integer> nextNodes = this.getNextPolicyNodes(prevNodes, t);
			prevNodes = nextNodes;
		}
	}
}
