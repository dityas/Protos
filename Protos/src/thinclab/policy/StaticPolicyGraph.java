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
import thinclab.ddinterface.DDTree;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class StaticPolicyGraph extends StaticPolicyTree {
	
	/*
	 * Creates a policy graph from a converged policy
	 */
	
	/* remember all beliefs */
	HashMap<DDTree, Integer> nodeDDTreeMap = new HashMap<DDTree, Integer>();
	
	private static final long serialVersionUID = 5003289336030260915L;
	private static final Logger LOGGER = Logger.getLogger(StaticPolicyGraph.class);

	// ---------------------------------------------------------------------------
	
	public StaticPolicyGraph(BaseSolver solver) {
		
		/* initialize super */
		super(solver, 10);
		LOGGER.info("Initialized Static Policy Graph");
	}
	
	// ----------------------------------------------------------------------------
	
	@Override
	public List<Integer> getNextPolicyNodes(List<Integer> previousNodes, int T) {
		/*
		 * Compute the next PolicyNode from the list of previous PolicyNodes
		 */
		
		HashMap<DDTree, Integer> nodeMap = new HashMap<DDTree, Integer>();
		
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
						nodeMap);
			
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
			
			if (this.f.getType().contentEquals("IPOMDP")) {
				
				LOGGER.error("Static Policy Graph only works for POMDP policies");
				System.exit(-1);
			}
			
			else 
				node.sBelief = this.f.getBeliefString(node.belief);
			
			this.idToNodeMap.put(i, node);
			this.nodeDDTreeMap.put(node.belief.toDDTree(), i);
			
			this.currentPolicyNodeCounter += 1;
		}
		
		for (int t = 0; t < this.maxT; t++) {
			
			List<Integer> nextNodes = this.getNextPolicyNodes(prevNodes, t);
			
			if (nextNodes.size() == 0) {
				LOGGER.info("Graph converged at t=" + t);
				break;
			}
			
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
			HashMap<DDTree, Integer> currentLevelBeliefSet) {
		
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
						Belief.beliefUpdate(
								(POMDP) solver.f, 
								parentNodeBelief, 
								solver.f.getActions().indexOf(action), 
								obs.toArray(new String[obs.size()])); 
			}
			
			String nextBeliefString = solver.f.getBeliefString(nextBelief);
			DDTree nextBelDDTree = nextBelief.toDDTree();
			
			/* add to belief set if nextBelief is unique */
			if (!this.nodeDDTreeMap.containsKey(nextBelDDTree)) {
				this.nodeDDTreeMap.put(nextBelDDTree, this.currentPolicyNodeCounter);
				currentLevelBeliefSet.put(nextBelDDTree, this.currentPolicyNodeCounter);
				this.currentPolicyNodeCounter += 1;
				
				int nextNodeId = this.nodeDDTreeMap.get(nextBelDDTree);
				PolicyNode nextNode = new PolicyNode();
				
				nextNode.id = nextNodeId;
				nextNode.belief = nextBelief;
				nextNode.actName = solver.getActionForBelief(nextBelief);
				nextNode.sBelief = nextBeliefString;
				
				/* add next unique node to the nodes map */
				this.idToNodeMap.put(nextNodeId, nextNode);
			}
			
			int nextNodeId = this.nodeDDTreeMap.get(nextBelDDTree);
			
			if (!this.edgeMap.containsKey(parentNodeId))
				this.edgeMap.put(parentNodeId, new HashMap<List<String>, Integer>());
			
			this.edgeMap.get(parentNodeId).put(obs, nextNodeId);
			
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
