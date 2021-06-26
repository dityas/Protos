/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.executables;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import thinclab.legacy.Global;
import thinclab.spuddx_parser.SpuddXParserWrapper;

/*
 * @author adityas
 *
 */
public class ProtosLauncher {

	public static void main(String[] args) throws Exception {

		/* Parse CMD args */
		CommandLineParser cliParser = new DefaultParser();
		Options opt = new Options();

		/* domain file */
		opt.addOption("d", true, "path to the SPUDDX file");

		CommandLine line = null;
		line = cliParser.parse(opt, args);
		
		String domainFile = line.getOptionValue("d");
		
		// Parse SPUDDX file
		var parser = new SpuddXParserWrapper(domainFile);
		
		// Parser variable declarations and initialize random variables
		var variables = parser.getVariableDeclarations();
		Global.primeVarsAndInitGlobals(variables);
		
		// Parse model declarations
		var models = parser.getModels();
		
		// Clean up
		parser = null;
		
		System.out.println(models);
	}

}
