/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops;

import java.util.List;
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

	@Override
	public DD beliefUpdate(POMDP model, DD b, int a, int[][] o) {

		DD[] OFao = OP.restrictN(model.OF[a], o);
		
		// concat b, TF and OF
		DD[] dynamicsArray = new DD[1 + model.Svars.length + model.Ovars.length];
		dynamicsArray[0] = b;
		System.arraycopy(model.TF[a], 0, dynamicsArray, 1, model.Svars.length);
		System.arraycopy(OFao, 0, dynamicsArray, 1 + model.TF.length, model.OF.length);

		DD nextBelState = OP.addMultVarElim(dynamicsArray, model.Svars);

		nextBelState = OP.primeVars(nextBelState, -(Global.valNames.size() / 2));
		DD obsProb = OP.addMultVarElim(nextBelState, model.Svars);

		if (obsProb.getVal() < 1e-8) return DDleaf.getDD(Float.NaN);

		nextBelState = OP.div(nextBelState, obsProb);

		return nextBelState;
	}

	@Override
	public DD beliefUpdate(POMDP model, DD b, String a, List<String> o) {

		// TODO Auto-generated method stub
		return null;
	}

}
