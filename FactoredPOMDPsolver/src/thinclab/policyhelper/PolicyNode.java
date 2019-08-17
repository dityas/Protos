package thinclab.policyhelper;

import java.util.*;
import java.util.stream.Collectors;

import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.Belief.Belief;
import thinclab.symbolicperseus.Belief.BeliefTreeNode;

public class PolicyNode {
	
	int alphaId=-1;
	int actId = 1;
	public String actName = "";
	DD belief;
	public int id = -1;
	
	HashMap<String, ArrayList<Float>> factoredBelief = new HashMap<String, ArrayList<Float>>();
	Map<List<String>, Integer> nextNode = new HashMap<List<String>, Integer>();
	
	/*
	 * Children hash map for the PolicyTree API
	 */
	HashMap<List<String>, PolicyNode> nextPolicyNode = new HashMap<List<String>, PolicyNode>();
	
	Map<List<String>, Integer> compressedNextNode = new HashMap<List<String>, Integer>();
	
	public boolean startNode = false;
	public POMDP p;
	
	// ------------------------------------------------------------------------------------
	/*
	 * Constructors
	 */
	
	public PolicyNode() {
		
	}
	
	public PolicyNode(POMDP p, DD belief) {
		/*
		 * Populates the POMDP and belief fields and computes the best action given the belief
		 */
		this.p = p;
		this.belief = belief;
		
		/*
		 * Find best alphaId and action
		 */
		this.alphaId = this.p.policyBestAlphaMatch(belief, this.p.alphaVectors, this.p.policy);
		this.actId = this.p.policy[this.alphaId];
		this.actName = this.p.actions[this.actId].name;
		
		/*
		 * Fill in belief state map
		 */
		this.factoredBelief = Belief.toStateMap(this.p, this.belief);
	}
	
	public PolicyNode(POMDP p, DD belief, int level, int maxDepth) {
		/*
		 * Initialize PolicyNode for a level in the policy tree
		 */
		this(p, belief);
		
		/*
		 * Check if this is the maxDepth for the policy tree. If not, expand to children.
		 */
		if (level < maxDepth) {
			
			List<List<String>> obs = this.p.getAllObservationsList();
			Iterator<List<String>> obsIter = obs.iterator();
			while (obsIter.hasNext()) {
				List<String> o = obsIter.next();
				
				DD nextBelief = null;
				
				try {
					nextBelief = 
							Belief.beliefUpdate(
									this.p, 
									this.belief, 
									this.actId, o.toArray(new String[o.size()]));
				}
				
				catch (Exception e) {
					continue;
				}
				
				this.nextPolicyNode.put(o, new PolicyNode(this.p, nextBelief, level + 1, maxDepth));
			}
		}
	}
	
	// ------------------------------------------------------------------------------------
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setStartNode() {
		this.startNode = true;
	}
	
	// -------------------------------------------------------------------------------------
	
	public boolean shallowEquals(PolicyNode other) {
		/*
		 * Takes another policy and compares its alphaId, actId and nexNodes
		 */

		if (this.actId == other.actId && this.alphaId == other.alphaId && this.nextNode.equals(other.nextNode)) {
			return true;
		}
		
		else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "PolicyNode [ID = " + this.id
				+ " alphaId=" + this.alphaId 
				+ " actId=" + actId 
				+ ", belief=" + this.factoredBelief 
				+ ", nextNode=" + nextNode.toString() + "]\r\n";
	}
	
	public String getDotHeader(String actionName) {
		/*
		 * Return Dot node definition for current node as string
		 */
		String header = " " + this.alphaId + " ";
		header = header + "[shape=record label=\"action= " + actionName + "\"]";
		return header;
	}
	
	public void compressNextNodes() {
		/*
		 * This is just for JUNG visualization. Groups together keys which lead to common nodes
		 */
		HashMap<Integer, List<List<String>>> nextNodeToObsMap = new HashMap<Integer, List<List<String>>>();
		
		/*
		 *  populate reverse hash map
		 */
		Iterator<Map.Entry<List<String>, Integer>> nextNodeIter = this.nextNode.entrySet().iterator();
		while(nextNodeIter.hasNext()) {
			Map.Entry<List<String>, Integer> theEdge = nextNodeIter.next();
			
			Integer child = theEdge.getValue();
			// Check if node exists
			if(nextNodeToObsMap.get(theEdge.getValue()) == null) {
				nextNodeToObsMap.put(theEdge.getValue(), new ArrayList<List<String>>());
			}
			
			List<List<String>> commonObservations = nextNodeToObsMap.get(child);
			commonObservations.add(theEdge.getKey());
			nextNodeToObsMap.put(child, commonObservations);

		} // while(nextNodeIter.hasNext())
		
		/*
		 *  Loop over reverse map and add compressed edges to compressedNextNode
		 */
		Iterator<Map.Entry<Integer, List<List<String>>>> reverseMapIter = nextNodeToObsMap.entrySet().iterator();
		while (reverseMapIter.hasNext()) {
			Map.Entry<Integer, List<List<String>>> entry = reverseMapIter.next();
			Integer child = entry.getKey();
			
			List<List<String>> observationSet = entry.getValue();
			String[] compressedObs = observationSet.get(0).toArray(new String[observationSet.get(0).size()]);			
			
			/*
			 *  Iterate over common observations
			 */
			Iterator<List<String>> obsIter = observationSet.iterator();
			while (obsIter.hasNext()) {
				List<String> list = obsIter.next();
				
				/*
				 *  Check each observation
				 */
				for (int i=0; i < list.size(); i++) {
					String obs = list.get(i);
					if (obs != compressedObs[i]) {
						compressedObs[i] = "*";
					} // if
				} // for (int i=0; i < list.size(); i++)
			} // while (obsIter.hasNext())
			
			/*
			 *  Add compressed observation to compressed hash map
			 */
			this.compressedNextNode.put(Arrays.asList(compressedObs), child);
		} // while (reverseMapIter.hasNext())
		
	} // void compressNextNodes()
	
	public String getBeliefLabel() {
		/*
		 * Pretty print belief for JUNG visualizations
		 */
		String beliefLabel = "<br>------------------";
		beliefLabel = beliefLabel + "<br>BELIEF:";
		
		Iterator<Map.Entry<String, ArrayList<Float>>> belIter = this.factoredBelief.entrySet().iterator();
		while(belIter.hasNext()) {
			Map.Entry<String, ArrayList<Float>> belief = belIter.next();
			beliefLabel = beliefLabel + "<br>" + belief.getKey() + ": " + belief.getValue();
		}
		
		beliefLabel = beliefLabel + "<br>------------------";
		
		return beliefLabel;
	} // public String getBeliefLabel()
}
