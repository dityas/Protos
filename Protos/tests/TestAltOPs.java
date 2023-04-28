import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;
import thinclab.spuddx_parser.SpuddXMainParser;
import thinclab.utils.Utils;
import thinclab.models.POMDP;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.SymbolicPerseusSolver;

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
class TestAltOPs {

	private static final Logger LOGGER = LogManager.getLogger(TestAltOPs.class);

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
	void testVarElimCaching() throws Exception {

		LOGGER.info("Running tests for alternate implementations of OPs");
	}

    @Test
    void testDDtoJson() throws Exception {
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

        LOGGER.debug(String.format("reward is %s", I.R()));

        var jsons = I.R().stream()
            .map(r -> r.toJson())
            .collect(Collectors.toList());

        LOGGER.debug(String.format("reward is %s", jsons));

        var dds = jsons.stream()
            .map(j -> DD.fromJson(j))
            .collect(Collectors.toList());

        LOGGER.info(String.format("Converted DDs are %s", dds));
    }

    @Test
    void testPolicySerialization() throws Exception {
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
		
        var solver = new SymbolicPerseusSolver<POMDP>(I);
		var policy = solver.solve(
                List.of(DDleaf.getDD(0.5f)), 
                100, 10);

        var gson = new GsonBuilder().setPrettyPrinting().create();
        var json = gson.toJson(policy.toJson());

        LOGGER.debug(
                String.format(
                    "Policy is %s", policy));

        LOGGER.debug(
                String.format(
                    "Policy to json is %s", json));

        LOGGER.debug(
                String.format(
                    "Recovered policy is %s",
                    AlphaVectorPolicy.fromJson(policy.toJson())));

        Utils.writeJsonToFile(policy.toJson(), "/tmp/policy.json");
        
        var policyFile = new File("/tmp/policy.json");
        assertTrue(policyFile.exists());

        policyFile.delete();

    }
}
