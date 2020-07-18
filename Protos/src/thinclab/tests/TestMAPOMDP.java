/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

//import static org.junit.jupiter.api.Assertions.*;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cern.colt.Arrays;
import thinclab.belief.FullBeliefExpansion;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.decisionprocesses.MAPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.legacy.DD;
import thinclab.parsers.IPOMDPParser;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestMAPOMDP {
	
	public String l1DomainFile;
	public String l0DomainFile;
	private static Logger LOGGER;

	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		LOGGER = Logger.getLogger(TestIPOMDP.class);
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt";
		this.l0DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void testInit() {
		LOGGER.info("Testing init");
		IPOMDPParser parser = new IPOMDPParser(l1DomainFile);
		parser.parseDomain();
		MAPOMDP mapomdp = new MAPOMDP(parser, 10);
		
		LOGGER.debug(mapomdp.getInitialBeliefs());
//		LOGGER.debug(mapomdp.Oi);
		
		FullBeliefExpansion BE = new FullBeliefExpansion(mapomdp, 5);
		BE.expand();
		
		for (DD belief: BE.exploredBeliefs) {
			LOGGER.debug(mapomdp.toMap(belief));
		}
		
		LOGGER.debug(Arrays.toString(mapomdp.varIndices));
		LOGGER.debug(Arrays.toString(mapomdp.primeVarIndices));
		LOGGER.debug(Arrays.toString(mapomdp.obsIndices));
		LOGGER.debug(Arrays.toString(mapomdp.primeObsIndices));
//		LOGGER.debug(mapomdp.Oi);
//		LOGGER.debug(mapomdp.Ti);
//		POMDP pomdp = new POMDP(this.l0DomainFile);
//		LOGGER.debug(pomdp.getObsDist(pomdp.currentBelief, "listen"));
		OfflineSymbolicPerseus solver = 
				new OfflineSymbolicPerseus((POMDP) mapomdp, new SSGABeliefExpansion(mapomdp, 10, 30), 
				20, 100);
		
		solver.solve();
		
//		PolicyGraph G = new PolicyGraph(solver, 5);
//		G.makeGraph();
//		
//		G.computeEU();
//		LOGGER.debug(mapomdp.evaluateDefaultPolicy("listen", 10000, 10, false));
//		LOGGER.debug(mapomdp.evaluateRandomPolicy(10000, 10, false));
//		
//		LOGGER.debug(G.getDotString());
//		
		LOGGER.debug(mapomdp.actions[0].rewFn);
		LOGGER.debug(mapomdp.actions[1].rewFn);
		LOGGER.debug(mapomdp.actions[2].rewFn);
//		
//		LOGGER.debug(mapomdp.R);
//		LOGGER.debug(mapomdp.costMap);
	}

}
