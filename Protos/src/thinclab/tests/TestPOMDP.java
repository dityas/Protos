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

import thinclab.frameworks.POMDP;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestPOMDP {

	public String tigerDom = "/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt";
	public String attDom = "/home/adityas/git/repository/Protos/domains/attacker_l0.txt";
	
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
		POMDP p2 = new POMDP(this.attDom);
		
		assertTrue(p1.S.size() >= 1);
		assertTrue(p1.Omega.size() >= 1);
		assertTrue(p1.A.size() >= 1);
		assertNotNull(p1.R);
		assertEquals(p1.S.size(), p1.stateVars.length);
		assertEquals(p1.Omega.size(), p1.obsVars.length);
		assertEquals(p1.A.size(), p1.actions.length);

		assertTrue(p2.S.size() >= 1);
		assertTrue(p2.Omega.size() >= 1);
		assertTrue(p2.A.size() >= 1);
		assertNotNull(p2.R);
		assertEquals(p2.S.size(), p2.stateVars.length);
		assertEquals(p2.Omega.size(), p2.obsVars.length);
		assertEquals(p2.A.size(), p2.actions.length);
	}
	
	@Test
	void testPOMDPSolvePBVI() {
		System.out.println("Running testPOMDPSolvePBVI()");
		
		POMDP p1 = new POMDP(this.tigerDom);
//		POMDP p2 = new POMDP(this.attDom);
		
		assertNull(p1.alphaVectors);
//		assertNull(p2.alphaVectors);
		
		p1.solvePBVI(3, 100);
//		p2.solvePBVI(15, 100);
		
		assertNotNull(p1.alphaVectors);
//		assertNotNull(p2.alphaVectors);
	}

}
