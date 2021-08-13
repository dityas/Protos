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
import java.util.stream.Collectors;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class IPOMDP extends PBVISolvablePOMDPBasedModel {

	public final List<String> Aj;
	public final int i_Aj;
	public final int i_Mj;
	public final int i_Thetaj;
	public final List<String> Thetaj;

	public List<Tuple<Integer, PBVISolvablePOMDPBasedModel>> frames_j;

	public IPOMDP(List<String> S, List<String> O, String A, String Aj, String Mj, String Thetaj,
			List<Tuple<String, Model>> frames_j, HashMap<String, Model> dynamics, HashMap<String, DD> R,
			DD initialBelief, float discount) {

		// initialize dynamics like POMDP
		super(S, O, A, dynamics, R, initialBelief, discount);

		// random variable for opponent's action
		this.i_Aj = Global.varNames.indexOf(Aj) + 1;
		this.Aj = Global.valNames.get(i_Aj - 1);

		// opponent's model variable
		this.i_Mj = Global.varNames.indexOf(Mj) + 1;

		// random variable for frame of the opponent
		this.i_Thetaj = Global.varNames.indexOf(Thetaj) + 1;
		this.Thetaj = Global.valNames.get(i_Thetaj - 1);

		this.frames_j = frames_j.stream().map(t -> Tuple.of(Global.valNames.get(i_Thetaj - 1).indexOf(t._0()), (PBVISolvablePOMDPBasedModel) t._1()))
				.collect(Collectors.toList());
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
