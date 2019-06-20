package thinclab.policyhelper;

import java.util.*;

import thinclab.symbolicperseus.DD;

public class PolicyNode {
	
	int alphaId=-1;
	int actId = 1;
	public String actName = "";
	DD belief;
	HashMap<String, ArrayList<Float>> factoredBelief = new HashMap<String, ArrayList<Float>>();
	Map<List<String>, Integer> nextNode = new HashMap<List<String>, Integer>();
	Map<List<String>, Integer> compressedNextNode = new HashMap<List<String>, Integer>();
	public boolean startNode = false;
	
	public boolean shallowEquals(PolicyNode other) {
		/*
		 * Takes another policy and compares its alphaId, actId and nexNodes
		 */
//		System.out.println("" + this.nextNode + "\r\n" + other.nextNode);
		if (this.actId == other.actId && this.alphaId == other.alphaId && this.nextNode.equals(other.nextNode)) {
			return true;
		}
		
		else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "PolicyNode [alphaId=" + this.alphaId + " actId=" + actId + ", belief=" + this.factoredBelief + ", nextNode="
				+ nextNode.toString() + "]\r\n";
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
		
		// populate reverse hash map
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
		
		// Loop over reverse map and add compressed edges to compressedNextNode
		Iterator<Map.Entry<Integer, List<List<String>>>> reverseMapIter = nextNodeToObsMap.entrySet().iterator();
		while (reverseMapIter.hasNext()) {
			Map.Entry<Integer, List<List<String>>> entry = reverseMapIter.next();
			Integer child = entry.getKey();
			
			List<List<String>> observationSet = entry.getValue();
			String[] compressedObs = observationSet.get(0).toArray(new String[observationSet.get(0).size()]);			
			
			// Iterate over common observations
			Iterator<List<String>> obsIter = observationSet.iterator();
			while (obsIter.hasNext()) {
				List<String> list = obsIter.next();
				
				// Check each observation
				for (int i=0; i < list.size(); i++) {
					String obs = list.get(i);
					if (obs != compressedObs[i]) {
						compressedObs[i] = "*";
					} // if
				} // for (int i=0; i < list.size(); i++)
			} // while (obsIter.hasNext())
			
			// Add compressed observation to compressed hash map
			this.compressedNextNode.put(Arrays.asList(compressedObs), child);
		} // while (reverseMapIter.hasNext())
		
//		System.out.println("BEFORE: ");
//		System.out.println(this.nextNode);
//		System.out.println("AFTER: ");
//		System.out.println(this.compressedNextNode);
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
