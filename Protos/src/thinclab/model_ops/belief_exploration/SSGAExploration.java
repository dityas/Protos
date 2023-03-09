/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class SSGAExploration
<M extends PBVISolvablePOMDPBasedModel> implements 
    ExplorationStrategy<M> 
{

    private final float e;
    public final int maxB;
    private final AlphaVectorPolicy P;

    private HashMap<Tuple<DD, Integer>, DD> likelihoodsCache = 
        new HashMap<>();

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(SSGAExploration.class);

    public SSGAExploration(AlphaVectorPolicy P, float explorationProb) {

        this.e = explorationProb;
        this.P = P;
        this.maxB = 300;
        LOGGER.debug(
                "Initialized SSGA exploration for exploration probability %s",
                e);
    }

    public float getMinDistance(DD b, Collection<DD> beliefs) {

        return beliefs.parallelStream()
            .map(_b -> DDOP.maxAll(DDOP.abs(DDOP.sub(_b, b))))
            .min((d1, d2) -> d1.compareTo(d2))
            .get();
    }

    public boolean isUniqueBelief(DD b, 
            Collection<DD> beliefs, float minDist) {

        var _minDist = getMinDistance(b, beliefs);
        if (_minDist >= minDist)
            return true;

        else
            return false;
    }

    private void cacheIfNotPresent(DD b, int a, M m) {
        if (!likelihoodsCache.containsKey(
                    Tuple.of(b, a)))
            likelihoodsCache.put(
                    Tuple.of(b, a), 
                    m.obsLikelihoods(b, a));
    }

    @Override
    public ReachabilityGraph explore(List<DD> bs, M m, int T, int maxI) {

        float Pa = 1 - e;
        ReachabilityGraph g = ReachabilityGraph.fromDecMakingModel(m);

        if (Pa > 1 || Pa < 0) {

            LOGGER.error(
                    "Exploration prob for SSGA exploration is %s which makes" +
                    " greedy action selection prob %s", e, (1 - e));
            System.exit(-1);
        }

        if (g.getNodeCount() >= maxB)
            return g;

        for (int n = 0; n < 30; n++) {

            if (g.getNodeCount() >= maxB)
                break;

            for (int init_b = 0; init_b < bs.size(); init_b++) {

                DD b = bs.get(init_b);

                for (int i = 0; i < T; i++) {

                    if (g.getNodeCount() >= maxB)
                        break;

                    var usePolicy = DDOP.sampleIndex(List.of(e, Pa));
                    int a = -1;

                    // greedy action
                    if (usePolicy == 1) {

                        a = P.getBestActionIndex(b);

                        cacheIfNotPresent(b, a, m);
                        var l = likelihoodsCache.get(
                                Tuple.of(b, a));

                        var oSampled = DDOP.sample(List.of(l), m.i_Om_p());

                        var _edge = Tuple.of(a, oSampled._1());
                        var b_ = g.getNodeAtEdge(b, _edge);

                        if (b_ == null) {

                            var b_n = m.beliefUpdate(b, _edge._0(), _edge._1());

                            if (isUniqueBelief(b_n, g.getAllNodes(), 0.01f))
                                g.addEdge(b, _edge, b_n);

                            b = b_n;
                        }

                        else
                            b = b_;

                    }

                    // exploratory action
                    else if (usePolicy == 0) {

                        int _a = Global.random.nextInt(m.A().size());

                        cacheIfNotPresent(b, _a, m);
                        var l = likelihoodsCache.get(
                                Tuple.of(b, _a));

                        var oSampled = DDOP.sample(List.of(l), m.i_Om_p());
                        var _edge = Tuple.of(_a, oSampled._1());
                        var b_ = g.getNodeAtEdge(b, _edge);

                        if (b_ == null)
                            b_ = m.beliefUpdate(b, _edge._0(), _edge._1());

                        var dist = getMinDistance(b_, g.getAllNodes());
                        if (dist > 0.01f)
                            g.addEdge(b, _edge, b_);

                        b = b_;
                    }

                    else {

                        LOGGER.error("Error while sampling exploration probability");
                        System.exit(-1);
                    }

                }
            }
        }

        return g;
    }

    public void clearCaches() {

        likelihoodsCache.clear();
    }

    public HashMap<Tuple<DD, Integer>, DD> getLikelihoodsCache() {
        LOGGER.warn("Calling a test method");
        return this.likelihoodsCache;
    }

}
