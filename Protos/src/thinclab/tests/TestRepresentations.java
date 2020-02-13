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

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.SSGABeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.parsers.IPOMDPParser;
import thinclab.representations.belieftreerepresentations.StaticBeliefTree;
import thinclab.representations.conditionalplans.ConditionalPlanGraph;
import thinclab.representations.conditionalplans.ConditionalPlanTree;
import thinclab.representations.modelrepresentations.MJ;
import thinclab.representations.policyrepresentations.PolicyGraph;
import thinclab.solvers.OfflineSymbolicPerseus;
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
	void testMjMergingEffects() {
		
		LOGGER.info("Testing effects of merging MJs");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 5, 10);
		
		LOGGER.info("Without merging");
		for (MJ mj: ipomdp.multiFrameMJ.MJs.values()) {
			LOGGER.debug("Printing MJ");
			System.out.println(mj.getDotString());
		}
		
		parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		IPOMDP newIpomdp = new IPOMDP(parser, 5, 10, 0.01);
		
		LOGGER.info("With merging");
		for (MJ mj: newIpomdp.multiFrameMJ.MJs.values()) {
			LOGGER.debug("Printing MJ");
			System.out.println(mj.getDotString());
		}
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
		
//		POMDP pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");
		
		POMDP pomdp = 
				new POMDP(
						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/exfil.5S.L0.domain");
		
		StaticBeliefTree T = new StaticBeliefTree(pomdp, 2);
		T.buildTree();
		
		System.out.println(T.getDotString());
//		assertTrue(T.idToNodeMap.size() == 9);
		
		OfflineSymbolicPerseus sp = 
				new OfflineSymbolicPerseus(
						pomdp, 
						new SSGABeliefExpansion(pomdp, 100, 1), 
						5, 100);
		
		sp.solve();
		
		ConditionalPlanTree t = new ConditionalPlanTree(sp, 5);
		t.buildTree();
		
		ConditionalPlanGraph pg = new ConditionalPlanGraph(sp, 5);
		pg.buildTree();
		
		System.out.println(t.getDotString());
//		System.out.println(t.getJSONString());
		
//		System.out.println(pg.getJSONString());
		System.out.println(pg.getDotString());
	
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
