/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cern.colt.Arrays;
import thinclab.ipomdpsolver.IPOMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.ipomdpsolver.InteractiveBelief;
import thinclab.ipomdpsolver.InteractiveStateVar;
import thinclab.ipomdpsolver.OpponentModel;

/*
 * @author adityas
 *
 */
class TestInteractiveBelief {

	public String l1DomainFile;
	
	@BeforeEach
	void setUp() throws Exception {
		this.l1DomainFile = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testIBeliefCreation() {
		System.out.println("Running testIBeliefCreation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {
			/*
			 * Make initial IS
			 */
			HashSet<OpponentModel> oppModelSet = new HashSet<OpponentModel>();
			
			tigerL1IPOMDP.initializeFromParsers(parser);
			List<OpponentModel> oppModels = tigerL1IPOMDP.getOpponentModels();
			oppModelSet.addAll(oppModels);
			List<InteractiveStateVar> ISVars = 
					tigerL1IPOMDP.makeInteractiveStateSpace(oppModelSet);
			
			InteractiveBelief IB = new InteractiveBelief(ISVars);
			IB.makeUniformBelief();
			
			float sum = 0;
			for (int i=0; i < IB.beliefPoint.size(); i++) {
				sum += IB.beliefPoint.get(i);
			}
			
			System.out.println(IB.beliefPoint);
			System.out.println(tigerL1IPOMDP.actions[0].name);
			System.out.println(Arrays.toString(tigerL1IPOMDP.getTransFnTabular(0)));
			assertEquals(1.0, sum);
		} 
		
		catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			fail();
		}
	}

}
