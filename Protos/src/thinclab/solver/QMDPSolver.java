
package thinclab.solver;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.policy.AlphaVector;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.policy.Policy;

public class QMDPSolver implements Solver {

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(QMDPSolver.class);

    public final int maxIter;
    public final float tolerance;

    public QMDPSolver(final int maxIter, final float tolerance) {

        this.maxIter = maxIter;
        this.tolerance = tolerance;
        LOGGER.info("QMDP solver initialized for max iterations %s and tolerance %s",
                this.maxIter, this.tolerance);
    }

	public Policy<DD> solve(final PBVISolvablePOMDPBasedModel m) {

        var Vn = m.R();
        if (m instanceof IPOMDP _i) {
            Vn = _i.R().stream()
                .map(r -> DDOP.addMultVarElim(
                            List.of(_i.PAjGivenEC, r), List.of(_i.i_Aj)))
                .collect(Collectors.toList());
        }

        for (int i = 0; i < this.maxIter; i++) {

            // max_a[Qfn]
            var Vs_p = Vn.stream().reduce(
                    DDleaf.getDD(Float.NEGATIVE_INFINITY), 
                    (v1, v2) -> DDOP.max(v1, v2));

            // perform value iteration
            Vn = m.MDPValueIteration(Vn);

            var Vs = Vn.stream().reduce(
                    DDleaf.getDD(Float.NEGATIVE_INFINITY), 
                    (v1, v2) -> DDOP.max(v1, v2));

            var bErr = DDOP.maxAll(DDOP.abs(DDOP.sub(Vs, Vs_p)));
            Global.clearHashtablesIfFull();            

            if (bErr < this.tolerance) {

                LOGGER.info("QMDP value iteration converged at err %s", bErr);
                break;
            }
        }

        var policy = new AlphaVectorPolicy(m.i_S());
        for (int i = 0; i < Vn.size(); i++)
            policy.add(new AlphaVector(i, Vn.get(i), Float.NaN));

        return policy;
    }

    public static AlphaVectorPolicy solveQMDP(final PBVISolvablePOMDPBasedModel m) {

        var Vn = m.R();
        if (m instanceof IPOMDP _i) {
            Vn = _i.R().stream()
                .map(r -> DDOP.addMultVarElim(
                            List.of(_i.PAjGivenEC, r), List.of(_i.i_Aj)))
                .collect(Collectors.toList());
        }

        for (int i = 0; i < 1000; i++) {

            // max_a[Qfn]
            var Vs_p = Vn.stream().reduce(
                    DDleaf.getDD(Float.NEGATIVE_INFINITY), 
                    (v1, v2) -> DDOP.max(v1, v2));

            // perform value iteration
            Vn = m.MDPValueIteration(Vn);

            var Vs = Vn.stream().reduce(
                    DDleaf.getDD(Float.NEGATIVE_INFINITY), 
                    (v1, v2) -> DDOP.max(v1, v2));

            var bErr = DDOP.maxAll(DDOP.abs(DDOP.sub(Vs, Vs_p)));
            Global.clearHashtablesIfFull();            

            if (bErr < 0.001) {

                LOGGER.info("QMDP value iteration converged at err %s", bErr);
                break;
            }
        }

        var policy = new AlphaVectorPolicy(m.i_S());
        for (int i = 0; i < Vn.size(); i++)
            policy.add(new AlphaVector(i, Vn.get(i), Float.NaN));

        return policy;
    }
}
