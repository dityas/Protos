/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.executables;

import java.util.List;

import com.google.gson.JsonObject;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.legacy.Global;
import thinclab.models.POMDP;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.spuddx_parser.SpuddXMainParser;
import thinclab.utils.Utils;

/*
 * @author adityas
 *
 */
public class GetPolicy {

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(GetPolicy.class);

	public static void main(String[] args) throws Exception {

		CommandLineParser cliParser = new DefaultParser();
		Options opt = new Options();
		
		opt.addOption("h", false, "print help");
		opt.addOption("pomdp", false, "solve for POMDP");
		opt.addOption("ipomdp", false, "solve for IPOMDP");
		opt.addOption("d", true, "path to the SPUDDX file");
		opt.addOption("b", true, "name of the initial belief DD");
		opt.addOption("m", true, "name of the POMDP/IPOMDP");
		opt.addOption("p", true, "path to the file to store the policy");

		CommandLine line = null;
		line = cliParser.parse(opt, args);

        if (line.hasOption("h")) {
            new HelpFormatter().printHelp(" ", opt);
            System.exit(0);
        }
		
		String domainFile = line.getOptionValue("d");
		String modelName = line.getOptionValue("m");
        String biName = line.getOptionValue("b");
        String policyFile = line.getOptionValue("p");

		// Parse SPUDDX file
		var parser = new SpuddXMainParser(domainFile);
		parser.run();

        // Solve POMDP
        if (line.hasOption("pomdp")) {
            var I = (POMDP) parser.getModel(modelName).orElseGet(() ->
			{
				LOGGER.error("Model %s not found", modelName);
				System.exit(-1);
				return null;
			});

            var b_i = parser.getDD(biName);
            if (b_i == null) {
                LOGGER.error("Belief DD %s does not exist", biName);
                System.exit(-1);
            }

    		var solver = new SymbolicPerseusSolver<POMDP>(I);
	    	var policy = solver.solve(List.of(b_i), 100, 10);

            var _json = new JsonObject();
            _json.add("variables", Global.toJson());
            _json.add("policy", policy.toJson());

            Utils.writeJsonToFile(_json, policyFile);
        }
        
        // Solve IPOMDP
        if (line.hasOption("ipomdp")) {
            var I = (IPOMDP) parser.getModel(modelName).orElseGet(() ->
			{
				LOGGER.error("Model %s not found", modelName);
				System.exit(-1);
				return null;
			});

            var b_i = parser.getDD(biName);
            if (b_i == null) {
                LOGGER.error("Belief DD %s does not exist", biName);
                System.exit(-1);
            }

    		var solver = new SymbolicPerseusSolver<IPOMDP>(I);
	    	var policy = solver.solve(List.of(b_i), 100, I.H);

            Utils.writeJsonToFile(policy.toJson(), policyFile);
        }
	}
}
