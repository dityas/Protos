/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import thinclab.policy.PolicyNode;

/*
 * @author adityas
 *
 */
public class GraphStorage implements Serializable {
	
	/*
	 * Data structures for representing the graph
	 */
	private static final long serialVersionUID = -3120634348367719313L;
	
	private HashMap<Integer, PolicyNode> idToNodeMap = new HashMap<Integer, PolicyNode>();
	private HashMap<Integer, HashMap<String, Integer>> idToSuccessorsMap =
			new HashMap<Integer, HashMap<String, Integer>>();
	
	private static final Logger logger = Logger.getLogger(GraphStorage.class); 
	
	// ------------------------------------------------------------------------------------
	
	public GraphStorage() {
		
	}
	
	// ------------------------------------------------------------------------------------
	
	public void insertNewBelief(int id, int horizon, String beliefString, String action) {
		/*
		 * Inserts new belief in the idToNodeMap
		 */
		
		/*
		 * First check for clashing ids
		 * 
		 * if id is unique, make a new PolicyNode.
		 * Note that PolicyNode is being used here simply as an object to store the properties
		 * of a particular belief point given by the OpponentModel.
		 */
		if (!this.idToNodeMap.containsKey(id)) {
			this.idToNodeMap.put(
					id, 
					new PolicyNode(
							id, 
							horizon, 
							beliefString, 
							action));
			
			logger.debug("Added " + id + " to GraphStorage");
		}
		
		/* else do something to get attention */
		else logger.error("belief id clash: " + id + " already exists");
		
	}
	
	public void insertNewEdge(int pId, int cId, String action, String obs) {
		/*
		 * Insert edge into the idToSuccessorMap
		 */
		
		/* check if observation to successor map exists */
		if(!this.idToSuccessorsMap.containsKey(pId))
			this.idToSuccessorsMap.put(pId, new HashMap<String, Integer>());
		
		if (action.length() >= 1)
			this.idToSuccessorsMap.get(pId).put(action + "," + obs, cId);
		
		else this.idToSuccessorsMap.get(pId).put(obs, cId);
		
		logger.debug("Added edge " + pId + " -[" + action + "]-[" + obs + "]-> " + cId);
	}
	
	// ----------------------------------------------------------------------------------------
	
	public String getActionForBelief(int belId) {
		/*
		 * Look for best action at for the belief
		 */
		
		if(!this.idToNodeMap.containsKey(belId)) {
			logger.error("No node named m" + belId + " in " + this.idToNodeMap);
			return "";
		}
		
		return this.idToNodeMap.get(belId).actName;
	}
	
	public String getBeliefTextForBelief(int belId) {
		/*
		 * Look for belief text label for the belief
		 */
		if(!this.idToNodeMap.containsKey(belId)) {
			logger.error("No node named " + belId + " in " + this.idToNodeMap);
			return "";
		}
		
		return this.idToNodeMap.get(belId).sBelief;
	}
	
	public List<Integer> getBeliefIDsAtTimeSteps(int startClosed, int endOpen) {
		/*
		 * Gets the IDs of belief nodes between the given time steps.
		 */
		List<Integer> belIds = new ArrayList<Integer>();
		
		/* iterate over all beliefs to check their T step value */
		for (Entry<Integer, PolicyNode> entry : this.idToNodeMap.entrySet())
			if (entry.getValue().H >= startClosed && entry.getValue().H < endOpen)
				belIds.add(entry.getKey());
		
		logger.debug("Found " + belIds + " beliefs between T" 
				+ startClosed + " and T" + endOpen);
		
		return belIds;
	}
	
	public String[][] getEdgeTriplesFromBeliefId(int belId) {
		/*
		 * Fetch edges from belIds to children as String arrays
		 * 
		 * TODO: each of the List to Array conversions and back is O(n).
		 * make them more efficient
		 */
		List<String[]> triples = new ArrayList<String[]>();
		
		for (Entry<String, Integer> entry : 
			this.idToSuccessorsMap.get(belId).entrySet()) {
			
			List<String> triple = new ArrayList<String>();
			
			/* add starting id */
			triple.add("m" + belId);
			
			/* create action-observation list */
			List<String> actObs = 
					Arrays.asList(entry.getKey().split(","));
			triple.addAll(actObs);
			
			/* add successor */
			triple.add("m" + entry.getValue());
			
			/* add entry to triples */
			triples.add(triple.toArray(new String[triple.size()]));
		}
		
		return triples.toArray(new String[triples.size()][]);
	}
	
	public List<Integer> getChildrenStartingFrom(List<Integer> parents, int timeSteps) {
		/*
		 * Returns all nodes from the given parents upto the given time steps.
		 */
		
		/* List of all children */
		HashSet<Integer> children = new HashSet<Integer>();
		
		/* parents currently being expanded */
		HashSet<Integer> currentParents = new HashSet<Integer>();
		currentParents.addAll(parents);
		children.addAll(parents);
		
		/* children of current parents */
		HashSet<Integer> currentChildren = new HashSet<Integer>();
		
		for (int t = 0; t < timeSteps; t++) {
			
			/* expand for all parents */
			for (Integer parent : currentParents)
				currentChildren.addAll(this.idToSuccessorsMap.get(parent).values());
			
			/* add to children set and start expanding from children */
			children.addAll(currentChildren);
			currentParents.clear();
			currentParents.addAll(currentChildren);
			currentChildren.clear();
		}
		
		/* return children as list */
		return new ArrayList<Integer>(children);
	}
	
	public HashMap<Integer, PolicyNode> getNodeMap() {
		/*
		 * Getter for idToNodeMap
		 */
		return this.idToNodeMap;
	}
	
	public HashMap<Integer, HashMap<String, Integer>> getEdgeMap() {
		/*
		 * Getter for idToSuccessorMap
		 */
		return this.idToSuccessorsMap;
	}
}
