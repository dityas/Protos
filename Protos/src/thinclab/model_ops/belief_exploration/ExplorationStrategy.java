/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import java.util.List;
import thinclab.models.POSeqDecMakingModel;
import thinclab.models.datastructures.AbstractAOGraph;
import thinclab.models.datastructures.ActionObservationGraph;

/*
 * @author adityas
 *
 */
public interface ExplorationStrategy<B, A extends POSeqDecMakingModel<B>, G extends AbstractAOGraph<B, List<?>>> {

	public List<B> expand(List<B> bs, A a);
}
