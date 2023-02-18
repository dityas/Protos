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
    implements PointBasedSolver<AlphaVectorPolicy> {

    private int usedBeliefs = 0;
    public final M m;
    public final AlphaVectorPolicy UB;

    public AlphaVectorPolicy Vn;

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(SymbolicPerseusSolver.class);

    public SymbolicPerseusSolver(final M m) {

        this.m = m;
        LOGGER.info("Initialized symbolic Perseus for %s", this.m.getName());
        Vn = AlphaVectorPolicy.fromR(this.m.R());

        // solve upper bound
        UB = QMDPSolver.solveQMDP(this.m);
        var actionSet = UB.getActions(this.m);
        LOGGER.info("UB for %s contains actions %s", 
                this.m.getName(), actionSet);
    }

    /*
     * Perform backups and get the next value function for a given explored belief
     * region
     */
    protected AlphaVectorPolicy solveForB(final Collection<DD> B, 
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
                            UB, 
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

            _B.removeIf(_b -> 
                    DDOP.value_b(Vn.aVecs, _b, m.i_S()) <=
                    DDOP.value_b(newVn, _b, m.i_S()));

            this.usedBeliefs++;
        }

        return new AlphaVectorPolicy(newVn);
    }

    public Tuple3<Float, Float, Float> getErrors(Collection<DD> B, 
            AlphaVectorPolicy Vn, AlphaVectorPolicy Vn_p) {

        var diffs = DDOP.getBeliefRegionEvalDiff(B, Vn_p, Vn, m.i_S());
        var _min = diffs.stream()
            .min((v1, v2) -> v1.compareTo(v2))
            .orElse(Float.NaN);
        var _max = diffs.stream()
            .max((v1, v2) -> v1.compareTo(v2))
            .orElse(Float.NaN);
        var avg = diffs.stream()
            .mapToDouble(Double::valueOf)
            .average().orElse(Double.NaN);

        return Tuple.of(_min, _max, (float) avg);
    }

    private boolean beliefsValid(Collection<DD> B) {

        for(var _b: B)
        {

            if (!DDOP.verifyJointProbabilityDist(_b, m.i_S())) {

                LOGGER.error(String.format("Belief %s is not a valid probability distribution",
                            DDOP.factors(_b, m.i_S())));
                return false;
            }
        }

        return true;
    }
    
    private void exitIfBeliefsInvalid(Collection<DD> B) {

        if (!beliefsValid(B)) {
            LOGGER.error("Belief region contains invalid beliefs");
            System.exit(-1);
        }
    }

    private void logConvergence() {
        LOGGER.info("Convergence, software version 7.0, "
                + "looking at life through the eyes of a tired heart.");
        LOGGER.info("Eating seeds as a past time activity, "
                + "the toxicity of my city of my city.");
    }

    @Override
    public AlphaVectorPolicy solve(final List<DD> b_is, int I, int H) {

        if (b_is.size() < 1) {
            LOGGER.error("[!] No initial beliefs. Returning upper bound");
            return UB;
        }

        // QMDP policy's estimate of initial beliefs
        var bVals = DDOP.getBeliefRegionEval(b_is, UB, m.i_S());
        LOGGER.info("UB evaulates initial beliefs at %s", bVals);

        // Start Perseus
        LOGGER.info("[*] Launching symbolic Perseus solver for model %s", m.getName());

        // Make initial reachability graph
        var g = ReachabilityGraph.fromDecMakingModel(m);
        var b_i = new ArrayList<>(b_is);
        b_i.forEach(g::addNode);
        exitIfBeliefsInvalid(b_i);

        // initial belief exploration based on QMDP approximation
        var explorationProb = 0.05f;
        var ES = new SSGAExploration<M, ReachabilityGraph>(explorationProb);
        LOGGER.info("[+] Starting with exploration probability %s and %s initial beliefs",
                explorationProb, b_i.size());

        ES.expand(b_i, g, m, H, UB);

        var vals = 
            DDOP.getBeliefRegionEval(
                    g.getAllNodes(), UB, m.i_S()).stream()
            .mapToDouble(Double::valueOf).average().orElse(Double.NaN);
        LOGGER.info("QMDP policy explored belief region mean of values %s", 
                vals);

        int convergenceCount = 0;
        var Vn_p = UB;
        for (int i = 0; i < I; i++) {

            var B = new ArrayList<>(g.getAllNodes());

            long then = System.nanoTime();

            // new value function after backups
            Vn_p = solveForB(B, Vn, g);

            // report error stats
            float backupT = (System.nanoTime() - then) / 1000000000.0f;
            var bellmanErrors = getErrors(B, Vn, Vn_p);
            var UBErrors = getErrors(B, Vn, UB);

            LOGGER.info("i=%2d, t=%.3f sec, |Vn|=%2d, |B|=%3d/%3d, "
                    + "bell err: %.3f, UB err: %.3f",
                    i, backupT, Vn_p.aVecs.size(), usedBeliefs, B.size(),
                    bellmanErrors._1(), UBErrors._1());
            
            // Prepare for next backup
            Vn = Vn_p;

            // Congergence check
            if (bellmanErrors._1() < 0.01 && i > 10) {

                convergenceCount += 1;
                if (convergenceCount > 5) {
                    
                    LOGGER.info("Declaring solution at Bellman error %s "
                            + "and iteration %s", bellmanErrors, i);
                    logConvergence();
                    break;
                }
            }

            else
                convergenceCount = 0;

            Global.clearHashtablesIfFull();

            // Expand belief region if close to convergence on current region
            if (bellmanErrors._1() < 0.05f)
                ES.expand(b_is, g, m, H, UB);

        } // end iterations over I

        Global.clearHashtablesIfFull();
        m.clearBackupCache();
        ES.clearCaches();
        System.gc();
        g.removeAllNodes();

        // Log exit
        LOGGER.info("Vn contains actions %s", Vn.getActions(m));
        LOGGER.info("[*] Finished solving %s", m.getName());

        // Print if approximation can be done
        for (int v1 = 0; v1 < Vn.aVecs.size(); v1++) {
            var vn = Vn.aVecs.get(v1);
            if (DDOP.canApproximate(vn._1()))
                LOGGER.warn("A solution DD can be easily approximated");
        }

        return Vn;
    }

}
