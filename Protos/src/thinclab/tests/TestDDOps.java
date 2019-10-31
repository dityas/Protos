/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.Belief;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.MySet;
import thinclab.legacy.OP;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestDDOps {

	private static boolean setUpDone = false;
	
	public POMDP pomdp;
	
	DD initBelief;
	DD bNOPNoneFromInit;
	DD bPRIVFromInit;
	DD bFILEFromInit;
	
	@BeforeEach
	void setUp() throws Exception {
		
		CustomConfigurationFactory.initializeLogging();
		
		if (!setUpDone) {
			
			pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/attacker_l0.txt");
			
			OfflineSymbolicPerseus solver = 
					OfflineSymbolicPerseus.createSolverWithSSGAExpansion(
							this.pomdp, 
							100, 
							1, 
							10, 
							100);
			
			solver.solve();
			
			/*
			 * Initial belief
			 */
			this.initBelief = pomdp.initialBelState;
			Global.clearHashtables();
			
			/* Belief after NOP on Init and getting none obs */
			this.bNOPNoneFromInit = 
					Belief.beliefUpdate(
							pomdp, 
							pomdp.initialBelState, 5, new String[] {"none"});
			Global.clearHashtables();
			
			/* Belief after PRIV_ESC on init belief and getting success */
			this.bPRIVFromInit = Belief.beliefUpdate(pomdp, pomdp.initialBelState, 1, new String[] {"success"});
			Global.clearHashtables();
			
			/* Belief after PRIV_ESC on bPREVFromInit belief and getting success */
			this.bFILEFromInit = Belief.beliefUpdate(pomdp, pomdp.initialBelState,
									pomdp.getActions().indexOf("FILE_RECON"), 
									new String[] {"data_c"});
			Global.clearHashtables();
			
		}
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testDDEquals() {
		System.out.println("Running testDDEquals()");
		
		assertTrue(this.initBelief.equals(this.bNOPNoneFromInit));
		assertTrue(this.bNOPNoneFromInit.equals(this.initBelief));
	}
	
	@Test
	void testDDFactorAndUnFactor() {
		System.out.println("Running testDDFactorAndUnFactor()");
		
		DD[] initBeliefF = Belief.factorBelief(this.pomdp, initBelief);
		Global.clearHashtables();
		
		DD initBeliefUf = OP.multN(initBeliefF);
		assertTrue(this.initBelief.equals(initBeliefUf));
	}
	
	@Test
	void testDDHashSets() {
		System.out.println("Running testDDHashSets()");
		
		HashSet<DD> ddHashSet = new HashSet<DD>();
		
		ddHashSet.add(this.initBelief);
		
		System.out.println(Belief.toStateMap(pomdp, this.initBelief));
		System.out.println(Belief.toStateMap(pomdp, this.bNOPNoneFromInit));
		System.out.println(Belief.toStateMap(pomdp, this.bPRIVFromInit));
		assertTrue(ddHashSet.contains(this.bNOPNoneFromInit));
		assertFalse(ddHashSet.contains(this.bPRIVFromInit));
	}
	
	@Test
	void testDDArrayHashSets() {
		System.out.println("Running testDDArrayHashSets()");
		
		HashSet<List<DD>> ddHashSet = new HashSet<List<DD>>();
		
		ddHashSet.add(Belief.factorBeliefPointAsList(this.pomdp, this.initBelief));
		assertTrue(ddHashSet.contains(Belief.factorBeliefPointAsList(this.pomdp, this.bNOPNoneFromInit)));
		assertFalse(ddHashSet.contains(Belief.factorBeliefPointAsList(this.pomdp, this.bPRIVFromInit)));
	}
	
	@Test
	public void testDDNorms() {
		System.out.println("Running testDDNorms");
		System.out.println(Arrays.toString(this.bNOPNoneFromInit.getVarSet()));
		System.out.println(Arrays.toString(this.pomdp.varIndices));
		System.out.println(this.bNOPNoneFromInit);
		DD norm = OP.addMultVarElim(this.bNOPNoneFromInit, this.pomdp.varIndices);
		System.out.println(norm);
		DD normed = OP.div(this.bNOPNoneFromInit, norm);
		System.out.println(normed);
		System.out.println(Arrays.asList(Belief.factorBelief(this.pomdp, normed)));
	}
	
}
