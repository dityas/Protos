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
	void testIPOMDPL1GetOpponentModels() {
		/*
		 * Test Opponents solutions by L1 agents
		 */
		System.out.println("Running testIPOMDPL1GetOpponentModels()");
		
		/*
		 * Create and Parse the IPOMDP as usual
		 */
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
		
		try {
			List<OpponentModel> models = tigerL1IPOMDP.getOpponentModels(); 
			assertTrue(models.size() > 2);
			
//			System.out.println(Arrays.deepToString(models));
		} 
		
		catch (SolverException e) {
			System.err.println(e.getMessage());
			fail();
		}
	}
	
	@Test
	void testOpponentModelHashing() {
		/*
		 * Test Opponents solutions by L1 agents
		 */
		System.out.println("Running testOpponentModelHashing()");
		
		/*
		 * Create and Parse the IPOMDP as usual
		 */
		IPOMDPParser parser1 = new IPOMDPParser(this.l1DomainFile);
		IPOMDPParser parser2 = new IPOMDPParser(this.l1DomainFile2);
		parser1.parseDomain();
		parser2.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP1 = new IPOMDP();
		IPOMDP tigerL1IPOMDP2 = new IPOMDP();
		
		try {
			tigerL1IPOMDP1.initializeFromParsers(parser1);
			tigerL1IPOMDP2.initializeFromParsers(parser2);
			
			tigerL1IPOMDP1.solveIPBVI(10, 100);
			tigerL1IPOMDP2.solveIPBVI(10, 100);
			
			List<POMDP> frameList1 = tigerL1IPOMDP1.lowerLevelFrames;
			List<POMDP> frameList2 = tigerL1IPOMDP2.lowerLevelFrames;
			
			HashSet<POMDP> uniqueFrames = new HashSet<POMDP>();
			uniqueFrames.addAll(frameList1);
			uniqueFrames.addAll(frameList2);
			
			assertTrue(uniqueFrames.size() < (frameList1.size() + frameList2.size()));
			
			System.out.println("Set size is " + uniqueFrames.size());
			System.out.println("Frames1 size is " + frameList1.size());
			System.out.println("Frames2 size is " + frameList2.size());
			
//			System.out.println(Arrays.deepToString(models));
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
	}
}
