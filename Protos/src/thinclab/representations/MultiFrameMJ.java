/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations;

import java.io.Serializable;
import java.lang.annotation.Inherited;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.ddinterface.DDMaker;
import thinclab.ddinterface.DDTree;
import thinclab.ddinterface.DDTreeLeaf;
import thinclab.decisionprocesses.DecisionProcess;
import thinclab.legacy.DD;
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
	 * TODO: don't try to subclass MJ now. Finish API first and then later try to
	 * write a common interface which both can implement.
	 */

	private static final long serialVersionUID = -4213555463993538751L;

	/* store a list of all Mjs */
	public HashMap<Integer, MJ> MJs = new HashMap<Integer, MJ>();

	/* store nodeMaps and edge Maps of all frames */
	public HashMap<Integer, HashMap<Integer, PolicyNode>> idToNodeMap = new HashMap<Integer, HashMap<Integer, PolicyNode>>();

	public HashMap<Integer, HashMap<Integer, HashMap<List<String>, Integer>>> edgeMap = new HashMap<Integer, HashMap<Integer, HashMap<List<String>, Integer>>>();

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

			/* build each tree */
			this.MJs.get(frameID).buildTree();

			/* add node map references */
			this.idToNodeMap.put(frameID, this.MJs.get(frameID).idToNodeMap);

			/* add edge map references */
			this.edgeMap.put(frameID, this.MJs.get(frameID).edgeMap);
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
			nodeNamesList.addAll(this.idToNodeMap.get(frameID).keySet().stream()
					.map(i -> MJ.makeModelLabelFromNodeId(i, frameID)).collect(Collectors.toList()));

		String[] nodeNames = nodeNamesList.toArray(new String[this.idToNodeMap.size()]);

		return new StateVar("M_j", index, nodeNames);
	}

	public DD[] getAjGivenMj(DDMaker ddMaker, HashMap<String, List<String>> Ajs) {
		/*
		 * Returns the factor P(Aj | Mj) as triples of (mj, aj, probability)
		 * 
		 * This will be used to make the P(Aj | Mj) DD
		 */

		/* initialize array to store AjiGivenMj */
		DD[] PAjGivenMjDDs = new DD[Ajs.size()];

		for (int frameID : this.idToNodeMap.keySet()) {

			List<String[]> triples = new ArrayList<String[]>();

			/* Create triples for optimal actions given node */
			for (int node : this.idToNodeMap.get(frameID).keySet()) {

				/* Get optimal action at node */
				String optimal_action = this.idToNodeMap.get(frameID).get(node).actName;

				for (String aj : Ajs.get("A_j/" + frameID)) {

					List<String> triple = new ArrayList<String>();

					/* Add mj */
					triple.add(MJ.makeModelLabelFromNodeId(node, frameID));

					/* Add action */
					triple.add(aj);

					/* Give 99% prob for performing optimal action */
					if (optimal_action.contentEquals(aj))
						triple.add("1.0");

					/* small but finite probability for non optimal behavior */
					else
						triple.add("0.0"); /* 0 probability for non optimal actions */

					triples.add(triple.toArray(new String[triple.size()]));
				}
			} /* for currentNodes */

			DDTree PAjGivenMjTree = 
					ddMaker.getDDTreeFromSequence(
							new String[] { "M_j", "A_j/" + frameID }, 
							triples.stream().toArray(String[][]::new));

			for (String child : PAjGivenMjTree.children.keySet()) {
				LOGGER.debug(child);
				if (DecisionProcess.getFrameIDFromVarName(child) != frameID) {
					LOGGER.debug(child + " != " + frameID);
					try {
						PAjGivenMjTree.setDDAt(child, new DDTreeLeaf(1.0 / Ajs.get("A_j/" + frameID).size()));
						LOGGER.debug(PAjGivenMjTree.children.get(child));
					}

					catch (Exception e) {
						e.printStackTrace();
						System.exit(-1);
					}
				}
			}
			LOGGER.debug("for frame " + frameID + PAjGivenMjTree);
			PAjGivenMjDDs[frameID] = OP.reorder(PAjGivenMjTree.toDD());
		}

//		DDTree PAjGivenMjDD = ddMaker.getDDTreeFromSequence(new String[] {"M_j"});
//		
//		for (DDTree PAjMj : PAjGivenMjDDs) {
//			
//			for (String child : PAjGivenMjDD.children.keySet()) {
//				
//				int frame = DecisionProcess.getFrameIDFromVarName(child);
//				
//				try {
//					PAjGivenMjDD.setDDAt(child, PAjGivenMjDDs[frame].children.get(child).getCopy());
//				} 
//				
//				catch (Exception e) {
//					
//					e.printStackTrace();
//					System.exit(-1);
//				}
//			}
//		}
//		
//		DD f = OP.reorder(PAjGivenMjDD.toDD());
//		
//		LOGGER.debug(f.toDDTree());
//		return f;

		return PAjGivenMjDDs;
	}

	public void makeAllObsCombinations(List<StateVar> obsJVars) {
		/*
		 * Makes all possible combinations of observation values
		 */
		this.obsCombinations = this.recursiveObsCombinations(obsJVars);
		LOGGER.debug("All possible Oj are " + this.obsCombinations);

		this.obsJVars = obsJVars;
		LOGGER.debug("Oj var sequence is " + this.obsJVars);
	}

	public String[][] getMjTransitionTriples() {
		/*
		 * Returns policy node transitions as triples of (start, obs, end)
		 * 
		 * Useful for making DDTree objects of OpponentModel and eventually making a
		 * symbolic perseus DD.
		 */

		String[][] triples = null;

		for (int frameID : this.idToNodeMap.keySet()) {

			String[][] frameTriples = this.MJs.get(frameID).getMjTransitionTriples();

			if (triples == null)
				triples = frameTriples;

			else
				triples = ArrayUtils.addAll(triples, frameTriples);
		}

		/* convert list to array and return */
		return triples;
	}

	// --------------------------------------------------------------------------------------

	private void recursiveObsGen(List<List<String>> obsComb, List<StateVar> obsVars, List<String> obsVector,
			int finalLen, int varIndex) {
		/*
		 * Recursively generates a list of all possible combinations of values of the
		 * observation variables
		 */

		if (varIndex < obsVars.size()) {

			if (obsVector.size() == finalLen) {
				obsComb.add(obsVector);
			}

			else {

				List<String> obsVectorCopy = new ArrayList<String>(obsVector);
				StateVar obs = obsVars.get(varIndex);
				for (int i = 0; i < obs.valNames.length; i++) {
					List<String> anotherObsVecCopy = new ArrayList<String>(obsVectorCopy);
					anotherObsVecCopy.add(obs.valNames[i]);
					this.recursiveObsGen(obsComb, obsVars, anotherObsVecCopy, finalLen, varIndex + 1);
				}
			}

		}

		else {
			obsComb.add(obsVector);
		}
	} // private void recursiveObsGen

	public List<List<String>> recursiveObsCombinations(List<StateVar> obsVars) {
		/*
		 * Driver program for generating observations recursively
		 */
		int finalLen = obsVars.size();
		List<String> obsVec = new ArrayList<String>();
		List<List<String>> obsComb = new ArrayList<List<String>>();

		recursiveObsGen(obsComb, obsVars, obsVec, finalLen, 0);

		return obsComb;
	}

	// ---------------------------------------------------------------------------------------

	public static int getFrameIDFromModel(String modelLabel) {
		/*
		 * Splits the model label to find out frameID
		 */
		return Integer.valueOf(modelLabel.split("/")[1].split("_")[0]);
	}
}
