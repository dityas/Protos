package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.ddinterface.DDMaker;
import thinclab.ddinterface.DDTree;
import thinclab.ddinterface.DDTreeLeaf;
import thinclab.utils.CustomConfigurationFactory;

class TestDDMaker {

	private DDMaker ddmaker = null;
	private static Logger LOGGER;
	
	@BeforeEach
	void setUp() throws Exception {
		
		CustomConfigurationFactory.initializeLogging();
		LOGGER = LogManager.getLogger(TestDDMaker.class);
		
		this.ddmaker = new DDMaker();
		this.ddmaker.addVariable("SESSION_PRIVS", new String[] {"user", "admin"});
		this.ddmaker.addVariable("PERSIST_LEVEL", new String[] {"none", "user", "admin"});
		this.ddmaker.primeVariables();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testDDCreationFromOnlySequences() {
		
		LOGGER.info("Running testDDCreationFromOnlySequences()");
		
		LOGGER.info("Testing DDTree init");
		DDTree DD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS",
							  "PERSIST_LEVEL",
							  "PERSIST_LEVEL'"});
		assertNotNull(DD);
		
		LOGGER.info("Checking var ordering");
		assertEquals(DD.varName, "SESSION_PRIVS");
		assertEquals(DD.children.get("user").varName, "PERSIST_LEVEL");
		
	}
	
	@Test
	void testDDCreationFromSequencesWithValues() {
		LOGGER.info("Running testDDCreationFromSequencesWithValues()");
		
		DDTree DD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS",
							  "PERSIST_LEVEL",
							  "PERSIST_LEVEL'"},
				new String[][] {{"user", "none", "user", "1.0"},
								{"admin", "none", "admin", "1.0"}}
								);
		
		LOGGER.info("Checking set values");
		assertEquals(
				((DDTreeLeaf) DD.children.get("user")
								.children.get("none")
								.children.get("user")).val, 
				1.0);
		
		assertEquals(
				((DDTreeLeaf) DD.children.get("admin")
								.children.get("none")
								.children.get("admin")).val, 
				1.0);
		
		LOGGER.info("Checking unset values");
		assertEquals(
				((DDTreeLeaf) DD.children.get("user")
								.children.get("none")
								.children.get("admin")).val, 
				0.0);
		
		assertEquals(
				((DDTreeLeaf) DD.children.get("admin")
								.children.get("none")
								.children.get("user")).val, 
				0.0);
		
		
	}
	
	@Test
	void testDDCreationFromSequencesWithStarValues() {
		
		LOGGER.info("Running testDDCreationFromSequencesWithStarValues()");
		
		DDTree DD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS",
							  "PERSIST_LEVEL",
							  "PERSIST_LEVEL'"},
				new String[][] {{"user", "*", "user", "1.0"},
								{"admin", "none", "admin", "1.0"}}
								);
		
		LOGGER.info("Checking set values");
		assertEquals(
				((DDTreeLeaf) DD.children.get("user")
								.children.get("none")
								.children.get("user")).val, 
				1.0);
		
		assertEquals(
				((DDTreeLeaf) DD.children.get("user")
								.children.get("user")
								.children.get("user")).val, 
				1.0);
		assertEquals(
				((DDTreeLeaf) DD.children.get("user")
								.children.get("admin")
								.children.get("user")).val, 
				1.0);
		assertEquals(
				((DDTreeLeaf) DD.children.get("admin")
								.children.get("none")
								.children.get("admin")).val, 
				1.0);
		
		LOGGER.info("Checking unset values");
		assertEquals(
				((DDTreeLeaf) DD.children.get("user")
								.children.get("none")
								.children.get("admin")).val, 
				0.0);
		
		assertEquals(
				((DDTreeLeaf) DD.children.get("user")
								.children.get("user")
								.children.get("admin")).val, 
				0.0);
		assertEquals(
				((DDTreeLeaf) DD.children.get("user")
								.children.get("admin")
								.children.get("admin")).val, 
				0.0);
		assertEquals(
				((DDTreeLeaf) DD.children.get("admin")
								.children.get("none")
								.children.get("none")).val, 
				0.0);
	}

}
