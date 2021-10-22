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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.Global;
import thinclab.models.POSeqDecMakingModel;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;

/*
 * @author adityas
 *
 */
public class ModelGraph<N extends ReachabilityNode> extends AbstractAOGraph<N, Integer, List<Integer>> {

	private static final Logger LOGGER = LogManager.getLogger(ModelGraph.class);

	public ModelGraph(final List<Tuple<Integer, List<Integer>>> AOSpace) {

		this.connections = new ConcurrentHashMap<>(10);
		this.edgeIndexMap = new ConcurrentHashMap<>(10);

		AOSpace.stream().forEach(i -> this.edgeIndexMap.put(i, this.edgeIndexMap.size()));
		LOGGER.info(String.format("Initialized policy graph for branching factor %s", AOSpace.size()));
	}
	
	public static ModelGraph<ReachabilityNode> fromDecMakingModel(POSeqDecMakingModel<?> m) {

		// Make action observation space for agent I
		var obsVars = m.i_Om().stream().map(i -> IntStream.range(1, Global.valNames.get(i - 1).size() + 1)
				.mapToObj(j -> j).collect(Collectors.toList())).collect(Collectors.toList());

		var oSpace = DDOP.cartesianProd(obsVars);
		var aoSpace = IntStream.range(0, m.A().size()).mapToObj(i -> i)
				.flatMap(i -> oSpace.stream().map(o -> Tuple.of(i, o))).collect(Collectors.toList());

		return new ModelGraph<>(aoSpace);
	}

	public List<Tuple3<N, List<Integer>, N>> getTriples() {

		var triples = this.connections.entrySet().stream().flatMap(e -> this.edgeIndexMap.entrySet().stream().map(f ->
			{

				// for each edge, make a list of indices of child vals
				var edge = new ArrayList<Integer>(f.getKey()._1().size() + 1);
				edge.add(f.getKey()._0() + 1);
				edge.addAll(f.getKey()._1());

				var mj_p = e.getValue().get(f.getValue());

				// if it is a leaf node, loop it back
				if (mj_p == null)
					return Tuple.of(e.getKey(), (List<Integer>) edge, e.getKey());

				else
					return Tuple.of(e.getKey(), (List<Integer>) edge, mj_p);
			})).collect(Collectors.toList());

		// LOGGER.debug(String.format("Triples are: %s", triples));
		return triples;
	}
	
	@Override
	public String toString() {

		var builder = new StringBuilder();
		builder.append("Model Graph: [").append("\r\n").append("edges: {\r\n");

		this.edgeIndexMap.entrySet().stream().forEach(e ->
			{

				builder.append("\t").append(e.getKey()).append(" -> ").append(e.getValue()).append("\r\n");
			});

		builder.append("}\r\n");
		builder.append("nodes: {\r\n");

		this.connections.entrySet().stream().forEach(e ->
			{

				builder.append("\t").append(e.getKey()).append(" -> ").append(e.getValue()).append("\r\n");
			});

		builder.append("}\r\n]");

		return builder.toString();
	}

	public static String toDot(ModelGraph<ReachabilityNode> G, POSeqDecMakingModel<?> m) {

		var nodeMap = new HashMap<ReachabilityNode, Integer>(G.connections.size());

		var builder = new StringBuilder();
		builder.append("digraph D {").append("\r\n").append("\t node [shape=Mrecord];\r\n");

		for (var _n : G.connections.keySet()) {

			nodeMap.put(_n, nodeMap.size());
			
			builder.append(nodeMap.get(_n)).append(" [label=\"{ ");

			// if (_n.alphaId < 0)
			builder.append(" ")
					.append(String.join(" | --- | ", _n.beliefs.stream().map(b -> DDOP.toDotRecord(b, m.i_S())).collect(Collectors.toList())))
					.append(" ")
					.append("| --- | A=");

			if (_n.i_a >= 0)
				builder.append(m.A().get(_n.i_a));
			
			builder.append(" | alpha= ").append(_n.alphaId).append(" | ");

			if (_n.h >= 0)
				builder.append(" t= ").append(_n.h).append(" }");

			builder.append("\"]\r\n");
		}

		builder.append("\r\n");

		for (var _n : G.connections.keySet()) {

			for (var edge : G.edgeIndexMap.keySet()) {

				if (G.connections.get(_n).get(G.edgeIndexMap.get(edge)) != null) {

					builder.append(nodeMap.get(_n)).append(" -> ")
							.append(nodeMap.get(G.connections.get(_n).get(G.edgeIndexMap.get(edge))))
							.append(" [label=\" ")
							.append(IntStream.range(0, m.i_Om().size())
									.mapToObj(i -> Global.valNames.get(m.i_Om().get(i) - 1).get(edge._1().get(i) - 1))
									.collect(Collectors.toList()))
							.append("\"]\r\n");
				}
			}

		}

		builder.append("}\r\n");
		return builder.toString();
	}

}
