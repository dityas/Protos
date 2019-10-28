/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.belief;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;

import thinclab.decisionprocesses.IPOMDP;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public class InteractiveBelief extends Belief {
	/*
	 * Represents a single interactive belief
	 * 
	 * This may be a little inefficient especially during belief updates. During the actual
	 * computation, the actual belief vector will have to be accessed through the object 
	 * attributes.
	 */
	
	// -------------------------------------------------------------------------------------
	
	public static DD staticL1BeliefUpdate(
			IPOMDP ipomdp,
			DD startBelief, 
			String actName, 
			String[] observations) throws ZeroProbabilityObsException, VariableNotFoundException {
		/*
		 * Performs a static L1 IPOMDP belief update given action and observations
		 * 
		 * Performs a query over the following DBN:
		 *      .---.                      .----.
		 *      |M_j|--------------------->|M_j'|
		 *      '---'                      '----'
		 *      
		 *                             
		 *                             
		 *                             
		 *      .---.                      .---.	
		 *      | S |--------------------->| S'|
		 *      '---'                      '---'
		 *                                       		 
		 *                                      .---.       .---.
		 *                                      |Oi'|       |Oj'|
		 *                                      '---'       '---'
		 * 	 P_hat(S', M_j' | Oi'=o) = 
		 * 			Sigma[S] (P(S)) x Sigma[M_j] (P(M_j) x P(S', S, M_j) x P(Oi'=o, S', M_j))
		 * 		 		x	Sigma[Oj'] (P(Oj', S', M_j) x P(M_j', M_j, Oj')) 
		 */
		
		ipomdp.setGlobals();
		
		/* First reduce Oi based on observations */
		Global.clearHashtables();

		int[] obsVals = new int[ipomdp.Omega.size() - ipomdp.OmegaJNames.size()];
		
		for (int o = 0; o < obsVals.length; o++) {
			int val = ipomdp.findObservationByName(o, observations[o]) + 1;
			
			if (val < 0) 
				throw new VariableNotFoundException(
						"Obs Variable " + ipomdp.Omega.get(o).name
						+ " does not take value " + observations[o]);
			
			else obsVals[o] = val;
		}
		
		/* Restrict Oi */
		DD[] restrictedOi = 
				OP.restrictN(
						ipomdp.currentOi.get(actName), 
						IPOMDP.stackArray(
								ipomdp.obsIVarPrimeIndices, obsVals));

		/* Collect f1 = P(S, Mj)  */
		DD f1 = startBelief;
		System.out.println(f1.toDDTree());
		System.out.println(OP.addMultVarElim(f1, ArrayUtils.subarray(
								ipomdp.stateVarIndices, 
								0, 
								ipomdp.stateVarIndices.length - 1)));
		/* Collect f2 = P(Aj | Mj) x P(Oi'=o, S', Aj) x P (S', Aj, S) */
		DD[] f2 = 
				ArrayUtils.addAll(
						ArrayUtils.add(
								ipomdp.currentTi.get(actName), 
								ipomdp.currentAjGivenMj), 
						restrictedOi);
		
		/* Collect f3 = f1 x f2 */
//		DD[] f3 = ArrayUtils.addAll(f2, f1);
		
		DD tau = ipomdp.currentTau;

		DD f3 = 
				OP.addMultVarElim(
						ArrayUtils.add(f2, tau), 
						ipomdp.AjIndex);
		
		/* Perform the sum out */
		DD nextBelief = 
				OP.addMultVarElim(
						new DD[] {f3, f1}, 
						ArrayUtils.subarray(ipomdp.stateVarIndices, 0, ipomdp.S.size() - 1));

		/* Shift indices */
		nextBelief = OP.primeVars(nextBelief, -(ipomdp.S.size() + ipomdp.Omega.size()));

		/* compute normalization factor */
		DD norm = 
				OP.addMultVarElim(
						nextBelief, 
						ArrayUtils.subarray(
								ipomdp.stateVarIndices, 
								0, 
								ipomdp.stateVarIndices.length - 1));
		
		if (norm.getVal() < 1e-8) 
			throw new ZeroProbabilityObsException(
					"Observation " + Arrays.toString(observations) 
					+ " not possible at belief " + startBelief);
		System.out.println(norm.toDDTree());
		return OP.div(nextBelief, norm); 
	}
	
	public static DD getL1BeliefUpdateNorm(
			IPOMDP ipomdp,
			DD startBelief, 
			String actName) throws ZeroProbabilityObsException, VariableNotFoundException {
		/*
		 * Because the dpBackUp implementation by Hoey needs it.
		 */
		
		/* Collect f1 = P(S, Mj)  */
		DD f1 = startBelief;
		
		/* Collect f2 = P(Aj | Mj) x P(Oi', S', Aj) x P (S', Aj, S) */
		DD[] f2 = 
				ArrayUtils.addAll(
						ArrayUtils.add(
								ipomdp.currentTi.get(actName), 
								ipomdp.currentAjGivenMj), 
						ipomdp.currentOi.get(actName));
		
//		/* Collect f3 = f1 x f2 */
//		DD[] f3 = ArrayUtils.addAll(f2, f1);
		
		/* Collect tau = Sumout [Oj'] P(Oj', S', Aj) x P(Mj', Mj, Oj', Aj) */
//		DD tau = OP.addMultVarElim(
//					ArrayUtils.add(
//							ipomdp.currentOj, 
//							ipomdp.currentMjTfn),
//					ipomdp.obsJVarPrimeIndices);
		
		DD tau = ipomdp.currentTau;
		
		/* Perform the sum out */
		DD f3 = 
				OP.addMultVarElim(
						ArrayUtils.add(f2, tau), 
						ipomdp.AjIndex);
		
		DD nextBelief = 
				OP.addMultVarElim(
						f3, ArrayUtils.subarray(ipomdp.stateVarIndices, 0, ipomdp.S.size() - 1));
		
		/*
		 * Multiply and sum out
		 * 
		 * Watch out for last index in stateVarPrimeIndices being A_j'
		 */
		DD norm = 
				OP.addMultVarElim(
						nextBelief,
						ArrayUtils.subarray(
								ipomdp.stateVarPrimeIndices, 
								0, 
								ipomdp.stateVarPrimeIndices.length));
		
		return norm;
	}
	
	public static DD[] getCpts(
			IPOMDP ipomdp,
			DD[] startBelief, 
			String actName) throws ZeroProbabilityObsException, VariableNotFoundException {
		/*
		 * Because the dpBackUp implementation by Hoey needs it.
		 * 
		 * Returns all CPTs for marginalization inside Hoey's symbolic perseus.
		 */
		
//		/* f(S, Mj) does not depend on Aj */
//		DD[] f1 = startBelief;
//		
		/* [f(S', Aj, S), f(Aj, Mj)] */
		DD[] f2 = ArrayUtils.add(ipomdp.currentTi.get(actName), ipomdp.currentAjGivenMj);
		
		/* [f(S', Aj, S), f(Aj, Mj), f(Oi', Aj, S)] */
		f2 = ArrayUtils.addAll(f2, ipomdp.currentOi.get(actName));
		
//		/* tau */
//		DD[] f3 = ArrayUtils.add(ipomdp.currentOj, ipomdp.currentMjTfn);
		
		/* Compute tau */
//		DD tau = OP.addMultVarElim(f3, ipomdp.obsJVarPrimeIndices);
		
		DD[] f4 = ArrayUtils.add(f2, ipomdp.currentTau);
		
		DD addOutAj = OP.addMultVarElim(f4, ipomdp.AjIndex);
		
//		DD[] cpts = new DD[f4.length];
//		
//		for (int i = 0; i < f4.length; i++)
//			cpts[i] = OP.addout(f4[i], ipomdp.AjIndex);
		
		return ArrayUtils.addAll(startBelief, addOutAj);
	}
	
	// ------------------------------------------------------------------------------------------
	
	public static DD[] factorInteractiveBelief(IPOMDP ipomdp, DD belief) {
		/*
		 * Factors belief point from joint distribution to individual variables
		 * 
		 * Exactly similar to Hoey's implementation for POMDPs
		 */
		ipomdp.setGlobals();
		
		DD[] fbs = new DD[ipomdp.stateVarIndices.length - 1];
		
		/* For each state var, summout everything else except A_j*/
		for (int varId = 0; varId < ipomdp.stateVarIndices.length - 1; varId++) {
			fbs[varId] = OP.addMultVarElim(belief,
					ArrayUtils.remove(
							ArrayUtils.subarray(
									ipomdp.stateVarIndices,
									0,
									ipomdp.stateVarIndices.length - 1), varId));
		}
		
		return fbs;
	}
	
	public static DD unFactorInteractiveBelief(IPOMDP ipomdp, DD[] factoredBelief) {
		/*
		 * Factors belief point from joint distribution to individual variables
		 * 
		 * Exactly similar to Hoey's implementation for POMDPs
		 */
		ipomdp.setGlobals();
		
		DD fbs = OP.reorder(OP.multN(factoredBelief));
		DD norm = OP.addMultVarElim(fbs, ArrayUtils.subarray(ipomdp.stateVarIndices, 0, ipomdp.S.size() - 1));

		return OP.reorder(OP.div(fbs, norm));
	}
	
	public static DD[][] factorInteractiveBeliefRegion(IPOMDP ipomdp, List<DD> iBeliefRegion) {
		/*
		 * Factors the full belief region represented as a list
		 */
		DD[][] iBelRegion = new DD[iBeliefRegion.size()][ipomdp.nStateVars];
		
		/* Convert to array because accessing known elements is faster in arrays */
		DD[] iBeliefRegionArray = iBeliefRegion.toArray( new DD[iBeliefRegion.size()]);
		
		for (int i = 0; i < iBeliefRegion.size(); i++)
			iBelRegion[i] = 
				InteractiveBelief.factorInteractiveBelief(
						ipomdp, 
						iBeliefRegionArray[i]);
				
		return iBelRegion;
	}
	
	// ------------------------------------------------------------------------------------------
	
	public static HashMap<String, HashMap<String, Float>> toStateMap(IPOMDP ipomdp, DD belState) {
		/*
		 * Makes a hashmap of belief state and values and returns it
		 */
		ipomdp.setGlobals();
		
		HashMap<String, HashMap<String, Float>> beliefs = 
				new HashMap<String, HashMap<String, Float>>();
		
		/* Factor the belief state into individual variables */
		DD[] fbs = new DD[ipomdp.stateVarIndices.length - 1];
		for (int varId = 0; varId < ipomdp.stateVarIndices.length - 1; varId++) {
			
			fbs[varId] = OP.addMultVarElim(belState,
					ArrayUtils.remove(
							ArrayUtils.subarray(
									ipomdp.stateVarIndices, 
									0, 
									ipomdp.stateVarIndices.length - 1), varId));
			
			/* Make state variable name */
			String name = ipomdp.S.get(varId).name;
			
			/* Get respective belief for the variable */
			DD[] varChildren = fbs[varId].getChildren();
			HashMap<String, Float> childVals = new HashMap<String, Float>();
			
			if (varChildren == null) {
				for (int i=0; i < ipomdp.stateVars[varId].arity; i++) {
					childVals.put(Global.valNames[varId][i], new Float(fbs[varId].getVal()));
				}
			}
			
			else {
				for (int i=0; i < ipomdp.stateVars[varId].arity; i++) {
					
					if (varChildren[i].getVal() == 0.0)
						continue;
					
					childVals.put(Global.valNames[varId][i], new Float(varChildren[i].getVal()));
				}
			}
			
			beliefs.put(name, childVals);
		}
		
		return beliefs;
	}
	
	// ---------------------------------------------------------------------------------------------
	
	public static String toDot(IPOMDP ipomdp, DD belief) {
		/*
		 * Converts to a dot format node for graphviz
		 */
		
		String endl = "\r\n";
		String dotString = "[";
		
		dotString += "label=\"{BELIEF" + endl;
		
		HashMap<String, HashMap<String, Float>> stateMap = 
				InteractiveBelief.toStateMap(ipomdp, belief);
		
		for (Entry<String, HashMap<String, Float>> sEntry : stateMap.entrySet()) {
			
			dotString += "|" + sEntry.getKey();
			
			for (Entry<String, Float> entry : sEntry.getValue().entrySet())
				dotString += "|{" + entry.getKey() + "|" + entry.getValue() + "}";  
		}

		dotString += "}\" , shape=record]";
		return dotString;
	}
	
	public static String getBeliefNodeLabel(IPOMDP ipomdp, DD belief) {
		/*
		 * Returns formatted and printable string for using with VizNodes
		 */
		String label = "";
		String endl = "<br>";
		
		HashMap<String, HashMap<String, Float>> sMap = 
				InteractiveBelief.toStateMap(ipomdp, belief);
		
		for (String stateVar: sMap.keySet()) {
			
			/* add state name */
			label += stateVar + endl;
			
			HashMap<String, Float> valProbs = sMap.get(stateVar);
			
			for (String valName : valProbs.keySet()) {
				
				/*
				 * if state var is M_j, create label <prob - lower level belief - Aj>
				 */
				if (stateVar.contains("M_j")) {
					
					String lowerLevelBelief = valName + " " +
							ipomdp.getLowerLevelBeliefLabel(valName).replace(",", "<br>");
					
					label += lowerLevelBelief + " = " + valProbs.get(valName) 
						+ endl +" Aj: " + ipomdp.getOptimalActionAtMj(valName) + endl + endl;
				}
				
				else label += valName + " = " + valProbs.get(valName) + endl;
			}
			
			label += "-------------------------" + endl;
		}
		
		return label;
	}
	
}
