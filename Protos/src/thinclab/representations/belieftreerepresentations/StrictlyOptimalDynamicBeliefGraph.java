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
import java.util.Map.Entry;

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
		
		this.extendToPolicyGraph(new ArrayList<Integer>(leafNodes));
		
		if (this instanceof PersistentStructuredTree)
			this.commitChanges();
		
		/* clear out unique nodes map to save mem */
		for (int nodeId : new ArrayList<Integer>(this.getAllNodeIds())) {
			if (!this.leafNodes.contains(nodeId)) {
				this.nodeToIdMap.remove(this.getPolicyNode(nodeId).getBelief());
			}
		}
	}
	
	public void appendPolicyGraph() {
		/*
		 * Appends the PolicyGraph to the overall Mj structure
		 */
		
		/* add nodes from policy graph */
		for (int id: this.G.getAllNodeIds()) {
			
			LOGGER.warn(this.G.getAllNodeIds());
			
			/* modify PolicyNode for use with BeliefGraph */
			PolicyNode node = this.G.getPolicyNode(id);
			
			PolicyNode copyNode = new PolicyNode();
			copyNode.setActName(node.getActName());
			copyNode.setsBelief("{\"N/A\":\"N/A\"}");
			
			int bGraphId = node.getAlphaId() + this.currentPolicyNodeCounter;
			copyNode.setId(bGraphId);
			
			this.putPolicyNode(bGraphId, copyNode);
			
			for (Entry<List<String>, Integer> entry: this.G.getEdges(id).entrySet()) {
				
				for (String action: this.solver.f.getActions()) {
					
					List<String> edge = new ArrayList<String>();
					edge.add(action);
					edge.addAll(entry.getKey());
					
					this.putEdge(
							bGraphId, 
							edge, 
							entry.getValue() + this.currentPolicyNodeCounter);
				}
			}
		}
	}
	
	public void extendToPolicyGraph(List<Integer> nodeIds) {
		/*
		 * Extend the leafs of the belief tree given in nodeIds to policy graph
		 */
		
		DecisionProcess DP = this.solver.f;
		
		/* branch for all possible observations */
		List<List<String>> obs = DP.getAllPossibleObservations();
		
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
						
						this.putPolicyNode(alphaId, nexNode);
						newLeaves.add(alphaId);
					}
					
					for (String action: DP.getActions()) {
						
						List<String> edge = new ArrayList<String>();
						edge.add(action);
						edge.addAll(theObs);
						
						if (!this.getEdges(node.getId()).containsKey(edge))
							this.putEdge(node.getId(), edge, alphaId);
					}
					
				}
				
				catch (ZeroProbabilityObsException e) {
					continue;
				}
			}
			
			nodeIds.addAll(newLeaves);
		}
	}
	
	public void mergeWithPolicyGraph(List<Integer> nodeIds) {
		/*
		 * Merge mj lookahead tree with policy graph
		 */
		
		for (int nodeId: nodeIds) {
			
			/* get PolicyNode */
			PolicyNode node = this.getPolicyNode(nodeId);
			DD nodeBelief = node.getBelief();
			
			/* for each action and observation, find optimal alpha ID */
			for (String action: this.f.getActions()) {
				for (List<String> obs: this.f.getAllPossibleObservations()) {
					
					try {
						DD nextBelief = 
								this.f.beliefUpdate(
										nodeBelief, action, obs.stream().toArray(String[]::new));
						
						int alphaId = 
								DecisionProcess.getBestAlphaIndex(
										this.f, 
										nextBelief, 
										((OfflinePBVISolver) this.solver).getAlphaVectors());
						
						List<String> edge = new ArrayList<String>();
						edge.add(action);
						edge.addAll(obs);
						
						HashMap<List<String>, Integer> prevEdges = this.getEdges(nodeId);
						
						if (!prevEdges.containsKey(edge))
							this.putEdge(nodeId, edge, alphaId + this.currentPolicyNodeCounter);
					}
					
					catch (Exception e) {
						LOGGER.error("While merging PolicyGraph with DynamicBeliefGraph, " + e.getMessage());
						e.printStackTrace();
						System.exit(-1);
					}
				}
			}
			
		}
	}
}
