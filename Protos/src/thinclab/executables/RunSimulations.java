/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.executables;

import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.belief.SparseFullBeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.parsers.IPOMDPParser;
import thinclab.representations.conditionalplans.ConditionalPlanTree;
import thinclab.representations.policyrepresentations.PolicyGraph;
import thinclab.simulations.MultiAgentSimulation;
import thinclab.simulations.StochasticSimulation;
import thinclab.solvers.BaseSolver;
import thinclab.solvers.OfflinePBVISolver;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;
import thinclab.utils.NextBelStateCache;

/*
 * @author adityas
 *
 */
public class RunSimulations extends Executable {
	
	/*
	 * Run POMDP or IPOMDP simulations for the given domain
	 */
	
	private static Logger LOGGER;
	
	// -----------------------------------------------------------------------------------------
	
	public static void main(String[] args) {
		
		/* Parse CMD args */
		CommandLineParser cliParser = new DefaultParser();
		Options opt = new Options();
		
		/* domain file */
		opt.addOption("d", true, "path to the domain file");
		
		/* log file */
		opt.addOption("l", false, "log to file in results dir?");
		
		/* backup iterations */
		opt.addOption("b", true, "number of backups in each round");
		
		/* solver rounds */
		opt.addOption("r", true, "number of symbolic perseus rounds (always 1 for IPOMDPs)");
		
		/* look ahead or search depth */
		opt.addOption("n", true, "look ahead for IPOMDPs / SSGA depth for POMDPs");
		
		/* simulation rounds */
		opt.addOption("y", true, "number of simulation rounds");
		
		/* Use SSGA? */
		opt.addOption(
				"e", 
				"ssga", 
				false, 
				"use SSGA expansion? (5 perseus rounds and 10 iterations of exploration)");
		
		/* simulation switch */
		opt.addOption(
				"x",
				"sim",
				true,
				"run stochastic simulation for given iterations");
		
		/* results storage directory */
		opt.addOption(
				"s",
				"output",
				true,
				"directory where result files are to be stored. (Should be an existing dir)");
		
		/* POMDP or IPOMDP switch */
		opt.addOption(
				"p",
				"pomdp",
				false,
				"set if the domain is a POMDP domain");
		
		opt.addOption(
				"i",
				"ipomdp",
				false,
				"set if the domain is a IPOMDP domain");
		
		/* merge threshold */
		opt.addOption("m", "merge", true, "For MJ merge threshold");
		
		CommandLine line = null;

		try {
			line = cliParser.parse(opt, args);
			
			/* set CLI args */
			
			/* output directory */
			String storageDir = line.getOptionValue("s");
			
			/* if log file is given, initialize logging accordingly */
			if (line.hasOption("l"))
				CustomConfigurationFactory.setLogFileName(storageDir + "/solver.log");
			
			CustomConfigurationFactory.initializeLogging();
			LOGGER = Logger.getLogger(RunSimulations.class);
			
			/* set domain file */
			String domainFile = line.getOptionValue("d");
			
			/* set look ahead */
			int backups = new Integer(line.getOptionValue("b"));
			
			/* set look ahead */
			int lookAhead = new Integer(line.getOptionValue("n"));
			
			/* conditional plan and policy graph */
			if (line.hasOption("p")) {
				LOGGER.info("Simulating POMDP...");
				
				int rounds = new Integer(line.getOptionValue("r"));
				int simRounds = new Integer(line.getOptionValue("y"));
				int simLength = new Integer(line.getOptionValue("x"));
				
				/* run simulation for simRounds */
				for (int i = 0; i < simRounds; i++) {
					
					LOGGER.info("Starting simulation round " + i);
					
					POMDP pomdp = new POMDP(domainFile);
					OfflineSymbolicPerseus solver = 
							OfflineSymbolicPerseus.createSolverWithSSGAExpansion(
									pomdp, lookAhead, 2, rounds, backups);
					
					solver.solve();
					
					PolicyGraph pg = new PolicyGraph((OfflinePBVISolver) solver);
					pg.makeGraph();
					pg.computeEU();
					
					pg.writeDotFile(storageDir, "policy_graph_" + i);
					pg.writeJSONFile(storageDir, "policy_graph_" + i);
					
					StochasticSimulation ss = new StochasticSimulation(solver, simLength);
					ss.runSimulation();
					ss.logToFile(storageDir + "/" + "sim" + i + ".json");
					ss.writeDotFile(storageDir, "sim" + i);
					
				}
			}
			
			/* for IPOMDPs */
			else if (line.hasOption("i")) {
				
				/* set NextBelState Caching */
				NextBelStateCache.useCache();
				
				LOGGER.info("Simulating IPOMDP...");
				
				int simRounds = new Integer(line.getOptionValue("y"));
				int simLength = new Integer(line.getOptionValue("x"));
				double mergeThreshold = 0.0;
				
				if (line.hasOption("m"))
					mergeThreshold = new Double(line.getOptionValue("m"));
				
				/* run simulation for simRounds */
				for (int i = 0; i < simRounds; i++) {
					
					LOGGER.info("Starting simulation round " + i);
					
					/* initialize IPOMDP */
					IPOMDPParser parser = new IPOMDPParser(domainFile);
					parser.parseDomain();
					
					IPOMDP ipomdp;
					
					if (mergeThreshold > 0.0)
						ipomdp = new IPOMDP(parser, lookAhead, simLength * 2, mergeThreshold);
					
					else ipomdp = new IPOMDP(parser, lookAhead, simLength * 2);
					
					Random rng = new Random();
					
					int frameSample = rng.nextInt(ipomdp.lowerLevelSolutions.size());
					
					for (BaseSolver solver : ipomdp.lowerLevelSolutions) {
						
						/* set context */
						solver.f.setGlobals();
						
						/* make policy graph */
						PolicyGraph pg = new PolicyGraph((OfflinePBVISolver) solver);
						pg.makeGraph();
						
						/* store policy graph solution */
						pg.writeDotFile(
								storageDir, 
								"policy_graph_frame_" + pg.solver.f.frameID + "_" + i);
						
						pg.writeJSONFile(
								storageDir, 
								"policy_graph_frame_" + pg.solver.f.frameID + "_" + i);
						
						/* make conditional plan */
						ConditionalPlanTree T = new ConditionalPlanTree(solver, simLength);
						T.buildTree();
						
						/* Store conditional Plan */
						T.writeDotFile(
								storageDir, 
								"plan_frame_" + T.f.frameID + "_" + i);
						
						T.writeJSONFile(
								storageDir, 
								"plan_frame_" + T.f.frameID + "_" + i);
					}
					
					/* store ref to agent J */
					BaseSolver jSolver = ipomdp.lowerLevelSolutions.get(frameSample);
					
					ipomdp.clearLowerLevelSolutions();
					
					/* set context back to IPOMDP */
					ipomdp.setGlobals();
					
					BeliefRegionExpansionStrategy BE;
					int numRounds = 1;
					
					if (line.hasOption("e")) {
						BE = new SSGABeliefExpansion(ipomdp, 10);
						numRounds = 5;
					}
					
					else {
						BE = new SparseFullBeliefExpansion(ipomdp, 10);
						numRounds = 1;
					}
					
					/* Agent i */
					OnlineInteractiveSymbolicPerseus solver = 
							new OnlineInteractiveSymbolicPerseus(
									ipomdp, 
									BE, numRounds, backups);
					
					MultiAgentSimulation ss = new MultiAgentSimulation(solver, jSolver, simLength);
//					ss.setMjDotDir(storageDir);
					ss.runSimulation();
					
					ss.logToFile(storageDir + "/" + "sim" + i + ".json");
					ss.writeDotFile(storageDir, "sim" + i);
				}
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
