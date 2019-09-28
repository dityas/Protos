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

import thinclab.Belief.FullBeliefExpansion;
import thinclab.Belief.FullInteractiveBeliefExpansion;
import thinclab.frameworks.IPOMDP;
import thinclab.frameworks.POMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.Global;

/*
 * @author adityas
 *
 */
class TestBeliefExpansionStartegies {

	public String tigerDom;
	public POMDP pomdp;
	
	
	@BeforeEach
	void setUp() throws Exception {
		this.tigerDom = 
				"/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.95.SPUDD.txt";
		
		this.pomdp = new POMDP(this.tigerDom);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testFullBeliefExpansion() {
		System.out.println("Running testFullBeliefExpansion()");
		
		FullBeliefExpansion fb = new FullBeliefExpansion(this.pomdp, 10);
		assertNotNull(fb);
		assertTrue(fb.getHBound() == 10);
		
		List<DD> beliefs0 = fb.getBeliefPoints();
		
		fb.expand();
		
		List<DD> beliefs1 = fb.getBeliefPoints();
		
		fb.expand();
		
		List<DD> beliefs2 = fb.getBeliefPoints();
		
		assertTrue(beliefs0.size() <= beliefs1.size());
		assertTrue(beliefs1.size() <= beliefs2.size());
	}
	
	@Test
	void testFullInteractiveBeliefExpansion() {
		System.out.println("Running testFullInteractiveBeliefExpansion()");
		
		Global.clearHashtables();
		
		String l1DomainFile = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
		
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
