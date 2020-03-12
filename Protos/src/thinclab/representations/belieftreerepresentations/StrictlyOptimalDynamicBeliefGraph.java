/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations.belieftreerepresentations;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.representations.PersistentStructuredTree;
import thinclab.representations.policyrepresentations.PolicyGraph;
import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.BaseSolver;
import thinclab.solvers.OfflinePBVISolver;

/*
 * @author adityas
 *
 */
public class StrictlyOptimalDynamicBeliefGraph extends DynamicBeliefGraph {

	/*
	 * Only does full belief expansion for the first step to maintain complete information
	 * about the immediate next step. After that, does policy graph expansion to produce Ajs for
	 * look ahead. 
	 */
	
	private PolicyGraph G;
	
	private static final long serialVersionUID = -4519925553640062581L;
	private static final Logger LOGGER = Logger.getLogger(StrictlyOptimalDynamicBeliefGraph.class);
	
	// ---------------------------------------------------------------------------------------------

	public StrictlyOptimalDynamicBeliefGraph(BaseSolver solver, int lookAhead) {
		
		super(solver, lookAhead);
		
		/* build and store policy graph */
		this.G = new PolicyGraph((OfflinePBVISolver) solver);
		LOGGER.debug("Policy graph for frame " + this.solver.f.frameID + " initialized");
		
	}
	
	// --------------------------------------------------------------------------------------
	
	@Override
	public void buildTree() {
		/*
		 * Builds a lookahead belief tree upto T = 1, and then merges the policy graph for the
		 * rest of the lookahead depth
		 */
		
		List<Integer> prevNodes = new ArrayList<Integer>();
		prevNodes.addAll(leafNodes);
		
		/*
		 * build lookahead tree for first step
		 */
		
		this.leafNodes.clear();
		this.leafNodes.addAll(this.getNextPolicyNodes(prevNodes, 1));
		
		for (int id: this.leafNodes)
			this.extendToPolicyGraph(id);
		
		if (this instanceof PersistentStructuredTree)
			this.commitChanges();
		
//		/* clear out unique nodes map to save mem */
//		for (int nodeId : new ArrayList<Integer>(this.getAllNodeIds())) {
//			if (!this.leafNodes.contains(nodeId)) {
//				this.nodeToIdMap.remove(this.getPolicyNode(nodeId).getBelief());
//			}
//		}
	}
	
	public void extendToPolicyGraph(int nodeId) {
		/*
		 * Extend the leafs of the belief tree given in nodeIds to policy graph
		 */
		
		DecisionProcess DP = this.solver.f;
		
		/* branch for all possible observations */
		List<List<String>> obs = DP.getAllPossibleObservations();
		List<Integer> nodeIds = new ArrayList<Integer>();
		nodeIds.add(nodeId);
		
		/* Do till there are no terminal policy leaves */
		while(!nodeIds.isEmpty()) {
			
			PolicyNode node = this.getPolicyNode(nodeIds.remove(0));
			List<Integer> newLeaves = new ArrayList<Integer>();
			
			/*
			 * For all observations, perform belief updates and get best action nodes
			 */
			for (List<String> theObs : obs) {
				
				try {
					
					DD nextBel = 
							DP.beliefUpdate( 
									node.getBelief(), 
									node.getActName(), 
									theObs.stream().toArray(String[]::new));
					
					/* get best next node */
					int alphaId = 
							DecisionProcess.getBestAlphaIndex(DP, nextBel, this.G.alphas)
							+ this.currentPolicyNodeCounter;
					
					if (!this.containsNode(alphaId)) {
						
						PolicyNode nexNode = new PolicyNode();
						
						nexNode.setsBelief("{\"N/A\":\"N/A\"}");
						nexNode.setBelief(nextBel);
						nexNode.setId(alphaId);
						nexNode.setActName(
								DP.getActions().get(
										this.G.actions[alphaId - this.currentPolicyNodeCounter]));
						nexNode.setH(2);
						
						this.putPolicyNode(alphaId, nexNode);
						newLeaves.add(alphaId);
					}
					
					for (String action: DP.getActions()) {
					
						List<String> edge = new ArrayList<String>();
						edge.add(action);
						edge.addAll(theObs);
					
						if (!this.getEdges(node.getId()).containsKey(edge)) {
							if (node.getActName().contentEquals(action))
								this.putEdge(node.getId(), edge, alphaId);
							else
								this.putEdge(node.getId(), edge, node.getId());
						}
					}
				}
				
				catch (ZeroProbabilityObsException e) {
					continue;
				}
			}
			
			nodeIds.addAll(newLeaves);
		}
	}
}
