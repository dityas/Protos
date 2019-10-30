/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.executables;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/*
 * @author adityas
 *
 */
public abstract class Executable {
	
	/*
	 * Basic skeleton for executable scripts and entry points
	 */
	
	public CommandLineParser cliParser = new DefaultParser();
	public Options opt = new Options();
	
	public void printHelp() {
		/*
		 * Print CLI help
		 */
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("cli", this.opt);
	}
	
	public static void printHelp(Options opt) {
		/*
		 * Print CLI help
		 */
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("cli", opt);
	}
}
