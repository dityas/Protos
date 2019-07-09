/*
 * 
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.Examples.TigerL0Frame;
import thinclab.domainMaker.L0Frame;
import thinclab.domainMaker.SPUDDHelpers.VariablesContext;
import thinclab.domainMaker.ddHelpers.DDMaker;

/*
 * @author adityas
 *
 */
class TestFrame {
	
	/*
	 * Test parsing and solution of frames in an IPOMDP
	 */
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testL0Frame() {
		/*
		 * Testing programmable frame creation for the tiger problem
		 */
		System.out.println("Running testL0Frame()");
		TigerL0Frame tigerl0 = new TigerL0Frame();
		assertNotNull(tigerl0);
	}

}
