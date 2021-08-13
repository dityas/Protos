/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class POMDP extends PBVISolvablePOMDPBasedModel {

	private static final Logger LOGGER = LogManager.getLogger(POMDP.class);

	public POMDP(List<String> S, List<String> O, String A, HashMap<String, Model> dynamics, HashMap<String, DD> R,
			DD initialBelief, float discount) {

		super(S, O, A, dynamics, R, initialBelief, discount);
	}

	@Override
	public String toString() {

		var builder = new StringBuilder();

		builder.append("POMDP: [").append("\r\n");
		builder.append("S : ").append(this.S).append("\r\n");
		builder.append("S vars : ").append(this.i_S).append("\r\n");
		builder.append("O : ").append(this.O).append("\r\n");
		builder.append("O vars : ").append(this.i_Om).append("\r\n");
		builder.append("A : ").append(this.A).append("\r\n");
		builder.append("A var : ").append(this.i_A).append("\r\n");

		builder.append("TF funct. : ").append("\r\n");

		builder.append("OF funct. : ").append(this.OF).append("\r\n");

		builder.append("R : ").append("\r\n");

		builder.append("b : ").append(this.b_i).append("\r\n");
		builder.append("discount : ").append(this.discount).append("\r\n");

		builder.append("]").append("\r\n");

		return builder.toString();
	}

	// --------------------------------------------------------------------------------------
	// Implementation of BeliefBasedAgent<DD>

	@Override
	public DD beliefUpdate(DD b, int a, List<Integer> o) {

		var OFao = DDOP.restrict(this.OF.get(a), i_Om_p, o);

		// concat b, TF and OF
		var dynamicsArray = new ArrayList<DD>(1 + i_S.size() + i_Om.size());
		dynamicsArray.add(b);
		dynamicsArray.addAll(TF.get(a));
		dynamicsArray.addAll(OFao);

		// Sumout[S] P(O'=o| S, A=a) x P(S'| S, A=a) x P(S)
		DD nextBelState = DDOP.addMultVarElim(dynamicsArray, i_S);

		nextBelState = DDOP.primeVars(nextBelState, -(Global.NUM_VARS / 2));
		DD obsProb = DDOP.addMultVarElim(List.of(nextBelState), i_S);

		if (obsProb.getVal() < 1e-8)
			return DDleaf.getDD(Float.NaN);

		nextBelState = DDOP.div(nextBelState, obsProb);

		return nextBelState;
	}

	@Override
	public DD beliefUpdate(DD b, String a, List<String> o) {

		int actIndex = Collections.binarySearch(this.A, a);
		var obs = new ArrayList<Integer>(i_Om.size());

		for (int i = 0; i < i_Om_p.size(); i++) {

			obs.add(Collections.binarySearch(Global.valNames.get(i_Om.get(i) - 1), o.get(i)) + 1);
		}

		return this.beliefUpdate(b, actIndex, obs);
	}

	@Override
	public DD obsLikelihoods(DD b, int a) {

		var dynamics = new ArrayList<DD>(1 + S().size() + Om().size());
		dynamics.add(b);
		dynamics.addAll(T().get(a));
		dynamics.addAll(O().get(a));

		var _vars = new ArrayList<Integer>(i_S().size() + i_S_p.size());
		_vars.addAll(i_S);
		_vars.addAll(i_S_p);

		return DDOP.addMultVarElim(dynamics, _vars);
	}

	// ----------------------------------------------------------------------------------------
	// PBVISolvable implementations

	@Override
	public Tuple<Float, DD> Gaoi(DD b, int a, List<DD> alphaPrimes) {

		List<Tuple<Float, DD>> Gaoi = new ArrayList<>(oAll.size());

		for (int _o = 0; _o < oAll.size(); _o++) {

			List<Tuple<Float, DD>> _Gaoi = new ArrayList<>();

			for (int i = 0; i < alphaPrimes.size(); i++) {

				var _factors = new ArrayList<DD>(TF.get(a).size() + OF.get(a).size() + 1);
				_factors.addAll(T().get(a));
				_factors.addAll(DDOP.restrict(O().get(a), i_Om_p, oAll.get(_o)));
				_factors.add(alphaPrimes.get(i));

				DD gaoi = DDOP.mult(DDleaf.getDD(discount), DDOP.addMultVarElim(_factors, i_S_p));
				_Gaoi.add(Tuple.of(DDOP.dotProduct(b, gaoi, i_S), gaoi));

			}

			Gaoi.add(_Gaoi.stream().reduce(Tuple.of(Float.NEGATIVE_INFINITY, DDleaf.zero),
					(a1, a2) -> a1._0() > a2._0() ? a1 : a2));

		}

		return Gaoi.stream().reduce(Tuple.of(0f, DDleaf.zero),
				(t1, t2) -> Tuple.of(t1._0() + t2._0(), DDOP.add(t1._1(), t2._1())));
	}

}
