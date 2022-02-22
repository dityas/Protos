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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.utils.Jsonable;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class PolicyGraph implements Jsonable {

	final public ConcurrentHashMap<Integer, PolicyNode> nodeMap;
	final public ConcurrentHashMap<Tuple<Integer, List<Integer>>, Integer> edgeMap;
	public ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> adjMap = new ConcurrentHashMap<>();

	private static final Logger LOGGER = LogManager.getLogger(PolicyGraph.class);

	public PolicyGraph(PBVISolvablePOMDPBasedModel m, AlphaVectorPolicy p) {

		// First create the action-observation space
		var obsVars = m.i_Om().stream().map(i -> IntStream.range(1, Global.valNames.get(i - 1).size() + 1)
				.mapToObj(j -> j).collect(Collectors.toList())).collect(Collectors.toList());

		var oSpace = DDOP.cartesianProd(obsVars);
		var aoSpace = IntStream.range(0, m.A().size()).mapToObj(i -> i)
				.flatMap(i -> oSpace.stream().map(o -> Tuple.of(i, o))).collect(Collectors.toList());
	
		// populate the edge map with action-observation values
		edgeMap = new ConcurrentHashMap<>();
		aoSpace.forEach(ao -> edgeMap.put(ao, edgeMap.size()));
		
		// populate policy nodes
		nodeMap = new ConcurrentHashMap<>(p.aVecs.size());
		
		IntStream.range(0, p.aVecs.size()).forEach(i -> {
			
			int actId = p.aVecs.get(i)._0();
			nodeMap.put(i, new PolicyNode(i, actId, m.A().get(actId)));
		});
	}
	
	@Override
	public String toString() {
		
		var gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(toJson());
	}
	
	@Override
	public JsonObject toJson() {
		
		var gson = new GsonBuilder().setPrettyPrinting().create();
		var json = new JsonObject();
		
		var nodeJson = new JsonObject();
		for (var node : adjMap.keySet()) {
			
			nodeJson.add(node.toString(), nodeMap.get(node).toJson());
		}
		
		json.add("nodes", gson.toJsonTree(nodeJson));
		
		return json;
	}

	public static Tuple<PolicyGraph, List<DD>> expandPolicyGraph(List<DD> b_is, PolicyGraph G,
			PBVISolvablePOMDPBasedModel m, AlphaVectorPolicy p) {

		LOGGER.debug(String.format("Graph contains %s nodes. Expanding from %s beliefs", 
				G.adjMap.size(), b_is.size()));

		// here, we transform each belief b to (next policy subgraph, next beliefs)
		var nextNodes = b_is.parallelStream().map(b ->
			{

				// get start policy node
				int alphaId = DDOP.bestAlphaIndex(p.aVecs, b, m.i_S());
				int bestAct = p.aVecs.get(alphaId)._0();

				// create tuples (obs, alpha vector, next belief)
				var p_n = G.edgeMap.entrySet().parallelStream().filter(ao -> ao.getKey()._0() == bestAct)
						.map(ao -> Tuple.of(ao.getValue(), m.beliefUpdate(b, bestAct, ao.getKey()._1())))
						.filter(b_n -> !b_n._1().equals(DD.zero))
						.map(b_n -> Tuple.of(b_n._0(), DDOP.bestAlphaIndex(p.aVecs, b_n._1(), m.i_S()), b_n._1()))
						.collect(Collectors.toList());

				var map = new ConcurrentHashMap<Integer, Integer>(10);
				var b_ns = new ArrayList<DD>(10);

				// add all (obs, alpha vector) pairs to create map
				// alpha vector -> obs -> alpha vector
				for (var _p_n : p_n) {

					map.put(_p_n._0(), _p_n._1());
					b_ns.add(_p_n._2());
				}

				return Tuple.of(alphaId, map, b_ns);
			})
				// only take next policy subgraph if root does not already exist
				.filter(_p -> !G.adjMap.containsKey(_p._0())).collect(Collectors.toList());

		var nextBels = new ArrayList<DD>();

		// add new policy subgraph to policy graph and update list of all new beliefs
		for (var nextNode : nextNodes) {

			G.adjMap.put(nextNode._0(), nextNode._1());
			nextBels.addAll(nextNode._2());
		}

		return Tuple.of(G, nextBels);
	}

	public static PolicyGraph makePolicyGraph(List<DD> b_is, PBVISolvablePOMDPBasedModel m, AlphaVectorPolicy p) {

		// Make empty policy graph
		var G = new PolicyGraph(m, p);
		LOGGER.debug(String.format("Initialized empty policy graph. Starting expansion from %s beliefs", b_is.size()));

		var nextState = Tuple.of(G, b_is);
		
		while (nextState._1().size() > 0) {
			
			nextState = expandPolicyGraph(nextState._1(), nextState._0(), m, p);
			
			if (nextState._1().size() > 300)
				LOGGER.warn("Belief explosion while making policy graph");
		}
			
		LOGGER.info(String.format("Policy Graph for %s has %s nodes", 
				m.getName(), nextState._0().adjMap.size()));
		
		return G;
	}
	
}
