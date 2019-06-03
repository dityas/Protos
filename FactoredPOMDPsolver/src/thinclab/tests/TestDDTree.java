package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.domainMaker.ddHelpers.DDTreeLeaf;

class TestDDTree {

	@BeforeEach
	void setUp() throws Exception {
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

}
