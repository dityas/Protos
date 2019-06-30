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
	void testL0Frames() {
		/*
		 * Testing programmable domain creation for the tiger problem
		 */
		System.out.println("Running testL0Frames()");
//		String TigerL1 = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tigerl1.txt";
		
		String[] varNames = new String[] {"TigerLoc"};
		String[][] varValNames = new String[][] {{"TL", "TR"}};
		
		String[] obsNames = new String[] {"GrowlLoc"};
		String[][] obsValNames = new String[][] {{"GL", "GR"}};
		
		VariablesContext varContext = new VariablesContext(varNames, varValNames, obsNames, obsValNames);
//		assertEquals(tigerIPOMDP.getFramesCount(), 2);
		
		L0Frame l0frame = new L0Frame(varContext);
		
		assertEquals(l0frame.getVarNames().length, varContext.getVarNames().length);
		assertEquals(l0frame.getObsNames().length, varContext.getObsNames().length);
		
		assertArrayEquals(l0frame.getVarNames(), varContext.getVarNames());
		assertArrayEquals(l0frame.getObsNames(), varContext.getObsNames());
	}

}
