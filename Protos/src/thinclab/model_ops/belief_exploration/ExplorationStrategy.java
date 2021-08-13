/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import thinclab.models.POSeqDecMakingModel;
import thinclab.models.datastructures.AbstractAOGraph;
import thinclab.policy.Policy;

/*
 * @author adityas
 *
 */
public interface ExplorationStrategy<B, A extends POSeqDecMakingModel<B>, G extends AbstractAOGraph<?, ?, ?>, P extends Policy<B>> {

	public G expand(G g, A a, int T, P Vn);
}
