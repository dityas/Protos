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

/*
 * @author adityas
 *
 */
public class OpponentModel {
	/*
	 * Defines a single interactive state <belief, frame> combination
	 */
	public DD belief;
	public POMDP frame;
	
	public OpponentModel(DD belief, POMDP frame) {
		/*
		 * Initialize from <belief, frame> combination
		 */
		this.belief = belief;
		this.frame = frame;
	}

	@Override
	public String toString() {
		return "OpponentModel [belief=" + belief + ", frame=" + frame + "]\r\n";
	}

}
