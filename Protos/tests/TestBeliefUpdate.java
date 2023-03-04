import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import thinclab.model_ops.belief_exploration.BreadthFirstExploration;
import thinclab.models.POMDP;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.spuddx_parser.SpuddXMainParser;
import thinclab.utils.Tuple;

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

		// Parse domain
		var runner = new SpuddXMainParser(domainFile);
		runner.run();

		var I = (POMDP) runner.getModel("agentI").get();

		// Make belief region for agent I
		var beliefGraph = ReachabilityGraph.fromDecMakingModel(I);
		beliefGraph.addNode(DDleaf.getDD(0.5f));

	}

	@Test
	void testSimpleTigerProblemBeliefUpdate() throws Exception {

		System.gc();

		LOGGER.info("Running Single agent tiger domain belief update test");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_tiger_domain.spudd")
				.getFile();

		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		var I = (POMDP) domainRunner.getModel("agentI").orElseGet(() ->
			{

				LOGGER.error("Could not find POMDP agentI");
				System.exit(-1);
				return null;
			});

		System.gc();

		DD initBelief = DDleaf.getDD(0.5f);
		DD bListenGL = I.beliefUpdate(initBelief, "L", Collections.singletonList("GL"));

		DD tl = DDleaf.getDD(0.85f);
		DD tr = DDleaf.getDD(0.15f);
		DD bPrime = DDnode.getDD(1, new DD[] { tl, tr });

		LOGGER.debug(String.format("Initial belief: %s", initBelief));
		LOGGER.debug(String.format("After update: %s", bListenGL));
		LOGGER.debug(String.format("Expected to be: %s", bPrime));

		printMemConsumption();
		assertTrue(DDOP.abs(DDOP.sub(bListenGL, bPrime)).getVal() < 1e-8f);

		DD bListenGR = I.beliefUpdate(bListenGL, "L", Collections.singletonList("GR"));

		bPrime = DDleaf.getDD(0.5f);

		LOGGER.debug(String.format("Initial belief: %s", bListenGL));
		LOGGER.debug(String.format("After update: %s", bListenGR));
		LOGGER.debug(String.format("Expected to be: %s", bPrime));

		printMemConsumption();
		assertTrue(DDOP.abs(DDOP.sub(bListenGR, bPrime)).getVal() < 1e-8f);

	}

	@Test
	void testL1TigerProblemlikelihood() throws Exception {

		System.gc();

		LOGGER.info("Running Single agent tiger domain belief update test");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_ipomdpl1.spudd").getFile();

		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		var I = (IPOMDP) domainRunner.getModel("agentI").orElseGet(() ->
			{

				LOGGER.error("Could not find IPOMDP agentI");
				System.exit(-1);
				return null;
			});

		System.gc();

		//DD initBelief = ;
		//var likelihoods = DDOP.factors(I.obsLikelihoods(initBelief, 0), I.i_Om_p());

		//LOGGER.debug(String.format("Initial belief: %s", initBelief));
		//LOGGER.debug(String.format("Likelihoods: %s", likelihoods));
	}

	@Test
	void testL1TigerProblemBeliefUpdate() throws Exception {

		System.gc();

		LOGGER.info("Testing Single agent tiger domain belief update");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_ipomdpl1.spudd").getFile();

		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		var I = (IPOMDP) domainRunner.getModel("agentI").orElseGet(() ->
			{

				LOGGER.error("Could not find IPOMDP agentI");
				System.exit(-1);
				return null;
			});

		System.gc();

		var obs = DDOP.cartesianProd(
				I.O.stream().map(o -> Global.valNames.get(Global.varNames.indexOf(o))).collect(Collectors.toList()));

		DD b = DDleaf.getDD(0.5f);
		var beliefs = new ArrayList<DD>();
		var aos = new ArrayList<Tuple<Integer, List<Integer>>>();

		for (int a = 0; a < I.A().size(); a++) {

			for (int o = 0; o < I.oAll.size(); o++) {

				beliefs.add(I.beliefUpdate(b, a, I.oAll.get(o)));
				aos.add(Tuple.of(a, I.oAll.get(o)));
			}
		}
		/*
		IntStream.range(0, aos.size()).forEach(i ->
			{

				var ao = aos.get(i);
				var a = I.A().get(ao._0());

				var initMjBeliefs = DDOP.factors(DDleaf.getDD(0.5f), I.i_S()).get(1).getChildren();
				var initL0Beliefs = IntStream.range(0, initMjBeliefs.length).boxed()
						.filter(j -> !initMjBeliefs[j].equals(DDleaf.getDD(0.0f)))
						.map(j -> Global.valNames.get(I.i_Mj - 1).get(j)).collect(Collectors.toList());

				var MjBeliefs = DDOP.factors(beliefs.get(i), I.i_S()).get(1).getChildren();
				var l0Beliefs = IntStream.range(0, MjBeliefs.length).boxed()
						.filter(j -> !MjBeliefs[j].equals(DDleaf.getDD(0.0f)))
						.map(j -> Global.valNames.get(I.i_Mj - 1).get(j)).collect(Collectors.toList());

				LOGGER.debug(String.format("Checking for %s from %s", l0Beliefs, initL0Beliefs));
				LOGGER.debug(String.format("%s is %s", initL0Beliefs,
						initL0Beliefs.stream().map(j -> I.mjMap.v2k.get(j)).collect(Collectors.toList())));

			});
		*/
	}

	@Test
	void testL2TigerProblemBeliefUpdate() throws Exception {

		System.gc();

		LOGGER.info("Testing Single agent tiger domain belief update for level 2");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_ipomdpl2.spudd").getFile();

		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		var I = (IPOMDP) domainRunner.getModel("agentJl2").orElseGet(() ->
			{

				LOGGER.error("Could not find IPOMDP agentI");
				System.exit(-1);
				return null;
			});

		System.gc();

		var obs = DDOP.cartesianProd(
				I.O.stream().map(o -> Global.valNames.get(Global.varNames.indexOf(o))).collect(Collectors.toList()));
		/*
		DD b = I.b_i();
		var beliefs = new ArrayList<DD>();
		var aos = new ArrayList<Tuple<Integer, List<Integer>>>();

		for (int a = 0; a < I.A().size(); a++) {

			for (int o = 0; o < I.oAll.size(); o++) {

				beliefs.add(I.beliefUpdate(b, a, I.oAll.get(o)));
				aos.add(Tuple.of(a, I.oAll.get(o)));
			}
		}

		IntStream.range(0, aos.size()).forEach(i ->
			{

				var ao = aos.get(i);
				var a = I.A().get(ao._0());

				LOGGER.debug(String.format("Starting from %s, for action %s and obs %s, the update is %s",
						DDOP.factors(b, I.i_S()), a, ao._1(), DDOP.factors(beliefs.get(i), I.i_S())));

			});
		*/
	}

}
