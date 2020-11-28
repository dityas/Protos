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

import org.apache.log4j.Logger;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.representations.PersistentStructuredTree;
import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class StaticBeliefTree extends PersistentStructuredTree {
	
	/*
	 * Holds a static belief tree which is expanded to max H at once. 
	 */
	
	private static final long serialVersionUID = 359334337512902886L;
	
	/* reference for the framework and solver */
	public DecisionProcess f;
	public BaseSolver solver = null;
	
	private static final Logger logger = Logger.getLogger(StaticBeliefTree.class);
	
	// ------------------------------------------------------------------------------------
	
	public StaticBeliefTree(DecisionProcess f, int maxH) {
		
		super(100 + f.frameID, Global.storagDir);
		
		/* set attributes */
		this.f = f;
		
		if (f.getType().contentEquals("IPOMDP"))
			this.maxT = ((IPOMDP) this.f).mjLookAhead;
		
		else this.maxT = maxH;
		
		this.observations = this.f.getAllPossibleObservations();
		
		logger.debug("Initializing StaticBeliefTree for maxT " + this.maxT);
	}
	
	public StaticBeliefTree(BaseSolver solver, int maxH) {
		
		this(solver.f, maxH);
		this.solver = solver;
	}
	
	public StaticBeliefTree() {
		super(-1, null);
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
					
					DD belief = this.getPolicyNode(parentId).getBelief();
					
					this.makeNextBeliefNode(
							parentId, 
							belief, f, action, this.solver, obs, nodeMap, null, T);
			
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
			node.setId(i);
			node.setBelief(this.f.getInitialBeliefs().get(i));
			node.setH(0);
			node.setsBelief(this.f.getBeliefString(node.getBelief()));
			
			/* record start node */
			node.setStartNode();
			
			if (this.solver != null)
				node.setActName(this.solver.getActionForBelief(node.getBelief()));
			
			else 
				node.setActName("");
				
			this.putPolicyNode(i, node);
			
			this.currentPolicyNodeCounter += 1;
		}
		
		for (int t = 0; t < this.maxT; t++) {
			
			List<Integer> nextNodes = this.getNextPolicyNodes(prevNodes, t);
			prevNodes = nextNodes;
		}
	}

}
