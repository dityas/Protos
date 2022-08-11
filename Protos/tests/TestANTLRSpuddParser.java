import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thinclab.domain_lang.DomainLangInterpreter;
import thinclab.domain_lang.Parser;
import thinclab.legacy.Global;
import thinclab.model_ops.belief_exploration.BreadthFirstExploration;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.spuddx_parser.SpuddXLexer;
import thinclab.spuddx_parser.SpuddXMainParser;

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
		
		var RG = ReachabilityGraph.fromDecMakingModel(agent);
		var ES = new BreadthFirstExploration<IPOMDP, ReachabilityGraph, AlphaVectorPolicy>(100);
		
		//RG.addNode(agent.b_i());
		
		//RG = ES.expand(RG, agent, agent.H, null);
		
		//var _vars = new ArrayList<>(agent.i_S());
		//_vars.add(agent.i_Mj);
		
		//RG.getAllNodes().forEach(d -> LOGGER.debug(DDOP.factors(d, _vars)));

		printMemConsumption();

	}

	@Test
	void testTinyLisp() throws Exception {

		System.gc();
		printMemConsumption();

		LOGGER.info("Testing L1 IPOMDP belief update");
		String domainFile = this.getClass()
				.getClassLoader()
				.getResource("test_domains/test_ipomdpl1_lisp.spudd")
				.getFile();

        // check file reading
        var code = Parser.readFromFile(domainFile);
        assertNotNull(code);

        // check tokenizing
        var reader = Parser.tokenize(code);
        LOGGER.debug(String.format("Read %s", reader));

        // check parsing
        DomainLangInterpreter.run(reader);

		printMemConsumption();

	}
	
}
