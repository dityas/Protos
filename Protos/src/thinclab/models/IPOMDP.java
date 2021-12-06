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
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.models.datastructures.ReachabilityNode;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;

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
	public DD PThetajGivenMj;
	public List<DD> Taus;

	public List<Tuple3<Integer, PBVISolvablePOMDPBasedModel, List<ReachabilityNode>>> framesj;
	public List<PBVISolvableFrameSolution> framesjSoln;

	private static final Logger LOGGER = LogManager.getLogger(IPOMDP.class);

	public IPOMDP(List<String> S, List<String> O, String A, String Aj, String Mj, String Thetaj,
			List<Tuple<String, Model>> frames_j, HashMap<String, Model> dynamics, HashMap<String, DD> R, float discount,
			int H) {

		// initialize dynamics like POMDP
		super(S, O, A, dynamics, R, discount);

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
				.map(t -> Tuple.of(t._0(), t._1(),
						Global.modelVars.get(Global.varNames.get(i_Mj - 1)).keySet().stream()
								.filter(k -> k._0() == t._0()).map(k -> k._1()).collect(Collectors.toList())))
				.collect(Collectors.toList());

		Collections.sort(this.framesj, (f1, f2) -> f1._0() - f2._0());

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
						.map(f -> this.prepareOj_pGivenAjAiS_p(i + 1, f._1().O())).collect(Collectors.toList()))
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
		this.framesjSoln = this.framesj.stream().map(f -> new PBVISolvableFrameSolution(f._2(), f._0(), f._1(), H))
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

		createPAjMj();
		createPThetajMj();
		createPMj_pMjAjOj_p();

		R = R.stream().map(ra -> DDOP.addMultVarElim(List.of(PAjGivenMj, ra), List.of(i_Aj)))
				.collect(Collectors.toList());
	}

	public void createIS() {

		var mjMap = Global.modelVars.get(Global.varNames.get(i_Mj - 1));

		this.framesjSoln.stream().forEach(f ->
			{

				f.solve();
			});

		var mjsList = this.framesjSoln.stream().flatMap(f -> f.mjList().stream()).collect(Collectors.toList());

		mjsList.stream().forEach(f ->
			{

				var key = Tuple.of(f._0(), f._1());

				if (!mjMap.containsKey(key)) {

					mjMap.put(key, "m" + mjMap.size());
				}

				else {

					LOGGER.warn(String.format("Model %s already exists in mjMap. Will skip it", key));
				}

				// If this node was initialized in the domain file, we'll need to populate the
				// optimal action and the optimal alpha vector
				/*
				if (f._1().i_a == -1 && f._1().beliefs.size() == 1) {

					String mName = mjMap.remove(key);

					var _b = f._1().beliefs.stream().findFirst().get();
					int i_a = this.framesjSoln.get(f._0()).Vn.getBestActionIndex(_b, framesj.get(f._0())._1().i_S());
					int alphaId = DDOP.bestAlphaIndex(framesjSoln.get(f._0()).Vn.aVecs, _b,
							framesj.get(f._0())._1().i_S());

					f._1().i_a = i_a;
					f._1().alphaId = alphaId;

					mjMap.put(Tuple.of(f._0(), f._1()), mName);
				}
				*/
			});

		var sortedVals = mjMap.values().stream().collect(Collectors.toList());
		Collections.sort(sortedVals);

		Global.replaceValues(i_Mj, sortedVals);
		Global.replaceValues(i_Mj_p, sortedVals);

		LOGGER.debug(String.format("Interactive state space has %s models", Global.valNames.get(i_Mj - 1).size()));
		LOGGER.debug(String.format("IPOMDP has %s interactive states in total",
				i_S().stream().map(i -> Global.valNames.get(i - 1).size()).reduce(1, (p, q) -> p * q) * Global.valNames.get(i_Mj - 1).size()));
	}

	public void createPAjMj() {

		var mjMap = Global.modelVars.get(Global.varNames.get(i_Mj - 1));
		var MjToOPTAj = framesjSoln.stream().flatMap(f -> f.mjToOPTAjMap().entrySet().stream())
				.map(e -> Tuple.of(mjMap.get(e.getKey()), DDnode.getDDForChild(i_Aj, e.getValue())))
				.collect(Collectors.toList());

		Collections.sort(MjToOPTAj, (a, b) -> a._0().compareTo(b._0()));

		var AjDDs = MjToOPTAj.stream().map(m -> m._1()).toArray(DD[]::new);
		PAjGivenMj = DDOP.reorder(DDnode.getDD(i_Mj, AjDDs));
		
	}

	public void createPThetajMj() {

		var mjMap = Global.modelVars.get(Global.varNames.get(i_Mj - 1));
		var ThetajToMj = mjMap.entrySet().stream()
				.map(e -> Tuple.of(DDnode.getDDForChild(i_Thetaj, e.getKey()._0()), e.getValue()))
				.collect(Collectors.toList());

		Collections.sort(ThetajToMj, (a, b) -> a._1().compareTo(b._1()));

		var ThetajDDs = ThetajToMj.stream().map(m -> m._0()).toArray(DD[]::new);
		PThetajGivenMj = DDOP.reorder(DDnode.getDD(i_Mj, ThetajDDs));
	}

	public void createPMj_pMjAjOj_p() {

		var then = System.nanoTime();
		
		var mjMap = Global.modelVars.get(Global.varNames.get(i_Mj - 1));

		var triples = framesjSoln.stream().flatMap(f -> f.getTriples().stream())
				.map(f -> Tuple.of(mjMap.get(f._0()), f._1(), mjMap.get(f._2()))).map(t ->
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

			LOGGER.debug(String.format("Models are %s", Global.modelVars));
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
		
		var now = System.nanoTime();
		LOGGER.debug(String.format("Building P(Mj'|Mj,Aj,Oj') took %s msecs", ((now - then) / 1000000.0)));
	}

	@Override
	public DD beliefUpdate(DD b, int a, List<Integer> o) {

		var Ofao = DDOP.restrict(O().get(a), i_Om_p, o);

		var factors = new ArrayList<DD>(S().size() + S().size() + Omj.size() + 3);

		factors.add(b);
		factors.add(PAjGivenMj);
		factors.add(PThetajGivenMj);
		factors.add(Taus.get(a));
		// factors.add(PMj_pGivenMjAjOj_p);
		factors.addAll(T().get(a));
		// factors.addAll(Oj.get(a));
		factors.addAll(Ofao);

		var vars = new ArrayList<Integer>(factors.size());
		vars.addAll(i_S());
		vars.add(i_Thetaj);
		vars.add(i_Aj);
		// vars.addAll(i_Omj_p);

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
		factors.add(PThetajGivenMj);
		// factors.add(PMj_pGivenMjAjOj_p);
		factors.add(Taus.get(a));
		factors.addAll(T().get(a));
		factors.addAll(O().get(a));
		// factors.addAll(Oj.get(a));

		var vars = new ArrayList<Integer>(factors.size());
		vars.addAll(i_S());
		// vars.addAll(i_Omj_p);
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
		factors.add(PThetajGivenMj);
		factors.add(Taus.get(a));
		factors.add(DDOP.primeVars(d, Global.NUM_VARS / 2));

		var results = DDOP.addMultVarElim(factors, gaoivars);

		return results;
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
