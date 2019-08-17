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
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.Examples.AttackerDomainPOMDP;
import thinclab.policyhelper.PolicyExtractor;
import thinclab.policyhelper.PolicyGraph;
import thinclab.policyhelper.PolicyTree;
import thinclab.policyhelper.PolicyVisualizer;
import thinclab.symbolicperseus.POMDP;

/*
 * @author adityas
 *
 */
class TestPolicyTree {
	
	public POMDP pomdp;

	@BeforeEach
	void setUp() throws Exception {
		
		AttackerDomainPOMDP attackerPOMDP = new AttackerDomainPOMDP();
		
		try {
			File domainFile = File.createTempFile("AttackerPOMDP", ".POMDP");
			attackerPOMDP.makeAll();
			attackerPOMDP.writeToFile(domainFile.getAbsolutePath());
			pomdp = new POMDP(domainFile.getAbsolutePath());
			pomdp.solvePBVI(10, 100);
		} 
		
		catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testPolicyTreeInit() {
		System.out.println("Running testPolicyTreeInit()");
		
		long startTime = System.nanoTime();
		PolicyTree policyTree = new PolicyTree(pomdp, 2);
		long endTime = System.nanoTime();
		
		assertTrue(policyTree.policyNodes.size() > pomdp.getInitialBeliefsList().size());
		System.out.println(policyTree.policyNodes);
	}

}
