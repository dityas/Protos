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
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.frameworks.IPOMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
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
		this.l1DomainFile = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		tigerL1IPOMDP = new IPOMDP();
		tigerL1IPOMDP.initializeFromParsers(parser);
		tigerL1IPOMDP.setMjDepth(10);
		tigerL1IPOMDP.setMjLookAhead(3);
		
		tigerL1IPOMDP.solveOpponentModels();
		
		tigerL1IPOMDP.S.set(
				tigerL1IPOMDP.oppModelVarIndex, 
				tigerL1IPOMDP.oppModel.getOpponentModelStateVar(
						tigerL1IPOMDP.oppModelVarIndex));
		
		Global.clearHashtables();
		tigerL1IPOMDP.commitVariables();
		
		tigerL1IPOMDP.currentAjGivenMj = 
				tigerL1IPOMDP.oppModel.getAjFromMj(
						tigerL1IPOMDP.ddMaker, 
						tigerL1IPOMDP.Aj);
		
		tigerL1IPOMDP.currentMjTfn = tigerL1IPOMDP.makeOpponentModelTransitionDD();
		
		tigerL1IPOMDP.currentOi = tigerL1IPOMDP.makeOi();
		tigerL1IPOMDP.currentTi = tigerL1IPOMDP.makeTi();
		tigerL1IPOMDP.currentOj = tigerL1IPOMDP.makeOj();
		tigerL1IPOMDP.currentRi = tigerL1IPOMDP.makeRi();
		
		DDTree mjRootBelief = 
				tigerL1IPOMDP.oppModel.getOpponentModelInitBelief(
						tigerL1IPOMDP.ddMaker);
		
		tigerL1IPOMDP.lookAheadRootInitBeliefs.clear();
		tigerL1IPOMDP.currentStateBeliefs.stream()
			.forEach(s -> tigerL1IPOMDP.lookAheadRootInitBeliefs.add(
					OP.multN(new DD[] {s.toDD(), mjRootBelief.toDD()})));
		
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
