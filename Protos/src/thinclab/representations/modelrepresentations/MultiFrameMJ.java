/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations.modelrepresentations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import thinclab.ddinterface.DDMaker;
import thinclab.ddinterface.DDTree;
import thinclab.ddinterface.DDTreeLeaf;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.legacy.StateVar;

/*
 * @author adityas
 *
 */
public class MultiFrameMJ implements Serializable, LowerLevelModel {

	/*
	 * Mj abstraction for IPOMDPs with multiple lower level frames
	 * 
	 */

	private static final long serialVersionUID = -4213555463993538751L;

	/* store a list of all Mjs */
	public HashMap<Integer, MJ> MJs = new HashMap<Integer, MJ>();

	/* store nodeMaps and edge Maps of all frames */
//	public HashMap<Integer, HashMap<Integer, PolicyNode>> idToNodeMap = 
//			new HashMap<Integer, HashMap<Integer, PolicyNode>>();
//
//	public HashMap<Integer, HashMap<Integer, HashMap<List<String>, Integer>>> edgeMap = 
//			new HashMap<Integer, HashMap<Integer, HashMap<List<String>, Integer>>>();

	/* compute all possible obs combinations at once and store them */
	public List<List<String>> obsCombinations;
	public List<StateVar> obsJVars;

	/* keep track of current time step */
	public int T = 0;

	private static final Logger LOGGER = Logger.getLogger(MultiFrameMJ.class);

	// -----------------------------------------------------------------------------------

	public MultiFrameMJ(Collection<MJ> MJs) {
		/*
		 * Set required attributes
		 */

		/* set MJs */
		for (MJ mj : MJs)
			this.MJs.put(mj.solver.f.frameID, mj);

		LOGGER.debug("Multi frame MJ initialized");

		/* build tree for initial stuff */
		this.buildTree();
	}

	// -----------------------------------------------------------------------------------

	public void buildTree() {
		/*
		 * Override to build all lower level trees
		 */

		for (Integer frameID : this.MJs.keySet()) {

			LOGGER.debug("Building look ahead tree for frame " + frameID);
			
			Global.clearHashtables();
			this.MJs.get(frameID).f.setGlobals();
			
			/* build each tree */
			this.MJs.get(frameID).buildTree();

			/* add node map references */
//			this.idToNodeMap.put(frameID, this.MJs.get(frameID).idToNodeMap);

			/* add edge map references */
//			this.edgeMap.put(frameID, this.MJs.get(frameID).edgeMap);
		}
	}

	// -------------------------------------------------------------------------------------

	public StateVar getOpponentModelStateVar(int index) {
		/*
		 * Collect all possible models from lower level MJs and accumulate into a
		 * variable
		 */

		List<String> nodeNamesList = new ArrayList<String>();

		/* for all frames */
		for (int frameID : this.MJs.keySet())
			nodeNamesList.addAll(this.MJs.get(frameID).getAllNodeIds().stream()
					.map(i -> MJ.makeModelLabelFromNodeId(i, frameID)).collect(Collectors.toList()));

		String[] nodeNames = nodeNamesList.stream().toArray(String[]::new);

		return new StateVar("M_j", index, nodeNames);
	}
	
	public DD getAjGivenMj(DDMaker ddMaker, List<String> Aj) {
		return this.getAjGivenMj(ddMaker, Aj, 0.0f);
	}

	public DD getAjGivenMj(DDMaker ddMaker, List<String> Aj, float p) {
		/*
		 * Returns the factor P(Aj | Mj) as triples of (mj, aj, probability)
		 * 
		 * This will be used to make the P(Aj | Mj) DD
		 */
		
		List<String[]> triples = new ArrayList<String[]>();
		
		/* build factors for each frame */
		for (int frameID : this.MJs.keySet()) {
			
			/* Create triples for optimal actions given node */
			for (int node : this.MJs.get(frameID).getAllNodeIds()) {

				/* Get optimal action at node */
				String optimal_action = this.MJs.get(frameID).getPolicyNode(node).getActName();
//				int oNode = this.MJs.get(frameID).
				
				/*
				 * For aj depending on mj, P(OPT(Aj) at mj | mj) = 1
				 */
				for (String aj : Aj) {

					List<String> triple = new ArrayList<String>();

					/* Add mj */
					triple.add(MJ.makeModelLabelFromNodeId(node, frameID));

					/* Add action */
					triple.add(aj);

					/* Give 99% prob for performing optimal action */
					if (optimal_action.contentEquals(aj))
						triple.add(Double.toString(p));

					/* small but finite probability for non optimal behavior */
					else
						triple.add(Double.toString((1.0 - p) / (double) (Aj.size() - 1))); /* 0 probability for non optimal actions */

					triples.add(triple.toArray(new String[triple.size()]));
				}
			} /* for currentNodes */
			
		}
		
		DDTree PAjGivenMjTree = 
				ddMaker.getDDTreeFromSequence(
						new String[] { "M_j", "A_j"}, 
						triples.stream().toArray(String[][]::new));

		return OP.reorder(PAjGivenMjTree.toDD());
	}
	
	public DD getThetajGivenMj(DDMaker ddMaker, List<String> Thetaj) {
		/*
		 * Returns the factor P(Aj | Mj) as triples of (mj, aj, probability)
		 * 
		 * This will be used to make the P(Aj | Mj) DD
		 */
		
		List<String[]> triples = new ArrayList<String[]>();
		
		/* build factors for each frame */
		for (int frameID : this.MJs.keySet()) {
			
			/* Create triples for optimal actions given node */
			for (int node : this.MJs.get(frameID).getAllNodeIds()) {

				/*
				 * For aj depending on mj, P(OPT(Aj) at mj | mj) = 1
				 */
				for (String thetaj : Thetaj) {

					List<String> triple = new ArrayList<String>();

					/* Add mj */
					triple.add(MJ.makeModelLabelFromNodeId(node, frameID));

					/* Add action */
					triple.add(thetaj);

					/* Give 99% prob for performing optimal action */
					if (IPOMDP.getFrameIDFromVarName(thetaj) == frameID)
						triple.add("1.0");

					/* small but finite probability for non optimal behavior */
					else
						triple.add("0.0"); /* 0 probability for non optimal actions */

					triples.add(triple.toArray(new String[triple.size()]));
				}
			} /* for currentNodes */
			
		}
		
		DDTree PThetajGivenMjTree = 
				ddMaker.getDDTreeFromSequence(
						new String[] { "M_j", "Theta_j"},
						triples.stream().toArray(String[][]::new));

		return OP.reorder(PThetajGivenMjTree.toDD());
	}

	public DD getPMjPGivenMjOjPAj(
			DDMaker ddMaker, 
			List<String> Aj, 
			List<String> OjNames) {
		/*
		 * Make P(Mj'| Mj, Oj', Aj)
		 */

		/* store independently built DDTrees */
		HashMap<Integer, DDTree> individualMjTrees = new HashMap<Integer, DDTree>();

		/*
		 * construct the factor from triples of relevant frames
		 * So for Mj1 transitions, let Mj1 construct parts of the factor which has Mj1 nodes
		 * and similar for all other frames.
		 */
		for (int frameID : this.MJs.keySet()) {

			DDTree t = 
					this.MJs.get(frameID).getPMjPGivenMjOjAj(ddMaker, Aj, OjNames);

			individualMjTrees.put(frameID, t);
		}
		
		/* Assemble the full Mj transition DD */
		DDTree PMjPGivenMjOjAj = ddMaker.getDDTreeFromSequence(new String[] {"M_j'"});
		
		for (String mj : PMjPGivenMjOjAj.children.keySet()) {
			
			int f = IPOMDP.getFrameIDFromVarName(mj);
			
			try {
				PMjPGivenMjOjAj.setDDAt(
						mj, individualMjTrees.get(f).children.get(mj));
			} 
			
			catch (Exception e) {
				LOGGER.error("While assembling factor P(Mj'| Mj, Oj', Aj)");
				e.printStackTrace();
				System.exit(-1);
			}
		}
		
		return OP.reorder(PMjPGivenMjOjAj.toDD());
	}
	
	public DDTree getMjInitBelief(DDMaker ddMaker, DDTree prior) {
		/*
		 * Constructs an initial belief DDTree based on the current roots
		 */
		LOGGER.debug("Making initial belief for current opponent model traversal");
		DDTree beliefMj = ddMaker.getDDTreeFromSequence(new String[] {"M_j"});
		
		if (prior == null) {
			
			int mjCount = 0;
			for (int frame : this.MJs.keySet())
				mjCount +=
					this.MJs.get(frame).getAllRootIds().size();
			
			for (int frameID : this.MJs.keySet()) {
				
				List<Integer> roots = 
						this.MJs.get(frameID).getAllRootIds();
				
				/* Uniform distribution over all current roots */
				for (int node : roots) {
					
					try {
						beliefMj.setValueAt(
								MJ.makeModelLabelFromNodeId(node, frameID), 
								(1.0 / mjCount));
					} 
					
					catch (Exception e) {
						LOGGER.error("While making initial Mj: " + e.getMessage());
						e.printStackTrace();
					}
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

	// --------------------------------------------------------------------------------------
	
	public void step(DD belief, int lookAhead, HashSet<String> nonZeroMj) {
		/*
		 * Moves to the next time step
		 */
		
		for (int frameID : this.MJs.keySet()) {
			
			/* Set the selected frame's context */
			this.MJs.get(frameID).f.setGlobals();
			this.MJs.get(frameID).step(belief, lookAhead, nonZeroMj);
		}
		
		this.T += 1;
		LOGGER.info("Mj currently tracking time step " + this.T);
		
		this.buildTree();
	}
	
	// --------------------------------------------------------------------------------------
	
	public String getOptimalActionAtNode(String node) {
		/*
		 * Returns j's optimal action at the belief point at node
		 */
		int frame = IPOMDP.getFrameIDFromVarName(node);
		
		return this.MJs.get(frame).getPolicyNode(MJ.getNodeId(node)).getActName();
	}
	
	public String getBeliefTextAtNode(String node) {
		/*
		 * Returns j's beliefs at node
		 * 
		 * Note that this method only returns the string representation and not the actual
		 * usable belief
		 */
		
		int frame = IPOMDP.getFrameIDFromVarName(node);
		
		return this.MJs.get(frame).getPolicyNode(MJ.getNodeId(node)).getsBelief();
	}

	// ---------------------------------------------------------------------------------------

	public static int getFrameIDFromModel(String modelLabel) {
		/*
		 * Splits the model label to find out frameID
		 */
		return Integer.valueOf(modelLabel.split("/")[1].split("_")[0]);
	}
}
