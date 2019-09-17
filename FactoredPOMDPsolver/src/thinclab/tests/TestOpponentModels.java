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
import thinclab.utils.LoggerFactory;

/*
 * @author adityas
 *
 */
class TestOpponentModels {

	public String l1DomainFile;
	public String l1DomainFile2;
	public IPOMDP tigerL1IPOMDP;
	
	@BeforeEach
	void setUp() throws Exception {
		
		LoggerFactory.startFineLogging();
		
		this.l1DomainFile = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
		this.l1DomainFile2 = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		this.tigerL1IPOMDP = new IPOMDP(parser, 15, 3);

		this.tigerL1IPOMDP.solveOpponentModels();
//		this.tigerL1IPOMDP.initializeIS();
	}

	@AfterEach
	void tearDown() throws Exception {
	}
//
//	@Test
//	void testIPOMDPL1OpponentModelStorage() throws Exception {
//		/*
//		 * Test Opponents solutions by L1 agents
//		 */
//		System.out.println("Running testIPOMDPL1OpponentModelBeliefStorage()");
//					
//		/* store the belief nodes */
////		tigerL1IPOMDP.oppModel.storeOpponentModel();
//		
//		BeliefTreeTable bt = this.tigerL1IPOMDP.oppModel.getLocalStorage();
//		
//		int numRecords = 0;
//		ResultSet res = bt.getBeliefTable();
//		while (res.next()) {
//			numRecords += 1;
////			System.out.println(res.getInt("belief_id") + "\t" 
////					+ res.getString("optimal_action") + "\t"
////					+ res.getInt("horizon") + "\t"
////					+ res.getString("belief_text"));
//		}
//		
//		assertTrue(numRecords == tigerL1IPOMDP.oppModel.nodesList.size());
//		
//		ResultSet resEdges = bt.getEdgesTable();
//		while (resEdges.next()) {
//			numRecords += 1;
////			System.out.println(resEdges.getInt("parent_belief_id") + "\t"
////					+ resEdges.getString("action") + "\t"
////					+ resEdges.getString("obs") + "\t"
////					+ resEdges.getInt("child_belief_id"));
//		}
//		
//		System.out.println(
//				Arrays.deepToString(
//						bt.getEdgeTriplesFromBeliefIds(
//								bt.getBeliefIDsAtTimeSteps(0, 4))));
//	}
	
	@Test
	void testOpponentModelInit() {
		System.out.println("Running testOpponentModelInit()");
		
		assertNotNull(this.tigerL1IPOMDP.oppModel);
	}
	
	@Test
	void testOpponentModelTraversal() {
		System.out.println("Running testOpponentModelTraversal()");
		
//		BeliefTreeTable bt = this.tigerL1IPOMDP.oppModel.getLocalStorage();
		
		try {
		HashSet<String> varChilds = new HashSet<String>();
		
		varChilds.addAll(
				Arrays.asList(this.tigerL1IPOMDP.S.get(
						this.tigerL1IPOMDP.oppModelVarIndex).valNames));
		
		varChilds.removeAll(this.tigerL1IPOMDP.oppModel.currentNodes);
		assertTrue(varChilds.isEmpty());
		}
		
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	void testPAjMjCreation() {
		System.out.println("Running testPAjMjCreation()");
		
		try {
			tigerL1IPOMDP.S.set(
					tigerL1IPOMDP.oppModelVarIndex, 
					tigerL1IPOMDP.oppModel.getOpponentModelStateVar(
							tigerL1IPOMDP.oppModelVarIndex));
			
			Global.clearHashtables();
			tigerL1IPOMDP.commitVariables();
			DDTree tree = tigerL1IPOMDP.oppModel.getAjFromMj(tigerL1IPOMDP.ddMaker, tigerL1IPOMDP.Aj);
			
			DD ddTree = OP.reorder(tree.toDD());
			
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
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.initializeIS();
			
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
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.initializeIS();
			
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
