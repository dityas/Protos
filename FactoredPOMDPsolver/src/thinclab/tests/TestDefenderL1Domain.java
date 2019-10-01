package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.domainMaker.DefenderL1Domain;
import thinclab.domainMaker.Domain;
import thinclab.domainMaker.SPUDDHelpers.ActionSPUDD;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.examples.AttackerDomainPOMDP;
import thinclab.exceptions.DDNotDefinedException;
import thinclab.frameworks.POMDP;
import thinclab.policyhelper.PolicyExtractor;
import thinclab.policyhelper.PolicyGraph;

class TestDefenderL1Domain {
	
	PolicyGraph attackerPolicyGraph;
	POMDP attackerPomdp;
	Domain attl0Domain;

	@BeforeEach
	void setUp() throws Exception {
		String attackerL0 = "/home/adityas/git/repository/FactoredPOMDPsolver/src/attacker_l0.txt";
		this.attl0Domain = new AttackerDomainPOMDP();
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
		System.out.println(after);
		assertNotEquals(before, after);
	}
	
	@Test
	void testWriteVarsForDefender() {
		/*
		 * Runs the method to populate the variables section of the domain
		 */
		System.out.println("Running testWriteVarsForDefender");
		DefenderL1Domain defDomain = new DefenderL1Domain(this.attl0Domain);
		defDomain.initializationDriver();

		String before = defDomain.variablesDef;
		defDomain.writeVariablesDef();
		String after = defDomain.variablesDef;
		System.out.println(after);
		assertNotEquals(before, after);
	}

	@Test
	void testDefenderDomainSolve() {
		/*
		 * Tests the automatic generation of the DefenderL1 SPUDD file and the solver
		 */
		System.out.println("Running testDefenderDomainSolve");
		String defenderL1 = "/home/adityas/git/repository/FactoredPOMDPsolver/src/defender_l1.txt";
		DefenderL1Domain defDomain = new DefenderL1Domain(this.attl0Domain);
		defDomain.solve(defenderL1, 5, 100);
	}
	
	@Test
	void testMakeAll() {
		/*
		 * Tests the automatic generation of the full SPUDD file.
		 */
		System.out.println("Running testDefenderDomainSolve");
		String defenderL1 = "/home/adityas/git/repository/FactoredPOMDPsolver/src/defender_l1.txt";
		DefenderL1Domain defDomain = new DefenderL1Domain(this.attl0Domain);
		String before = defDomain.domainString;
		defDomain.makeAll();
		String after = defDomain.domainString;
		assertNotNull(after);
		System.out.println("-----------");
		System.out.println(after);
		System.out.println("-----------");
	}

}
