/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
public class POMDP implements POSeqDecMakingModel<DD>, BeliefBasedModel<DD>, PBVISolvable {

	public final List<String> S;
	public final List<String> O;
	public final List<String> A;
	public final DD b;
	public final float discount;

	public final int[] Svars;
	public final int[] Ovars;
	public final int Avar;

	public final DD[][] TF;
	public final DD[][] OF;
	public final DD[] R;

	private static final Logger LOGGER = LogManager.getLogger(POMDP.class);

	public POMDP(List<String> S, List<String> O, String A, HashMap<String, Model> dynamics, HashMap<String, DD> R,
			DD initialBelief, float discount) {

		this.S = this.sortByVarOrdering(S, Global.varNames);
		this.O = this.sortByVarOrdering(O, Global.varNames);
		this.A = Global.valNames.get(Global.varNames.indexOf(A));

		this.Svars = this.S.stream().mapToInt(s -> Global.varNames.indexOf(s) + 1).toArray();
		this.Ovars = this.O.stream().mapToInt(o -> Global.varNames.indexOf(o) + 1).toArray();
		this.Avar = Global.varNames.indexOf(A) + 1;

		// take out DBNs from set of models
		var dyn = new HashMap<String, DBN>(5);
		dyn.putAll(dynamics.entrySet().stream().filter(e -> e.getValue() instanceof DBN)
				.collect(Collectors.toMap(e -> e.getKey(), e -> (DBN) e.getValue())));

		// Populate dynamics for missing actions
		Global.valNames.get(this.Avar - 1).stream().forEach(a ->
			{

				if (!dyn.containsKey(a)) {

					LOGGER.warn(String.format("Dynamics not defined for action %s. "
							+ "Will apply with SAME transitions and random observations for that action", a));
					dyn.put(a, new DBN(new HashMap<Integer, DD>(1)));
				}
			});

		// Initialize TF and OF
		this.TF = new DD[this.A.size()][];
		this.OF = new DD[this.A.size()][];

		for (int i = 0; i < this.A.size(); i++) {

			var tfa = this.getTransitionFunction(dyn.get(this.A.get(i)));
			var ofa = this.getObsFunction(dyn.get(this.A.get(i)));

			this.TF[i] = tfa;
			this.OF[i] = ofa;
		}

		// Initialized Rewards
		this.R = IntStream.range(0, this.A.size()).boxed().map(i ->
			{

				if (R.containsKey(this.A.get(i)))
					return R.get(this.A.get(i));

				else
					return DD.zero;

			}).toArray(DD[]::new);

		this.b = initialBelief;
		this.discount = discount;
	}

	protected DD[] getTransitionFunction(DBN dbn) {

		DD[] Ta = new DD[this.Svars.length];

		for (int i = 0; i < Ta.length; i++) {

			if (dbn.cpds.containsKey(Svars[i]))
				Ta[i] = dbn.cpds.get(this.Svars[i]);

			else
				Ta[i] = DBN.getSameTransitionDD(Global.varNames.get(this.Svars[i] - 1));
		}

		return Ta;
	}

	protected DD[] getObsFunction(DBN dbn) {

		DD[] Oa = new DD[this.Ovars.length];

		for (int i = 0; i < Oa.length; i++) {

			if (dbn.cpds.containsKey(Ovars[i]))
				Oa[i] = dbn.cpds.get(this.Ovars[i]);

			else
				Oa[i] = DDnode.getUniformDist(this.Ovars[i] + (Global.varNames.size() / 2));
		}

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
		builder.append("S vars : ").append(Arrays.toString(this.Svars)).append("\r\n");
		builder.append("O : ").append(this.O).append("\r\n");
		builder.append("O vars : ").append(Arrays.toString(this.Ovars)).append("\r\n");
		builder.append("A : ").append(this.A).append("\r\n");
		builder.append("A var : ").append(this.Avar).append("\r\n");

		builder.append("TF funct. : ").append("\r\n");
		IntStream.range(0, this.TF.length).boxed().forEach(i -> builder.append("\t").append(i).append(" - ")
				.append(this.A.get(i)).append(" : ").append(this.TF[i]).append("\r\n"));

		builder.append("OF funct. : ").append(this.OF).append("\r\n");
		IntStream.range(0, this.OF.length).boxed().forEach(i -> builder.append("\t").append(i).append(" - ")
				.append(this.A.get(i)).append(" : ").append(this.OF[i]).append("\r\n"));

		builder.append("R : ").append("\r\n");
		IntStream.range(0, this.R.length).boxed().forEach(i -> builder.append("\t").append(i).append(" - ")
				.append(this.A.get(i)).append(" : ").append(this.R[i]).append("\r\n"));

		builder.append("b : ").append(this.b).append("\r\n");
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
		DD[] dynamicsArray = new DD[1 + this.Svars.length + this.Ovars.length];
		dynamicsArray[0] = b;
		System.arraycopy(this.TF[a], 0, dynamicsArray, 1, this.Svars.length);
		System.arraycopy(OFao, 0, dynamicsArray, 1 + this.Svars.length, this.Ovars.length);

		// Sumout[S] P(O'=o| S, A=a) x P(S'| S, A=a) x P(S)
		DD nextBelState = OP.addMultVarElim(dynamicsArray, this.Svars);

		nextBelState = OP.primeVars(nextBelState, -(Global.NUM_VARS / 2));
		DD obsProb = OP.addMultVarElim(nextBelState, this.Svars);

		if (obsProb.getVal() < 1e-8)
			return DDleaf.getDD(Float.NaN);

		nextBelState = OP.div(nextBelState, obsProb);

		return nextBelState;
	}

	@Override
	public DD beliefUpdate(DD b, String a, List<String> o) {

		int actIndex = Collections.binarySearch(this.A, a);
		int[][] obs = new int[2][this.Ovars.length];

		IntStream.range(0, o.size()).forEach(i ->
			{

				obs[0][i] = this.Ovars[i] + (Global.NUM_VARS / 2);
				obs[1][i] = Collections.binarySearch(Global.valNames.get(this.Ovars[i] - 1), o.get(i)) + 1;
			});

		return this.beliefUpdate(b, actIndex, obs);
	}

	// ----------------------------------------------------------------------------------------
	// POSeqDecMakingModel implementations

	@Override
	public int[] i_S() {

		return this.Svars;
	}

	@Override
	public List<String> S() {

		return this.S;
	}

	@Override
	public DD b_i() {

		return this.b;
	}

	@Override
	public int[] i_Om() {

		return this.Ovars;
	}

	@Override
	public int i_A() {

		return this.Avar;
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

		// TODO Auto-generated method stub
		return null;
	}

}
