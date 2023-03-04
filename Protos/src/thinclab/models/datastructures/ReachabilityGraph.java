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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
public class ReachabilityGraph extends 
AbstractAOGraph<DD, Integer, List<Integer>> {

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(ReachabilityGraph.class);

    private int cacheHits = 0;
    private int cacheMisses = 0;

    public ReachabilityGraph(final List<Tuple<Integer,
            List<Integer>>> AOSpace) {

        // Build (action, observation) keys for the edgeIndexMap
        for (var i: AOSpace)
            edgeIndexMap.put(i, edgeIndexMap.size());

        LOGGER.info(
                "Initialized reachability graph for branching factor %s", 
                AOSpace.size());
    }

    public void recordHit() {
        cacheHits += 1;
    }

    public void recordMiss() {
        cacheMisses += 1;
    }

    public void printCachingStats() {
        LOGGER.info("%s hits and %s misses", cacheHits, cacheMisses);
        LOGGER.info("hit fraction %s", 
                ((float) cacheHits / (float) (cacheHits + cacheMisses)));
    }

    public static ReachabilityGraph 
        fromDecMakingModel(POSeqDecMakingModel<?> m) {

            // Make action observation space for agent I
            var obsVars = m.i_Om().stream()
                .map(i -> IntStream.range(
                            1, Global.valNames.get(i - 1).size() + 1)
                        .mapToObj(j -> j)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

            var oSpace = DDOP.cartesianProd(obsVars);
            var aoSpace = IntStream.range(0, m.A().size())
                .mapToObj(i -> i)
                .flatMap(i -> oSpace.stream().map(o -> Tuple.of(i, o)))
                .collect(Collectors.toList());

            return new ReachabilityGraph(aoSpace);
        }

    public List<Tuple3<DD, List<Integer>, DD>> getTriples() {

        var triples = connections.entrySet().stream()
            .flatMap(e -> edgeIndexMap.entrySet().stream()
                    .map(f ->
                        {

                            // for each edge, make a list of indices of child vals
                            var edge = new ArrayList<Integer>(f.getKey()._1().size() + 1);
                            edge.add(f.getKey()._0() + 1);
                            edge.addAll(f.getKey()._1());

                            var mj_p = e.getValue().get(f.getValue());

                            // if it is a leaf node, loop it back
                            if (mj_p == null)
                                return Tuple.of(
                                        revNodeIndex.get(e.getKey()), 
                                        (List<Integer>) edge, 
                                        revNodeIndex.get(e.getKey()));

                            else
                                return Tuple.of(
                                        revNodeIndex.get(e.getKey()), 
                                        (List<Integer>) edge, 
                                        revNodeIndex.get(mj_p));
                        })).collect(Collectors.toList());

        // LOGGER.debug(String.format("Triples are: %s", triples));
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

}
