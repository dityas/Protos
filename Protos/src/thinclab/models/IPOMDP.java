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
			int H, String name) {
		
		// initialize dynamics like POMDP
		super(S, O, A, dynamics, R, discount);

		this.name = name;
		
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
			});

		var sortedVals = mjMap.values().stream().collect(Collectors.toList());
		Collections.sort(sortedVals);

		Global.replaceValues(i_Mj, sortedVals);
		Global.replaceValues(i_Mj_p, sortedVals);

		LOGGER.debug(String.format("Interactive state space for %s has %s models", getName(),
				Global.valNames.get(i_Mj - 1).size()));
		LOGGER.debug(String.format("IPOMDP %s has %s interactive states in total", getName(),
				i_S().stream().map(i -> Global.valNames.get(i - 1).size()).reduce(1, (p, q) -> p * q)));
	}

	public void createPAjMj() {

		var mjMap = Global.modelVars.get(Global.varNames.get(i_Mj - 1));
		var MjToOPTAj = framesjSoln.stream().flatMap(f -> f.mjToOPTAjMap().entrySet().stream())
				.map(e -> Tuple.of(mjMap.get(e.getKey()), DDnode.getDDForChild(i_Aj, e.getValue())))
				.collect(Collectors.toList());

		Collections.sort(MjToOPTAj, (a, b) -> a._0().compareTo(b._0()));

		var AjDDs = MjToOPTAj.stream().map(m -> m._1()).toArray(DD[]::new);
		
		LOGGER.debug(String.format("AjDDs are %s", AjDDs.length));
		LOGGER.debug(String.format("i_Mj size is %s", Global.valNames.get(i_Mj - 1).size()));
		
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
		LOGGER.debug(
				String.format("Building P(Mj'|Mj,Aj,Oj') for %s took %s msecs", getName(), ((now - then) / 1000000.0)));
	}

	@Override
	public DD beliefUpdate(DD b, int a, List<Integer> o) {

		var Ofao = DDOP.restrict(O().get(a), i_Om_p, o);

		var factors = new ArrayList<DD>(S().size() + S().size() + Omj.size() + 3);

		factors.add(b);
		factors.add(PAjGivenMj);
		factors.add(PThetajGivenMj);
		factors.add(Taus.get(a));
		factors.addAll(T().get(a));
		factors.addAll(Ofao);

		var vars = new ArrayList<Integer>(factors.size());
		vars.addAll(i_S());
		vars.add(i_Thetaj);
		vars.add(i_Aj);

		var b_p = DDOP.primeVars(DDOP.addMultVarElim(factors, vars), -(Global.NUM_VARS / 2));
		var stateVars = new ArrayList<Integer>(S().size() + 2);
		stateVars.addAll(i_S());

		var prob = DDOP.addMultVarElim(List.of(b_p), stateVars);

		if (DDOP.abs(DDOP.sub(prob, DD.zero)).getVal() < 1e-6)
			return DD.zero;

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

		// Do not forget to clear cache after every IPOMDP step
		var key = Tuple.of(b, a);
		if (lCache.containsKey(key))
			return lCache.get(key);

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

		var l = DDOP.addMultVarElim(factors, vars);

		lCache.clearIfFull();
		lCache.put(key, l);

		return l;
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

	public List<Tuple3<Integer, DD, Float>> computeNextBaParallel(DD b, DD likelihoods, int a, ReachabilityGraph g) {

		var res = IntStream.range(0, oAll.size()).parallel().boxed()
				.map(o -> Tuple.of(DDOP.restrict(likelihoods, i_Om_p, oAll.get(o)).getVal(), o))
				.filter(o -> o._0() > 1e-6f).map(o ->
					{

						var b_n = g.getNodeAtEdge(b, Tuple.of(a, oAll.get(o._1())));

						if (b_n == null)
							b_n = beliefUpdate(b, a, oAll.get(o._1()));

						return Tuple.of(o._1(), b_n, o._0());
					})
				.collect(Collectors.toList());

		return res;
	}

	public Tuple3<Integer, DD, Float> getBestAlpha(Tuple3<Integer, DD, Float> nextBa, List<DD> alphas, int a) {

		var obsIndex = nextBa._0();
		var b_n = nextBa._1();
		var prob = nextBa._2();

		var bestAlpha = Gaoi(b_n, a, oAll.get(obsIndex), alphas);

		return Tuple.of(obsIndex, alphas.get(bestAlpha._1()), (prob * bestAlpha._0()));

	}

	public Tuple<Integer, DD> backup(DD b, List<DD> alphas, ReachabilityGraph g) {

		int bestA = -1;
		float bestVal = Float.NEGATIVE_INFINITY;

		var nextBels = belCache.get(b);

		long missThen = System.nanoTime();

		if (nextBels == null) {

			// build cache entry for this belief
			nextBels = new HashMap<Integer, List<Tuple3<Integer, DD, Float>>>(A().size());

			var res = IntStream.range(0, A().size()).parallel().boxed()
					.map(a -> Tuple.of(a, computeNextBaParallel(b, obsLikelihoods(b, a), a, g)))
					.collect(Collectors.toList());

			for (var r : res)
				nextBels.put(r._0(), r._1());

			belCache.put(b, nextBels);

			if (Global.DEBUG) {

				float missT = (System.nanoTime() - missThen) / 1000000.0f;
				LOGGER.debug(String.format("Cache missed computation took %s msecs", missT));
			}

		}

		long hitThen = System.nanoTime();

		// compute everything from the cached entries
		var Gao = new ArrayList<ArrayList<Tuple<Integer, DD>>>(A().size());

		for (int a = 0; a < A().size(); a++) {

			var nextBa = nextBels.get(a);
			float val = 0.0f;
			
			var _a = a;
			var argmaxGois = nextBa.parallelStream()
					.map(nb -> getBestAlpha(nb, alphas, _a))
					.collect(Collectors.toList());
			
			var argmax_iGaoi = argmaxGois.stream()
					.map(a_ -> Tuple.of(a_._0(), a_._1()))
					.collect(Collectors.toList());
			
			val = argmaxGois.stream().map(a_ -> a_._2()).reduce(0.0f, (a1_, a2_) -> a1_ + a2_);

			val *= discount;
			val += DDOP.dotProduct(b, R().get(a), i_S());

			if (val >= bestVal) {

				bestVal = val;
				bestA = a;
			}

			Gao.add((ArrayList<Tuple<Integer,DD>>) argmax_iGaoi);

		}

		if (Global.DEBUG) {

			float hitT = (System.nanoTime() - hitThen) / 1000000.0f;
			LOGGER.debug(String.format("Cache hit computation took %s msecs", hitT));
		}

		long constThen = System.nanoTime();
		var vec = constructAlphaVector(Gao.get(bestA), bestA);
		var newVec = DDOP.add(R().get(bestA), DDOP.mult(DDleaf.getDD(discount), vec));

		if (Global.DEBUG) {

			float constT = (System.nanoTime() - constThen) / 1000000.0f;
			LOGGER.debug(String.format("Alpha vector construction took %s msecs", constT));
		}

		return Tuple.of(bestA, newVec);
	}

	private DD constructAlphaVector(ArrayList<Tuple<Integer, DD>> Gao, int bestA) {

		var vec = Gao.parallelStream().map(g -> project(g._1(), bestA, oAll.get(g._0()))).reduce(DD.zero,
				(a, b) -> DDOP.add(a, b));

		return vec;
	}

	@Override
	public DD step(DD b, int a, List<Integer> o) {

		var b_n = beliefUpdate(b, a, o);
		var bnList = Global.decoupleMj(b_n, i_Mj);
		Global.modelVars.get(Global.varNames.get(i_Mj - 1)).clear();
		
		framesjSoln.forEach(f -> f.step());
		updateIS();
		
		return null;
	}

	@Override
	public DD step(DD b, String a, List<String> o) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toJson() {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toDot() {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toLabel() {

		// TODO Auto-generated method stub
		return null;
	}

}
