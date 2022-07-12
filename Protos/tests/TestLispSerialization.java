
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;
import thinclab.spuddx_parser.SpuddXMainParser;
import thinclab.utils.LispExpressible;
import thinclab.models.POMDP;
import thinclab.policy.AlphaVectorPolicy;
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
    void testDomainLangLexer() throws Exception {


        System.gc();

		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_tiger_domain.spudd")
				.getFile();

        var data = Files.readString(Path.of(domainFile));
        LOGGER.info("Reader read %s chars", data.length());

        var tokenizer = new StreamTokenizer(new BufferedReader(new FileReader(domainFile)));
        
        while(true) {

            var tok = tokenizer.nextToken();
            
            var word = tok == StreamTokenizer.TT_NUMBER ? tokenizer.nval : tokenizer.sval;

            LOGGER.debug("Tokenizer read %s", word);

            if (tok == StreamTokenizer.TT_EOF)
                break;
        }
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

        var solver = new SymbolicPerseusSolver<POMDP>();
		var policy = solver.solve(
                List.of(DDleaf.getDD(0.5f)), 
                I, 100, 10, AlphaVectorPolicy.fromR(I.R()));

        LOGGER.debug(String.format("Policy is %s", policy));
        System.out.println(LispExpressible.toString(policy.toLisp()));
    }
}
