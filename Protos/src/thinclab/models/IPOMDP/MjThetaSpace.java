/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.IPOMDP;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.PolicyGraph;
import thinclab.models.datastructures.PolicyNode;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;

/*
 * @author adityas
 *
 */
public class MjThetaSpace implements Frame<PolicyNode> {

    final public int frame;
    final public PolicyGraph G;
    final public PBVISolvablePOMDPBasedModel m;
    final public SymbolicPerseusSolver<PBVISolvablePOMDPBasedModel> s;
    final public AlphaVectorPolicy Vn;

    final private static Logger LOGGER = 
        LogManager.getFormatterLogger(MjThetaSpace.class);

    public MjThetaSpace(List<DD> b_j, int frame, 
            PBVISolvablePOMDPBasedModel m) {

        this.frame = frame;
        this.m = m;

        this.s = new SymbolicPerseusSolver<>();

        var b_js = new ArrayList<DD>();

        if (m instanceof IPOMDP)
            b_js.addAll(b_j.stream()
                    .map(d -> ((IPOMDP) m).getECDDFromMjDD(d))
                    .collect(Collectors.toList()));

        else
            b_js.addAll(b_j);

        this.Vn = s.solve(b_js, m, 100, 10, AlphaVectorPolicy.fromR(m.R()));

        this.G = PolicyGraph.makePolicyGraph(b_js, m, Vn);

        LOGGER.debug(
                "Graph has %s nodes and %s node sources", 
                G.nodeMap.size(), G.adjMap.size());
        LOGGER.info(
                "MjTheta space for frame %s initialized with %s EQ classes",
                frame, G.adjMap.size());

        if (Global.RESULTS_DIR != null) {
            try {
                Files.writeString(
                        Paths.get(
                            String.format("%s/%s_%s_pol_graph.json",
                                Global.RESULTS_DIR
                                .toAbsolutePath().toString(),
                                G.hashCode(), m.getName())),
                        G.toString());
            }

            catch (Exception e) {
                LOGGER.error("Got error while writing policy graph to file: %s", e);
            }
        }
    }

    @Override
    public List<MjRepr<PolicyNode>> allModels() {

        return G.adjMap.keySet().stream()
            .map(n -> new MjRepr<>(frame, G.nodeMap.get(n)))
            .collect(Collectors.toList());
    }

    @Override
    public List<MjRepr<PolicyNode>> bMj() {

        return null;
    }

    @Override
    public List<Tuple3<MjRepr<PolicyNode>, List<Integer>, MjRepr<PolicyNode>>> getTriples() {

        var triples = G.adjMap.entrySet().stream()
            .flatMap(e -> G.edgeMap.entrySet().stream().map(f ->
                        {

                            // for each edge, make a list of indices of child vals
                            var edge = new ArrayList<Integer>(
                                    f.getKey()._1().size() + 1);
                            edge.add(f.getKey()._0() + 1);
                            edge.addAll(f.getKey()._1());

                            var mj_p = e.getValue().get(f.getValue());

                            // if it is a leaf node, loop it back
                            if (mj_p == null)
                                return Tuple.of(
                                        new MjRepr<>(frame, 
                                            G.nodeMap.get(e.getKey())), 
                                        (List<Integer>) edge,
                                        new MjRepr<>(frame, 
                                            G.nodeMap.get(e.getKey())));

                            else
                                return Tuple.of(
                                        new MjRepr<>(frame, 
                                            G.nodeMap.get(e.getKey())), 
                                        (List<Integer>) edge,
                                        new MjRepr<>(frame, 
                                            G.nodeMap.get(mj_p)));

                        })).collect(Collectors.toList());

        return triples;
    }

}
