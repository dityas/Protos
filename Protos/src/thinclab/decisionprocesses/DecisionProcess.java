/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.decisionprocesses;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import thinclab.belief.BeliefOperations;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public abstract class DecisionProcess implements Serializable {
	/*
	 * Defines the basic skeleton for a POMDP or IPOMDP object
	 */
	
	private static final long serialVersionUID = 6510842405416915682L;
	
	public int level;
	public int frameID;
	
	public BeliefOperations bOPs;
	
	// --------------------------------------------------------------------------------
	
	public abstract List<String> getActions();
	public abstract List<List<String>> getAllPossibleObservations();
	public abstract List<String> getObsVarNames();
	public abstract List<String> getStateVarNames();
	public abstract List<DD> getInitialBeliefs();
	public abstract DD getCurrentBelief();
	public abstract int[] getStateVarIndices();
	public abstract int[] getObsVarIndices();
	public abstract void setGlobals();
	public abstract String getType();
	public abstract String getBeliefString(DD belief);
	public abstract DD getRewardFunctionForAction(String action);
	public abstract void step(DD belief, String action, String[] obs) throws Exception;
	
	// ---------------------------------------------------------------------------------
	
	public static String getActionFromPolicy(
			DecisionProcess f, DD belief, DD[] alphaVectors, int[] policy) {
		
		/*
		 * Compute the dot product of each alpha vector with the belief and
		 * return the action represented by the max alpha vector 
		 */
		
		int bestAlphaId = DecisionProcess.getBestAlphaIndex(f, belief, alphaVectors);
		String bestAction = f.getActions().get(policy[bestAlphaId]); 
		
		return bestAction;
	}
	
	// --------------------------------------------------------------------------------
	
	public static int getVarIndex(String varName) throws VariableNotFoundException {
		/*
		 * Gets the global varIndex for variable varName
		 */
		if (varName.contains("'")) 
			varName = varName.substring(0, varName.length() - 1) + "_P";
		
		int varIndex = Arrays.asList(Global.varNames).indexOf(varName) + 1;
		
		if (varIndex == -1)
			throw new VariableNotFoundException("Var " + varName + " does not exist");
		
		return varIndex;
	}
	
	public static String getVarName(int varIndex) throws VariableNotFoundException {
		/*
		 * Gets the Global varName for the varIndex
		 */
		if (varIndex > Global.varNames.length) 
			throw new VariableNotFoundException("Can't find var for index " + varIndex);
		
		/* sub 1 from index to compensate for Matlab-like indexing in Globals */
		String varName = Global.varNames[varIndex - 1];
		
		if (varName.length() > 2 && varName.substring(varName.length() - 2).contains("_P"))
			return varName.substring(0, varName.length() - 2) + "'";
		
		else return varName;
	}
	
	public static int getBestAlphaIndex(DecisionProcess DP, DD belief, DD[] alphaVectors) {
		
		/*
		 * Returns the index of the alpha vector with the max value
		 */
		
		double bestVal = Double.NEGATIVE_INFINITY;
		double val;
		int bestAlphaId = 0;
		
		double[] values = new double[alphaVectors.length];
		for (int alphaId = 0; alphaId < alphaVectors.length; alphaId++) {
			
			val = OP.dotProduct(belief, alphaVectors[alphaId], DP.getStateVarIndices());
			values[alphaId] = val;
			
			if (val >= bestVal) {
				bestVal = val;
				bestAlphaId = alphaId;
			}
		}
		
		return bestAlphaId;
	}
	
	public static String getCanonicalName(DecisionProcess DP, String varName) {
		/*
		 * Appends frame ID to the varName
		 */
		
		/* check for primed vars */
		if (varName.lastIndexOf("'") != -1)
			return varName.substring(0, varName.length() - 1) + "/" + DP.frameID + "'";
		
		else
			return varName + "/" + DP.frameID;
	}
	
	public String getCanonicalName(String valName) {
		/*
		 * Appends frame ID to the varName
		 */
		
		return DecisionProcess.getCanonicalName(this, valName);
	}
	
	public static String getCanonicalName(int frameID, String varName) {
		/*
		 * Appends frame ID to the varName
		 */
		
		/* check for primed vars */
		if (varName.lastIndexOf("'") != -1)
			return varName.substring(0, varName.length() - 1) + "/" + frameID + "'";
		
		else
			return varName + "/" + frameID;
	}
	
	public static List<String> getCanonicalName(
			DecisionProcess DP, Collection<String> valNames) {
		/*
		 * Appends frame ID to the varName
		 */
		
		return valNames.stream()
					.map(n -> DP.getCanonicalName(n))
					.collect(Collectors.toList());
	}
	
	public List<String> getCanonicalName(Collection<String> valNames) {
		/*
		 * Appends frame ID to the varName
		 */
		
		return DecisionProcess.getCanonicalName(this, valNames);
	}
	
	public static String getLocalName(String valName) {
		/*
		 * removes frameID information from the valName
		 */
		
		return valName.split("/")[0];
	}
	
	public static int getFrameIDFromVarName(String varName) {
		
		return Integer.parseInt(varName.split("/")[1].split("_")[0]);
	}
	
	// ---------------------------------------------------------------------------------
	/*
	 * Expose belief operations
	 */
	
	public DD beliefUpdate(
			DD previousBelief, String action, String[] observations) 
					throws ZeroProbabilityObsException {
		
		return this.bOPs.beliefUpdate(previousBelief, action, observations);
	}
	
	public DD[] factorBelief(DD belief) {
		
		return this.bOPs.factorBelief(belief);
	}
	
	public DD norm(
			DD previousBelief, String action) {
		
		return this.bOPs.norm(previousBelief, action);
	}
	
	public HashMap<String, HashMap<String, Float>> toMap(DD belief) {
		
		return this.bOPs.toMap(belief);
	}
	
	public DD[][] factorBeliefRegion(Collection<DD> beliefRegion) {
		
		return this.bOPs.factorBeliefRegion(beliefRegion);
	}
}
