/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.symbolicperseus;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * @author adityas
 *
 */
public class Belief {
	/*
	 * Handles operations with belief DDs for POMDPs
	 */
	
	public static DD[] factorBeliefPoint(POMDP pomdp, DD beliefPoint) {
		/*
		 * factors belief state into a DD array
		 */
		DD[] fbs = new DD[pomdp.nStateVars];
		for (int varId = 0; varId < pomdp.nStateVars; varId++) {
			fbs[varId] = OP.addMultVarElim(beliefPoint,
					MySet.remove(pomdp.varIndices, varId + 1));
		}
		
		return fbs;
	} 
	
	// --------------------------------------------------------------------------------------
	
	public static HashMap<String, ArrayList<Float>> toStateMap(POMDP pomdp, DD belState) {
		/*
		 * Makes a hashmap of belief state and values and returns it
		 */
		HashMap<String, ArrayList<Float>> beliefs = new HashMap<String, ArrayList<Float>>();
		DD[] fbs = new DD[pomdp.nStateVars];
		for (int varId = 0; varId < pomdp.nStateVars; varId++) {
			fbs[varId] = OP.addMultVarElim(belState,
					MySet.remove(pomdp.varIndices, varId + 1));
			
			// Make state variable name
			String name = pomdp.varName[varId];
			
			// Get respective belief for the variable
			DD[] varChildren = fbs[varId].getChildren();
			ArrayList<Float> childVals = new ArrayList<Float>();
			
			if (varChildren == null) {
				for (int i=0; i < pomdp.stateVars[varId].arity; i++) {
					childVals.add(new Float(fbs[varId].getVal()));
				}
			}
			
			else {
				for (int i=0; i < pomdp.stateVars[varId].arity; i++) {
					childVals.add(new Float(varChildren[i].getVal()));
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
		HashMap<String, ArrayList<Float>> beliefs = new HashMap<String, ArrayList<Float>>();

		if (belState.length != pomdp.nStateVars) {
			System.err.println("SOMETHINGS SERIOUSLY WRONG WITH THE BELIEF STATE");
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
