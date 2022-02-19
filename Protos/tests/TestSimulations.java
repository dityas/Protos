import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import thinclab.Agent;
import thinclab.DDOP;
import thinclab.env.PartiallyObservableEnv;
import thinclab.env.StochasticSimulation;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.models.IPOMDP;
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
	
	@Test
	void testStochasticSimulation() throws Exception {
		
		LOGGER.info("Testing stochastic simulation for partially observable env");
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

		LOGGER.debug("Initializing StochasticSimulation");
	
		var agentJ = Agent.of(J, DDleaf.getDD(0.5f), solver, 100, 10);
		LOGGER.debug(String.format("Agent %s", agentJ.toDot()));
		var sim = new StochasticSimulation<>();
		var e = sim.run(env, s, List.of(agentJ), 5);
		LOGGER.debug(e.toDot());
		
		LOGGER.debug(Class.forName("thinclab.Agent"));
	}

	@Test
	void testMAStochasticSimulation() throws Exception {
		
		LOGGER.info("Testing stochastic simulation for multiagent partially observable env");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_ipomdpl1_env.spudd").getFile();

		// Run domain
		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		// Get agent J
		var J = (POMDP) domainRunner.getModel("agentJ").orElseGet(() ->
			{

				LOGGER.error("Model not found");
				System.exit(-1);
				return null;
			});
		
		LOGGER.debug(String.format("Got a hold of POMDP agent %s", J));
		
		// Get agent I
		var I = (IPOMDP) domainRunner.getModel("agentI").orElseGet(() ->
			{

				LOGGER.error("Model not found");
				System.exit(-1);
				return null;
			});
		
		LOGGER.debug(String.format("Got a hold of IPOMDP agent %s", I));
		
		var env = domainRunner.envs.get("maTigerEnv");
		LOGGER.debug(String.format("Got env %s", env));
		
		LOGGER.debug("Making initial state");
		var s = DDnode.getDDForChild("TigerLoc", "TR");
		
		var Isolver = new SymbolicPerseusSolver<>();
		var Jsolver = new SymbolicPerseusSolver<>();

		//LOGGER.debug("Initializing StochasticSimulation");
		var b = DDOP.mult(
					DDleaf.getDD(0.5f), 
					DDnode.getDistribution(
							I.i_Mj, 
							List.of(Tuple.of("m0", 1.0f))));
		
		var agentI = Agent.of(I, b, Isolver, 100, 10);
		LOGGER.debug(String.format("Agent %s", agentI.toDot()));
		
		var agentJ = Agent.of(J, DDleaf.getDD(0.5f), Jsolver, 100, 10);
		LOGGER.debug(String.format("Agent %s", agentJ.toDot()));
		
		var sim = new StochasticSimulation<>();
		var e = sim.run(env, s, List.of(agentI, agentJ), 3);
		System.out.println(e);
		
		//LOGGER.debug(Class.forName("thinclab.Agent"));
	}
	
	@Test
	void testPrinting() throws Exception {
		
		LOGGER.info("Testing json printing");
		
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_ipomdpl1_env.spudd").getFile();

		// Run domain
		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		// Get agent J
		var J = (POMDP) domainRunner.getModel("agentJ").orElseGet(() ->
			{

				LOGGER.error("Model not found");
				System.exit(-1);
				return null;
			});
		
		LOGGER.debug(String.format("Got a hold of POMDP agent %s", J));
		var Jsolver = new SymbolicPerseusSolver<>();
		var agentJ = Agent.of(J, DDleaf.getDD(0.5f), Jsolver, 100, 10);
		
		LOGGER.debug(String.format("Agent J is %s", agentJ));
	
		var gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
		var ddJson = gson.toJson(agentJ);
		LOGGER.debug(String.format("JSON looks like %s", ddJson));
	}
	
}
