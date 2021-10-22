import static org.junit.jupiter.api.Assertions.*;
import java.util.Collections;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thinclab.legacy.Global;
import thinclab.legacy.DD;
import thinclab.models.POMDP;
import thinclab.models.datastructures.PBVISolvableFrameSolution;
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

	private static final Logger LOGGER = LogManager.getLogger(TestIPOMDPCreation.class);

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
		/*
		LOGGER.info("Creating Mj space from single agent tiger problem POMDP");
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
		
		TwoWayMap<Tuple<Integer, DD>, String> mjMap = new TwoWayMap<>();
		var frameSoln = new PBVISolvableFrameSolution(0, I, 5);
		frameSoln.solve();
		
		var mjsList = frameSoln.mjList().stream().collect(Collectors.toList());
		
		LOGGER.debug(String.format("mjs are %s", mjsList));
		
		mjsList.stream().forEach(f ->
			{

				mjMap.put(Tuple.of(f._0(), f._1()), "m" + mjMap.k2v.size());
			});

		var sortedVals = mjMap.k2v.values().stream().collect(Collectors.toList());
		Collections.sort(sortedVals, (a, b) -> Integer.valueOf(a.split("m")[1]) - Integer.valueOf(b.split("m")[1]));
		
		LOGGER.debug(String.format("Renamed mjs are %s", sortedVals));
		var b_js = frameSoln.bMjList().stream().map(b -> mjMap.k2v.get(b)).collect(Collectors.toList());
		LOGGER.debug(String.format("b_js %s", b_js));
		LOGGER.debug(String.format("b_js index in Mj is %s", Collections.binarySearch(sortedVals, "m10")));
		*/
	}
}
