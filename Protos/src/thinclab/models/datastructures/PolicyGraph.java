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
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.utils.Jsonable;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class PolicyGraph implements Jsonable {

    final public ConcurrentHashMap<Integer, PolicyNode> nodeMap;
    final public ConcurrentHashMap<Tuple<Integer, List<Integer>>, Integer> edgeMap;
    final public ConcurrentHashMap<List<Integer>, List<String>> edgeLabelMap;
    public ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> adjMap = new ConcurrentHashMap<>();

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(PolicyGraph.class);

    public PolicyGraph(PBVISolvablePOMDPBasedModel m, AlphaVectorPolicy p) {

        // First create the action-observation space
        var obsVars = m.i_Om().stream()
            .map(i -> 
                    IntStream.range(1, Global.valNames.get(i - 1).size() + 1)
                    .mapToObj(j -> j)
                    .collect(Collectors.toList()))
            .collect(Collectors.toList());

        var oSpace = DDOP.cartesianProd(obsVars);
        var aoSpace = 
            IntStream.range(0, m.A().size()).mapToObj(i -> i)
            .flatMap(i -> oSpace.stream().map(o -> Tuple.of(i, o)))
            .collect(Collectors.toList());

        // populate the edge map with action-observation values
        edgeMap = new ConcurrentHashMap<>();
        edgeLabelMap = new ConcurrentHashMap<>();
        aoSpace.forEach(ao ->
                {

                    edgeMap.put(ao, edgeMap.size());

                    var obs = ao._1();
                    var namedObs = IntStream.range(0, obs.size()).boxed().map(i ->
                            {

                                var name = Global.varNames.get(m.i_Om.get(i) - 1);
                                var obsName = Global.valNames.get(m.i_Om.get(i) - 1).get(obs.get(i) - 1);

                                return Tuple.of(name, obsName).toString();
                            }).collect(Collectors.toList());

                    if (!edgeLabelMap.containsKey(obs))
                        edgeLabelMap.put(obs, namedObs);
                });

        // populate policy nodes
        nodeMap = new ConcurrentHashMap<>(p.aVecs.size());

        IntStream.range(0, p.aVecs.size()).forEach(i ->
                {

                    int actId = p.aVecs.get(i)._0();
                    nodeMap.put(i, new PolicyNode(i, actId, m.A().get(actId)));
                });
    }

    public void approximate() {

        LOGGER.warn("[!] Creating an approximation of an incomplete graph.");
        LOGGER.warn("[!] This will only be accurate to a limited horizon.");

        var terminals = adjMap.keySet().parallelStream().flatMap(s ->
                {

                    var leaves = adjMap.get(s).entrySet().stream().filter(e -> !adjMap.containsKey(e.getValue()))
                        .map(e -> e.getValue()).collect(Collectors.toSet());

                    return leaves.stream();

                }).collect(Collectors.toList());

        terminals.forEach(t ->
                {

                    adjMap.put(t, new ConcurrentHashMap<>());
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
        var edgeJson = new JsonObject();

        for (var node : adjMap.keySet()) {

            nodeJson.add(node.toString(), nodeMap.get(node).toJson());

            var thisEdgeJson = new JsonObject();
            for (var edge : edgeMap.entrySet()) {

                if (adjMap.get(node).containsKey(edge.getValue())) {

                    var _node = Tuple.of(nodeMap.get(adjMap.get(node).get(edge.getValue())).actName,
                            nodeMap.get(adjMap.get(node).get(edge.getValue())).alphaId);

                    thisEdgeJson.add(edgeLabelMap.get(edge.getKey()._1()).toString(),
                            gson.toJsonTree(_node.toString()));
                }
            }

            edgeJson.add(Tuple.of(nodeMap.get(node).actName, nodeMap.get(node).alphaId).toString(), thisEdgeJson);
        }

        json.add("nodes", gson.toJsonTree(nodeJson));
        json.add("edges", edgeJson);

        return json;
    }

    public static Tuple<PolicyGraph, List<DD>> expandPolicyGraphDFS(
            List<DD> b_is, PolicyGraph G, PBVISolvablePOMDPBasedModel m, 
            AlphaVectorPolicy p) {

        if (b_is.size() == 0)
            return Tuple.of(G, b_is);

        DD b = b_is.remove(0);
        if (G.adjMap.containsKey(DDOP.bestAlphaIndex(p.aVecs, b, m.i_S())))
            return expandPolicyGraphDFS(b_is, G, m, p);

        // get start policy node
        int alphaId = DDOP.bestAlphaIndex(p.aVecs, b, m.i_S());
        int bestAct = p.aVecs.get(alphaId)._0();

        // create tuples (obs, alpha vector, next belief)
        var p_n = G.edgeMap.entrySet().parallelStream()
            .filter(ao -> ao.getKey()._0() == bestAct)
            .map(ao -> Tuple.of(
                        ao.getValue(), 
                        m.beliefUpdate(b, bestAct, ao.getKey()._1())))
            .filter(b_n -> !b_n._1().equals(DD.zero))
            .map(b_n -> Tuple.of(
                        b_n._0(), 
                        DDOP.bestAlphaIndex(
                            p.aVecs, b_n._1(), m.i_S()),
                        b_n._1()))
            .collect(Collectors.toList());

        var map = new ConcurrentHashMap<Integer, Integer>(10);
        var b_ns = new ArrayList<DD>(10);

        // add all (obs, alpha vector) pairs to create map
        // alpha vector -> obs -> alpha vector
        for (var _p_n : p_n) {

            map.put(_p_n._0(), _p_n._1());
            b_ns.add(_p_n._2());
        }

        G.adjMap.put(alphaId, map);
        b_is.addAll(b_ns);

        return Tuple.of(G, b_is);
    }

    public static Tuple<PolicyGraph, List<DD>> expandPolicyGraph(
            List<DD> b_is, PolicyGraph G, PBVISolvablePOMDPBasedModel m, 
            AlphaVectorPolicy p) {

        LOGGER.debug(String.format("Graph contains %s nodes. Expanding from %s beliefs", G.adjMap.size(), b_is.size()));

        // here, we transform each belief b to 
        // (next policy subgraph, next beliefs)
        var nextNodes = b_is.parallelStream()
            .filter(b -> !G.adjMap.containsKey(
                        DDOP.bestAlphaIndex(p.aVecs, b, m.i_S())))
            .map(b ->
                    {

                        // get start policy node
                        int alphaId = DDOP.bestAlphaIndex(p.aVecs, b, m.i_S());
                        int bestAct = p.aVecs.get(alphaId)._0();

                        // create tuples (obs, alpha vector, next belief)
                        var p_n = G.edgeMap.entrySet().parallelStream().filter(ao -> ao.getKey()._0() == bestAct)
                            .map(ao -> Tuple.of(ao.getValue(), m.beliefUpdate(b, bestAct, ao.getKey()._1())))
                            .filter(b_n -> !b_n._1().equals(DD.zero))
                            .map(b_n -> Tuple.of(b_n._0(), DDOP.bestAlphaIndex(p.aVecs, b_n._1(), m.i_S()),
                                        b_n._1()))
                            .filter(b_n -> !G.adjMap.containsKey(b_n._1())).collect(Collectors.toList());

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

    public static PolicyGraph getPolicyGraphFromModel(final List<DD> b_i,
            PBVISolvablePOMDPBasedModel m) {

        var policy = 
            new SymbolicPerseusSolver<>().solve(
                    b_i, m, 100, 10, AlphaVectorPolicy.fromR(m.R));

        return PolicyGraph.makePolicyGraph(b_i, m, policy);
    }

    public static PolicyGraph makePolicyGraph(final List<DD> b_is, PBVISolvablePOMDPBasedModel m, AlphaVectorPolicy p) {

        // Make empty policy graph
        var G = new PolicyGraph(m, p);
        var _b_is = new ArrayList<DD>(b_is);
        LOGGER.debug(String.format("Initialized empty policy graph. Starting expansion from %s beliefs", b_is.size()));

        var nextState = Tuple.of(G, (List<DD>) _b_is);

        while (nextState._1().size() > 0) {

            nextState = expandPolicyGraphDFS(nextState._1(), nextState._0(), m, p);

            if (nextState._1().size() > 1000) {

                LOGGER.warn("Belief explosion while making policy graph");
                LOGGER.info("Stopping Policy Graph construction");
                break;
            }
        }

        if (nextState._1().size() > 0) {

            LOGGER.warn("Graph construction was terminated prematurely. The partial graph is shown below");
            G.approximate();
        }

        LOGGER.info("Policy Graph for %s has %s nodes", 
                m.getName(), nextState._0().adjMap.size());

        // mark start nodes
        LOGGER.debug("Marking start nodes for %s beliefs", b_is.size());
        b_is.forEach(_b -> {

            int id = DDOP.bestAlphaIndex(p.aVecs, _b, m.i_S());
            var _n = G.nodeMap.get(id);
            G.nodeMap.put(id, 
                    new PolicyNode(_n.alphaId, _n.actId, _n.actName, true));
        });

        return G;
    }

}
