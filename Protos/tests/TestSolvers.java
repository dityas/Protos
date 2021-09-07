import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;
import thinclab.model_ops.belief_exploration.BreadthFirstExploration;
import thinclab.model_ops.belief_exploration.PolicyGraphExpansion;
import thinclab.model_ops.belief_exploration.SSGAExploration;
import thinclab.models.IPOMDP;
import thinclab.models.POMDP;
import thinclab.models.datastructures.ModelGraph;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.models.datastructures.ReachabilityNode;
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

		var solver = new SymbolicPerseusSolver<POMDP>();
		var policy = solver.solve(I, 100, 10, AlphaVectorPolicy.fromR(I.R()));

		int bestAct = policy.getBestActionIndex(I.b_i(), I.i_S());

		LOGGER.info(String.format("Suggested optimal action for tiger problem is %s which resolves to %s", bestAct,
				I.A().get(bestAct)));

		assertTrue(bestAct == 0);
		assertTrue(policy.aVecs.size() == 5);

		LOGGER.debug(String.format("Solved policy is %s", policy));
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

		var policy = solver.solve(I, 100, I.H, AlphaVectorPolicy.fromR(I.R()));
		int bestAct = policy.getBestActionIndex(I.b_i(), I.i_S());

		LOGGER.info(String.format("Suggested optimal action for tiger problem is %s which resolves to %s", bestAct,
				I.A().get(bestAct)));

		assertTrue(bestAct == 0);

		DD b_ = I.b_i();

		LOGGER.info(String.format("Suggested optimal action for %s  is %s which resolves to %s",
				DDOP.factors(b_, I.i_S()), bestAct, I.A().get(bestAct)));

		assertTrue(bestAct == 0);

		LOGGER.info(String.format("Agent hears %s, %s ", Global.valNames.get(I.i_Om.get(0) - 1).get(0),
				Global.valNames.get(I.i_Om.get(1) - 1).get(2)));
		b_ = I.beliefUpdate(b_, bestAct, List.of(1, 3));
		bestAct = policy.getBestActionIndex(b_, I.i_S());

		assertTrue(bestAct == 0);

		System.gc();
		printMemConsumption();
	}

	@Test
	void testL1IPOMDPSolutionTree() throws Exception {

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

		var policy = solver.solve(I, 100, I.H, AlphaVectorPolicy.fromR(I.R()));
		int bestAct = policy.getBestActionIndex(I.b_i(), I.i_S());

		LOGGER.info(String.format("Suggested optimal action for tiger problem is %s which resolves to %s", bestAct,
				I.A().get(bestAct)));

		assertTrue(bestAct == 0);

		var G = ReachabilityGraph.fromDecMakingModel(I);
		var ES = new SSGAExploration<IPOMDP, ReachabilityGraph, AlphaVectorPolicy>(0.0f);

		int numNodes = G.getAllNodes().size();

		G = ES.expand(G, I, I.H, policy);

		assertTrue(G.getAllNodes().size() > numNodes);

		System.gc();
		printMemConsumption();
	}

	@Test
	void testPolicyGraphExpansion() throws Exception {

		System.gc();

		LOGGER.info("Running Single agent tiger problem to test policy trace");
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
		var policy = solver.solve(I, 100, 10, AlphaVectorPolicy.fromR(I.R()));

		var modelGraph = ModelGraph.fromDecMakingModel(I);
		var expansionStrat = new PolicyGraphExpansion<ReachabilityNode, POMDP, AlphaVectorPolicy>();
		
		var initNode = new ReachabilityNode(-1);
		initNode.beliefs.add(DDleaf.getDD(0.5f));
		
		modelGraph = expansionStrat.expand(List.of(initNode), modelGraph, I, policy);
		
		printMemConsumption();
	}

}
