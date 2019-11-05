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
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.FullInteractiveBeliefExpansion;
import thinclab.belief.InteractiveBelief;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.ddinterface.DDTree;
import thinclab.ddinterface.DDTreeLeaf;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ParserException;
import thinclab.exceptions.SolverException;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.OP;
import thinclab.parsers.IPOMDPParser;
import thinclab.representations.ConditionalPlanGraph;
import thinclab.representations.ConditionalPlanTree;
import thinclab.representations.DynamicBeliefTree;
import thinclab.representations.MJ;
import thinclab.representations.PolicyGraph;
import thinclab.representations.StaticBeliefTree;
import thinclab.representations.WalkablePolicyTree;
import thinclab.solvers.OfflinePBVISolver;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.solvers.OnlineIPBVISolver;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.solvers.OnlineValueIterationSolver;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestRepresentations {

	public String l1DomainFile;
	public String multiJDomainFile;
	
	private static Logger LOGGER;
	
	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		LOGGER = Logger.getLogger(TestRepresentations.class);
		
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
		this.multiJDomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
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
		
		OnlineInteractiveSymbolicPerseus solver = 
				new OnlineInteractiveSymbolicPerseus(
						tigerL1IPOMDP, 
						new FullInteractiveBeliefExpansion(tigerL1IPOMDP), 
						1, 
						100);

		WalkablePolicyTree T = new WalkablePolicyTree(solver, 3);
		T.buildTree();
		
		System.out.println(T.nodeIdToFileNameMap);
		System.out.println(T.idToNodeMap);
		System.out.println(T.edgeMap);
		
		assertEquals(T.nodeIdToFileNameMap.size(), T.idToNodeMap.size());
		
		System.out.println(T.getDotString());
	}
	
	@Test
	void testStaticPolicyGraph() {
		System.out.println("Running testStaticPolicyGraph()");
		
//		POMDP pomdp = 
//				new POMDP("/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");
		
		POMDP pomdp = 
				new POMDP("/home/adityas/UGA/THINCLab/DomainFiles/honeypot_exfil_minimal_l0.domain");
		
		OfflineSymbolicPerseus solver = 
				OfflineSymbolicPerseus.createSolverWithSSGAExpansion(pomdp, 100, 1, 5, 100);
		
		solver.solve();
		
		System.out.println(Arrays.toString(solver.getAlphaVectors()));
		System.out.println(Arrays.toString(solver.getPolicy()));
		
		PolicyGraph pg = new PolicyGraph(solver);
		pg.makeGraph();
		
		System.out.println(pg.getDotString());
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
		
		ConditionalPlanTree T = new ConditionalPlanTree(solver, 5);
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
//		System.out.println(isolver.expansionStrategy.getBeliefPoints().stream().map(b -> isolver.f.getBeliefString(b)).collect(Collectors.toList()));
		ConditionalPlanTree sT = new ConditionalPlanTree(isolver, 3);
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
	void testMJ() throws ZeroProbabilityObsException, VariableNotFoundException {
		LOGGER.info("Running testMJ()");
		
		POMDP pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");
	
		OfflineSymbolicPerseus solver = 
				new OfflineSymbolicPerseus(
						pomdp, 
						new SSGABeliefExpansion(pomdp, 20, 1), 
						5, 100);
		
		solver.solve();
		
		MJ Mj = new MJ(solver, 3);
		
		LOGGER.debug(Mj.getOpponentModelStateVar(1));
		
		/* only true for tiger problem */
		assertTrue(Mj.getOpponentModelStateVar(1).arity == 9);
		
		/* test PAjMj */
//		DD PAjMj = mj.getAjGivenMj(tigerL1IPOMDP.ddMaker, tigerL1IPOMDP.getActions());
////		System.out.println(PAjMj);
//		assertTrue(
//				OP.maxAll(
//						OP.abs(
//							OP.sub(
//								DD.one, 
//								OP.addMultVarElim(
//									PAjMj, 3)))) < 1e-8);
//		
////		System.out.println(mj.idToNodeMap);
////		System.out.println(mj.edgeMap);
////		System.out.println(mj.getJSONString());
//		/* test Mj transition */
//		System.out.println(Arrays.deepToString(mj.getMjTransitionTriples()));
//		
//		System.out.println(mj.getMjInitBelief(tigerL1IPOMDP.ddMaker));
//		
//		DD nextBel = 
//				InteractiveBelief.staticL1BeliefUpdate(
//						tigerL1IPOMDP, 
//						mj.getMjInitBelief(tigerL1IPOMDP.ddMaker).toDD(), 
//						"listen", tigerL1IPOMDP.getAllPossibleObservations().get(2).toArray(new String[2]));
//		
//		System.out.println(tigerL1IPOMDP.getBeliefString(nextBel));
//		
//		Mj.step(InteractiveBelief.toStateMap(tigerL1IPOMDP, nextBel).get("M_j"), 3);
//		
//		nextBel = 
//				InteractiveBelief.staticL1BeliefUpdate(
//						tigerL1IPOMDP, 
//						nextBel, 
//						"listen", tigerL1IPOMDP.getAllPossibleObservations().get(5).toArray(new String[2]));
//		
//		System.out.println(tigerL1IPOMDP.getBeliefString(nextBel));
//		
//		Mj.step(InteractiveBelief.toStateMap(tigerL1IPOMDP, nextBel).get("M_j"), 3);
	}
	
	@Test
	void testMultipleMJInit() throws 
		ZeroProbabilityObsException, 
		VariableNotFoundException, 
		ParserException, SolverException {
		
		/*
		 * Single Frame
		 */
		LOGGER.info("Running testMultipleMJInit() on single frame");
		
		/* parse multiple frames */
		String fileName = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
		IPOMDP ipomdp = new IPOMDP();
		
		IPOMDPParser parser = new IPOMDPParser(fileName);
		parser.parseDomain();
		
		LOGGER.info("Testing child frame parsing");
		assertEquals(parser.childFrames.size(), 1);
		
		/* check IPOMDP initialization */
		ipomdp.initializeFromParsers(parser);
		
		LOGGER.info("Check multi frame Aj init");
		assertEquals(ipomdp.Aj.size(), 1);
		LOGGER.debug(ipomdp.Aj);
		LOGGER.debug(ipomdp.S);
		
		/* set look ahead manually */
		ipomdp.setMjLookAhead(3);
		
		LOGGER.info("Checking Mj solving");
		ipomdp.solveMj();
		
		LOGGER.info("Checking Mj var arity");
		assertEquals(ipomdp.multiFrameMJ.getOpponentModelStateVar(ipomdp.oppModelVarIndex).arity, 21);
	}
	
	@Test
	void testMultipleMJInit2Frames() throws Exception {
		/*
		 * 2 Frames
		 */
		LOGGER.info("Running testMultipleMJInit() on 2 frames");
		
		/* parse multiple frames */
		String fileName2 = "/home/adityas/git/repository/Protos/domains/tiger.L1multiple.txt";
		IPOMDP ipomdp2 = new IPOMDP();
		
		IPOMDPParser parser2 = new IPOMDPParser(fileName2);
		parser2.parseDomain();
		
		LOGGER.info("Testing child frame parsing");
		assertTrue(parser2.childFrames.size() > 1);
		
		/* check IPOMDP initialization */
		ipomdp2.initializeFromParsers(parser2);
		
		LOGGER.info("Check multi frame Aj init");
		assertEquals(ipomdp2.Aj.size(), 2);
		LOGGER.debug(ipomdp2.Aj);
		LOGGER.debug(ipomdp2.S);
		
		/* set look ahead manually */
		ipomdp2.setMjLookAhead(3);
		
		LOGGER.info("Checking Mj solving");
		ipomdp2.solveMj();
		
		LOGGER.info("Checking Mj var arity");
		LOGGER.debug(Arrays.toString(ipomdp2.multiFrameMJ.getOpponentModelStateVar(2).valNames));
		assertEquals(ipomdp2.multiFrameMJ.getOpponentModelStateVar(2).arity, 42);
		
		
	}

}
