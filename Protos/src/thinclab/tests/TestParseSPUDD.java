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

import thinclab.parsers.ParseSPUDD;

/*
 * @author adityas
 *
 */
class TestParseSPUDD {

	public String l1DomainFile;
	
	@BeforeEach
	void setUp() throws Exception {
		this.l1DomainFile = 
				"/home/adityas/git/repository/FactoredPOMDPsolver/src/attacker_l0.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testParserInit() {
		System.out.println("Running testParserInit()");
		
		ParseSPUDD parser = new ParseSPUDD(this.l1DomainFile);
		assertNotNull(parser);
	}
	
	@Test
	void testParserRun() {
		System.out.println("Running testParserRun()");
		
		ParseSPUDD parser = new ParseSPUDD(this.l1DomainFile);
		parser.parsePOMDP(false);
		
		assertEquals(parser.S.size(), 8);
		assertEquals(parser.Omega.size(), 1);
		assertEquals(parser.A.size(), 7);
		
		parser.Ti.entrySet().stream()
			.forEach(e -> assertEquals(e.getValue().size(), parser.S.size()));
		
		parser.Oi.entrySet().stream()
			.forEach(e -> assertEquals(e.getValue().size(), parser.Omega.size()));
		
		assertEquals(parser.costs.size(), parser.A.size());
	}
}
