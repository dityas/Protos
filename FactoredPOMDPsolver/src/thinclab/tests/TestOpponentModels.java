/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.ParserException;
import thinclab.exceptions.SolverException;
import thinclab.ipomdpsolver.IPOMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.ipomdpsolver.OpponentModel;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.Global;
import thinclab.symbolicperseus.OP;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.StateVar;
import thinclab.utils.BeliefTreeTable;

/*
 * @author adityas
 *
 */
class TestOpponentModels {

	public String l1DomainFile;
	public String l1DomainFile2;
	public String pomdpDomain;
	public IPOMDP tigerL1IPOMDP;
	public POMDP pomdp;
	
	public OpponentModel TestOM;
	
	@BeforeEach
	void setUp() throws Exception {
		
		this.l1DomainFile = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
		this.l1DomainFile2 = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
		this.pomdpDomain = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.95.SPUDD.txt";
		
		this.pomdp = new POMDP(this.pomdpDomain);
		this.pomdp.solvePBVI(5, 100);
		
		List<POMDP> someList = new ArrayList<POMDP>();
		someList.add(this.pomdp);
		
		this.TestOM = new OpponentModel(someList, 30);
		
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void testOpponentModelInit() {
		System.out.println("Running testOpponentModelInit()");
		assertNotNull(this.TestOM);
	}
	
	@Test
	void testOpponentModelBuildLocalModel() {
		System.out.println("Running testOpponentModelBuildLocalModel()");
		
		assertTrue(this.TestOM.T == 0);
		
		this.TestOM.buildLocalModel(2);
		
		/* will only work for the test domain */
		System.out.println(this.TestOM.currentNodes);
		assertTrue(this.TestOM.currentNodes.size() == 9);
	}
	
	@Test
	void testOpponentModelstep() {
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		this.tigerL1IPOMDP = new IPOMDP(parser, 10, 3);
		
		this.TestOM.buildLocalModel(2);
	}
	
	@Test
	void testPAjMjCreation() {
		System.out.println("Running testPAjMjCreation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		this.tigerL1IPOMDP = new IPOMDP(parser, 10, 3);
		
		try {
			tigerL1IPOMDP.S.set(
					tigerL1IPOMDP.oppModelVarIndex, 
					tigerL1IPOMDP.oppModel.getOpponentModelStateVar(
							tigerL1IPOMDP.oppModelVarIndex));
			
			Global.clearHashtables();
			tigerL1IPOMDP.commitVariables();
			DD tree = tigerL1IPOMDP.oppModel.getAjFromMj(tigerL1IPOMDP.ddMaker, tigerL1IPOMDP.Aj);
			
			DD ddTree = OP.reorder(tree);
			
			assertTrue(
					OP.maxAll(
							OP.abs(
								OP.sub(
									DD.one, 
									OP.addMultVarElim(
										ddTree, 3)))) < 1e-8);
		}
		
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	void testOpponentModelVariableCreation() {
		System.out.println("Running testOpponentModelVariableCreation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {

//			tigerL1IPOMDP.initializeFromParsers(parser);
			
			/* Get opponent model */
//			tigerL1IPOMDP.solveOpponentModels();
//			tigerL1IPOMDP.initializeIS();
			
			StateVar Mj = 
					tigerL1IPOMDP.oppModel.getOpponentModelStateVar(
							tigerL1IPOMDP.oppModelVarIndex);
			
			System.out.println(Mj);
			
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
	}
	
	@Test
	void testContextClearing() {
		System.out.println("Running testContextClearing()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 10, 2);
		try {
			
			/* Get opponent model */
//			tigerL1IPOMDP.solveOpponentModels();
//			tigerL1IPOMDP.initializeIS();
			
			tigerL1IPOMDP.oppModel.clearCurrentContext();
			
			assertTrue(tigerL1IPOMDP.oppModel.currentNodes.isEmpty());
			assertTrue(tigerL1IPOMDP.oppModel.currentRoots.isEmpty());
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
	}
}
