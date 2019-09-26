/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.Belief;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.Global;
import thinclab.symbolicperseus.MySet;
import thinclab.symbolicperseus.OP;
import thinclab.symbolicperseus.POMDP;

/*
 * @author adityas
 *
 */
public class Belief {
	/*
	 * Handles operations with belief DDs for POMDPs
	 */
	
	public static DD[] factorBeliefPoint(POMDP p, DD beliefPoint) {
		/*
		 * factors belief state into a DD array
		 */
		p.setGlobals();
		DD[] fbs = new DD[p.nStateVars];
		for (int varId = 0; varId < p.nStateVars; varId++) {
			fbs[varId] = OP.addMultVarElim(beliefPoint,
					MySet.remove(p.varIndices, varId + 1));
		}
		
		return fbs;
	}
	
	public static List<DD> factorBeliefPointAsList(POMDP p, DD beliefPoint) {
		/*
		 * factors belief state into a DD List.
		 * 
		 * This is useful if a factored point has to be stored in a HashSet.
		 * 
		 * NOTE: 	hashing arrays is difficult because the hashCode() method includes
		 * 			the array object's address. This is difficult to override. So it is
		 * 			more convenient to use Lists instead. 
		 */
		p.setGlobals();
		DD[] fbs = new DD[p.nStateVars];
		for (int varId = 0; varId < p.nStateVars; varId++) {
			fbs[varId] = OP.addMultVarElim(beliefPoint,
					MySet.remove(p.varIndices, varId + 1));
		}
		
		return  (List<DD>) Arrays.asList(fbs);
	}
	
	public static DD unFactorBeliefPoint(POMDP p, DD[] factoredBeliefPoint) {
		/*
		 * Returns a joint distribution from a factored distribution
		 */
		p.setGlobals();
		return (DD) OP.multN(factoredBeliefPoint);
	}
	
	// --------------------------------------------------------------------------------------
	
	public static HashMap<String, HashMap<String, Float>> toStateMap(POMDP pomdp, DD belState) {
		/*
		 * Makes a hashmap of belief state and values and returns it
		 */
		pomdp.setGlobals();
		
		HashMap<String, HashMap<String, Float>> beliefs = 
				new HashMap<String, HashMap<String, Float>>();
		
		/* Factor the belief state into individual variables */
		DD[] fbs = new DD[pomdp.nStateVars];
		for (int varId = 0; varId < pomdp.nStateVars; varId++) {
			
			fbs[varId] = OP.addMultVarElim(belState,
					MySet.remove(pomdp.varIndices, varId + 1));
			
			/* Make state variable name */
			String name = pomdp.varName[varId];
			
			/* Get respective belief for the variable */
			DD[] varChildren = fbs[varId].getChildren();
			HashMap<String, Float> childVals = new HashMap<String, Float>();
			
			if (varChildren == null) {
				for (int i=0; i < pomdp.stateVars[varId].arity; i++) {
					childVals.put(Global.valNames[varId][i], new Float(fbs[varId].getVal()));
				}
			}
			
			else {
				for (int i=0; i < pomdp.stateVars[varId].arity; i++) {
					childVals.put(Global.valNames[varId][i], new Float(varChildren[i].getVal()));
				}
			}
			
			beliefs.put(name, childVals);
		}
		
		return beliefs;
	}
	
	public static HashMap<String, ArrayList<Float>> toStateMap(POMDP pomdp, DD[] belState) {
		/*
		 * Makes a hashmap of belief state and values and returns it
		 */
		
		pomdp.setGlobals();
		HashMap<String, ArrayList<Float>> beliefs = new HashMap<String, ArrayList<Float>>();

		if (belState.length != pomdp.nStateVars) {
			System.err.println("SOMETHING'S SERIOUSLY WRONG WITH THE BELIEF STATE");
		}
		for (int i = 0; i < belState.length; i++) {
			
			// Make state variable name
			String name = pomdp.varName[i];
			
			// Get respective belief for the variable
			DD[] varChildren = belState[i].getChildren();
			ArrayList<Float> childVals = new ArrayList<Float>();
			
			if (varChildren == null) {
				for (int j=0; j < pomdp.stateVars[i].arity; j++) {
					childVals.add(new Float(belState[i].getVal()));
				}
			}
			
			else {
				for (int j=0; j < pomdp.stateVars[i].arity; j++) {
					childVals.add(new Float(varChildren[j].getVal()));
				}
			}
			
			beliefs.put(name, childVals);
		}
		
		return beliefs;
	}
	
	// --------------------------------------------------------------------------------------
	
	public static DD beliefUpdate(POMDP p,
			DD belState, 
			int actId, 
			String[] obsnames) throws ZeroProbabilityObsException {
		/*
		 * Belief update with known action and Observations as Strings
		 * 
		 * Throws exception on zero probability observations. This is just an exact copy
		 * of Jesse Hoey's belief update code in symbolic perseus with an added
		 * exception for zero probability observations.
		 */
		p.setGlobals();
		if (obsnames.length != p.nObsVars) return null;
		
		int[] obsvals = new int[obsnames.length];
		
		for (int o = 0; o < obsnames.length; o++) {
			obsvals[o] = p.findObservationByName(o, obsnames[o]) + 1;
			
			if (obsvals[o] < 0) return null;
		}
		
		int[][] obsVals = POMDP.stackArray(p.primeObsIndices, obsvals);
		DD[] restrictedObsFn = OP.restrictN(p.actions[actId].obsFn, obsVals);
		
		DD nextBelState = OP.addMultVarElim(
				POMDP.concatenateArray(belState, 
						p.actions[actId].transFn,
						restrictedObsFn), 
				p.varIndices);
		
		nextBelState = OP.primeVars(nextBelState, -p.nVars);
		DD obsProb = OP.addMultVarElim(nextBelState, p.varIndices);
		
		if (obsProb.getVal() < 1e-8) {
			throw new ZeroProbabilityObsException(
					"OBSERVATION " + obsnames + " is zero probability");
		}
		
		nextBelState = OP.div(nextBelState,
				OP.addMultVarElim(nextBelState, 
						p.varIndices));
		
		return nextBelState;
	}

	public static DD beliefUpdate(POMDP p, 
			DD belState, 
			int actId, 
			int[][] obsVals) throws ZeroProbabilityObsException {
		/*
		 * Belief update with observation ID arrays instead of String arrays
		 */
		
		p.setGlobals();
		DD[] restrictedObsFn = OP.restrictN(p.actions[actId].obsFn, obsVals);
		
		DD nextBelState = OP.addMultVarElim(
				POMDP.concatenateArray(belState, p.actions[actId].transFn,
						restrictedObsFn), p.varIndices);
		
		nextBelState = OP.primeVars(nextBelState, -p.nVars);
		DD obsProb = OP.addMultVarElim(nextBelState, p.varIndices);
		
		if (obsProb.getVal() < 1e-8) {
			throw new ZeroProbabilityObsException(
					"OBSERVATION is zero probability");
		}
		
		nextBelState = OP.div(nextBelState,
				OP.addMultVarElim(nextBelState, p.varIndices));
		
		return nextBelState;
	}
	
	// --------------------------------------------------------------------------------------
	
	public static boolean checkEquals(DD belief, DD otherBelief) {
		/*
		 * Checks if belief is equal to other belief
		 */
		return belief.equals(otherBelief);
	}
	
	public static boolean checkEquals(DD[] belief, DD[] otherBelief) {
		/*
		 * Equality for factored beliefs
		 */
		if (belief.length != otherBelief.length) return false;
		
		for (int i=0;i < belief.length; i++) {
			if (!belief[i].equals(otherBelief[i])) return false;
		}
		
		return true;
	}
	
	public static boolean checkEquals(POMDP p, DD[] belief, DD otherBelief) {
		
		DD[] otherBeliefFactored = Belief.factorBeliefPoint(p, otherBelief);
		return Belief.checkEquals(belief, otherBeliefFactored);
	}
	
	public static boolean checkEquals(POMDP p, DD belief, DD[] otherBelief) {
		
		DD[] beliefFactored = Belief.factorBeliefPoint(p, belief);
		return Belief.checkEquals(beliefFactored, otherBelief);
	}

}
