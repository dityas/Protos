import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.RandomVariable;
import thinclab.legacy.Global;
import thinclab.spuddx_parser.SpuddXLexer;
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

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);

		assertNotNull(parserWrapper);
		printMemConsumption();
	}

	@Test
	void testSimplePOMDPVarsParsing() throws Exception {

		LOGGER.info("Running Parser Wrapper state var parse test");

		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_var_decls.spudd").getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);
		var randomVars = parserWrapper.getVariableDeclarations();

		assertTrue(randomVars.size() == 7);
		printMemConsumption();
	}

	@Test
	void testSimplePOMDPDDParsing() throws Exception {

		LOGGER.info("Running Parser Wrapper state var parse test");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_var_decls.spudd").getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);
		var randomVars = parserWrapper.getVariableDeclarations();

		assertTrue(randomVars.size() == 7);
		
		Global.primeVarsAndInitGlobals(randomVars);
		printMemConsumption();
		
		assertTrue(Global.varNames.size() == 14);
	}
	
	@Test
	void testSimplePOMDPDDDeclsParsing() throws Exception {
		
		LOGGER.info("Running Parser Wrapper DD decls parse test");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_dd_decls.spudd").getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);
		var randomVars = parserWrapper.getVariableDeclarations();

		Global.primeVarsAndInitGlobals(randomVars);
		
		var dds = parserWrapper.getDDs();
		LOGGER.debug(dds);
		printMemConsumption();
		
		assertTrue(dds.size() == 4);
		
	}
	
	@Test
	void testSimplePOMDPDBNDeclsParsing() throws Exception {
		
		LOGGER.info("Running Parser Wrapper DBN decls parse test");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_dbn_def.spudd").getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);
		var randomVars = parserWrapper.getVariableDeclarations();

		Global.primeVarsAndInitGlobals(randomVars);
		
		var dbns = SpuddXParserWrapper.getDBNs(parserWrapper.getModels());
		printMemConsumption();
	}

	@Test
	void testSimplePOMDPParsing() throws Exception {

		System.gc();
		printMemConsumption();
		
		LOGGER.info("Running Parser Wrapper POMDP decls parse test");
		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_complete_domain.spudd").getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);
		var randomVars = parserWrapper.getVariableDeclarations();

		Global.primeVarsAndInitGlobals(randomVars);
		
		var models = parserWrapper.getModels();
		var pomdps = SpuddXParserWrapper.getPOMDPs(models);
		printMemConsumption();
	}

}
