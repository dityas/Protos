
package thinclab.executables;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.models.datastructures.PolicyGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.spuddx_parser.SpuddXMainParser;
import thinclab.utils.Tuple;
import thinclab.utils.Utils;

public class SimulateInteraction {

    public static GsonBuilder JSON = null;
    public static JsonArray JSON_DATA = null; 

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(SimulateBeliefUpdates.class);

    public static JsonObject getVarValsJSON(
            Tuple<List<Integer>, List<Integer>> varVals) {

        var json = new JsonObject();
        var repr = IntStream.range(0, varVals._0().size())
            .mapToObj(i -> 
                    Tuple.of(
                        Global.varNames.get(varVals._0().get(i) - 1),
                        Global.valNames.get(varVals._0().get(i) - 1)
                        .get(varVals._1().get(i) - 1)))
            .collect(Collectors.toList());

        for(var o: repr)
            json.addProperty(o._0(), o._1());

        return json;
            }

    public static void printVarVals(
            Tuple<List<Integer>, List<Integer>> varVals) {

        var repr = IntStream.range(0, varVals._0().size())
            .mapToObj(i -> 
                    List.of(
                        Global.varNames.get(varVals._0().get(i) - 1),
                        Global.valNames.get(varVals._0().get(i) - 1)
                        .get(varVals._1().get(i) - 1)))
            .collect(Collectors.toList());

        System.out.println(repr);
            }

    public static void printBeliefStats(PBVISolvablePOMDPBasedModel m,
            DD b) {

        var b_factors = DDOP.factors(b, m.i_S());
        b_factors.forEach(_b -> {
            System.out.println(_b);
        });
    }

    public static void printBeliefStats(IPOMDP m, DD b) {

        var b_factors = DDOP.factors(b, m.i_S());
        var b_EC = b_factors.get(b_factors.size() - 1);

        // print belief
        b_factors.forEach(_b -> {
            System.out.println(_b);
        });

        // for IPOMDPs, print everything
        System.out.println(
                String.format(
                    "Belief over Aj's frame is %s", 
                    DDOP.getFrameBelief(
                        b, 
                        m.PThetajGivenEC,
                        m.i_EC, 
                        m.i_S())));
        System.out.println(
                String.format(
                    "Predicted actions: %s", 
                    DDOP.addMultVarElim(
                        List.of(m.PAjGivenEC, b_EC), 
                        List.of(m.i_EC))));
    }

    public static void runSimulator(
            final IPOMDP model,
            final PBVISolvablePOMDPBasedModel jModel,
            DD s,
            DD b_i,
            DD b_j,
            AlphaVectorPolicy p,
            AlphaVectorPolicy jPolicy,
            int length) {

        // prepare state and prime state indices for use later
        var X = new ArrayList<>(model.i_S());
        X.remove(X.size() - 1);
        var X_p = Global.makePrimeIndices(X);

        var JSON_ARRAY = Global.RESULTS_DIR == null ? null : new JsonArray();

        for (int iter = 0; iter < length; iter++) {

            var iAct = p.getBestActionIndex(b_i, model.i_S());
            var jAct = jPolicy.getBestActionIndex(b_j, jModel.i_S());
            var s_p = updateState(s, X, model.T().get(iAct), model.i_Aj, jAct);
            var iObs = sampleObservations(s_p, model.O().get(iAct), 
                    model.i_Om_p(), X_p, model.i_Aj, jAct);
            var jObs = jModel instanceof IPOMDP ? 
                sampleObservations(s_p, jModel.O().get(jAct), 
                        jModel.i_Om_p(), X_p, ((IPOMDP) jModel).i_Aj, iAct) :
                sampleObservations(s_p, jModel.O().get(jAct), 
                        jModel.i_Om_p(), X_p);

            // Write results
            if (Global.RESULTS_DIR != null) {

                var json = new JsonObject();

                json.addProperty("time step", iter);
                json.add("iBel", DDOP.toJson(b_i, model.i_S()));
                json.addProperty("iR", DDOP.dotProduct(
                            model.R().get(iAct), b_i, model.i_S()));

                if (model instanceof IPOMDP) {
                    var i_theta_j = DDOP.getFrameBelief(
                            b_i, 
                            model.PThetajGivenEC,
                            model.i_EC, 
                            model.i_S());

                    json.add(
                            "iThetaHat", 
                            DDOP.toJson(i_theta_j, model.i_Thetaj));

                    var PAj = 
                        DDOP.addMultVarElim(
                                List.of(b_i, model.PAjGivenEC), 
                                model.i_S());

                    json.add(
                            "i_P(Aj)",
                            DDOP.toJson(PAj, model.i_Aj));

                }

                json.add("iObs", getVarValsJSON(iObs));
                json.add("jBel", DDOP.toJson(b_j, jModel.i_S()));
                json.addProperty("jR", DDOP.dotProduct(
                            jModel.R().get(jAct), b_j, jModel.i_S()));

                if (jModel instanceof IPOMDP) {
                    var _j = (IPOMDP) jModel;
                    var j_theta_j = DDOP.getFrameBelief(
                            b_j, 
                            _j.PThetajGivenEC,
                            _j.i_EC, 
                            _j.i_S());

                    json.add(
                            "jThetaHat", 
                            DDOP.toJson(j_theta_j, _j.i_Thetaj));
                    
                    var PAj = 
                        DDOP.addMultVarElim(
                                List.of(b_j, _j.PAjGivenEC), 
                                _j.i_S());

                    json.add(
                            "j_P(Aj)",
                            DDOP.toJson(PAj, _j.i_Aj));

                }

                json.add("jObs", getVarValsJSON(jObs));
                json.add("state", DDOP.toJson(s, X));
                json.addProperty("iAct", model.A().get(iAct));
                json.addProperty("jAct", jModel.A().get(jAct));

                JSON_ARRAY.add(json);
            }

            b_i = model.beliefUpdate(b_i, iAct, iObs._1());
            b_j = jModel.beliefUpdate(b_j, jAct, jObs._1());
            s = s_p;
        }

        JSON_DATA.add(JSON_ARRAY);

            }

    public static DD ddFromVarVals(
            Tuple<List<Integer>, List<Integer>> varVals) {

        List<DD> _s = IntStream.range(0, varVals._0().size())
            .mapToObj(i -> DDnode.getDDForChild(
                        varVals._0().get(i), varVals._1().get(i) - 1))
            .collect(Collectors.toList());

        return DDOP.mult(_s);
            }

    public static DD updateState(
            DD s,
            List<Integer> i_S,
            List<DD> T,
            int i_Aj,
            int jAct) {

        var dds = new ArrayList<>(T);
        dds.add(s);

        var s_p = DDOP.addMultVarElim(dds, i_S);
        s_p = DDOP.restrict(s_p, List.of(i_Aj), List.of(jAct + 1));
        s_p = DDOP.primeVars(s_p, -(Global.NUM_VARS / 2));

        var nextState = DDOP.sample(List.of(s_p), i_S);
        return ddFromVarVals(nextState);
            }

    public static Tuple<List<Integer>, List<Integer>> sampleObservations(
            DD s, 
            List<DD> O, List<Integer> i_Om_p, List<Integer> i_S_p,
            int i_Aj, int jAct) {

        var s_p = DDOP.primeVars(s, (Global.NUM_VARS / 2));
        var o = DDOP.restrict(O, List.of(i_Aj), List.of(jAct + 1));
        o.add(s_p);

        var oDD = DDOP.addMultVarElim(o, i_S_p);
        return DDOP.sample(List.of(oDD), i_Om_p);
            }

    public static 
        Tuple<List<Integer>, List<Integer>> sampleObservations(DD s,
                List<DD> O, List<Integer> i_Om_p, List<Integer> i_S_p) {

            var s_p = DDOP.primeVars(s, (Global.NUM_VARS / 2));
            var o = new ArrayList<>(O);
            o.add(s_p);

            var oDD = DDOP.addMultVarElim(o, i_S_p);
            return DDOP.sample(List.of(oDD), i_Om_p);
        }

    public static void main(String[] args) throws Exception {

        CommandLineParser cliParser = new DefaultParser();
        Options opt = new Options();

        opt.addOption("h", false, "print help");
        opt.addOption("d", true, "path to the SPUDDX file");
        opt.addOption("r", true, "results dir");
        opt.addOption("iBel", true, 
                "name of the initial belief DD of agent i");
        opt.addOption("jBel", true, 
                "name of the initial belief DD of agent j");
        opt.addOption("iState", true, 
                "name of the initial state DD");
        opt.addOption("iName", true, "name of agent i");
        opt.addOption("jName", true, "name of agent j");
        opt.addOption("p", true, 
                "path to the JSON file to store the policy " +
                "(solve) for computing it online");
        opt.addOption("l", true, "length of the interaction");
        opt.addOption("i", true, "number of interactions");

        CommandLine line = null;
        line = cliParser.parse(opt, args);

        if (line.hasOption("h")) {
            new HelpFormatter().printHelp(" ", opt);
            System.exit(0);
        }

        String domainFile = line.getOptionValue("d");
        String resultsDir = line.getOptionValue("r");

        Global.RESULTS_DIR = Path.of(resultsDir);
        SimulateInteraction.JSON_DATA = new JsonArray();

        String iName = line.getOptionValue("iName");
        String jName = line.getOptionValue("jName");
        String iBel = line.getOptionValue("iBel");
        String jBel = line.getOptionValue("jBel");
        String iState = line.getOptionValue("iState");
        int l = Integer.parseInt(line.getOptionValue("l"));
        int i = Integer.parseInt(line.getOptionValue("i"));


        // Multi-agent interaction

        // Parse SPUDDX file
        var parser = new SpuddXMainParser(domainFile);
        parser.run();

        AlphaVectorPolicy p = null;

        var model = (IPOMDP) parser.getModel(iName).orElseGet(() ->
                {
                    LOGGER.error("Model %s not found", iName);
                    System.exit(-1);
                    return null;
                });

        var jModel = model.framesj.stream()
            .filter(_m -> _m._1().getName().equals(jName))
            .findFirst()
            .map(_m -> _m._1()).get();

        var jPolicy = model.ecThetas.stream()
            .filter(_p -> _p.m.getName().equals(jName))
            .findFirst()
            .map(_p -> _p.Vn).get();

        var b_i = parser.getDD(iBel);
        var b_j = parser.getDD(jBel);
        var s = parser.getDD(iState);

        if (b_i == null) {
            LOGGER.error("Belief DD %s does not exist", iBel);
            System.exit(-1);
        }

        if (b_j == null) {
            LOGGER.error("Belief DD %s does not exist", jBel);
            System.exit(-1);
        }

        b_i = model.getECDDFromMjDD(b_i);
        b_j = jModel instanceof IPOMDP _jModel ?
            _jModel.getECDDFromMjDD(b_j) : b_j;

        if (line.getOptionValue("p") != null) {
            if (line.getOptionValue("p").equals("solve"))
                p = new SymbolicPerseusSolver<>()
                    .solve(List.of(b_i), 
                            model, 100, 20, 
                            AlphaVectorPolicy.fromR(model.R()));

            else
                p = AlphaVectorPolicy.fromJson(
                        Utils.readJsonFromFile(
                            line.getOptionValue("p")));

            System.out.println("Graph is:");
            var G = PolicyGraph.makePolicyGraph(List.of(b_i), model, p);
            Utils.serializePolicyGraph(G, model.getName());
        }

        for (int n = 0; n < i; n++) {
            System.out.printf("Running interaction %s\r\n", n);
            SimulateInteraction.runSimulator(
                    model, jModel, s, b_i, b_j, p, jPolicy, l);
        }

        var gson = new GsonBuilder().setPrettyPrinting().create();
        Files.writeString(
                Path.of(
                    Global.RESULTS_DIR.toAbsolutePath().toString(), 
                    "trace.json"), 
                gson.toJson(JSON_DATA));
    }
}
