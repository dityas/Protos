package thinclab.policyhelper;

import java.util.HashMap;
import java.util.List;
import java.util.*;

public class PolicyGraph {
	
	private HashMap<Integer, PolicyNode> nodeHashMap;

	public PolicyGraph(List<PolicyNode> policyNodes) {
		/*
		 * Make policy graph from the list nodes given by PolicyExtractor
		 */
		
		// Populate nodeHashMap
		Iterator<PolicyNode> nodeIter = policyNodes.iterator();
		
		while(nodeIter.hasNext()) {
			PolicyNode nodeCurr = nodeIter.next(); 
			this.nodeHashMap.put(nodeCurr.alphaId, nodeCurr);
		}
	}

}
