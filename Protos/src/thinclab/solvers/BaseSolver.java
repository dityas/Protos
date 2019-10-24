/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.io.Serializable;

import thinclab.frameworks.Framework;
import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public abstract class BaseSolver implements Serializable {
	
	private static final long serialVersionUID = 1521369802284824797L;
	
	/* reference to the framework */
	public Framework f;
	
	public abstract String getActionForBelief(DD belief);
	
	/* return the status of the solver */
	public abstract boolean hasSolution();

}
