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
import thinclab.belief.SSGABeliefExpansion;
import thinclab.frameworks.IPOMDP;
import thinclab.frameworks.POMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.policy.OnlinePolicyTree;
import thinclab.policy.StaticBeliefTree;
import thinclab.policy.StaticPolicyTree;
import thinclab.solvers.OfflineSymbolicPerseus;
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
		
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 7, 3);
		
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
		System.out.println(T.edgeMap);
		
//		assertEquals(T.idToNodeMap.size(), 3);
		assertEquals(T.nodeIdToFileNameMap.size(), T.idToNodeMap.size());
		
		System.out.println(T.getDotString());
		System.out.println(T.getJSONString());
	}
	
	@Test
	void testStaticBeliefTree() {
		System.out.println("Running testStaticBeliefTree()");
		
		POMDP pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");
		
		StaticBeliefTree T = new StaticBeliefTree(pomdp, 2);
		T.buildTree();
		
		System.out.println(T.getDotString());
		assertTrue(T.idToNodeMap.size() == 9);
		
		OfflineSymbolicPerseus sp = 
				new OfflineSymbolicPerseus(
						pomdp, 
						new SSGABeliefExpansion(pomdp, 100, 1), 
						5, 100);
		
		sp.solve();
		
		StaticBeliefTree t = new StaticBeliefTree(sp, 3);
		t.buildTree();
		
		System.out.println(t.getDotString());
		
//		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
//		parser.parseDomain();
//		
//		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 7, 2);
//
//		StaticBeliefTree sT = new StaticBeliefTree(tigerL1IPOMDP, 2);
//		sT.buildTree();
//		
//		System.out.println(sT.getDotString());
	}
	
	@Test
	void testStaticPolicyTree() {
		System.out.println("Running testStaticPolicyTree()");
		
		POMDP pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");
//		POMDP pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/attacker_l0.txt");
//		POMDP pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/honeypot_exfil_minimal_l0.domain");
		
		OfflineSymbolicPerseus solver = 
				new OfflineSymbolicPerseus(
						pomdp, 
						new SSGABeliefExpansion(pomdp, 20, 1), 
						5, 100);
		
		solver.solve();
		
		StaticPolicyTree T = new StaticPolicyTree(solver, 5);
		T.buildTree();
		
		System.out.println(T.getDotString());
//		assertTrue(T.idToNodeMap.size() == 9);
//		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 7, 3);
		
		OnlineIPBVISolver isolver = 
				new OnlineIPBVISolver(
						tigerL1IPOMDP, 
						new FullInteractiveBeliefExpansion(tigerL1IPOMDP), 
						1, 100);

		isolver.solveCurrentStep();
		
		StaticPolicyTree sT = new StaticPolicyTree(isolver, 3);
		sT.buildTree();
		
		System.out.println(sT.getDotString());
	}

}
