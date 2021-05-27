/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.decisionprocesses.POMDP;
import thinclab.legacy.DD;
import thinclab.legacy.OP;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestBoundedRationalityExtensions {
	
	private static Logger LOGGER;
	public POMDP pomdp;

	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		LOGGER = Logger.getLogger(TestBoundedRationalityExtensions.class);
		this.pomdp = 
				new POMDP(
						"/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testInit() {
		LOGGER.info("Starting tests");
		
		LOGGER.debug("O for action listen: ");
		for (DD OiA : this.pomdp.getOiForAction("listen")) {
			LOGGER.debug(OiA.toDDTree().toString());
		}
		
		LOGGER.debug("Testing how exponents work");
		for (DD OiA : this.pomdp.getOiForAction("listen")) {
			LOGGER.debug(OP.exp(OiA).toDDTree().toString());
		}

	}

}
