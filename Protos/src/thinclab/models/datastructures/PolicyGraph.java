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
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class PolicyGraph {

	final public ConcurrentHashMap<Tuple<Integer, List<Integer>>, Integer> edgeMap;
	public ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> adjMap = new ConcurrentHashMap<>();

	private static final Logger LOGGER = LogManager.getLogger(PolicyGraph.class);

	public PolicyGraph(List<Tuple<Integer, List<Integer>>> aoSpace) {

		edgeMap = new ConcurrentHashMap<>();
		aoSpace.forEach(ao -> edgeMap.put(ao, edgeMap.size()));
	}
	
	@Override
	public String toString() {
		return adjMap.toString();
	}

	public static Tuple<PolicyGraph, List<DD>> expandPolicyGraph(List<DD> b_is, PolicyGraph G,
			PBVISolvablePOMDPBasedModel m, AlphaVectorPolicy p) {

		LOGGER.debug(String.format("Expanding graph from %s beliefs", b_is.size()));

		var nextNodes = b_is.parallelStream().map(b ->
			{

				// get start policy node
				int alphaId = DDOP.bestAlphaIndex(p.aVecs, b, m.i_S());
				int bestAct = p.aVecs.get(alphaId)._0();

				// create tuples (obs, next belief, alpha vector)
				var p_n = G.edgeMap.entrySet().parallelStream().filter(ao -> ao.getKey()._0() == bestAct)
						.map(ao -> Tuple.of(ao.getValue(), m.beliefUpdate(b, bestAct, ao.getKey()._1())))
						.filter(b_n -> !b_n._1().equals(DD.zero))
						.map(b_n -> Tuple.of(b_n._0(), DDOP.bestAlphaIndex(p.aVecs, b_n._1(), m.i_S()), b_n._1()))
						.collect(Collectors.toList());

				var map = new ConcurrentHashMap<Integer, Integer>(10);
				var b_ns = new ArrayList<DD>(10);

				for (var _p_n : p_n) {

					map.put(_p_n._0(), _p_n._1());
					b_ns.add(_p_n._2());
				}

				return Tuple.of(alphaId, map, b_ns);
			}).filter(_p -> !G.adjMap.containsKey(_p._0())).collect(Collectors.toList());

		var nextBels = new ArrayList<DD>();

		for (var nextNode : nextNodes) {

			G.adjMap.put(nextNode._0(), nextNode._1());
			nextBels.addAll(nextNode._2());
		}

		return Tuple.of(G, nextBels);
	}

	public static PolicyGraph makePolicyGraph(List<DD> b_is, PBVISolvablePOMDPBasedModel m, AlphaVectorPolicy p) {

		// First create the action-observation space
		var obsVars = m.i_Om().stream().map(i -> IntStream.range(1, Global.valNames.get(i - 1).size() + 1)
				.mapToObj(j -> j).collect(Collectors.toList())).collect(Collectors.toList());

		var oSpace = DDOP.cartesianProd(obsVars);
		var aoSpace = IntStream.range(0, m.A().size()).mapToObj(i -> i)
				.flatMap(i -> oSpace.stream().map(o -> Tuple.of(i, o))).collect(Collectors.toList());

		// Make empty policy graph
		var G = new PolicyGraph(aoSpace);
		LOGGER.debug(String.format("Initialized empty policy graph. Starting expansion from %s beliefs", b_is.size()));

		var nextState = expandPolicyGraph(b_is, G, m, p);
		LOGGER.debug(String.format("Next state is %s", nextState));
		
		return G;
	}
	
}
