/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.datastructures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.models.POSeqDecMakingModel;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;

/*
 * @author adityas
 *
 */
public class ReachabilityGraph extends AbstractAOGraph<DD, Integer, List<Integer>> {

	private static final Logger LOGGER = LogManager.getLogger(ReachabilityGraph.class);

	public ReachabilityGraph(final List<Tuple<Integer, List<Integer>>> AOSpace) {

		this.connections = new ConcurrentHashMap<>(10);
		this.edgeIndexMap = new ConcurrentHashMap<>(10);

		AOSpace.stream().forEach(i -> this.edgeIndexMap.put(i, this.edgeIndexMap.size()));
		LOGGER.info(String.format("Initialized reachability graph for branching factor %s", AOSpace.size()));
	}

	public static ReachabilityGraph fromDecMakingModel(POSeqDecMakingModel<?> m) {

		// Make action observation space for agent I
		var obsVars = m.i_Om().stream().map(i -> IntStream.range(1, Global.valNames.get(i - 1).size() + 1)
				.mapToObj(j -> j).collect(Collectors.toList())).collect(Collectors.toList());

		var oSpace = DDOP.cartesianProd(obsVars);
		var aoSpace = IntStream.range(0, m.A().size()).mapToObj(i -> i)
				.flatMap(i -> oSpace.stream().map(o -> Tuple.of(i, o))).collect(Collectors.toList());

		return new ReachabilityGraph(aoSpace);
	}

	public List<Tuple3<DD, List<Integer>, DD>> getTriples() {

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

		//LOGGER.debug(String.format("Triples are: %s", triples));
		return triples;
	}

	@Override
	public String toString() {

		var builder = new StringBuilder();
		builder.append("Reachability Graph: [").append("\r\n").append("edges: {\r\n");

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

	public String toDot(POSeqDecMakingModel<?> m) {

		var nodeMap = new HashedMap<DD, Integer>(connections.size());

		var builder = new StringBuilder();
		builder.append("digraph D {").append("\r\n").append("\t node [shape=record];\r\n");

		for (var dd : connections.keySet()) {

			nodeMap.put(dd, nodeMap.size());
			builder.append(nodeMap.get(dd)).append(" [label=\"").append(DDOP.factors(dd, m.i_S())).append("\"]\r\n");
		}

		builder.append("\r\n");

		for (var dd : connections.keySet()) {

			for (var edge : edgeIndexMap.keySet()) {

				builder.append(nodeMap.get(dd)).append(" -> ")
						.append(nodeMap.get(connections.get(dd).get(edgeIndexMap.get(edge)))).append(" [label=\" ")
						.append(m.A().get(edge._0())).append(" ").append(edge._1()).append("\"]\r\n");
			}

		}

		builder.append("}\r\n]");
		return builder.toString();

	}

}
