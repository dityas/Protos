package thinclab.policyhelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import thinclab.pomdpsolver.DD;

public class PolicyNode {
	int alphaId=-1;
	int actId = 1;
	DD belief;
	Map<String, Integer> nextNode = new HashMap<String, Integer>();
	
	public boolean shallowEquals(PolicyNode other) {
		/*
		 * Takes another policy and compares its alphaId, actId and nexNodes
		 */
		
		// Check alphaId and actId
		if ((this.alphaId != other.alphaId) || (this.actId != other.actId)) {
//			System.out.println("DEBUG: Ids differ");
			return false;
		}
		
		// Check if nextNode sizes are equal
		else if (this.nextNode.size() != other.nextNode.size()) {
//			System.out.println("DEBUG: sizes differ");
			return false;
		}
		
		// Check each element of nextNode
		else {
			Iterator<String> keys = this.nextNode.keySet().iterator();
			
			while(keys.hasNext()) {
				String theKey = keys.next();
				
				if (this.nextNode.get(theKey) != other.nextNode.get(theKey)) {
//					System.out.println("DEBUG: Maps differ");
					return false;
				} 
			}
			
			return true;
		}
	}

	@Override
	public String toString() {
		return "PolicyNode [alphaId=" + alphaId + ", actId=" + actId + ", belief=" + belief + ", nextNode="
				+ nextNode.toString() + "]\r\n";
	}

}
