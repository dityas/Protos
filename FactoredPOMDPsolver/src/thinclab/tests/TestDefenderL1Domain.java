package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.domainMaker.AttackerDomain;
import thinclab.domainMaker.DefenderL1Domain;
import thinclab.domainMaker.Domain;
import thinclab.domainMaker.SPUDDHelpers.ActionSPUDD;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.DDNotDefinedException;
import thinclab.policyhelper.PolicyExtractor;
import thinclab.policyhelper.PolicyGraph;
import thinclab.symbolicperseus.POMDP;

class TestDefenderL1Domain {
	
	PolicyGraph attackerPolicyGraph;
	POMDP attackerPomdp;
	Domain attl0Domain;

	@BeforeEach
	void setUp() throws Exception {
		String attackerL0 = "/home/adityas/git/repository/FactoredPOMDPsolver/src/attacker_l0.txt";
		this.attl0Domain = new AttackerDomain();
		this.attl0Domain.solve(attackerL0, 5, 100);
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void testOppObsDDInit() {
		/*
		 * Tests initialization of opponent's observation DD initialization
		 */
		System.out.println("Running testOppObsDDInit()");
		DefenderL1Domain defDomain = new DefenderL1Domain(this.attl0Domain);
		defDomain.initializationDriver();
		
		String[] oppObsForStateNames = defDomain.nextLevelVarContext.getOppObsForStateNames();
		for (int i=0; i < oppObsForStateNames.length; i++) {
			assertTrue(defDomain.oppObsStateToOppObsDDRef.containsKey(oppObsForStateNames[i]));
			assertTrue(
					defDomain.oppObsForStateDDDefMap.containsKey(
							defDomain.oppObsStateToOppObsDDRef.get(oppObsForStateNames[i])));
		}
	}
	
	@Test
	void testActToVarToDDMapInit() {
		System.out.println("Running testActToVarToDDMapInit");
		DefenderL1Domain defDomain = new DefenderL1Domain(this.attl0Domain);
		defDomain.initializationDriver();
		
		HashMap<String, ActionSPUDD> actSPUDDMap = defDomain.lowerDomain.actionSPUDDMap;
		Iterator<Entry<String, ActionSPUDD>> actSPUDDIter = actSPUDDMap.entrySet().iterator();
		while (actSPUDDIter.hasNext()) {
			Entry<String, ActionSPUDD> entry = actSPUDDIter.next();
			String actName = entry.getKey();
			
			Iterator<Entry<String, DDTree>> DDMapIter = entry.getValue().varToDDMap.entrySet().iterator();
			while (DDMapIter.hasNext()) {
				Entry<String, DDTree> DDEntry = DDMapIter.next();
				assertTrue(defDomain.actToVarToDDMap.get(actName).get(DDEntry.getKey()).equals(DDEntry.getValue()));
			}
			
		}
	}
	
	@Test
	void testMakeActionSPUDDForDefender() {
		/*
		 * Runs the makeActionsSPUDD method and ensures that the actionSection of the domain
		 * is properly written
		 */
		System.out.println("Running testMakeActionSPUDDForDefender");
		DefenderL1Domain defDomain = new DefenderL1Domain(this.attl0Domain);
		defDomain.initializationDriver();
		
		/*
		 * functionality to write actionSection of the domain
		 */
		String before = defDomain.actionSection;
		defDomain.makeActionsSPUDD();
		defDomain.writeActions();
		String after = defDomain.actionSection;
		
		System.out.println(after);
		assertNotEquals(before, after);
	}

	@Test
	void testMakeBeliefSPUDDForDefender() {
		/*
		 * Runs the makeBeliefsSPUDD method and ensures that the BeliefSection of the domain
		 * is properly written
		 */
		System.out.println("Running testMakeBeliefSPUDDForDefender");
		DefenderL1Domain defDomain = new DefenderL1Domain(this.attl0Domain);
		defDomain.initializationDriver();
		
		/*
		 * functionality to write actionSection of the domain
		 */
		String before = defDomain.beliefSection;
		defDomain.makeBeliefsSPUDD();
		defDomain.writeBeliefs();
		String after = defDomain.beliefSection;

		assertNotEquals(before, after);
	}

	
//	@Test
//	void testMakeDomain() {
//		System.out.println("Running testMakeDomain");
//		DefenderL1Domain defDomain = new DefenderL1Domain(this.attl0Domain);
//		defDomain.initializationDriver();
//		defDomain.writeVariablesDef();
//		defDomain.writeObsDef();
//		defDomain.writeOppPolicyDD();
//		
//		try {
//			defDomain.writeOppObsDDs();
//		} 
//		
//		catch (DDNotDefinedException e) {
//			System.err.println(e.getMessage());
//			System.exit(-1);
//		}
//
//		System.out.println(defDomain.variablesDef);
//		System.out.println(defDomain.obsDef);
//		System.out.println(defDomain.oppPolicyDDDef);
//		System.out.println(defDomain.oppObsDDDef);
////		System.out.println(defDomain.actionSection);
//	}

}
