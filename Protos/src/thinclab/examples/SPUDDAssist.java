/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.examples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import thinclab.ddhelpers.DDMaker;
import thinclab.ddhelpers.DDTree;
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
	public HashMap<String, DDTree> ddMap;
	
	private Scanner consoleReader;

	// -----------------------------------------------------------------------------------------
	
	public SPUDDAssist() {
		
		/* Initialize logging  */
		CustomConfigurationFactory.initializeLogging();
		
		/* Initialize DDMaker and HashMap for DDs*/
		this.ddMaker = new DDMaker();
		this.ddMap = new HashMap<String, DDTree>();
		
		/* Initialize console reader */
		this.consoleReader = new Scanner(System.in);
	}
	
	// -----------------------------------------------------------------------------------------
	
	public void makeNewDD(String[] cliArgs) {
		/*
		 * Constructs a new DD using the current variables in the DDMaker
		 */
		
		String ddName = cliArgs[0];
		String[] varSequence = ArrayUtils.subarray(cliArgs, 1, cliArgs.length);
		List<String[]> newCPT = new ArrayList<String[]>();
		
		try {
			List<List<String>> CPT = this.ddMaker.getDDTreeFromSequence(varSequence).getCPT();
			
			/* Show default CPT */
			System.out.println("CPT is...");
			for (List<String> row: CPT)
				System.out.println(row);
			System.out.println();
			
			/* Ask for each row */
			for (List<String> row : CPT) {
				System.out.println(Arrays.toString(varSequence));
				System.out.print(row.subList(0, row.size() - 1) + "> ");
				String newVal = consoleReader.nextLine();
				row.set(row.size() - 1, newVal);
				
				newCPT.add(row.toArray(new String[row.size()]));
			}
			
			/* Construct DDTree from new CPTs and the varSequence */
			DDTree t = 
					this.ddMaker.getDDTreeFromSequence(
							varSequence, 
							newCPT.toArray(new String[newCPT.size()][]));
			
			/* Store DD in the HashMap */
			this.ddMap.put(ddName, t);
			
			this.showDDs();
		}
		
		catch (Exception e) {
			System.err.println("While making new CPT: " + e.getMessage());
		}
	}
	
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
	
	public void writeToFile(String[] cliArgs) {
		/*
		 * Writes all variable and DDs to the given file in SPUDD format
		 */
		try {
			System.out.println("Writing to " + cliArgs[0]);
			PrintWriter writer = new PrintWriter(cliArgs[0]);
			
			/* write variables */
			writer.println("(variables");
			
			for (Entry<String, String[]> entry : this.ddMaker.getVariablesMap().entrySet()) {
				
				String name = entry.getKey();
				
				/* Only write unprimed vars */
				if (!name.endsWith("'")) {
					String values = String.join(" ", entry.getValue());
					writer.println("\t(" + name + " " + values + ")");
					writer.flush();
				}
				
			}
			
			writer.println(")");
			writer.println();
			
			/* write DDs */
			for (String name : this.ddMap.keySet()) {
				
				writer.println("dd " + name);
				writer.println(this.ddMap.get(name).toSPUDD());
				writer.println("enddd");
				writer.println();
				writer.flush();
			}
			
			/* close writer */
			writer.close();
		}
		
		catch (Exception e) {
			System.err.println("While writing to file: " + e.getMessage());
		}
	}
	
	public void importVarsFromFile(String[] cliArgs) {
		/*
		 * Import variables from a file
		 * 
		 * The variables should be written as comma separated strings in the following format:
		 * 
		 *  	Var1, value1, value2
		 *  	Var2, value1, value2
		 */
		try {
			
			System.out.println("Importing vars from " + cliArgs[0]);
			
			this.ddMaker.clearContext();
			this.ddMap.clear();
			
			BufferedReader reader = new BufferedReader(new FileReader(cliArgs[0]));
			
			while(true) {
				String line = reader.readLine();
				
				if (line == null || line.length() == 0) break;
				
				String[] varDef = line.split(",\\s*");
				
				if (varDef.length == 0) break;
				
				this.ddMaker.addVariable(varDef[0], ArrayUtils.subarray(varDef, 1, varDef.length));
			}
			
			reader.close();
			this.ddMaker.primeVariables();
			
			this.showCurrentVariables();
		} 
		
		catch (Exception e) {
			System.err.println("While trying to import vars from file");
			System.exit(-1);
		}
	}
	
	// -----------------------------------------------------------------------------------------
	
	private void showCurrentVariables() {
		/*
		 * Prints out all the variables in the DDMaker
		 */
		System.out.println("Current vars are: ");
		
		for (Entry<String, String[]> entry: this.ddMaker.getVariablesMap().entrySet())
			System.out.println(
					entry.getKey() + " : " + Arrays.toString(entry.getValue()));
	}
	
	private void showDDs() {
		/*
		 * Prints out the DDs to the console
		 */
		System.out.println("Current DDs are: ");
		for (String name : this.ddMap.keySet())
			System.out.println(name + " = " + this.ddMap.get(name));
	}
	
	// -----------------------------------------------------------------------------------------
	
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
			
			else if (command[0].toLowerCase().contentEquals("addvar"))
				this.addVariable(ArrayUtils.subarray(command, 1, command.length));
			
			else if (command[0].toLowerCase().contentEquals("make")) this.ddMaker.primeVariables();
			
			else if (command[0].toLowerCase().contentEquals("showvars")) this.showCurrentVariables();
			
			else if (command[0].toLowerCase().contentEquals("showdds")) this.showDDs();
			
			else if (command[0].toLowerCase().contentEquals("newdd"))
				this.makeNewDD(ArrayUtils.subarray(command, 1, command.length));
			
			else if (command[0].toLowerCase().contentEquals("write"))
				this.writeToFile(ArrayUtils.subarray(command, 1, command.length));
			
			else if (command[0].toLowerCase().contentEquals("importvars"))
				this.importVarsFromFile(ArrayUtils.subarray(command, 1, command.length));
				
			else if (command[0].toLowerCase().contentEquals("reset")) {
				this.ddMaker.clearContext();
				this.ddMap.clear();
			}
			
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
