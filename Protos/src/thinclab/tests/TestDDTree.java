package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.ddinterface.DDMaker;
import thinclab.ddinterface.DDTree;
import thinclab.ddinterface.DDTreeLeaf;
import thinclab.decisionprocesses.POMDP;
import thinclab.legacy.DD;
import thinclab.legacy.OP;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;

class TestDDTree {

	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testTreeCreation() {
		System.out.println("Running testTreeCreation()");
		DDTree testTree = new DDTree("TestVar");
		testTree.children.put("val1", new DDTreeLeaf(0.01));
		testTree.children.put("val2", new DDTreeLeaf(0.99));
		
		System.out.println(testTree.toSPUDD());
	}
	
	@Test
	void testTreeCopy() {
		System.out.println("Running testTreeCopy()");
		DDTree testTree = new DDTree("TestVar");
		testTree.children.put("val1", new DDTreeLeaf(0.01));
		testTree.children.put("val2", new DDTreeLeaf(0.99));
		
		DDTree copyTestTree = testTree.getCopy();
		
		System.out.println("Tree 1: \r\n" + testTree.toSPUDD());
		System.out.println("Copy Tree: \r\n" + copyTestTree.toSPUDD());
		
		assertTrue(testTree.equals(copyTestTree));
	}
	
	@Test
	void testTreeEquals() {
		System.out.println("Running testTreeEquals()");
		DDTree testTree1 = new DDTree("TestVar");
		testTree1.children.put("val1", new DDTreeLeaf(0.01));
		testTree1.children.put("val2", new DDTreeLeaf(0.99));
		
		DDTree copyTestTree1 = testTree1.getCopy();
		
		DDTree testTree2 = new DDTree("TestVarAnother");
		testTree2.children.put("val1", new DDTreeLeaf(0.01));
		testTree2.children.put("val2", new DDTreeLeaf(0.99));
		
		assertTrue(testTree1.equals(copyTestTree1));
		assertFalse(testTree1.equals(testTree2));
	}
	
	@Test
	void testNodeRefFetching() {
		System.out.println("Running testNodeRefFetching()");
		DDTree testTree1 = new DDTree("TestVar");
		testTree1.children.put("val1", new DDTreeLeaf(0.01));
		testTree1.children.put("val2", new DDTreeLeaf(0.99));
		
		DDTree deepTestTree = new DDTree("DeepVar");
		deepTestTree.children.put("val1", testTree1.getCopy());
		deepTestTree.children.put("val2", testTree1.getCopy());
		deepTestTree.children.put("val3", testTree1.getCopy());
		
		System.out.println("Printing the deep tree:\r\n" + deepTestTree.toSPUDD());
		
		try {
			assertTrue(deepTestTree.atChild("val1").equals(testTree1));
		}
		catch (Exception e) {
			System.out.println("[X][X][X] SOMETHING BROKE WHILE VISITING CHILD");
		}
	}
	
	@Test
	void testDDTreeToDDConversion() {
		/*
		 * Test to check conversion from DDTree to symbolic perseus DD
		 */
		System.out.println("Running testDDTreeToDDConversion()");
		
		/*
		 * Make test POMDP domain and solve it
		 */
		
		POMDP pomdp = null;
		
		try {
			
			pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/attacker_l0.txt");
			
			OfflineSymbolicPerseus solver = 
					OfflineSymbolicPerseus.createSolverWithSSGAExpansion(
							pomdp, 
							50, 
							1, 
							10, 
							100);
			solver.solve();
		}
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		
		System.out.println(pomdp.initialBelState_f[0]);
		
		DDTree sessPrivsBelief = new DDTree("SESSION_PRIVS");
		sessPrivsBelief.children.put("user", new DDTreeLeaf(0.5));
		sessPrivsBelief.children.put("admin", new DDTreeLeaf(0.5));
		
		DD sessPrivsDD = sessPrivsBelief.toDD();
		
		System.out.println(sessPrivsDD);
		System.out.println("SESS_PRIVS: " + pomdp.initialBelState_f[0]);
		System.out.println("SESS_PRIVS varset: " + Arrays.toString(pomdp.initialBelState_f[0].getVarSet()));
		
		System.out.println("OTHER: " + pomdp.initialBelState_f[1]);
		System.out.println("OTHER varset: " + Arrays.toString(pomdp.initialBelState_f[1].getVarSet()));
		
		System.out.println("MULT: " + OP.mult(pomdp.initialBelState_f[1], pomdp.initialBelState_f[0]));
//		System.out.println("OTHER varset: " + Arrays.toString(pomdp.initialBelState_f[1].getVarSet()));
		
//		assertTrue(pomdp.initialBelState_f[0].equals(sessPrivsDD));
	}
	
	@Test
	void testDDToDDTree() {
		System.out.println("Running testDDToDDTree()");
		
//		AttackerDomainPOMDP attackerPOMDP = new AttackerDomainPOMDP();
		
		POMDP pomdp = null;
		OfflineSymbolicPerseus solver = null;
		try {
			pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");
			
			solver = 
					OfflineSymbolicPerseus.createSolverWithSSGAExpansion(
							pomdp, 
							50, 
							1, 
							10, 
							100);
			solver.solve();
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		
		assertTrue(solver.alphaVectors[0].equals(solver.alphaVectors[0].toDDTree().toDD()));
	}
	
	@Test
	void testGetCPT() {
		System.out.println("Running testGetCPT()");
		DDMaker d = new DDMaker();
		d.addVariable("A", new String[] {"a1", "a2"});
		d.addVariable("B", new String[] {"b1", "b2"});
		d.addVariable("C", new String[] {"c1", "c2"});
		d.primeVariables();
		
		DDTree t = d.getDDTreeFromSequence(new String[] {"A", "B", "C"});
		
		List<List<String>> cpt = t.getCPT(); 
		
		System.out.println(cpt);
		
		assertTrue(cpt.size() == 2*2*2);
	}

}
