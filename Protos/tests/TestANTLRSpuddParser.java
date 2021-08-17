import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.RandomVariable;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.models.IPOMDP;
import thinclab.spuddx_parser.SpuddXLexer;
import thinclab.spuddx_parser.SpuddXMainParser;
import thinclab.spuddx_parser.SpuddXParserWrapper;

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

	private static final Logger LOGGER = LogManager.getLogger(TestANTLRSpuddParser.class);

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

		assertTrue(domainRunner.getDDs().size() == 4);

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
	void testIPOMDPL1BeliefUpdate() throws Exception {

		System.gc();
		printMemConsumption();

		LOGGER.info("Testing L1 IPOMDP belief update");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_ipomdpl1.spudd")
				.getFile();

		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();
		
		var I = domainRunner.getModel("agentI");
		
		if (I.isEmpty()) {
			LOGGER.error(String.format("Could not find IPOMDP"));
			System.exit(-1);
		}
		
		var agent = (IPOMDP) I.get();
		agent.beliefUpdate(agent.b_i(), 0, List.of(1, 3));

		printMemConsumption();

	}

}
