/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.datastructures;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public abstract class AbstractAOGraph<N, A, O> implements ActionObservationGraph<N, A, O> {

	public ConcurrentHashMap<Tuple<A, O>, Integer> edgeIndexMap;
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
	public void addEdge(N parent, Tuple<A, O> edge, N child) {

		if (this.addNode(parent) || this.connections.get(parent) == null)
			this.connections.replace(parent, new ConcurrentHashMap<>(this.edgeIndexMap.size()));

		this.addNode(child);
		this.connections.get(parent).put(this.edgeIndexMap.get(edge), child);
	}

	@Override
	public N getNodeAtEdge(N parent, Tuple<A, O> edge) {

		if (!this.connections.containsKey(parent) || this.connections.get(parent) == null
				|| !(this.edgeIndexMap.containsKey(edge)))
			return null;

		else
			return this.connections.get(parent).get(this.edgeIndexMap.get(edge));
	}

	@Override
	public Set<N> getParents() {

		return this.connections.keySet().stream().filter(b -> this.connections.get(b).size() > 0)
				.collect(Collectors.toSet());
	}

	@Override
	public Set<N> getChildren(Collection<N> parents) {

		return parents.stream().flatMap(p -> this.connections.containsKey(p) ? this.connections.get(p).values().stream()
				: new HashSet<N>(1).stream()).collect(Collectors.toSet());
	}

	@Override
	public Set<N> getAllChildren() {

		return this.connections.keySet().stream()
				.filter(n -> this.connections.get(n) == null || this.connections.get(n).size() == 0)
				.collect(Collectors.toSet());
	}

	@Override
	public Set<N> getAllNodes() {

		return this.connections.keySet();
	}

	@Override
	public void removeNode(N node) {

		this.connections.remove(node);
	}

	@Override
	public void replaceNode(N fNode, N tNode) {

		// first replace any child nodes from fNode to tNode
		var val = this.connections.remove(fNode);
		this.connections.put(tNode, val);

		// then the difficult part of changing fNode's parents to tNode's parents
		var keys = this.connections.keySet();
		keys.forEach(k ->
			{

				// for each src node, get all dests
				var dest = this.connections.get(k);

				// in all dests find one which is fNode and replace that
				var toReplace = dest.entrySet().stream().filter(e -> e.getValue().equals(fNode)).map(e -> e.getKey())
						.collect(Collectors.toList());

				toReplace.forEach(_k -> dest.remove(_k));
				toReplace.forEach(_k -> dest.put(_k, tNode));

				this.connections.remove(k);
				this.connections.put(k, dest);
			});
	}

	@Override
	public void removeAllNodes() {

		this.connections.clear();
	}

}
