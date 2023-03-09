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
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.FQDDleaf;
import thinclab.legacy.Global;
import thinclab.model_ops.belief_exploration.MDPExploration;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;

/*
 * @author adityas
 *
 */
public class 
SymbolicPerseusSolver<M extends PBVISolvablePOMDPBasedModel>
    implements PointBasedSolver<AlphaVectorPolicy> {

    private int usedBeliefs = 0;
    public final M m;
    public final AlphaVectorPolicy UB;

    public AlphaVectorPolicy Vn;
    public List<Float> beliefSamplingWeights;

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
     * Mark beliefs that don't need updates 
     */
    void markUpdatedBeliefs(List<DD> B,
            List<Tuple<Integer, DD>> newVn) {

        for (int i = 0; i < B.size(); i++) {

            if (beliefSamplingWeights.get(i) == 0)
                continue;

            var b = B.get(i);

            // if a belief is evaluated higher by the new policy,
            // don't update it further for now
            if (DDOP.value_b(Vn.aVecs, b, m.i_S()) 
                    <= DDOP.value_b(newVn, b, m.i_S())) {
                beliefSamplingWeights.set(i, 0.0f);
                    }
        }
    }

    /*
     * Perform backups and get the next value function for a given explored belief
     * region
     */
    protected AlphaVectorPolicy solveForB(final List<DD> B, 
            AlphaVectorPolicy Vn, ReachabilityGraph g) {

        List<Tuple<Integer, DD>> newVn = new ArrayList<>(10);
        this.usedBeliefs = 0;

        var _Vn = Vn.aVecs.stream()
            .map(a -> a._1())
            .collect(Collectors.toList());

        // Evaluate the difference between the evaluations of UB and Vn
        beliefSamplingWeights = DDOP.getBeliefRegionEvalDiff(B, UB, Vn, m.i_S());

        while (true) {

            var index = DDOP.sample(beliefSamplingWeights);
            if (index < 0)
                break;

            // It is extremely important to remove the belief at which
            // the back up was just performed!!! Do not change this!!!
            // It populates the belief region with low quality beliefs if not
            // removed!!!
            DD b = B.get(index);
            beliefSamplingWeights.set(index, 0.0f);

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
            LOGGER.info("Value of new vector at witness is %s", newAlphab);
            LOGGER.info("Belief inside solver is %s", DDOP.factors(b, m.i_S()));

            // If new \alpha.b > Vn(b) add it to new V
            if (newAlphab > bestVal)
                newVn.add(newAlpha);

            else
                newVn.add(Tuple.of(bestA, bestDD));

            markUpdatedBeliefs(B, newVn);
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
        LOGGER.info("[*] Launching symbolic Perseus solver for model %s", 
                m.getName());

        exitIfBeliefsInvalid(b_is);

        // Belief exploration based on QMDP approximation
        var explorationProb = 0.05f;
        var ES = new MDPExploration<M>(UB, explorationProb);
        LOGGER.info("[+] Starting with exploration probability" +
                " %s and %s initial beliefs", 
                explorationProb, b_is.size());

        // get explored belief space
        var exploredSpace = ES.explore(b_is, m, H, 500);
        var B = exploredSpace.getAllNodes()
            .stream()
            .map(FQDDleaf::unquantize)
            .collect(Collectors.toList());

        // check if all beliefs seem valid
        exitIfBeliefsInvalid(B);

        // evaluate explored belef space
        var vals = 
            DDOP.getBeliefRegionEval(
                    B, UB, m.i_S()).stream()
            .mapToDouble(Double::valueOf).average().orElse(Double.NaN);
        LOGGER.info("QMDP policy explored belief region mean of values %s", 
                vals);

        int convergenceCount = 0;
        var Vn_p = UB;
        for (int i = 0; i < I; i++) {


            long then = System.nanoTime();

            // new value function after backups
            Vn_p = solveForB(B, Vn, exploredSpace);

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

        } // end iterations over I
        
        exploredSpace.printCachingStats();

        Global.clearHashtablesIfFull();
        m.clearBackupCache();
        ES.clearCaches();
        System.gc();
        exploredSpace.removeAllNodes();

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
