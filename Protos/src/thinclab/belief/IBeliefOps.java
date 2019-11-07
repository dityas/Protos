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
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.decisionprocesses.IPOMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public class IBeliefOps extends BeliefOperations {
	
	/*
	 * Operations on IPOMDP beliefs
	 */
	
	private static final Logger LOGGER = Logger.getLogger(IBeliefOps.class);
	
	public IBeliefOps(IPOMDP ipomdp) {
		/*
		 * Interactive belief only works for IPOMDPs
		 */
		this.DP = (IPOMDP) ipomdp;
		LOGGER.debug("IBeliefOps init for DP " + this.DP);
	}
	
	// -------------------------------------------------------------------------------------
	
	public IPOMDP getIPOMDP() {
		return (IPOMDP) this.DP;
	}
	
	@Override
	public DD beliefUpdate(
			DD belief, String action, String[] observations) throws ZeroProbabilityObsException {
		
		/*
		 * Level 1 belief update
		 * 
		 * P(S, Mj| Oi'=o) = 
		 * 		norm x Sumout[S, Mj, Aj1, Aj2, ... Ajn] 
		 * 					f(S, Mj) x f(Aj1, Mj) x f(Aj2, Mj) x ... x f(Ajn, Mj) 
		 * 					x f(S', S, Aj1, Aj2, ... Ajn) x f(Oi'=o, S', Aj1, Aj2, ... Ajn)
		 * 			 x Sumout[Oj1', Oj2', ... Ojn'] 
		 * 					f(Oj1', Aj1, S') x f(Oj2', Aj2, S') x ... x f(Ojn', Ajn, S')
		 * 					x f(Mj', Mj, Aj1, Aj2, ... Ajn, Oj1', Oj2', ... Ojn') 
		 */
		
		/* set globals and clear caches */
		this.DP.setGlobals();
		IPOMDP DPRef = this.getIPOMDP();
		
		/* First reduce Oi based on observations */
		int[] obsVals = 
				new int[DPRef.Omega.size() - DPRef.OmegaJNames.size()];
		
		for (int o = 0; o < obsVals.length; o++) {
			int val = DPRef.findObservationByName(o, observations[o]) + 1;
			
			if (val < 0) {
				LOGGER.error(
						"Obs Variable " + DPRef.Omega.get(o).name
						+ " does not take value " + observations[o]);
				System.exit(-1);
			}
			
			else obsVals[o] = val;
		}
		
		/* Restrict Oi */
		DD[] restrictedOi = 
				OP.restrictN(
						DPRef.currentOi.get(action), 
						IPOMDP.stackArray(
								DPRef.obsIVarPrimeIndices, obsVals));
		LOGGER.debug("restricted Oi is ");
		for (DD oi : restrictedOi)
			LOGGER.debug(oi.toDDTree());
		
		/* Collect f1 = P(S, Mj)  */
		DD f1 = belief;

		/* Collect f2 = P(Aj | Mj) x P(Oi'=o, S', Aj) x P (S', Aj, S) */
		DD[] f2 = 
				ArrayUtils.addAll(
						ArrayUtils.addAll(
								DPRef.currentTi.get(action), 
								DPRef.currentAjGivenMj), 
						restrictedOi);
		
		/* Get TAU */
		DD tau = DPRef.currentTau;
		
		/* Perform the sum out */
		DD nextBelief = 
				OP.addMultVarElim(
						ArrayUtils.add(
								ArrayUtils.addAll(f2, f1), 
								tau), 
						DPRef.stateVarIndices);

		/* Shift indices */
		nextBelief = OP.primeVars(nextBelief, -(DPRef.S.size() + DPRef.Omega.size()));
		
		/* compute normalization factor */
		DD norm = 
				OP.addMultVarElim(
						nextBelief, 
						ArrayUtils.subarray(
								DPRef.stateVarIndices, 
								0, 
								DPRef.AjVarStartPosition));
		LOGGER.debug(norm);
		if (norm.getVal() < 1e-8) 
			throw new ZeroProbabilityObsException(
					"Observation " + Arrays.toString(observations) 
					+ " not possible at belief " + belief);
		
		return OP.div(nextBelief, norm); 
	}
	
	@Override
	public DD norm(DD belief, String action) {
		/*
		 * Because the dpBackUp implementation by Hoey needs it.
		 */
		
		/* set globals and clear caches */
		this.DP.setGlobals();
		
		IPOMDP DPRef = this.getIPOMDP();

		/* Collect f1 = P(S, Mj)  */
		DD f1 = belief;

		/* Collect f2 = P(Aj | Mj) x P(Oi'=o, S', Aj) x P (S', Aj, S) */
		DD[] f2 = 
				ArrayUtils.addAll(
						ArrayUtils.addAll(
								DPRef.currentTi.get(action), 
								DPRef.currentAjGivenMj), 
						DPRef.currentOi.get(action));
		
		/* Get TAU */
		DD tau = DPRef.currentTau;
		
		/* Perform the sum out */
		DD nextBelief = 
				OP.addMultVarElim(
						ArrayUtils.add(
								ArrayUtils.addAll(f2, f1), 
								tau), 
						DPRef.stateVarIndices);

		/* Shift indices */
		nextBelief = OP.primeVars(nextBelief, -(DPRef.S.size() + DPRef.Omega.size()));

		/* compute normalization factor */
		DD norm = 
				OP.addMultVarElim(
						nextBelief, 
						ArrayUtils.subarray(
								DPRef.stateVarIndices, 
								0, 
								DPRef.AjVarStartPosition));
		
		return norm;
	}
	
//	public static DD[] getCpts(
//			IPOMDP ipomdp,
//			DD[] startBelief, 
//			String actName) throws ZeroProbabilityObsException, VariableNotFoundException {
//		/*
//		 * Because the dpBackUp implementation by Hoey needs it.
//		 * 
//		 * Returns all CPTs for marginalization inside Hoey's symbolic perseus.
//		 */
//		
////		/* f(S, Mj) does not depend on Aj */
////		DD[] f1 = startBelief;
////		
//		/* [f(S', Aj, S), f(Aj, Mj)] */
//		DD[] f2 = ArrayUtils.add(ipomdp.currentTi.get(actName), ipomdp.currentAjGivenMj);
//		
//		/* [f(S', Aj, S), f(Aj, Mj), f(Oi', Aj, S)] */
//		f2 = ArrayUtils.addAll(f2, ipomdp.currentOi.get(actName));
//		
////		/* tau */
////		DD[] f3 = ArrayUtils.add(ipomdp.currentOj, ipomdp.currentMjTfn);
//		
//		/* Compute tau */
////		DD tau = OP.addMultVarElim(f3, ipomdp.obsJVarPrimeIndices);
//		
//		DD[] f4 = ArrayUtils.add(f2, ipomdp.currentTau);
//		
//		DD addOutAj = OP.addMultVarElim(f4, ipomdp.AjIndex);
//		
////		DD[] cpts = new DD[f4.length];
////		
////		for (int i = 0; i < f4.length; i++)
////			cpts[i] = OP.addout(f4[i], ipomdp.AjIndex);
//		
//		return ArrayUtils.addAll(startBelief, addOutAj);
//	}
	
	@Override
	public DD[] factorBelief(DD belief) {
		/*
		 * Factors belief point from joint distribution to individual variables
		 * 
		 * Exactly similar to Hoey's implementation for POMDPs
		 */
		
		/* set globals and clear caches */
		this.DP.setGlobals();
		IPOMDP DPRef = this.getIPOMDP();
		
		DD[] fbs = new DD[DPRef.S.subList(0, DPRef.AjVarStartPosition).size()];
		
		/* For each state var, summout everything else except A_j*/
		for (int varId = 0; varId < fbs.length; varId++) {
			fbs[varId] = OP.addMultVarElim(belief,
					ArrayUtils.remove(
							ArrayUtils.subarray(
									DPRef.stateVarIndices,
									0,
									DPRef.AjVarStartPosition), varId));
		}
		
		return fbs;
	}

	@Override
	public HashMap<String, HashMap<String, Float>> toMap(DD belief) {
		/*
		 * Makes a hashmap of belief state and values and returns it
		 */
		
		/* set globals and clear caches */
		this.DP.setGlobals();
		IPOMDP DPRef = this.getIPOMDP();
		
		HashMap<String, HashMap<String, Float>> beliefs = 
				new HashMap<String, HashMap<String, Float>>();
		
		/* Factor the belief state into individual variables */
		DD[] fbs = new DD[DPRef.AjVarStartPosition];
		for (int varId = 0; varId < fbs.length; varId++) {
			
			fbs[varId] = OP.addMultVarElim(belief,
					ArrayUtils.remove(
							ArrayUtils.subarray(
									DPRef.stateVarIndices, 
									0, 
									DPRef.AjVarStartPosition), varId));
			
			/* Make state variable name */
			String name = DPRef.S.get(varId).name;
			
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
					
					if (varChildren[i].getVal() == 0.0)
						continue;
					
					childVals.put(Global.valNames[varId][i], new Float(varChildren[i].getVal()));
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
		this.DP.setGlobals();
		IPOMDP DPRef = this.getIPOMDP();
		
		DD[][] iBelRegion = 
				new DD[beliefRegion.size()][DPRef.S.subList(0, DPRef.AjVarStartPosition).size()];
		
		/* Convert to array because accessing known elements is faster in arrays */
		DD[] iBeliefRegionArray = beliefRegion.toArray(new DD[beliefRegion.size()]);
		
		for (int i = 0; i < beliefRegion.size(); i++)
			iBelRegion[i] = DPRef.factorBelief(iBeliefRegionArray[i]);
				
		return iBelRegion;
	}
	
	// ---------------------------------------------------------------------------------------
	
	public String toDot(DD belief) {
		/*
		 * Converts to a dot format node for graphviz
		 */
		
		String endl = "\r\n";
		String dotString = "[";
		
		dotString += "label=\"{BELIEF" + endl;
		
		HashMap<String, HashMap<String, Float>> stateMap = 
				this.toMap(belief);
		
		for (Entry<String, HashMap<String, Float>> sEntry : stateMap.entrySet()) {
			
			dotString += "|" + sEntry.getKey();
			
			for (Entry<String, Float> entry : sEntry.getValue().entrySet())
				dotString += "|{" + entry.getKey() + "|" + entry.getValue() + "}";  
		}

		dotString += "}\" , shape=record]";
		return dotString;
	}
	
}
