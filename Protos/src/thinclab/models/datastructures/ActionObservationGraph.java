/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.datastructures;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/*
 * @author adityas
 *
 */
public interface ActionObservationGraph<N, E extends List<?>> {

	public boolean addNode(N node);

	public void addEdge(N parent, E edge, N child);

	public Optional<N> getNodeAtEdge(N parent, E edge);

	public Set<N> getParents();

	public Set<N> getChildren();

	public Set<N> getAllNodes();

}
