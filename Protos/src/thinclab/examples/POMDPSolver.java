/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.examples;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import thinclab.frameworks.POMDP;
import thinclab.policyhelper.PolicyExtractor;
import thinclab.policyhelper.PolicyGraph;
import thinclab.policyhelper.PolicyVisualizer;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
public class POMDPSolver {
	
	/*
	 * Entry point for POMDP solver using CLI
	 */
	
	/* POMDP information */
	public POMDP pomdp;
	public String domainFile;
	
	/* Solver params */
	public int perseusRounds;
	public int numDpBackups;
	
	// ---------------------------------------------------------------------------------------------
	
	public POMDPSolver(String fileName, int rounds, int dpBackups) {
		/*
		 * Set class attributes
		 */
		
		this.domainFile = fileName;
		this.perseusRounds = rounds;
		this.numDpBackups = dpBackups;
		
	}
	
	public void solvePOMDP() {
		/*
		 * Starts the solver and waits for convergence or max rounds
		 */
		this.pomdp = new POMDP(this.domainFile);
		this.pomdp.solvePBVI(this.perseusRounds, this.numDpBackups);
	}
	
	public void showPolicyGraph() {
		/*
		 * Starts the visualizer and shows the policy graph in JUNG
		 */
		PolicyExtractor policyExtractor = new PolicyExtractor(this.pomdp);
		PolicyGraph policyGraph = new PolicyGraph(policyExtractor.policyNodes);
		PolicyVisualizer viz = new PolicyVisualizer(policyGraph);
	}
	
	// -----------------------------------------------------------------------------------------------

	public static void main(String[] args) {
		
		/* Parse CMD args */
		CommandLineParser cliParser = new DefaultParser();
		Options opt = new Options();
		
		/* domain file */
		opt.addOption("d", true, "path to the domain file");
		
		/* log file */
		opt.addOption("l", true, "log file path");
		
		/* max policy tree depth for Mj */
		opt.addOption("r", true, "number of rounds for symbolic perseus");
		
		/* look ahead horizon for agent I */
		opt.addOption("b", true, "number of backups in each round");
		
		/* Visualize? */
		opt.addOption("z", "viz", false, "show policy graph");
		
		CommandLine line = null;
		POMDPSolver solver = null;
		
		try {
			line = cliParser.parse(opt, args);
			
			/* set CLI args */
			
			/* if log file is given, initialize logging accordingly */
			if (line.hasOption("l"))
				CustomConfigurationFactory.setLogFileName(line.getOptionValue("l"));
			
			CustomConfigurationFactory.initializeLogging();
			
			/* set domain file */
			String domainFile = line.getOptionValue("d");
			
			/* set mjDepth */
			int rounds = new Integer(line.getOptionValue("r"));
			
			/* set look ahead */
			int backups = new Integer(line.getOptionValue("b"));
			
			solver = new POMDPSolver(domainFile, rounds, backups);
			solver.solvePOMDP();
			
			if (line.hasOption("z")) solver.showPolicyGraph();
		} 
		
		catch (ParseException e) {
			System.out.println("While parsing args: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}

	}

}
