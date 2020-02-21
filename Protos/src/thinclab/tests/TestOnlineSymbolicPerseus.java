/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cern.colt.Arrays;
import thinclab.belief.FullBeliefExpansion;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.belief.SparseFullBeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.NextBelState;
import thinclab.legacy.OP;
import thinclab.parsers.IPOMDPParser;
import thinclab.simulations.MultiAgentSimulation;
import thinclab.simulations.StochasticSimulation;
import thinclab.solvers.OnlineIPBVISolver;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.solvers.OnlineSolver;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestOnlineSymbolicPerseus {

	public String l1DomainFile;
	public String l1multiple;
	public String tigerDom;
	
	private static Logger LOGGER;
	
	@BeforeEach
	void setUp() throws Exception {
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
		this.tigerDom = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.95.SPUDD.txt";
		this.l1multiple = 
				"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt";
//		this.l1multiple = 
//				"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/cybersec.5S.2O.L1.2F.domain";
		
		CustomConfigurationFactory.initializeLogging();
		LOGGER = Logger.getLogger(TestOnlineSymbolicPerseus.class);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testIPOMDPSymbolicPerseus() throws ZeroProbabilityObsException {

		
		LOGGER.info("Running testOnlineSymbolicPerseus()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1multiple);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 3, 10);
		
		FullBeliefExpansion fb = 
				new FullBeliefExpansion(
						tigerL1IPOMDP);
		
		OnlineInteractiveSymbolicPerseus solver = 
				new OnlineInteractiveSymbolicPerseus(
						tigerL1IPOMDP, 
						fb, 
						1, 
						100);
		
		MultiAgentSimulation ss = 
				new MultiAgentSimulation(solver, tigerL1IPOMDP.lowerLevelSolutions.get(0), 3);
		ss.runSimulation();
		
//		LOGGER.debug(ss.getDotString());
	}
	
	@Test
	void testIPOMDPNextBelStates() throws Exception {
		LOGGER.info("Testing IPOMDP next belstates");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1multiple);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 4, 20);
		
//		DD belief = tigerL1IPOMDP.getCurrentBelief();
//		LOGGER.debug(belief.toDDTree());
		
		for (int t = 0; t < 5; t++) {
			
			DD belief = tigerL1IPOMDP.getCurrentBelief();
			
			HashMap<String, NextBelState> nextStates = 
					NextBelState.oneStepNZPrimeBelStatesCached(
							tigerL1IPOMDP, 
							belief, 
							false, 0.00000001);
			
			LOGGER.debug("Starting belief is " + belief.toDDTree());
			LOGGER.debug("All possible combinations are " 
					+ tigerL1IPOMDP.getAllPossibleObservations());
			
			DD obsDist = tigerL1IPOMDP.getObsDist(belief, "listen");
//			DD obsDist = tigerL1IPOMDP.getObsDist(tigerL1IPOMDP.getCurrentBelief(), "NOP");
			
			LOGGER.debug("Obs dist is " + obsDist);
			LOGGER.debug(Arrays.toString(OP.convert2array(obsDist, tigerL1IPOMDP.obsIVarPrimeIndices)));
			LOGGER.debug(Arrays.toString(Global.varDomSize));
			
			for (String action : tigerL1IPOMDP.getActions()) {
				
				NextBelState nextStateForAi = nextStates.get(action);
				
				for (int s = 0; s < nextStateForAi.nextBelStates.length; s++) {
					LOGGER.debug("For Ai=" + action + " and o=" 
							+ tigerL1IPOMDP.getAllPossibleObservations().get(s) 
							+ " belief is " + Arrays.toString(nextStateForAi.nextBelStates[s]));
					
					DD nextBelief = 
							tigerL1IPOMDP.beliefUpdate(
									belief, 
									action, 
									tigerL1IPOMDP.obsCombinations.get(s).stream().toArray(String[]::new));
					
					LOGGER.debug("Factored belief is " 
							+ Arrays.toString(tigerL1IPOMDP.factorBelief(nextBelief)));
					
					for (int b = 0; b < nextStateForAi.nextBelStates[s].length - 1; b++) {
						
						DD marginal = nextStateForAi.nextBelStates[s][b];
						DD primedFactor = 
								OP.primeVars(tigerL1IPOMDP.factorBelief(nextBelief)[b], 
										tigerL1IPOMDP.S.size() + tigerL1IPOMDP.Omega.size());
										
						LOGGER.debug("Marginal is: " + marginal);
						LOGGER.debug("Primed factor is: " + primedFactor);
						
						assertTrue(OP.maxAll(OP.abs(OP.sub(primedFactor, marginal))) < 1e-8);
					}
					
//					belief = nextBelief;
				}
			}
			
			int[][] obsConfig = OP.sampleMultinomial(obsDist, tigerL1IPOMDP.obsIVarPrimeIndices);
			String[] obs = new String[obsConfig[0].length];
			for (int varI = 0; varI < obsConfig[0].length; varI ++) {
				obs[varI] = Global.valNames[obsConfig[0][varI] - 1][obsConfig[1][varI] - 1];
			}
			
			tigerL1IPOMDP.step(belief, "listen", obs);
//			tigerL1IPOMDP.step(tigerL1IPOMDP.getCurrentBelief(), "NOP", obs);
		}
	}
	
	@Test
	void testIPBVISolverWithSSGAExpansion() throws Exception {
		
		CustomConfigurationFactory.initializeLogging();
		
		LOGGER.info("Comparing Solver with different strats");
		
		String l1DomainFile = 
				"/home/adityas/UGA/THINCLab/DomainFiles/tiger.L1.F3.agnostic.domain";
		
		IPOMDPParser parser = new IPOMDPParser(l1DomainFile);
		parser.parseDomain();
		
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 4, 20);
		
		LOGGER.info("Starting with IPBVI with full belief expansion");
		long then = System.nanoTime();
		
		FullBeliefExpansion fb = 
				new FullBeliefExpansion(
						tigerL1IPOMDP);
		
		OnlineIPBVISolver solver = 
				new OnlineIPBVISolver(
						tigerL1IPOMDP, 
						fb, 1, 100);
		
		solver.solveCurrentStep();
		
		long now = System.nanoTime();
		
		long FBTime = ((now - then) / 1000000000);
		LOGGER.info("With FB expansion, time is: " + FBTime);
		
		parser = new IPOMDPParser(l1DomainFile);
		parser.parseDomain();
		
		tigerL1IPOMDP = new IPOMDP(parser, 4, 20);
		
		LOGGER.info("Starting with IPBVI with SSGA expansion");
		then = System.nanoTime();
		
		SSGABeliefExpansion ssgaB = new SSGABeliefExpansion(tigerL1IPOMDP, 5);
		
		OnlineIPBVISolver ssgaSolver = 
				new OnlineIPBVISolver(
						tigerL1IPOMDP, 
						ssgaB, 5, 100);
		
		ssgaSolver.solveCurrentStep();
		
		now = System.nanoTime();
		long SSGATime = ((now - then) / 1000000000);
		
		
		LOGGER.info("With FB expansion, time is: " + FBTime);
		LOGGER.info("With SSGA expansion, time is: " + SSGATime);
		
	}
	
	@Test
	void testISymbolicPerseusSolverWithSSGAExpansion() throws Exception {
		
		CustomConfigurationFactory.initializeLogging();
		
		LOGGER.info("Comparing Solver with different strats");
		
		String l1DomainFile = 
				"/home/adityas/UGA/THINCLab/DomainFiles/tiger.L1.F3.agnostic.domain";
		
		IPOMDPParser parser = new IPOMDPParser(l1DomainFile);
		parser.parseDomain();
		
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 4, 20);
		
		LOGGER.info("Starting with IPBVI with full belief expansion");
		long then = System.nanoTime();
		
		FullBeliefExpansion fb = 
				new FullBeliefExpansion(
						tigerL1IPOMDP);
		
		OnlineInteractiveSymbolicPerseus solver = 
				new OnlineInteractiveSymbolicPerseus(tigerL1IPOMDP, fb, 1, 100);
		
		solver.solveCurrentStep();
		
		long now = System.nanoTime();
		
		long FBTime = ((now - then) / 1000000000);
		LOGGER.info("With FB expansion, time is: " + FBTime);
		
		parser = new IPOMDPParser(l1DomainFile);
		parser.parseDomain();
		
		tigerL1IPOMDP = new IPOMDP(parser, 4, 20);
		
		LOGGER.info("Starting with IPBVI with SSGA expansion");
		then = System.nanoTime();
		
		SSGABeliefExpansion ssgaB = new SSGABeliefExpansion(tigerL1IPOMDP, 5);
		
		OnlineInteractiveSymbolicPerseus ssgaSolver = 
				new OnlineInteractiveSymbolicPerseus(tigerL1IPOMDP, ssgaB, 5, 100);
		
		ssgaSolver.solveCurrentStep();
		
		now = System.nanoTime();
		long SSGATime = ((now - then) / 1000000000);
		
		
		LOGGER.info("With FB expansion, time is: " + FBTime);
		LOGGER.info("With SSGA expansion, time is: " + SSGATime);
		
	}
	
	@Test
	void testOnlineIPBVI() throws ZeroProbabilityObsException {
//		CustomConfigurationFactory.setLogFileName("test.log");
		CustomConfigurationFactory.initializeLogging();
		System.out.println("Running testFHLASolverUtiliyComputation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1multiple);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 3, 20);
		
		FullBeliefExpansion fb = 
				new FullBeliefExpansion(
						tigerL1IPOMDP);
		
		OnlineIPBVISolver solver = 
				new OnlineIPBVISolver(
						tigerL1IPOMDP, 
						fb, 1, 100);
		
		solver.solveCurrentStep();
		
		solver.nextStep(
				solver.getActionAtCurrentBelief(), 
				tigerL1IPOMDP.obsCombinations.get(2));
		
		solver.solveCurrentStep();
		
		solver.nextStep(
				solver.getActionAtCurrentBelief(), 
				tigerL1IPOMDP.obsCombinations.get(2));
		
		solver.solveCurrentStep();
		
		solver.nextStep(
				solver.getActionAtCurrentBelief(), 
				tigerL1IPOMDP.obsCombinations.get(2));
	}

}
