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

import thinclab.belief.FullBeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.parsers.IPOMDPParser;
import thinclab.representations.conditionalplans.WalkablePolicyTree;
import thinclab.solvers.OnlineIPBVISolver;
import thinclab.solvers.OnlineSolver;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
public class IPOMDPSolver extends Executable {
	
	/*
	 * Entry point for POMDP solver using CLI
	 */
	
	/* POMDP information */
	public IPOMDP ipomdp;
	public String domainFile;
	
	/* Solver params */
	public int perseusRounds;
	public int numDpBackups;
	public int lookAhead;
	public int planDepth;
	
	/* conditional plan instance */
	public WalkablePolicyTree plan;
	
	/* solver instance */
	public OnlineSolver solver;
	
	// ---------------------------------------------------------------------------------------------
	
	public IPOMDPSolver(String fileName, int rounds, int dpBackups, int lookAhead, int planDepth) {
		
		/*
		 * Set class attributes
		 */
		
		this.domainFile = fileName;
		this.perseusRounds = rounds;
		this.numDpBackups = dpBackups;
		this.lookAhead = lookAhead;
		this.planDepth = planDepth;
	}
	
	public void buildPlan() {
		/*
		 * Starts the solver and waits for convergence or max rounds
		 */
		IPOMDPParser parser = new IPOMDPParser(this.domainFile);
		parser.parseDomain();
		
		this.ipomdp = new IPOMDP(parser, this.lookAhead, this.lookAhead);
		
		this.solver = 
				new OnlineIPBVISolver(
						this.ipomdp, 
						new FullBeliefExpansion(this.ipomdp), 
						1, this.numDpBackups);
		
		this.plan = new WalkablePolicyTree(solver, this.planDepth);
		this.plan.buildTree();
	}
	
	public void makeConditionalPlan(String dirName) {
		/*
		 * Starts the visualizer and shows the policy graph in JUNG
		 */
		this.plan.writeDotFile(dirName, "plan");
		this.plan.writeJSONFile(dirName, "plan");
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
		
		/* backup iterations */
		opt.addOption("b", true, "number of backups in each round");
		
		/* plan depth */
		opt.addOption("s", true, "depth of the conditional plan");
		
		/* look ahead steps */
		opt.addOption("h", true, "look ahead for online IPOMDP");
		
		/* Create conditional plan */
		opt.addOption(
				"p", 
				"plan", 
				true, 
				"make conditional plan for 10 time steps and "
				+ "create dot and JSON files in the given dir");
		
		CommandLine line = null;
		IPOMDPSolver solver = null;

		try {
			line = cliParser.parse(opt, args);
			
			/* set CLI args */
			
			/* if log file is given, initialize logging accordingly */
			if (line.hasOption("l"))
				CustomConfigurationFactory.setLogFileName(line.getOptionValue("l"));
			
			CustomConfigurationFactory.initializeLogging();
			
			/* set domain file */
			String domainFile = line.getOptionValue("d");
			
			/* set look ahead */
			int backups = new Integer(line.getOptionValue("b"));
			
			/* set search depth */
			int search = new Integer(line.getOptionValue("s"));
			
			/* set look ahead */
			int lookAhead = new Integer(line.getOptionValue("h"));
			
			solver = new IPOMDPSolver(domainFile, 1, backups, lookAhead, search);
			solver.buildPlan();
			
			/* conditional plan and policy graph */
			if (line.hasOption("p")) {
				String planDir = line.getOptionValue("p");
				solver.makeConditionalPlan(planDir);
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

