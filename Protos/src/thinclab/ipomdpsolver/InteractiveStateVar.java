/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import thinclab.legacy.StateVar;

/*
 * @author adityas
 *
 */
public class InteractiveStateVar {
	/*
	 * Defines an interactive state variable
	 * 
	 * Contains a reference for the lower level agent's model and a single physical state
	 */
	
	public StateVar physicalState;
	public OpponentModel oppModel;
	
	public InteractiveStateVar(StateVar physicalState, OpponentModel oppModel) {
		/*
		 * Set attributes
		 */
		this.physicalState = physicalState;
		this.oppModel = oppModel;
	}

}
