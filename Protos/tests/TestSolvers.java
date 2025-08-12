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
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.model_ops.belief_exploration.MDPExploration;
import thinclab.model_ops.belief_exploration.MjSpaceExpansion;
import thinclab.model_ops.belief_exploration.PolicyGraphExpansion;
import thinclab.models.POMDP;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.models.datastructures.ModelGraph;
import thinclab.models.datastructures.ReachabilityNode;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.QMDPSolver;
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.spuddx_parser.SpuddXMainParser;
import thinclab.utils.Tuple;


class TestSolvers {

	private static final Logger LOGGER = 
        LogManager.getFormatterLogger(TestSolvers.class);

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
	void testQMDPSolver() throws Exception {

		System.gc();

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

        var solver = new QMDPSolver(1000, 0.001f);
        var policy = solver.solve(I);
	}
//
//	@Test
//	void testQMDPSolverForIPOMDP() throws Exception {
//
//		System.gc();
//
//		String domainFile = this.getClass().getClassLoader()
//            .getResource("test_domains/test_ipomdpl1.spudd").getFile();
//
//		// Run domain
//		var domainRunner = new SpuddXMainParser(domainFile);
//		domainRunner.run();
//
//		// Get agent I
//		var I = (IPOMDP) domainRunner.getModel("agentI").orElseGet(() ->
//			{
//
//				LOGGER.error("Model not found");
//				System.exit(-1);
//				return null;
//			});
//
//        List<DD> Qfn = I.R().stream()
//            .map(r -> DDOP.addMultVarElim(List.of(r, I.PAjGivenEC), List.of(I.i_Aj)))
//            .collect(Collectors.toList());
//
//        LOGGER.info("Initial Qfn is %s", Qfn);
//        var nextQfn = I.MDPValueIteration(Qfn);
//        LOGGER.info("Next Qfn is %s", nextQfn);
//		
//		System.gc();
//		printMemConsumption();
//	}
//
//	@Test
//	void testBasicPOMDPPerseusSolver() throws Exception {
//
//		System.gc();
//
//		LOGGER.info("Running Single agent tiger domain belief exploration test");
//		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_tiger_domain.spudd")
//				.getFile();
//
//		// Run domain
//		var domainRunner = new SpuddXMainParser(domainFile);
//		domainRunner.run();
//
//		// Get agent I
//		var I = (POMDP) domainRunner.getModel("agentI").orElseGet(() ->
//			{
//
//				LOGGER.error("Model not found");
//				System.exit(-1);
//				return null;
//			});
//
//		var solver = new SymbolicPerseusSolver<POMDP>(300, 10);
//        solver.putM(I);
//		var policy = solver.solve(List.of(DDleaf.getDD(0.5f)), 100, 10);
//
//		int bestAct = policy.getBestActionIndex(DDleaf.getDD(0.5f));
//
//		LOGGER.info(String.format("Suggested optimal action for tiger problem is %s which resolves to %s", bestAct,
//				I.A().get(bestAct)));
//
//		assertTrue(bestAct == 0);
//		assertTrue(policy.size() == 5);
//
//		LOGGER.debug(String.format("Solved policy is %s", policy));
//		printMemConsumption();
//
//		LOGGER.info("Testing MjSpace representation");
//		var initNodes = List.of(DDleaf.getDD(0.5f)).stream()
//				.map(d -> ReachabilityNode.getStartNode(policy.getBestActionIndex(d), d))
//				.collect(Collectors.toList());
//
//		var modelGraph = ModelGraph.fromDecMakingModel(I);
//		var expStrat = new MjSpaceExpansion<>(); /* new PolicyGraphExpansion<>(); */
//
//		modelGraph = expStrat.expand(initNodes, modelGraph, I, 5, policy);
//		LOGGER.debug(String.format("After expanding the MjSpace graph, no. of models are %s",
//				modelGraph.getAllNodes().size()));
//		LOGGER.debug(String.format("Graph is %s", ModelGraph.toDot(modelGraph, I)));
//
//	}
//
}
