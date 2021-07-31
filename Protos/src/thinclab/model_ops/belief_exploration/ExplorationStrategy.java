/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import thinclab.models.BeliefBasedModel;
import thinclab.models.datastructures.ActionObservationGraph;

/*
 * @author adityas
 *
 */
public interface ExplorationStrategy<A extends BeliefBasedModel<?>, G extends ActionObservationGraph<?, ?>> {

	public G expandRG(A a, G RG);
}
