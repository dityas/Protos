/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import thinclab.belief.InteractiveBelief;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.legacy.DD;
import thinclab.policyhelper.PolicyNode;
import thinclab.solvers.OnlineSolver;

/*
 * @author adityas
 *
 */
public class OnlinePolicyTree extends StructuredTree {
	
	/*
	 * Makes a static policy tree from online solvers
	 * 
	 * This representation is computed by taking an online solver and walking it through
	 * all possible observation combinations for a fixed horizon
	 */
	
	/* Store solver instance and max time steps */
	public OnlineSolver solver;
	
	/* Directory to store intermediate IPOMDP states. Default is /tmp/ */
	public String storageDir = "/tmp/";
	
	/* Store filenames where IPOMDP state was serialized */
	public HashMap<Integer, String> nodeIdToFileNameMap = new HashMap<Integer, String>();

	private static final Logger logger = Logger.getLogger(OnlinePolicyTree.class);
	
	// ---------------------------------------------------------------------------------------
	
	public OnlinePolicyTree(OnlineSolver solver, int maxT) {
		
		this.solver = solver;
		this.maxT = maxT;
		
		this.observations = ((IPOMDP) this.solver.getFramework()).getAllPossibleObservations();
		
		logger.info("Making OnlinePolicyTree for maxT " + this.maxT);
	}
	
	// ---------------------------------------------------------------------------------------
	
	public void computeAllObservationPaths() {
		/*
		 * Computes and stores the paths of all possible observations for the given IPOMDP
		 * and walks the solver through them one by one
		 */
	}
	
	public List<Integer> getNextPolicyNodes(List<Integer> previousNodes, int T) {
		/*
		 * Compute the next PolicyNode from the list of previous PolicyNodes
		 */
		
		HashMap<String, Integer> nodeMap = new HashMap<String, Integer>();
		
		/* For each previous Node */
		for (int parentId : previousNodes) {
			
			/* For all combinations */
			for (List<String> obs : this.observations) {
				
				/*
				 * If IPOMDP has not been solved for this state, solve it and
				 * save the IPOMDP state to file. 
				 */
				if (!this.nodeIdToFileNameMap.containsKey(parentId)) {
					
					/* Solve current step and store the solved IPOMDP */
					this.solver.resetBeliefExpansion();
					this.solver.solveCurrentStep();
					
					String optimalAction = this.solver.getActionAtCurrentBelief();
					DD beliefDD = 
							((IPOMDP) this.solver.getFramework())
								.getCurrentBeliefs()
								.get(0);
					
					String belief = 
							InteractiveBelief.toStateMap(
									(IPOMDP) this.solver.getFramework(), 
									beliefDD).toString();
					
					PolicyNode node = 
							new PolicyNode(
									this.currentPolicyNodeCounter, 
									T, belief, optimalAction);
					
					/* Save IPOMDP state */
					String fName = 
							this.storageDir 
								+ "IPOMDP_stateAtNode_" 
								+ this.currentPolicyNodeCounter
								+ ".obj";
					
					try {
						IPOMDP.saveIPOMDP((IPOMDP) this.solver.getFramework(), fName);
					} 
					
					catch (IOException e) {
						logger.error("While storing IPOMDP state in " + fName);
						e.printStackTrace();
						System.exit(-1);
					}
					
					/* populate hashmaps */
					this.idToNodeMap.put(this.currentPolicyNodeCounter, node);
					this.nodeIdToFileNameMap.put(this.currentPolicyNodeCounter, fName);
					
					/* increment node counter */
					this.currentPolicyNodeCounter += 1;
				}
					
				try {
					
					/* Load solved IPOMDP */
					IPOMDP ipomdp = 
							IPOMDP.loadIPOMDP(
									this.nodeIdToFileNameMap.get(parentId));
					
					/* Replace IPOMDP reference in solver */
					this.solver.setFramework(ipomdp);
				} 
				
				catch (Exception e) {
					logger.error("While loading IPOMDP from file");
					e.printStackTrace();
					System.exit(-1);
				}
				
				/* Step the solver */
				this.solver.nextStep(this.idToNodeMap.get(parentId).actName, obs);
				
				/* solve the IPOMDP for the current state */
				this.solver.solveCurrentStep();
				
				/* See if the next belief already exists */
				IPOMDP nextStepIPOMDP = (IPOMDP) this.solver.getFramework();
				
				String belief = 
						InteractiveBelief.toStateMap(
								nextStepIPOMDP, 
								nextStepIPOMDP.getCurrentBeliefs().get(0)).toString();
				
				/* If not already computed, compute and save the IPOMDP state */
				if (!nodeMap.containsKey(belief)) { 
					
					nodeMap.put(belief, this.currentPolicyNodeCounter);
					
					/* Save the IPOMDP state */
					String fName = this.storageDir + "IPOMDP_stateAtNode_" 
							+ this.currentPolicyNodeCounter + ".obj";
					
					try {
						IPOMDP.saveIPOMDP(nextStepIPOMDP, fName);
					} 
					
					catch (IOException e) {
						logger.error("While saving IPOMDP to " + fName);
						e.printStackTrace();
						System.exit(-1);
					}
					
					this.nodeIdToFileNameMap.put(this.currentPolicyNodeCounter, fName);
					
					/* Store new policy Node */
					this.idToNodeMap.put(
							this.currentPolicyNodeCounter, 
							new PolicyNode(
									this.currentPolicyNodeCounter, T + 1, 
									belief, this.solver.getActionAtCurrentBelief()));
					
					/* increment node id */
					this.currentPolicyNodeCounter += 1;
					
				}
				
				int nodeId = nodeMap.get(belief);
				
				/* Create edge */
				if (!this.edgeMap.containsKey(parentId)) 
					this.edgeMap.put(parentId, new HashMap<List<String>, Integer>());
				
				this.edgeMap.get(parentId).put(obs, nodeId);
				
			} /* for all observations */
		} /* for all actions */
		
		return new ArrayList<Integer>(nodeMap.values());
	}
	
	public void buildTree() {
		/*
		 * Builds the full OnlinePolicyTree upto maxT
		 */
		
		List<Integer> prevNodes = new ArrayList<Integer>();
		prevNodes.add(0);
		
		for (int t = 0; t < this.maxT; t++) {
			
			List<Integer> nextNodes = this.getNextPolicyNodes(prevNodes, t);
			prevNodes = nextNodes;
		}
	}
	
	// ------------------------------------------------------------------------------------------
	
	public String getDotString() {
		/*
		 * Converts to graphviz compatible dot string
		 */
		String endl = "\r\n";
		String dotString = "digraph G{ " + endl;
		
		/* Make nodes */
		for (Entry<Integer, PolicyNode> entry : this.idToNodeMap.entrySet()) {
			dotString += " " + entry.getKey() + " [shape=record, label=\"{"
					+ "Ai=" + entry.getValue().actName + " | "
					+ entry.getValue().sBelief
						.replace("{", "(")
						.replace("}", ")")
						.replace("),", "),|")
					+ "}\"];" + endl;
		}
		
		dotString += endl;
		
		for (Entry<Integer, HashMap<List<String>, Integer>> edges : this.edgeMap.entrySet()) {
			
			String from = edges.getKey().toString();
			
			for (Entry<List<String>, Integer> ends : edges.getValue().entrySet()) {
				
				dotString += " " + from + " -> " + ends.getValue()
					+ " [label=\"" + ends.getKey().toString() + "\"]" + endl;
			}
		}
		
		dotString += "}" + endl;
		
		return dotString;
	}
	
}
