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
import thinclab.legacy.TypedCacheMap;
import thinclab.models.datastructures.PBVISolvableFrameSolution;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.utils.Diagnostics;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;
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

	public final List<Integer> allvars;
	public final List<Integer> gaoivars;

	public final List<List<DD>> Oj;

	public DD PAjGivenMj;
	public DD PMj_pGivenMjAjOj_p;
	public List<DD> Taus;

	public List<Tuple<Integer, PBVISolvablePOMDPBasedModel>> framesj;
	public List<PBVISolvableFrameSolution> framesjSoln;

	// public HashMap<Tuple<Integer, DD>, Integer> mjMap = new HashMap<>(1000);
	public TwoWayMap<Tuple<Integer, DD>, String> mjMap = new TwoWayMap<>();
	private TypedCacheMap<Tuple3<DD, Integer, Integer>, Float> obsProbCache = new TypedCacheMap<>(1000);

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

		this.i_S.add(i_Mj);
		this.i_S_p.add(i_Mj_p);

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

		allvars = new ArrayList<Integer>();
		allvars.addAll(i_S());
		allvars.add(i_Aj);
		allvars.add(i_Thetaj);
		allvars.addAll(i_Omj_p);

		Collections.sort(allvars);

		gaoivars = new ArrayList<Integer>();
		gaoivars.addAll(i_S_p());
		gaoivars.add(i_Aj);
		gaoivars.add(i_Thetaj);

		Collections.sort(gaoivars);

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

		R = R.stream().map(ra -> DDOP.addMultVarElim(List.of(PAjGivenMj, ra), List.of(i_Aj)))
				.collect(Collectors.toList());
	}

	public void createFirstb_i() {

		var b_js = framesjSoln.stream().flatMap(f -> f.bMjList().stream().map(b -> mjMap.k2v.get(b)))
				.collect(Collectors.toList());
		var b_MjDD = b_js.stream()
				.map(b -> DDOP.mult(DDleaf.getDD(1.0f / b_js.size()),
						DDnode.getDDForChild(i_Mj, Integer.valueOf(b.split("m")[1]))))
				.reduce(DD.zero, (d1, d2) -> DDOP.add(d1, d2));

		// LOGGER.debug(String.format("Mj is %s", Global.valNames.get(i_Mj - 1)));
		// b_js.stream().forEach(b -> {
		// var child = Collections.binarySearch(Global.valNames.get(i_Mj - 1), b);
		// LOGGER.debug(String.format("Child for %s is %s", b, child));
		// });

		b_i = DDOP.reorder(DDOP.mult(b_i, b_MjDD));

		var b_iSanity = DDOP.addMultVarElim(List.of(b_i), i_S());
		if (DDOP.maxAll(DDOP.abs(DDOP.sub(b_iSanity, DD.one))) > 1e-5) {

			LOGGER.error(String.format("b_i: %s, # (S) b_i is %s != 1.0", DDOP.factors(b_i, i_S()),
					DDOP.addMultVarElim(List.of(b_i), i_S())));
			LOGGER.error("IPOMDP failed sanity check");
			System.exit(-1);
		}
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
		Collections.sort(sortedVals);

		Global.replaceValues(i_Mj, sortedVals);
		Global.replaceValues(i_Mj_p, sortedVals);

		LOGGER.debug(String.format("Interactive state space has %s models", Global.valNames.get(i_Mj - 1).size()));
	}

	public void createPAjMj() {

		var MjToOPTAj = framesjSoln.stream().flatMap(f -> f.mjToOPTAjMap().entrySet().stream())
				.map(e -> Tuple.of(mjMap.k2v.get(e.getKey()), DDnode.getDDForChild(i_Aj, e.getValue())))
				.collect(Collectors.toList());

		Collections.sort(MjToOPTAj, (a, b) -> a._0().compareTo(b._0()));

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

		var ddSum = DDOP.addMultVarElim(List.of(PMj_pGivenMjAjOj_p), List.of(i_Mj_p));
		if (DDOP.maxAll(DDOP.abs(DDOP.sub(ddSum, DD.one))) > 1e-5f) {

			LOGGER.error(String.format("# (Mj') P(Mj'|Mj,Aj,Oj') is %s != 1", ddSum));
			System.exit(-1);
		}

		Taus = IntStream.range(0, A().size()).mapToObj(i ->
			{

				var factors = new ArrayList<DD>(Omj.size() + 1);
				factors.addAll(Oj.get(i));
				factors.add(PMj_pGivenMjAjOj_p);

				return DDOP.addMultVarElim(factors, i_Omj_p);
			}).collect(Collectors.toList());
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
		// vars.add(i_Thetaj);
		vars.add(i_Aj);
		vars.addAll(i_Omj_p);

		// Collections.sort(vars);

		var b_p = DDOP.primeVars(DDOP.addMultVarElim(factors, vars), -(Global.NUM_VARS / 2));
		var stateVars = new ArrayList<Integer>(S().size() + 2);
		stateVars.addAll(i_S());
		// stateVars.add(i_Thetaj);

		var prob = DDOP.addMultVarElim(List.of(b_p), stateVars);
		b_p = DDOP.div(b_p, prob);
		// LOGGER.debug(String.format("obs prob for %s from belief %s to belief %s with
		// state vars %s is %s", o,
		// DDOP.factors(b, i_S()), DDOP.factors(b_p, i_S()), stateVars, prob));

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
		factors.addAll(O().get(a));
		factors.addAll(Oj.get(a));

		var vars = new ArrayList<Integer>(factors.size());
		vars.addAll(i_S());
		vars.addAll(i_Omj_p);
		vars.add(i_Aj);
		vars.add(i_Thetaj);
		vars.addAll(i_S_p());

		return DDOP.addMultVarElim(factors, vars);
	}

	// ----------------------------------------------------------------------------------------
	// PBVISolvable implementations

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

		return Tuple.of(bestVal, bestAlpha);
	}

	public DD project(DD d, int a, List<Integer> o) {

		var factors = new ArrayList<DD>(S.size() + Om().size() + 1);
		factors.addAll(DDOP.restrict(O().get(a), i_Om_p, o));
		factors.addAll(T().get(a));
		factors.add(PAjGivenMj);
		factors.add(Taus.get(a));
		factors.add(DDOP.primeVars(d, Global.NUM_VARS / 2));

		var results = DDOP.addMultVarElim(factors, gaoivars);

		return results;
	}

	public Tuple<Integer, DD> backup(DD b, List<DD> alphas, ReachabilityGraph g) {

		// var Gao = IntStream.range(0, A().size()).mapToObj(a -> Gaoi(a, b, alphas,
		// g)).collect(Collectors.toList());

		int bestA = -1;
		float bestVal = Float.NEGATIVE_INFINITY;

		var Gao = new ArrayList<ArrayList<DD>>(A().size());
		for (int a = 0; a < A().size(); a++) {

			float val = 0.0f;

			var argmax_iGaoi = new ArrayList<DD>(oAll.size());
			for (int o = 0; o < oAll.size(); o++) {

				var key = Tuple.of(b, a, o);
				var prob = obsProbCache.get(key);

				var b_p = g.getNodeAtEdge(b, Tuple.of(a, oAll.get(o)));
				if (b_p == null) {

					b_p = beliefUpdate(b, a, oAll.get(o));
					g.addEdge(b, Tuple.of(a, oAll.get(o)), b_p);
				}

				if (prob == null) {

					var likelihoods = obsLikelihoods(b, a);
					prob = DDOP.restrict(likelihoods, i_Om_p, oAll.get(o)).getVal();
					obsProbCache.put(key, prob);
				}

				var bestAlpha = Gaoi(b_p, a, oAll.get(o), alphas);
				argmax_iGaoi.add(alphas.get(bestAlpha._1()));

				val += (prob * bestAlpha._0());
			}

			val *= discount;
			val += DDOP.dotProduct(b, R().get(a), i_S());

			if (val >= bestVal) {

				bestVal = val;
				bestA = a;
			}

			Gao.add(argmax_iGaoi);
		}

		var vec = DD.zero;
		for (int o = 0; o < Gao.get(bestA).size(); o++)
			vec = DDOP.add(project(Gao.get(bestA).get(o), bestA, oAll.get(o)), vec);

		return Tuple.of(bestA, DDOP.add(R().get(bestA), DDOP.mult(DDleaf.getDD(discount), vec)));
	}

}
