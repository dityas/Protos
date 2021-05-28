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
import thinclab.parsers.IPOMDPParser;
import thinclab.utils.CustomConfigurationFactory;
import thinclab.utils.MjDB;

/*
 * @author adityas
 *
 */
class TestMjDB {

	public String l1DomainFile;
	private static Logger LOGGER;

	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		LOGGER = LogManager.getLogger(TestBenchmarkNZPrimeStorage.class);
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testDBStart() {
		LOGGER.info("Testing IPOMDP stochastic sim");
		
		IPOMDPParser parser = 
				new IPOMDPParser("/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		parser.parseDomain();
		
		/* Initialize IPOMDP */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 3, 20);
	}

}
