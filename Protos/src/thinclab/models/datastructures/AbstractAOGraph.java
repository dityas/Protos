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

	public ConcurrentHashMap<N, Integer> nodeIndexMap = new ConcurrentHashMap<>(10);
	public ConcurrentHashMap<Integer, N> revNodeIndex = new ConcurrentHashMap<>(10);
	public ConcurrentHashMap<Tuple<A, O>, Integer> edgeIndexMap = new ConcurrentHashMap<>(10);;
	public ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> connections = new ConcurrentHashMap<>(10);
	
	public int nodeCounter = 0;

    public int getNodeCount() {
        return nodeIndexMap.size();
    }

	@Override
	public boolean addNode(N node) {

		if (nodeIndexMap.containsKey(node))
			return false;

		else {

			nodeIndexMap.put(node, nodeCounter);
			revNodeIndex.put(nodeCounter, node);
			
			nodeCounter += 1;

			connections.put(nodeIndexMap.get(node), new ConcurrentHashMap<>());

			return true;
		}
	}

	@Override
	public void addEdge(N parent, Tuple<A, O> edge, N child) {

		//if (this.addNode(parent) || this.connections.get(parent) == null)
		//	this.connections.replace(parent, new ConcurrentHashMap<>(this.edgeIndexMap.size()));

		addNode(parent);
		addNode(child);
		connections.get(nodeIndexMap.get(parent)).put(edgeIndexMap.get(edge), nodeIndexMap.get(child));
	}

	@Override
	public N getNodeAtEdge(N parent, Tuple<A, O> edge) {

		if (!nodeIndexMap.containsKey(parent) || connections.get(nodeIndexMap.get(parent)).get(edgeIndexMap.get(edge)) == null
				|| !(edgeIndexMap.containsKey(edge)))
			return null;

		else {
			return revNodeIndex.get(connections.get(nodeIndexMap.get(parent)).get(edgeIndexMap.get(edge)));
		}
	}

	@Override
	public Set<N> getParents() {

		return connections.keySet().stream().filter(b -> connections.get(b).size() > 0).map(b -> revNodeIndex.get(b))
				.collect(Collectors.toSet());
	}

	@Override
	public Set<N> getChildren(Collection<N> parents) {

		return parents.stream()
				.flatMap(p -> connections.containsKey(nodeIndexMap.get(p))
						? connections.get(p).values().stream().map(c -> revNodeIndex.get(c))
						: new HashSet<N>(1).stream())
				.collect(Collectors.toSet());
	}

	@Override
	public Set<N> getAllChildren() {

		return connections.keySet().stream()
				.filter(n -> connections.get(n) == null || this.connections.get(n).size() == 0).map(n -> revNodeIndex.get(n))
				.collect(Collectors.toSet());
	}

	@Override
	public Set<N> getAllNodes() {

		return nodeIndexMap.keySet();
	}

	@Override
	public void removeNode(N node) {

		int key = nodeIndexMap.remove(node);
		revNodeIndex.remove(key);
		connections.remove(key);
	}

	@Override
	public void replaceNode(N fNode, N tNode) {

		// first replace any child nodes from fNode to tNode
		var val = connections.remove(nodeIndexMap.get(fNode));
		connections.put(nodeIndexMap.get(tNode), val);

		// then the difficult part of changing fNode's parents to tNode's parents
		var keys = connections.keySet();
		keys.forEach(k ->
			{

				// for each src node, get all dests
				var dest = connections.get(k);

				// in all dests find one which is fNode and replace that
				var toReplace = dest.entrySet().stream().filter(e -> e.getValue() == nodeIndexMap.get(fNode)).map(e -> e.getKey())
						.collect(Collectors.toList());

				toReplace.forEach(_k -> dest.remove(_k));
				toReplace.forEach(_k -> dest.put(_k, nodeIndexMap.get(tNode)));

				connections.remove(k);
				connections.put(k, dest);
			});
	}

	@Override
	public void removeAllNodes() {

		nodeCounter = 0;
		nodeIndexMap.clear();
		revNodeIndex.clear();
		connections.clear();
	}

}
