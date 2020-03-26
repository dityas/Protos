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
import org.apache.log4j.Logger;

import thinclab.belief.FullBeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.parsers.IPOMDPParser;
import thinclab.representations.conditionalplans.ConditionalPlanTree;
import thinclab.representations.policyrepresentations.PolicyGraph;
import thinclab.simulations.CyberDeceptionSimulation;
import thinclab.solvers.BaseSolver;
import thinclab.solvers.OfflinePBVISolver;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
public class CyberDeceptionAgent extends Executable {
	
	/*
	 * Defender agent for the cyber deception project
	 */
	
	private static Logger LOGGER;
	
	// ------------------------------------------------------------------------------------

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
		
		/* IP address and port of env */
		opt.addOption("a", "ip", true, "IP address for the env server");
		opt.addOption("t", "port", true, "port for the env server");
		
		/* simulation switch */
		opt.addOption(
				"x",
				"interactions",
				true,
				"run these many iterations");
		
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
			
			/* get IP address and port of the env server */
			String envIP = line.getOptionValue("a");
			int envPort = new Integer(line.getOptionValue("t"));
			
			/* conditional plan and policy graph */
			if (line.hasOption("p")) {
				LOGGER.info("Simulating POMDP...");
				
				int rounds = new Integer(line.getOptionValue("r"));
				int simLength = new Integer(line.getOptionValue("x"));
					
				LOGGER.info("Starting simulation");
				
				POMDP pomdp = new POMDP(domainFile);
				OfflineSymbolicPerseus solver = 
						OfflineSymbolicPerseus.createSolverWithSSGAExpansion(
								pomdp, lookAhead, 2, rounds, backups);
				
				solver.solve();
				
				CyberDeceptionSimulation cyberDecSim = 
						new CyberDeceptionSimulation(solver, simLength, envIP, envPort);
				
				cyberDecSim.runSimulation();
				cyberDecSim.logToFile(storageDir + "/" + "interaction.json");
				cyberDecSim.writeDotFile(storageDir, "interaction");
					
			}
			
			/* for IPOMDPs */
			else if (line.hasOption("i")) {
				
				LOGGER.info("Simulating IPOMDP...");
				int simLength = new Integer(line.getOptionValue("x"));
					
				LOGGER.info("Starting simulation");
				
				/* initialize IPOMDP */
				IPOMDPParser parser = new IPOMDPParser(domainFile);
				parser.parseDomain();
				
				IPOMDP ipomdp = new IPOMDP(parser, lookAhead, simLength * 2);
				
				for (BaseSolver solver : ipomdp.lowerLevelSolutions) {
					
					/* set context */
					solver.f.setGlobals();
					
					/* make policy graph */
					PolicyGraph pg = new PolicyGraph((OfflinePBVISolver) solver, simLength);
					pg.makeGraph();
					
					/* store policy graph solution */
					pg.writeDotFile(
							storageDir, 
							"policy_graph_frame_" + pg.solver.f.frameID);
					
					pg.writeJSONFile(
							storageDir, 
							"policy_graph_frame_" + pg.solver.f.frameID);
					
					/* make conditional plan */
					ConditionalPlanTree T = new ConditionalPlanTree(solver, simLength);
					T.buildTree();
					
					/* Store conditional Plan */
					T.writeDotFile(
							storageDir, 
							"plan_frame_" + T.f.frameID);
					
					T.writeJSONFile(
							storageDir, 
							"plan_frame_" + T.f.frameID);
				}
				
				ipomdp.clearLowerLevelSolutions();
				
				/* set context back to IPOMDP */
				ipomdp.setGlobals();
				
				OnlineInteractiveSymbolicPerseus solver = 
						new OnlineInteractiveSymbolicPerseus(
								ipomdp, 
								new FullBeliefExpansion(ipomdp), 1, backups);
				
				CyberDeceptionSimulation cyberDecSim = 
						new CyberDeceptionSimulation(solver, simLength, envIP, envPort);
				cyberDecSim.runSimulation();
				
				cyberDecSim.logToFile(storageDir + "/" + "interaction.json");
				cyberDecSim.writeDotFile(storageDir, "interaction");
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
