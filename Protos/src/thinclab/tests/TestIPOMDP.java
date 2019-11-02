/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.Belief;
import thinclab.belief.InteractiveBelief;
import thinclab.ddinterface.DDTree;
import thinclab.ddinterface.DDTreeLeaf;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ParserException;
import thinclab.exceptions.SolverException;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.parsers.IPOMDPParser;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestIPOMDP {

	public String l1DomainFile;
	
	private static Logger LOGGER;
	
	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		LOGGER = Logger.getLogger(TestIPOMDP.class);
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testIPOMDPL1Init() throws Exception {
		/*
		 * Test POMDP creation from L0Frame
		 */
		
		/* single frame init */
		LOGGER.info("Running testIPOMDPL1Init()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP();
		
		LOGGER.info("Parsing the domain");
		ipomdp.initializeFromParsers(parser);
		ipomdp.setMjDepth(3);
		ipomdp.setMjLookAhead(3);
		
		LOGGER.info("Solve MJs");
		ipomdp.solveMj();
		ipomdp.callUpdateIS();
		
		LOGGER.info("Checking Oi creation");
		ipomdp.currentOi = ipomdp.makeOi();
		
		assertEquals(ipomdp.currentOi.size(), ipomdp.getActions().size());
		
		for (String ai : ipomdp.getActions()) {
			LOGGER.debug(Arrays.toString(ipomdp.currentOi.get(ai)));
			assertEquals(ipomdp.currentOi.get(ai).length, ipomdp.Omega.size() - ipomdp.OmegaJNames.size());
		}
		
		LOGGER.info("Checking Ti creation");
		ipomdp.currentTi = ipomdp.makeTi();
		
		assertEquals(ipomdp.currentTi.size(), ipomdp.getActions().size());
		
		for (String ai : ipomdp.getActions()) {
			LOGGER.debug("For Ai=" + ai + Arrays.toString(ipomdp.currentTi.get(ai)));
			assertEquals(ipomdp.currentTi.get(ai).length, ipomdp.S.size() - 2);
		}
		
		LOGGER.info("Checking Oj creation");
		ipomdp.currentOj = ipomdp.makeOj();
		
		assertEquals(ipomdp.currentOj.length, ipomdp.OmegaJNames.size());
		
//		for (String ai : ipomdp.getActions()) {
//			LOGGER.debug("For Ai=" + ai + Arrays.toString(ipomdp.currentTi.get(ai)));
//			assertEquals(ipomdp.currentTi.get(ai).length, ipomdp.S.size() - 2);
//		}
		
	}
	
	@Test
	void testIPOMDPL1Init2Frames() throws Exception {
		
		/* 2 frames init */
		LOGGER.info("Running testIPOMDPL1Init()");
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple.txt");
		parser.parseDomain();
		
		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP();
		
		LOGGER.info("Parsing the domain");
		ipomdp.initializeFromParsers(parser);
		ipomdp.setMjDepth(3);
		ipomdp.setMjLookAhead(3);
		
		LOGGER.info("Solve MJs");
		ipomdp.solveMj();
		ipomdp.callUpdateIS();
		
		LOGGER.info("Checking Oi creation");
		ipomdp.currentOi = ipomdp.makeOi();
		
		assertEquals(ipomdp.currentOi.size(), ipomdp.getActions().size());
		
		for (String ai : ipomdp.getActions()) {
			LOGGER.debug(Arrays.toString(ipomdp.currentOi.get(ai)));
			assertEquals(ipomdp.currentOi.get(ai).length, ipomdp.Omega.size() - ipomdp.OmegaJNames.size());
		}
		
		LOGGER.info("Checking Ti creation");
		ipomdp.currentTi = ipomdp.makeTi();
		
		assertEquals(ipomdp.currentTi.size(), ipomdp.getActions().size());
		
		for (String ai : ipomdp.getActions()) {
			LOGGER.debug("For Ai=" + ai + Arrays.toString(ipomdp.currentTi.get(ai)));
			assertEquals(ipomdp.currentTi.get(ai).length, ipomdp.S.size() - 2);
		}
		
		LOGGER.info("Checking Oj creation");
		ipomdp.currentOj = ipomdp.makeOj();
		LOGGER.debug(Arrays.toString(ipomdp.currentOj));
		assertEquals(ipomdp.currentOj.length, ipomdp.OmegaJNames.size());
	}
	
	@Test
	void testIPOMDPOiDDCreation() {
		/*
		 * Test IPOMDP solve function for L1
		 */
		System.out.println("Running testIPOMDPOjDDCreation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {
//			tigerL1IPOMDP.solveOpponentModels();
//			
//			/* 
//			 * Stage and commit additional state and variables to populate global 
//			 * arrays
//			 */
//			tigerL1IPOMDP.S.set(
//					tigerL1IPOMDP.oppModelVarIndex, 
//					tigerL1IPOMDP.oppModel.getOpponentModelStateVar(
//							tigerL1IPOMDP.oppModelVarIndex));
//			
//			Global.clearHashtables();
//			tigerL1IPOMDP.commitVariables();
//			
//			tigerL1IPOMDP.currentOi = tigerL1IPOMDP.makeOi();
			
//			System.out.println(Arrays.toString(tigerL1IPOMDP.makeOi().get("listen")));
//			System.out.println(Arrays.toString(tigerL1IPOMDP.makeOi().get("open-right")));
//			System.out.println(Arrays.toString(tigerL1IPOMDP.makeOi().get("open-left")));
			for (String Ai : tigerL1IPOMDP.currentOi.keySet()) {
				for (int s = 0; s < tigerL1IPOMDP.Omega.size() - 1; s++) {
					System.out.println("For Ai " + Ai + " and o " + tigerL1IPOMDP.Omega.get(s));
					assertTrue(
							OP.maxAll(
									OP.abs(
										OP.sub(
											DD.one, 
											OP.addMultVarElim(
												tigerL1IPOMDP.currentOi.get(Ai)[s],
												IPOMDP.getVarIndex(
														tigerL1IPOMDP.Omega.get(s).name + "'"))))) < 1e-8);
				}
			}
		}
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	void testIPOMDPTiDDCreation() {
		/*
		 * Test IPOMDP solve function for L1
		 */
		System.out.println("Running testIPOMDPTiDDCreation()");
		
		long then = System.nanoTime();
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 10, 3);
		try {
			
			for (String Ai : tigerL1IPOMDP.currentTi.keySet()) {
				for (int s = 0; s < tigerL1IPOMDP.S.size() - 2; s++) {
					System.out.println("For Ai " + Ai + " and s " + tigerL1IPOMDP.S.get(s));
					assertTrue(
							OP.maxAll(
									OP.abs(
										OP.sub(
											DD.one, 
											OP.addMultVarElim(
												tigerL1IPOMDP.currentTi.get(Ai)[s],
												IPOMDP.getVarIndex(
														tigerL1IPOMDP.S.get(s).name + "'"))))) < 1e-8);
				}
			}
		}
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	void testIPOMDPISCreation() {
		System.out.println("Running testIPOMDPISCreation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 3, 15);
		try {
//			tigerL1IPOMDP.initializeFromParsers(parser);
//			tigerL1IPOMDP.setMjDepth(10);
//			tigerL1IPOMDP.setMjLookAhead(2);
//			tigerL1IPOMDP.solveOpponentModels();
//			tigerL1IPOMDP.commitVariables();
//			tigerL1IPOMDP.reinitializeOnlineFunctions();
//			tigerL1IPOMDP.initializeOfflineFunctions();
			
			assertEquals(
					tigerL1IPOMDP.S.size(), 
					tigerL1IPOMDP.lowerLevelFrames.get(0).S.size() + 2);
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	void testIPOMDPObsVarCreation() throws SolverException {
		System.out.println("Running testIPOMDPObsVarCreation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 10, 2);
//		tigerL1IPOMDP.solveOpponentModels();
		
		assertEquals(tigerL1IPOMDP.Omega.size(),
					2 + 
					(tigerL1IPOMDP.lowerLevelFrames.size() * 
					tigerL1IPOMDP.lowerLevelFrames.get(0).nObsVars));
		
	}
	
	@Test
	void testIPOMDPOjExtraction() {
		System.out.println("Running testIPOMDPOjExtraction()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {
			
			for (int s = 0; s < tigerL1IPOMDP.OmegaJNames.size() - 1; s++) {
				System.out.println("For and OmegaJ " + tigerL1IPOMDP.OmegaJNames.get(s));
				assertTrue(
						OP.maxAll(
								OP.abs(
									OP.sub(
										DD.one, 
										OP.addMultVarElim(
											tigerL1IPOMDP.currentOj[s],
											IPOMDP.getVarIndex(
													tigerL1IPOMDP.OmegaJNames.get(s) + "'"))))) < 1e-8);
			}
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
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
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {
//			tigerL1IPOMDP.solveOpponentModels();
//			
//			tigerL1IPOMDP.S.set(
//					tigerL1IPOMDP.oppModelVarIndex, 
//					tigerL1IPOMDP.oppModel.getOpponentModelStateVar(
//							tigerL1IPOMDP.oppModelVarIndex));
//			
//			Global.clearHashtables();
//			tigerL1IPOMDP.commitVariables();
//			
//			tigerL1IPOMDP.currentMjTfn = tigerL1IPOMDP.makeOpponentModelTransitionDD();
			
//			System.out.println(tigerL1IPOMDP.currentMjTfn);
			assertTrue(
					OP.maxAll(
							OP.abs(
								OP.sub(
									DD.one, 
									OP.addMultVarElim(
										tigerL1IPOMDP.currentMjTfn,
										IPOMDP.getVarIndex("M_j'"))))) < 1e-8);
			
			tigerL1IPOMDP.step(
					tigerL1IPOMDP.getCurrentBelief(), 
					"listen", 
					tigerL1IPOMDP.getAllPossibleObservations().get(2).toArray(new String[2]));
			
			assertTrue(
					OP.maxAll(
							OP.abs(
								OP.sub(
									DD.one, 
									OP.addMultVarElim(
										tigerL1IPOMDP.currentMjTfn,
										IPOMDP.getVarIndex("M_j'"))))) < 1e-8);
			
			tigerL1IPOMDP.step(
					tigerL1IPOMDP.getCurrentBelief(), 
					"listen", 
					tigerL1IPOMDP.getAllPossibleObservations().get(2).toArray(new String[2]));
			
			assertTrue(
					OP.maxAll(
							OP.abs(
								OP.sub(
									DD.one, 
									OP.addMultVarElim(
										tigerL1IPOMDP.currentMjTfn,
										IPOMDP.getVarIndex("M_j'"))))) < 1e-8);
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
		
	}
	
	@Test
	void testIPOMDPISInitBelief() {
		System.out.println("Running testIPOMDPISInitBelief()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {
//			System.out.println(tigerL1IPOMDP.lookAheadRootInitBelief);
//			System.out.println(
//					OP.addMultVarElim(tigerL1IPOMDP.lookAheadRootInitBelief, new int[] {2}));
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
		
	}
	
	@Test
	void testIPOMDPInitRForLATree() {
		System.out.println("Running testIPOMDPInitRforLATree()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {
////			tigerL1IPOMDP.solveOpponentModels();
//////			tigerL1IPOMDP.initializeIS();
////			
////			tigerL1IPOMDP.S.set(
////					tigerL1IPOMDP.oppModelVarIndex, 
////					tigerL1IPOMDP.oppModel.getOpponentModelStateVar(
////							tigerL1IPOMDP.oppModelVarIndex));
//			
//			Global.clearHashtables();
//			tigerL1IPOMDP.commitVariables();
//			tigerL1IPOMDP.currentTi = tigerL1IPOMDP.makeTi();
//			tigerL1IPOMDP.currentRi = tigerL1IPOMDP.makeRi();
			
//			System.out.println(tigerL1IPOMDP.currentRi.get("listen"));
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	void testIPOMDPstepping() {
		System.out.println("Running testIPOMDPstepping()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {

			List<String> beliefNodes = 
					tigerL1IPOMDP.Mj.idToNodeMap.keySet().stream()
						.map(i -> "m" + i)
						.collect(Collectors.toList());
			System.out.println(beliefNodes);
			
			System.out.println(
					InteractiveBelief.toStateMap(
							tigerL1IPOMDP, 
							tigerL1IPOMDP.lookAheadRootInitBeliefs.get(0)));
			
			tigerL1IPOMDP.step(
					tigerL1IPOMDP.lookAheadRootInitBeliefs.get(0), 
					"listen", 
					new String[] {"growl-right", "silence"});
			
			tigerL1IPOMDP.step(
					tigerL1IPOMDP.lookAheadRootInitBeliefs.get(0), 
					"listen", 
					new String[] {"growl-right", "silence"});
			
			List<String> beliefNodesNow = 
					tigerL1IPOMDP.Mj.idToNodeMap.keySet().stream()
						.map(i -> "m" + i)
						.collect(Collectors.toList());
			
			System.out.println(beliefNodesNow);
			System.out.println(
					InteractiveBelief.toStateMap(
							tigerL1IPOMDP, 
							tigerL1IPOMDP.lookAheadRootInitBeliefs.get(0)));
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	void testIPOMDPSerialization() 
			throws ZeroProbabilityObsException, 
				VariableNotFoundException, 
				IOException, 
				ClassNotFoundException {
		System.out.println("Running testIPOMDPSerialization()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		
		tigerL1IPOMDP.step(
				tigerL1IPOMDP.getInitialBeliefs().get(0), 
				"listen", 
				new String[] {"growl-left", "silence"});
		
		IPOMDP.saveIPOMDP(tigerL1IPOMDP, "/tmp/tigerIPOMDP.obj");
		
		IPOMDP ipomdp = IPOMDP.loadIPOMDP("/tmp/tigerIPOMDP.obj");
		
		ipomdp.step(
				ipomdp.getInitialBeliefs().get(0), 
				"listen", 
				new String[] {"growl-left", "silence"});
	}
	
	@Test
	void testStepVSUpdate() throws ZeroProbabilityObsException, VariableNotFoundException {
		System.out.println("Running testStepVSUpdate()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 4);
		
		DD nextBel = 
				InteractiveBelief.staticL1BeliefUpdate(
						tigerL1IPOMDP, 
						tigerL1IPOMDP.getCurrentBelief(), 
						"listen", tigerL1IPOMDP.getAllPossibleObservations().get(2).toArray(new String[2]));
		
		System.out.println("L1 BU 1: " + InteractiveBelief.toStateMap(tigerL1IPOMDP, nextBel));
//		System.out.println("L1 BU 1 DD: " + nextBel.toDDTree());
//		System.out.println("L1 BU 1 DD: " + OP.reorder(nextBel).toDDTree());
////		System.out.println("L1 BU 1 factor X: " 
////				+ OP.reorder(OP.multN(InteractiveBelief.factorInteractiveBelief(tigerL1IPOMDP, nextBel))).toDDTree());
//		System.out.println("L1 BU 1 factor : " 
//				+ Arrays.toString(InteractiveBelief.factorInteractiveBelief(tigerL1IPOMDP, nextBel)));
//		DDTree T = nextBel.toDDTree();
//		
//		for (Entry<String, DDTree> entry: T.children.entrySet()) {
//			
//			DDTree child = entry.getValue();
//			
//			if (child.varName.contentEquals("LeafVar") && ((DDTreeLeaf) child).val != 0.0)
//				System.out.println(entry);
//			
//			else if (!child.varName.contentEquals("LeafVar"))
//				System.out.println(entry);
//		}
//		
//		DD[] fbs = InteractiveBelief.factorInteractiveBelief(tigerL1IPOMDP, nextBel);
//		System.out.println("L1 BU 1 unfactor : " 
//				+ InteractiveBelief.unFactorInteractiveBelief(tigerL1IPOMDP, fbs).toDDTree());
//		System.out.println("L1 BU 1 norm : " 
//				+ OP.addMultVarElim(
//						InteractiveBelief.unFactorInteractiveBelief(tigerL1IPOMDP, fbs),
//						ArrayUtils.subarray(tigerL1IPOMDP.stateVarIndices, 0, tigerL1IPOMDP.S.size() - 1)));
//		
		nextBel = 
				InteractiveBelief.staticL1BeliefUpdate(
						tigerL1IPOMDP, 
						nextBel, 
						"listen", tigerL1IPOMDP.getAllPossibleObservations().get(5).toArray(new String[2]));
		
		System.out.println("L1 BU 2: " + InteractiveBelief.toStateMap(tigerL1IPOMDP, nextBel));
//		
//		System.out.println(tigerL1IPOMDP.Mj.idToNodeMap);
//		for (String[] triple : tigerL1IPOMDP.Mj.getMjTransitionTriples()) {
//			System.out.println(Arrays.toString(triple));
//		}
//		
//		
		nextBel = 
				InteractiveBelief.staticL1BeliefUpdate(
						tigerL1IPOMDP, 
						nextBel, 
						"listen", tigerL1IPOMDP.getAllPossibleObservations().get(5).toArray(new String[2]));
		
		System.out.println("L1 BU 3: " + InteractiveBelief.toStateMap(tigerL1IPOMDP, nextBel));
//		
		tigerL1IPOMDP.step(
				tigerL1IPOMDP.getCurrentBelief(), 
				"listen", 
				tigerL1IPOMDP.getAllPossibleObservations().get(2).toArray(new String[2]));
		
		System.out.println(InteractiveBelief.toStateMap(tigerL1IPOMDP, tigerL1IPOMDP.getCurrentBelief()));
//		
//		System.out.println(tigerL1IPOMDP.Mj.idToNodeMap);
//		for (String[] triple : tigerL1IPOMDP.Mj.getMjTransitionTriples()) {
//			System.out.println(Arrays.toString(triple));
//		}
//		System.out.println("L1 BU 1 DD: " + tigerL1IPOMDP.getCurrentBelief().toDDTree());
//		System.out.println("L1 BU 1 DD: " + OP.reorder(tigerL1IPOMDP.getCurrentBelief()).toDDTree());
//		
		tigerL1IPOMDP.step(
				tigerL1IPOMDP.getCurrentBelief(), 
				"listen",
				tigerL1IPOMDP.getAllPossibleObservations().get(5).toArray(new String[2]));

		System.out.println(InteractiveBelief.toStateMap(tigerL1IPOMDP, tigerL1IPOMDP.getCurrentBelief()));
////		for (String[] triple : tigerL1IPOMDP.Mj.getMjTransitionTriples()) {
////			System.out.println(Arrays.toString(triple));
////		}
//		
		tigerL1IPOMDP.step(
				tigerL1IPOMDP.getCurrentBelief(), 
				"listen",
				tigerL1IPOMDP.getAllPossibleObservations().get(5).toArray(new String[2]));
		
		System.out.println(InteractiveBelief.toStateMap(tigerL1IPOMDP, tigerL1IPOMDP.getCurrentBelief()));
////		
////		tigerL1IPOMDP.step(
////				tigerL1IPOMDP.getCurrentBelief(), 
////				"listen",
////				tigerL1IPOMDP.getAllPossibleObservations().get(2).toArray(new String[2]));
////		
////		System.out.println(InteractiveBelief.toStateMap(tigerL1IPOMDP, tigerL1IPOMDP.getCurrentBelief()));
	}
}
