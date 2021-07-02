/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops;

import java.util.Set;

/*
 * @author adityas
 *
 */
public interface ActionObservationGenericGraph<S> {
	
	public Set<S> getNodes();
	public void insertNode(S node);

}
