/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.List;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.Belief;
import thinclab.belief.BeliefSet;
import thinclab.examples.TigerProblemPOMDP;
import thinclab.frameworks.POMDP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
class TestBelief {

	public POMDP pomdp;
	
	@BeforeEach
	void setUp() throws Exception {
		TigerProblemPOMDP tigerPOMDP = new TigerProblemPOMDP();
		File domainFile = File.createTempFile("TigerPOMDP", ".POMDP");
		tigerPOMDP.makeAll();
		tigerPOMDP.writeToFile(domainFile.getAbsolutePath());
		pomdp = new POMDP(domainFile.getAbsolutePath());
		pomdp.solvePBVI(10, 100);
		domainFile.deleteOnExit();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testCheckEquals() {
		System.out.println("Running testCheckEquals()");
//		pomdp.prettyPrintBeliefRegion();
//		pomdp.initialBelState.display();
		DD nextBelief1 = pomdp.beliefUpdate(pomdp.initialBelState, 1, new String[] {"GL"});
//		nextBelief1.display();
		DD nextBelief2 = pomdp.beliefUpdate(nextBelief1, 1, new String[] {"GL"});
//		nextBelief2.display();
		Global.clearHashtables();
		DD nextBelief3 = pomdp.beliefUpdate(nextBelief2, 1, new String[] {"GR"});
//		nextBelief3.display();
//		OP.sub(nextBelief1, nextBelief1).display();
		assertFalse(Belief.checkEquals(nextBelief1, nextBelief2));
		assertTrue(Belief.checkEquals(nextBelief1, nextBelief1));
		assertTrue(Belief.checkEquals(nextBelief1, nextBelief3));
	}
	
	@Test
	void testBeliefSetBFSExpansion() {
		System.out.println("Running testBeliefSetBFSExpansion()");
		BeliefSet bSet = new BeliefSet(Arrays.asList(new DD[] {this.pomdp.initialBelState}));
		
		int previousSize = bSet.beliefSet.size();
		for (int i=0; i < 100; i++) { 
			bSet.expandBeliefRegionBF(this.pomdp, 1);
			System.out.println("SET SIZE: " + bSet.beliefSet.size());
		}
		
		assertTrue(bSet.beliefSet.size() > previousSize);
	}
	
	@Test
	void testBeliefSetSSGAExpansion() {
		System.out.println("Running testBeliefSetSSGAExpansion()");
		BeliefSet bSet = new BeliefSet(Arrays.asList(new DD[] {this.pomdp.initialBelState}));
		
		int previousSize = bSet.beliefSet.size();
		for (int i=0; i < 100; i++) { 
			bSet.expandBeliefRegionSSGA(this.pomdp, 50);
			System.out.println("SET SIZE: " + bSet.beliefSet.size());
		}
		
		assertTrue(bSet.beliefSet.size() > previousSize);
	}

}
