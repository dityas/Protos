import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.model_ops.belief_exploration.POMDPBreadthFirstBeliefExploration;
import thinclab.model_ops.belief_update.POMDPBeliefUpdate;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.solver.POMDPSymbolicPerseusSolver;
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
class TestSolvers {

	private static final Logger LOGGER = LogManager.getLogger(TestSolvers.class);

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
	void testBasicPOMDPPerseusSolver() throws Exception {

		System.gc();

		LOGGER.info("Running Single agent tiger domain belief exploration test");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_tiger_domain.spudd")
				.getFile();

		// Parse domain
		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);
		var randomVars = parserWrapper.getVariableDeclarations();

		// Initialize random variables
		Global.primeVarsAndInitGlobals(randomVars);

		// Get POMDP models
		var models = parserWrapper.getModels();
		var pomdps = SpuddXParserWrapper.getPOMDPs(models);

		models.clear();
		models = null;
		parserWrapper = null;

		// Get agent I
		var I = pomdps.get("agentI");

		// Initialize belief update mechanism
		var BU = new POMDPBeliefUpdate();

		// Make action observation space for agent I
		var obsVars = Arrays.stream(I.Ovars).mapToObj(i -> Global.valNames.get(i - 1)).collect(Collectors.toList());
		obsVars.add(Global.valNames.get(I.Avar - 1));
		var aoSpace = OP.cartesianProd(obsVars);

		// Make belief region for agent I
		var beliefGraph = new ReachabilityGraph(aoSpace);
		beliefGraph.addNode(I.b);

		// Initialize belief exploration
		var BE = new POMDPBreadthFirstBeliefExploration(200);

		for (int i = 0; i < 10; i++)
			beliefGraph = BE.expandRG(I, BU, beliefGraph);
		
		var solver = new POMDPSymbolicPerseusSolver();
		
		long then = System.nanoTime();
		solver.solve(I, BU, beliefGraph, BE);
		long now = System.nanoTime();
		
		float T = (now - then) / 1000.0f;
		LOGGER.debug(String.format("solver took %s us", T));

		LOGGER.debug(String.format("Graph contains %s nodes", beliefGraph.getAllNodes().size()));
		printMemConsumption();
	}
}
