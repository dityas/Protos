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

	private static final long serialVersionUID = -2262326916798060288L;
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
	
	public HashMap<String, Float> getThetaBelief(
			HashMap<String, HashMap<String, Float>> beliefMap) {
		/*
		 * Computes Thetaj probabilities from the given beliefMap
		 */
		
		HashMap<String, Float> ThetajBelief = new HashMap<String, Float>();
		for (String frame : ((IPOMDP) this.DP).ThetaJ)
			ThetajBelief.put(frame, (float) 0.0);
		
		/* get Mj belief */
		HashMap<String, Float> MjBelief = beliefMap.get("M_j");
		
		/*
		 * Each Mj node maps to one Thetaj, so belief over Thetaj is sum of beliefs of Mj
		 * belonging to that Thetaj
		 */
		for (String model : MjBelief.keySet()) {
			
			int frameID = IPOMDP.getFrameIDFromVarName(model);
			String frameName = IPOMDP.getCanonicalName(frameID, "theta");
			
			float val = ThetajBelief.get(frameName);
			ThetajBelief.put(frameName, val + MjBelief.get(model));
		}
			
		return ThetajBelief;
	}
	
	public HashMap<String, HashMap<String, Float>> toMapWithTheta(DD belief) {
		/*
		 * Converts to beliefMap and adds belief over theta
		 */
		HashMap<String, HashMap<String, Float>> beliefMap = this.toMap(belief);
		beliefMap.put("Theta_j", this.getThetaBelief(beliefMap));
		
		return beliefMap;
	}
	
	@Override
	public DD beliefUpdate(
			DD belief, String action, String[] observations) throws ZeroProbabilityObsException {
		
		/*
		 * Level 1 belief update
		 * 
		 * P(S, Mj| Oi'=o) = 
		 * 		norm x Sumout[S, Mj, Thetaj, Aj] 
		 * 					f(S, Mj) x f(Thetaj, Mj) x f(Aj, Mj)  
		 * 					x f(S', S, Aj) x f(Oi'=o, S', Aj)
		 * 			 x Sumout[Oj'] 
		 * 					f(Oj', Aj, Thetaj, S') x f(Mj', Mj, Aj, Oj') 
		 */
		
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
		
		/* Collect f1 = P(S, Mj)  */
		DD f1 = belief;

		/* Collect f2 = P(Aj | Mj) x P(Thetaj| Mj) x P(Oi'=o, S', Aj) x P (S', Aj, S) */
		DD[] f2 = 
				ArrayUtils.addAll(
						ArrayUtils.addAll(
								DPRef.currentTi.get(action), 
								new DD[] {DPRef.currentAjGivenMj, DPRef.currentThetajGivenMj}), 
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
								DPRef.thetaVarPosition));
		
		if (norm.getVal() < 1e-8) 
			throw new ZeroProbabilityObsException(
					"Observation " + Arrays.toString(observations) 
					+ " not possible at belief " + belief);
		
		return OP.div(nextBelief, norm); 
	}
	
	@Override
	public DD getObsDist(DD belief, String action) {
		/*
		 * Because the dpBackUp implementation by Hoey needs it.
		 */
		
		IPOMDP DPRef = this.getIPOMDP();

		/* Collect f1 = P(S, Mj)  */
		DD f1 = belief;

		/* Collect f2 = P(Aj | Mj) x P(Thetaj| Mj) x P(Oi'=o, S', Aj) x P (S', Aj, S) */
		DD[] f2 = 
				ArrayUtils.addAll(
						ArrayUtils.addAll(
								DPRef.currentTi.get(action), 
								new DD[] {DPRef.currentAjGivenMj, DPRef.currentThetajGivenMj}), 
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

		/* compute normalization factor */
		DD norm = 
				OP.addMultVarElim(
						nextBelief, 
						ArrayUtils.subarray(
								DPRef.stateVarPrimeIndices, 
								0, 
								DPRef.thetaVarPosition));

		return norm;
	}
	
	public DD[] getCpts(
			DD belief, 
			String actName) throws ZeroProbabilityObsException {
		/*
		 * Because the dpBackUp implementation by Hoey needs it.
		 * 
		 * Returns all CPTs for marginalization inside Hoey's symbolic perseus.
		 */
		IPOMDP DPRef = this.getIPOMDP();

		/* Collect f1 = P(S, Mj)  */
		DD f1 = belief;

		/* Collect f2 = P(Aj | Mj) x P(Thetaj| Mj) x P(Oi'=o, S', Aj) x P (S', Aj, S) */
		DD[] f2 = 
				ArrayUtils.addAll(
						ArrayUtils.addAll(
								DPRef.currentTi.get(actName), 
								new DD[] {DPRef.currentAjGivenMj, DPRef.currentThetajGivenMj}), 
						DPRef.currentOi.get(actName));
		
		/* Get TAU */
		DD tau = DPRef.currentTau;
		
		/* assemble all factors in a single array */
		DD[] f3 = ArrayUtils.add(f2, tau);
		f3 = ArrayUtils.add(f3, DPRef.currentThetajGivenMj);
		
		DD addOutThetajAj = 
				OP.addMultVarElim(
						f3, 
						new int[] {
								DPRef.thetaVarPosition + 1, 
								DPRef.AjVarStartPosition + 1});
		
		return new DD[] {addOutThetajAj, f1};
	}
	
	@Override
	public DD[] factorBelief(DD belief) {
		/*
		 * Factors belief point from joint distribution to individual variables
		 * 
		 * Exactly similar to Hoey's implementation for POMDPs
		 */
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
		IPOMDP DPRef = this.getIPOMDP();
		
		HashMap<String, HashMap<String, Float>> beliefs = 
				new HashMap<String, HashMap<String, Float>>();
		
		/* Factor the belief state into individual variables */
		DD[] fbs = new DD[DPRef.thetaVarPosition];
		for (int varId = 0; varId < fbs.length; varId++) {
			
			fbs[varId] = OP.addMultVarElim(belief,
					ArrayUtils.remove(
							ArrayUtils.subarray(
									DPRef.stateVarIndices, 
									0, 
									DPRef.thetaVarPosition), varId));
			
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
		
		IPOMDP DPRef = this.getIPOMDP();
		
		DD[][] iBelRegion = 
				new DD[beliefRegion.size()][DPRef.S.subList(0, DPRef.AjVarStartPosition).size()];
		
		/* Use parallelization if Mj arity is huge */
		if (DPRef.S.get(DPRef.MjVarPosition).arity > 200)
			iBelRegion = 
				beliefRegion.parallelStream()
					.map(this::factorBelief)
					.toArray(DD[][]::new);
		
		else
			iBelRegion = 
				beliefRegion.stream()
					.map(this::factorBelief)
					.toArray(DD[][]::new);
				
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
