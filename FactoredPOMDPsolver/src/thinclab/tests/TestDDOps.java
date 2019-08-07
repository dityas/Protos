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

import thinclab.Examples.AttackerDomainPOMDP;
import thinclab.Examples.TigerProblemPOMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.Global;
import thinclab.symbolicperseus.OP;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.Belief.Belief;
import thinclab.symbolicperseus.MySet;

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
		
		if (!setUpDone) {
			AttackerDomainPOMDP attackerPOMDP = new AttackerDomainPOMDP();
//			TigerProblemPOMDP attackerPOMDP = new TigerProblemPOMDP();
			File domainFile = File.createTempFile("AttackerPOMDP", ".POMDP");
			attackerPOMDP.makeAll();
			attackerPOMDP.writeToFile(domainFile.getAbsolutePath());
			pomdp = new POMDP(domainFile.getAbsolutePath());
			pomdp.solvePBVI(5, 100);
			
			/*
			 * Initial belief
			 */
			this.initBelief = pomdp.initialBelState;
			Global.clearHashtables();
			
			/*
			 * Belief after NOP on Init and getting none obs
			 */
			this.bNOPNoneFromInit = pomdp.beliefUpdate(pomdp.initialBelState, 5, new String[] {"none"});
			Global.clearHashtables();
			
			/*
			 * Belief after PRIV_ESC on init belief and getting success
			 */
			this.bPRIVFromInit = pomdp.beliefUpdate(pomdp.initialBelState, 1, new String[] {"success"});
			Global.clearHashtables();
			
			/*
			 * Belief after PRIV_ESC on bPREVFromInit belief and getting success
			 */
			this.bFILEFromInit = pomdp.beliefUpdate(pomdp.initialBelState,
									pomdp.findActionByName("FILE_RECON"), 
									new String[] {"data_c"});
			Global.clearHashtables();
			
//			this.setUpDone = true;
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
		
		DD[] initBeliefF = Belief.factorBeliefPoint(this.pomdp, initBelief);
		Global.clearHashtables();
		
		DD initBeliefUf = OP.multN(initBeliefF);
		assertTrue(this.initBelief.equals(initBeliefUf));
	}
	
	@Test
	void testDDHashSets() {
		System.out.println("Running testDDHashSets()");
		
		HashSet<DD> ddHashSet = new HashSet<DD>();
		
		ddHashSet.add(this.initBelief);
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
	
//	@Test
//	void testBeliefUpdateInternals() throws IOException, ZeroProbabilityObsException {
////		TigerProblemPOMDP attackerPOMDP = new TigerProblemPOMDP();
////		File domainFile = File.createTempFile("AttackerPOMDP", ".POMDP");
////		attackerPOMDP.makeAll();
////		attackerPOMDP.writeToFile(domainFile.getAbsolutePath());
////		pomdp = new POMDP(domainFile.getAbsolutePath());
////		pomdp.solvePBVI(5, 100);
//		
//		System.out.println("Running testBeliefUpdateInternals()");
//		
//		int actId = 0;
//		System.out.println("actId is " + actId +", Action is " + pomdp.actions[actId].name);
//		System.out.println("O is " + Arrays.deepToString(pomdp.actions[actId].obsFn));
//		System.out.println("T is " + Arrays.deepToString(pomdp.actions[actId].transFn));
//		
//		String o = "success";
//		System.out.println("Let obs recieved be " + o);
//		
//		int obs = (this.pomdp.findObservationByName(0, o) + 1);
//		System.out.println("o is " + obs);
//		
//		int[][] obsConfig = POMDP.stackArray(this.pomdp.primeObsIndices, new int[] {obs});
//		System.out.println("obs config is " + 
//				Arrays.deepToString(obsConfig));
//		
//		DD actualNextBel = Belief.beliefUpdate(
//				this.pomdp, 
//				this.pomdp.initialBelState, 
//				actId,
//				new String[] {o});
//		
//		DD[] restrictedO = OP.restrictN(this.pomdp.actions[actId].obsFn, obsConfig);
//		System.out.println("restricted O is " + 
//				Arrays.deepToString(restrictedO));
//		
//		DD[] concatArrays = POMDP.concatenateArray(
//				this.pomdp.initialBelState, 
//				this.pomdp.actions[actId].transFn, restrictedO);
//		System.out.println("concated arrays are " + Arrays.deepToString(concatArrays));
//		System.out.println("varIndices are " + Arrays.toString(this.pomdp.varIndices));
//		System.out.println("next belief is " + OP.addMultVarElim(concatArrays, this.pomdp.varIndices));
//		
//		DD[] fBelief = Belief.factorBeliefPoint(this.pomdp, this.pomdp.initialBelState);
//		System.out.println("Factored belief is " + Arrays.deepToString(fBelief));
//		
//		DD[] testConcats = POMDP.concatenateArray(
//				fBelief[0], 
//				this.pomdp.actions[actId].transFn, restrictedO);
//		/*
//		 * Trying weird shit
//		 */
//		System.out.println("Actual next belief is " + Belief.toStateMap(this.pomdp, actualNextBel));
//		System.out.println("Single var belief is " + fBelief[0]);
//		System.out.println("Before trying update " + Belief.toStateMap(this.pomdp, fBelief));
//		System.out.println("Single Var next belief is " + 
//				Belief.toStateMap(this.pomdp, 
//						Belief.beliefUpdate(this.pomdp, fBelief[0], actId, new String[] {o})));
//		/*
//		 * Ok forget this shit. Lets try a manual belief update
//		 * 
//		 */
//		System.out.println("Trying manual belief update...\r\n\r\n");
//		
//		DD[] nextBel = new DD[fBelief.length];
//		for (int b=0; b < fBelief.length; b++) {
//			DD[] restricted = OP.restrictN(this.pomdp.actions[actId].obsFn, obsConfig);
//			DD[] concatDDs = POMDP.concatenateArray(fBelief[b], new DD[] {this.pomdp.actions[actId].transFn[b]}, restricted);
//			nextBel[b] = OP.multN(concatDDs);
//			System.out.println("b is " + b + nextBel[b]);
//		}
//		
////		System.out.println("reversing mult order");
////		nextBel = new DD[fBelief.length];
////		for (int b=0; b < fBelief.length; b++) {
////			DD[] restricted = OP.restrictN(this.pomdp.actions[actId].obsFn, obsConfig);
////			DD[] concatDDs = POMDP.concatenateArray(restricted, fBelief[b], this.pomdp.actions[actId].transFn[b]);
////			nextBel[b] = OP.addMultVarElim(concatDDs, this.pomdp.varIndices);
////			System.out.println("b is " + b + nextBel[b]);
////		}
//		
//		DD nextBelJ = OP.multN(nextBel);
//		nextBelJ = OP.primeVars(nextBelJ, -pomdp.nVars);
//		System.out.println("nextBelJ is " + nextBelJ);
//		System.out.println("varIndices is " + this.pomdp.varIndices);
//		nextBelJ = OP.div(nextBelJ, OP.addMultVarElim(nextBelJ, this.pomdp.varIndices));
//		
//		System.out.println("Start belief is " + Belief.toStateMap(this.pomdp, this.pomdp.initialBelState));
//		System.out.println("Manual belief is " + Belief.toStateMap(this.pomdp, nextBelJ));
//		System.out.println("Actual belief update is " + Belief.toStateMap(this.pomdp, actualNextBel));
//	}

}
