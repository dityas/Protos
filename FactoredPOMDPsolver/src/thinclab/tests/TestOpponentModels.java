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
import thinclab.symbolicperseus.StateVar;

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
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {

//			tigerL1IPOMDP.initializeFromParsers(parser);
			
			/* Get opponent model */
//			OpponentModel oppModel = tigerL1IPOMDP.getOpponentModel();
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.initializeIS();
			
			/* 
			 * manually compute the total nodes in the opponent model for
			 * verification.
			 */
//			int totalNodes = tigerL1IPOMDP.lowerLevelFrames.stream()
//					.map(f -> f.getPolicyTree(15).policyNodes.size())
//					.reduce(0, (totalSize, frameSize) -> totalSize + frameSize);
//			
//			assertEquals(tigerL1IPOMDP.oppModel.nodesList.size(), totalNodes);
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
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 10, 3);
		try {

//			tigerL1IPOMDP.initializeFromParsers(parser);
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.initializeIS();
			/* Get opponent model */
//			OpponentModel oppModel = tigerL1IPOMDP.getOpponentModel();
			
			assertEquals(
					tigerL1IPOMDP.oppModel.nodesList.size(), 
					tigerL1IPOMDP.oppModel.triplesMap.size());
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
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 10, 3);
		try {

//			tigerL1IPOMDP.initializeFromParsers(parser);
			
			/* Get opponent model */
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.oppModel.expandFromRoots(3);
			System.out.println(tigerL1IPOMDP.oppModel.currentNodes);
			assertTrue(tigerL1IPOMDP.oppModel.currentNodes.size() > 1);
			
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
	}
	
	@Test
	void testOpponentModelVariableCreation() {
		System.out.println("Running testOpponentModelVariableCreation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {

//			tigerL1IPOMDP.initializeFromParsers(parser);
			
			/* Get opponent model */
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.initializeIS();
			
			StateVar Mj = 
					tigerL1IPOMDP.oppModel.getOpponentModelStateVar(
							tigerL1IPOMDP.oppModelVarIndex);
			
			System.out.println(Mj);
			
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
	}
	
	@Test
	void testContextClearing() {
		System.out.println("Running testContextClearing()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 10, 2);
		try {
			
			/* Get opponent model */
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.initializeIS();
			
			tigerL1IPOMDP.oppModel.clearCurrentContext();
			
			assertTrue(tigerL1IPOMDP.oppModel.currentNodes.isEmpty());
			assertTrue(tigerL1IPOMDP.oppModel.currentRoots.isEmpty());
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
	}
}
