import static org.junit.jupiter.api.Assertions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.models.POMDP;
import thinclab.spuddx_parser.SpuddXMainParser;

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
class TestSimulations {

	private static final Logger LOGGER = LogManager.getLogger(TestSimulations.class);

	@BeforeEach
	void setUp() throws Exception {

		Global.clearAll();
	}

	@AfterEach
	void tearDown() throws Exception {

		Global.clearAll();
	}

	void printMemConsumption() throws Exception {

		var total = Runtime.getRuntime().totalMemory() / 1000000.0;
		var free = Runtime.getRuntime().freeMemory() / 1000000.0;

		LOGGER.info(String.format("Free mem: %s", free));
		LOGGER.info(String.format("Used mem: %s", (total - free)));
		Global.logCacheSizes();
	}
	
	@Test
	void testSingleAgentEnvInit() throws Exception {
	
		System.gc();

		LOGGER.info("Testing L1 IPOMDP Solver Gaoi on single frame tiger problem");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_ipomdpl1_env.spudd").getFile();

		// Run domain
		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		// Get agent I
		var J = (POMDP) domainRunner.getModel("agentJ").orElseGet(() ->
			{

				LOGGER.error("Model not found");
				System.exit(-1);
				return null;
			});
		
		LOGGER.debug(String.format("Got a hold of POMDP agent %s", J));
		
		var env = domainRunner.envs.get("tigerEnv");
		LOGGER.debug(String.format("Got env %s", env));
		
		LOGGER.debug("Making initial state");
		var s = DDnode.getDDForChild("TigerLoc", "TR");
		
		LOGGER.debug(String.format("Setting initial state to %s", s));
		env.init(s);
		
		

	}


}
