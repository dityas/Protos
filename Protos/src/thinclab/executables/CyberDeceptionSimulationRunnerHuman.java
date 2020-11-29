/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.executables;

import java.io.File;
import java.util.HashMap;
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
import thinclab.legacy.Global;
import thinclab.parsers.IPOMDPParser;
import thinclab.representations.policyrepresentations.PolicyGraph;
import thinclab.representations.policyrepresentations.PolicyTree;
import thinclab.simulations.CyberDeceptionSimulation;
import thinclab.simulations.CyberDeceptionSimulationWithHuman;
import thinclab.simulations.StochasticSimulation;
import thinclab.solvers.AlphaVectorPolicySolver;
import thinclab.solvers.BaseSolver;
import thinclab.solvers.DefaultActionPolicySolver;
import thinclab.solvers.HumanAgentSolver;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.solvers.RandomActionPolicySolver;
import thinclab.solvers.basiccyberdeceptionsolvers.ReactiveSolver;
import thinclab.utils.CustomConfigurationFactory;
import thinclab.utils.HumanAgentInteractionLoggingConfigurationFactory;
import thinclab.utils.NextBelStateCache;

/*
 * @author adityas
 *
 */
public class CyberDeceptionSimulationRunnerHuman extends Executable {
	
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
				"use SSGA expansion? (5 perseus rounds and 30 iterations of exploration)");
		
		/* use default policy */
		opt.addOption(
				"j", 
				"default-policy", 
				true, 
				"use default policy for L1?");
		
		/* use cyber deception solver */
		opt.addOption(
				"q", 
				"cyberdec-reactive", 
				false, 
				"use reactive solver");
		
		/* use default policy */
		opt.addOption(
				"k", 
				"random-policy", 
				false, 
				"use random policy for L1?");
		
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
		
		opt.addOption(
				"c",
				"ip",
				true,
				"IP address of env connector");
		
		/* merge threshold */
		opt.addOption("m", "merge", true, "For MJ merge threshold");
		
		/* merge threshold */
		opt.addOption("I", "exit-on-init", true, "Exit after IPOMDP is initialized and serialized");
		
		CommandLine line = null;

		try {
			line = cliParser.parse(opt, args);
			
			/* set CLI args */
			
			/* output directory */
			String storageDir = line.getOptionValue("s");
			
			/* if log file is given, initialize logging accordingly */
			if (line.hasOption("l"))
				HumanAgentInteractionLoggingConfigurationFactory.setLogFileName(
						storageDir + "/solver.log");
			
			HumanAgentInteractionLoggingConfigurationFactory.initializeLogging();
			LOGGER = Logger.getLogger(RunSimulations.class);
			
			/* set domain file */
			String domainFile = line.getOptionValue("d");
			
			/* get IP address */
			String ipAddr = line.getOptionValue("c");
			
			/* set look ahead */
			int backups = new Integer(line.getOptionValue("b"));
			
			/* set look ahead */
			int lookAhead = new Integer(line.getOptionValue("n"));
			
			/* conditional plan and policy graph */
			if (line.hasOption("p")) {
				LOGGER.info("Simulating POMDP...");
				NextBelStateCache.useCache();
				
				int rounds = new Integer(line.getOptionValue("r"));
				int simRounds = new Integer(line.getOptionValue("y"));
				int simLength = new Integer(line.getOptionValue("x"));
				
				/* run simulation for simRounds */
				for (int i = 0; i < simRounds; i++) {
					
					LOGGER.info("Starting simulation round " + i);
					
					POMDP pomdp = new POMDP(domainFile);
					OfflineSymbolicPerseus solver = 
							OfflineSymbolicPerseus.createSolverWithSSGAExpansion(
									pomdp, lookAhead, 30, rounds, backups);
					
					solver.solve();
					
					PolicyTree T = new PolicyTree(solver, simLength);
					T.buildTree();
					T.computeEU();
					T.writeDotFile(storageDir, "policy_tree" + i);
					T.writeJSONFile(storageDir, "policy_tree" + i);
					
					StochasticSimulation ss = new StochasticSimulation(solver, simLength);
					ss.runSimulation();
					ss.logToFile(storageDir + "/" + "sim" + i + ".json");
					ss.writeDotFile(storageDir, "sim" + i);
					
				}
			}
			
			/* for IPOMDPs */
			else if (line.hasOption("i")) {
				
				/* set NextBelState Caching */
				NextBelStateCache.useCache(storageDir);
				Global.storagDir = storageDir;
				
				new ProcessBuilder("clear").inheritIO().start().waitFor();
				System.out.println("Starting session...");
				System.out.println("Please be patient. This will take around 20 mins...");
				
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
					
					IPOMDP ipomdp = null;
					
					if (new File(storageDir + "/ipomdp.obj").exists()) {
						try {
							
							ipomdp = IPOMDP.loadIPOMDP(storageDir + "/ipomdp.obj");
							LOGGER.info("IPOMDP loaded from " + storageDir + "/ipomdp.obj");
						} 
						
						catch (Exception e) {
							LOGGER.error("Could not load IPOMDP from " + storageDir);
							ipomdp = null;
						}
					}
					
					if (ipomdp == null) {
					
						if (mergeThreshold > 0.0)
							ipomdp = new IPOMDP(parser, lookAhead, simLength, mergeThreshold);
						
						else ipomdp = new IPOMDP(parser, lookAhead, simLength, "/tmp/");
					
						Random rng = new Random();
						
						int frameSample = rng.nextInt(ipomdp.lowerLevelSolutions.size());
						
						for (BaseSolver solver : ipomdp.lowerLevelSolutions) {
							
							/* set context */
							solver.f.setGlobals();
							
							/* make policy graph */
							PolicyGraph G = new PolicyGraph((AlphaVectorPolicySolver) solver, 5);
							G.makeGraph();
							G.computeEU();
							
							PolicyTree T = new PolicyTree((AlphaVectorPolicySolver) solver, 5);
							T.buildTree();
							
							/* store policy graph solution */
							G.writeDotFile(
									storageDir, 
									"policy_graph_frame_" + G.solver.f.frameID + "_" + i);
							
							T.writeDotFile(
									storageDir, 
									"policy_tree_frame_" + T.solver.f.frameID + "_" + i);
							
							G.writeJSONFile(
									storageDir, 
									"policy_graph_frame_" + G.solver.f.frameID + "_" + i);
						}
						
						// Save the IPOMDP
						IPOMDP.saveIPOMDP(ipomdp, storageDir + "/ipomdp.obj");
						
						if (line.hasOption('I')) {
							LOGGER.info("IPOMDP initialized and serialized. Exiting...");
							System.exit(0);
						}
						
						ipomdp = IPOMDP.loadIPOMDP(storageDir + "/ipomdp.obj");
					}
					
					/* store ref to agent J */
					HashMap<String, String> humanActionsToPOMDPActions = 
							new HashMap<String, String>();
					
					/* actions */
					humanActionsToPOMDPActions.put("WHOAMI", "CHECK_ROOT");
//					humanActionsToPOMDPActions.put("CHECK_ROOT", "CHECK_ROOT");
					humanActionsToPOMDPActions.put("FIND_SEC_CONFIG_HOMEDIR", "FILE_RECON_CDATA");
					humanActionsToPOMDPActions.put("FIND_SEC_CONFIG_ROOTDIR", "FILE_RECON_CDATA");
					humanActionsToPOMDPActions.put("FIND_SECRET_FILE_HOMEDIR", "FILE_RECON_SDATA");
					humanActionsToPOMDPActions.put("FIND_SECRET_FILE_ROOTDIR", "FILE_RECON_SDATA");
					humanActionsToPOMDPActions.put("MANIPULATE_SEC_CONFIG", "MANIPULATE_DATA");
					humanActionsToPOMDPActions.put("ADD_USER", "PERSIST");
					humanActionsToPOMDPActions.put("EDIT_BASHRC", "PERSIST");
					humanActionsToPOMDPActions.put("CHECK_KERNEL_VERSION", "VULN_RECON");
					humanActionsToPOMDPActions.put("CHECK_PS", "VULN_RECON");
					humanActionsToPOMDPActions.put("RUN_OFS_EXPLOIT", "PRIV_ESC");
					humanActionsToPOMDPActions.put("RUN_VSFTPD_EXPLOIT", "PRIV_ESC");
					humanActionsToPOMDPActions.put("EXIT", "EXIT");
					humanActionsToPOMDPActions.put("DOWNLOAD_SECRET_FILE", "START_EXFIL");
					
					/* help strings */
					HashMap<String, String> helpStrings = 
							new HashMap<String, String>();
					
					helpStrings.put(
							"WHOAMI", 
							"\t\t\t show current user");
//					helpStrings.put("CHECK_ROOT", "same as whoami");
					helpStrings.put(
							"FIND_SEC_CONFIG_HOMEDIR", 
							" look for secret_config.txt (NOT secret.txt) in /home. "
							+ "Same as running 'find /home -name secret_config.txt'");
					helpStrings.put(
							"FIND_SEC_CONFIG_ROOTDIR", 
							" look for secret_config.txt (NOT secret.txt) in /root. "
							+ "Same as running 'find /root -name secret_config.txt'");
					helpStrings.put(
							"FIND_SECRET_FILE_HOMEDIR", 
							" look for secret.txt (NOT secret_config.txt) in /home. "
							+ "Same as running 'find /home -name secret.txt'");
					helpStrings.put(
							"FIND_SECRET_FILE_ROOTDIR", 
							" look for secret.txt (NOT secret_config.txt) in /root. "
							+ "Same as running 'find /root -name secret.txt'");
					helpStrings.put(
							"MANIPULATE_SEC_CONFIG", 
							"\t manipulate secret_config.txt"
							+ "(will only work if secret_config.txt has been found.)");
					helpStrings.put(
							"ADD_USER", 
							"\t\t create new sudo user. (Will only be successful if you are root)");
					helpStrings.put(
							"EDIT_BASHRC", 
							"\t\t persist through root's bashrc. (Will only be successful if you are root)");
					helpStrings.put(
							"CHECK_KERNEL_VERSION", 
							"\t check kernel version for ofs exploit");
					helpStrings.put(
							"CHECK_PS", 
							"\t\t check running processes ");
					helpStrings.put(
							"RUN_OFS_EXPLOIT", 
							"\t run OFS exploit (only works on kernel version "
							+ "3.13.0). "
							+ "IF SUCCESSFUL THIS ACTION ELEVATES YOUR PRIVILEGES TO ROOT");
					helpStrings.put(
							"RUN_VSFTPD_EXPLOIT", 
							" run VSFTPD exploit (only works if process "
							+ "'vsftpd' is running). "
							+ "IF SUCCESSFUL THIS ACTION ELEVATES YOUR PRIVILEGES TO ROOT");
					helpStrings.put(
							"EXIT", 
							"\t\t\t stop attacker agent. (To conclude the engagement.)");
					helpStrings.put(
							"DOWNLOAD_SECRET_FILE",
							"\t download secret.txt from target computer (will only download"
							+ "if the file has been found).");
					
					HumanAgentSolver jSolver = 
							new HumanAgentSolver(humanActionsToPOMDPActions, helpStrings);
					
					ipomdp.clearLowerLevelSolutions();
					
					/* set context back to IPOMDP */
					ipomdp.setGlobals();
					
					BeliefRegionExpansionStrategy BE;
					int numRounds = 1;
					
					if (line.hasOption("e")) {
						BE = new SSGABeliefExpansion(ipomdp, 30);
						numRounds = 5;
					}
					
					else {
						BE = new SparseFullBeliefExpansion(ipomdp, 10);
						numRounds = 1;
					}
					
					BaseSolver solver = null;
					
					if (line.hasOption('j')) {
						
						String action = line.getOptionValue('j');
						
						if (action == null)
							action = ipomdp.lowerLevelGuessForAi;
						
						solver = new DefaultActionPolicySolver(ipomdp, action);
					}
					
					else if (line.hasOption('k'))
						solver = new RandomActionPolicySolver(ipomdp);
					
					else if (line.hasOption('q'))
						solver = new ReactiveSolver(ipomdp);
					
					/* Agent i */
					else {
						solver = 
							new OnlineInteractiveSymbolicPerseus(
									ipomdp, 
									BE, numRounds, backups);
					}
					
					CyberDeceptionSimulationWithHuman ss = 
							new CyberDeceptionSimulationWithHuman(
									solver, jSolver, simLength, ipAddr, 2004);

					ss.setMjDotDir(storageDir, i);
					ss.runSimulation();
					
					ss.logToFile(storageDir + "/" + "sim" + i + ".json");
					ss.writeDotFile(storageDir, "sim" + i);
					
					System.out.println("Engagement terminated. Thank you for your time.");
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
