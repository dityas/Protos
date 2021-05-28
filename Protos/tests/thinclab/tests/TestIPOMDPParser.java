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

import thinclab.decisionprocesses.IPOMDP;
import thinclab.exceptions.ParserException;
import thinclab.parsers.IPOMDPParser;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestIPOMDPParser {

	public String testFileName;
	public String testL1domain;

	private static Logger LOGGER;

	@BeforeEach
	void setUp() throws Exception {

		CustomConfigurationFactory.initializeLogging();
		LOGGER = LogManager.getLogger(TestIPOMDPParser.class);

		this.testFileName = "/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt";
		this.testL1domain = "/home/adityas/UGA/THINCLab/DomainFiles/final_domains/"
				+ "deception.single_host/defender.L1.spudd";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testParserInit() throws Exception {
		LOGGER.info("Running testParserInit()");
//		IPOMDPParser parser = new IPOMDPParser(this.testFileName);
		IPOMDPParser parser = new IPOMDPParser(this.testFileName);
		parser.parseDomain();
		assertNotNull(parser);

//		assertTrue(parser.childFrames.size() == 2);
//		assertEquals(0, parser.childFrames.get(0).level);
//		assertEquals(0, parser.childFrames.get(1).level);

		IPOMDP ipomdp = new IPOMDP();
		ipomdp.initializeFromParsers(parser);

		LOGGER.debug(ipomdp.Ti);
		LOGGER.debug(ipomdp.Oi);
		LOGGER.debug(ipomdp.lowerLevelGuessForAi);
		LOGGER.debug(ipomdp.lowerLevelAiProb);
		LOGGER.debug(ipomdp.lowerLevelAiDist);
	}

	@Test
	void testParserInit2() throws Exception {
		LOGGER.info("Running testParserInit()");
//		IPOMDPParser parser = new IPOMDPParser(this.testFileName);
		IPOMDPParser parser = new IPOMDPParser("/home/adityas/UGA/THINCLab/DomainFiles/"
				+ "CyberDeceptionDomainFiles/final_domains/deception.adaptive/" + "defender.L1.spudd");
		parser.parseDomain();
		assertNotNull(parser);

//		assertTrue(parser.childFrames.size() == 2);
//		assertEquals(0, parser.childFrames.get(0).level);
//		assertEquals(0, parser.childFrames.get(1).level);

		IPOMDP ipomdp = new IPOMDP(parser, 10, 3);
//		ipomdp.initializeFromParsers(parser);

		LOGGER.debug(ipomdp.Ti);
		LOGGER.debug(ipomdp.Oi);
		LOGGER.debug(ipomdp.lowerLevelGuessForAi);
		LOGGER.debug(ipomdp.lowerLevelAiProb);
		LOGGER.debug(ipomdp.lowerLevelAiDist);

		LOGGER.debug(ipomdp.currentTi);
	}

}
