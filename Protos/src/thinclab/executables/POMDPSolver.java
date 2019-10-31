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

import thinclab.belief.SSGABeliefExpansion;
import thinclab.decisionprocesses.POMDP;
import thinclab.representations.ConditionalPlanTree;
import thinclab.representations.PolicyGraph;
import thinclab.solvers.OfflinePBVISolver;
import thinclab.solvers.OfflineSolver;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
public class POMDPSolver extends Executable {
	
	/*
	 * Entry point for POMDP solver using CLI
	 */
	
	/* POMDP information */
	public POMDP pomdp;
	public String domainFile;
	
	/* Solver params */
	public int perseusRounds;
	public int numDpBackups;
	public int searchDepth;
	
	/* solver instance */
	public OfflineSolver solver;
	
	// ---------------------------------------------------------------------------------------------
	
	public POMDPSolver(String fileName, int rounds, int dpBackups, int searchDepth) {
		
		/*
		 * Set class attributes
		 */
		
		this.domainFile = fileName;
		this.perseusRounds = rounds;
		this.numDpBackups = dpBackups;
		this.searchDepth = searchDepth;
	}
	
	public void solvePOMDP() {
		/*
		 * Starts the solver and waits for convergence or max rounds
		 */
		this.pomdp = new POMDP(this.domainFile);
		
		this.solver = 
				new OfflineSymbolicPerseus(
						this.pomdp, 
						new SSGABeliefExpansion(this.pomdp, this.searchDepth, 1), 
						this.perseusRounds, 
						this.numDpBackups);
		
		solver.solve();
	}
	
	public void makeConditionalPlan(String dirName) {
		/*
		 * Starts the visualizer and shows the policy graph in JUNG
		 */
		ConditionalPlanTree T = new ConditionalPlanTree(this.solver, 10);
		T.buildTree();
		
		T.writeDotFile(dirName, "plan");
		T.writeJSONFile(dirName, "plan");
	}
	
	public void makePolicyGraph(String dirName) {
		/*
		 * Makes the policy graph from the alpha vector policy
		 */
		
		PolicyGraph pg = new PolicyGraph((OfflinePBVISolver) solver);
		pg.makeGraph();
		
		pg.writeDotFile(dirName, "policy_graph");
		pg.writeJSONFile(dirName, "policy_graph");
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
		
		/* SSGA Expansion depth */
		opt.addOption("s", true, "depth of the SSGA belief search");
		
		/* Create conditional plan */
		opt.addOption(
				"p", 
				"plan", 
				true, 
				"make conditional plan for 10 time steps and "
				+ "create dot and JSON files in the given dir");
		
		/* create policy graph */
		opt.addOption(
				"g", 
				"graph", 
				true, 
				"make policy graph and create dot and JSON files in the given dir");
		
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
			
			/* set search depth */
			int search = new Integer(line.getOptionValue("s"));
			
			solver = new POMDPSolver(domainFile, rounds, backups, search);
			solver.solvePOMDP();
			
			/* conditional plan and policy graph */
			if (line.hasOption("p")) {
				String planDir = line.getOptionValue("p");
				solver.makeConditionalPlan(planDir);
			}
			
			if (line.hasOption("g")) {
				String planDir = line.getOptionValue("g");
				solver.makePolicyGraph(planDir);
			}
		} 
		
		catch (Exception e) {
			System.out.println("While parsing args: " + e.getMessage());
			Executable.printHelp(opt);
			e.printStackTrace();
			System.exit(-1);
		}

	}

}
