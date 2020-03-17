/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations.modelrepresentations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.ddinterface.DDMaker;
import thinclab.ddinterface.DDTree;
import thinclab.ddinterface.DDTreeLeaf;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.legacy.DD;
import thinclab.legacy.OP;
import thinclab.legacy.StateVar;
import thinclab.representations.belieftreerepresentations.OptimalDynamicBeliefTree;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class MJ extends OptimalDynamicBeliefTree {
	
	/*
	 * Contains the opponent's model and functions which enable IPOMDP objects
	 * to talk with Mj DDs
	 */
	
	private static final long serialVersionUID = -3580584950505049855L;
	
	/* keep track of root time step */
	int T = 0;
	
	private static final Logger LOGGER = Logger.getLogger(MJ.class);
	
	// ------------------------------------------------------------------------------------
	
	public MJ(BaseSolver solver, int lookAhead) {
		
		super(solver, lookAhead);
		LOGGER.debug("Building MJ for look ahead of " + this.maxT);
	}

	// -------------------------------------------------------------------------------------
	
	public void step(DD belief, int lookAhead, HashSet<String> nonZeroMj) {
		/*
		 * Moves to the next time step
		 */
		
		/* add new roots as previous child nodes */
		this.pruneZeroProbabilityLeaves(nonZeroMj);
		
		if (this.leafNodes.size() > 0)
			this.currentPolicyNodeCounter = Collections.max(this.leafNodes) + 1;
		
		else LOGGER.info("No models from frame " + this.solver.f.frameID);
		
		LOGGER.debug("Cached previous belief and added non zero nodes "
				+ this.leafNodes + " to current roots");
		
		this.T += 1;
		LOGGER.info("Opponent Model currently tracking time step " + this.T);

	}
	
	public StateVar getOpponentModelStateVar(int index) {
		/*
		 * Makes a StateVar object for the opponent model
		 * 
		 * Makes a new random variable M. The possible values taken by M are the
		 * policy nodes in the opponent model policy trees. 
		 */
		List<String> valNames = this.getNodeLabels(); 
//				this.idToNodeMap.keySet().stream()
//					.map(i -> "m" + i)
//					.collect(Collectors.toList()); 
		
		String[] nodeNames = 
				valNames.toArray(new String[this.getNumNodes()]);
		
		return new StateVar("M_j", index, nodeNames);
	}
	
	public DD getAjGivenMj(DDMaker ddMaker, List<String> Aj) {
		/*
		 * Returns the factor P(Aj | Mj) as triples of (mj, aj, probability)
		 * 
		 * This will be used to make the P(Aj | Mj) DD
		 */
		List<String[]> triples = new ArrayList<String[]>();

		/* Create triples for optimal actions given node */
		for (int node : this.getAllNodeIds()) {
			
			/* Get optimal action at node */
			String optimal_action = 
					this.getPolicyNode(node).getActName();
			
			for (String aj : Aj) {
				
				List<String> triple = new ArrayList<String>();
				
				/* Add mj */
				triple.add(this.makeModelLabelFromNodeId(node));
				
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
		
		DD PAjMjDD = OP.reorder(PAjMj.toDD()); 
		LOGGER.debug("P(Aj | Mj) DD contains variables " 
				+ Arrays.toString(PAjMjDD.getVarSet()));
		
		return PAjMjDD;
	}
	
	public String[][] getMjTransitionTriples() {
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
//		LOGGER.debug("Edges are " + this.getAllEdgeIds());
		for (int node : this.getAllNodeIds()) {
//			LOGGER.debug("Node is " + this.getPolicyNode(node));
			String[][] triplesFromNode = null;
			
			if (this.containsEdge(node))
				triplesFromNode = this.edgeEntryToStringTriples(node);
				
			else
				triplesFromNode = this.getLeafNodeEdges(node);
			
			for (String[] triple : triplesFromNode) {
				
				/* Add transition probability */
				triple = ArrayUtils.add(triple, "1.0");
//				LOGGER.debug("Constructed triple is " + Arrays.toString(triple));
				triples.add(triple);
			}
		}
		
		/* convert list to array and return */
		return triples.toArray(new String[triples.size()][]);
	}
	
	public DDTree getPMjPGivenMjOjAj(
			DDMaker ddMaker, 
			List<String> Ajs,
			List<String> Ojs) {
		/*
		 * Construct the factor P(Mj'| Mj, Oj, Aj)
		 */
		
		String[][] triples = this.getMjTransitionTriples();
		
		String[] varSequence = new String[] {"M_j'", "M_j"};
		varSequence = ArrayUtils.add(varSequence, "A_j");
		varSequence = 
				ArrayUtils.addAll(
						varSequence, 
						Ojs.stream()
							.map(oj -> oj + "'").toArray(String[]::new));
		
//		for (int r = 0; r < triples.length; r++) {
//			LOGGER.debug("triple " + r + " is " + Arrays.toString(triples[r]));
//		}
		
		DDTree tree = ddMaker.getDDTreeFromSequence(varSequence, triples);
		
		/* set probs for mjs of other frames */
		for (String child : tree.children.keySet()) {
//			LOGGER.debug("child is " + child);
			if (IPOMDP.getFrameIDFromVarName(child) != this.solver.f.frameID) {
				
				try {
					tree.setDDAt(child, new DDTreeLeaf(0.0));
				} 
				
				catch (Exception e) {
					LOGGER.error("While assigning 0 prob to " + child);
					e.printStackTrace();
					System.exit(-1);
				}
			}
			
			try {
				DDTree independentsFactor = tree.atChild(child);
				
				for (String childT : independentsFactor.children.keySet()) {
					
					int f = IPOMDP.getFrameIDFromVarName(childT);
					
					if (f == this.solver.f.frameID) continue;
					
					else {
						
						independentsFactor.setDDAt(
								childT, 
								new DDTreeLeaf(0.0));
					}
				}
			} 
			
			catch (Exception e) {
				LOGGER.error("While visiting value " + child + " M_j" + this.solver.f.frameID);
				e.printStackTrace();
				System.exit(-1);
			}
				
		}
		
		return tree;
	}
	
	public DDTree getMjInitBelief(DDMaker ddMaker, DDTree prior) {
		/*
		 * Constructs an initial belief DDTree based on the current roots
		 */
		LOGGER.debug("Making initial belief for current opponent model traversal");
		DDTree beliefMj = ddMaker.getDDTreeFromSequence(new String[] {"M_j"});
		
		if (prior == null) {
			
			List<Integer> roots = this.getAllRootIds(); 
//					this.idToNodeMap.values().stream()
//						.filter(n -> (n.getH() == 0))
//						.map(i -> i.getId())
//						.collect(Collectors.toList());
			
			/* Uniform distribution over all current roots */
			for (int node : roots) {
				
				try {
					beliefMj.setValueAt("m" + node, (1.0 / roots.size()));
				} 
				
				catch (Exception e) {
					LOGGER.error("While making initial Mj: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		/* else use previous belief values */
		else {
			
			for (Entry<String, DDTree> entry : prior.children.entrySet()) {
				
				DDTree child = entry.getValue();
				
				/* add all non leaf vars */
				if (!child.varName.contentEquals("LeafVar"))
					beliefMj.addChild(entry.getKey(), child);
				
				else if (child.varName.contentEquals("LeafVar") 
						&& ((DDTreeLeaf) child).val != 0.0)
					beliefMj.addChild(entry.getKey(), child);
			}
			
		}
		
		LOGGER.debug("Made initial belief");
		return beliefMj;
	}
	
	public String getOptimalActionAtNode(String node) {
		/*
		 * Returns j's optimal action at the belief point at node
		 */
		return this.getPolicyNode(MJ.getNodeId(node)).getActName();
	}
	
	public String getBeliefTextAtNode(String node) {
		/*
		 * Returns j's beliefs at node
		 * 
		 * Note that this method only returns the string representation and not the actual
		 * usable belief
		 */
		return this.getPolicyNode(MJ.getNodeId(node)).getsBelief();
	}
	
	// -------------------------------------------------------------------------------------
	
	private String[][] edgeEntryToStringTriples(int nodeId) {
		/*
		 * Returns the edges starting from given nodeId as string triples
		 */
		HashMap<List<String>, Integer> edges = this.getEdges(nodeId);
		List<String[]> triples = new ArrayList<String[]>();
		
		for (Entry<List<String>, Integer> edge : edges.entrySet()) {
			
			List<String> triple = new ArrayList<String>();
			
			/* add end node */
			triple.add(this.makeModelLabelFromNodeId(edge.getValue()));
			
			/* add parent node */
			triple.add(makeModelLabelFromNodeId(nodeId));
			
			/* add edge */
			List<String> theEdge = edge.getKey();
			
			triple.addAll(theEdge);
			
			/* add to collection */
			triples.add(triple.toArray(new String[triple.size()]));
		}
		
		return triples.toArray(new String[triples.size()][]);
	}
	
	private String[][] getLeafNodeEdges(int nodeId) {
		/*
		 * Creates self looping triples for leaf nodes in the look ahead tree
		 */
		
		/* first make sure if the node is indeed a leaf */
		if (this.containsEdge(nodeId))
			LOGGER.error("NODE: " + nodeId + " is not a leaf");
		
		List<String[]> triples = new ArrayList<String[]>();
		
		for (List<String> obs : this.f.getAllPossibleObservations()) {
			
			for (String action : this.f.getActions()) {
				List<String> triple = new ArrayList<String>();
				
				/* add tail */
				triple.add(this.makeModelLabelFromNodeId(nodeId));
				
				/* add head */
				triple.add(this.makeModelLabelFromNodeId(nodeId));
				
				/* add action */
				triple.add(action);
				
				/* add all obs */
				triple.addAll(obs);
				
				/* add the triple */
				triples.add(triple.toArray(new String[triple.size()]));
			}
		}
		
		return triples.toArray(new String[triples.size()][]);
	}
	
	public String getNodeName(int nodeId) {
		/*
		 * Appends frame ID to the node name
		 */
		return "m/" + this.solver.f.frameID + "_" + nodeId;
	}
	
	// -------------------------------------------------------------------------------------
	
	public static List<Integer> getNodeIds(Collection<String> nodes) {
		/*
		 * Convert list of nodes to their integer IDs
		 */
		return nodes.stream()
				.map(n -> new Integer(MJ.getNodeId(n)))
				.collect(Collectors.toList());
	}
	
	public static int getNodeId(String node) {
		/*
		 * Converts nodes names into integer IDs as used in the database
		 */
		return new Integer(node.split("/")[1].split("_")[1]);
	}
	
	public static String makeModelLabelFromNodeId(int nodeId, int frameID) {
		/*
		 * Splits the model label to find out frameID
		 */
		return "m/" + frameID + "_" + nodeId;
	}
	
	public String makeModelLabelFromNodeId(int nodeId) {
		
		return MJ.makeModelLabelFromNodeId(nodeId, this.solver.f.frameID);
	}
}
