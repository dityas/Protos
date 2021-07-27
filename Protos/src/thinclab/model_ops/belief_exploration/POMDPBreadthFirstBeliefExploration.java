/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.model_ops.belief_update.BeliefUpdate;
import thinclab.models.POMDP;
import thinclab.models.datastructures.ReachabilityGraph;

/*
 * @author adityas
 *
 */
public class POMDPBreadthFirstBeliefExploration implements ExplorationStrategy<POMDP> {

	public final int maxB;

	private static final Logger LOGGER = LogManager.getLogger(POMDPBreadthFirstBeliefExploration.class);

	public POMDPBreadthFirstBeliefExploration() {

		this.maxB = 100;
		LOGGER.info(String.format("Initialized POMDP breadth first belief explorer for max beliefs %s", this.maxB));
	}

	public POMDPBreadthFirstBeliefExploration(int maxB) {

		this.maxB = maxB;
		LOGGER.info(String.format("Initialized POMDP breadth first belief explorer for max beliefs %s", this.maxB));
	}

	@Override
	public ReachabilityGraph expandRG(POMDP m, BeliefUpdate<POMDP> BE, ReachabilityGraph RG) {

		if (RG.connections.size() < this.maxB) {

			RG.getChildren().stream().forEach(b ->
				{

					RG.edgeIndexMap.keySet().stream().forEach(e ->
						{

							if (RG.getNodeAtEdge(b, e).isEmpty() && RG.connections.size() < this.maxB) {

								var a = e.get(e.size() - 1);
								var o = e.subList(0, e.size() - 1);

								var bNext = BE.beliefUpdate(m, b, a, o);

								RG.addEdge(b, e, bNext);
							}
						});
				});
		}

		return RG;
	}
}
