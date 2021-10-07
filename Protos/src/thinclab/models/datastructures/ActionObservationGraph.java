/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.datastructures;

import java.util.Collection;
import java.util.Set;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public interface ActionObservationGraph<N, A, O> {

	public boolean addNode(N node);

	public void addEdge(N parent, Tuple<A, O> edge, N child);

	public N getNodeAtEdge(N parent, Tuple<A, O> edge);

	public Set<N> getParents();

	public Set<N> getChildren(Collection<N> parents);

	public Set<N> getAllChildren();

	public Set<N> getAllNodes();

	public void removeNode(N node);

	public void replaceNode(N fNode, N tNode);

	public void removeAllNodes();

}
