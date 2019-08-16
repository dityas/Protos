/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.exceptions.ParserException;
import thinclab.exceptions.SolverException;
import thinclab.ipomdpsolver.IPOMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.ipomdpsolver.OpponentModel;
import thinclab.symbolicperseus.Global;
import thinclab.symbolicperseus.POMDP;

/*
 * @author adityas
 *
 */
class TestOpponentModels {

	public String l1DomainFile;
	public String l1DomainFile2;
	
	@BeforeEach
	void setUp() throws Exception {
		this.l1DomainFile = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
		this.l1DomainFile2 = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testIPOMDPL1GetOpponentModel() {
		/*
		 * Test Opponents solutions by L1 agents
		 */
		System.out.println("Running testIPOMDPL1GetOpponentModel()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {

			tigerL1IPOMDP.initializeFromParsers(parser);
			
			/* Get opponent model */
			OpponentModel oppModel = tigerL1IPOMDP.getOpponentModel();
			
			/* 
			 * manually compute the total nodes in the opponent model for
			 * verification.
			 */
			int totalNodes = tigerL1IPOMDP.lowerLevelFrames.stream()
					.map(f -> f.getPolicyTree(5).policyNodes.size())
					.reduce(0, (totalSize, frameSize) -> totalSize + frameSize);
			
			assertEquals(oppModel.nodesList.size(), totalNodes);
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
		
		System.out.println(Arrays.toString(Global.varNames));
		System.out.println(Arrays.deepToString(Global.valNames));
		System.out.println(Arrays.toString(Global.varDomSize));
	}
	
	@Test
	void testOpponentModelHashing() {
		
	}
}
