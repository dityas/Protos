/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policyhelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.Belief.Belief;

/*
 * @author adityas
 *
 */
public class BeliefTree extends PolicyTree {
	/*
	 * Represents belief tree for given POMDP using PolicyNodes
	 * 
	 * Unlike PolicyTree, the optimal action for PolicyNode is not used. Instead, the tree is
	 * expanded for all action observation combinations. 
	 */
	
	private static final Logger logger = Logger.getLogger(BeliefTree.class);
	
	public BeliefTree(POMDP p, int horizon) {
		/*
		 * Constructor is similar to super.
		 */
		
		/* explict call to super's constructor */
		super(p, horizon);
	}
	
	@Override
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
				for (String a : this.pomdp.A) {
					
					List<List<String>> dummyObs = this.allObsCombinations;
					for (PolicyNode nextNode : nextNodes) {
						
						for (List<String> o : dummyObs) {
							List<String> t = new ArrayList<String>(o);
							t.add(0, a);
							nextNode.nextNode.put(t, nextNode.id);
						}
					}
				}
			}
			this.policyNodes.addAll(nextNodes);
			previousLeaves = nextNodes;
		}
	}
	
	@Override
	public List<PolicyNode> expandForSingleStep(
			List<PolicyNode> previousLeaves, 
			int currentH) {
		/*
		 * Override super's expandForSingleStep to use all actions instead of just optimal ones
		 */
		this.logger.debug("Expanding from horizon " + currentH + " from " + previousLeaves.size() 
			+ " nodes");
		
		List<PolicyNode> nextNodes = new ArrayList<PolicyNode>();
		
		/*
		 * Use hashmap to index new nodes. This ensures only unique nodes are added and
		 * repeating nodes at the same horizon do not exist.
		 */
		HashMap<DD, Integer> nodeIndexMap = new HashMap<DD, Integer>();
		
		for (PolicyNode policyNode : previousLeaves) {
			
			for (String a : this.pomdp.A) {
				/*
				 * Update for each observation 
				 */
				List<List<String>> obs = this.allObsCombinations;
				for (List<String> o : obs) {
					
					DD nextBelief;
					
					try {
						nextBelief = 
								Belief.beliefUpdate(
										this.pomdp, 
										policyNode.belief, 
										this.pomdp.findActionByName(a), 
										o.toArray(new String[o.size()]));
					}
					
					catch (ZeroProbabilityObsException e) {
						continue;
					}
					
					/* unique belief. So add it to the node index and give it a new ID */
					if (!nodeIndexMap.containsKey(nextBelief))
						nodeIndexMap.put(nextBelief, new Integer(this.getNextId()));
						
					int newNodeId = nodeIndexMap.get(nextBelief);
					
					/* first index in PolicyNode nextNode maps should be the action */
					List<String> t = new ArrayList<String>(o);
					t.add(0, a);
					
					policyNode.nextNode.put(t, newNodeId);
				}
			}
		}
		
		/* Add each unique node to next nodes */
		nodeIndexMap.forEach((k, v) -> {
			PolicyNode newNode = new PolicyNode(this.pomdp, k, currentH + 1);
			newNode.setId(v);
			nextNodes.add(newNode);
		});
		
		this.logger.debug("For H=" + currentH + " branching factor is A=" + this.pomdp.nActions 
				+ " * O=" + this.allObsCombinations.size() + " * n=" + previousLeaves.size()
				+ " = " + (this.pomdp.nActions*this.allObsCombinations.size()*previousLeaves.size())
				+ " points out of which " + nextNodes.size() + " are unique");
		
		return nextNodes;
	}
}
