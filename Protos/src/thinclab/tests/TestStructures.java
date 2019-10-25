/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.FullInteractiveBeliefExpansion;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.legacy.DD;
import thinclab.legacy.OP;
import thinclab.policy.DynamicBeliefTree;
import thinclab.policy.MJ;
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
class TestStructures {

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
	
	}
	
	@Test
	void testStaticPolicyTree() {
		System.out.println("Running testStaticPolicyTree()");
		
		POMDP pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");
	
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
	
	@Test
	void testDynamicBeliefTree() {
		System.out.println("Running testDynamicBeliefTree()");
		
		POMDP pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");
	
		OfflineSymbolicPerseus solver = 
				new OfflineSymbolicPerseus(
						pomdp, 
						new SSGABeliefExpansion(pomdp, 20, 1), 
						5, 100);
		
		solver.solve();
		
		DynamicBeliefTree T = new DynamicBeliefTree(solver, 3);
		
		HashSet<Integer> prevLeaves = new HashSet<Integer>(T.leafNodes);
		System.out.println(prevLeaves);
		
		T.buildTree();
		
		HashSet<Integer> nextLeaves = new HashSet<Integer>(T.leafNodes);
		System.out.println(nextLeaves);
		System.out.println(prevLeaves);
		System.out.println(nextLeaves.containsAll(prevLeaves));
		/* check if leaf nodes are updated properly */
		assertTrue(!nextLeaves.containsAll(prevLeaves));
		
		T.buildTree();
		
		HashSet<Integer> nextNextLeaves = new HashSet<Integer>(T.leafNodes);
		System.out.println(nextNextLeaves);
		
		assertTrue(!nextNextLeaves.containsAll(nextLeaves));
		
		List<Integer> nonZeroTest = new ArrayList<Integer>(nextNextLeaves).subList(0, 5);
		System.out.println(T.idToNodeMap);
		T.pruneZeroProbabilityLeaves(nonZeroTest);
		System.out.println(T.idToNodeMap);
		
		assertTrue(nonZeroTest.size() == T.idToNodeMap.size());
		assertTrue(T.edgeMap.size() == 0);
		
		
	}
	
	
	@Test
	void testMJ() {
		System.out.println("Running testMJ()");
		
		POMDP pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");
	
		OfflineSymbolicPerseus solver = 
				new OfflineSymbolicPerseus(
						pomdp, 
						new SSGABeliefExpansion(pomdp, 20, 1), 
						5, 100);
		
		solver.solve();
		
		MJ Mj = new MJ(solver, 3);
		
		System.out.println(Mj.getOpponentModelStateVar(1));
		
		/* only true for tiger problem */
		assertTrue(Mj.getOpponentModelStateVar(1).arity == 16);
		
		/* next tests on IPOMDP */
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 7, 3);
		tigerL1IPOMDP.setGlobals();
		
		/* manually assemble solver */
		OfflineSymbolicPerseus Solver = 
				new OfflineSymbolicPerseus(
						tigerL1IPOMDP.lowerLevelFrames.get(0), 
						new SSGABeliefExpansion(pomdp, 20, 1), 
						5, 100);
		
		tigerL1IPOMDP.setGlobals();
		Solver.solve();
		
		MJ mj = new MJ(Solver, 3);
		System.out.println(mj.getOpponentModelStateVar(tigerL1IPOMDP.oppModelVarIndex));
		assertTrue(mj.getOpponentModelStateVar(1).arity == 16);
		
		/* manually inject Mj in the IPOMDP */
		System.out.println(tigerL1IPOMDP.S);
		tigerL1IPOMDP.S.remove(tigerL1IPOMDP.oppModelVarIndex);
		tigerL1IPOMDP.S.add(
				tigerL1IPOMDP.oppModelVarIndex, 
				mj.getOpponentModelStateVar(tigerL1IPOMDP.oppModelVarIndex));
		tigerL1IPOMDP.commitVariables();
		
		/* test PAjMj */
		DD PAjMj = mj.getAjGivenMj(tigerL1IPOMDP.ddMaker, tigerL1IPOMDP.getActions());
//		System.out.println(PAjMj);
		assertTrue(
				OP.maxAll(
						OP.abs(
							OP.sub(
								DD.one, 
								OP.addMultVarElim(
									PAjMj, 3)))) < 1e-8);
		
//		System.out.println(mj.idToNodeMap);
//		System.out.println(mj.edgeMap);
//		System.out.println(mj.getJSONString());
		/* test Mj transition */
		System.out.println(Arrays.deepToString(mj.getMjTransitionTriples()));
		
		System.out.println(mj.getMjInitBelief(tigerL1IPOMDP.ddMaker));
	}

}
