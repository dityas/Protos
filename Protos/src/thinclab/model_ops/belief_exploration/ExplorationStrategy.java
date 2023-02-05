/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import java.util.List;

import thinclab.legacy.DD;
import thinclab.models.POSeqDecMakingModel;
import thinclab.models.datastructures.AbstractAOGraph;
import thinclab.policy.AlphaVectorPolicy;

/*
 * @author adityas
 *
 */
public interface ExplorationStrategy<A extends POSeqDecMakingModel<DD>, G extends AbstractAOGraph<?, ?, ?>> {

	public void expand(List<DD> bs, G g, A a, int T, AlphaVectorPolicy Vn);
	public int getMaxB();
}
