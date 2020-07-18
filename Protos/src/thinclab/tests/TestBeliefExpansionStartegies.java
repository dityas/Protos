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
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.FullBeliefExpansion;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.belief.SparseFullBeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.legacy.AlphaVector;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.parsers.IPOMDPParser;
import thinclab.solvers.OfflinePBVISolver;
import thinclab.solvers.OnlineIPBVISolver;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;
import thinclab.utils.NextBelStateCache;

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
				"/home/adityas/UGA/THINCLab/DomainFiles/tiger.L1.txt";
		
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
	
	private HashMap<String, Float> getActionProbs(IPOMDP ipomdp, DD belief) {
		
		HashMap<String, Float> actProbs = new HashMap<String, Float>();
		
		for (String mj: ipomdp.toMap(belief).get("M_j").keySet()) {
			
			String act = ipomdp.getOptimalActionAtMj(mj);
			
			if (!actProbs.containsKey(act))
				actProbs.put(act, ipomdp.toMap(belief).get("M_j").get(mj));
			else
				actProbs.put(act, actProbs.get(act) + ipomdp.toMap(belief).get("M_j").get(mj));
		}
		
		return actProbs;
	}
	
	@SuppressWarnings("unused")
	@Test
	void testBeliefExpansionForStrictlyOptimalMj() throws Exception {
		LOGGER.info("Testing belief expansion in strictly optimal MJs");
		
		NextBelStateCache.useCache();
		
//		String l1DomainFile = 
//				"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/deception.6S.2O.2F.domain";
		String l1DomainFile = 
				"/home/adityas/git/repository/Protos/domains/tiger.L1.enemy.txt";
		
		IPOMDPParser parser = new IPOMDPParser(l1DomainFile);
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 5, 10);
		
		FullBeliefExpansion FB = new FullBeliefExpansion(ipomdp);
		
		FB.expandSingleStep();
		List<DD> T1 = FB.getBeliefPoints();
		
		FB.expandSingleStep();
		List<DD> T2 = FB.getBeliefPoints();
		
		FB.expandSingleStep();
		List<DD> T3 = FB.getBeliefPoints();
		
		List<DD> beliefs = FB.getBeliefPoints();
		
		DD t0belief = ipomdp.getCurrentBelief();
		DD t1belief = ipomdp.beliefUpdate(t0belief, "listen", new String[] {"growl-left", "silence"});
		DD t2belief = ipomdp.beliefUpdate(t1belief, "listen", new String[] {"growl-left", "silence"});
		DD t3belief = ipomdp.beliefUpdate(t2belief, "listen", new String[] {"growl-left", "silence"});
		DD t4belief = ipomdp.beliefUpdate(t3belief, "listen", new String[] {"growl-left", "silence"});

		DD belief = t3belief;
		
		LOGGER.debug("===========================");
		LOGGER.debug("===========================");
		LOGGER.debug("Mj: " + ipomdp.toMap(belief));
		LOGGER.debug("Most probable Aj: " + ipomdp.getMostProbableAj(belief));
		LOGGER.debug("Aj probs are: " + this.getActionProbs(ipomdp, belief));
		for (String act: ipomdp.getActions()) {
			LOGGER.debug("For action: " + act);
			DD Vec = ipomdp.getRewardFunctionForAction(act);
			double val = OP.dotProduct(Vec, belief, ipomdp.getStateVarIndices());
			LOGGER.debug("val is " + val);
			LOGGER.debug("Ri is: " + Vec.toDDTree());
		}
		
		OnlineInteractiveSymbolicPerseus S = 
				new OnlineInteractiveSymbolicPerseus(ipomdp, FB, 1, 1);
		
		DD[] alphaVectors = S.getAlphaVectors();
		
		DD[] primedV = new DD[alphaVectors.length];
		
		for (int i = 0; i < alphaVectors.length; i++) {
			primedV[i] = 
					OP.primeVars(
							alphaVectors[i], 
							ipomdp.getNumVars());
		}
		
		double maxAbsVal = 
				Math.max(
						OP.maxabs(
								IPOMDP.concatenateArray(
										OP.maxAllN(alphaVectors), 
										OP.minAllN(alphaVectors))), 1e-10);
		
		AlphaVector newVector = 
				AlphaVector.dpBackup2(
						ipomdp,
						t3belief,
						primedV,
						maxAbsVal,
						alphaVectors.length);
		
		LOGGER.debug("Alpha Vector is: " + newVector.alphaVector.toDDTree());
		LOGGER.debug("Value is: " + newVector.value);
		
		T1 = null;
		T2 = null;
		
	
//		LOGGER.debug(ipomdp.multiFrameMJ.MJs.get(0).getDotStringForPersistent());
	}

}
