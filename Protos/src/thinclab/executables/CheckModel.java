
package thinclab.executables;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.models.POMDP;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.spuddx_parser.SpuddXMainParser;


public class CheckModel {

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(CheckModel.class);

    public static void printTransitions(POMDP model) {

        for (int a = 0; a < model.A().size(); a++) {
            for (int s = 0; s < model.S.size(); s++) {
                
                System.out.printf("T(%s' | %s, %s) = %s\r\n\r\n",
                        model.S.get(s),
                        model.S.get(s),
                        model.A().get(a),
                        model.T().get(a).get(s));
            }
        }
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

        var parser = new SpuddXMainParser(domainFile);
        parser.run();

        var model = parser.getModel(name).orElseGet(() ->
                {
                    LOGGER.error("Model %s not found", name);
                    System.exit(-1);
                    return null;
                });

        if (model instanceof POMDP m)
            CheckModel.printTransitions(m);

//        else if (model instanceof IPOMDP m)
//            CheckModel.printTransitions(m);
    }
}
