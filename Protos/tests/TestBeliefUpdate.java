import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.model_ops.belief_update.POMDPBeliefUpdate;
import thinclab.models.datastructures.ReachabilityGraph;
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
	void testTigerProblemSSGABeliefExploration() throws Exception {

		System.gc();

		LOGGER.info("Running Single agent tiger domain belief exploration test");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_tiger_domain.spudd")
				.getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);
		var randomVars = parserWrapper.getVariableDeclarations();

		Global.primeVarsAndInitGlobals(randomVars);

		var models = parserWrapper.getModels();
		var pomdps = SpuddXParserWrapper.getPOMDPs(models);

		models.clear();
		models = null;
		parserWrapper = null;

		var I = pomdps.get("agentI");
		var obsVars = Arrays.stream(I.Ovars).mapToObj(i -> Global.valNames.get(i - 1)).collect(Collectors.toList());
		obsVars.add(Global.valNames.get(I.Avar - 1));
	
		var beliefGraph = new ReachabilityGraph(OP.cartesianProd(obsVars));
		beliefGraph.addNode(I.b);
		
		LOGGER.debug(String.format("Graph is %s", beliefGraph));
		printMemConsumption();
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

		models.clear();
		models = null;
		parserWrapper = null;
		System.gc();

		var I = pomdps.get("agentI");
		var BE = new POMDPBeliefUpdate();

		DD initBelief = I.b;
		DD bListenGL = BE.beliefUpdate(I, initBelief, "L", Collections.singletonList("GL"));

		DD tl = DDleaf.getDD(0.85f);
		DD tr = DDleaf.getDD(0.15f);
		DD bPrime = DDnode.getDD(1, new DD[] { tl, tr });

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

	@Test
	void timeSimpleTigerProblemBeliefUpdate() throws Exception {

		System.gc();

		LOGGER.info("Timing Single agent tiger domain belief update with string based obs and actions");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_tiger_domain.spudd")
				.getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);
		var randomVars = parserWrapper.getVariableDeclarations();

		Global.primeVarsAndInitGlobals(randomVars);

		var models = parserWrapper.getModels();
		var pomdps = SpuddXParserWrapper.getPOMDPs(models);

		models.clear();
		models = null;
		parserWrapper = null;
		System.gc();

		var I = pomdps.get("agentI");
		var BE = new POMDPBeliefUpdate();

		var rand = new Random();
		var actions = (ArrayList<String>) IntStream.range(0, 1000).mapToObj(i -> I.A.get(rand.nextInt(3)))
				.collect(Collectors.toList());
		var obs = OP.cartesianProd(
				I.O.stream().map(o -> Global.valNames.get(Global.varNames.indexOf(o))).collect(Collectors.toList()));
		var os = IntStream.range(0, 1000).mapToObj(i -> obs.get(rand.nextInt(2))).collect(Collectors.toList());

		DD b = I.b;

		long then = System.nanoTime();
		for (int i = 0; i < 1000; i++) {

			b = BE.beliefUpdate(I, b, actions.get(i), os.get(i));
		}

		LOGGER.debug(String.format("Last belief is %s for action %s", b, actions.get(999)));
		long now = System.nanoTime();
		long timeElapsed = (now - then);
		float avgTime = timeElapsed / 1000.0f;

		LOGGER.info(String.format("1000 random belief updates on the tiger problem took %s ns total and %s ns on avg",
				timeElapsed, avgTime));

	}

}
