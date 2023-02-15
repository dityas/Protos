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
    public final M m;
    public final AlphaVectorPolicy UB;

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(SymbolicPerseusSolver.class);

    public SymbolicPerseusSolver(final M m) {

        this.m = m;
        LOGGER.info("Initialized symbolic Perseus for %s", this.m.getName());

        // solve upper bound
        UB = QMDPSolver.solveQMDP(this.m);
        var actionSet = UB.getActions(this.m);
        LOGGER.info("UB for %s contains actions %s", actionSet);
    }

    /*
     * Perform backups and get the next value function for a given explored belief
     * region
     */
    protected AlphaVectorPolicy solveForB(final M m, final Collection<DD> B, 
            AlphaVectorPolicy Vn, ReachabilityGraph g) {

        List<Tuple<Integer, DD>> newVn = new ArrayList<>(10);
        this.usedBeliefs = 0;

        var _B = new LinkedList<DD>(B);
        var _Vn = Vn.aVecs.stream().map(a -> a._1()).collect(Collectors.toList());

        while (_B.size() > 0) {

            var index = Global.random.nextInt(_B.size());

            if (newVn.size() > 0) {
            
                var diffs = 
                    DDOP.getBeliefRegionEvalDiff(_B, 
                            new AlphaVectorPolicy(newVn), 
                            Vn, m.i_S());

                index = DDOP.sample(diffs);
            }

            // It is extremely important to remove the belief at which
            // the back up was just performed!!! Do not change this!!!
            // It populates the belief region with low quality beliefs if not
            // removed!!!
            DD b = _B.remove(index);

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

            // Just add the new alpha vector without comparing
            // newVn.add(newAlpha);

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

        // QMDP approximation policy
        var qMDPPolicy = QMDPSolver.solveQMDP(m);
        qMDPPolicy.printSolutionSet(m);

        // QMDP policy's estimate of initial beliefs
        var bVals = DDOP.getBeliefRegionEval(b_is, qMDPPolicy, m.i_S());
        LOGGER.info("MDP policy evaulates initial beliefs at %s", bVals);

        // Start Perseus
        LOGGER.info("[*] Launching symbolic Perseus solver for model %s", m.getName());

        // Make initial reachability graph
        var g = ReachabilityGraph.fromDecMakingModel(m);
        var b_i = new ArrayList<>(b_is);
        b_i.forEach(g::addNode);

        var explorationProb = 0.2f;
        if (!beliefsValid(b_i, m.i_S())) {
            LOGGER.error("Belief region contains invalid beliefs");
            System.exit(-1);
        }
        var ES = new SSGAExploration<M, ReachabilityGraph>(explorationProb);
        LOGGER.info("[+] Starting with exploration probability %s and %s initial beliefs",
                explorationProb, b_i.size());

        // initial belief exploration based on QMDP approximation
        ES.expand(b_i, g, m, H, qMDPPolicy);
        if (!beliefsValid(g.getAllNodes(), m.i_S())) {
            LOGGER.error("Belief region contains invalid beliefs");
            System.exit(-1);
        }

        var vals = 
            DDOP.getBeliefRegionEval(
                    g.getAllNodes(), qMDPPolicy, m.i_S()).stream()
            .mapToDouble(Double::valueOf).average().orElse(Double.NaN);
        LOGGER.info("QMDP policy explored belief region mean of values %s", 
                vals);

        int convergenceCount = 0;
        var Vn_p = qMDPPolicy;
        for (int i = 0; i < I; i++) {

            var B = new ArrayList<>(g.getAllNodes());

            long then = System.nanoTime();

            // new value function after backups
            Vn_p = solveForB(m, B, Vn, g);

            float backupT = (System.nanoTime() - then) / 1000000000.0f;
            float bellmanError = computeBellmanError(B, m, Vn, Vn_p);
            var ubErrors =
                DDOP.getBeliefRegionEvalDiff(
                        B, qMDPPolicy, Vn_p, m.i_S())
                .stream()
                .collect(Collectors.toList());

            var minUBErr = ubErrors.stream()
                .min((v1, v2) -> v1.compareTo(v2)).orElse(Float.NaN);
            var maxUBErr = ubErrors.stream()
                .max((v1, v2) -> v1.compareTo(v2)).orElse(Float.NaN);

            LOGGER.info(
                    "i=%s, max||Vn' - Vn||=%.3f, " +
                    "|Vn|=%s, |B|=%s/%s, " +
                    "|UB - Vn'|: min=%.3f max=%.3f", 
                    i, bellmanError,
                    Vn_p.aVecs.size(), 
                    this.usedBeliefs, B.size(), minUBErr, maxUBErr);

            Vn = Vn_p;

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

        Vn_p.printSolutionSet(m);
        g.removeAllNodes();
        System.gc();
        LOGGER.info("[*] Finished solving %s", m.getName());

        // Print if approximation can be done
        for (int v1 = 0; v1 < Vn_p.aVecs.size(); v1++) {
            var vn = Vn.aVecs.get(v1);
            if (DDOP.canApproximate(vn._1()))
                LOGGER.warn("A solution DD can be easily approximated");
        }

        return Vn_p;
    }

}
