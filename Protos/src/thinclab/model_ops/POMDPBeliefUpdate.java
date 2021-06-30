/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.models.POMDP;

/*
 * @author adityas
 *
 */
public class POMDPBeliefUpdate implements BeliefUpdate<POMDP> {
	
	private static final Logger LOGGER = LogManager.getLogger(POMDPBeliefUpdate.class);

	@Override
	public DD beliefUpdate(POMDP model, DD b, int a, int[][] o) {

		DD[] OFao = OP.restrictN(model.OF[a], o);
		
		
		// concat b, TF and OF
		DD[] dynamicsArray = new DD[1 + model.Svars.length + model.Ovars.length];
		dynamicsArray[0] = b;
		System.arraycopy(model.TF[a], 0, dynamicsArray, 1, model.Svars.length);
		System.arraycopy(OFao, 0, dynamicsArray, 1 + model.Svars.length, model.Ovars.length);

		// Sumout[S] P(O'=o| S, A=a) x P(S'| S, A=a) x P(S)
		DD nextBelState = OP.addMultVarElim(dynamicsArray, model.Svars);

		nextBelState = OP.primeVars(nextBelState, -(Global.NUM_VARS / 2));
		DD obsProb = OP.addMultVarElim(nextBelState, model.Svars);

		if (obsProb.getVal() < 1e-8) return DDleaf.getDD(Float.NaN);

		nextBelState = OP.div(nextBelState, obsProb);

		return nextBelState;
	}

	@Override
	public DD beliefUpdate(POMDP model, DD b, String a, List<String> o) {

		int actIndex = model.A.indexOf(a);
		int[][] obs = new int[2][model.Ovars.length];
		
		IntStream.range(0, o.size()).boxed().forEach(i -> {
			obs[0][i] = model.Ovars[i] + (Global.NUM_VARS / 2);
			obs[1][i] = Global.valNames.get(model.Ovars[i] - 1).indexOf(o.get(i)) + 1;
		});
		
		return this.beliefUpdate(model, b, actIndex, obs);
	}

}
