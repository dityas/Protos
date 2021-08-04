/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
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
		this.oAll = OP.cartesianProd(i_Om.stream().map(
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

		/*
		// Initialize TF and OF
		this.TF = new DD[this.A.size()][];
		this.OF = new DD[this.A.size()][];

		for (int i = 0; i < this.A.size(); i++) {

			var tfa = this.getTransitionFunction(dyn.get(this.A.get(i)));
			var ofa = this.getObsFunction(dyn.get(this.A.get(i)));

			this.TF[i] = tfa;
			this.OF[i] = ofa;
		}
		*/
		
		this.TF = this.A.stream().map(a -> this.getTransitionFunction(dyn.get(a))).collect(Collectors.toList());
		this.OF = this.A.stream().map(a -> this.getObsFunction(dyn.get(a))).collect(Collectors.toList());
		
		/*
		// Initialized Rewards
		this.R = IntStream.range(0, this.A.size()).boxed().map(i ->
			{

				if (R.containsKey(this.A.get(i)))
					return R.get(this.A.get(i));

				else
					return DD.zero;

			}).toArray(DD[]::new);
		*/
		
		this.R = this.A.stream().map(a -> R.containsKey(a) ? R.get(a) : DD.zero).collect(Collectors.toList());
		
		this.b_i = initialBelief;
		this.discount = discount;
	}

	protected List<DD> getTransitionFunction(DBN dbn) {

		var Ta = i_S.stream().map(
				s -> dbn.cpds.containsKey(s) ? dbn.cpds.get(s) : DBN.getSameTransitionDD(Global.varNames.get(s - 1)))
				.collect(Collectors.toList());
		/*
		 * DD[] Ta = new DD[this.i_S.length];
		 * 
		 * for (int i = 0; i < Ta.length; i++) {
		 * 
		 * if (dbn.cpds.containsKey(i_S[i])) Ta[i] = dbn.cpds.get(this.i_S[i]);
		 * 
		 * else Ta[i] = DBN.getSameTransitionDD(Global.varNames.get(this.i_S[i] - 1)); }
		 */
		return Ta;
	}

	protected List<DD> getObsFunction(DBN dbn) {

		var Oa = i_S.stream()
				.map(o -> dbn.cpds.containsKey(o) ? dbn.cpds.get(o) : DDnode.getUniformDist(o + (Global.NUM_VARS / 2)))
				.collect(Collectors.toList());
		
		/*
		DD[] Oa = new DD[this.i_Om.length];

		for (int i = 0; i < Oa.length; i++) {

			if (dbn.cpds.containsKey(i_Om[i]))
				Oa[i] = dbn.cpds.get(this.i_Om[i]);

			else
				Oa[i] = DDnode.getUniformDist(this.i_Om[i] + (Global.varNames.size() / 2));
		}
		*/
		
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
	public DD beliefUpdate(DD b, int a, int[][] o) {

		DD[] OFao = OP.restrictN(this.OF[a], o);

		// concat b, TF and OF
		DD[] dynamicsArray = new DD[1 + this.i_S.length + this.i_Om.length];
		dynamicsArray[0] = b;
		System.arraycopy(this.TF[a], 0, dynamicsArray, 1, this.i_S.length);
		System.arraycopy(OFao, 0, dynamicsArray, 1 + this.i_S.length, this.i_Om.length);

		// Sumout[S] P(O'=o| S, A=a) x P(S'| S, A=a) x P(S)
		DD nextBelState = OP.addMultVarElim(dynamicsArray, this.i_S);

		nextBelState = OP.primeVars(nextBelState, -(Global.NUM_VARS / 2));
		DD obsProb = OP.addMultVarElim(nextBelState, this.i_S);

		if (obsProb.getVal() < 1e-8)
			return DDleaf.getDD(Float.NaN);

		nextBelState = OP.div(nextBelState, obsProb);

		return nextBelState;
	}

	@Override
	public DD beliefUpdate(DD b, String a, List<String> o) {

		int actIndex = Collections.binarySearch(this.A, a);
		int[][] obs = new int[2][this.i_Om.length];

		IntStream.range(0, o.size()).forEach(i ->
			{

				obs[0][i] = this.i_Om[i] + (Global.NUM_VARS / 2);
				obs[1][i] = Collections.binarySearch(Global.valNames.get(this.i_Om[i] - 1), o.get(i)) + 1;
			});

		return this.beliefUpdate(b, actIndex, obs);
	}

	// ----------------------------------------------------------------------------------------
	// POSeqDecMakingModel implementations

	@Override
	public int[] i_S() {

		return this.i_S;
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
	public int[] i_Om() {

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
	public DD[][] O() {

		return this.OF;
	}

	@Override
	public DD[][] T() {

		return this.TF;
	}

	@Override
	public DD[] R() {

		return this.R;
	}

	// ----------------------------------------------------------------------------------------
	// PBVISolvable implementations

	@Override
	public Tuple<Float, DD> Gaoi(DD b, int a, List<DD> alphaPrimes) {

		List<Tuple<Float, DD>> Gaoi = new ArrayList<>(oAll.size());

		for (int _o = 0; _o < oAll.size(); _o++) {

			List<Tuple<Float, DD>> _Gaoi = new ArrayList<>();

			var _factors = ArrayUtils.addAll(TF[a], OP.restrict(OF[a], i_Om_p, oAll.get(_o)));

			for (int i = 0; i < alphaPrimes.size(); i++) {

				var factors = ArrayUtils.add(_factors, alphaPrimes.get(i));

				DD gaoi = OP.mult(DDleaf.getDD(discount), OP.addMultVarElim(factors, i_S_p));
				_Gaoi.add(new Tuple<>(OP.dotProduct(b, gaoi, i_S), gaoi));

			}

			Gaoi.add(_Gaoi.stream().reduce(new Tuple<>(Float.NEGATIVE_INFINITY, DDleaf.zero),
					(a1, a2) -> a1._0() > a2._0() ? a1 : a2));

		}

		return Gaoi.stream().reduce(new Tuple<>(0f, DDleaf.zero),
				(t1, t2) -> new Tuple<>(t1._0() + t2._0(), OP.add(t1._1(), t2._1())));
	}

}
