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
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;

/*
 * @author adityas
 *
 */
public class POMDP extends PBVISolvablePOMDPBasedModel {


	private static final Logger LOGGER = LogManager.getLogger(POMDP.class);

	public POMDP(List<String> S, List<String> O, String A, HashMap<String, Model> dynamics, HashMap<String, DD> R,
			float discount) {

		super(S, O, A, dynamics, R, discount);
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
			return DDleaf.getDD(0.0f);

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

	public DD project(DD d, int a, List<Integer> o) {

		var factors = new ArrayList<DD>(S.size() + Om().size() + 1);
		factors.addAll(DDOP.restrict(O().get(a), i_Om_p, o));
		factors.addAll(T().get(a));
		factors.add(DDOP.primeVars(d, Global.NUM_VARS / 2));

		var results = DDOP.addMultVarElim(factors, i_S_p());

		return results;
	}

	public Tuple<Float, Integer> Gaoi(final DD b, final int a, final List<Integer> o, final List<DD> alphas) {

		float bestVal = Float.NEGATIVE_INFINITY;
		int bestAlpha = -1;

		for (int i = 0; i < alphas.size(); i++) {

			float val = DDOP.dotProduct(b, alphas.get(i), i_S());
			if (val >= bestVal) {

				bestVal = val;
				bestAlpha = i;

			}

		}

		if (bestAlpha < 0) {

			LOGGER.error(String.format("Could not find best alpha vector while backing up at %s", b));
			LOGGER.debug(String.format("All Alphas are: %s", alphas));
			System.exit(-1);
		}

		// LOGGER.debug(String.format("Best is %s", Tuple.of(bestVal, bestAlpha)));
		return Tuple.of(bestVal, bestAlpha);
	}

	public Tuple<Integer, DD> backup(DD b, List<DD> alphas, ReachabilityGraph g) {

		int bestA = -1;
		float bestVal = Float.NEGATIVE_INFINITY;

		var nextBels = belCache.get(b);
		
		if (nextBels == null) {
		
			// build cache entry for this belief
			nextBels = new HashMap<Integer, List<Tuple3<Integer, DD, Float>>>(A().size());
			
			for (int a = 0; a < A().size(); a++) {
				
				var nextBa = new ArrayList<Tuple3<Integer, DD, Float>>();
				var likelihoods = obsLikelihoods(b, a);
				
				for (int o = 0; o < oAll.size(); o++) {
					
					var prob = DDOP.restrict(likelihoods, i_Om_p, oAll.get(o)).getVal();
					
					if (prob < 1e-6f)
						continue;
					
					// perform belief update and cache next belief
					var b_n = g.getNodeAtEdge(b, Tuple.of(a, oAll.get(o)));
					if (b_n == null)
						b_n = beliefUpdate(b, a, oAll.get(o));
					
					nextBa.add(Tuple.of(o, b_n, prob));
				}
				
				nextBels.put(a, nextBa);
			}
			
			belCache.put(b, nextBels);
		}

		// compute everything from the cached entries
		var Gao = new ArrayList<ArrayList<Tuple<Integer, DD>>>(A().size());

		for (int a = 0; a < A().size(); a++) {

			var nextBa = nextBels.get(a);
			float val = 0.0f;
			var argmax_iGaoi = new ArrayList<Tuple<Integer, DD>>(nextBa.size());

			// project to next belief for all observations and compute values
			for (int o = 0; o < nextBa.size(); o++) {

				var obsIndex = nextBa.get(o)._0();
				var b_n = nextBa.get(o)._1();
				var prob = nextBa.get(o)._2();

				var bestAlpha = Gaoi(b_n, a, oAll.get(obsIndex), alphas);

				argmax_iGaoi.add(Tuple.of(obsIndex, alphas.get(bestAlpha._1())));
				val += (prob * bestAlpha._0());
			}

			// compute value of a and check best action and best value
			val *= discount;
			val += DDOP.dotProduct(b, R().get(a), i_S());

			if (val >= bestVal) {

				bestVal = val;
				bestA = a;
			}

			Gao.add(argmax_iGaoi);
		}

		var vec = DD.zero;
		for (int o = 0; o < Gao.get(bestA).size(); o++) {

			var _gao = Gao.get(bestA).get(o);
			vec = DDOP.add(project(_gao._1(), bestA, oAll.get(_gao._0())), vec);
		}

		var newVec = DDOP.add(R().get(bestA), DDOP.mult(DDleaf.getDD(discount), vec));

		return Tuple.of(bestA, newVec);
	}

}
