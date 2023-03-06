
package thinclab.executables;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
import thinclab.Simulator;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.models.datastructures.PolicyGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.spuddx_parser.SpuddXMainParser;
import thinclab.utils.Utils;


public class SimulateInteraction {


    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(SimulateInteraction.class);

    public static void runMultiAgentInteraction(Simulator sim,
            final IPOMDP agentI,
            final PBVISolvablePOMDPBasedModel agentJ,
            final AlphaVectorPolicy agentIPolicy,
            final AlphaVectorPolicy agentJPolicy,
            DD state, DD iBelief, DD jBelief, int length,
            JsonArray recorder) {

        // set initial state
        sim.setState(state);

        for (int i = 0; i < length; i++) {

            // get optimal actions
            var optActI = agentIPolicy.getBestActionIndex(
                    iBelief, agentI.i_S());
            var optActJ = agentJPolicy.getBestActionIndex(
                    jBelief, agentJ.i_S());

            // step the simulator
            var observations = sim.step(optActI, optActJ);

            // Record the step in JSON
            var step = new JsonObject();
            var stateJSON = DDOP.toJson(sim.getState(), sim.stateIndices);
            step.add("state", stateJSON);

            var agentIJSON = DDOP.toJson(iBelief, agentI.i_S())
                .getAsJsonObject();
            agentIJSON.add("observation", observations._0().toJson());
            step.add("agent_i", agentIJSON);

            var agentJJSON = DDOP.toJson(jBelief, agentJ.i_S())
                .getAsJsonObject();
            agentJJSON.add("observation", observations._1().toJson());
            step.add("agent_j", agentJJSON);

            recorder.add(step);

            // update agent beliefs
            iBelief = agentI.beliefUpdate(
                    iBelief, optActI, observations._0()._1());
            jBelief = agentJ.beliefUpdate(
                    jBelief, optActJ, observations._1()._1());

        }
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

        // Get the agent model
        var model = (IPOMDP) parser.getModel(iName).orElseGet(() ->
                {
                    LOGGER.error("Model %s not found", iName);
                    System.exit(-1);
                    return null;
                });

        // Get the opponent model
        var jModel = model.framesj.stream()
            .filter(_m -> _m._1().getName().equals(jName))
            .findFirst()
            .map(_m -> _m._1()).get();

        // Get opponent policy
        var jPolicy = model.ecThetas.stream()
            .filter(_p -> _p.m.getName().equals(jName))
            .findFirst()
            .map(_p -> _p.Vn).get();

        // Get initial beliefs and starting state
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

        // Map beliefs to equivalence classes if we are dealing with IPOMDPs
        b_i = model.getECDDFromMjDD(b_i);
        b_j = jModel instanceof IPOMDP _jModel ?
            _jModel.getECDDFromMjDD(b_j) : b_j;

        // Solve or load policy
        if (line.getOptionValue("p") != null) {
            if (line.getOptionValue("p").equals("solve"))
                p = new SymbolicPerseusSolver<>(model)
                    .solve(List.of(b_i), 100, 20);

            else
                p = AlphaVectorPolicy.fromJson(
                        Utils.readJsonFromFile(
                            line.getOptionValue("p")));

            var G = PolicyGraph.makePolicyGraph(List.of(b_i), model, p);
            Utils.serializePolicyGraph(G, model.getName());
        }

        // Run the interaction
        var stateIndices = new ArrayList<>(model.i_S());
        stateIndices.remove(stateIndices.size() - 1);

        var sim = new Simulator(stateIndices,
                model.i_A, jModel.i_A, 
                model.i_Om_p(), jModel.i_Om_p(), 
                model.T(), model.O(), jModel.O());

        for (int n = 0; n < i; n++) {
            LOGGER.info("Running interaction %s", n);

            // For recording the interaction
            var recorder = new JsonArray();
            runMultiAgentInteraction(sim, model, jModel, p, jPolicy, 
                    s, b_i, b_j, l, recorder);

            // Write the interaction to a file
            if (Global.RESULTS_DIR != null) {
                String fileName = String.format("%s/trace.%s.json", 
                        Global.RESULTS_DIR, n);
                LOGGER.info("Recording interaction %s to %s", n, fileName);
                Utils.writeJsonToFile(recorder, fileName);
            }
        }
    }
}
