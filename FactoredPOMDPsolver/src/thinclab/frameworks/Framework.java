/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.frameworks;

import java.util.List;

import thinclab.symbolicperseus.DD;

/*
 * @author adityas
 *
 */
public abstract class Framework {
	/*
	 * Defines the basic skeleton for a POMDP or IPOMDP object
	 */
	
	public abstract List<String> getActions();
	public abstract List<List<String>> getAllPossibleObservations();
	public abstract List<String> getObsVarNames();
	public abstract List<String> getStateVarNames();
	public abstract List<DD> getInitialBeliefs();
}
