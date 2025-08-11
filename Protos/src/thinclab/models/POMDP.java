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
import thinclab.legacy.Global;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVector;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;

/*
 * @author adityas
 *
 */
public class POMDP extends PBVISolvablePOMDPBasedModel {

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(POMDP.class);

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

        if (obsProb.getVal() < 1e-8) {
            LOGGER.error("Zero probability observation");
            return DDleaf.getDD(Float.NaN);
        }

        nextBelState = DDOP.div(nextBelState, obsProb);

        return nextBelState;
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

    public List<Tuple3<Integer, DD, Float>> computeNextBaParallel(DD b, DD likelihoods, int a, ReachabilityGraph g) {

        var res = IntStream.range(0, oAll.size()).parallel().boxed()
            .map(o -> Tuple.of(DDOP.restrict(likelihoods, i_Om_p, oAll.get(o)).getVal(), o))
            .filter(o -> o._0() > 1e-6f).map(o ->
                    {
                        var b_n = beliefUpdate(b, a, oAll.get(o._1()));
                        return Tuple.of(o._1(), b_n, o._0());
                    })
        .collect(Collectors.toList());

        return res;
    }

    public int getBestProjectionIndexAtO(DD nextBelief, AlphaVectorPolicy Vn) {
        return DDOP.bestAlphaIndex(Vn, nextBelief);
    }

    @Override
    public AlphaVector backup(DD b,
            AlphaVectorPolicy Vn, ReachabilityGraph g) {

        int bestA = -1;
        float bestVal = Float.NEGATIVE_INFINITY;

        var nextBels = belCache.get(b);

        if (nextBels == null) {

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

                // var bestAlpha = Gaoi(b_n, a, oAll.get(obsIndex), alphas);
                var bestAlphaIndex = DDOP.bestAlphaIndex(Vn, b_n);
                var bestAlpha = Vn.get(bestAlphaIndex);
                var bestAlphaVal = DDOP.dotProduct(Vn.get(bestAlphaIndex).getVector(), 
                        b_n, Vn.stateIndices);

                argmax_iGaoi.add(Tuple.of(obsIndex, bestAlpha.getVector()));
                val += (prob * bestAlphaVal);
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

        return new AlphaVector(bestA, DDOP.approximate(vec), bestVal);
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
