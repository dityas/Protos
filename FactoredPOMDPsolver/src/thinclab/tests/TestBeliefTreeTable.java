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
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.ipomdpsolver.IPOMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.ipomdpsolver.OpponentModel;
import thinclab.utils.BeliefTreeTable;

/*
 * @author adityas
 *
 */
class TestBeliefTreeTable {

	public String l1DomainFile;
	public IPOMDP tigerL1IPOMDP;
	
	@BeforeEach
	void setUp() throws Exception {
		this.l1DomainFile = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		this.tigerL1IPOMDP = new IPOMDP(parser, 15, 3);

		this.tigerL1IPOMDP.solveOpponentModels();
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void testBTTinsertion() throws Exception {
		System.out.println("Running testBTTinsertion()");
		
		BeliefTreeTable bt = new BeliefTreeTable();
		bt.insertNewBelief(1, 0, "b1 at 0", "NOP");
		bt.insertNewBelief(2, 0, "b2 at 0", "ACT1");
		bt.insertNewBelief(3, 1, "b3 at 1", "ACT1");
		bt.insertNewBelief(4, 1, "b4 at 1", "NOP");
		
		int count = 0;
		ResultSet res = bt.getBeliefTable();
		
		while(res.next()) count += 1;
		
		assertTrue(count == 4);
		
		count = 0;
		res = bt.getEdgesTable();
		
		while(res.next()) count += 1;
		assertTrue(count == 0);
		
		bt.insertNewEdge(1, 3, "", "o1");
		bt.insertNewEdge(1, 4, "", "o2");
		bt.insertNewEdge(2, 4, "", "o2");
		
		count = 0;
		res = bt.getEdgesTable();
		
		while(res.next()) count += 1;
		assertTrue(count == 3);
		
	}
	
	@Test
	void testBTTbelieffetching() throws Exception {
		System.out.println("Running testBTTbelieffetching()");
		
		BeliefTreeTable bt = new BeliefTreeTable();
		bt.insertNewBelief(1, 0, "b1 at 0", "NOP");
		bt.insertNewBelief(2, 0, "b2 at 0", "ACT1");
		bt.insertNewBelief(3, 1, "b3 at 1", "ACT1");
		bt.insertNewBelief(4, 1, "b4 at 1", "NOP");
		bt.insertNewBelief(5, 2, "b5 at 2", "ACT1");
		bt.insertNewBelief(6, 2, "b6 at 2", "NOP");
		
		bt.insertNewEdge(1, 3, "", "o1");
		bt.insertNewEdge(1, 4, "", "o2");
		bt.insertNewEdge(2, 4, "", "o2");
		
		assertTrue(bt.getBeliefIDsAtTimeSteps(0, 1).size() == 2);
		assertTrue(bt.getBeliefIDsAtTimeSteps(0, 2).size() == 4);
	}
	
	@Test
	void testBTTfetchEdgeFromPId() throws Exception {
		System.out.println("Running testBTTfetchEdgeFromPId()");
		
		BeliefTreeTable bt = new BeliefTreeTable();
		bt.insertNewBelief(1, 0, "b1 at 0", "NOP");
		bt.insertNewBelief(2, 0, "b2 at 0", "ACT1");
		bt.insertNewBelief(3, 1, "b3 at 1", "ACT1");
		bt.insertNewBelief(4, 1, "b4 at 1", "NOP");
		bt.insertNewBelief(5, 2, "b5 at 2", "ACT1");
		bt.insertNewBelief(6, 2, "b6 at 2", "NOP");
		
		bt.insertNewEdge(1, 3, "", "o1");
		bt.insertNewEdge(1, 4, "", "o2");
		bt.insertNewEdge(2, 4, "", "o2");
		
		String[][] triples = bt.getEdgeTriplesFromBeliefId(1);
		
		System.out.println(Arrays.deepToString(triples));
		
		assertTrue(triples.length == 2);
		assertTrue(triples[0].length == 3);
		assertTrue(triples[0][2].contentEquals("m3"));
		assertTrue(triples[1][2].contentEquals("m4"));
		assertTrue(bt.getBeliefIDsAtTimeSteps(0, 2).size() == 4);
	}

}
