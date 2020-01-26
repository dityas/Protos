/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.SparseFullBeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.legacy.AlphaVector;
import thinclab.legacy.DD;
import thinclab.legacy.NextBelState;
import thinclab.legacy.OP;
import thinclab.parsers.IPOMDPParser;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestBenchmarkDPBackups {

	public String l1DomainFile;
	private static Logger LOGGER;

	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		LOGGER = Logger.getLogger(TestBenchmarkDPBackups.class);
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testDPBackups() throws Exception {
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/cybersec.5S.2O.L1.2F.domain");
		
		
		parser.parseDomain();
		
		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10);
		
		LOGGER.debug("TAU contains " + ipomdp.currentTau.getNumLeaves() + " DD nodes");

		SparseFullBeliefExpansion BE = new SparseFullBeliefExpansion(ipomdp, 10);
		BE.expand();
		List<DD> exploredBeliefs = BE.getBeliefPoints();
		
		LOGGER.debug("Found " + exploredBeliefs.size() + " beliefs");
		
		OnlineInteractiveSymbolicPerseus solver = 
				new OnlineInteractiveSymbolicPerseus(
						ipomdp, 
						BE, 1, 100);
		
		DD[] alphaVectors = solver.getAlphaVectors();
		LOGGER.debug("Making primed V");
		
		/* make primed V */
		DD[] primedV = new DD[alphaVectors.length];
		
		for (int i = 0; i < alphaVectors.length; i++) {
			primedV[i] = 
					OP.primeVars(
							alphaVectors[i], 
							ipomdp.S.size() + ipomdp.Omega.size());
		}
		
		/* compute maxAbsVal */
		double maxAbsVal = 
				Math.max(
						OP.maxabs(
								IPOMDP.concatenateArray(
										OP.maxAllN(alphaVectors), 
										OP.minAllN(alphaVectors))), 1e-10);
		
		DD[][] factoredBeliefs = ipomdp.factorBeliefRegion(exploredBeliefs);
		
		List<Double> times = new ArrayList<Double>();
		
		/* do all those iterations of DP backup and check computation time */
		for (int i = 0; i < exploredBeliefs.size(); i++) {
			
			long then = System.nanoTime();
			
			AlphaVector vec = 
					AlphaVector.dpBackup(
							ipomdp, 
							exploredBeliefs.get(i), 
							factoredBeliefs[i], primedV, maxAbsVal, 
							alphaVectors.length);
			
			long now = System.nanoTime();
			times.add((double) (now - then) / 1000000);
		}
		
		LOGGER.debug("DP backup took " + times.stream().mapToDouble(a -> a).average().getAsDouble() 
				+ " msec");
		
	}

}
