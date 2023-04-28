/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

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
public class MDPExploration
<M extends PBVISolvablePOMDPBasedModel> implements 
ExplorationStrategy<M> {

    private final float e;
    public final int maxB;
    private final AlphaVectorPolicy mdpPolicy;

    private HashMap<Tuple<DD, Integer>, DD> likelihoodsCache = 
        new HashMap<>();

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(MDPExploration.class);

    public MDPExploration(AlphaVectorPolicy mdpPolicy,
            float explorationProb) {

        this.e = explorationProb;
        this.maxB = 200;
        this.mdpPolicy = mdpPolicy;
        LOGGER.debug(
                "Initialized MDP exploration for exploration probability %s",
                e);
    }

    public DD getObsLikelihoods(DD b, 
            int a, M m, 
            HashMap<Tuple<DD, Integer>, DD> lCache) {

        var key = Tuple.of(b, a);

        // get likelihoods from cache
        var likelihoods = lCache.get(key);

        // if cache does not contain likelihoods,
        // compute them and cache them
        if (likelihoods == null) {
            likelihoods = m.obsLikelihoods(b, a);
            lCache.put(key, likelihoods);
        }
        
        return likelihoods;
    }

    /*
     * Explore a single path from starting belief b
     */
    public void runTrace(DD b, 
            M m, int T, ReachabilityGraph exploredSpace) {
    
        // for length t, explore a path as long as we have not
        // exceeded max explotation
        while ((T-- > 0) && exploredSpace.getNodeCount() < maxB) 
            b = getNextBelief(b, m, exploredSpace);
    }

    public DD getNextBelief(DD b, 
            M m, ReachabilityGraph exploredSpace) {

        // sample according to exploration prob, else
        // get best action from MDP policy
        int a = (Global.random.nextFloat() <= e) ?
            Global.random.nextInt(m.A().size()) :
            mdpPolicy.getBestActionIndex(b);

        // sample an observation
        var likelihoods = getObsLikelihoods(b, a, m, likelihoodsCache);
        var o = DDOP.sample(List.of(likelihoods), m.i_Om_p());

        // update and store next belief
        var qBelief = b;
        var edge = Tuple.of(a, o._1());
        var nextBelief = exploredSpace.getNodeAtEdge(qBelief, edge);

        // if next belief does not exist, compute it
        if (nextBelief == null) {
            nextBelief = m.beliefUpdate(b, a, o._1());
            exploredSpace.addEdge(
                    qBelief, edge, nextBelief);
        }

        return nextBelief;
    }

    @Override
    public ReachabilityGraph explore(List<DD> bs,
            M m, int T, int maxI) {

        // use reachability graph to track explored space
        ReachabilityGraph exploredSpace = 
            ReachabilityGraph.fromDecMakingModel(m);
        bs.forEach(exploredSpace::addNode);

        if (exploredSpace.getNodeCount() >= maxB)
            return exploredSpace;

        int numIter = maxI;
        while ((maxI-- > 0) && exploredSpace.getNodeCount() < maxB) {

            // sample a random initial belief and explore a path
            // into the belief region from there
            var b = bs.get(Global.random.nextInt(bs.size()));
            runTrace(b, m, T, exploredSpace);
        }

        LOGGER.info("After %s iterations of length %s, " +
                "explored belief region contains %s beliefs",
                (numIter - maxI), T, exploredSpace.getNodeCount());

        return exploredSpace;
    }

    public void clearCaches() {
        likelihoodsCache.clear();
    }

    public HashMap<Tuple<DD, Integer>, DD> getLikelihoodsCache() {
        LOGGER.warn("Calling a test method");
        return this.likelihoodsCache;
    }

}
