/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.examples;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;

import thinclab.ddhelpers.DDMaker;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
public class SPUDDAssist {
	
	/*
	 * A CLI tool to help with creating huge DDs
	 */
	
	public DDMaker ddMaker;
	
	private Scanner consoleReader;

	// -----------------------------------------------------------------------------------------
	
	public SPUDDAssist() {
		
		/* Initialize logging  */
		CustomConfigurationFactory.initializeLogging();
		
		/* Initialize DDMaker */
		this.ddMaker = new DDMaker();
		
		/* Initialize console reader */
		this.consoleReader = new Scanner(System.in);
	}
	
	// -----------------------------------------------------------------------------------------
	
	public void addVariable(String[] cliArgs) {
		/*
		 * Adds variable to the DDMaker
		 * 
		 * cliArgs[0] is assumed to be the var name and cliArgs[1:...] are the values
		 */
		
		try {
			this.ddMaker.addVariable(
					cliArgs[0],
					ArrayUtils.subarray(
							cliArgs, 
							1, 
							cliArgs.length));
			this.showCurrentVariables();
		}
		
		catch (Exception e) {
			System.err.println("ERROR while running addVariable: " + e.getMessage());
		}
	}
	
	private void showCurrentVariables() {
		/*
		 * Prints out all the variables in the DDMaker
		 */
		System.out.println("Current vars are: ");
		
		for (Entry<String, String[]> entry: this.ddMaker.getVariablesMap().entrySet())
			System.out.println(
					entry.getKey() + " : " + Arrays.toString(entry.getValue()));
	}
	
	private String[] getCommand() {
		/*
		 * Prompts the user and fetches the command and parses it
		 */
		try {
			
			System.out.print(">>> ");
			String cmd = this.consoleReader.nextLine();
			return cmd.split(" ");
		}
		
		catch (Exception e) {
			System.err.println("While parsing command: " + e.getMessage());
			return null;
		}
	}

	public void loop() {
		
		System.out.println("Started SPUDDAssist...");
		
		/* Loop forever and ever and ever... */
		while (true) {
			
			String[] command = this.getCommand();
			
			/* the CLI parser is really just a huge if else statement */
			if (command[0].toLowerCase().contentEquals("exit")) break;
			
			else if (command[0].toLowerCase().contentEquals("addv"))
				this.addVariable(ArrayUtils.subarray(command, 1, command.length));
			
			else if (command[0].toLowerCase().contentEquals("make")) this.ddMaker.primeVariables();
			
			else System.err.println("No such command: " + Arrays.toString(command));
		}
		
		this.consoleReader.close();
		System.out.println("Exiting...");
	}
	
	public static void main(String[] args) {
		
		SPUDDAssist sa = new SPUDDAssist();
		sa.loop();
	}
}
