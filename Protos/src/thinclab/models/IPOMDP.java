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
import thinclab.models.datastructures.PBVISolvableFrameSolution;
import thinclab.utils.Tuple;
import thinclab.utils.TwoWayMap;

/*
 * @author adityas
 *
 */
public class IPOMDP extends PBVISolvablePOMDPBasedModel {

	public final int H;

	public final List<String> Aj;
	public final int i_Aj;
	public final int i_Mj;
	public final int i_Mj_p;
	public final int i_Thetaj;
	public final List<String> Thetaj;

	public final List<String> Omj;

	public final List<Integer> i_Omj;
	public final List<Integer> i_Omj_p;

	public final List<List<DD>> Oj;

	public DD PAjGivenMj;
	public DD PMj_pGivenMjAjOj_p;

	public List<Tuple<Integer, PBVISolvablePOMDPBasedModel>> framesj;
	public List<PBVISolvableFrameSolution> framesjSoln;

	// public HashMap<Tuple<Integer, DD>, Integer> mjMap = new HashMap<>(1000);
	public TwoWayMap<Tuple<Integer, DD>, String> mjMap = new TwoWayMap<>();

	private static final Logger LOGGER = LogManager.getLogger(IPOMDP.class);

	public IPOMDP(List<String> S, List<String> O, String A, String Aj, String Mj, String Thetaj,
			List<Tuple<String, Model>> frames_j, HashMap<String, Model> dynamics, HashMap<String, DD> R,
			DD initialBelief, float discount, int H) {

		// initialize dynamics like POMDP
		super(S, O, A, dynamics, R, initialBelief, discount);

		// random variable for opponent's action
		this.i_Aj = Global.varNames.indexOf(Aj) + 1;
		this.Aj = Global.valNames.get(i_Aj - 1);

		// opponent's model variable
		this.i_Mj = Global.varNames.indexOf(Mj) + 1;
		this.i_Mj_p = this.i_Mj + (Global.NUM_VARS / 2);

		// random variable for frame of the opponent
		this.i_Thetaj = Global.varNames.indexOf(Thetaj) + 1;
		this.Thetaj = Global.valNames.get(i_Thetaj - 1);

		this.H = H;

		// initialize frames
		this.framesj = frames_j.stream().map(
				t -> Tuple.of(Global.valNames.get(i_Thetaj - 1).indexOf(t._0()), (PBVISolvablePOMDPBasedModel) t._1()))
				.collect(Collectors.toList());

		// verify and prepare Oj
		var incorrectFrame = this.framesj.stream().filter(f -> !f._1().Om().equals(this.framesj.get(0)._1().Om()))
				.findAny();
		if (incorrectFrame.isPresent()) {

			LOGGER.error(String.format(
					"All frame should have the same set of observation variables. %s seems to be different.",
					incorrectFrame.get()));
			System.exit(-1);
		}

		// prepare opponent's observations and obs functions
		Omj = this.framesj.get(0)._1().Om();
		i_Omj = this.framesj.get(0)._1().i_Om();
		i_Omj_p = this.framesj.get(0)._1().i_Om_p();

		var OjRestrictedAi = IntStream
				.range(0, A().size()).mapToObj(i -> this.framesj.stream()
						.map(f -> this.prepareOj_pGivenAjAiS_p(i, f._1().O())).collect(Collectors.toList()))
				.collect(Collectors.toList());

		Oj = IntStream
				.range(0,
						A().size())
				.mapToObj(i -> IntStream.range(0, Omj.size())
						.mapToObj(o -> DDnode.getDD(i_Thetaj,
								IntStream.range(0, this.framesj.size())
										.mapToObj(f -> OjRestrictedAi.get(i).get(f).get(o)).toArray(DD[]::new)))
						.collect(Collectors.toList()))
				.collect(Collectors.toList());

		// prepare structures for solving frames
		this.framesjSoln = this.framesj.stream().map(f -> new PBVISolvableFrameSolution(f._0(), f._1(), H))
				.collect(Collectors.toList());

		// create interactive state space using mjs
		updateIS();
	}

	public List<DD> prepareOj_pGivenAjAiS_p(int ai, List<List<DD>> _Oj) {

		var Oj_ = _Oj.stream().map(oja -> oja.stream().map(oj -> DDOP.restrict(oj, List.of(i_A), List.of(ai)))
				.collect(Collectors.toList())).collect(Collectors.toList());

		var Oj_pGivenAjAiS_p = IntStream.range(0, Omj.size())
				.mapToObj(i -> DDOP.reorder(DDnode.getDD(i_Aj,
						IntStream.range(0, Aj.size()).mapToObj(j -> Oj_.get(j).get(i)).toArray(DD[]::new))))
				.collect(Collectors.toList());

		return Oj_pGivenAjAiS_p;
	}

	public void updateIS() {

		createIS();

		// weird way to create initial belief the first time, but ok...
		if (PAjGivenMj == null)
			createFirstb_i();

		createPAjMj();
		createPMj_pMjAjOj_p();
	}

	public void createFirstb_i() {

		var b_js = framesjSoln.stream().flatMap(f -> f.bMjList().stream().map(b -> mjMap.k2v.get(b)))
				.collect(Collectors.toList());
		var b_MjDD = b_js.stream()
				.map(b -> DDOP.mult(DDleaf.getDD(1.0f / b_js.size()),
						DDnode.getDDForChild(i_Mj,
								Collections.binarySearch(Global.valNames.get(i_Mj - 1), b.toString()))))
				.reduce(DD.zero, (d1, d2) -> DDOP.add(d1, d2));

		b_i = DDOP.reorder(DDOP.mult(b_i, b_MjDD));
	}

	public void createIS() {

		this.framesjSoln.stream().parallel().forEach(f ->
			{

				f.solve();
			});

		var mjsList = this.framesjSoln.stream().flatMap(f -> f.mjList().stream()).collect(Collectors.toList());

		mjsList.stream().forEach(f ->
			{

				this.mjMap.put(Tuple.of(f._0(), f._1()), "m" + this.mjMap.k2v.size());
			});

		var sortedVals = mjMap.k2v.values().stream().collect(Collectors.toList());
		Collections.sort(sortedVals, (a, b) -> Integer.valueOf(a.split("m")[1]) - Integer.valueOf(b.split("m")[1]));

		Global.valNames.set(i_Mj - 1, sortedVals);
		Global.valNames.set(i_Mj_p - 1, sortedVals);
		Global.varDomSize.set(i_Mj - 1, sortedVals.size());
		Global.varDomSize.set(i_Mj_p - 1, sortedVals.size());

		LOGGER.debug(String.format("Interactive state space has %s models", Global.valNames.get(i_Mj - 1).size()));
	}

	public void createPAjMj() {

		var MjToOPTAj = framesjSoln.stream().flatMap(f -> f.mjToOPTAjMap().entrySet().stream())
				.map(e -> Tuple.of(mjMap.k2v.get(e.getKey()), DDnode.getDDForChild(i_Aj, e.getValue())))
				.collect(Collectors.toList());

		// MjToOPTAj.forEach(d -> LOGGER.debug(String.format("P(Aj|Mj) is %s, %s",
		// mjMap.v2k.get(d._0()), d)));

		Collections.sort(MjToOPTAj,
				(a, b) -> Integer.valueOf(a._0().split("m")[1]) - Integer.valueOf(b._0().split("m")[1]));

		var AjDDs = MjToOPTAj.stream().map(m -> m._1()).toArray(DD[]::new);
		PAjGivenMj = DDOP.reorder(DDnode.getDD(i_Mj, AjDDs));
	}

	public void createPMj_pMjAjOj_p() {

		var triples = framesjSoln.stream().flatMap(f -> f.getTriples().stream())
				.map(f -> Tuple.of(mjMap.k2v.get(f._0()), f._1(), mjMap.k2v.get(f._2()))).map(t ->
					{

						// convert everything into list
						List<Integer> valList = new ArrayList<>(t._1().size() + 2);
						valList.add(Global.valNames.get(i_Mj - 1).indexOf(t._0()) + 1);
						valList.addAll(t._1());
						valList.add(Global.valNames.get(i_Mj_p - 1).indexOf(t._2()) + 1);

						return valList;
					})
				.collect(Collectors.toList());

		var varList = new ArrayList<Integer>(Omj.size() + 2);
		varList.add(i_Mj);
		varList.add(i_Aj);
		varList.addAll(i_Omj_p);
		varList.add(i_Mj_p);

		PMj_pGivenMjAjOj_p = triples.stream().map(t -> DDOP.reorder(DDnode.getDD(varList, t, 1.0f)))
				.reduce(DDleaf.getDD(0.0f), (d1, d2) -> DDOP.add(d1, d2));

	}

	public void printGraph(List<List<Integer>> triples) {

		var builder = new StringBuilder();
		builder.append("digraph {\r\n");
		triples.forEach(t ->
			{

				builder.append("\"" + mjMap.v2k.get(t.get(0)) + "\"").append(" -> ")
						.append("\"" + mjMap.v2k.get(t.get(t.size() - 1)) + "\"").append("\r\n");
			});

		builder.append("}");

		LOGGER.debug(builder.toString());
	}

	@Override
	public DD beliefUpdate(DD b, int a, List<Integer> o) {

		var Ofao = DDOP.restrict(O().get(a), i_Om_p, o);

		var factors = new ArrayList<DD>(S().size() + S().size() + Omj.size() + 3);

		factors.add(b);
		factors.add(PAjGivenMj);
		factors.add(PMj_pGivenMjAjOj_p);
		factors.addAll(T().get(a));
		factors.addAll(Oj.get(a));
		factors.addAll(Ofao);

		var vars = new ArrayList<Integer>(factors.size());
		vars.addAll(i_S());
		vars.addAll(i_Omj_p);
		vars.add(i_Aj);
		vars.add(i_Mj);
		vars.add(i_Thetaj);

		Collections.sort(vars);

		var b_p = DDOP.primeVars(DDOP.addMultVarElim(factors, vars), -(Global.NUM_VARS / 2));
		var stateVars = new ArrayList<Integer>(S().size() + 2);
		stateVars.addAll(i_S());
		stateVars.add(i_Mj);
		stateVars.add(i_Thetaj);

		var prob = DDOP.addMultVarElim(List.of(b_p), stateVars);
		b_p = DDOP.div(b_p, prob);

		return b_p;
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

		var factors = new ArrayList<DD>(S().size() + S().size() + Omj.size() + 3);

		factors.add(b);
		factors.add(PAjGivenMj);
		factors.add(PMj_pGivenMjAjOj_p);
		factors.addAll(T().get(a));
		factors.addAll(Oj.get(a));

		var vars = new ArrayList<Integer>(factors.size());
		vars.addAll(i_S());
		vars.addAll(i_Omj_p);
		vars.add(i_Aj);
		vars.add(i_Mj);
		vars.add(i_Thetaj);

		Collections.sort(vars);

		var b_p = DDOP.primeVars(DDOP.addMultVarElim(factors, vars), -(Global.NUM_VARS / 2));
		var stateVars = new ArrayList<Integer>(S().size() + 2);
		stateVars.addAll(i_S());
		stateVars.add(i_Mj);
		stateVars.add(i_Thetaj);

		var prob = DDOP.addMultVarElim(List.of(b_p), stateVars);
		b_p = DDOP.div(b_p, prob);

		return b_p;

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tuple<Float, DD> Gaoi(DD b, int a, List<DD> alphaPrimes) {

		// TODO Auto-generated method stub
		return null;
	}

}
