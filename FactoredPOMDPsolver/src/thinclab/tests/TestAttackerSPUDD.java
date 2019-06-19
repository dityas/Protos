package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.domainMaker.AttackerDomain;
import thinclab.domainMaker.DefenderL1Domain;
import thinclab.domainMaker.Domain;
import thinclab.domainMaker.SPUDDHelpers.ActionSPUDD;
import thinclab.domainMaker.SPUDDHelpers.ActionSPUDDFactory;
import thinclab.domainMaker.SPUDDHelpers.VariablesContext;
import thinclab.domainMaker.ddHelpers.DDMaker;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.VariableNotFoundException;

class TestAttackerSPUDD {

	public String[] variables;
	public String[][] varValues;
	public String[] observations;
	public String[][] obsValues;

	public DDMaker ddmaker;
	
	Domain attl0Domain;

	public VariablesContext varContext;

	@BeforeEach
	void setUp() throws Exception {
		// State Variables
		this.variables = new String[] { "HAS_ROOT_VULN", "HAS_C_DATA", "HAS_FAKE_DATA", "SESSION_PRIVS",
				"PERSIST_GAINED", "C_DATA_ACCESSED", "FAKE_DATA_ACCESSED", "EXFIL_ONGOING" };

		this.varValues = new String[][] { { "yes", "no" }, { "yes", "no" }, { "yes", "no" }, { "user", "admin" },
				{ "none", "user", "admin" }, { "yes", "no" }, { "yes", "no" }, { "yes", "no" } };

		this.observations = new String[] { "OBS" };

		this.obsValues = new String[][] { { "success", "none", "vuln", "data_c", "data_nc", "user", "admin" } };

		this.ddmaker = new DDMaker();
		this.ddmaker.addAllAndPrime(this.variables, this.varValues, this.observations, this.obsValues);

		this.varContext = new VariablesContext(variables, varValues, observations, obsValues);
		
		String attackerL0 = "/home/adityas/git/repository/FactoredPOMDPsolver/src/attacker_l0.txt";
		
		if (this.attl0Domain == null) { 
			this.attl0Domain = new AttackerDomain();
			this.attl0Domain.solve(attackerL0, 5, 100);
		}
	}

	@AfterEach
	void tearDown() throws Exception {

	}

	@Test
	void testActionSPUDDInit() {
		System.out.println("Running testActionSPUDDInit()");
		String actName = "NOP";
		ActionSPUDD nopSPUDD = new ActionSPUDD(actName, this.varContext, 0.1);

		DDTree nopObsDD = this.ddmaker.getDDTreeFromSequence(new String[] { "OBS'" },
				new String[][] { { "none", "1.0" } });

		try {
			nopSPUDD.putDD("OBS", nopObsDD);
		}

		catch (VariableNotFoundException e) {
			e.printStackTrace();
		}

		nopSPUDD.fillNullDDs();

		System.out.println(nopSPUDD.toSPUDD());
	}

	@Test
	void testActionSPUDDFactory() {
		System.out.println("Running testActionSPUDDFactory()");

		// ------------------------------------------
		// action PERSIST

		String actName = "PERSIST";

		// SESSION_PRIVS transition
		DDTree sessPrivsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] { "SESSION_PRIVS", "PERSIST_GAINED", "PERSIST_GAINED'" },

				new String[][] { { "user", "none", "none", "0.1" },
						{ "user", "none", "user", "0.9" },
						{ "user", "user", "user", "1.0" },
						{ "user", "admin", "admin", "1.0" },
						{ "admin", "none", "admin", "1.0" },
						{ "admin", "admin", "admin", "1.0" },
						{ "admin", "user", "admin", "1.0" } });
		
		// Observation DD
		DDTree persistObsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"PERSIST_GAINED'", "OBS'"},
				new String[][] {
					{"none", "none", "1.0"},
					{"user", "success", "1.0"},
					{"admin", "success", "1.0"},
				});
		
		String[] ddVars = new String[] {"SESSION_PRIVS", "OBS"};
		DDTree[] dds = new DDTree[] {sessPrivsDD, persistObsDD};
		
		ActionSPUDD persistSPUDD = ActionSPUDDFactory.getActionSPUDD(
				this.varContext,
				actName,
				ddVars,
				dds,
				0.5);
		
		System.out.println(persistSPUDD.toSPUDD());

	} // void testActionSPUDDFactory
	
	@Test
	void testActionSPUDDPrefixing() {
		System.out.println("Running testActionSPUDDPrefixing");
		DefenderL1Domain defDomain = new DefenderL1Domain(this.attl0Domain);
		defDomain.initializationDriver();
		
		ActionSPUDD testSPUDD = ActionSPUDDFactory.getPrefixedActionSPUDD(
				"TEST", 
				defDomain.nextLevelVarContext, 
				defDomain.ddmaker.getDDTreeFromSequence(
						new String[] {defDomain.nextLevelVarContext.getOppPolicyName()}),
				defDomain.oppObsStateToOppObsDDRef);
		testSPUDD.fillNullDDs();
		
//		System.out.println(testSPUDD.toSPUDD());
		
		String[] varNames = defDomain.nextLevelVarContext.getVarNames();
		DDTree prefix = 
				defDomain.ddmaker.getDDTreeFromSequence(
						new String[] {defDomain.nextLevelVarContext.getOppPolicyName()});
		
		for (int i=0; i < varNames.length; i++) {
			System.out.println("Testing prefix at " + varNames[i]);
			assertTrue(prefix.equals(testSPUDD.varToDDMap.get(varNames[i])));
		}
	}
	
	@Test
	void testActionSPUDDWithDefaultTransitions() {
		System.out.println("Running testActionSPUDDWithDefaultTransitions");
		DefenderL1Domain defDomain = new DefenderL1Domain(this.attl0Domain);
		defDomain.initializationDriver();
		
		ActionSPUDD actSPUDD = ActionSPUDDFactory.getPrefixedActSPUDDWithOppTransitions(
				"TEST", 
				defDomain.nextLevelVarContext, 
				defDomain.ddmaker.getDDTreeFromSequence(
						new String[] {defDomain.nextLevelVarContext.getOppPolicyName()}), 
				defDomain.oppObsStateToOppObsDDRef, 
				defDomain.policyNodetoAction, 
				defDomain.actToVarToDDMap);
		actSPUDD.fillNullDDs();
		
//		System.out.println(actSPUDD.toSPUDD());
		DDTree prefix = 
				defDomain.ddmaker.getDDTreeFromSequence(
						new String[] {defDomain.nextLevelVarContext.getOppPolicyName()});
		
		Iterator<Entry<String, DDTree>> actSPUDDIter = actSPUDD.varToDDMap.entrySet().iterator();
		while (actSPUDDIter.hasNext()) {
			Entry<String, DDTree> entry = actSPUDDIter.next();
			assertFalse(prefix.equals(entry.getValue()));
		}
	} // void testActionSPUDDWithDefaultTransitions()

}
