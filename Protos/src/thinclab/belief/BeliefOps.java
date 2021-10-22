/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.belief;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class BeliefOps extends BeliefOperations {
	
	/*
	 * POMDP belief operations
	 */
	
	private static final long serialVersionUID = 8420140973547035341L;
	
	private static final Logger LOGGER = LogManager.getLogger(BeliefOps.class); 
	
	// -----------------------------------------------------------------------------------
	
	public BeliefOps(POMDP pomdp) {
		
		this.DP = (POMDP) pomdp;
		LOGGER.debug("BeliefOps initialised for POMDP " + pomdp);
	}
	
	// -----------------------------------------------------------------------------------
	
	public POMDP getPOMDP() {
		
		return (POMDP) this.DP;
	}
	
	// --------------------------------------------------------------------------------------
	
	public DD biasedBeliefUpdate(
			DD prior, String action, String[] obsnames) throws ZeroProbabilityObsException {
		
		// Get POMDP reference
		POMDP DPRef = this.getPOMDP();
		
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
				POMDP.concatenateArray(prior, 
						DPRef.actions[actId].transFn, new DD[] {}), 
				DPRef.varIndices);
		
		DD[] pred = this.factorBelief(nextBelState);
		System.out.println("preds are: " + Arrays.toString(pred));
		
		float weight = 1.0f / 1.0f + OP.l2NormSq(pred, restrictedObsFn);
		System.out.println("Weight is: " + weight);

		System.out.println("Evidence is: " + Arrays.toString(restrictedObsFn));
		DD[] weightedEvidence = OP.pow(restrictedObsFn, weight);
		System.out.println("Weighted evidence is: " 
				+ Arrays.toString(weightedEvidence));
		nextBelState = OP.multN(ArrayUtils.addAll(pred, weightedEvidence));
		
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
	
	@Override
	public DD[] factorBelief(DD belief) {
		
		/*
		 * factors belief state into a DD array
		 */
		
		/* set globals, clear caches and get POMDP ref */
		POMDP DPRef = this.getPOMDP();
//		DPRef.setGlobals();
//		Global.clearHashtables();
		
		DD[] fbs = new DD[DPRef.nStateVars];
		
		for (int varId = 0; varId < DPRef.nStateVars; varId++) {
			fbs[varId] = OP.addMultVarElim(belief,
					MySet.remove(DPRef.varIndices, varId + 1));
		}
		
		return fbs;
	}

	@Override
	public DD getObsDist(DD previousBelief, String action) {
		/*
		 * Gets the observation distribution
		 */
		
		/* set globals and all that */
//		this.DP.setGlobals();
		POMDP POMDPRef = this.getPOMDP();
		
		int actId = POMDPRef.getActions().indexOf(action);
		DD obsDist = OP.addMultVarElim(
				POMDP.concatenateArray(previousBelief, 
						POMDPRef.actions[actId].transFn,
						POMDPRef.actions[actId].obsFn), 
				ArrayUtils.addAll(POMDPRef.varIndices, POMDPRef.primeVarIndices));
		
		return obsDist;
	}

	@Override
	public HashMap<String, HashMap<String, Float>> toMap(DD belief) {
		/*
		 * Makes a hashmap of belief state and values and returns it
		 */
		
		POMDP DPRef = this.getPOMDP();
//		DPRef.setGlobals();
		
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
					childVals.put(Global.valNames.get(varId).get(i), Float.valueOf((float) fbs[varId].getVal()));
				}
			}
			
			else {
				for (int i=0; i < DPRef.stateVars[varId].arity; i++) {
					childVals.put(Global.valNames.get(varId).get(i), Float.valueOf((float) varChildren[i].getVal()));
				}
			}
			
			beliefs.put(name, childVals);
		}
		
		return beliefs;
	}

	@Override
	public DD[][] factorBeliefRegion(Collection<DD> beliefRegion) {
		/*
		 * Factors the full belief region represented as a list
		 */
		
		/* set globals and clear caches */
//		this.DP.setGlobals();
		POMDP DPRef = this.getPOMDP();
		
		DD[][] belRegion = 
				new DD[beliefRegion.size()][DPRef.S.size()];
		
		/* Convert to array because accessing known elements is faster in arrays */
		DD[] beliefRegionArray = beliefRegion.toArray(new DD[beliefRegion.size()]);
		
		for (int i = 0; i < beliefRegion.size(); i++)
			belRegion[i] = DPRef.factorBelief(beliefRegionArray[i]);
				
		return belRegion;
	}

}
