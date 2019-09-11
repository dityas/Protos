/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.ipomdpsolver.IPOMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.ipomdpsolver.InteractiveBelief.InteractiveBelief;
import thinclab.ipomdpsolver.InteractiveBelief.LookAheadTree;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.OP;
import thinclab.utils.visualizers.Visualizer;
import thinclab.utils.visualizers.VizGraph;

/*
 * @author adityas
 *
 */
class TestL1InteractiveBelief {

	String l1DomainFile;
	
	@BeforeEach
	void setUp() throws Exception {
		this.l1DomainFile = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testL1StaticBeliefUpdate() {
		System.out.println("Running testL1BeliefUpdate()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 5, 3);
		try {
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.initializeIS();
			
			DD start = tigerL1IPOMDP.lookAheadRootInitBeliefs.get(0);
						
			DD nextBelief = 
					InteractiveBelief.staticL1BeliefUpdate(
							tigerL1IPOMDP, 
							start, "listen", new String[] {"growl-left", "creak-left"});
			
			assertTrue(
					InteractiveBelief.toStateMap(
							tigerL1IPOMDP, 
							nextBelief).get("tiger-location")
									   .get("tiger-left") == 0.5);
			
			System.out.println(InteractiveBelief.toStateMap(tigerL1IPOMDP, nextBelief));
			
			nextBelief = 
					InteractiveBelief.staticL1BeliefUpdate(
							tigerL1IPOMDP, 
							start, "listen", new String[] {"growl-left", "silence"});
			
			System.out.println(InteractiveBelief.toStateMap(tigerL1IPOMDP, nextBelief));
			
			assertTrue(
					InteractiveBelief.toStateMap(
							tigerL1IPOMDP, 
							nextBelief).get("tiger-location")
									   .get("tiger-left") > 0.5);
		}

		catch (Exception e) {
			e.printStackTrace();
			fail();
			System.exit(-1);
		}
	}
	
//	@Test
//	void testL1StaticBeliefUpdateUnrestricted() {
//		System.out.println("Running testL1BeliefUpdateUnrestricted()");
//		
//		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
//		parser.parseDomain();
//		
//		/*
//		 * Initialize IPOMDP
//		 */
//		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
//		try {
//			tigerL1IPOMDP.solveOpponentModels();
//			tigerL1IPOMDP.initializeIS();
//			
//			DD start = tigerL1IPOMDP.lookAheadRootInitBeliefs.get(0);
//			List<List<String>> obs = tigerL1IPOMDP.getAllObservationsList();
//			
//			for (String Ai : tigerL1IPOMDP.Ai) {
////				for (List<String> o : obs) {
//					
//					DD nextBelief;
//					
//					try {
//						nextBelief = InteractiveBelief.staticL1BeliefUpdateUnrestricted(
//										tigerL1IPOMDP, start, Ai);
//					}
//					
//					catch (ZeroProbabilityObsException e) {
//						continue;
//					}
//					
//					System.out.println(
//							"For action " 
//							+ Ai + " starting from belief " 
//							+ InteractiveBelief.toStateMap(tigerL1IPOMDP, start));
//					
//					System.out.println("Next Belief is " + InteractiveBelief.toStateMap(
//							tigerL1IPOMDP, nextBelief));
//					
//					System.out.println("============================================\r\n");
//					
////					assertTrue(
////							OP.maxAll(
////									OP.abs(
////										OP.sub(
////											DD.one, 
////											OP.addMultVarElim(
////												nextBelief,
////												tigerL1IPOMDP.stateVarIndices)))) < 1e-8);
//			}
//		}
//
//		catch (Exception e) {
//			e.printStackTrace();
//			fail();
//			System.exit(-1);
//		}
//	}
	
	@Test
	void testL1LookAheadTree() {
		System.out.println("Running testL1LookAheadTree()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.initializeIS();
			
			LookAheadTree lt = new LookAheadTree(tigerL1IPOMDP);
			
//			VizGraph vg = VizGraph.getVizGraphFromLATreeTriples(lt.toStringTriples());
//			System.out.println(vg.graph);
			
//			Visualizer viz = 
//					new Visualizer(VizGraph.getVizGraphFromLATreeTriples(lt.toStringTriples()));
		}

		catch (Exception e) {
			e.printStackTrace();
			fail();
			System.exit(-1);
		}
	}
}
