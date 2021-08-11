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
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.solver.PBVISolvable;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class POMDP implements POSeqDecMakingModel<DD>, PBVISolvable {

	public final List<String> S;
	public final List<String> O;
	public final List<String> A;
	public final DD b_i;
	public final float discount;

	public final List<Integer> i_S;
	public final List<Integer> i_Om;
	public final int i_A;

	public final List<Integer> i_S_p;
	public final List<Integer> i_Om_p;
	public final List<List<Integer>> oAll;

	public final List<List<DD>> TF;
	public final List<List<DD>> OF;
	public final List<DD> R;

	private static final Logger LOGGER = LogManager.getLogger(POMDP.class);

	public POMDP(List<String> S, List<String> O, String A, HashMap<String, Model> dynamics, HashMap<String, DD> R,
			DD initialBelief, float discount) {

		// variable names
		this.S = this.sortByVarOrdering(S, Global.varNames);
		this.O = this.sortByVarOrdering(O, Global.varNames);
		this.A = Global.valNames.get(Global.varNames.indexOf(A));

		// variable indices
		this.i_S = this.S.stream().map(s -> Global.varNames.indexOf(s) + 1).collect(Collectors.toList());
		this.i_Om = this.O.stream().map(o -> Global.varNames.indexOf(o) + 1).collect(Collectors.toList());
		this.i_A = Global.varNames.indexOf(A) + 1;

		// primed variable indices
		this.i_S_p = this.i_S.stream().map(i -> i + (Global.NUM_VARS / 2)).collect(Collectors.toList());
		this.i_Om_p = this.i_Om.stream().map(i -> i + (Global.NUM_VARS / 2)).collect(Collectors.toList());

		// all possible observations
		this.oAll = DDOP.cartesianProd(i_Om.stream().map(
				o -> IntStream.range(1, Global.valNames.get(o - 1).size() + 1).boxed().collect(Collectors.toList()))
				.collect(Collectors.toList()));

		// take out DBNs from set of models
		var dyn = new HashMap<String, DBN>(5);
		dyn.putAll(dynamics.entrySet().stream().filter(e -> e.getValue() instanceof DBN)
				.collect(Collectors.toMap(e -> e.getKey(), e -> (DBN) e.getValue())));

		// Populate dynamics for missing actions
		Global.valNames.get(this.i_A - 1).stream().forEach(a ->
			{

				if (!dyn.containsKey(a)) {

					LOGGER.warn(String.format("Dynamics not defined for action %s. "
							+ "Will apply with SAME transitions and random observations for that action", a));
					dyn.put(a, new DBN(new HashMap<Integer, DD>(1)));
				}
			});

		this.TF = this.A.stream().map(a -> this.getTransitionFunction(dyn.get(a))).collect(Collectors.toList());
		this.OF = this.A.stream().map(a -> this.getObsFunction(dyn.get(a))).collect(Collectors.toList());

		this.R = this.A.stream().map(a -> R.containsKey(a) ? R.get(a) : DD.zero).collect(Collectors.toList());

		this.b_i = initialBelief;
		this.discount = discount;
	}

	protected List<DD> getTransitionFunction(DBN dbn) {

		var Ta = i_S.stream().map(
				s -> dbn.cpds.containsKey(s) ? dbn.cpds.get(s) : DBN.getSameTransitionDD(Global.varNames.get(s - 1)))
				.collect(Collectors.toList());

		return Ta;
	}

	protected List<DD> getObsFunction(DBN dbn) {

		var Oa = i_Om.stream()
				.map(o -> dbn.cpds.containsKey(o) ? dbn.cpds.get(o) : DDnode.getUniformDist(o + (Global.NUM_VARS / 2)))
				.collect(Collectors.toList());

		return Oa;
	}

	private List<String> sortByVarOrdering(List<String> varList, List<String> ordering) {

		var unknownVar = varList.stream().filter(v -> ordering.indexOf(v) < 0).findFirst();
		if (unknownVar.isPresent()) {

			LOGGER.error(String.format("Symbol %s is not defined in %s", unknownVar.get(), ordering));
			return null;
		}

		Collections.sort(varList, (a, b) -> ordering.indexOf(a) - ordering.indexOf(b));
		return varList;
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
	// POSeqDecMakingModel implementations

	@Override
	public List<Integer> i_S() {

		return this.i_S;
	}

	@Override
	public List<Integer> i_S_p() {

		return this.i_S_p;
	}

	@Override
	public List<Integer> i_Om_p() {

		return this.i_Om_p;
	}

	@Override
	public List<String> S() {

		return this.S;
	}

	@Override
	public DD b_i() {

		return this.b_i;
	}

	@Override
	public List<Integer> i_Om() {

		return this.i_Om;
	}

	@Override
	public int i_A() {

		return this.i_A;
	}

	@Override
	public List<String> Om() {

		return this.O;
	}

	@Override
	public List<String> A() {

		return this.A;
	}

	@Override
	public List<List<DD>> O() {

		return this.OF;
	}

	@Override
	public List<List<DD>> T() {

		return this.TF;
	}

	@Override
	public List<DD> R() {

		return this.R;
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
