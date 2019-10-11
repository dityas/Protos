package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.ddhelpers.DDMaker;
import thinclab.ddhelpers.DDTree;

class TestDDMaker {

	private DDMaker ddmaker = null;
	@BeforeEach
	void setUp() throws Exception {
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
		System.out.println("Running testDDCreationFromOnlySequences()");
		DDTree DD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS",
							  "PERSIST_LEVEL",
							  "PERSIST_LEVEL'"});
		System.out.println(DD.toSPUDD());
	}
	
	@Test
	void testDDCreationFromSequencesWithValues() {
		System.out.println("Running testDDCreationFromSequencesWithValues()");
		DDTree DD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS",
							  "PERSIST_LEVEL",
							  "PERSIST_LEVEL'"},
				new String[][] {{"user", "none", "user", "1.0"},
								{"admin", "none", "admin", "1.0"}}
								);
		
		System.out.println(DD.toSPUDD());
	}
	
	@Test
	void testDDCreationFromSequencesWithStarValues() {
		System.out.println("Running testDDCreationFromSequencesWithStarValues()");
		DDTree DD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS",
							  "PERSIST_LEVEL",
							  "PERSIST_LEVEL'"},
				new String[][] {{"user", "*", "user", "1.0"},
								{"admin", "none", "admin", "1.0"}}
								);
		
		System.out.println(DD.toSPUDD());
	}
	
	@Test
	void testDDAppend() {
		System.out.println("Running testDDAppend()");
		
	}

}
