/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.HashSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.Examples.TigerProblemPOMDP;
import thinclab.symbolicperseus.Belief;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.Global;
import thinclab.symbolicperseus.OP;
import thinclab.symbolicperseus.POMDP;

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
		
		HashSet<DD> beliefSet = new HashSet<DD>();
		beliefSet.add(nextBelief1);
		System.out.println(beliefSet.contains(nextBelief2));
		System.out.println(beliefSet.contains(nextBelief3));
		System.out.println("Checking");
		System.out.println(nextBelief1.equals(nextBelief2));
		System.out.println(nextBelief1.equals(nextBelief3));
	}

}
