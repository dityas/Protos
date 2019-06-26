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

import thinclab.ipomdpsolver.IPOMDPParser;

/*
 * @author adityas
 *
 */
class TestIPOMDPParser {

	public String testFileName;	
	@BeforeEach
	void setUp() throws Exception {
		this.testFileName = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testParserInit() {
		System.out.println("Running testParserInit()");
		IPOMDPParser parser = new IPOMDPParser(this.testFileName);
		assertNotNull(parser);
	}

}
