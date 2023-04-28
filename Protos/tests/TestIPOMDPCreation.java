import static org.junit.jupiter.api.Assertions.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thinclab.legacy.Global;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.models.POMDP;
import thinclab.models.IPOMDP.BjSpace;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.models.datastructures.PolicyGraph;
import thinclab.spuddx_parser.SpuddXMainParser;
import thinclab.utils.Tuple;
import thinclab.utils.TwoWayMap;

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
class TestIPOMDPCreation {

	private static final Logger LOGGER = 
        LogManager.getFormatterLogger(TestIPOMDPCreation.class);

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
	void testMjCreation() throws Exception {

		System.gc();
		LOGGER.info(
                "Creating Mj space from single agent tiger problem POMDP");

		String domainFile = this.getClass()
            .getClassLoader()
            .getResource("test_domains/test_tiger_domain.spudd")
            .getFile();

		// Run domain
		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		// Get agent I
		var I = (POMDP) domainRunner.getModel("agentI").orElseGet(() ->
			{

				LOGGER.error("Model not found");
				System.exit(-1);
				return null;
			});
		
        LOGGER.debug("Parsed POMDP %s", I);
        var G = PolicyGraph.getPolicyGraphFromModel(
                List.of(DDleaf.getDD(0.5f)), I);

        LOGGER.debug("Policy graph is %s", G);
        assertTrue(G.nodeMap.size() == 5);
        LOGGER.debug("Policy graph looks correct");

        LOGGER.debug("Checking Mj set creation");
        var mjs = IPOMDP.getOpponentModels(List.of(Tuple.of(0, G)));
        LOGGER.debug("Got mj set %s", mjs);
        assertTrue(G.nodeMap.size() == mjs.size());
        LOGGER.debug("Mj set creation look correct");
	}
}
