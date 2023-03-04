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
import thinclab.DDOP;
import thinclab.Simulator;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.models.POMDP;
import thinclab.models.IPOMDP.IPOMDP;
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

	private static final Logger LOGGER = 
        LogManager.getFormatterLogger(TestSimulations.class);

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
	void testMultiAgentSim() throws Exception {

		System.gc();

		LOGGER.info("Testing L1 IPOMDP Sim");
		String domainFile = this.getClass()
            .getClassLoader()
            .getResource("test_domains/test_ipomdpl1.spudd")
            .getFile();

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

		var b_i = DDOP.mult(DDleaf.getDD(0.5f), DDnode.getDistribution(
                    I.i_Mj, List.of(Tuple.of("m0", 0.5f), Tuple.of("m1", 0.5f))));

		LOGGER.debug(String.format("Level 1 recursive belief is %s", b_i));

        // map to belief over equivalence classes
		b_i = I.getECDDFromMjDD(b_i);


        // init simulator
        var states = new ArrayList<>(I.i_S());
        states.remove(states.size() - 1);

        var initState = DDnode.getDDForChild(1, 0);
        var sim = new Simulator(states, I.T(), I.i_A, I.i_Aj);
        sim.setState(initState);

        // test updates
        for (int i = 0; i < 10; i++) {
            sim.updateState(0, 0);
            var nextState = sim.getState();

            assertEquals(nextState, initState);
        }
    }
}
