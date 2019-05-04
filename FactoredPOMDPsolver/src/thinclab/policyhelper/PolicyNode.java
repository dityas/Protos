package thinclab.policyhelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.*;

import thinclab.symbolicperseus.DD;

public class PolicyNode {
	
	int alphaId=-1;
	int actId = 1;
	DD belief;
	HashMap<String, ArrayList<Float>> factoredBelief = new HashMap<String, ArrayList<Float>>();
	Map<List<String>, Integer> nextNode = new HashMap<List<String>, Integer>();
	
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
//			Iterator<String> keys = this.nextNode.keySet().iterator();
//			
//			while(keys.hasNext()) {
//				String theKey = keys.next();
//				
//				if (this.nextNode.get(theKey) != other.nextNode.get(theKey)) {
////					System.out.println("DEBUG: Maps differ");
//					return false;
//				} 
//			}
//			
//			return true;
//		}
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
}
