/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver.InteractiveBelief;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.ArrayUtils;

import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.ipomdpsolver.IPOMDP;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.MySet;
import thinclab.symbolicperseus.OP;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.Belief.Belief;

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
		 *      .---.                    .----.
		 *      |M_j|----------.-------->|M_j'|<----------------------.
		 *      '---'          |_______. '----'                       |
		 *      .---.                  '-->.---.______________.       |	
		 *      | S |--------------------->| S'|--,           |       |
		 *      '---'                      '---'  |           |       |
		 *                                        V           V       | 		 
		 *                                      .---.       .---.     |
		 *                                      |Oi'|       |Oj'|_____|
		 *                                      '---'       '---'
		 * 	 P_hat(S', M_j' | Oi'=o) = 
		 * 			Sigma[S] (P(S)) x Sigma[M_j] (P(M_j) x P(S', S, M_j) x P(Oi'=o, S', M_j))
		 * 		 		x	Sigma[Oj'] (P(Oj', S', M_j) x P(M_j', M_j, Oj')) 
		 */
		
		/*
		 * First reduce Oi based on observations
		 */
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
		
		/* Collect f1 = P(S, Mj) x P(S', S, Mj) */
		DD[] f1 = ArrayUtils.add(ipomdp.currentTi.get(actName), startBelief);
		
		/* Collect f2 = f1 x P(Oi'=o, S', Mj) */
		DD[] f2 = ArrayUtils.addAll(f1, restrictedOi);
		
		/* Collect f3 = P(Oj', S', Mj) x P(Mj', Mj, Oj') */
		DD[] f3 = ArrayUtils.add(ipomdp.currentOj, ipomdp.currentMjTfn);
		
		/* Collect f4 = f2 x f3 */
		DD[] f4 = ArrayUtils.addAll(f2, f3);
		
		/* Multiply and sum out */
		DD nextBelief = 
				OP.addMultVarElim(
						f4, 
						ArrayUtils.addAll(
								ipomdp.stateVarIndices, 
								ipomdp.obsJVarPrimeIndices));
		
		/* Shift indices */
		nextBelief = OP.primeVars(nextBelief, -(ipomdp.S.size() + ipomdp.Omega.size()));
		
		/* compute normalization factor */
		DD norm = OP.addMultVarElim(nextBelief, ipomdp.stateVarIndices);
		
		if (norm.getVal() < 1e-8) 
			throw new ZeroProbabilityObsException(
					"Observation " + Arrays.toString(observations) 
					+ " not possible at belief " + startBelief);
		
		return OP.div(nextBelief, norm); 
	}
	
}
