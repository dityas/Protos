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
import thinclab.legacy.Global;
import thinclab.model_ops.belief_exploration.MDPExploration;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVector;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.utils.Tuple;

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

        // initialize lower bound as the reward function
        Vn = AlphaVectorPolicy.getLowerBound(m);

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
            AlphaVectorPolicy newVn) {

        for (int i = 0; i < B.size(); i++) {

            if (beliefSamplingWeights.get(i) == 0)
                continue;

            var b = B.get(i);

            // if a belief is evaluated higher by the new policy,
            // don't update it further for now
            var diff = DDOP.value_b(Vn, b) - DDOP.value_b(newVn, b);
            if (diff > 0.0)
                beliefSamplingWeights.set(i, diff);
            else
                beliefSamplingWeights.set(i, 0.0f);
        }
    }

    protected AlphaVectorPolicy solveForB(final List<DD> B, 
            AlphaVectorPolicy Vn, ReachabilityGraph g) {
        /*
         * Perform backups and get the next value function for a given explored 
         * belief region
         */

        var newVn = new AlphaVectorPolicy(m.i_S());
        this.usedBeliefs = 0;

        var _Vn = Vn.getAsDDList();
        beliefSamplingWeights = DDOP.getBeliefRegionEvalDiff(B, UB, Vn);

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
            AlphaVector best = null;

            for (var vec: Vn) {

                float val = DDOP.dotProduct(b, vec.getVector(), m.i_S());

                if (val > bestVal) { 
                    bestVal = val;
                    best = vec;
                }
                
            }

            // If new \alpha.b >= Vn(b) add it to new V
            if (newAlpha.getVal() >= bestVal)
                newVn.add(newAlpha);

            else
                newVn.add(best);

            markUpdatedBeliefs(B, newVn);
            this.usedBeliefs++;
        }

        beliefSamplingWeights = DDOP.getBeliefRegionEvalDiff(B, newVn, Vn);
        return newVn;
    }

    public float getBellmanError(Collection<DD> B, 
            AlphaVectorPolicy Vn, AlphaVectorPolicy Vn_p) {

        return DDOP.getBeliefRegionEvalDiff(B, Vn_p, Vn)
            .stream()
            .max((v1, v2) -> v1.compareTo(v2))
            .orElse(Float.NaN);
    }

    private boolean beliefsValid(Collection<DD> B) {

        for(var _b: B) {
            if (!DDOP.verifyJointProbabilityDist(_b, m.i_S())) {
                LOGGER.error("Belief %s is not a valid probability distribution",
                        DDOP.factors(_b, m.i_S()));
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
        var ubVals = b_is.stream()
            .map(b -> DDOP.bestAlphaWithValue(UB, b))
            .map(v -> Tuple.of(m.A().get(v._0().getActId()), v._1()))
            .collect(Collectors.toList());
        LOGGER.info("UB evaulates initial beliefs at %s", ubVals);

        var lbVals = b_is.stream()
            .map(b -> DDOP.bestAlphaWithValue(Vn, b))
            .map(v -> Tuple.of(m.A().get(v._0().getActId()), v._1()))
            .collect(Collectors.toList());
        LOGGER.info("LB evaulates initial beliefs at %s", lbVals);

        // Start Perseus
        LOGGER.info("[*] Launching symbolic Perseus solver for model %s", 
                m.getName());

        exitIfBeliefsInvalid(b_is);

        // Belief exploration based on QMDP approximation
        var explorationProb = 0.05f;
        var ES = new MDPExploration<M>(UB, explorationProb);
        LOGGER.info("[+] Starting with exploration probability %s and %s initial beliefs", 
                explorationProb, b_is.size());

        // get explored belief space
        var exploredSpace = ES.explore(b_is, m, H, 500);
        var B = new ArrayList<>(exploredSpace.getAllNodes());

        // evaluate explored belef space
        var vals = DDOP.getBeliefRegionEval(B, UB).stream()
            .mapToDouble(Double::valueOf).average().orElse(Double.NaN);
        LOGGER.info("QMDP policy explored belief region mean of values %s", vals);

        int convergenceCount = 0;
        var Vn_p = UB;
        for (int i = 0;; i++) {

            long then = System.nanoTime();

            // new value function after backups
            Vn_p = solveForB(B, Vn, exploredSpace);

            // report error stats
            float backupT = (System.nanoTime() - then) / 1000000000.0f;
            var bellmanError = getBellmanError(B, Vn, Vn_p);
            var UBError = getBellmanError(B, Vn, UB);

            LOGGER.info("i=%2d, t=%.3f sec, |Vn|=%2d, |B|=%3d/%3d, "
                    + "bell err: %.3f, UB err: %.3f",
                    i, backupT, Vn_p.size(), usedBeliefs, B.size(),
                    bellmanError, UBError);

            // Prepare for next backup
            Vn = Vn_p;

            // Congergence check
            if (bellmanError < 0.01 && i > 10) {

                convergenceCount += 1;
                if (convergenceCount > 5) {

                    LOGGER.info("Declaring solution at Bellman error %s "
                            + "and iteration %s", bellmanError, i);
                    logConvergence();
                    break;
                }
            }

            else
                convergenceCount = 0;

            Global.clearHashtablesIfFull();

        } // end iterations over I

        Global.clearHashtablesIfFull();
        m.clearBackupCache();
        ES.clearCaches();
        System.gc();
        exploredSpace.removeAllNodes();

        // Log exit
        LOGGER.info("Vn contains actions %s", Vn.getActions(m));
        var valsAtWitnesses = Vn.stream()
            .map(v -> Tuple.of(m.A().get(v.getActId()), v.getVal()))
            .collect(Collectors.toList());
        LOGGER.info("Vn values at witness points are %s", valsAtWitnesses);
        LOGGER.info("[*] Finished solving %s", m.getName());

        // Print if approximation can be done
        for (var vec: Vn) {
            if (DDOP.canApproximate(vec.getVector()))
                LOGGER.warn("A solution DD can be easily approximated");
        }

        return Vn;
    }
}
