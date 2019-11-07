/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.belief;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.MySet;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public class BeliefOps extends BeliefOperations{
	
	/*
	 * POMDP belief operations
	 */
	
	private static final Logger LOGGER = Logger.getLogger(BeliefOps.class);
	
	// -----------------------------------------------------------------------------------
	
	public BeliefOps(POMDP pomdp) {
		
		this.DP = (POMDP) pomdp;
		LOGGER.debug("BeliefOps initialised for POMDP " + pomdp);
	}
	
	// -----------------------------------------------------------------------------------
	
	public POMDP getPOMDP() {
		
		return (POMDP) this.DP;
	}
	
	public static DD[] factorBelief(POMDP p, DD beliefPoint) {
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
	
	@Override
	public DD beliefUpdate(
			DD belState, String action, String[] obsnames) throws ZeroProbabilityObsException {
		/*
		 * Belief update with known action and Observations as Strings
		 * 
		 * Throws exception on zero probability observations. This is just an exact copy
		 * of Jesse Hoey's belief update code in symbolic perseus with an added
		 * exception for zero probability observations.
		 */
		
		/* get POMDP ref */
		POMDP DPRef = this.getPOMDP();
		
		/* set Globals and clear caches */
		this.DP.setGlobals();
		Global.clearHashtables();
		
		int actId = DPRef.getActions().indexOf(action);
		
		if (obsnames.length != DPRef.nObsVars) return null;
		
		int[] obsvals = new int[obsnames.length];
		
		for (int o = 0; o < obsnames.length; o++) {
			obsvals[o] = DPRef.findObservationByName(o, obsnames[o]) + 1;
			
			if (obsvals[o] < 0) return null;
		}
		
		int[][] obsVals = POMDP.stackArray(DPRef.primeObsIndices, obsvals);
		DD[] restrictedObsFn = OP.restrictN(DPRef.actions[actId].obsFn, obsVals);
		
		DD nextBelState = OP.addMultVarElim(
				POMDP.concatenateArray(belState, 
						DPRef.actions[actId].transFn,
						restrictedObsFn), 
				DPRef.varIndices);
		
		nextBelState = OP.primeVars(nextBelState, -DPRef.nVars);
		DD obsProb = OP.addMultVarElim(nextBelState, DPRef.varIndices);
		
		if (obsProb.getVal() < 1e-8) {
			throw new ZeroProbabilityObsException(
					"OBSERVATION " + obsnames + " is zero probability");
		}
		
		nextBelState = OP.div(nextBelState,
				OP.addMultVarElim(nextBelState, 
						DPRef.varIndices));
		
		return nextBelState;
	}

	public DD beliefUpdate( 
			DD belState, String action, int[][] obsVals) throws ZeroProbabilityObsException {
		/*
		 * Belief update with observation ID arrays instead of String arrays
		 * 
		 * WARNING: This just left here to ensure compatibility with the previous solver.
		 * Should not really be used explicitly.
		 */
		
		/* Set globals and all that */
		POMDP DPRef = this.getPOMDP();
		DPRef.setGlobals();
		int actId = DPRef.getActions().indexOf(action);
		
		DD[] restrictedObsFn = OP.restrictN(DPRef.actions[actId].obsFn, obsVals);
		
		DD nextBelState = OP.addMultVarElim(
				POMDP.concatenateArray(belState, DPRef.actions[actId].transFn,
						restrictedObsFn), DPRef.varIndices);
		
		nextBelState = OP.primeVars(nextBelState, -DPRef.nVars);
		DD obsProb = OP.addMultVarElim(nextBelState, DPRef.varIndices);
		
		if (obsProb.getVal() < 1e-8) {
			throw new ZeroProbabilityObsException(
					"OBSERVATION is zero probability");
		}
		
		nextBelState = OP.div(nextBelState,
				OP.addMultVarElim(nextBelState, DPRef.varIndices));
		
		return nextBelState;
	}
	
	// --------------------------------------------------------------------------------------
	
	public static DD[][] factorBeliefRegion(POMDP pomdp, List<DD> beliefRegion) {
		/*
		 * Factors the full belief region represented as a list
		 */
		pomdp.setGlobals();
		DD[][] belRegion = new DD[beliefRegion.size()][pomdp.nStateVars];
		
		/* Convert to array because accessing known elements is faster in arrays */
		DD[] beliefRegionArray = beliefRegion.toArray(new DD[beliefRegion.size()]);
		
		for (int i = 0; i < beliefRegion.size(); i++)
			belRegion[i] = 
				BeliefOps.factorBelief(
						pomdp, 
						beliefRegionArray[i]);
				
		return belRegion;
	}
	
	// --------------------------------------------------------------------------------------
	
//	public static boolean checkEquals(DD belief, DD otherBelief) {
//		/*
//		 * Checks if belief is equal to other belief
//		 */
//		return belief.equals(otherBelief);
//	}
//	
//	public static boolean checkEquals(DD[] belief, DD[] otherBelief) {
//		/*
//		 * Equality for factored beliefs
//		 */
//		if (belief.length != otherBelief.length) return false;
//		
//		for (int i=0;i < belief.length; i++) {
//			if (!belief[i].equals(otherBelief[i])) return false;
//		}
//		
//		return true;
//	}
//	
//	public static boolean checkEquals(POMDP p, DD[] belief, DD otherBelief) {
//		
//		DD[] otherBeliefFactored = BeliefOps.factorBelief(p, otherBelief);
//		return BeliefOps.checkEquals(belief, otherBeliefFactored);
//	}
//	
//	public static boolean checkEquals(POMDP p, DD belief, DD[] otherBelief) {
//		
//		DD[] beliefFactored = BeliefOps.factorBelief(p, belief);
//		return BeliefOps.checkEquals(beliefFactored, otherBelief);
//	}

	@Override
	public DD[] factorBelief(DD belief) {
		
		/*
		 * factors belief state into a DD array
		 */
		
		/* set globals, clear caches and get POMDP ref */
		POMDP DPRef = this.getPOMDP();
		DPRef.setGlobals();
		Global.clearHashtables();
		
		DD[] fbs = new DD[DPRef.nStateVars];
		
		for (int varId = 0; varId < DPRef.nStateVars; varId++) {
			fbs[varId] = OP.addMultVarElim(belief,
					MySet.remove(DPRef.varIndices, varId + 1));
		}
		
		return fbs;
	}

	@Override
	public DD norm(DD previousBelief, String action, String[] observations) throws ZeroProbabilityObsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, HashMap<String, Float>> toMap(DD belief) {
		/*
		 * Makes a hashmap of belief state and values and returns it
		 */
		
		POMDP DPRef = this.getPOMDP();
		DPRef.setGlobals();
		
		HashMap<String, HashMap<String, Float>> beliefs = 
				new HashMap<String, HashMap<String, Float>>();
		
		/* Factor the belief state into individual variables */
		DD[] fbs = new DD[DPRef.nStateVars];
		for (int varId = 0; varId < DPRef.nStateVars; varId++) {
			
			fbs[varId] = OP.addMultVarElim(belief,
					MySet.remove(DPRef.varIndices, varId + 1));
			
			/* Make state variable name */
			String name = DPRef.varName[varId];
			
			/* Get respective belief for the variable */
			DD[] varChildren = fbs[varId].getChildren();
			HashMap<String, Float> childVals = new HashMap<String, Float>();
			
			if (varChildren == null) {
				for (int i=0; i < DPRef.stateVars[varId].arity; i++) {
					childVals.put(Global.valNames[varId][i], new Float(fbs[varId].getVal()));
				}
			}
			
			else {
				for (int i=0; i < DPRef.stateVars[varId].arity; i++) {
					childVals.put(Global.valNames[varId][i], new Float(varChildren[i].getVal()));
				}
			}
			
			beliefs.put(name, childVals);
		}
		
		return beliefs;
	}

	@Override
	public DD[][] factorBeliefRegion(Collection<DD> beliefRegion) {
		// TODO Auto-generated method stub
		return null;
	}

}
