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
import com.google.common.base.Optional;
import thinclab.models.BeliefBasedModel;
import thinclab.models.POMDP;
import thinclab.models.datastructures.AbstractAOGraph;
import thinclab.models.datastructures.ReachabilityGraph;

/*
 * @author adityas
 *
 */
public class BreadthFirstExploration<B, A extends BeliefBasedModel<B>, G extends AbstractAOGraph<B, List<String>>>
		implements ExplorationStrategy<A, G> {

	private final int maxB;
	
	public BreadthFirstExploration(int maxB) {

		this.maxB = maxB;
	}

	@Override
	public G expandRG(A a, G g) {

		if (g.getAllNodes().size() < this.maxB) {
			// for each belief node b
			g.getChildren().stream().forEach(b ->
				{
					// for each action-observation edge e
					g.edgeIndexMap.keySet().stream().forEach(e ->
						{

							if (g.getNodeAtEdge(b, e).isEmpty() && (g.getAllNodes().size() < this.maxB)) {

								var _a = e.get(e.size() - 1);
								var _o = e.subList(0, e.size() - 1);
								
								// compute next belief
								var bNext = a.beliefUpdate(b, _a, _o);

								g.addEdge(b, e, bNext);
							}
						});
				});
		}

		return g;
	}
}
