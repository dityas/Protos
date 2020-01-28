/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations.conditionalplans;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.decisionprocesses.IPOMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.representations.StructuredTree;
import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.OnlineSolver;

/*
 * @author adityas
 *
 */
public class WalkablePolicyTree extends StructuredTree {
	
	/*
	 * Makes a static policy tree from online solvers
	 * 
	 * This representation is computed by taking an online solver and walking it through
	 * all possible observation combinations for a fixed horizon
	 */
	
	private static final long serialVersionUID = -8228448045160971899L;

	/* Store solver instance and max time steps */
	public OnlineSolver solver;
	
	/* Directory to store intermediate IPOMDP states. Default is /tmp/ */
	public String storageDir = "/tmp/";
	
	/* Store filenames where IPOMDP state was serialized */
	public HashMap<Integer, String> nodeIdToFileNameMap = new HashMap<Integer, String>();

	private static final Logger logger = Logger.getLogger(WalkablePolicyTree.class);
	
	// ---------------------------------------------------------------------------------------
	
	public WalkablePolicyTree(OnlineSolver solver, int maxT) {
		
		this.solver = solver;
		this.maxT = maxT;
		
		this.observations = this.solver.getFramework().getAllPossibleObservations();
		
		logger.info("Making OnlinePolicyTree for maxT " + this.maxT);
	}
	
	// ---------------------------------------------------------------------------------------
	
	public List<Integer> getNextPolicyNodes(List<Integer> previousNodes, int T) {
		/*
		 * Compute the next PolicyNode from the list of previous PolicyNodes
		 */
		
		HashMap<String, Integer> nodeMap = new HashMap<String, Integer>();
		
		/* For each previous Node */
		for (int parentId : previousNodes) {
			
			/* For all combinations */
			for (List<String> obs : this.observations) {
				
				/* load saved state */
				IPOMDP ipomdp = (IPOMDP) IPOMDP.loadIPOMDP(this.nodeIdToFileNameMap.get(parentId));
				this.solver.setFramework(ipomdp);
				
				/* try stepping to the next IPOMDP state */
				try {
					
					this.solver.nextStep(this.idToNodeMap.get(parentId).getActName(), obs);
				}
				
				catch (ZeroProbabilityObsException e) {
					continue;
				}
				
				/* make next node */
				String sBelief = this.solver.f.getBeliefString(this.solver.f.getCurrentBelief());
				
				if (!nodeMap.containsKey(sBelief)) {
				
					this.solver.solveCurrentStep();
					String action = this.solver.getActionAtCurrentBelief();
					
					PolicyNode nextNode = new PolicyNode();
					nextNode.setId(this.currentPolicyNodeCounter);
					this.currentPolicyNodeCounter += 1;
					nextNode.setActName(action);
					nextNode.setsBelief(sBelief);
					
					/* populate the maps */
					nodeMap.put(nextNode.getsBelief(), nextNode.getId());
					this.idToNodeMap.put(nextNode.getId(), nextNode);
					
					/* Store IPOMDP state */
					String fName = 
							this.storageDir + "IPOMDP_stateAtNode_" + nextNode.getId() + ".obj"; 
					IPOMDP.saveIPOMDP(
							(IPOMDP) this.solver.f, 
							fName);
					
					this.nodeIdToFileNameMap.put(nextNode.getId(), fName);
				
				}
				
				int nextNode = nodeMap.get(sBelief);
				
				if (!this.edgeMap.containsKey(parentId))
					this.edgeMap.put(parentId, new HashMap<List<String>, Integer>());
				
				this.edgeMap.get(parentId).put(obs, nextNode);
				
			} /* for all observations */
		} /* for all parents */
		
		return new ArrayList<Integer>(nodeMap.values());
	}
	
	public void buildTree() {
		/*
		 * Builds the full OnlinePolicyTree upto maxT
		 */
		
		List<Integer> prevNodes = new ArrayList<Integer>();
		
		/* make node for start state */
		PolicyNode node = new PolicyNode();
		node.setId(this.currentPolicyNodeCounter);
		this.currentPolicyNodeCounter += 1;
		
		solver.solveCurrentStep();
		
		node.setsBelief(this.solver.f.getBeliefString(this.solver.f.getCurrentBelief()));
		node.setActName(this.solver.getActionAtCurrentBelief());
		
		/* store current state */
		String fName = this.storageDir + "IPOMDP_stateAtNode_" + node.getId() + ".obj"; 
		IPOMDP.saveIPOMDP(
				(IPOMDP) this.solver.f, 
				fName);
		
		/* populate maps */
		this.idToNodeMap.put(node.getId(), node);
		this.nodeIdToFileNameMap.put(node.getId(), fName);
		
		prevNodes.add(node.getId());
		
		/* build the rest of the tree */
		for (int t = 0; t < this.maxT; t++) {
			
			List<Integer> nextNodes = this.getNextPolicyNodes(prevNodes, t);
			prevNodes = nextNodes;
		}
		
		this.nodeIdToFileNameMap.values().forEach(v -> {
			
			try {
				Files.delete(Paths.get(v));
				logger.debug("Deleted " + v);
			} 
			
			catch (IOException e) {
				logger.warn("Could not delete " + v);
			}
		});
	}
	
}
