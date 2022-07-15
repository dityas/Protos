
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
import thinclab.policy.AlphaVectorPolicy;
import thinclab.spuddx_parser.SpuddXMainParser;
import thinclab.utils.Utils;

public class SimulateBeliefUpdates {

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(SimulateBeliefUpdates.class);

    public static void runSimulator(
            final PBVISolvablePOMDPBasedModel model,
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
        var b = b_i;

        // start belief stepping
        while (true) {

            System.out.println();

            var b_factors = DDOP.factors(b, model.i_S());
            var b_EC = b_factors.get(b_factors.size() - 1);

            // print belief
            System.out.println("Current belief is:");
            b_factors.forEach(_b -> {
                System.out.println(_b);
            });


            // for IPOMDPs, print everything

            if (model instanceof IPOMDP _model) {
                System.out.println(
                        String.format(
                            "Belief over Aj's frame is %s", 
                            DDOP.getFrameBelief(
                                b, 
                                _model.PThetajGivenEC,
                                _model.i_EC, 
                                _model.i_S())));
                System.out.println(
                        String.format(
                            "Belief of attacker level 2 is %s", 
                            b_factors));
                System.out.println(
                        String.format(
                            "Predicted actions: %s", 
                            DDOP.addMultVarElim(
                                List.of(_model.PAjGivenEC, b_EC), 
                                List.of(_model.i_EC))));
            }

            // Prompt for actions
            System.out.println();
            System.out.print("Enter action index: ");
            if (p != null) {
                var suggestedA = p.getBestActionIndex(b, model.i_S());
                System.out.print(String.format("(policy suggests: %s)",
                            model.A().get(suggestedA)));
            }
            System.out.println();
            IntStream.range(0, model.A().size())
                .forEach(i -> {
                    System.out.println(
                            String.format("%s: %s", 
                                i, model.A().get(i)));
                });

            System.out.print(">>> ");
            int aIndex = in.nextInt();

            // obs likelihoods
            var likelihoods = model.obsLikelihoods(b, aIndex);

            System.out.println();
            System.out.println("Enter observation index:");

            IntStream.range(0, allObsLabels.size())
                .forEach(i -> {
                    var obs = DDOP.restrict(
                            likelihoods, model.i_Om_p(), allObs.get(i));
                    System.out.println(
                            String.format("%s: %s \t(likelihood: %s)", 
                                i, allObsLabels.get(i), obs));
                });

            System.out.print(">>> ");
            int oIndex = in.nextInt();

            System.out.println();
            System.out.println(
                    String.format(
                        "Action: %s, obs: %s, reward: %s", 
                        model.A().get(aIndex), 
                        allObsLabels.get(oIndex),
                        DDOP.dotProduct(
                            b, model.R().get(aIndex), model.i_S())));

            b = model.beliefUpdate(b, aIndex, allObs.get(oIndex));
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
        opt.addOption("p", true, "path to the JSON file to store the policy");

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

        if (line.getOptionValue("p") != null)
            p = AlphaVectorPolicy.fromJson(
                    Utils.readJsonFromFile(line.getOptionValue("p")));
        
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

        var b_i = parser.getDD(biName);
        if (b_i == null) {
            LOGGER.error("Belief DD %s does not exist", biName);
            System.exit(-1);
        }

        SimulateBeliefUpdates.runSimulator(model, b_i, p);

    }
}
