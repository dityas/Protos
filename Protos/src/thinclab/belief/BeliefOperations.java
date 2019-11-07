/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.belief;

import java.util.Collection;
import java.util.HashMap;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public abstract class BeliefOperations {
	
	/*
	 * All operations related to POMDP beliefs or IPOMDP beliefs
	 */
	
	/* store a reference to the DecisionProcess */
	public DecisionProcess DP;
	
	/* the core belief update operation */
	public abstract DD beliefUpdate(
			DD previousBelief, String action, String[] observations) 
					throws ZeroProbabilityObsException;
	
	/* factorize joint distributions */
	public abstract DD[] factorBelief(DD belief);
	
	/* compute norm of next belief given previous belief */
	public abstract DD norm(
			DD previousBelief, String action, String[] observations) 
					throws ZeroProbabilityObsException;
	
	/* get a hashmap representation of the belief */
	public abstract HashMap<String, HashMap<String, Float>> toMap(DD belief);
	
	public abstract DD[][] factorBeliefRegion(Collection<DD> beliefRegion);
}
