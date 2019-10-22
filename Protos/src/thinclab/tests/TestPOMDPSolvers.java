/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.Belief;
import thinclab.belief.FullBeliefExpansion;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.frameworks.POMDP;
import thinclab.legacy.DD;
import thinclab.solvers.OfflinePBVISolver;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestPOMDPSolvers {

	public String tigerDom = "/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt";
//	public String tigerDom = "/home/adityas/git/repository/Protos/domains/attacker_l0.txt";
	
	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testPOMDPInitFromParser() {
		System.out.println("Running testPOMDPInitFromParser()");
		
		POMDP p1 = new POMDP(this.tigerDom);
//		POMDP p2 = new POMDP(this.attDom);
		
		assertTrue(p1.S.size() >= 1);
		assertTrue(p1.Omega.size() >= 1);
		assertTrue(p1.A.size() >= 1);
		assertNotNull(p1.R);
		assertEquals(p1.S.size(), p1.stateVars.length);
		assertEquals(p1.Omega.size(), p1.obsVars.length);
		assertEquals(p1.A.size(), p1.actions.length);

//		assertTrue(p2.S.size() >= 1);
//		assertTrue(p2.Omega.size() >= 1);
//		assertTrue(p2.A.size() >= 1);
//		assertNotNull(p2.R);
//		assertEquals(p2.S.size(), p2.stateVars.length);
//		assertEquals(p2.Omega.size(), p2.obsVars.length);
//		assertEquals(p2.A.size(), p2.actions.length);
	}
	
	@Test
	void testOfflinePBVISolverForPOMDP() throws ZeroProbabilityObsException {
		System.out.println("Running testOfflinePBVISolverForPOMDP()");
		
		POMDP p1 = new POMDP(this.tigerDom);

		FullBeliefExpansion fb = new FullBeliefExpansion(p1, 3);
		
		OfflinePBVISolver solver = new OfflinePBVISolver(p1, fb, 3, 100);
		solver.solve();
		
		DD initial = p1.getInitialBeliefs().get(0);
		
		assertTrue(
				solver.getBestActionAtBelief(
						p1.getInitialBeliefs().get(0)).contentEquals("listen"));
		
		DD nextBelief = 
				Belief.beliefUpdate(
						p1, 
						initial, 
						p1.getActions().indexOf("listen"), 
						new String[] {"growl-left"});
		
		assertTrue(
				solver.getBestActionAtBelief(nextBelief).contentEquals("listen"));
	}
	
	@Test
	void testOfflineSymbolicPerseusSolverForPOMDP() throws ZeroProbabilityObsException {
		System.out.println("Running testOfflineSymbolicPerseusSolverForPOMDP()");
		
		POMDP p1 = new POMDP(this.tigerDom);
		SSGABeliefExpansion be = new SSGABeliefExpansion(p1, 100, 1);
		OfflineSymbolicPerseus solver = new OfflineSymbolicPerseus(p1, be, 15, 100);
		
		solver.solve();
		
		DD initial = p1.getInitialBeliefs().get(0);
		
		assertTrue(
				solver.getBestActionAtBelief(
						p1.getInitialBeliefs().get(0)).contentEquals("listen"));
		
		DD nextBelief = 
				Belief.beliefUpdate(
						p1, 
						initial, 
						p1.getActions().indexOf("listen"), 
						new String[] {"growl-left"});
		
		assertTrue(
				solver.getBestActionAtBelief(nextBelief).contentEquals("listen"));
	}
	
	@Test
	void testOfflineSymbolicPerseusSolverWithFullExpansionForPOMDP() throws ZeroProbabilityObsException {
		System.out.println("Running testOfflineSymbolicPerseusSolverWithFullExpansionForPOMDP()");
		
		POMDP p1 = new POMDP(this.tigerDom);
		FullBeliefExpansion fb = new FullBeliefExpansion(p1, 2);
		OfflineSymbolicPerseus solver = new OfflineSymbolicPerseus(p1, fb, 4, 100);
		
		solver.solve();
		
		DD initial = p1.getInitialBeliefs().get(0);
		
		assertTrue(
				solver.getBestActionAtBelief(
						p1.getInitialBeliefs().get(0)).contentEquals("listen"));
		
		DD nextBelief = 
				Belief.beliefUpdate(
						p1, 
						initial, 
						p1.getActions().indexOf("listen"), 
						new String[] {"growl-left"});
		
		assertTrue(
				solver.getBestActionAtBelief(nextBelief).contentEquals("listen"));
	}
	
	@Test
	void testPOMDPSolve() {
		System.out.println("Running testPOMDPSolve");
		POMDP p1 = new POMDP(this.tigerDom);
		p1.solvePBVI(15, 100);
		
	}

}