package thinclab.policyhelper;

import java.util.*;
import java.util.logging.Logger;

import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.Belief.Belief;
import thinclab.utils.LoggerFactory;

public class PolicyNode {
	
	int alphaId=-1;
	int actId = 1;
	
	public String actName = "";
	public DD belief;

	public int id = -1;
	public int H = -1;
	
	private Logger logger = LoggerFactory.getNewLogger("Node");
	
	public HashMap<String, HashMap<String, Float>> factoredBelief;
	
	public Map<List<String>, Integer> nextNode = 
			new HashMap<List<String>, Integer>();
	
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
		this.factoredBelief = Belief.toStateMap(this.p, belief);
	}
	
	public PolicyNode(POMDP p, DD belief, int level, int maxDepth) {
		/*
		 * Initialize PolicyNode for a level in the policy tree
		 */
		this(p, belief);
	}
	
	public PolicyNode(POMDP p, DD belief, int horizon) {
		/*
		 * Constructor to store belief, action and horizon
		 */
		this(p, belief);
		this.H = horizon;
		
		this.logger.fine("New PolicyNode initialized with belief " + Belief.toStateMap(p, belief)
				+ " and optimal action " + this.actName + " at time step " + this.H);
	}
	
	// ------------------------------------------------------------------------------------
	
	public void setId(int id) {
		/*
		 * Setter for id
		 */
		this.id = id;
	}
	
	public void setStartNode() {
		/*
		 * Marks the node as a start node
		 */
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
		return "PolicyNode \t [ID = " + this.id
				+ " \t level=" + this.H 
				+ " \t action=" + this.actName 
				+ " \t belief=" + this.factoredBelief 
				+ " \t nextNode=" + nextNode.toString() + "]\r\n";
	}
	
	public String getDotHeader(String actionName) {
		/*
		 * Return Dot node definition for current node as string
		 */
		String header = " " + this.alphaId + " ";
		header = header + "[shape=record label=\"action= " + actionName + "\"]";
		return header;
	}
	
	public String getBeliefLabel() {
		/*
		 * Pretty print belief for JUNG visualizations
		 */
		String beliefLabel = "<br>------------------";
		beliefLabel = beliefLabel + "<br>BELIEF:";
		
		Iterator<Map.Entry<String, HashMap<String, Float>>> belIter = 
				this.factoredBelief.entrySet().iterator();
		
		while(belIter.hasNext()) {
			Map.Entry<String, HashMap<String, Float>> belief = belIter.next();
			beliefLabel = beliefLabel + "<br>" + belief.getKey() + ": " + belief.getValue();
		}
		
		beliefLabel = beliefLabel + "<br>------------------";
		
		return beliefLabel;
	} 
}
