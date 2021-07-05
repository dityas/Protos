/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/*
 * @author adityas
 *
 */
public abstract class AbstractAOGraph<N, E> implements ActionObservationGraph<N, E> {

	public HashMap<E, Integer> edgeIndexMap;
	public HashMap<N, List<N>> connections;

	@Override
	public boolean addNode(N node) {

		if (this.connections.containsKey(node))
			return false;

		else {

			this.connections.put(node, null);
			return true;
		}
	}

	@Override
	public void addEdge(N parent, E edge, N child) {

		if (this.addNode(parent) || this.connections.get(parent) == null)
			this.connections.replace(parent, new ArrayList<>(this.edgeIndexMap.size()));

		this.connections.get(parent).add(this.edgeIndexMap.get(edge), child);
	}

	@Override
	public Optional<N> getNodeAtEdge(N parent, E edge) {

		if (!this.connections.containsKey(parent) || this.connections.get(parent) == null
				|| !(this.edgeIndexMap.containsKey(edge)))
			return Optional.empty();

		else
			return Optional.ofNullable(this.connections.get(parent).get(this.edgeIndexMap.get(edge)));
	}

	@Override
	public Set<N> getNodeSet() {

		return this.connections.keySet();
	}
}
