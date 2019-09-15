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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.Examples.AttackerDomainPOMDP;
import thinclab.policyhelper.PolicyExtractor;
import thinclab.policyhelper.PolicyGraph;
import thinclab.policyhelper.PolicyTree;
import thinclab.policyhelper.PolicyVisualizer;
import thinclab.symbolicperseus.POMDP;
import thinclab.utils.BeliefTreeTable;

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
			pomdp.solvePBVI(5, 100);
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
	void testParseSPUDDToDDTree() {
		System.out.println("Running testParseSPUDDToDDTree()");
		
		System.out.println(this.pomdp.parser.Oi);
		System.out.println(this.pomdp.parser.Ti);
	}

	@Test
	void testPolicyTreeInit() {
		System.out.println("Running testPolicyTreeInit()");
		
		long startTime = System.nanoTime();
		PolicyTree policyTree = new PolicyTree(pomdp, 2);
		long endTime = System.nanoTime();
		
		System.out.println(policyTree.policyNodes);
	}
	
	@Test
	void testShiftIndex() {
		System.out.println("Running testShiftIndex()");
		
		PolicyTree policyTree = new PolicyTree(pomdp, 2);
		System.out.println(policyTree.policyNodes);
		/* Collect orignal indices */
		Set<Integer> previousIndices = 
				policyTree.policyNodes
					.stream()
					.map(n -> new Integer(n.id))
					.collect(Collectors.toSet());
		
		/* offset indices */
		policyTree.shiftIndex(previousIndices.size());
		System.out.println(policyTree.policyNodes);
		
		Set<Integer> newIndices = 
				policyTree.policyNodes
					.stream()
					.map(n -> new Integer(n.id))
					.collect(Collectors.toSet());
		
		assertEquals(previousIndices.size(), newIndices.size());
		previousIndices.addAll(newIndices);
		assertTrue(previousIndices.size() == (2 * newIndices.size()));
	}

}
