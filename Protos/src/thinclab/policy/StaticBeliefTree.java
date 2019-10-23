/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;

import thinclab.belief.Belief;
import thinclab.belief.InteractiveBelief;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.frameworks.Framework;
import thinclab.frameworks.IPOMDP;
import thinclab.frameworks.POMDP;
import thinclab.legacy.DD;
import thinclab.policyhelper.PolicyNode;

/*
 * @author adityas
 *
 */
public class StaticBeliefTree extends StructuredTree {
	
	/*
	 * Holds a static belief tree which is expanded to max H at once. 
	 */
	
	/* reference for the framework */
	Framework f;
	
	private static final Logger logger = Logger.getLogger(StaticBeliefTree.class);
	
	// ------------------------------------------------------------------------------------
	
	public StaticBeliefTree(Framework f, int maxH) {
		
		/* set attributes */
		this.f = f;
		
		if (f instanceof IPOMDP)
			this.maxT = ((IPOMDP) this.f).mjLookAhead;
		
		else this.maxT = maxH;
		
		this.observations = this.f.getAllPossibleObservations();
		
		logger.debug("Initializing StaticBeliefTree for maxT " + this.maxT);
	}
	
	// -------------------------------------------------------------------------------------
	
	public List<Integer> getNextPolicyNodes(List<Integer> previousNodes, int T) {
		/*
		 * Compute the next PolicyNode from the list of previous PolicyNodes
		 */
		
		HashMap<DD, Integer> nodeMap = new HashMap<DD, Integer>();
		
		/* For each previous Node */
		for (int parentId : previousNodes) {
			
			/* For all combinations */
			for (List<String> obs : this.observations) {
				
				for (String action : this.f.getActions()) {
					
					DD belief = this.idToNodeMap.get(parentId).belief;
					
					this.makeNextNode(
							parentId, 
							belief, f, action, obs, nodeMap);
			
				} /* for all actions */
			} /* for all observations */
		} /* for all parents */
		
		return new ArrayList<Integer>(nodeMap.values());
	}
	
	public void buildTree() {
		/*
		 * Builds the full OnlinePolicyTree upto maxT
		 */
		
		List<Integer> prevNodes = new ArrayList<Integer>();
		
		for (int i = 0; i < this.f.getInitialBeliefs().size(); i++) {
			prevNodes.add(i);
			
			PolicyNode node = new PolicyNode();
			node.id = i;
			node.belief = this.f.getInitialBeliefs().get(i);
			
			if (this.f instanceof IPOMDP)
				node.sBelief = 
					InteractiveBelief.toStateMap(
							(IPOMDP) this.f, 
							node.belief).toString();
			
			else 
				node.sBelief =
					Belief.toStateMap((POMDP) this.f, node.belief).toString();
			
			this.idToNodeMap.put(i, node);
			
			this.currentPolicyNodeCounter += 1;
		}
		
		for (int t = 0; t < this.maxT; t++) {
			
			List<Integer> nextNodes = this.getNextPolicyNodes(prevNodes, t);
			prevNodes = nextNodes;
		}
	}

}
