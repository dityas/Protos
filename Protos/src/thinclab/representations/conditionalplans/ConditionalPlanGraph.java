/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations.conditionalplans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.ddinterface.DDTree;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class ConditionalPlanGraph extends ConditionalPlanTree {
	
	/*
	 * Creates a policy graph from a converged policy
	 */
	
	/* remember all beliefs */
	HashMap<DDTree, Integer> nodeDDTreeMap = new HashMap<DDTree, Integer>();
	
	private static final long serialVersionUID = 5003289336030260915L;
	private static final Logger LOGGER = Logger.getLogger(ConditionalPlanGraph.class);

	// ---------------------------------------------------------------------------
	
	public ConditionalPlanGraph(BaseSolver solver, int maxH) {
		
		/* initialize super */
		super(solver, maxH);
		LOGGER.info("Initialized Static Policy Graph");
	}
	
	// ----------------------------------------------------------------------------
	
	@Override
	public List<Integer> getNextPolicyNodes(List<Integer> previousNodes, int T) {
		/*
		 * Compute the next PolicyNode from the list of previous PolicyNodes
		 */
		
		List<Integer> nextNodes = new ArrayList<Integer>();
		
		/* For each previous Node */
		for (int parentId : previousNodes) {
			
			/* For all combinations */
			for (List<String> obs : this.observations) {
				
				DD belief = this.getPolicyNode(parentId).getBelief();
				
				this.makeNextPolicyNode(
						parentId, 
						belief, 
						this.solver, 
						this.solver.getActionForBelief(belief), 
						obs, 
						nextNodes);
			
			} /* for all observations */
		} /* for all parents */
		
		return new ArrayList<Integer>(nextNodes);
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
			node.setActName(this.solver.getActionForBelief(node.getBelief()));
			
			if (this.f.getType().contentEquals("IPOMDP")) {
				
				LOGGER.error("Static Policy Graph only works for POMDP policies");
				System.exit(-1);
			}
			
			else 
				node.setsBelief(this.f.getBeliefString(node.getBelief()));
			
			this.putPolicyNode(i, node);
			this.nodeDDTreeMap.put(node.getBelief().toDDTree(), i);
			
			this.currentPolicyNodeCounter += 1;
		}
		
		for (int t = 0; t < this.maxT; t++) {
			
			List<Integer> nextNodes = this.getNextPolicyNodes(prevNodes, t);
			
			if (nextNodes.size() == 0) {
				LOGGER.info("Graph converged at t=" + t);
				break;
			}
			LOGGER.debug("Generated " + nextNodes + " at t=" + t);
			prevNodes = nextNodes;
		}
	}
	
	// ----------------------------------------------------------------------------------
	
	public void makeNextPolicyNode(
			int parentNodeId, 
			DD parentNodeBelief,
			BaseSolver solver,
			String action,
			List<String> obs,
			List<Integer> nextNodes) {
		
		/* 
		 * Make the next node after taking action at parentNodeBelief and observing the
		 * specified observation
		 */
		try {
			
			DD nextBelief = null;
			
			/*
			 * If the process is an IPOMDP, do an IPOMDP belief update
			 * else POMDP belief update
			 */
			if (solver.f.getType().contentEquals("IPOMDP")) {
				LOGGER.error("Static Policy Graph only works for POMDP policies");
				System.exit(-1);
			}
			
			else {
				nextBelief = 
						solver.f.beliefUpdate( 
								parentNodeBelief, 
								action, 
								obs.toArray(new String[obs.size()])); 
			}
			
			String nextBeliefString = solver.f.getBeliefString(nextBelief);
			DDTree nextBelDDTree = nextBelief.toDDTree();
			
			/* add to belief set if nextBelief is unique */
			if (!this.nodeDDTreeMap.containsKey(nextBelDDTree)) {
				this.nodeDDTreeMap.put(nextBelDDTree, this.currentPolicyNodeCounter);
				nextNodes.add(this.currentPolicyNodeCounter);
				this.currentPolicyNodeCounter += 1;
				
				int nextNodeId = this.nodeDDTreeMap.get(nextBelDDTree);
				PolicyNode nextNode = new PolicyNode();
				
				nextNode.setId(nextNodeId);
				nextNode.setBelief(nextBelief);
				nextNode.setActName(solver.getActionForBelief(nextBelief));
				nextNode.setsBelief(nextBeliefString);
				
				/* add next unique node to the nodes map */
				this.putPolicyNode(nextNodeId, nextNode);
			}
			
			int nextNodeId = this.nodeDDTreeMap.get(nextBelDDTree);
			
//			if (!this.edgeMap.containsKey(parentNodeId))
//				this.edgeMap.put(parentNodeId, new HashMap<List<String>, Integer>());
//			
//			this.edgeMap.get(parentNodeId).put(obs, nextNodeId);
			this.putEdge(parentNodeId, obs, nextNodeId);
			
		}
		
		catch (ZeroProbabilityObsException o) {
			
		}
		
		catch (Exception e) {
			LOGGER.error("While running belief update " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
