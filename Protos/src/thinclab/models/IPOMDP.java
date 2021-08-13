/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.solver.PBVISolvable;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class IPOMDP implements PBVISolvable, POSeqDecMakingModel<DD> {

	public final List<String> S;
	public final List<String> O;
	public final List<String> A;

	public final List<Integer> i_S;
	public final List<Integer> i_Om;
	public final int i_A;

	public final List<List<DD>> TF;
	public final List<List<DD>> OF;

	public final List<DD> R;
	public final float discount;

	public DD b_i;

	public final List<Integer> i_S_p;
	public final List<Integer> i_Om_p;

	public final List<List<Integer>> oAll;

	private static final Logger LOGGER = LogManager.getLogger(IPOMDP.class);

	public IPOMDP(List<String> S, List<String> O, String A, HashMap<String, Model> dynamics, HashMap<String, DD> R,
			DD b_i, float discount) {

		// vars
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

		this.b_i = b_i;
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

	@Override
	public List<Integer> i_S() {

		return i_S;
	}

	@Override
	public List<String> S() {

		// TODO Auto-generated method stub
		return S;
	}

	@Override
	public List<Integer> i_Om() {

		// TODO Auto-generated method stub
		return i_Om;
	}

	@Override
	public List<Integer> i_Om_p() {

		// TODO Auto-generated method stub
		return i_Om_p;
	}

	@Override
	public List<Integer> i_S_p() {

		// TODO Auto-generated method stub
		return i_S_p;
	}

	@Override
	public int i_A() {

		// TODO Auto-generated method stub
		return i_A;
	}

	@Override
	public List<String> Om() {

		// TODO Auto-generated method stub
		return O;
	}

	@Override
	public List<String> A() {

		// TODO Auto-generated method stub
		return A;
	}

	@Override
	public List<List<DD>> O() {

		// TODO Auto-generated method stub
		return OF;
	}

	@Override
	public List<List<DD>> T() {

		// TODO Auto-generated method stub
		return TF;
	}

	@Override
	public List<DD> R() {

		// TODO Auto-generated method stub
		return R;
	}

	@Override
	public DD b_i() {

		// TODO Auto-generated method stub
		return b_i;
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

	@Override
	public Tuple<Float, DD> Gaoi(DD b, int a, List<DD> alphaPrimes) {

		// TODO Auto-generated method stub
		return null;
	}

}
