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
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.solvers.OnlineValueIterationSolver;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestOnlineValueIterationSolver {

public String l1DomainFile;
	
	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
//		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/defender_l1.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testUtilityComputationAtLeaves() {
		System.out.println("Running testUtilityComputationAtLeaves()");
		
		IPOMDPParser parser = new IPOMDPParser(l1DomainFile);
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 5, 3);
		
		OnlineValueIterationSolver solver = new OnlineValueIterationSolver(ipomdp);
		
		System.out.println(
				solver.computeUtilityRecursive(
						ipomdp.getInitialBeliefs().get(0), 
						0));
	}

}
