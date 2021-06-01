package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.ddinterface.DDMaker;
import thinclab.ddinterface.DDTree;
import thinclab.ddinterface.DDTreeLeaf;
import thinclab.decisionprocesses.POMDP;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;

class TestDDTree {

	private static Logger LOGGER;

	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		LOGGER = LogManager.getLogger(TestDDTree.class);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testTreeCreation() {
		LOGGER.info("Running testTreeCreation()");
		DDTree testTree = new DDTree("TestVar");
		testTree.children.put("val1", new DDTreeLeaf(0.01f));
		testTree.children.put("val2", new DDTreeLeaf(0.99f));

		LOGGER.debug(testTree.toSPUDD());
	}

	@Test
	void testTreeCopy() {
		LOGGER.info("Running testTreeCopy()");
		DDTree testTree = new DDTree("TestVar");
		testTree.children.put("val1", new DDTreeLeaf(0.01f));
		testTree.children.put("val2", new DDTreeLeaf(0.99f));

		DDTree copyTestTree = testTree.getCopy();

		LOGGER.debug("Tree 1: \r\n" + testTree.toSPUDD());
		LOGGER.debug("Copy Tree: \r\n" + copyTestTree.toSPUDD());

		assertTrue(testTree.equals(copyTestTree));
	}

	@Test
	void testTreeEquals() {
		LOGGER.info("Running testTreeEquals()");
		DDTree testTree1 = new DDTree("TestVar");
		testTree1.children.put("val1", new DDTreeLeaf(0.01f));
		testTree1.children.put("val2", new DDTreeLeaf(0.99f));

		DDTree copyTestTree1 = testTree1.getCopy();

		DDTree testTree2 = new DDTree("TestVarAnother");
		testTree2.children.put("val1", new DDTreeLeaf(0.01f));
		testTree2.children.put("val2", new DDTreeLeaf(0.99f));

		assertTrue(testTree1.equals(copyTestTree1));
		assertFalse(testTree1.equals(testTree2));
	}

	@Test
	void testNodeRefFetching() {
		LOGGER.info("Running testNodeRefFetching()");
		DDTree testTree1 = new DDTree("TestVar");
		testTree1.children.put("val1", new DDTreeLeaf(0.01f));
		testTree1.children.put("val2", new DDTreeLeaf(0.99f));

		DDTree deepTestTree = new DDTree("DeepVar");
		deepTestTree.children.put("val1", testTree1.getCopy());
		deepTestTree.children.put("val2", testTree1.getCopy());
		deepTestTree.children.put("val3", testTree1.getCopy());

		LOGGER.debug("Printing the deep tree:\r\n" + deepTestTree.toSPUDD());

		try {
			assertTrue(deepTestTree.atChild("val1").equals(testTree1));
		} catch (Exception e) {
			LOGGER.error("[X][X][X] SOMETHING BROKE WHILE VISITING CHILD");
		}
	}

	@Test
	void testDDToDDTree() {
		LOGGER.info("Running testDDToDDTree()");

//		AttackerDomainPOMDP attackerPOMDP = new AttackerDomainPOMDP();

		POMDP pomdp = null;
		OfflineSymbolicPerseus solver = null;
		try {
			pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");

			solver = OfflineSymbolicPerseus.createSolverWithSSGAExpansion(pomdp, 50, 1, 10, 100);
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
		LOGGER.info("Running testGetCPT()");
		DDMaker d = new DDMaker();
		d.addVariable("A", new String[] { "a1", "a2" });
		d.addVariable("B", new String[] { "b1", "b2" });
		d.addVariable("C", new String[] { "c1", "c2" });
		d.primeVariables();

		DDTree t = d.getDDTreeFromSequence(new String[] { "A", "B", "C" });

		List<List<String>> cpt = t.getCPT();

		LOGGER.debug(cpt);

		assertTrue(cpt.size() == 2 * 2 * 2);
	}

	@Test
	void testAddDDDeep() throws Exception {
		LOGGER.info("Running testAddDDDeep()");

		DDMaker d = new DDMaker();
		d.addVariable("A", new String[] { "a1", "a2" });
		d.addVariable("B", new String[] { "b1", "b2" });
		d.addVariable("C", new String[] { "c1", "c2" });
		d.primeVariables();

		DDTree t = d.getDDTreeFromSequence(new String[] { "A", "B", "C" });

		LOGGER.debug("t is " + t);

		DDTree t1 = t.getCopy();

		LOGGER.debug("sequence is " + new ArrayList<String>(Arrays.asList(new String[] { "a1", "b2", "c1" })));

		t.setDDAt(new ArrayList<String>(Arrays.asList(new String[] { "a1", "b2", "c1" })), t1);

		LOGGER.debug("t is now " + t);

		assertTrue(t1.equals(t.atChild("a1").atChild("b2").atChild("c1")));
	}

}
