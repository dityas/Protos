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

import thinclab.frameworks.IPOMDP;
import thinclab.frameworks.POMDP;
import thinclab.ipomdpsolver.FiniteHorizonLookAheadValueIterationSolver;
import thinclab.ipomdpsolver.IPOMDPParser;

/*
 * @author adityas
 *
 */
class TestFHLookAheadSolver {

	public String l1DomainFile;
	public String tigerDom = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.95.SPUDD.txt";
	
	@BeforeEach
	void setUp() throws Exception {
		this.l1DomainFile = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testFHLASolverUtiliyComputation() {
		System.out.println("Running testFHLASolverUtiliyComputation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		FiniteHorizonLookAheadValueIterationSolver solver = 
				new FiniteHorizonLookAheadValueIterationSolver(tigerL1IPOMDP);
		try {
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.initializeIS();
			
			solver.solvePBVI(1, 1000);
			System.out.println(solver.getBestActionsMap());
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	void testPOMDPSolvePBVI() {
		System.out.println("Running testPOMDPSolvePBVI()");
		
		POMDP p1 = new POMDP(this.tigerDom);
//		POMDP p2 = new POMDP(this.attDom);
		
		assertNull(p1.alphaVectors);
//		assertNull(p2.alphaVectors);
		
		p1.solvePBVI(1, 10);
//		p2.solvePBVI(15, 100);
		
		assertNotNull(p1.alphaVectors);
//		assertNotNull(p2.alphaVectors);
	}

}
