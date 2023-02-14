/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.model_ops.belief_exploration.SSGAExploration;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;

/*
 * @author adityas
 *
 */
public class SymbolicPerseusSolver<M extends PBVISolvablePOMDPBasedModel>
    implements PointBasedSolver<M, AlphaVectorPolicy> {

    private int usedBeliefs = 0;

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(SymbolicPerseusSolver.class);

    /*
     * Perform backups and get the next value function for a given explored belief
     * region
     */

    protected AlphaVectorPolicy solveForB(final M m, final Collection<DD> B, AlphaVectorPolicy Vn, ReachabilityGraph g) {

        List<Tuple<Integer, DD>> newVn = new ArrayList<>(10);
        this.usedBeliefs = 0;

        var _B = new LinkedList<DD>(B);
        var _Vn = Vn.aVecs.stream().map(a -> a._1()).collect(Collectors.toList());

        while (_B.size() > 0) {

            // DD b = _B.get(Global.random.nextInt(_B.size()));
            DD b = _B.remove(0);

            var newAlpha = m.backup(b, _Vn, g);

            // Construct V_{n+1}(b)
            float bestVal = Float.NEGATIVE_INFINITY;
            int bestA = -1;
            DD bestDD = DD.zero;
            for (int a = 0; a < Vn.aVecs.size(); a++) {

                float val = DDOP.dotProduct(b, Vn.aVecs.get(a)._1(), m.i_S());

                if (val > bestVal) {

                    bestVal = val;
                    bestA = Vn.aVecs.get(a)._0();
                    bestDD = Vn.aVecs.get(a)._1();
                }
            }

            var newAlphab = DDOP.dotProduct(b, newAlpha._1(), m.i_S());

            // If new \alpha.b > Vn(b) add it to new V
            if (newAlphab > bestVal)
                newVn.add(newAlpha);

            else
                newVn.add(Tuple.of(bestA, bestDD));

            _B.removeIf(_b -> 
                    DDOP.value_b(Vn.aVecs, _b, m.i_S()) <=
                    DDOP.value_b(newVn, _b, m.i_S()));

            this.usedBeliefs++;
        }

        return new AlphaVectorPolicy(newVn);
    }

    public float computeBellmanError(Collection<DD> B, 
            final M m, AlphaVectorPolicy Vn, AlphaVectorPolicy Vn_p) {

        float bellmanError = B.parallelStream()
            .map(_b -> Math.abs(
                        DDOP.value_b(Vn.aVecs, _b, m.i_S())
                        - DDOP.value_b(Vn_p.aVecs, _b, m.i_S())))
            .max((v1, v2) -> v1.compareTo(v2)).orElseGet(() ->
                    {
                        LOGGER.debug(
                                "Could not compute bellman error on B with size %s",
                                B.size());
                        return null;
                    });

        return bellmanError;
    }

    public static boolean beliefsValid(Collection<DD> B, List<Integer> vars) {
        for(var _b: B)
        {

            if (!DDOP.verifyJointProbabilityDist(_b, vars)) {

                LOGGER.error(String.format("Belief %s is not a valid probability distribution",
                            DDOP.factors(_b, vars)));
                return false;
            }
        }

        return true;
    }

    @Override
    public AlphaVectorPolicy solve(final List<DD> b_is,
            final M m, int I, int H, AlphaVectorPolicy Vn) {

        if (b_is.size() < 1)
            return Vn;

        var qMDPPolicy = QMDPSolver.solveQMDP(m);
        qMDPPolicy.printSolutionSet(m);

        var bVals = b_is.stream()
            .map(b -> DDOP.value_b(qMDPPolicy.aVecs, b, m.i_S()))
            .collect(Collectors.toList());

        LOGGER.info("MDP policy evaulates initial beliefs at %s", bVals);
        LOGGER.info("[*] Launching symbolic Perseus solver for model %s", m.getName());

        var g = ReachabilityGraph.fromDecMakingModel(m);
        var b_i = new ArrayList<>(b_is);

        b_i.forEach(g::addNode);

        // initial belief exploration
        var _then = System.nanoTime();

        new SSGAExploration<M, ReachabilityGraph>(0.1f)
            .expand(b_i, g, m, H, qMDPPolicy);

        var _now = System.nanoTime();
        LOGGER.info("Initial belief exploration for %s took %s msecs", m.getName(),
                ((_now - _then) / 1000000.0));

        var explorationProb = 0.2f;

        if (!beliefsValid(b_i, m.i_S())) {
            LOGGER.error("Belief region contains invalid beliefs");
            System.exit(-1);
        }

        int convergenceCount = 0;
        var ES = new SSGAExploration<M, ReachabilityGraph>(explorationProb);

        // Remove node erasing and init belief addition
        LOGGER.info("[+] Starting with exploration probability %s and %s initial beliefs",
                explorationProb, b_i.size());

        ES.expand(b_i, g, m, H, qMDPPolicy);
        if (!beliefsValid(g.getAllNodes(), m.i_S())) {
            LOGGER.error("Belief region contains invalid beliefs");
            System.exit(-1);
        }

        var vals = g.getAllNodes().stream()
            .map(d -> DDOP.value_b(qMDPPolicy.aVecs, d, m.i_S()))
            .mapToDouble(Double::valueOf).average().orElse(Double.NaN);
        LOGGER.info("QMDP policy explored belief region of values %s", vals);

        for (int i = 0; i < I; i++) {

            var _B = g.getAllNodes().stream()
                .map(__b -> Tuple.of(
                            __b, 
                            qMDPPolicy.getEvalDifferenceAtBelief(Vn,
                                __b, m.i_S())))
                .collect(Collectors.toList());

            Collections.sort(_B, (b1, b2) -> b2._1().compareTo(b1._1()));
            var B = _B.stream()
                .map(__b -> __b._0())
                .collect(Collectors.toList());

            long then = System.nanoTime();

            // new value function after backups
            var Vn_p = solveForB(m, B, Vn, g);

            float backupT = (System.nanoTime() - then) / 1000000000.0f;
            float bellmanError = computeBellmanError(B, m, Vn, Vn_p);
            var evalDiffs = B.stream()
                .map(b -> Vn_p.getEvalDifferenceAtBelief(Vn, b, m.i_S()))
                .collect(Collectors.toList());

            float imp = evalDiffs.stream()
                .reduce(Float.NaN, (v1, v2) -> v1 > v2 ? v1 : v2);
            float det = evalDiffs.stream()
                .reduce(Float.NaN, (v1, v2) -> v1 < v2 ? v1 : v2);

            float ubError = computeBellmanError(B, m, qMDPPolicy, Vn_p);

            // prepare for next iter
            Vn.aVecs.clear();
            Vn.aVecs.addAll(Vn_p.aVecs);

            LOGGER.info(
                    "i=%s, max||Vn' - Vn||=%.3f, min||Vn' - Vn||=%.3f, " +
                    "|Vn|=%s, |B|=%s/%s, " +
                    "||UB - Vn'||=%.5f", 
                    i, imp, det,
                    Vn_p.aVecs.size(), 
                    this.usedBeliefs, B.size(), ubError);

            if (bellmanError < 0.01 && i > 10) {

                convergenceCount += 1;
                if (convergenceCount > 5) {

                    LOGGER.info("Declaring solution at Bellman error %s and iteration %s",
                            bellmanError, i);
                    LOGGER.info(
                            "Convergence, software version 7.0, looking at life through the eyes of a tired heart.");
                    LOGGER.info("Eating seeds as a past time activity, the toxicity of my city of my city.");

                    break;
                }
            }

            else
                convergenceCount = 0;

            Global.clearHashtablesIfFull();

            if (bellmanError < 0.05f) {
                ES.expand(b_is, g, m, H, qMDPPolicy);
                if (!beliefsValid(g.getAllNodes(), m.i_S())) {
                    LOGGER.error("Belief region contains invalid beliefs");
                    System.exit(-1);
                }
            }

        }

        Global.clearHashtablesIfFull();
        m.clearBackupCache();
        ES.clearCaches();
        System.gc();

        if (Global.DEBUG)
            Global.logCacheSizes();

        Vn.printSolutionSet(m);
        g.removeAllNodes();
        System.gc();
        LOGGER.info("[*] Finished solving %s", m.getName());

        // Print if approximation can be done
        for (int v1 = 0; v1 < Vn.aVecs.size(); v1++) {

            var vn = Vn.aVecs.get(v1);
            if (DDOP.canApproximate(vn._1()))
                LOGGER.warn("A solution DD can be easily approximated");

            var equalSet = 
                new HashSet<Tuple3<
                Tuple<Integer, Integer>, 
                Tuple<Integer, Integer>, 
                Float>>();

            for (int v2 = 0; v2 < Vn.aVecs.size(); v2++) {

                var _vn = Vn.aVecs.get(v2);
                if (_vn.equals(vn))
                    continue;

                var diff = DDOP.maxAll(
                        DDOP.abs(DDOP.sub(vn._1(), _vn._1())));

                if (diff < 1e-4f)
                    equalSet.add(
                            Tuple.of(
                                Tuple.of(v1, vn._0()), 
                                Tuple.of(v2, _vn._0()), 
                                diff));
            }

            if (equalSet.size() > 0)
                LOGGER.warn("Found approx equal vecs %s", equalSet);
        }

        return Vn;
    }

}
