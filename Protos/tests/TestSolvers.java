import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.model_ops.belief_exploration.BreadthFirstExploration;
import thinclab.model_ops.belief_exploration.SSGAExploration;
import thinclab.models.IPOMDP;
import thinclab.models.POMDP;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.SymbolicPerseusSolver;
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

		var solver = new SymbolicPerseusSolver<POMDP>();
		var policy = solver.solve(List.of(I.b_i()), I, 100, 10, new SSGAExploration<>(0.1f),
				AlphaVectorPolicy.fromR(I.R()));

		int bestAct = policy.getBestActionIndex(I.b_i(), I.i_S());

		LOGGER.info(String.format("Suggested optimal action for tiger problem is %s which resolves to %s", bestAct,
				I.A().get(bestAct)));

		assertTrue(bestAct == 0);
		assertTrue(policy.aVecs.size() == 5);

		LOGGER.debug(String.format("Solved policy is %s", policy));
		printMemConsumption();
	}

	@Test
	void testL1IPOMDPGaoi() throws Exception {

		System.gc();

		LOGGER.info("Testing L1 IPOMDP Gaoi on single frame tiger problem");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_ipomdpl1.spudd").getFile();

		// Run domain
		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		// Get agent I
		var I = (IPOMDP) domainRunner.getModel("agentI").orElseGet(() ->
			{

				LOGGER.error("Model not found");
				System.exit(-1);
				return null;
			});

		LOGGER.debug(String.format("Gaoi for %s is %s", I.A().get(0), I.Gaoi(I.b_i(), 0, I.R())));

		printMemConsumption();
	}

	@Test
	void testL1IPOMDPSolution() throws Exception {

		System.gc();

		LOGGER.info("Testing L1 IPOMDP Solver Gaoi on single frame tiger problem");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_ipomdpl1.spudd").getFile();

		// Run domain
		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		// Get agent I
		var I = (IPOMDP) domainRunner.getModel("agentI").orElseGet(() ->
			{

				LOGGER.error("Model not found");
				System.exit(-1);
				return null;
			});

		var solver = new SymbolicPerseusSolver<IPOMDP>();
		var policy = solver.solve(List.of(I.b_i()), I, 100, I.H,
				new SSGAExploration<IPOMDP, ReachabilityGraph, AlphaVectorPolicy>(0.1f),
				AlphaVectorPolicy.fromR(I.R()));
		int bestAct = policy.getBestActionIndex(I.b_i(), I.i_S());

		LOGGER.info(String.format("Suggested optimal action for tiger problem is %s which resolves to %s", bestAct,
				I.A().get(bestAct)));

		assertTrue(bestAct == 0);

		LOGGER.debug(String.format("Solved policy is %s", policy));

		printMemConsumption();
	}

}
