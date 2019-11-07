/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.ddinterface.DDTree;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.parsers.IPOMDPParser;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestBeliefOperations {

	String l1DomainFile;
	String l1DomainMultipleFrames;
	
	IPOMDP ipomdp;
	POMDP pomdp;
	
	private static Logger LOGGER;
	
	@BeforeEach
	void setUp() throws Exception {
		
		CustomConfigurationFactory.initializeLogging();
		LOGGER = Logger.getLogger(TestBeliefOperations.class);
		
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
		this.l1DomainMultipleFrames =
				"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testL1InteractiveBeliefUpdate1Frame() throws Exception {
		LOGGER.info("Running testL1InteractiveBeliefUpdate1Frame()");
		
		/* Initialize IPOMDP */
		LOGGER.info("Initializing IPOMDP");
		IPOMDPParser parser = new IPOMDPParser(l1DomainFile);
		parser.parseDomain();
		
		this.ipomdp = new IPOMDP(parser, 10, 3);
		LOGGER.info("IPOMDP initialized");
		
		/* start from initial belief */
		LOGGER.info("Starting from initial belief");
		DD start = this.ipomdp.getCurrentBelief();
		LOGGER.debug("Initial belief is " + start.toDDTree());
		
		
	}
	
}
