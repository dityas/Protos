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

import thinclab.domainMaker.L0Frame;
import thinclab.domainMaker.SPUDDHelpers.VariablesContext;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.ParserException;
import thinclab.exceptions.SolverException;
import thinclab.ipomdpsolver.IPOMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.ipomdpsolver.InteractiveStateVar;
import thinclab.ipomdpsolver.OpponentModel;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.Global;
import thinclab.symbolicperseus.OP;
import thinclab.symbolicperseus.POMDP;

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
	
//	@Test
//	void testIPOMDPL1FrameSolve() {
//		/*
//		 * Test IPOMDP solve function for L1
//		 */
//		System.out.println("Running testIPOMDPL1FrameSolve()");
//		
//		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
//		parser.parseDomain();
//		
//		/*
//		 * Initialize IPOMDP
//		 */
//		IPOMDP tigerL1IPOMDP = new IPOMDP();
//		try {
//			tigerL1IPOMDP.initializeFromParsers(parser);
//		} 
//		
//		catch (ParserException e) {
//			System.err.println(e.getMessage());
//			fail();
//		}
//		
//		tigerL1IPOMDP.solveIPBVI(10, 100);
//		
//	}
	
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
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {
			tigerL1IPOMDP.initializeFromParsers(parser);
			
			tigerL1IPOMDP.oppModel = tigerL1IPOMDP.getOpponentModel();
			
			/* 
			 * Stage and commit additional state and variables to populate global 
			 * arrays
			 */
			tigerL1IPOMDP.setUpIS();
			tigerL1IPOMDP.setUpOmegaI();
			tigerL1IPOMDP.commitVariables();
			
			/*
			 * These will need to be constructed at every belief update
			 */
			
			/* Make M_j transition DD */
			tigerL1IPOMDP.makeOpponentModelTransitionDD();
			
			long then = System.nanoTime();
			HashMap<String, HashMap<String, DDTree>> Oi = tigerL1IPOMDP.makeOi();
			long now = System.nanoTime();

			System.out.println("Exec time: " + (now - then)/10000000 + " millisec.");
			assertTrue(Oi.size() == tigerL1IPOMDP.Ai.size());
			System.out.println(tigerL1IPOMDP.Omega.size());
			for (String actName : Oi.keySet())
				assertTrue(Oi.get(actName).size() == tigerL1IPOMDP.Omega.size());
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
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {
			tigerL1IPOMDP.initializeFromParsers(parser);
			
			tigerL1IPOMDP.oppModel = tigerL1IPOMDP.getOpponentModel();
			
			/* 
			 * Stage and commit additional state and variables to populate global 
			 * arrays
			 */
			tigerL1IPOMDP.setUpIS();
			tigerL1IPOMDP.setUpOmegaI();
			tigerL1IPOMDP.commitVariables();
			
			/*
			 * These will need to be constructed at every belief update
			 */
			
			/* Make M_j transition DD */
			tigerL1IPOMDP.makeOpponentModelTransitionDD();
			
			long then = System.nanoTime();
			HashMap<String, HashMap<String, DDTree>> Ti = tigerL1IPOMDP.makeTi();
			long now = System.nanoTime();

			System.out.println("Exec time: " + (now - then)/10000000 + " millisec.");
			
			for (String Ai : Ti.keySet()) {
				for (String s : Ti.get(Ai).keySet()) {
					assertTrue(
							OP.maxAll(
									OP.abs(
										OP.sub(
											DD.one, 
											OP.addMultVarElim(
												Ti.get(Ai).get(s).toDD(),
												IPOMDP.getVarIndex(s + "'"))))) < 1e-8);
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
			
			/* set opponent model var */
			tigerL1IPOMDP.oppModel = tigerL1IPOMDP.getOpponentModel();
			
			System.out.println(tigerL1IPOMDP.parser.S);
			
			tigerL1IPOMDP.setUpIS();
			
			assertEquals(
					tigerL1IPOMDP.S.size(), 
					tigerL1IPOMDP.lowerLevelFrames.get(0).S.size() + 1);
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	void testIPOMDPObsVarCreation() {
		System.out.println("Running testIPOMDPObsVarCreation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {
			tigerL1IPOMDP.initializeFromParsers(parser);
			
			/* set opponent model var */
			tigerL1IPOMDP.oppModel = tigerL1IPOMDP.getOpponentModel();
			tigerL1IPOMDP.setUpIS();
			
			int prevNObs = tigerL1IPOMDP.Omega.size();
			
			tigerL1IPOMDP.setUpOmegaI();
			
			assertEquals(tigerL1IPOMDP.Omega.size(),
						2 + 
						(tigerL1IPOMDP.lowerLevelFrames.size() * 
						tigerL1IPOMDP.lowerLevelFrames.get(0).nObsVars));
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
//			e.printStackTrace();
			fail();
		}
		
		System.out.println();
	}
	
	@Test
	void testIPOMDPOjExtraction() {
		System.out.println("Running testIPOMDPOjExtraction()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {
			tigerL1IPOMDP.initializeFromParsers(parser);
			
			tigerL1IPOMDP.oppModel = tigerL1IPOMDP.getOpponentModel();
			
			/* 
			 * Stage and commit additional state and variables to populate global 
			 * arrays
			 */
			tigerL1IPOMDP.setUpIS();
			tigerL1IPOMDP.setUpOmegaI();
			tigerL1IPOMDP.commitVariables();
			
			/*
			 * These will need to be constructed at every belief update
			 */
			
			/* Make M_j transition DD */
			tigerL1IPOMDP.makeOpponentModelTransitionDD();
			
			/* Make O (observation functions) */
//			tigerL1IPOMDP.makeOi();
			
			DD growl_jDD = tigerL1IPOMDP.getOj().get("growl_j").toDD();
			List<Integer> varsBeforeSummout = new ArrayList<Integer>();
			varsBeforeSummout.addAll(
					Arrays.stream(growl_jDD.getVarSet())
						.boxed()
						.collect(Collectors.toList()));
			
			varsBeforeSummout.addAll(
					Arrays.stream(tigerL1IPOMDP.MjTFn.getVarSet())
						.boxed()
						.collect(Collectors.toList()));
			
			assertTrue(varsBeforeSummout.contains(POMDP.getVarIndex("growl_j'")));
			
			DD tauDD = 
					OP.addMultVarElim(
							new DD[] {growl_jDD, tigerL1IPOMDP.MjTFn}, 
							POMDP.getVarIndex("growl_j'"));
			
			List<Integer> varsAfterSummout = new ArrayList<Integer>();
			varsAfterSummout.addAll(
					Arrays.stream(tauDD.getVarSet())
						.boxed()
						.collect(Collectors.toList()));
			
			assertFalse(varsAfterSummout.contains(POMDP.getVarIndex("growl_j'")));
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
//			e.printStackTrace();
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
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {
			tigerL1IPOMDP.initializeFromParsers(parser);
			
			/* set opponent model var */
			tigerL1IPOMDP.oppModel = tigerL1IPOMDP.getOpponentModel();
			tigerL1IPOMDP.setUpIS();
			tigerL1IPOMDP.setUpOmegaI();
			tigerL1IPOMDP.commitVariables();
			
			long then = System.nanoTime();
			tigerL1IPOMDP.makeOpponentModelTransitionDD();
			long now = System.nanoTime();
			
			System.out.println("Exec time: " + (now - then)/10000000 + " millisec.");
			assertTrue(
					OP.maxAll(
							OP.abs(
								OP.sub(
									DD.one, 
									OP.addMultVarElim(
										tigerL1IPOMDP.MjTFn,
										IPOMDP.getVarIndex("M_j'"))))) < 1e-8);
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
		
	}

}
