/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policyhelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.collections15.map.HashedMap;

import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.Belief.Belief;
import thinclab.utils.BeliefTreeTable;

/*
 * @author adityas
 *
 */
public class PolicyTree {
	/*
	 * Constructs the policy tree for a solved POMDP
	 * 
	 * Similar to policy graph. But in this case, it computes the policy up to a given horizon
	 */
	
	public POMDP pomdp;
	public List<List<String>> allObsCombinations = new ArrayList<List<String>>();
	
	public List<PolicyNode> roots;
	public List<PolicyNode> policyNodes;
	
	public HashSet<DD> treeRoots = new HashSet<DD>();
	
	private int idCounter = 0;
	
	// -------------------------------------------------------------------------------------
	
	public PolicyTree(POMDP p, int horizon) {
		/*
		 * Constructor makes the policy tree for the given horizon
		 */
		this.pomdp = p;
		
		this.roots = 
				this.pomdp.getInitialBeliefsList().stream()
												  .map(b -> new PolicyNode(
														  this.pomdp, 
														  b, 
														  0))
												  .collect(Collectors.toList());
		
		this.roots.stream().forEach(n -> n.setStartNode());
		IntStream.range(0, this.roots.size())
				 .forEach(i -> 
				 	this.roots.get(i).setId(this.getNextId()));
		
		this.expandForHorizon(horizon);
	}

	public PolicyTree(POMDP p, int horizon, BeliefTreeTable localStorage) {
		/*
		 * Constructor makes the policy tree for the given horizon and stores it
		 * to the local storage
		 */
		this.pomdp = p;
//		this.storage = localStorage;
		
		this.allObsCombinations = this.pomdp.getAllObservationsList();
		
		this.expandForHorizon(horizon);
	}
	
	// -------------------------------------------------------------------------------------
	
//	public HashSet<DD> getNextBeliefSet(HashSet<DD> currentBeliefs, int currentH) {
//		/*
//		 * Performs one step belief update over DDs
//		 */
//		HashSet<DD> nextBeliefs = new HashSet<DD>();
//		
//		/* for each DD, do a belief update and store unique beliefs */
//		for (DD belief : currentBeliefs) {
//			
//			/*
//			 * Update for each observation 
//			 */
//			String action = 
//					this.storage.getActionForBeliefAtHorizon(
//							Belief.toStateMap(this.pomdp, belief).toString(), currentH);
//			
//			for (List<String> o : this.allObsCombinations) {
//				
//				DD nextBelief;
//				
//				try {
//					nextBelief = 
//							Belief.beliefUpdate(
//									this.pomdp, 
//									belief, 
//									this.pomdp.findActionByName(action), 
//									o.toArray(new String[o.size()]));
//				}
//				
//				catch (Exception e) {
//					continue;
//				}
//				
//				PolicyNode nextNode = new PolicyNode(this.pomdp, nextBelief);
//				nextNode.setId(this.getNextId());
//				
////				policyNode.nextNode.put(o, nextNode.id);
////				nextNodes.add(nextNode);
//			}
//			
//		}
//		
//		return nextBeliefs;
//	}

	
	// --------------------------------------------------------------------------------------
	
	public List<PolicyNode> expandForSingleStep(
			List<PolicyNode> previousLeaves, 
			int currentH) {
		/*
		 * Expands the policy tree for a single time step
		 */
		List<PolicyNode> nextNodes = new ArrayList<PolicyNode>();
		
		/*
		 * Use hashmap to index new nodes. This ensures only unique nodes are added and
		 * repeating nodes at the same horizon do not exist.
		 */
		HashMap<DD, Integer> nodeIndexMap = new HashMap<DD, Integer>();
		
		for (PolicyNode policyNode : previousLeaves) {
			
			/*
			 * Update for each observation 
			 */
			List<List<String>> obs = this.pomdp.getAllObservationsList();
			for (List<String> o : obs) {
				
				DD nextBelief;
				
				try {
					nextBelief = 
							Belief.beliefUpdate(
									this.pomdp, 
									policyNode.belief, 
									policyNode.actId, 
									o.toArray(new String[o.size()]));
				}
				
				catch (Exception e) {
					continue;
				}
				
				/* unique belief. So add it to the node index and give it a new ID */
				if (!nodeIndexMap.containsKey(nextBelief))
					nodeIndexMap.put(nextBelief, new Integer(this.getNextId()));
					
				int newNodeId = nodeIndexMap.get(nextBelief);

//				PolicyNode nextNode = new PolicyNode(this.pomdp, nextBelief);
//				nextNode.setId(this.getNextId());
				
				policyNode.nextNode.put(o, newNodeId);
//				nextNodes.add(nextNode);
			}
		}
		
		/* Add each unique node to next nodes */
		nodeIndexMap.forEach((k, v) -> {
			PolicyNode newNode = new PolicyNode(this.pomdp, k, currentH + 1);
			newNode.setId(v);
			nextNodes.add(newNode);
		});
		
		return nextNodes;
	}
	
	public void expandForHorizon(int horizons) {
		/*
		 * Expands the policy tree for given number of horizons
		 */
		this.policyNodes = new ArrayList<PolicyNode>();
		List<PolicyNode> previousLeaves = new ArrayList<PolicyNode>();
		
		previousLeaves.addAll(this.roots);
		this.policyNodes.addAll(this.roots);
		
		/*
		 * Expand the policy tree
		 */
		for (int h=0; h < horizons; h++) {
			List<PolicyNode> nextNodes = this.expandForSingleStep(previousLeaves, h);
			
			/*
			 * If this is the last time step. Loop all nodes back to themselves for
			 * any random observation
			 * 
			 * WARNING: THIS IS REALLY A HACK TO ENSURE THAT THE RESULTING DD IS NORMALISED.
			 * LOOPING LEAVES TO THEMSELVES MAY HAVE OTHER UNFORESEEN IMPLICATIONS.
			 */
			if (h == (horizons - 1)) {
				List<List<String>> dummyObs = this.pomdp.getAllObservationsList();
				for (PolicyNode nextNode : nextNodes)
					dummyObs.stream().forEach(o -> nextNode.nextNode.put(o, nextNode.id));
			}
			this.policyNodes.addAll(nextNodes);
			previousLeaves = nextNodes;
		}
	}
	
	public void shiftIndex(int start) {
		/*
		 * Offsets the indices of policy nodes by the given arg 
		 */
		for (PolicyNode node : this.policyNodes) {
			
			/* replace for node */
			node.setId(node.id + start);
			
			/* replace for children */
			for (List<String> obs : node.nextNode.keySet()) {
				node.nextNode.replace(obs, node.nextNode.get(obs) + start);
			}
		} /* for this.policyNodes */
	}
	
	private int getNextId() {
		/*
		 * Get the next unique int ID for policy Nodes.
		 */
		this.idCounter++;
		return this.idCounter;
	}
	
	public List<String> getObsVarSequence() {
		/*
		 * Returns the observation variable names in sequence
		 * 
		 * The DDMaker implementation needs a variable ordering while making DDTree objects.
		 * This method can be useful while making a DD for policy node transitions. 
		 */
		
		List<String> obsSeq = 
				Arrays.asList(this.pomdp.obsVars).stream()
					.map(v -> v.name)
					.collect(Collectors.toList());
		
		return obsSeq;
	}
	
}
