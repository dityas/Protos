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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;
import thinclab.legacy.TypedCacheMap;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;

/*
 * @author adityas
 *
 */
public class POMDP extends PBVISolvablePOMDPBasedModel {

	private TypedCacheMap<Tuple3<DD, Integer, List<Integer>>, Float> obsProbCache = new TypedCacheMap<>(1000);
	
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

	public List<DD> Gaoi(final int a, final DD b, final List<DD> alphas, ReachabilityGraph g) {

		var gaoi = new ArrayList<DD>(oAll.size());

		for (int o = 0; o < oAll.size(); o++) {

			var _o = oAll.get(o);
			var b_p = g.getNodeAtEdge(b, Tuple.of(a, _o));
			if (b_p == null) {

				b_p = beliefUpdate(b, a, _o);
				g.addEdge(b, Tuple.of(a, _o), b_p);
			}

			float bestVal = Float.NEGATIVE_INFINITY;
			int bestAlpha = -1;

			for (int i = 0; i < alphas.size(); i++) {

				float val = DDOP.dotProduct(b_p, alphas.get(i), i_S());

				if (val >= bestVal) {

					bestVal = val;
					bestAlpha = i;

				}

			}

			/*
			 * var factors = new ArrayList<DD>(S.size() + O.size() + 1);
			 * factors.add(DDOP.primeVars(alphas.get(bestAlpha), Global.NUM_VARS / 2));
			 * factors.addAll(T().get(a)); factors.addAll(DDOP.restrict(O().get(a), i_Om_p,
			 * _o));
			 * 
			 * gaoi.add(DDOP.addMultVarElim(factors, i_S_p()));
			 */

			gaoi.add(alphas.get(bestAlpha));
		}

		return gaoi;
	}
	
	public DD project(DD d, int a, List<Integer> o) {
		
		var factors = new ArrayList<DD>(S.size() + Om().size() + 1);
		factors.addAll(DDOP.restrict(O().get(a), i_Om_p, o));
		factors.addAll(T().get(a));
		factors.add(DDOP.primeVars(d, Global.NUM_VARS / 2));
		
		var results = DDOP.addMultVarElim(factors, i_S_p());
		
		return results;
	}

	public Tuple<Integer, DD> backup(DD b, List<DD> alphas, ReachabilityGraph g) {

		var Gao = IntStream.range(0, A().size()).mapToObj(a -> Gaoi(a, b, alphas, g)).collect(Collectors.toList());

		int bestA = -1;
		float bestVal = Float.NEGATIVE_INFINITY;
		for (int a = 0; a < A().size(); a++) {

			float val = 0.0f;
			for (int o = 0; o < Gao.get(a).size(); o++) {
				
				var b_p = g.getNodeAtEdge(b, Tuple.of(a, oAll.get(o)));
				
				var key = Tuple.of(b, a, oAll.get(o));
				var prob = obsProbCache.get(key);
				
				if (prob == null) {
					var likelihoods = obsLikelihoods(b, a);
					prob = DDOP.restrict(likelihoods, i_Om_p, oAll.get(o)).getVal();
					obsProbCache.put(key, prob);
				}
				
				val += (prob * DDOP.dotProduct(b_p, Gao.get(a).get(o), i_S()));
			}

			val *= discount;
			val += DDOP.dotProduct(b, R().get(a), i_S());

			if (val >= bestVal) {

				bestVal = val;
				bestA = a;
			}

		}

		var vec = DD.zero;
		for (int o = 0; o < Gao.get(bestA).size(); o++)
			vec = DDOP.add(project(Gao.get(bestA).get(o), bestA, oAll.get(o)), vec);

		return Tuple.of(bestA, DDOP.add(R().get(bestA), DDOP.mult(DDleaf.getDD(discount), vec)));
	}

}
