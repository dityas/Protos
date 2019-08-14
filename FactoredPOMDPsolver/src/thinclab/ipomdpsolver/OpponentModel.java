/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.POMDP;

import java.util.ArrayList;
import java.util.List;

import thinclab.policyhelper.PolicyNode;
import thinclab.policyhelper.PolicyTree;

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
	
	public OpponentModel(List<POMDP> frames) {
		/*
		 * Initialize from <belief, frame> combination
		 */
		for (POMDP frame: frames) {
			
			/* Make policy tree for limited horizons */
			PolicyTree T = frame.getPolicyTree(5);
			
			/*
			 * Start indexing nodes incrementally so that each node has a unique index
			 * even if it is from different trees
			 */
			T.indexNodes(nodesList.size());
			
			/* Add nodes to */
			nodesList.addAll(T.policyNodes);
		}
	}
	
	// -----------------------------------------------------------------------------

//	@Override
//	public String toString() {
//		return "OpponentModel [belief=" + belief + ", frame=" + frame + "]\r\n";
//	}
//
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((belief == null) ? 0 : belief.hashCode());
//		result = prime * result + ((frame == null) ? 0 : frame.hashCode());
//		return result;
//	}

//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		OpponentModel other = (OpponentModel) obj;
//		if (belief == null) {
//			if (other.belief != null)
//				return false;
//		} else if (!belief.equals(other.belief))
//			return false;
//		if (frame == null) {
//			if (other.frame != null)
//				return false;
//		} else if (!frame.equals(other.frame))
//			return false;
//		return true;
//	}

}
