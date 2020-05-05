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
import thinclab.representations.conditionalplans.ConditionalPlanTree;
import thinclab.representations.policyrepresentations.PolicyTree;
import thinclab.simulations.StochasticSimulation;
import thinclab.solvers.AlphaVectorPolicySolver;
import thinclab.solvers.OfflinePBVISolver;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;
import thinclab.utils.NextBelStateCache;

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
	public AlphaVectorPolicySolver solver;
	
	// ---------------------------------------------------------------------------------------------
	
	public POMDPSolver(String fileName, int rounds, int dpBackups, int searchDepth) {
		
		/*
		 * Set class attributes
		 */
		NextBelStateCache.useCache();
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
						new SSGABeliefExpansion(this.pomdp, this.searchDepth, 10), 
						this.perseusRounds, 
						this.numDpBackups);
		
		solver.solve();
	}
	
	public void makeConditionalPlan(String dirName) {
		/*
		 * Starts the visualizer and shows the policy graph in JUNG
		 */
		ConditionalPlanTree T = new ConditionalPlanTree(this.solver, 5);
		T.buildTree();
		
		T.writeDotFile(dirName, "plan");
		T.writeJSONFile(dirName, "plan");
	}
	
	public void makePolicyGraph(String dirName) {
		/*
		 * Makes the policy graph from the alpha vector policy
		 */
		
		PolicyTree T = new PolicyTree((OfflinePBVISolver) solver, 10);
		T.buildTree();
		T.computeEU();
		
		T.writeDotFile(dirName, "policy_graph");
		T.writeJSONFile(dirName, "policy_graph");
	}
	
	public void runSimulation(int trials, int iterations, String dirName) {
		/*
		 * Runs the simulation for given iterations
		 */
		
		for (int i = 0; i < trials; i++) {
			
			StochasticSimulation ss = new StochasticSimulation(this.solver, iterations);
			ss.runSimulation();
			
			ss.logToFile(dirName + "/" + "sim" + i + ".json");
			ss.writeDotFile(dirName, "sim" + i);
		}
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
		
		/* solution dir */
		opt.addOption(
				"o", 
				"output", 
				true, 
				"write all output in this dir");
		
		/* simulation switch */
		opt.addOption(
				"t",
				"sim",
				true,
				"run stochastic simulation for given iterations");
		
		/* number of simulations */
		opt.addOption(
				"u",
				"iter",
				true,
				"run stochastic simulation these many times");
		
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
			if (line.hasOption("o")) {
				
				String planDir = line.getOptionValue("o");
//				solver.makeConditionalPlan(planDir);
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
