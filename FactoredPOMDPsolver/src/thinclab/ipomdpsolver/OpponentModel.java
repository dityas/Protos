/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.OP;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.StateVar;
import thinclab.utils.BeliefTreeTable;
import thinclab.utils.GraphStorage;
import thinclab.utils.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import thinclab.domainMaker.ddHelpers.DDMaker;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.policyhelper.BeliefTree;
import thinclab.policyhelper.PolicyNode;

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
	public List<String> currentRoots = new ArrayList<String>();
	public HashSet<String> currentNodes = new HashSet<String>();
	
	/*
	 * Previous belief over Mj
	 * 
	 * When transforming Mj belief to a new belief space, instead of assigning a uniform
	 * distribution over all nodes, use the probabilities from the previous belief
	 */
	public HashMap<String, Float> previousMjBeliefs = new HashMap<String, Float>();
	
	/*
	 * Connection objects and variables for db handling
	 */
//	private BeliefTreeTable localStorage = new BeliefTreeTable();
	private GraphStorage localStorage = new GraphStorage();
	
	/*
	 * We will be tracking the position in the policy tree using a time step counter.
	 */
	public int T = 0;
	
	/* look ahead time steps */
	private int t = -1;
	
	// ------------------------------------------------------------------------------------------
	
	public OpponentModel(List<POMDP> frames, int horizon) {
		/*
		 * Initialize from <belief, frame> combination
		 */
		this.logger.info(
				"Initializing Opponent Model for a total of " + horizon + " time steps");
		
		for (POMDP frame: frames) {
			
			/* Make policy tree for limited horizons */
			BeliefTree T = frame.getBeliefTree(horizon);
			
			/*
			 * Start indexing nodes incrementally so that each node has a unique index
			 * even if it is from different trees
			 */
			T.shiftIndex(nodesList.size());
			
			/* Add nodes to */
			nodesList.addAll(T.policyNodes);
			
			this.logger.info("Added " + T.policyNodes.size() + " PolicyNodes to Opponent Model");
			this.logger.info("Opponent Model now contains " + this.nodesList.size() + " PolicyNodes");
		}

		this.storeOpponentModel();
		
		/* populate current roots as nodes at time step 0 */
		this.currentRoots = 
				this.localStorage.getBeliefIDsAtTimeSteps(0, 1).stream()
					.map(i -> "m" + i)
					.collect(Collectors.toList());
	}
	
	// ------------------------------------------------------------------------------
	
	public void storeOpponentModel() {
		/*
		 * Flattens the trees into triples and stores them into an in memory db
		 */
		
		this.nodesList.forEach(n -> {
			
			/* store belief */
			this.localStorage.insertNewBelief(
					n.id, 
					n.H, 
					n.factoredBelief.toString(), 
					n.actName);

			/* store edges */
			n.nextNode.forEach((k, v) -> {
				
				this.localStorage.insertNewEdge(
						n.id, 
						v, 
						k.remove(0), /* Extract action */ 
						String.join(",", k));
			});
		});
	}
	
	// -------------------------------------------------------------------------------------
	
	public void buildLocalModel(int lookAheadTime) {
		/*
		 * Builds the Mj variable domain, and transition function based on current time step in
		 * the policy tree
		 */
		
		this.logger.fine("Building local model starting from T=" + this.T);
		
		/* Collect Nodes for current H */
		this.currentNodes = (HashSet<String>)
				this.localStorage.getChildrenStartingFrom(
						this.currentRoots.stream()
							.map(i -> this.getNodeId(i))
							.collect(Collectors.toList()), 
						lookAheadTime).stream()
							.map(n -> "m" + n).collect(Collectors.toSet());
		
		this.logger.fine("Traversed nodes are : " + this.currentNodes);
	}
	
	public void clearCurrentContext() {
		/*
		 * Clears the data structures representing currently visited nodes
		 */
		this.currentNodes.clear();
		this.currentRoots.clear();
	}
	
	// -----------------------------------------------------------------------------
	
	public void storePreviousBeliefValues(HashMap<String, Float> previousBelief) {
		/*
		 * Store previous non zero beliefs to create initial belief over the transformed
		 * belief state
		 */
		this.previousMjBeliefs.clear();
		this.previousMjBeliefs = previousBelief;
	}
	
	public void step(HashMap<String, Float> previousBelief, int lookAhead) {
		/*
		 * Moves to the next time step
		 */
		
		this.storePreviousBeliefValues(previousBelief);
		
		/* clear current context */
		this.clearCurrentContext();
		
		/* add new roots as previous child nodes */
		this.currentRoots.addAll(this.previousMjBeliefs.keySet());
		
		this.logger.fine("Cached previous belief and added non zero nodes "
				+ this.currentRoots + " to current roots");
		
		this.T += 1;
		this.logger.info("Opponent Model currently tracking time step " + this.T);
		
		this.buildLocalModel(lookAhead);
	}
	
	public DDTree getOpponentModelInitBelief(DDMaker ddMaker) {
		/*
		 * Constructs an initial belief DDTree based on the current roots
		 */
		this.logger.info("Making initial belief for current opponent model traversal");
		DDTree beliefMj = ddMaker.getDDTreeFromSequence(new String[] {"M_j"});
		
		if (this.previousMjBeliefs.size() == 0) {
			
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
		}
		
		/* else use previous belief values */
		else {
			
			for (String node : this.previousMjBeliefs.keySet()) {
				
				try {
					beliefMj.setValueAt(node, this.previousMjBeliefs.get(node).floatValue());
				} 
				
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
	
	public DD getAjFromMj(DDMaker ddMaker, List<String> Aj) {
		/*
		 * Returns the factor P(Aj | Mj) as triples of (mj, aj, probability)
		 * 
		 * This will be used to make the P(Aj | Mj) DD
		 */
		List<String[]> triples = new ArrayList<String[]>();

		/* Create triples for optimal actions given node */
		for (String node : this.currentNodes) {
			
			/* Get optimal action at node */
			String optimal_action = 
					this.localStorage.getActionForBelief(this.getNodeId(node));
			
			for (String aj : Aj) {
				
				List<String> triple = new ArrayList<String>();
				
				/* Add mj */
				triple.add(node);
				
				/* Add action */
				triple.add(aj);
				
				/* Give 99% prob for performing optimal action */
				if (optimal_action.contentEquals(aj)) triple.add("1.0");
				
				/* small but finite probability for non optimal behavior */
				else triple.add("0.0"); /* 0 probability for non optimal actions */
				
				triples.add(triple.toArray(new String[triple.size()]));
			}
		} /* for currentNodes */
		
		/* Get default DDTree for P(Aj | Mj) */
		DDTree PAjMj = 
				ddMaker.getDDTreeFromSequence(
						new String[] {"M_j", "A_j"},
						triples.toArray(new String[triples.size()][]));
		
		this.logger.fine("P(Aj | Mj) DD is " + PAjMj);
		return OP.reorder(PAjMj.toDD());
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
		
		for (String node : this.currentNodes) {
			
			String[][] triplesFromNode = 
					this.localStorage.getEdgeTriplesFromBeliefId(
							this.getNodeId(node));
			
			for (String[] triple : triplesFromNode) {
				
				if (!this.currentNodes.contains(triple[triple.length - 1]))
					triple[triple.length - 1] = triple[0];
				
				/* Add transition probability */
				triple = ArrayUtils.add(triple, "1.0");
				
				triples.add(triple);
			}
		}
		
		/* convert list to array and return */
		return triples.toArray(new String[triples.size()][]);
	}
	
	// --------------------------------------------------------------------------------------
	
	public String getOptimalActionAtNode(String node) {
		/*
		 * Returns j's optimal action at the belief point at node
		 */
		return this.localStorage.getActionForBelief(this.getNodeId(node));
	}
	
	public String getBeliefTextAtNode(String node) {
		/*
		 * Returns j's beliefs at node
		 * 
		 * Note that this method only returns the string representation and not the actual
		 * usable belief
		 */
		return this.localStorage.getBeliefTextForBelief(this.getNodeId(node));
	}
	
//	public String[] getDebugTraces() {
//		/*
//		 * Back traces current non zero Mj beliefs for debugging
//		 */
//		int[] beliefIds = 
//				this.previousMjBeliefs.keySet().stream()
//					.map(b -> this.getNodeId(b))
//					.mapToInt(Integer::intValue).toArray();
//		
//		return this.localStorage.getBackTraceDebugBanner(beliefIds);
//	}
	
	public List<String> getCurrentRoots() {
		
		return this.currentRoots;
	}
	
//	public BeliefTreeTable getLocalStorage() {
//		/*
//		 * Getter for the storage table
//		 */
//		return this.localStorage;
//	}
	
	private int getNodeId(String node) {
		/*
		 * Converts nodes names into integer IDs as used in the database
		 */
		return new Integer(node.substring(1));
	}

}
