/*
 *	THINC Lab at UGA | Cyber Deception Group
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

import thinclab.domainMaker.L0Frame;
import thinclab.domainMaker.SPUDDHelpers.VariablesContext;
import thinclab.symbolicperseus.POMDP;

/*
 * @author adityas
 *
 */
class TestIPOMDP {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testPOMDPfromL0Frame() {
		/*
		 * Test POMDP creation from L0Frame
		 */
		System.out.println("Running testPOMDPfromL0Frame()");
		
		String[] varNames = new String[] {"TigerLoc"};
		String[][] varValNames = new String[][] {{"TL", "TR"}};
		
		String[] obsNames = new String[] {"GrowlLoc"};
		String[][] obsValNames = new String[][] {{"GL", "GR"}};
		
		VariablesContext varContext = new VariablesContext(varNames, varValNames, obsNames, obsValNames);
		
		POMDP l0pomdp = new POMDP(new L0Frame(varContext));
		
		assertEquals(l0pomdp.nVars, varNames.length + obsNames.length);
	}

}
