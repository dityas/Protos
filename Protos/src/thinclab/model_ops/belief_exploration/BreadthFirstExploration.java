/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.ReachabilityGraph;

/*
 * @author adityas
 *
 */
public class BreadthFirstExploration<M extends PBVISolvablePOMDPBasedModel>
		implements ExplorationStrategy<M> {

	private static final Logger LOGGER = LogManager.getLogger(BreadthFirstExploration.class);

	public BreadthFirstExploration(int maxCachedB) {
        LOGGER.info("Initialized breadth-first exploration");
	}

	@Override
	public ReachabilityGraph explore(List<DD> bs, M m, int T, int maxI) {
        return null;
	}

}
