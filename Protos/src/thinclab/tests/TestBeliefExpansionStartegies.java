/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.FullBeliefExpansion;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.belief.SparseFullBeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.parsers.IPOMDPParser;
import thinclab.solvers.OfflinePBVISolver;
import thinclab.solvers.OnlineIPBVISolver;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestBeliefExpansionStartegies {

	public String tigerDom;
	public POMDP pomdp;
	
	private static Logger LOGGER;
	
	@BeforeEach
	void setUp() throws Exception {
		
		CustomConfigurationFactory.initializeLogging();
		
		LOGGER = Logger.getLogger(TestBeliefExpansionStartegies.class);
		this.tigerDom = 
				"/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt";
		
		this.pomdp = new POMDP(this.tigerDom);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testFullBeliefExpansion() {
		LOGGER.info("Running testFullBeliefExpansion()");
		
		/* initialize POMDP */
		this.tigerDom = 
				"/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt";
		this.pomdp = new POMDP(this.tigerDom);
		
		LOGGER.info("Testing initialization");
		FullBeliefExpansion fBE = new FullBeliefExpansion(this.pomdp, 10);
		assertNotNull(fBE);

		LOGGER.info("Testing initial beliefs");
		List<DD> beliefs0 = fBE.getBeliefPoints();
		assertTrue(beliefs0.size() == this.pomdp.getInitialBeliefs().size());
		
		LOGGER.info("Testing expansion");
		fBE.expand();
		assertTrue(fBE.getBeliefPoints().size() > this.pomdp.getInitialBeliefs().size());

		LOGGER.info("Testing reset");
		fBE.resetToNewInitialBelief();
		assertTrue(fBE.getBeliefPoints().size() == this.pomdp.getInitialBeliefs().size());
		
	}
	
	@Test
	void testSparseBeliefExpansion() {
		LOGGER.info("Testing sparse belief expansion");
		
		String l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
		
		IPOMDPParser parser = new IPOMDPParser(l1DomainFile);
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 5, 10, true);
		
		LOGGER.info("Testing initialization");
		FullBeliefExpansion fb = new FullBeliefExpansion(ipomdp);
		fb.expand();
		
		
		SparseFullBeliefExpansion sb = new SparseFullBeliefExpansion(ipomdp, 30);
		sb.expand();
		
		LOGGER.debug("FullBeliefExpansion has " + fb.getBeliefPoints().size() + " beliefs.");
		LOGGER.debug("SparseFullBeliefExpansion has " + sb.getBeliefPoints().size() + " beliefs.");
		
	}
	
	@Test
	void testSSGABeliefExpansion() {
		LOGGER.info("Running testSSGABeliefExpansion()");
		
		/* initialize POMDP */
		this.tigerDom = 
				"/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt";
		this.pomdp = new POMDP(this.tigerDom);
		
		LOGGER.info("Testing initialization");
		SSGABeliefExpansion ssgaBE = new SSGABeliefExpansion(this.pomdp, 10, 1);
		assertNotNull(ssgaBE);

		LOGGER.info("Testing initial beliefs");
		List<DD> beliefs0 = ssgaBE.getBeliefPoints();
		assertTrue(beliefs0.size() >= this.pomdp.getInitialBeliefs().size());

		LOGGER.info("Testing policy based expansion");
		LOGGER.info("computing policy...");
		OfflinePBVISolver solver = 
				new OfflinePBVISolver(
						this.pomdp, 
						new FullBeliefExpansion(pomdp, 2), 
						1, 100);
		solver.solve();
		ssgaBE.setRecentPolicy(solver.getAlphaVectors(), solver.getPolicy());
		ssgaBE.expand();
		
		int numExplored = ssgaBE.getBeliefPoints().size(); 
		assertTrue(numExplored > 3);
		
		LOGGER.info("Testing reset");
		ssgaBE.resetToNewInitialBelief();
		assertTrue(ssgaBE.getBeliefPoints().size() == this.pomdp.getInitialBeliefs().size());
	}
	
	@Test
	void testFullInteractiveBeliefExpansion() {
		LOGGER.info("Running testFullInteractiveBeliefExpansion()");
		
		Global.clearHashtables();
		
		String l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
		
		IPOMDPParser parser = new IPOMDPParser(l1DomainFile);
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10);
		
		LOGGER.info("Testing initialization");
		FullBeliefExpansion fb = new FullBeliefExpansion(ipomdp);
		assertNotNull(fb);
		
		LOGGER.info("Testing expansion bound");
		assertTrue(fb.getHBound() == ipomdp.mjLookAhead);
		
		LOGGER.info("Testing initial beliefs");
		assertTrue(fb.getBeliefPoints().size() == ipomdp.getInitialBeliefs().size());
		
		LOGGER.info("Testing expansion");
		fb.expand();
		List<DD> explored = fb.getBeliefPoints();
		assertTrue(explored.size() >= ipomdp.getInitialBeliefs().size());
		
		LOGGER.info("Testing reset");
		fb.resetToNewInitialBelief();
		assertTrue(fb.getBeliefPoints().size() == ipomdp.getInitialBeliefs().size());
	}
	
	@Test
	void testInteractiveSSGABeliefExpansion() {
		LOGGER.info("Running testInteractiveSSGABeliefExpansion()");
		
		Global.clearHashtables();
		
//		String l1DomainFile = 
//				"/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
		
		String l1DomainFile = 
				"/home/adityas/UGA/THINCLab/DomainFiles/tiger.L1.F3.agnostic.domain";
		
		IPOMDPParser parser = new IPOMDPParser(l1DomainFile);
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 4, 10);
		
		LOGGER.info("Testing initialization");
		FullBeliefExpansion fb = new FullBeliefExpansion(ipomdp);
		assertNotNull(fb);
		
		SSGABeliefExpansion ssgaB = new SSGABeliefExpansion(ipomdp, 5);
		
		OnlineIPBVISolver solv = new OnlineIPBVISolver(ipomdp, ssgaB, 1, 100);
		ssgaB.setRecentPolicy(solv.alphaVectors, solv.policy);
		
		LOGGER.info("Testing expansion bound");
		assertTrue(fb.getHBound() == ipomdp.mjLookAhead);
		assertTrue(ssgaB.getHBound() == ipomdp.mjLookAhead);
		
		
		LOGGER.info("Testing expansion");
		fb.expand();
		List<DD> explored = fb.getBeliefPoints();
		LOGGER.debug("Full expansion has beliefs: " + explored.size());
		
		ssgaB.expand();
		List<DD> SSGAexplored = ssgaB.getBeliefPoints();
		LOGGER.debug("SSGA expansion has beliefs: " + SSGAexplored.size());
		
		LOGGER.info("Testing reset");
		fb.resetToNewInitialBelief();
		assertTrue(fb.getBeliefPoints().size() == ipomdp.getInitialBeliefs().size());
		ssgaB.resetToNewInitialBelief();
		assertTrue(ssgaB.getBeliefPoints().size() == ipomdp.getInitialBeliefs().size());
	}
	
	@Test
	void testBeliefExpansionForStrictlyOptimalMj() throws Exception {
		LOGGER.info("Testing belief expansion in strictly optimal MJs");
		
		String l1DomainFile = 
				"/home/adityas/UGA/THINCLab/DomainFiles/tiger.L1.F3.agnostic.domain";
		
		IPOMDPParser parser = new IPOMDPParser(l1DomainFile);
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 6, 10);
		
//		SparseFullBeliefExpansion BE = new SparseFullBeliefExpansion(ipomdp, 1);
//		BE.expand();
//		
//		for (DD belief: BE.getBeliefPoints()) {
//			LOGGER.debug("Belief is: " + ipomdp.toMapWithTheta(belief));
//		}
		
		DD startBelief = ipomdp.getCurrentBelief();
		
		for (int i = 0; i < 4; i++) {
			
			LOGGER.info("Belief is: " + ipomdp.toMapWithTheta(startBelief));
			
			Set<String> mjs = ipomdp.toMap(startBelief).get("M_j").keySet();
			
			for (String mj: mjs) {
				LOGGER.debug("MJ: " + mj + " OPT(Mj): " + ipomdp.getOptimalActionAtMj(mj));
			}
			
			startBelief = 
					ipomdp.beliefUpdate(startBelief, "listen", new String[] {"growl-left", "silence"});
			
		}
	}

}
