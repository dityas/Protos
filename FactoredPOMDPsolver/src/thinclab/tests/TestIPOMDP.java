/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.Belief.Belief;
import thinclab.Belief.InteractiveBelief;
import thinclab.domainMaker.L0Frame;
import thinclab.domainMaker.SPUDDHelpers.VariablesContext;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.ParserException;
import thinclab.exceptions.SolverException;
import thinclab.frameworks.IPOMDP;
import thinclab.frameworks.POMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.ipomdpsolver.InteractiveStateVar;
import thinclab.ipomdpsolver.OpponentModel;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.Global;
import thinclab.symbolicperseus.OP;

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
			tigerL1IPOMDP.solveOpponentModels();
			
			/* 
			 * Stage and commit additional state and variables to populate global 
			 * arrays
			 */
			tigerL1IPOMDP.S.set(
					tigerL1IPOMDP.oppModelVarIndex, 
					tigerL1IPOMDP.oppModel.getOpponentModelStateVar(
							tigerL1IPOMDP.oppModelVarIndex));
			
			Global.clearHashtables();
			tigerL1IPOMDP.commitVariables();
			
			tigerL1IPOMDP.currentOi = tigerL1IPOMDP.makeOi();
			
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
			
			tigerL1IPOMDP.solveOpponentModels();
			
			tigerL1IPOMDP.S.set(
					tigerL1IPOMDP.oppModelVarIndex, 
					tigerL1IPOMDP.oppModel.getOpponentModelStateVar(
							tigerL1IPOMDP.oppModelVarIndex));
			
			Global.clearHashtables();
			tigerL1IPOMDP.commitVariables();
			
			tigerL1IPOMDP.currentTi = tigerL1IPOMDP.makeTi();

//			System.out.println(Arrays.toString(tigerL1IPOMDP.makeTi().get("listen")));
//			System.out.println(Arrays.toString(tigerL1IPOMDP.makeTi().get("open-right")));
//			System.out.println(Arrays.toString(tigerL1IPOMDP.makeTi().get("open-left")));
//			System.out.println("State Sequence " + tigerL1IPOMDP.S);

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
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {
			tigerL1IPOMDP.initializeFromParsers(parser);
			tigerL1IPOMDP.setMjDepth(10);
			tigerL1IPOMDP.setMjLookAhead(2);
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.commitVariables();
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
		tigerL1IPOMDP.solveOpponentModels();
		
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
			tigerL1IPOMDP.solveOpponentModels();
			
			tigerL1IPOMDP.S.set(
					tigerL1IPOMDP.oppModelVarIndex, 
					tigerL1IPOMDP.oppModel.getOpponentModelStateVar(
							tigerL1IPOMDP.oppModelVarIndex));
			
			Global.clearHashtables();
			tigerL1IPOMDP.commitVariables();
			
			tigerL1IPOMDP.currentMjTfn = tigerL1IPOMDP.makeOpponentModelTransitionDD();
			
//			System.out.println(tigerL1IPOMDP.currentMjTfn);
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
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.reinitializeOnlineFunctions();
			tigerL1IPOMDP.initializeOfflineFunctions();

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
			tigerL1IPOMDP.solveOpponentModels();
//			tigerL1IPOMDP.initializeIS();
			
			tigerL1IPOMDP.S.set(
					tigerL1IPOMDP.oppModelVarIndex, 
					tigerL1IPOMDP.oppModel.getOpponentModelStateVar(
							tigerL1IPOMDP.oppModelVarIndex));
			
			Global.clearHashtables();
			tigerL1IPOMDP.commitVariables();
			tigerL1IPOMDP.currentTi = tigerL1IPOMDP.makeTi();
			tigerL1IPOMDP.currentRi = tigerL1IPOMDP.makeRi();
			
			System.out.println(tigerL1IPOMDP.currentRi.get("listen"));
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

			List<String> beliefNodes = tigerL1IPOMDP.oppModel.currentRoots;
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
			
			List<String> beliefNodesNow = tigerL1IPOMDP.oppModel.currentRoots;
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
}
