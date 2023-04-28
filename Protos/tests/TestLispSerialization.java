
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.domain_lang.Parser;
import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;
import thinclab.spuddx_parser.SpuddXMainParser;
import thinclab.utils.LispExpressible;
import thinclab.models.POMDP;
import thinclab.solver.SymbolicPerseusSolver;

class TestLispSerialization {

	private static final Logger LOGGER = 
        LogManager.getFormatterLogger(TestLispSerialization.class);

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
    void testDDLangParser() throws Exception {
        
        String test1 = "12";
        String test2 = "12.0000";
        String test3 = "hello";
        String test4 = "(var TigerLoc tigerLeft tigerRight)";

        var res1 = Parser.parse(Parser.tokenize(test1));
        LOGGER.info("%s parsed to %s", test1, res1);

        var res2 = Parser.parse(Parser.tokenize(test2));
        LOGGER.info("%s parsed to %s", test2, res2);

        var res3 = Parser.parse(Parser.tokenize(test3));
        LOGGER.info("%s parsed to %s", test3, res3);
        
        var res4 = Parser.parse(Parser.tokenize(test4));
        LOGGER.info("%s parsed to %s", test4, res4);

        assertTrue(res1 instanceof Float && res2 instanceof Float);
        assertTrue(res3 instanceof String);
        assertTrue(res4 instanceof List);
    }

    @Test
    void testVarsToDDLang() throws Exception {

		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_tiger_domain.spudd")
				.getFile();

		// Run domain
		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

        LOGGER.info(Global.getVarsAsDDLangDefs());

    }
    
    @Test
    void testPolicyToLisp() throws Exception {

        LOGGER.info("Testing Policy to Lisp");

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

        LOGGER.debug(String.format("Policy is %s", policy));
        System.out.println(LispExpressible.toString(policy.toLisp()));
    }
    
    @Test
    void testPOMDPToLisp() throws Exception {

        LOGGER.info("Testing Policy to Lisp");

        System.gc();

		LOGGER.info("Running Single agent tiger domain belief exploration test");
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

        LOGGER.debug(String.format("POMDP is %s", I.toLispObjects()));
        System.out.println(LispExpressible.toString(I.toLispObjects()));
    }
}
