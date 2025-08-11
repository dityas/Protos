import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thinclab.DDOP;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.models.IPOMDP.IPOMDP;
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
class TestExpansionStrats {

	private static final Logger LOGGER = 
        LogManager.getFormatterLogger(TestExpansionStrats.class);

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
    void testMDPExploration() throws Exception {
    
    }

}
