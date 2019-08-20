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

import thinclab.domainMaker.L0Frame;
import thinclab.domainMaker.SPUDDHelpers.VariablesContext;
import thinclab.exceptions.ParserException;
import thinclab.exceptions.SolverException;
import thinclab.ipomdpsolver.IPOMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.ipomdpsolver.InteractiveStateVar;
import thinclab.ipomdpsolver.OpponentModel;
import thinclab.symbolicperseus.POMDP;

/*
 * @author adityas
 *
 */
class TestIPOMDP {

	public String l1DomainFile;
	
	@BeforeEach
	void setUp() throws Exception {
		this.l1DomainFile = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testIPOMDPL1FrameInit() {
		/*
		 * Test POMDP creation from L0Frame
		 */
		System.out.println("Running testIPOMDPL1FrameInit()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {
			tigerL1IPOMDP.initializeFromParsers(parser);
		} 
		
		catch (ParserException e) {
			System.err.println(e.getMessage());
			fail();
		}
		
		assertEquals(tigerL1IPOMDP.lowerLevelFrames.size(), parser.childFrames.size());
	}
	
	@Test
	void testIPOMDPL1FrameSolve() {
		/*
		 * Test IPOMDP solve function for L1
		 */
		System.out.println("Running testIPOMDPL1FrameSolve()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {
			tigerL1IPOMDP.initializeFromParsers(parser);
		} 
		
		catch (ParserException e) {
			System.err.println(e.getMessage());
			fail();
		}
		
		tigerL1IPOMDP.solveIPBVI(10, 100);
	}
	
	@Test
	void testIPOMDPISCreation() {
		System.out.println("Running testIPOMDPISCreation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {
			tigerL1IPOMDP.initializeFromParsers(parser);
			
			/* set opponent model var */
			tigerL1IPOMDP.oppModel = tigerL1IPOMDP.getOpponentModel();
			tigerL1IPOMDP.setUpMj();
			
			assertEquals(
					tigerL1IPOMDP.stateVarStaging.size(), 
					tigerL1IPOMDP.lowerLevelFrames.get(0).stateVarStaging.size() + 1);
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
		
		System.out.println(tigerL1IPOMDP.stateVarStaging);
	}
	
	@Test
	void testIPOMDPObsVarCreation() {
		System.out.println("Running testIPOMDPObsVarCreation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {
			tigerL1IPOMDP.initializeFromParsers(parser);
			
			/* set opponent model var */
			tigerL1IPOMDP.oppModel = tigerL1IPOMDP.getOpponentModel();
			tigerL1IPOMDP.setUpMj();
			
			int prevNObs = tigerL1IPOMDP.obsVarStaging.size();
			
			tigerL1IPOMDP.setUpOj();
			
			assertEquals(tigerL1IPOMDP.obsVarStaging.size(),
					prevNObs + 
						(tigerL1IPOMDP.lowerLevelFrames.size() * 
						tigerL1IPOMDP.lowerLevelFrames.get(0).nObsVars));
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
//			e.printStackTrace();
			fail();
		}
		
		System.out.println();
	}
	
	@Test
	void testIPOMDPMjDDCreation() {
		System.out.println("Running testIPOMDPMjDDCreation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {
			tigerL1IPOMDP.initializeFromParsers(parser);
			
			/* set opponent model var */
			tigerL1IPOMDP.oppModel = tigerL1IPOMDP.getOpponentModel();
			tigerL1IPOMDP.setUpMj();
			tigerL1IPOMDP.setUpOj();
			
			System.out.println(tigerL1IPOMDP.stateVarStaging);
			System.out.println(tigerL1IPOMDP.obsVarStaging);
//			tigerL1IPOMDP.makeOpponentModelTransitionDD();
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
//			e.printStackTrace();
			fail();
		}
		
		System.out.println();
	}

}
