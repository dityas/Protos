/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.symbolicperseus.Belief;

import java.util.HashMap;

import thinclab.symbolicperseus.DD;

/*
 * @author adityas
 *
 */
public class BeliefTreeNode {
	/*
	 * Defines a single node in a belief tree structure.
	 * 
	 * Each node points to a parent and children based on observations
	 */
	
	public DD belief;
	public int optimalActId;
	public HashMap<String[], BeliefTreeNode> children = new HashMap<String[], BeliefTreeNode>();
	
}
