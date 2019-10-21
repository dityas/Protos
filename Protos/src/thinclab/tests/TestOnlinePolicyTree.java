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

import thinclab.belief.FullInteractiveBeliefExpansion;
import thinclab.frameworks.IPOMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.policy.OnlinePolicyTree;
import thinclab.solvers.OnlineIPBVISolver;
import thinclab.solvers.OnlineValueIterationSolver;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestOnlinePolicyTree {

	public String l1DomainFile;
	
	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testOnlinePolicyTree() {
		System.out.println("Running testOnlinePolicyTree()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		
//		OnlineIPBVISolver solver = 
//				new OnlineIPBVISolver(
//						tigerL1IPOMDP, 
//						new FullInteractiveBeliefExpansion(tigerL1IPOMDP), 
//						1, 100);
		OnlineValueIterationSolver solver = new OnlineValueIterationSolver(tigerL1IPOMDP);
		
		OnlinePolicyTree T = new OnlinePolicyTree(solver, 1);
		T.buildTree();
		
		System.out.println(T.nodeIdToFileNameMap);
		System.out.println(T.idToNodeMap);
		
		assertEquals(T.idToNodeMap.size(), 7);
		assertEquals(T.nodeIdToFileNameMap.size(), T.idToNodeMap.size());
	}

}
