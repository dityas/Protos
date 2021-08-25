import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
	void testPOMDPGaoi() throws Exception {

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

		var g = ReachabilityGraph.fromDecMakingModel(I);
		var _g = new BreadthFirstExploration<DD, POMDP, ReachabilityGraph, AlphaVectorPolicy>(100).expand(g, I, 5,
				null);

		IntStream.range(0, I.A().size()).forEach(i ->
			{

				LOGGER.debug(String.format("backup for %s at %s is %s", I.A().get(i), I.b_i(),
						I.backup(I.b_i(), I.R(), _g)));
			});
		printMemConsumption();
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
		/*
		var G = ReachabilityGraph.fromDecMakingModel(I);
		G.addNode(I.b_i());
		G = new BreadthFirstExploration<DD, POMDP, ReachabilityGraph, AlphaVectorPolicy>(100).expand(G, I, 5, null);

		var solver = new SymbolicPerseusSolver<POMDP>();
		var policy = solver.solve(I, 100, 10, AlphaVectorPolicy.fromR(I.R()));

		int bestAct = policy.getBestActionIndex(I.b_i(), I.i_S());

		LOGGER.info(String.format("Suggested optimal action for tiger problem is %s which resolves to %s", bestAct,
				I.A().get(bestAct)));

		assertTrue(bestAct == 0);
		assertTrue(policy.aVecs.size() == 5);

		LOGGER.debug(String.format("Solved policy is %s", policy)); */
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

		var G = ReachabilityGraph.fromDecMakingModel(I);
		G.addNode(I.b_i());

		G = new BreadthFirstExploration<DD, IPOMDP, ReachabilityGraph, AlphaVectorPolicy>(100).expand(G, I, 5, null);

		LOGGER.debug(String.format("Gaoi for %s is %s", I.A().get(0), I.Gaoi(0, I.b_i(), I.R(), G)));
		LOGGER.debug(String.format("backup at %s is %s", I.b_i(), I.backup(I.b_i(), I.R(), G)));

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
		
		//LOGGER.debug(String.format("P(Aj|Mj) is %s", I.PAjGivenMj));
		//LOGGER.debug(String.format("P(Mj'|Mj,Aj,Oj') is %s", I.PMj_pGivenMjAjOj_p));

		var solver = new SymbolicPerseusSolver<IPOMDP>();
		var G = ReachabilityGraph.fromDecMakingModel(I);
		G.addNode(I.b_i());

		G = new BreadthFirstExploration<DD, IPOMDP, ReachabilityGraph, AlphaVectorPolicy>(100).expand(G, I, I.H, null);

		var policy = solver.solve(I, 100, I.H, AlphaVectorPolicy.fromR(I.R()));
		int bestAct = policy.getBestActionIndex(I.b_i(), I.i_S());

		LOGGER.info(String.format("Suggested optimal action for tiger problem is %s which resolves to %s", bestAct,
				I.A().get(bestAct)));

		LOGGER.info(String.format("Policy is %s", policy.aVecs.stream().map(a -> a._0()).collect(Collectors.toList())));

		assertTrue(bestAct == 0);

		DD b_ = I.b_i();

		for (int i = 0; i < I.H; i++) {

			LOGGER.info(String.format("Suggested optimal action for %s  is %s which resolves to %s",
					DDOP.factors(b_, I.i_S()), bestAct, I.A().get(bestAct)));

			LOGGER.info(String.format("Agent hears %s, %s ", Global.valNames.get(I.i_Om.get(0) - 1).get(0),
					Global.valNames.get(I.i_Om.get(1) - 1).get(2)));
			b_ = I.beliefUpdate(b_, bestAct, List.of(1, 3));
			bestAct = policy.getBestActionIndex(b_, I.i_S());
		}
		
		LOGGER.debug(String.format("Policy is %s", policy));
		
		System.gc();
		printMemConsumption();
	}

}
