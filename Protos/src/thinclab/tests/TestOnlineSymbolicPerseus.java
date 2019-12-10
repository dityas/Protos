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
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.NextBelState;
import thinclab.legacy.OP;
import thinclab.parsers.IPOMDPParser;
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
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 3, 20);
		
		FullBeliefExpansion fb = 
				new FullBeliefExpansion(
						tigerL1IPOMDP);
		
		OnlineInteractiveSymbolicPerseus solver = 
				new OnlineInteractiveSymbolicPerseus(
						tigerL1IPOMDP, 
						fb, 
						1, 
						100);
		
		StochasticSimulation ss = new StochasticSimulation(solver, 2);
		ss.runSimulation();
		
		LOGGER.debug(ss.getDotString());
	}
	
	@Test
	void testIPOMDPNextBelStates() throws Exception {
		LOGGER.info("Testing IPOMDP next belstates");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1multiple);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 2, 20);
		
		HashMap<String, NextBelState> nextStates = 
				NextBelState.oneStepNZPrimeBelStates(
						tigerL1IPOMDP, 
						tigerL1IPOMDP.getCurrentBelief(), 
						true, 0.00000001);
		
		LOGGER.debug("Starting belief is " + tigerL1IPOMDP.getCurrentBelief().toDDTree());
		LOGGER.debug("All possible combinations are " 
				+ tigerL1IPOMDP.getAllPossibleObservations());
		
		DD obsDist = tigerL1IPOMDP.getObsDist(tigerL1IPOMDP.getCurrentBelief(), "listen");
		
		LOGGER.debug("Obs dist is " + obsDist);
		LOGGER.debug(Arrays.toString(OP.convert2array(obsDist, tigerL1IPOMDP.obsIVarPrimeIndices)));
		LOGGER.debug(Arrays.toString(Global.varDomSize));
		
		for (String action : tigerL1IPOMDP.getActions()) {
			
			NextBelState nextStateForAi = nextStates.get(action);
			
			for (int s = 0; s < nextStateForAi.nextBelStates.length; s++) {
				LOGGER.debug("For Ai=" + action + " and o=" 
						+ tigerL1IPOMDP.getAllPossibleObservations().get(s) 
						+ " belief is " + Arrays.toString(nextStateForAi.nextBelStates[s]));
			}
		}
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
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 4, 20);
		
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
