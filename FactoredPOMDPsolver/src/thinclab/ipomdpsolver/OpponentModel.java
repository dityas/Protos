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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import thinclab.domainMaker.ddHelpers.DDMaker;
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
	
	public OpponentModel(List<POMDP> frames) {
		/*
		 * Initialize from <belief, frame> combination
		 */
		for (POMDP frame: frames) {
			
			/* Make policy tree for limited horizons */
			PolicyTree T = frame.getPolicyTree(5);
			
			/*
			 * Start indexing nodes incrementally so that each node has a unique index
			 * even if it is from different trees
			 */
			T.shiftIndex(nodesList.size());
			
			/* Add nodes to */
			nodesList.addAll(T.policyNodes);
		}
		
		/* create child names for state var */
		this.nodesList.stream().forEach(n -> this.nodeIndex.put("m" + n.id, n));
	}
	
	// -----------------------------------------------------------------------------
	
	public StateVar getOpponentModelStateVar(int index) {
		/*
		 * Makes a StateVar object for the opponent model
		 * 
		 * Makes a new random variable M. The possible values taken by M are the
		 * policy nodes in the opponent model policy trees. 
		 */
		String[] nodeNames = 
				this.nodeIndex.keySet().toArray(
						new String[this.nodesList.size()]);
		
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
		
		for (PolicyNode node : this.nodesList) {
			
			/* if node is terminal, give equal probability of ending in all states */
			
			/* for each possible obs */
			for (Entry<List<String>, Integer> entry : node.nextNode.entrySet()) {
				List<String> triple = new ArrayList<String>();
				
				/* add start node */
				triple.add("m" + node.id);
				
				/* add obs combination */
				triple.addAll(entry.getKey());
				
				/* add end node */
				triple.add("m" + entry.getValue());
				
				/* add probability 1.0 for deterministic transition */
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

}
