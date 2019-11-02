/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import thinclab.legacy.StateVar;

/*
 * @author adityas
 *
 */
public class MultiFrameMJ {
	
	/*
	 * Mj abstraction for IPOMDPs with multiple lower level frames
	 * 
	 * TODO: don't try to subclass MJ now. Finish API first and then later try to write
	 * a common interface which both can implement.
	 */

	private static final long serialVersionUID = -4213555463993538751L;

	/* store a list of all Mjs */
	public HashMap<Integer, MJ> MJs = new HashMap<Integer, MJ>();
	public HashMap<Integer, HashMap<Integer, PolicyNode>> idToNodeMap = 
			new HashMap<Integer, HashMap<Integer, PolicyNode>>();
	public HashMap<Integer, HashMap<Integer, HashMap<List<String>, Integer>>> edgeMap = 
			new HashMap<Integer, HashMap<Integer, HashMap<List<String>, Integer>>>();
	
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
	
	public StateVar getOpponentModelStateVar(int index) {
		/*
		 * Collect all possible models from lower level MJs and accumulate into a variable 
		 */
		
		List<String> nodeNamesList = new ArrayList<String>();
		
		/* for all frames */
		for (int frameID : this.MJs.keySet())
			nodeNamesList.addAll(
					this.idToNodeMap.get(frameID).keySet().stream()
						.map(i -> MJ.makeModelLabelFromNodeId(i, frameID))
						.collect(Collectors.toList()));
		
		String[] nodeNames = 
				nodeNamesList.toArray(new String[this.idToNodeMap.size()]);
		
		return new StateVar("M_j", index, nodeNames);
	}
	
	public static int getFrameIDFromModel(String modelLabel) {
		/*
		 * Splits the model label to find out frameID
		 */
		return Integer.valueOf(modelLabel.split("/")[1].split("_")[0]);
	}
}
