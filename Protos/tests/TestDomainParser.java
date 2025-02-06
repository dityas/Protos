import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.domain_parser.Interpreter;
import thinclab.domain_parser.InterpreterUtils;
import thinclab.domain_parser.Parser;
import thinclab.domain_parser.AssocList;
import thinclab.legacy.Global;

/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */

class TestDomainParser {

	private static final Logger LOGGER = 
        LogManager.getFormatterLogger(TestDomainParser.class);

	public String pomdpDomain;

	@BeforeEach
	void setUp() throws Exception {

		Global.clearAll();
		this.pomdpDomain = this.getClass()
            .getClassLoader()
            .getResource("test_domains/test_pomdp.dom")
            .getFile();
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
	void testDomainParser() throws Exception {

        LOGGER.debug("Testing POMDP domain");
        var test0Stream = new FileInputStream(this.pomdpDomain);
        Interpreter.evalStream(test0Stream);
	}

}

