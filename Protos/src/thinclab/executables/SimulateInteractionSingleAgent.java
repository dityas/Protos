
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
import thinclab.models.POMDP;
import thinclab.models.datastructures.PolicyGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.spuddx_parser.SpuddXMainParser;
import thinclab.utils.Tuple;
import thinclab.utils.Utils;

public class SimulateInteractionSingleAgent {

    public static GsonBuilder JSON = null;
    public static JsonArray JSON_DATA = null; 

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(SimulateBeliefUpdates.class);

    public static 
        JsonObject 
        getVarValsJSON(Tuple<List<Integer>, List<Integer>> varVals) {

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

    public static void 
        runSimulatorSingleAgent(final PBVISolvablePOMDPBasedModel model,
                DD s,
                DD b_i,
                AlphaVectorPolicy p,
                int length) {

            // prepare state and prime state indices for use later
            var X = new ArrayList<>(model.i_S());
            var X_p = Global.makePrimeIndices(X);

            var JSON_ARRAY = Global.RESULTS_DIR == null ? null : new JsonArray();

            for (int iter = 0; iter < length; iter++) {

                var iAct = p.getBestActionIndex(b_i);
                var s_p = updateState(s, X, model.T().get(iAct));
                var iObs = sampleObservations(s_p, model.O().get(iAct), 
                        model.i_Om_p(), X_p);


                // Write results
                if (Global.RESULTS_DIR != null) {

                    var json = new JsonObject();

                    json.addProperty("time step", iter);
                    json.add("iBel", DDOP.toJson(b_i, model.i_S()));
                    json.addProperty("iR", DDOP.dotProduct(
                                model.R().get(iAct), b_i, model.i_S()));

                    json.add("iObs", getVarValsJSON(iObs));
                    json.add("state", DDOP.toJson(s, X));
                    json.addProperty("iAct", model.A().get(iAct));

                    JSON_ARRAY.add(json);
                }

                b_i = model.beliefUpdate(b_i, iAct, iObs._1());
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

    public static DD updateState(DD s, List<Integer> i_S, List<DD> T) {

        var dds = new ArrayList<>(T);
        dds.add(s);

        var s_p = DDOP.addMultVarElim(dds, i_S);
        s_p = DDOP.primeVars(s_p, -(Global.NUM_VARS / 2));

        var nextState = DDOP.sample(List.of(s_p), i_S);
        return ddFromVarVals(nextState);
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
        opt.addOption("bel", true, 
                "name of the initial belief DD of agent i");
        opt.addOption("state", true, 
                "name of the initial state DD");
        opt.addOption("name", true, "name of agent i");
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
        SimulateInteractionSingleAgent.JSON_DATA = new JsonArray();

        String name = line.getOptionValue("name");
        String bel = line.getOptionValue("bel");
        String state = line.getOptionValue("state");
        int l = Integer.parseInt(line.getOptionValue("l"));
        int i = Integer.parseInt(line.getOptionValue("i"));

        // Single agent interaction
        var parser = new SpuddXMainParser(domainFile);
        parser.run();

        AlphaVectorPolicy p = null;

        var model = (POMDP) parser.getModel(name).orElseGet(() ->
                {
                    LOGGER.error("Model %s not found", name);
                    System.exit(-1);
                    return null;
                });

        var b_i = parser.getDD(bel);
        var s = parser.getDD(state);

        if (line.getOptionValue("p") != null) {
            if (line.getOptionValue("p").equals("solve"))
                p = new SymbolicPerseusSolver<>(model)
                    .solve(List.of(b_i), 100, 20);

            else
                p = AlphaVectorPolicy.fromJson(
                        Utils.readJsonFromFile(
                            line.getOptionValue("p")));

            System.out.println("Graph is:");
            var G = PolicyGraph.makePolicyGraph(List.of(b_i), model, p);
            System.out.println(G);
        }

        for (int n = 0; n < i; n++) {
            System.out.printf("Running interaction %s\r\n", n);
            SimulateInteractionSingleAgent.runSimulatorSingleAgent(
                    model, s, b_i, p, l);
        }

        var gson = new GsonBuilder().setPrettyPrinting().create();
        Files.writeString(
                Path.of(
                    Global.RESULTS_DIR.toAbsolutePath().toString(), 
                    "trace.json"), 
                gson.toJson(SimulateInteractionSingleAgent.JSON_DATA));

    }
}
