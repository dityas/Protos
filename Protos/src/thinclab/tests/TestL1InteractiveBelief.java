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
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.InteractiveBelief;
import thinclab.ddhelpers.DDTree;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.utils.CustomConfigurationFactory;
import thinclab.utils.visualizers.Visualizer;
import thinclab.utils.visualizers.VizGraph;

/*
 * @author adityas
 *
 */
class TestL1InteractiveBelief {

	String l1DomainFile;
	
	IPOMDP tigerL1IPOMDP;
	
	@BeforeEach
	void setUp() throws Exception {
		
		CustomConfigurationFactory.initializeLogging();
		
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		tigerL1IPOMDP = new IPOMDP(parser, 10, 3);
			
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testL1StaticBeliefUpdate() throws Exception {
		System.out.println("Running testL1BeliefUpdate()");
		
		DD start = tigerL1IPOMDP.lookAheadRootInitBeliefs.get(0);
		System.out.println(Arrays.toString(tigerL1IPOMDP.stateVarIndices));
		System.out.println(InteractiveBelief.toStateMap(tigerL1IPOMDP, start));
		
		DD nextBelief = 
				InteractiveBelief.staticL1BeliefUpdate(
						tigerL1IPOMDP, 
						start, "listen", new String[] {"growl-left", "creak-right"});
		
//		assertTrue(
//				InteractiveBelief.toStateMap(
//						tigerL1IPOMDP, 
//						nextBelief).get("tiger-location")
//								   .get("tiger-left") == 0.5);
		System.out.println(Arrays.toString(tigerL1IPOMDP.stateVarIndices));
		System.out.println(InteractiveBelief.toStateMap(tigerL1IPOMDP, nextBelief));
		
		nextBelief = 
				InteractiveBelief.staticL1BeliefUpdate(
						tigerL1IPOMDP, 
						start, "listen", new String[] {"growl-left", "silence"});
		
		System.out.println(Arrays.toString(tigerL1IPOMDP.stateVarIndices));
		System.out.println(InteractiveBelief.toStateMap(tigerL1IPOMDP, nextBelief));
		
//		assertTrue(
//				InteractiveBelief.toStateMap(
//						tigerL1IPOMDP, 
//						nextBelief).get("tiger-location")
//								   .get("tiger-left") > 0.5);
	}
	
}
