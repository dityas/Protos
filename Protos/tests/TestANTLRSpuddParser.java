import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thinclab.domain_lang.Parser;
import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;
import thinclab.model_ops.belief_exploration.BreadthFirstExploration;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.spuddx_parser.SpuddXLexer;
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
class TestANTLRSpuddParser {

	private static final Logger LOGGER = 
        LogManager.getFormatterLogger(TestANTLRSpuddParser.class);

	public String domainFile;

	@BeforeEach
	void setUp() throws Exception {

		Global.clearAll();
		this.domainFile = this.getClass().getClassLoader().getResource("test_domains/test_var_decls.spudd").getFile();
	}

	@AfterEach
	void tearDown() throws Exception {

	}

	void printMemConsumption() throws Exception {

		var total = Runtime.getRuntime().totalMemory() / 1000000.0;
		var free = Runtime.getRuntime().freeMemory() / 1000000.0;

		LOGGER.info(String.format("Free mem: %s", free));
		LOGGER.info(String.format("Used mem: %s", (total - free)));
		Global.logCacheSizes();
	}

	@Test
	void testSimplePOMDPVarDeclsLexer() throws Exception {

		LOGGER.info("Running tests for simple POMDP parsing");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_var_decls.spudd").getFile();

		LOGGER.info(String.format("Trying to parse file %s", domainFile));

		InputStream is = new FileInputStream(domainFile);
		ANTLRInputStream antlrIs = new ANTLRInputStream(is);

		SpuddXLexer lexer = new SpuddXLexer(antlrIs);

		LOGGER.debug(String.format("Initialized lexer %s", lexer));

		var tokens = lexer.getAllTokens();

		LOGGER.debug(String.format("Tokens extracted: %s", tokens));

		assertNotNull(tokens);
		printMemConsumption();
	}

	@Test
	void testParserWrapperInit() throws Exception {

		LOGGER.info("Running Parser Wrapper init test");

		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_var_decls.spudd").getFile();

		var domainRunner = new SpuddXMainParser(domainFile);

		assertNotNull(domainRunner);
		printMemConsumption();
	}

	@Test
	void testSimplePOMDPVarsParsing() throws Exception {

		LOGGER.info("Running Parser Wrapper state var parse test");

		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_var_decls.spudd").getFile();

		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		assertTrue(Global.varNames.size() == (7 * 2));

		printMemConsumption();
	}

	@Test
	void testSimplePOMDPDDDeclsParsing() throws Exception {

		LOGGER.info("Running Parser Wrapper DD decls parse test");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_dd_decls.spudd").getFile();

		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		printMemConsumption();

	}

	@Test
	void testSimplePOMDPDBNDeclsParsing() throws Exception {

		LOGGER.info("Running Parser Wrapper DBN decls parse test");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_dbn_def.spudd").getFile();

		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		printMemConsumption();

	}

	@Test
	void testTigerPOMDPParsing() throws Exception {

		System.gc();
		printMemConsumption();

		LOGGER.info("Running Parser Wrapper Tiger Domain parse test");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_tiger_domain.spudd")
				.getFile();

		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		printMemConsumption();
	}

	@Test
	void testSolverDefInFile() throws Exception {

		System.gc();
		printMemConsumption();

		LOGGER.info("Running Parser Wrapper Tiger Domain parse test");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_solver_spec.spudd")
				.getFile();

		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		printMemConsumption();
	}

	@Test
	void testIPOMDPL1Def() throws Exception {

		System.gc();
		printMemConsumption();

		LOGGER.info("Running Parser Wrapper Tiger Domain parse test");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_ipomdpl1.spudd")
				.getFile();

		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		printMemConsumption();

	}

    @Test
    void testParser() throws Exception {
        
        var test1 = "(define a 1)";
        LOGGER.debug(
                "test 1 parsed as %s", 
                Parser.parse(Parser.tokenize(test1)));
        
        var type1 = Class.forName("thinclab.legacy.DD");
        var type2 = Class.forName("thinclab.legacy.DD");
        
        var method = Class.forName("thinclab.DDOP")
            .getMethod("add", type1, type2);
    }

    @Test
    void testRepl() throws Exception {

        LOGGER.info("Starting repl");
        Function<Integer, Integer> f = n -> n + 1;
        LOGGER.debug("%s", f);

        var methods = Arrays.asList(Class
            .forName("thinclab.legacy.DDnode")
            .getMethods());
        
        var method = methods
            .stream()
            .filter(m -> m.getName().contains("getDD"))
            .map(m -> Arrays.toString(m.getParameterTypes()))
            .collect(Collectors.toList());

        LOGGER.debug("%s", method);
    }
}
