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
	void testIBeliefOps1Frame() throws Exception {
		LOGGER.info("Running testIBeliefOps1Frame()");
		
		/* Initialize IPOMDP */
		LOGGER.info("Initializing IPOMDP");
		IPOMDPParser parser = new IPOMDPParser(l1DomainFile);
		parser.parseDomain();
		
		this.ipomdp = new IPOMDP(parser, 10, 3);
		LOGGER.info("IPOMDP initialized");
		
		LOGGER.info("Checking DD to hashmap conversion for beliefs");
		LOGGER.debug("Checking for consistency in number of states");
		assertEquals(
				this.ipomdp.toMap(this.ipomdp.getCurrentBelief()).size(),
				this.ipomdp.S.subList(0, this.ipomdp.thetaVarPosition).size());
		
		/* start from initial belief */
		LOGGER.info("Starting from initial belief");
		DD start = this.ipomdp.getCurrentBelief();
		LOGGER.debug("Initial belief is " + start.toDDTree());
		
		LOGGER.info("Taking action listen and observing growl-left, silence");
		start = this.ipomdp.getCurrentBelief();
		LOGGER.debug("Starting from " + this.ipomdp.toMap(start));
		DD nextBel = this.ipomdp.beliefUpdate(start, "listen", new String[] {"growl-left", "silence"});
		LOGGER.debug("Next belief is " + this.ipomdp.toMap(nextBel));
		
		HashMap<String, HashMap<String, Float>> map = this.ipomdp.toMap(nextBel);
		LOGGER.info("Verifying if state transitions make sense");
		assertEquals((float) 0.85, map.get("tiger-location").get("tiger-left"));
		
		LOGGER.info("Taking action listen and observing growl-right, silence");
		start = nextBel;
		LOGGER.debug("Starting from " + this.ipomdp.toMap(start));
		nextBel = 
				this.ipomdp.beliefUpdate(start, "listen", new String[] {"growl-right", "silence"});
		LOGGER.debug("Next belief is " + this.ipomdp.toMap(nextBel));
		
		map = this.ipomdp.toMap(nextBel);
		LOGGER.info("Verifying if state transitions make sense");
		assertEquals((float) 0.5, map.get("tiger-location").get("tiger-left"));
		
	}
	
	@Test
	void testIBeliefOps2Frames() throws Exception {
		LOGGER.info("Running testIBeliefOps2Frame()");
		
		/* Initialize IPOMDP */
		LOGGER.info("Initializing IPOMDP");
		IPOMDPParser parser = new IPOMDPParser(l1DomainMultipleFrames);
		parser.parseDomain();
		
		this.ipomdp = new IPOMDP(parser, 10, 3);
		LOGGER.info("IPOMDP initialized");
		
		LOGGER.info("Checking DD to hashmap conversion for beliefs");
		LOGGER.debug("Checking for consistency in number of states");
		LOGGER.debug("Current belief: " + this.ipomdp.toMap(this.ipomdp.getCurrentBelief()));
		assertEquals(
				this.ipomdp.toMap(this.ipomdp.getCurrentBelief()).size(),
				this.ipomdp.S.subList(0, this.ipomdp.thetaVarPosition).size());
		
		/* start from initial belief */
		LOGGER.info("Starting from initial belief");
		DD start = this.ipomdp.getCurrentBelief();
		LOGGER.debug("Initial belief is " + start.toDDTree());
		
		LOGGER.info("Taking action listen and observing growl-left, silence");
		start = this.ipomdp.getCurrentBelief();
		LOGGER.debug("Starting from " + this.ipomdp.toMapWithTheta(start));
		DD nextBel = this.ipomdp.beliefUpdate(start, "listen", new String[] {"growl-left", "silence"});
		LOGGER.debug("Next belief is " + this.ipomdp.toMapWithTheta(nextBel));
		
		HashMap<String, HashMap<String, Float>> map = this.ipomdp.toMap(nextBel);
		LOGGER.info("Verifying if state transitions make sense");
		assertEquals((float) 0.85, map.get("tiger-location").get("tiger-left"));
		
		LOGGER.info("Taking action listen and observing growl-right, silence");
		start = nextBel;
		LOGGER.debug("Starting from " + this.ipomdp.toMapWithTheta(start));
		nextBel = 
				this.ipomdp.beliefUpdate(start, "listen", new String[] {"growl-right", "silence"});
		LOGGER.debug("Next belief is " + this.ipomdp.toMapWithTheta(nextBel));
		
		map = this.ipomdp.toMap(nextBel);
		LOGGER.info("Verifying if state transitions make sense");
		assertEquals((float) 0.5, map.get("tiger-location").get("tiger-left"));
		
		LOGGER.info("Taking action listen and observing growl-right, silence");
		start = nextBel;
		LOGGER.debug("Starting from " + this.ipomdp.toMapWithTheta(start));
		nextBel = 
				this.ipomdp.beliefUpdate(start, "listen", new String[] {"growl-right", "silence"});
		LOGGER.debug("Next belief is " + this.ipomdp.toMapWithTheta(nextBel));
		
		map = this.ipomdp.toMap(nextBel);
		LOGGER.info("Verifying if state transitions make sense");
		assertTrue((float) 0.5 > map.get("tiger-location").get("tiger-left"));
		
		LOGGER.info("Taking action listen and observing growl-right, silence");
		start = nextBel;
		LOGGER.debug("Starting from " + this.ipomdp.toMapWithTheta(start));
		nextBel = 
				this.ipomdp.beliefUpdate(start, "listen", new String[] {"growl-right", "silence"});
		LOGGER.debug("Next belief is " + this.ipomdp.toMapWithTheta(nextBel));
		
		map = this.ipomdp.toMap(nextBel);
		LOGGER.info("Verifying if state transitions make sense");
		assertTrue((float) 0.5 > map.get("tiger-location").get("tiger-left"));
		
		LOGGER.info("Taking action listen and observing growl-right, silence");
		start = nextBel;
		LOGGER.debug("Starting from " + this.ipomdp.toMapWithTheta(start));
		nextBel = 
				this.ipomdp.beliefUpdate(start, "listen", new String[] {"growl-right", "silence"});
		LOGGER.debug("Next belief is " + this.ipomdp.toMapWithTheta(nextBel));
		
		map = this.ipomdp.toMap(nextBel);
		LOGGER.info("Verifying if state transitions make sense");
		assertTrue((float) 0.5 > map.get("tiger-location").get("tiger-left"));
	}
	
}
