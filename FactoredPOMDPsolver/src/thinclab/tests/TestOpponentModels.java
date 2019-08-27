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
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.Global;
import thinclab.symbolicperseus.OP;
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
	void testOpponentModelInit() {
		System.out.println("Running testOpponentModelInit()");
		
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
			
			assertEquals(oppModel.nodesList.size(), oppModel.triplesMap.size());
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
	}
	
	@Test
	void testOpponentModelTraversal() {
		System.out.println("Running testOpponentModelTraversal()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {

			tigerL1IPOMDP.initializeFromParsers(parser);
			
			/* Get opponent model */
			tigerL1IPOMDP.oppModel = tigerL1IPOMDP.getOpponentModel();
			tigerL1IPOMDP.setUpIS();
			tigerL1IPOMDP.setUpOmegaI();
			tigerL1IPOMDP.commitVariables();
			
			long then = System.nanoTime();
			tigerL1IPOMDP.makeOpponentModelTransitionDD();
			long now = System.nanoTime();
			
			System.out.println("Exec time: " + (now - then)/10000000 + " millisec.");
			System.out.println(tigerL1IPOMDP.MjTFn);
//			assertTrue(
//					OP.maxAll(
//							OP.abs(
//								OP.sub(
//									DD.one, 
//									OP.addMultVarElim(
//										tigerL1IPOMDP.MjTFn,
//										IPOMDP.getVarIndex("M_j'"))))) < 1e-8);
			
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
	}
}
