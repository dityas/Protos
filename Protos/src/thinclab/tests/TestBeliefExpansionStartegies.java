/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.FullBeliefExpansion;
import thinclab.belief.FullInteractiveBeliefExpansion;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestBeliefExpansionStartegies {

	public String tigerDom;
	public POMDP pomdp;
	
	
	@BeforeEach
	void setUp() throws Exception {
		
		CustomConfigurationFactory.initializeLogging();
		this.tigerDom = 
				"/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt";
		
		this.pomdp = new POMDP(this.tigerDom);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testFullBeliefExpansion() {
		System.out.println("Running testFullBeliefExpansion()");
		
		FullBeliefExpansion fb = new FullBeliefExpansion(this.pomdp, 2);
		assertNotNull(fb);
		assertTrue(fb.getHBound() == 2);
		
		List<DD> beliefs0 = fb.getBeliefPoints();
		
		fb.expand();
		
		List<DD> beliefs1 = fb.getBeliefPoints();
		
		fb.expand();
		
		List<DD> beliefs2 = fb.getBeliefPoints();
		
		assertTrue(beliefs0.size() <= beliefs1.size());
		assertTrue(beliefs1.size() <= beliefs2.size());
		
		assertTrue(beliefs0.size() == 1);
		assertTrue(beliefs1.size() == 3);
		assertTrue(beliefs2.size() == 5);
	}
	
	@Test
	void testSSGABeliefExpansion() {
		System.out.println("Running testSSGABeliefExpansion()");
		
		OfflineSymbolicPerseus solver = 
				OfflineSymbolicPerseus.createSolverWithSSGAExpansion(
						this.pomdp, 
						100, 
						1, 
						10, 
						100);
		solver.solve();
		
		SSGABeliefExpansion ssgaBE = new SSGABeliefExpansion(this.pomdp, 100, 1000);
		assertNotNull(ssgaBE);
//		assertTrue(ssgaBE.getHBound() == 10);
		
		List<DD> beliefs0 = ssgaBE.getBeliefPoints();
		
		ssgaBE.expand();
		
		List<DD> beliefs1 = ssgaBE.getBeliefPoints();
		
		ssgaBE.expand();
		
		List<DD> beliefs2 = ssgaBE.getBeliefPoints();
		
		assertTrue(beliefs0.size() <= beliefs1.size());
		assertTrue(beliefs1.size() <= beliefs2.size());
		
	}
	
	@Test
	void testFullInteractiveBeliefExpansion() {
		System.out.println("Running testFullInteractiveBeliefExpansion()");
		
		Global.clearHashtables();
		
		String l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
		
		IPOMDPParser parser = new IPOMDPParser(l1DomainFile);
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 10, 3);
		
		FullInteractiveBeliefExpansion fb = new FullInteractiveBeliefExpansion(ipomdp);
		
		assertNotNull(fb);
		assertTrue(fb.getHBound() == 3);
		
		List<DD> beliefs0 = fb.getBeliefPoints();
		
		fb.expand();
		
		List<DD> beliefs1 = fb.getBeliefPoints();
		
		
		assertTrue(beliefs0.size() <= beliefs1.size());
	}

}
