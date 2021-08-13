/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models;

import java.util.HashMap;
import java.util.List;
import thinclab.legacy.DD;
import thinclab.legacy.Global;

/*
 * @author adityas
 *
 */
public class IPOMDP extends PBVISolvablePOMDPBasedModel {
	
	public final List<String> Aj;
	public final int i_Aj;

	public IPOMDP(List<String> S, List<String> O, String A, String Aj, HashMap<String, Model> dynamics, HashMap<String, DD> R, 
			DD initialBelief, float discount) {

		super(S, O, A, dynamics, R, initialBelief, discount);
		
		this.i_Aj = Global.varNames.indexOf(Aj) + 1;
		this.Aj = Global.valNames.get(i_Aj - 1);
	}

	@Override
	public DD beliefUpdate(DD b, int a, List<Integer> o) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DD beliefUpdate(DD b, String a, List<String> o) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DD obsLikelihoods(DD b, int a) {

		// TODO Auto-generated method stub
		return null;
	}

}
