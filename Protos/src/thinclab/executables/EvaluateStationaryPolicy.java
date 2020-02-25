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

import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.belief.SparseFullBeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.parsers.IPOMDPParser;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;
import thinclab.utils.NextBelStateCache;

/*
 * @author adityas
 *
 */
public class EvaluateStationaryPolicy extends Executable {
	
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
		opt.addOption("n", false, "look ahead for IPOMDPs / SSGA depth for POMDPs");
		
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
				LOGGER.info("Solving POMDP...");
				
				int rounds = new Integer(line.getOptionValue("r"));
				
				POMDP pomdp = new POMDP(domainFile);
				OfflineSymbolicPerseus solver = 
						OfflineSymbolicPerseus.createSolverWithSSGAExpansion(
								pomdp, lookAhead, 2, rounds, backups);
				
				solver.solve();
			}
			
			/* for IPOMDPs */
			else if (line.hasOption("i")) {
				
				/* set NextBelState Caching */
				NextBelStateCache.useCache();
				LOGGER.info("Solving IPOMDP...");
				
				/* initialize IPOMDP */
				IPOMDPParser parser = new IPOMDPParser(domainFile);
				parser.parseDomain();
				
				IPOMDP ipomdp;
				
				ipomdp = new IPOMDP(parser, lookAhead, 10);
				ipomdp.clearLowerLevelSolutions();
				
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
				
				/* Agent i */
				OnlineInteractiveSymbolicPerseus solver = 
						new OnlineInteractiveSymbolicPerseus(
								ipomdp, 
								BE, numRounds, backups);
				
				solver.solveCurrentStep();
				LOGGER.info("Calculated policy value is: " +
						ipomdp.evaluatePolicy(
								solver.getAlphaVectors(), 
								solver.getPolicy(), 
								100000, lookAhead, false));
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
