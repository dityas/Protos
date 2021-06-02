import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.decisionprocesses.POMDP;
import thinclab.parsers.ParseSPUDD;

/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */

/*
 * @author adityas
 *
 */
class TestPOMDPSanity {
	
	private static final Logger LOGGER = LogManager.getLogger(TestPOMDPSanity.class);

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testPOMDPInit() {
		LOGGER.info("Checking if POMDPs can be initialized.");
		
		//ParseSPUDD parser = new ParseSPUDD("test_domains/tiger.95.SPUDD.txt");
		
		POMDP pomdp = new POMDP(this.getClass().getClassLoader().getResource("test_domains/tiger.95.SPUDD.txt").getFile());
	}

}
