/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.frameworks;

import java.util.List;

import org.apache.log4j.Logger;

import thinclab.belief.Belief;
import thinclab.legacy.DD;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public abstract class Framework {
	/*
	 * Defines the basic skeleton for a POMDP or IPOMDP object
	 */
	
	// --------------------------------------------------------------------------------
	
	public abstract List<String> getActions();
	public abstract List<List<String>> getAllPossibleObservations();
	public abstract List<String> getObsVarNames();
	public abstract List<String> getStateVarNames();
	public abstract List<DD> getInitialBeliefs();
	public abstract int[] getStateVarIndices();
	public abstract int[] getObsVarIndices();
	
	// ---------------------------------------------------------------------------------
	
	public static String getActionFromPolicy(
			Framework f, DD belief, DD[] alphaVectors, int[] policy) {
		
		/*
		 * Compute the dot product of each alpha vector with the belief and
		 * return the action represented by the max alpha vector 
		 */
		
		double bestVal = Double.NEGATIVE_INFINITY;
		double val;
		int bestAlphaId = 0;
		
		double[] values = new double[alphaVectors.length];
		for (int alphaId = 0; alphaId < alphaVectors.length; alphaId++) {
			
			val = OP.dotProduct(belief, alphaVectors[alphaId], f.getStateVarIndices());
			values[alphaId] = val;
			
			if (val >= bestVal) {
				bestVal = val;
				bestAlphaId = alphaId;
			}
		}
		
		String bestAction = f.getActions().get(policy[bestAlphaId]); 
		
		return bestAction;
	}
}
