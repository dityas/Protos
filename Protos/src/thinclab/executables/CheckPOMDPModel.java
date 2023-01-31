
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

public class CheckPOMDPModel {

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(CheckPOMDPModel.class);

    public static void printTransitions(POMDP model) {

        System.out.println(model.T());
    
    }

    public static void main(String[] args) throws Exception {

        CommandLineParser cliParser = new DefaultParser();
        Options opt = new Options();

        opt.addOption("h", false, "print help");
        opt.addOption("d", true, "path to the SPUDDX file");
        opt.addOption("name", true, "name of the agent");

        CommandLine line = null;
        line = cliParser.parse(opt, args);

        if (line.hasOption("h")) {
            new HelpFormatter().printHelp(" ", opt);
            System.exit(0);
        }

        String domainFile = line.getOptionValue("d");

        String name = line.getOptionValue("name");

        // Single agent interaction
        var parser = new SpuddXMainParser(domainFile);
        parser.run();

        var model = (POMDP) parser.getModel(name).orElseGet(() ->
                {
                    LOGGER.error("Model %s not found", name);
                    System.exit(-1);
                    return null;
                });

    }
}
