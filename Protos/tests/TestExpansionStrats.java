import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thinclab.DDOP;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.models.IPOMDP.IPOMDP;
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
class TestExpansionStrats {

	private static final Logger LOGGER = LogManager.getLogger(TestExpansionStrats.class);

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
	void testMjExpansionAtL1() throws Exception {

//		LOGGER.info("Checking Mj expansion at L1");
//		System.gc();
//
//		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_ipomdpl1.spudd").getFile();
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
//		var MG = I.framesjSoln.get(0).MG;
//		var m = I.framesj.get(0)._1();
//
//		LOGGER.debug(String.format("MG for L0 contains %s nodes for horizon %s", MG.getAllNodes().size(), I.H));
//
//		var edges = MG.getTriples();
//		LOGGER.debug(String.format("MG graph contains %s edges", edges.size()));
//
//		edges.forEach(e ->
//			{
//
//				var S = e._0();
//				var D = e._2();
//
//				var a = e._1().get(0) - 1;
//				var o = e._1().subList(1, e._1().size());
//
//				if (!S.equals(D)) {
//
//					S.beliefs.forEach(b ->
//						{
//
//							var b_n = m.beliefUpdate(b, a, o);
//							assertTrue(D.beliefs.contains(b_n));
//
//						});
//					LOGGER.debug(String.format("Edge: %s -- %s -> %s verified!", e._0().hashCode(), e._1(),
//							e._2().hashCode()));
//
//				}
//
//			});
	}

	@Test
	void testMjExpansionAtL2() throws Exception {

//		LOGGER.info("Checking Mj expansion at L2");
//		System.gc();
//
//		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_ipomdpl2.spudd").getFile();
//
//		// Run domain
//		var domainRunner = new SpuddXMainParser(domainFile);
//		domainRunner.run();
//
//		// Get agent I
//		var I = (IPOMDP) domainRunner.getModel("agentJl2").orElseGet(() ->
//			{
//
//				LOGGER.error("Model not found");
//				System.exit(-1);
//				return null;
//			});
//
//		var MG = I.framesjSoln.get(0).MG;
//		var m = I.framesj.get(0)._1();
//
//		LOGGER.debug(String.format("MG for L1 contains %s nodes for horizon %s", MG.getAllNodes().size(), I.H));
//
//		var edges = MG.getTriples();
//		LOGGER.debug(String.format("MG graph contains %s edges", edges.size()));
//
//		edges.forEach(e ->
//			{
//
//				LOGGER.debug(String.format("Checking edge %s", e));
//				var S = e._0();
//				var D = e._2();
//
//				var a = e._1().get(0) - 1;
//				var o = e._1().subList(1, e._1().size());
//
//				if (!S.equals(D)) {
//
//					S.beliefs.forEach(b ->
//						{
//
//							var b_n = m.beliefUpdate(b, a, o);
//
//							LOGGER.debug(String.format("Verifying %s for %s in %s", DDOP.factors(b_n, m.i_S()), e._1(),
//									D.beliefs.stream().map(_b -> DDOP.factors(_b, m.i_S()))
//											.collect(Collectors.toList())));
//
//							assertTrue(D.beliefs.stream()
//								.map(_b -> DDOP.maxAll(DDOP.abs(DDOP.sub(_b, b_n))))
//								.reduce(0.0f, (x, y) -> x <= y ? x : y) < 1e-5f);
//
//						});
//					LOGGER.debug(String.format("Edge: %s -- %s -> %s verified!", e._0().hashCode(), e._1(),
//							e._2().hashCode()));
//
//				}
//				
//				else
//					LOGGER.debug("Edge not explored. Self loop");
//
//			});
	}
	
	@Test
	void testIPOMDPL1Stepping() throws Exception {
		
		LOGGER.info("Checking L1 IPOMDP stepping");
		System.gc();

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

		var b = DDOP.reorder(
					DDOP.mult(
							DDnode.getDistribution(
									I.i_Mj, 
									List.of(Tuple.of("m0", 1.0f))), 
							DDleaf.getDD(0.5f)));
		
		LOGGER.debug(String.format("Actions are %s", Global.valNames.get(I.i_A - 1)));
		LOGGER.debug(String.format("Obs are %s", I.i_Om().stream().map(o -> Global.valNames.get(o - 1)).collect(Collectors.toList())));
		LOGGER.debug(String.format("Starting belief is %s", DDOP.factors(b, I.i_S())));
		
		var _b_n = I.beliefUpdate(b, 0, List.of(1, 3));
		var b1 = DDOP.factors(_b_n, I.i_S()).get(0);
		
		var b_n = I.step(b, 0, List.of(1, 3));
		var b2 = DDOP.factors(b_n, I.i_S()).get(0);
		
		LOGGER.debug(String.format("Next belief is %s", DDOP.factors(b_n, I.i_S())));
		assertTrue(DDOP.abs(DDOP.sub(b1, b2)).getVal() < 1e-5f);
	}

}
