
package thinclab.solver;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.DDOP;
import thinclab.legacy.DDleaf;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.policy.AlphaVectorPolicy;

public class QMDPSolver {

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(QMDPSolver.class);

    public static AlphaVectorPolicy solveQMDP(final PBVISolvablePOMDPBasedModel m) {

        var Vn = m.R();
        if (m instanceof IPOMDP _i) {
            Vn = _i.R().stream()
                .map(r -> DDOP.addMultVarElim(
                            List.of(_i.PAjGivenEC, r), List.of(_i.i_Aj)))
                .collect(Collectors.toList());
        }

        for (int i = 0; i < 1000; i++) {

            var Vs_p = Vn.stream().reduce(
                    DDleaf.getDD(Float.NEGATIVE_INFINITY), 
                    (v1, v2) -> DDOP.max(v1, v2));

            Vn = m.MDPValueIteration(Vn);

            var Vs = Vn.stream().reduce(
                    DDleaf.getDD(Float.NEGATIVE_INFINITY), 
                    (v1, v2) -> DDOP.max(v1, v2));

            var bErr = DDOP.maxAll(DDOP.abs(DDOP.sub(Vs, Vs_p)));

            if (bErr < 1e-4) {

                LOGGER.info("QMDP value iteration converged at err %s", bErr);
                break;
            }
        }

        return AlphaVectorPolicy.fromR(Vn);
    }
}
