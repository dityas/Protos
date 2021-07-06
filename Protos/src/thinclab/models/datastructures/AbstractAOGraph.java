/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.datastructures;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/*
 * @author adityas
 *
 */
public abstract class AbstractAOGraph<N, E> implements ActionObservationGraph<N, E> {

	public ConcurrentHashMap<E, Integer> edgeIndexMap;
	public ConcurrentHashMap<N, ConcurrentHashMap<Integer, N>> connections;

	@Override
	public boolean addNode(N node) {

		if (this.connections.containsKey(node))
			return false;

		else {

			this.connections.put(node, new ConcurrentHashMap<>());
			return true;
		}
	}

	@Override
	public void addEdge(N parent, E edge, N child) {

		if (this.addNode(parent) || this.connections.get(parent) == null)
			this.connections.replace(parent, new ConcurrentHashMap<>(this.edgeIndexMap.size()));

		this.addNode(child);
		this.connections.get(parent).put(this.edgeIndexMap.get(edge), child);
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
	public Set<N> getParents() {

		return this.connections.keySet().stream().filter(b -> this.connections.get(b).size() > 0)
				.collect(Collectors.toSet());
	}

	@Override
	public Set<N> getChildren() {

		return this.connections.keySet().stream().filter(b -> this.connections.get(b).size() == 0)
				.collect(Collectors.toSet());
	}

	@Override
	public Set<N> getAllNodes() {

		return this.connections.keySet();
	}

}
