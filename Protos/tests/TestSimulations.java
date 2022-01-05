import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thinclab.env.PartiallyObservableEnv;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.models.POMDP;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.SymbolicPerseusSolver;
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
class TestSimulations {

	private static final Logger LOGGER = LogManager.getLogger(TestSimulations.class);

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
	void testSingleAgentEnvInit() throws Exception {
	
		System.gc();

		LOGGER.info("Testing L1 IPOMDP Solver Gaoi on single frame tiger problem");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_ipomdpl1_env.spudd").getFile();

		// Run domain
		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		// Get agent I
		var J = (POMDP) domainRunner.getModel("agentJ").orElseGet(() ->
			{

				LOGGER.error("Model not found");
				System.exit(-1);
				return null;
			});
		
		LOGGER.debug(String.format("Got a hold of POMDP agent %s", J));
		
		var env = domainRunner.envs.get("tigerEnv");
		LOGGER.debug(String.format("Got env %s", env));
		
		LOGGER.debug("Making initial state");
		var s = DDnode.getDDForChild("TigerLoc", "TR");
		
		var solver = new SymbolicPerseusSolver<>();
		var Vn = solver.solve(List.of(DDleaf.getDD(0.5f)), J, 100, 10, AlphaVectorPolicy.fromR(J.R()));
		
		LOGGER.info("Solved POMDP for infinite horizon");
		
		LOGGER.debug(String.format("Setting initial state to %s", s));
		env.init(s);

		var obs = env.step(
				List.of(Tuple.of(
						J.i_A, 
						Vn.getBestActionIndex(DDleaf.getDD(0.5f), J.i_S()) + 1)));
		
		var statesList = new ArrayList<DD>();
		for (int i = 0; i < 10; i++) {
			
			var _obs = env.step(List.of(Tuple.of(J.i_A, 2)));
			statesList.add(((PartiallyObservableEnv) env).s);
		}
		
		LOGGER.debug(String.format("After 10 OL transitions, states are %s", statesList));
		var result = statesList.stream().reduce(s, (p, q) -> p.equals(q) ? p : DD.zero);
		
		assertEquals(result, DD.zero);

	}


}