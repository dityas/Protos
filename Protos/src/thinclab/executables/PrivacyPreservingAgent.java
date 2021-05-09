/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.executables;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.belief.SparseFullBeliefExpansion;
import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.MAPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.parsers.IPOMDPParser;
import thinclab.parsers.ParseSPUDD;
import thinclab.representations.policyrepresentations.PolicyGraph;
import thinclab.representations.policyrepresentations.PolicyTree;
import thinclab.simulations.MultiAgentSimulation;
import thinclab.simulations.StochasticSimulation;
import thinclab.solvers.AlphaVectorPolicySolver;
import thinclab.solvers.BaseSolver;
import thinclab.solvers.DefaultActionPolicySolver;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.solvers.RandomActionPolicySolver;
import thinclab.solvers.RandomizedResponsePrivacySolver;
import thinclab.solvers.basiccyberdeceptionsolvers.ReactiveSolver;
import thinclab.utils.CustomConfigurationFactory;
import thinclab.utils.NextBelStateCache;

/*
 * @author adityas
 *
 */
public class PrivacyPreservingAgent extends Executable {
	
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
		
		/* backup iterations */
		opt.addOption("b", true, "number of backups in each round");
		
		/* solver rounds */
		opt.addOption("r", true, "number of symbolic perseus rounds (always 1 for IPOMDPs)");
		
		/* look ahead or search depth */
		opt.addOption("n", true, "look ahead for IPOMDPs / SSGA depth for POMDPs");
		
		/* simulation rounds */
		opt.addOption("y", true, "number of simulation rounds");
		
		/* main agent frame index */
		opt.addOption("i", true, "main agent frame index");
		
		/* main agent frame index */
		opt.addOption("s", true, "solutions dir");
		
		/* randomization probability */
		opt.addOption("p", true, "randomization probability");
		
		CommandLine line = null;

		try {
			line = cliParser.parse(opt, args);
			
			/* set CLI args */
			
			/* output directory */
			String storageDir = line.getOptionValue("s");
			
			CustomConfigurationFactory.initializeLogging();
			LOGGER = Logger.getLogger(RunSimulations.class);
			
			/* set domain file */
			String domainFile = line.getOptionValue("d");
			
			/* set look ahead */
			int backups = Integer.parseInt(line.getOptionValue("b"));
			
			/* set look ahead */
			int lookAhead = Integer.parseInt(line.getOptionValue("n"));
			
			LOGGER.info("Simulating POMDP...");
			NextBelStateCache.useCache();
			
			int rounds = new Integer(line.getOptionValue("r"));
			int simRounds = new Integer(line.getOptionValue("y"));
//			int simLength = new Integer(line.getOptionValue("x"));
			int mainAgentFrame = Integer.parseInt(line.getOptionValue("i"));
			float prob = 0.0f;
			
			float[] probs = new float[] {0.01f, 0.05f, 0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.35f, 0.4f, 0.45f, 0.5f};
			
			// Run single shot game
			for (float p : probs) {
				
				prob = p;
				
				storageDir = "./single_shot_game/";
				
				String storageSubDir = "prob_" + p;
				File storage = new File(storageDir + "/" + storageSubDir + "/");
				storage.mkdirs();
				
				/* run simulation for simRounds */
				for (int i = 0; i < simRounds; i++) {
					
					LOGGER.info("Starting simulation round " + i);
					
					/* initialize IPOMDP parser */
					IPOMDPParser parser = new IPOMDPParser(domainFile);
					parser.parseDomain();
					
					// Make list of all frames
					List<DecisionProcess> frameList = new ArrayList<>();
					
					for (ParseSPUDD parsedFrame : parser.childFrames) {
						
						POMDP pomdp = new POMDP();
						pomdp.initializeFromParsers(parsedFrame);
						
						frameList.add(pomdp);
					}
					
					BaseSolver solver = new RandomizedResponsePrivacySolver(frameList, mainAgentFrame, 1.0f - prob, rounds, backups);
					solver.solve();
					
					// Init Observer
					
					IPOMDPParser iParser = new IPOMDPParser(domainFile);
					iParser.parseDomain();
					
					IPOMDP ipomdp = new IPOMDP(iParser, lookAhead, 10, 1.0f - prob);
					
					BeliefRegionExpansionStrategy BE = new SSGABeliefExpansion(ipomdp, 30);
					int numRounds = 5;
					
					BaseSolver iSolver = 
							new OnlineInteractiveSymbolicPerseus(
									ipomdp, 
									BE, numRounds, backups);
					
					MultiAgentSimulation ss = new MultiAgentSimulation(iSolver, solver, 2);
					
					ss.runSimulation();
					
					ss.logToFile(storage.getPath() + "/" + "sim" + i + ".json");
					ss.writeDotFile(storage.getPath(), "sim" + i);
				}
			}
			
			// Run repeated game
			for (float p : probs) {
				
				prob = p;
				
				storageDir = "./repeated_game/";
				
				String storageSubDir = "prob_" + p;
				File storage = new File(storageDir + "/" + storageSubDir + "/");
				storage.mkdirs();
				
				/* run simulation for simRounds */
				for (int i = 0; i < simRounds; i++) {
					
					LOGGER.info("Starting simulation round " + i);
					
					/* initialize IPOMDP parser */
					IPOMDPParser parser = new IPOMDPParser(domainFile);
					parser.parseDomain();
					
					// Make list of all frames
					List<DecisionProcess> frameList = new ArrayList<>();
					
					for (ParseSPUDD parsedFrame : parser.childFrames) {
						
						POMDP pomdp = new POMDP();
						pomdp.initializeFromParsers(parsedFrame);
						
						frameList.add(pomdp);
					}
					
					BaseSolver solver = new RandomizedResponsePrivacySolver(frameList, mainAgentFrame, 1.0f - prob, rounds, backups);
					solver.solve();
					
					// Init Observer
					
					IPOMDPParser iParser = new IPOMDPParser(domainFile);
					iParser.parseDomain();
					
					IPOMDP ipomdp = new IPOMDP(iParser, lookAhead, 10, 1.0f - prob);
					
					BeliefRegionExpansionStrategy BE = new SSGABeliefExpansion(ipomdp, 30);
					int numRounds = 5;
					
					BaseSolver iSolver = 
							new OnlineInteractiveSymbolicPerseus(
									ipomdp, 
									BE, numRounds, backups);
					
					MultiAgentSimulation ss = new MultiAgentSimulation(iSolver, solver, 5);
					
					ss.runSimulation();
					
					ss.logToFile(storage.getPath() + "/" + "sim" + i + ".json");
					ss.writeDotFile(storage.getPath(), "sim" + i);
				}
			}
			
			
//			/* for IPOMDPs */
//			else if (line.hasOption("i")) {
//				
//				/* set NextBelState Caching */
//				NextBelStateCache.useCache();
//				
//				LOGGER.info("Simulating IPOMDP...");
//				
//				int simRounds = new Integer(line.getOptionValue("y"));
//				int simLength = new Integer(line.getOptionValue("x"));
//				double mergeThreshold = 0.0;
//				
//				/* run simulation for simRounds */
//				for (int i = 0; i < simRounds; i++) {
//					
//					LOGGER.info("Starting simulation round " + i);
//					
//					/* initialize IPOMDP */
//					IPOMDPParser parser = new IPOMDPParser(domainFile);
//					parser.parseDomain();
//					
//					IPOMDP ipomdp;
//					
//					if (mergeThreshold > 0.0)
//						ipomdp = new IPOMDP(parser, lookAhead, simLength, 0.0);
//					
//					else ipomdp = new IPOMDP(parser, lookAhead, simLength);
//					
//					Random rng = new Random();
//					
//					int frameSample = rng.nextInt(ipomdp.lowerLevelSolutions.size());
//					
//					for (BaseSolver solver : ipomdp.lowerLevelSolutions) {
//						
//						/* set context */
//						solver.f.setGlobals();
//						
//						/* make policy graph */
//						PolicyGraph G = new PolicyGraph((AlphaVectorPolicySolver) solver, simLength);
//						G.makeGraph();
//						G.computeEU();
//						
//						PolicyTree T = new PolicyTree((AlphaVectorPolicySolver) solver, 5);
//						T.buildTree();
//						
//						/* store policy graph solution */
//						G.writeDotFile(
//								storageDir, 
//								"policy_graph_frame_" + G.solver.f.frameID + "_" + i);
//						
//						T.writeDotFile(
//								storageDir, 
//								"policy_tree_frame_" + T.solver.f.frameID + "_" + i);
//						
//						G.writeJSONFile(
//								storageDir, 
//								"policy_graph_frame_" + G.solver.f.frameID + "_" + i);
//					}
//					
//					/* store ref to agent J */
//					BaseSolver jSolver = ipomdp.lowerLevelSolutions.get(frameSample);
//					
//					ipomdp.clearLowerLevelSolutions();
//					
//					/* set context back to IPOMDP */
//					ipomdp.setGlobals();
//					
//					BeliefRegionExpansionStrategy BE;
//					int numRounds = 1;
//					
//					if (line.hasOption("e")) {
//						BE = new SSGABeliefExpansion(ipomdp, 30);
//						numRounds = 5;
//					}
//					
//					else {
//						BE = new SparseFullBeliefExpansion(ipomdp, 10);
//						numRounds = 1;
//					}
//					
//					BaseSolver solver = null;
//					
//					if (line.hasOption('j')) {
//						
//						String action = line.getOptionValue('j');
//						
//						if (action == null)
//							action = ipomdp.lowerLevelGuessForAi;
//						
//						solver = new DefaultActionPolicySolver(ipomdp, action);
//					}
//					
//					else if (line.hasOption('k'))
//						solver = new RandomActionPolicySolver(ipomdp);
//					
//					else if (line.hasOption('q'))
//						solver = new ReactiveSolver(ipomdp);
//					
//					/* Agent i */
//					else {
//						solver = 
//							new OnlineInteractiveSymbolicPerseus(
//									ipomdp, 
//									BE, numRounds, backups);
//					}
//					
//					MultiAgentSimulation ss = new MultiAgentSimulation(solver, jSolver, simLength);
//
//					ss.setMjDotDir(storageDir, i);
//					ss.runSimulation();
//					
//					ss.logToFile(storageDir + "/" + "sim" + i + ".json");
//					ss.writeDotFile(storageDir, "sim" + i);
//				}
//			}
//			
			
		} 
		
		catch (Exception e) {
			System.out.println("While parsing args: " + e.getMessage());
			Executable.printHelp(opt);
			e.printStackTrace();
			System.exit(-1);
		}

	}

}
