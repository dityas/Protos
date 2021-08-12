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
import thinclab.spuddx_parser.SpuddXMainParser;

/*
 * @author adityas
 *
 */
public class ProtosLauncher {

	public static void main(String[] args) throws Exception {

		CommandLineParser cliParser = new DefaultParser();
		Options opt = new Options();
		
		opt.addOption("d", true, "path to the SPUDDX file");

		CommandLine line = null;
		line = cliParser.parse(opt, args);
		
		String domainFile = line.getOptionValue("d");
		
		// Parse SPUDDX file
		var parser = new SpuddXMainParser(domainFile);
		parser.run();
	}
}
