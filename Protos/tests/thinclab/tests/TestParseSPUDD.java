/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.parsers.ParseSPUDD;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestParseSPUDD {

	public String l1DomainFile;
	private Logger LOGGER;
	
	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		LOGGER = LogManager.getLogger(TestParseSPUDD.class);
		this.l1DomainFile = 
				"/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt";
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
		
		assertEquals(parser.S.size(), 1);
		assertEquals(parser.Omega.size(), 1);
		assertEquals(parser.A.size(), 3);
		
		parser.Ti.entrySet().stream()
			.forEach(e -> assertEquals(e.getValue().size(), parser.S.size()));
		
		parser.Oi.entrySet().stream()
			.forEach(e -> assertEquals(e.getValue().size(), parser.Omega.size()));
		
		assertEquals(parser.costs.size(), parser.A.size());
	}
}
