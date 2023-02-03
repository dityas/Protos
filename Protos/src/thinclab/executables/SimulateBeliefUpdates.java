
package thinclab.executables;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.POMDP;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.models.datastructures.PolicyGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.spuddx_parser.SpuddXMainParser;
import thinclab.utils.Utils;

public class SimulateBeliefUpdates {

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(SimulateBeliefUpdates.class);

    public static void runSimulator(final PBVISolvablePOMDPBasedModel model,
            DD b_i,
            AlphaVectorPolicy p) {

        var allObs = model.oAll;

        var allObsLabels = allObs.stream()
            .map(os -> IntStream.range(0, os.size())
                    .boxed()
                    .map(i -> 
                        Global.valNames
                        .get(model.i_Om_p.get(i) - 1)
                        .get(os.get(i) - 1))
                    .collect(Collectors.toList()))
            .collect(Collectors.toList());

        var in = new Scanner(System.in);

        var totalReward = 0.0f;

        while (true) {

            System.out.println();

            var b_factors = DDOP.factors(b_i, model.i_S());
            var b_EC = b_factors.get(b_factors.size() - 1);

            // print belief
            System.out.println("Factors:==");
            b_factors.forEach(_b -> {
                System.out.println(_b);
            });
            System.out.println("==========");


            // for IPOMDPs, print everything
            if (model instanceof IPOMDP _model) {
                System.out.printf("Belief over Aj's frame is %s\r\n", 
                            DDOP.getFrameBelief(
                                b_i, 
                                _model.PThetajGivenEC,
                                _model.i_EC, 
                                _model.i_S()));

                System.out.printf("Predicted actions: %s\r\n", 
                            DDOP.addMultVarElim(
                                List.of(_model.PAjGivenEC, b_EC), 
                                List.of(_model.i_EC)));
            }

            // Prompt for actions
            System.out.println();
            System.out.print("Enter action index: ");
            if (p != null) {
                var suggestedA = p.getBestActionIndex(b_i, model.i_S());
                System.out.printf("(policy suggests: %s)\r\n", 
                        model.A().get(suggestedA));
            }
            System.out.println();
            IntStream.range(0, model.A().size())
                .forEach(i -> {
                    System.out.printf("%s: %s\r\n", 
                                i, model.A().get(i));
                });

            System.out.print(">>> ");
            int aIndex = in.nextInt();

            // obs likelihoods
            var likelihoods = model.obsLikelihoods(b_i, aIndex);

            System.out.println();
            System.out.println("Enter observation index:");
            var obsNames = model.i_Om().stream()
                .map(o -> Global.varNames.get(o - 1))
                .collect(Collectors.toList());
            System.out.println(obsNames);

            IntStream.range(0, allObsLabels.size())
                .forEach(i -> {
                    var obs = DDOP.restrict(
                            likelihoods, model.i_Om_p(), allObs.get(i));

                    if (DDOP.maxAll(DDOP.abs(DDOP.sub(obs, DD.zero))) > 1e-4f) {
                        System.out.printf("%s: %s \t(likelihood: %s) \r\n", 
                                    i, allObsLabels.get(i), obs);
                    }
                });

            System.out.print(">>> ");
            int oIndex = in.nextInt();

            System.out.println();

            var rew = DDOP.dotProduct(b_i, model.R().get(aIndex), model.i_S());
            totalReward += rew;

            System.out.printf(
                        "a: %s, o: %s, R: %s, total R: %s\r\n", 
                        model.A().get(aIndex), 
                        allObsLabels.get(oIndex), 
                        rew, totalReward);

            b_i = model.beliefUpdate(b_i, aIndex, allObs.get(oIndex));
        }
    }

    public static void main(String[] args) throws Exception {

        CommandLineParser cliParser = new DefaultParser();
        Options opt = new Options();

        opt.addOption("h", false, "print help");
        opt.addOption("pomdp", false, "solve for POMDP");
        opt.addOption("ipomdp", false, "solve for IPOMDP");
        opt.addOption("d", true, "path to the SPUDDX file");
        opt.addOption("b", true, "name of the initial belief DD");
        opt.addOption("m", true, "name of the POMDP/IPOMDP");
        opt.addOption("p", true, "path to the JSON file to store the policy" +
                "(solve) for computing it online");

        CommandLine line = null;
        line = cliParser.parse(opt, args);

        if (line.hasOption("h")) {
            new HelpFormatter().printHelp(" ", opt);
            System.exit(0);
        }

        String domainFile = line.getOptionValue("d");
        String modelName = line.getOptionValue("m");
        String biName = line.getOptionValue("b");

        // Parse SPUDDX file
        var parser = new SpuddXMainParser(domainFile);
        parser.run();

        AlphaVectorPolicy p = null;
        PBVISolvablePOMDPBasedModel model = null;

        // Solve POMDP
        if (line.hasOption("pomdp")) {
            model = (POMDP) parser.getModel(modelName).orElseGet(() ->
                    {
                        LOGGER.error("Model %s not found", modelName);
                        System.exit(-1);
                        return null;
                    });
        }

        else {
            model = (IPOMDP) parser.getModel(modelName).orElseGet(() ->
                    {
                        LOGGER.error("Model %s not found", modelName);
                        System.exit(-1);
                        return null;
                    });
        }

        var b_i = parser.getDD(biName);
        if (b_i == null) {
            LOGGER.error("Belief DD %s does not exist", biName);
            System.exit(-1);
        }

        b_i = model instanceof IPOMDP _model ? 
            _model.getECDDFromMjDD(b_i) : b_i;

        if (line.getOptionValue("p") != null) {
            if (line.getOptionValue("p").equals("solve"))
                p = new SymbolicPerseusSolver<>()
                    .solve(List.of(b_i), 
                            model, 100, 10, 
                            AlphaVectorPolicy.fromR(model.R()));

            else
                p = AlphaVectorPolicy.fromJson(
                        Utils.readJsonFromFile(line.getOptionValue("p")));

            System.out.println("Graph is:");
            var G = PolicyGraph.makePolicyGraph(List.of(b_i), model, p);
            System.out.println(G);
            System.out.println();
        }

        SimulateBeliefUpdates.runSimulator(model, b_i, p);
    }
}
