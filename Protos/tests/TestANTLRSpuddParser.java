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
	}

	@Test
	void testParserWrapperInit() throws Exception {

		LOGGER.info("Running Parser Wrapper init test");

		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_var_decls.spudd").getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);

		assertNotNull(parserWrapper);
	}

	@Test
	void testSimplePOMDPStateVarsParsing() throws Exception {

		LOGGER.info("Running Parser Wrapper state var parse test");

		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_var_decls.spudd").getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);

		var sVars = parserWrapper.getStateVarDecls();

		assertTrue(sVars.size() > 0);
	}

	@Test
	void testSimplePOMDPObsVarsParsing() throws Exception {

		LOGGER.info("Running Parser Wrapper obs var parse test");

		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_var_decls.spudd").getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);

		var oVars = parserWrapper.getObsVarDecls();

		assertTrue(oVars.size() > 0);
	}

	@Test
	void testSimplePOMDPActionVarsParsing() throws Exception {

		LOGGER.info("Running Parser Wrapper action var parse test");

		String domainFile = this.getClass().getClassLoader().getResource("test_domains/test_var_decls.spudd").getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);

		var aVars = parserWrapper.getActionVarDecls();

		assertTrue(aVars.size() > 0);
	}

	@Test
	void testSimplePOMDPGlobalVariablesSetting() throws Exception {

		LOGGER.info("Running test for extracting globals from domain file");
        
		String domainFile = this.getClass().getClassLoader()
								.getResource("test_domains/test_var_decls.spudd").getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);
		
		// Get all random variables
		var sVars = parserWrapper.getStateVarDecls();
		var oVars = parserWrapper.getObsVarDecls();
		var aVars = parserWrapper.getActionVarDecls();
		
		// Aggregate RVs and prepare for global init
		List<RandomVariable> allVars = new ArrayList<>();
		allVars.addAll(sVars);
		allVars.addAll(oVars);
		allVars.addAll(aVars);
		
		LOGGER.debug("All parsed variables are " + allVars);
		
		var allVarsPrimed = RandomVariable.primeVariables(allVars);
		
		LOGGER.debug("Primed variables are " + allVarsPrimed);
		
		Global.populateFromRandomVariables(allVarsPrimed);

	}

	@Test
	void testSimplePOMDPDDDeclarations() throws Exception {

		LOGGER.info("Running test for extracting globals from domain file");
        
		String domainFile = this.getClass().getClassLoader()
								.getResource("test_domains/test_var_decls.spudd").getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);
		
		// Get all random variables
		var sVars = parserWrapper.getStateVarDecls();
		var oVars = parserWrapper.getObsVarDecls();
		var aVars = parserWrapper.getActionVarDecls();
		
		// Aggregate RVs and prepare for global init
		List<RandomVariable> allVars = new ArrayList<>();
		allVars.addAll(sVars);
		allVars.addAll(oVars);
		allVars.addAll(aVars);
		
		LOGGER.debug("All parsed variables are " + allVars);
		
		var allVarsPrimed = RandomVariable.primeVariables(allVars);
		
		LOGGER.debug("Primed variables are " + allVarsPrimed);
		
		Global.populateFromRandomVariables(allVarsPrimed);

	}
	
	@Test
	void testSimplePOMDPAllVarDecls() throws Exception {
		
		LOGGER.info("Running test for extracting globals from domain file");
        
		String domainFile = this.getClass().getClassLoader()
								.getResource("test_domains/test_var_decls.spudd").getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);
		
		// Get all random variables
		var sVars = parserWrapper.getStateVarDecls();
		var oVars = parserWrapper.getObsVarDecls();
		var aVars = parserWrapper.getActionVarDecls();
		
		// Aggregate RVs and prepare for global init
		List<RandomVariable> allVars = new ArrayList<>();
		allVars.addAll(sVars);
		allVars.addAll(oVars);
		allVars.addAll(aVars);
		
		LOGGER.debug("All parsed variables are " + allVars);
		
		var allParsedVars = parserWrapper.getAllVarDecls();
		
		LOGGER.debug("All parsed variables from allDecls call are " 
				+ allParsedVars);
		
		
		assertTrue(allParsedVars.size() == allVars.size());
	}
	
	@Test
	void testDDParsingSimplePOMDP() throws Exception {
		
		LOGGER.info("Running test for extracting DDs from domain file");
        
		String domainFile = this.getClass().getClassLoader()
								.getResource("test_domains/test_dd_decls.spudd").getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);
		
		var allParsedVars = parserWrapper.getAllVarDecls();
		LOGGER.debug("All parsed variables from allDecls call are " 
				+ allParsedVars);
		
		var primedVars = RandomVariable.primeVariables(allParsedVars);
		Global.populateFromRandomVariables(primedVars);
		
		var DDs = parserWrapper.getDefinedDDs();
		assertTrue(DDs.size() == 5);

	}
	
	@Test
	void testDDExprsSimplePOMDP() throws Exception {
		
		LOGGER.info("Running test for DD expressions");
        
		String domainFile = this.getClass().getClassLoader()
								.getResource("test_domains/test_dd_exprs.spudd").getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);
		
		var allParsedVars = parserWrapper.getAllVarDecls();
		
		var primedVars = RandomVariable.primeVariables(allParsedVars);
		Global.populateFromRandomVariables(primedVars);
		
		var DDs = parserWrapper.getDefinedDDs();
		assertEquals(DDs.size(), 6);

	}

	@Test
	void testEnvParsingSimplePOMDP() throws Exception {
		
		LOGGER.info("Running test for extracting environment from domain file");
        
		String domainFile = this.getClass().getClassLoader()
								.getResource("test_domains/test_env_def.spudd").getFile();

		SpuddXParserWrapper parserWrapper = new SpuddXParserWrapper(domainFile);
		
		var allParsedVars = parserWrapper.getAllVarDecls();
		
		var primedVars = RandomVariable.primeVariables(allParsedVars);
		Global.populateFromRandomVariables(primedVars);
		
		var DDs = parserWrapper.getDefinedDDs();
		var env = parserWrapper.getEnv(DDs);
		
	}
}
