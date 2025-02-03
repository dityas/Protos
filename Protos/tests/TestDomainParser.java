import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.domain_parser.Parser;
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

	public String domainFile;

	@BeforeEach
	void setUp() throws Exception {

		Global.clearAll();
		this.domainFile = this.getClass()
            .getClassLoader()
            .getResource("test_domains/test_var_decls.spudd")
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

        LOGGER.debug("Testing domain parser");
        var test0 = "(start (def v 0.0)\r\n(def s hello world))";
        var test0Stream = new ByteArrayInputStream(test0.getBytes("UTF-8"));
        var parser = new Parser(test0Stream);
        LOGGER.debug(parser.parse());
	}

}

