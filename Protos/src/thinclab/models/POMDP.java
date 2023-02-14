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
import thinclab.legacy.FQDDleaf;
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

	public POMDP(List<String> S, List<String> O, String A, 
            HashMap<String, Model> dynamics, HashMap<String, DD> R,
			float discount) {

		super(S, O, A, dynamics, R, discount);
	}

    public POMDP(List<String> S, List<String> O, String A, 
            List<List<DD>> TF, List<List<DD>> OF, List<DD> R, 
            float discount) {

        super(S, O, A, TF, OF, R, discount);
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

	// -----------------------------------------------------------------------
	// Implementation of BeliefBasedAgent<DD>

	@Override
	public DD beliefUpdate(DD b, int a, List<Integer> o) {

		try {

			var OFao = DDOP.restrict(this.OF.get(a), i_Om_p, o);

			// concat b, TF and OF
			var dynamicsArray = new ArrayList<DD>(1 + i_S.size() + i_Om.size());
			dynamicsArray.add(b);
			dynamicsArray.addAll(TF.get(a));
			dynamicsArray.addAll(OFao);

			// Sumout[S] P(O'=o| S, A=a) x P(S'| S, A=a) x P(S)
			DD nextBelState = DDOP.addMultVarElim(dynamicsArray, i_S);

			nextBelState = DDOP.primeVars(
                    nextBelState, -(Global.NUM_VARS / 2));
			DD obsProb = DDOP.addMultVarElim(List.of(nextBelState), i_S);

			if (obsProb.getVal() < 1e-8)
				return DD.zero;

			nextBelState = DDOP.div(nextBelState, obsProb);

			return nextBelState;
		}

		catch (Exception e) {

			LOGGER.error("Error while running POMDP belief update!");

			LOGGER.info("If you are not able to find the cause of the error, here are the likely causes,");
			LOGGER.info("1. A DD in the dynamics is defined over wrong variables that are not in the POMDP.");
			LOGGER.info(
					"2. If there is a sumout expression in the dynamics DBN, there might be a problem with operator precedence. Use brackets.");
			LOGGER.info("3. You are likely summing out over a wrong variable in the dynamics.");
			LOGGER.info(
					"If none of the above seem to be the problem, contact me (Aditya Shinde) through GitHub and delay my Ph.D. by another 6 months.");

			LOGGER.error(String.format("Starting belief %s", b));
			LOGGER.error(String.format("Actions %s index %s", A().get(a), a));
			LOGGER.error(String.format("Obs: %s", o));

			var OFao = DDOP.restrict(this.OF.get(a), i_Om_p, o);

			// concat b, TF and OF
			var dynamicsArray = new ArrayList<DD>(1 + i_S.size() + i_Om.size());
			dynamicsArray.add(b);
			dynamicsArray.addAll(TF.get(a));
			dynamicsArray.addAll(OFao);

			// Sumout[S] P(O'=o| S, A=a) x P(S'| S, A=a) x P(S)
			DD nextBelState = DDOP.addMultVarElim(dynamicsArray, i_S);

			LOGGER.error(String.format("NextBelState %s", nextBelState));

			nextBelState = DDOP.primeVars(nextBelState, -(Global.NUM_VARS / 2));
			DD obsProb = DDOP.addMultVarElim(List.of(nextBelState), i_S);
			LOGGER.error(String.format("Obs Prob: %s", obsProb));

			e.printStackTrace();
			System.exit(-1);
		}

		return DD.zero;
	}

	@Override
	public DD beliefUpdate(DD b, String a, List<String> o) {

		int actIndex = Collections.binarySearch(this.A, a);
		var obs = new ArrayList<Integer>(i_Om.size());

		for (int i = 0; i < i_Om_p.size(); i++) {
			obs.add(Collections.binarySearch(
                        Global.valNames.get(i_Om.get(i) - 1), 
                        o.get(i)) + 1);
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
	// Mj Space transformations. (Only here to conform to POSeqDecModel interface)
	// This will probably change in the future

	@Override
	public DD step(DD b, int a, List<Integer> o) {

		return beliefUpdate(b, a, o);
	}

	@Override
	public DD step(DD b, String a, List<String> o) {

		return beliefUpdate(b, a, o);
	}

//	@Override
//	public void step(Set<Tuple<Integer, ReachabilityNode>> modelFilter) {
//
//	}
	
	@Override
	public void step() {}

    public Object toLispObjects() {

        var transitionFunction = new ArrayList<Object>(i_S().size() + 1);
        transitionFunction.add("list");
        transitionFunction.addAll(TF.get(0));

        return transitionFunction;
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

	public Tuple<Float, Integer> Gaoi(final DD b, 
            final int a, final List<Integer> o, final List<DD> alphas) {

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
	
	public List<Tuple3<Integer, DD, Float>> computeNextBaParallelOptimized(DD b, 
            int a, ReachabilityGraph g) {
		
		// compute that huge factor
		var factors = new ArrayList<DD>(S().size() + O().size() + 1);

		factors.add(b);
		factors.addAll(T().get(a));
		factors.addAll(O().get(a));

		var thatHugeFactor = DDOP.addMultVarElim(factors, i_S());

		DD l = DD.zero;
		
		var key = Tuple.of(b, a);
		if (lCache.containsKey(key))
			l = lCache.get(key);
		
		else {
			l = DDOP.addMultVarElim(List.of(thatHugeFactor), i_S_p());
			lCache.put(Tuple.of(b, a), l);
		}
		
		final var _l = l;
//		var _diff = DDOP.maxAll(DDOP.abs(DDOP.sub(likelihoods, l)));
//		if (_diff > 1e-4f) {
//			
//			LOGGER.error("[!!!] Likelihoods don't match!");
//			LOGGER.debug(String.format("Difference is %s", _diff));
//			LOGGER.debug(String.format("Original for b=%s, a=%s is %s", b, a, likelihoods));
//			LOGGER.debug(String.format("Computed for b=%s, a=%s is %s", b, a, _l));
//			System.exit(-1);
//		}
//		
//		else
//			LOGGER.debug("[*] Yaay! Likelihoods match!!!");
		
//		var res = IntStream.range(0, oAll.size()).parallel().boxed()
//				.map(o -> Tuple.of(DDOP.restrict(likelihoods, i_Om_p, oAll.get(o)).getVal(), o))
//				.filter(o -> o._0() > 1e-6f).map(o ->
//					{
//
//						var b_n = g.getNodeAtEdge(b, Tuple.of(a, oAll.get(o._1())));
//
//						if (b_n == null)
//							b_n = beliefUpdate(b, a, oAll.get(o._1()));
//
//						return Tuple.of(o._1(), b_n, o._0());
//					})
//				.collect(Collectors.toList());
		
		var _res = IntStream.range(0, oAll.size()).parallel().boxed()
				.map(o -> Tuple.of(DDOP.restrict(_l, i_Om_p, oAll.get(o)).getVal(), o))
				.filter(o -> o._0() > 1e-6f).map(o ->
					{

						var b_n = g.getNodeAtEdge(b, Tuple.of(a, oAll.get(o._1())));

						if (b_n == null)
							b_n = DDOP.div(
									DDOP.primeVars(
											DDOP.restrict(
													thatHugeFactor, i_Om_p, oAll.get(o._1())),
													-(Global.NUM_VARS / 2)),
									DDleaf.getDD(o._0()));

						return Tuple.of(o._1(), b_n, o._0());
					})
				.collect(Collectors.toList());
		
//		IntStream.range(0, res.size()).boxed().forEach(i -> {
//			
//			var diff = DDOP.maxAll(DDOP.abs(DDOP.sub(res.get(i)._1(), _res.get(i)._1())));
//			LOGGER.debug(diff);
//			
//			if (diff > 1e-6f) {
//				LOGGER.error("Mistake during likelihood computation");
//				System.exit(-1);
//			}
//		});

		return _res;
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

	public Tuple<Integer, DD> backup(DD b,
            List<DD> alphas, ReachabilityGraph g) {

		int bestA = -1;
		float bestVal = Float.NEGATIVE_INFINITY;

		var nextBels = belCache.get(FQDDleaf.quantize(b));

		if (nextBels == null) {

			long missThen = System.nanoTime();
			// build cache entry for this belief
			nextBels = new HashMap<Integer, 
                     List<Tuple3<Integer, DD, Float>>>(A().size());

			var res = IntStream.range(0, A().size()).parallel().boxed()
					.map(a -> Tuple.of(
                                a, computeNextBaParallel(
                                    b, obsLikelihoods(b, a), a, g)))
					.collect(Collectors.toList());

			for (var r : res)
				nextBels.put(r._0(), r._1());

			belCache.put(FQDDleaf.quantize(b), nextBels);

			if (Global.DEBUG) {

				float missT = (System.nanoTime() - missThen) / 1000000.0f;
				LOGGER.debug("Cache missed computation took %s msecs", missT);
			}
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

		var vec = constructAlphaVector(Gao.get(bestA), bestA);
		vec = DDOP.add(R().get(bestA), DDOP.mult(DDleaf.getDD(discount), vec));

		return Tuple.of(bestA, DDOP.approximate(vec));
	}
	
	private DD constructAlphaVector(ArrayList<Tuple<Integer, DD>> Gao,
            int bestA) {

		var vec = Gao.parallelStream()
            .map(g -> project(g._1(), bestA, oAll.get(g._0())))
            .reduce(DD.zero, (a, b) -> DDOP.add(a, b));

		return vec;
	}

    private DD project(int a, DD vec) {

        var ddArray = new ArrayList<DD>();
        ddArray.addAll(TF.get(a));
        ddArray.add(DDOP.primeVars(vec, (Global.NUM_VARS / 2)));

        return DDOP.approximate(DDOP.addMultVarElim(ddArray, i_S_p));
    }

    @Override
    public List<DD> MDPValueIteration(List<DD> QFn) {
    
        DD Vn = QFn.stream()
            .reduce(
                    DDleaf.getDD(Float.NEGATIVE_INFINITY),
                    (q1, q2) -> DDOP.max(q1, q2));

        int A = this.A.size();
        var gamma = DDleaf.getDD(discount);
        List<DD> nextVn = IntStream.range(0, A).boxed().parallel()
            .map(a -> Tuple.of(
                        R.get(a), 
                        project(a, Vn)))
            .map(q -> DDOP.add(q._0(), DDOP.mult(gamma, q._1())))
            .collect(Collectors.toList());

        return nextVn;
    }

}
