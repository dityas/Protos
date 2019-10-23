/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import thinclab.frameworks.Framework;
import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public abstract class BaseSolver {
	
	/* reference to the framework */
	public Framework f;
	
	public abstract String getActionForBelief(DD belief);

}
