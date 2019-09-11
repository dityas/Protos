/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.StateVar;
import thinclab.utils.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import thinclab.domainMaker.ddHelpers.DDMaker;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.policyhelper.PolicyNode;
import thinclab.policyhelper.PolicyTree;

/*
 * @author adityas
 *
 */
public class OpponentModel {
	/*
	 * Defines the opponent model as a single transition function between opponent's
	 * policy tree nodes
	 * 
	 * The opponent model itself is an abstraction. The policy trees from each individual
	 * frame are combined single transition function between different nodes on these policy trees.
	 * Each node in the policy tree is associated with a single optimal action and a single belief.
	 */
	public List<PolicyNode> nodesList = new ArrayList<PolicyNode>();
	public HashMap<String, PolicyNode> nodeIndex = new HashMap<String, PolicyNode>();
	
	/* Store the PolicyTrees as global triples */
	public HashMap<String, HashMap<List<String>, String>> triplesMap =
			new HashMap<String, HashMap<List<String>, String>>();
	
	private Logger logger = LoggerFactory.getNewLogger("Opponent Model");
	
	/*
	 * Variables for determining the current context i.e. current roots and currently visited
	 * nodes of the OpponentModel
	 */
	public HashSet<String> currentRoots = new HashSet<String>();
	public HashSet<String> currentNodes =
			new HashSet<String>();
	
	// ------------------------------------------------------------------------------------------
	
	public OpponentModel(List<POMDP> frames, int horizon) {
		/*
		 * Initialize from <belief, frame> combination
		 */
		this.logger.info(
				"Initializing Opponent Model for a total of " + horizon + " time steps");
		
		for (POMDP frame: frames) {
			
			/* Make policy tree for limited horizons */
			PolicyTree T = frame.getPolicyTree(horizon);
			
			/*
			 * Start indexing nodes incrementally so that each node has a unique index
			 * even if it is from different trees
			 */
			T.shiftIndex(nodesList.size());
			
			/* Add nodes to */
			nodesList.addAll(T.policyNodes);
			
			this.logger.info("Added " + T.policyNodes.size() + " PolicyNodes to Opponent Model");
			this.logger.info("Opponent Model now contains " + this.nodesList.size() + " PolicyNodes");
			
			this.currentRoots.addAll(
					T.roots.stream().map(r -> "m" + r.id).collect(Collectors.toList()));
		}
		
		/* Name and index each node and populate triplesMap */
		this.nodesList.stream()
			.forEach(n -> { 
				
				this.nodeIndex.put("m" + n.id, n);
				
				HashMap<List<String>, String> sNextNode = 
						new HashMap<List<String>, String>(); 
				n.nextNode.entrySet()
					.forEach(e -> sNextNode.put(e.getKey(), "m" + e.getValue()));
				
				this.triplesMap.put("m" + n.id, sNextNode);
			});
	}
	
	// -----------------------------------------------------------------------------
	
	public HashSet<String> step(HashSet<String> parents) 
			throws VariableNotFoundException {
		/*
		 * Makes a single tree traversal starting from the current roots.
		 */
		
		HashSet<String> nextNodes = new HashSet<String>();
		
		for (String parent : parents) {
			
			if (!this.nodeIndex.containsKey(parent))
				throw new VariableNotFoundException(
						"Node " + parent + " does not exist in opponent model"); 
			
			/* Add the next nodes for all observations to the set of next nodes*/
			nextNodes.addAll(this.triplesMap.get(parent).values());
		}
		
		return nextNodes;
	}
	
	public void expandForHorizon(HashSet<String> start, int horizon) {
		/*
		 * Traverses the opponent model for the given horizon and populates the current
		 * context.
		 */
		this.logger.info(
				"Expanding Opponent Model for " 
				+ horizon + " time steps starting from " + start);
		
		HashSet<String> startNodes = start;
		this.currentNodes.clear();
		this.currentNodes.addAll(start);
		
		for (int h = 0; h < horizon; h++) {
			
			try {
				
				HashSet<String> nextNodes = this.step(startNodes);
				
				this.currentNodes.addAll(nextNodes);
				startNodes = nextNodes;
				
				/* TEST */
				if (h < horizon - 1) {
					this.currentRoots.addAll(nextNodes);
				}
			}
			
			catch (Exception e) {
				this.logger.severe(
						"ERROR[!!!] While stepping through the Opponent Model"
						+ e.getMessage());
				System.exit(-1);
			}
			
		}
		
		this.logger.info("Done with OpponentModel expansion. Traversed node are " + this.currentNodes);
	}
	
	public void expandFromRoots(int horizon) {
		/*
		 * Traverses opponent model from the root nodes.
		 */
		this.expandForHorizon(new HashSet<String>(this.currentRoots), horizon);
	}
	
	// -----------------------------------------------------------------------------
	
	public DDTree getOpponentModelInitBelief(DDMaker ddMaker) {
		/*
		 * Constructs an initial belief DDTree based on the current roots
		 */
		this.logger.info("Making initial belief for current opponent model traversal");
		DDTree beliefMj = ddMaker.getDDTreeFromSequence(new String[] {"M_j"});
		
		/* Uniform distribution over all current roots */
		for (String node : this.currentRoots) {
			
			try {
				beliefMj.setValueAt(node, (1.0 / this.currentRoots.size()));
			} 
			
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.logger.info("Initial belief is " + beliefMj);
		
		return beliefMj;
	}
	
	public StateVar getOpponentModelStateVar(int index) {
		/*
		 * Makes a StateVar object for the opponent model
		 * 
		 * Makes a new random variable M. The possible values taken by M are the
		 * policy nodes in the opponent model policy trees. 
		 */
		String[] nodeNames = 
				this.currentNodes.toArray(
						new String[this.currentNodes.size()]);
		
		return new StateVar("M_j", index, nodeNames);
	}
	
	public String[][] getOpponentModelTriples() {
		/*
		 * Returns policy node transitions as triples of (start, obs, end)
		 * 
		 * Useful for making DDTree objects of OpponentModel and eventually making a
		 * symbolic perseus DD.
		 * 
		 * WARNING: This method assumes that the observation space is common for all
		 * frames.
		 */
		
		List<String[]> triples = new ArrayList<String[]>();
		
//		for (PolicyNode node : this.nodesList) {
//			
//			/* if node is terminal, give equal probability of ending in all states */
//			
//			/* for each possible obs */
//			for (Entry<List<String>, Integer> entry : node.nextNode.entrySet()) {
//				List<String> triple = new ArrayList<String>();
//				
//				/* add start node */
//				triple.add("m" + node.id);
//				
//				/* add obs combination */
//				triple.addAll(entry.getKey());
//				
//				/* add end node */
//				triple.add("m" + entry.getValue());
//				
//				/* add probability 1.0 for deterministic transition */
//				triple.add("1.0");
//				
//				triples.add(triple.toArray(new String[triple.size()]));
//			}
//		}
		
		for (String node : this.currentNodes) {
			
			for (Entry<List<String>, String> nextNodes : 
				this.triplesMap.get(node).entrySet()) {
				
				List<String> triple = new ArrayList<String>();
				
				/* Add start node */
				triple.add(node);
				
				/* Add observation */
				triple.addAll(nextNodes.getKey());
				
				/*
				 * Check if end node is in the current set of nodes. If they are not, 
				 * loop back to self for all observations to maintain a normalized transition
				 * function. 
				 */
				String endNode = nextNodes.getValue();
				
				if (this.currentNodes.contains(endNode)) triple.add(endNode);
				
				else triple.add(node); 
				
				/* Add transition probability */
				triple.add("1.0");
				
				triples.add(triple.toArray(new String[triple.size()]));
			}
		}
		
		/* convert list to array and return */
		return triples.toArray(new String[triples.size()][]);
	}
	
	// --------------------------------------------------------------------------------------
	
	public PolicyNode getPolicyNode(String nodeName) {
		/*
		 * Returns the policy node mapped to the nodeName
		 */
		PolicyNode node = this.nodeIndex.get(nodeName);
		return node;
	}
	
	public HashSet<String> getCurrentRoots() {
		
		return this.currentRoots;
	}

}
