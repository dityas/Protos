import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.FQDDleaf;
import thinclab.legacy.Global;
import thinclab.spuddx_parser.SpuddXMainParser;

class TestMisc {

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(TestMisc.class);

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
    void testDDQuantization() throws Exception {
    
		System.gc();

		LOGGER.info("Running Single agent tiger domain");
		String domainFile = this.getClass().getClassLoader()
            .getResource("test_domains/test_tiger_domain.spudd")
            .getFile();

		// Run domain
		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();


        var testDDs = new HashSet<DD>();
        var testDDList = new ArrayList<DD>();

        var val = 0.0f;

        while (val < 1.0f) {
            
            var dd = DDnode.getDD(
                    1, 
                    new DD[] {DDleaf.getDD(val), DDleaf.getDD(1 - val)});

            testDDList.add(dd);
            val += 0.0001f;
        }

        testDDs.addAll(testDDList);

        LOGGER.info("DD set contains %s DDs", testDDs.size());

        var qDDs = testDDList.stream()
            .map(d -> FQDDleaf.quantize(d))
            .collect(Collectors.toList());

        var qDDSet = new HashSet<DD>();
        qDDSet.addAll(qDDs);

        LOGGER.info("Quantized DD set contains %s DDs", qDDSet.size());

        var diff = testDDList.stream()
            .map(d -> DDOP.sub(d, FQDDleaf.unquantize(FQDDleaf.quantize(d))))
            .map(d -> DDOP.maxAll(DDOP.abs(d)))
            .max((d1, d2) -> d1.compareTo(d2)).orElse(0.0f);

        var tolerance = diff - (1.0f / (float) FQDDleaf.BINS);

        LOGGER.info("Max error is: %s", diff);
        assertTrue(tolerance < 1e-5f);

    }
}
