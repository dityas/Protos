import static org.junit.jupiter.api.Assertions.*;
import java.util.Collections;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.model_ops.POMDPBeliefUpdate;
import thinclab.spuddx_parser.SpuddXParserWrapper;

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
class TestBeliefUpdate {

	private static final Logger LOGGER = LogManager.getLogger(TestBeliefUpdate.class);

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
	void testSimpleTigerProblemBeliefUpdate() throws Exception {

		System.gc();

		LOGGER.info("Running Single agent tiger domain belief update test");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_tiger_domain.spudd")
				.getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);
		var randomVars = parserWrapper.getVariableDeclarations();

		Global.primeVarsAndInitGlobals(randomVars);

		var models = parserWrapper.getModels();
		var pomdps = SpuddXParserWrapper.getPOMDPs(models);

		var I = pomdps.get("agentI");
		var BE = new POMDPBeliefUpdate();
		
		DD initBelief = I.b;
		DD bListenGL = BE.beliefUpdate(I, initBelief, "L", Collections.singletonList("GL"));
		
		DD tl = DDleaf.getDD(0.85f);
		DD tr = DDleaf.getDD(0.15f);
		DD bPrime = DDnode.getDD(1, new DD[] {tl, tr});
		
		LOGGER.debug(String.format("Initial belief: %s", initBelief));
		LOGGER.debug(String.format("After update: %s", bListenGL));
		LOGGER.debug(String.format("Expected to be: %s", bPrime));
		
		printMemConsumption();
		assertTrue(OP.abs(OP.sub(bListenGL, bPrime)).getVal() < 1e-8f);
		
		DD bListenGR = BE.beliefUpdate(I, bListenGL, "L", Collections.singletonList("GR"));
	
		bPrime = DDleaf.getDD(0.5f);
		
		LOGGER.debug(String.format("Initial belief: %s", bListenGL));
		LOGGER.debug(String.format("After update: %s", bListenGR));
		LOGGER.debug(String.format("Expected to be: %s", bPrime));
		
		printMemConsumption();
		assertTrue(OP.abs(OP.sub(bListenGR, bPrime)).getVal() < 1e-8f);

	}

}
