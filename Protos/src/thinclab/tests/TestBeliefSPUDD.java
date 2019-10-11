package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.ddhelpers.DDMaker;
import thinclab.ddhelpers.DDTree;
import thinclab.domainMaker.SPUDDHelpers.ActionSPUDD;
import thinclab.domainMaker.SPUDDHelpers.ActionSPUDDFactory;
import thinclab.domainMaker.SPUDDHelpers.BeliefSPUDD;
import thinclab.domainMaker.SPUDDHelpers.BeliefSPUDDFactory;
import thinclab.domainMaker.SPUDDHelpers.VariablesContext;
import thinclab.exceptions.VariableNotFoundException;

class TestBeliefSPUDD {

	public String[] variables;
	public String[][] varValues;
	public String[] observations;
	public String[][] obsValues;

	public DDMaker ddmaker;

	public VariablesContext varContext;
	
	public BeliefSPUDD bSPUDD;

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
	}

	@AfterEach
	void tearDown() throws Exception {

	}

	@Test
	void testBeliefSPUDDFactory() {
		System.out.println("Running testBeliefSPUDDFactory()");

		// ----------------------------------------------------------------------------
		// HAS_ROOT_VULN

		DDTree hasRootVulnInit = this.ddmaker.getDDTreeFromSequence(
				new String[] { "HAS_ROOT_VULN" },
				new String[][] { { "yes", "0.5" }, { "no", "0.5" } });

		// ----------------------------------------------------------------------------

		// ----------------------------------------------------------------------------
		// HAS_C_DATA

		DDTree hasCDataInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"HAS_C_DATA"},
				new String[][] {
					{"yes", "0.5"},
					{"no", "0.5"} });

		// ----------------------------------------------------------------------------

		// ----------------------------------------------------------------------------
		// HAS_FAKE_DATA

		DDTree hasFakeDataInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"HAS_FAKE_DATA"},
				new String[][] {
					{"yes", "0.5"},
					{"no", "0.5"} });

		// ----------------------------------------------------------------------------

		// ----------------------------------------------------------------------------
		// SESSION_PRIVS

		DDTree sessionPrivsInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS"},
				new String[][] {
					{"user", "1.0"},
					{"admin", "0.0"} });

		// ----------------------------------------------------------------------------
		
		// ----------------------------------------------------------------------------
		// PERSIST_GAINED

		DDTree persistGainedInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"PERSIST_GAINED"},
				new String[][] {
					{"none", "1.0"},
					{"user", "0.0"},
					{"admin", "0.0"}
				});

		// ----------------------------------------------------------------------------
		
		// ----------------------------------------------------------------------------
		// C_DATA_ACCESSED

		DDTree cDataAccessedInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"C_DATA_ACCESSED"},
				new String[][] {
					{"yes", "0.0"},
					{"no", "1.0"}
				});

		// ----------------------------------------------------------------------------
		
		// ----------------------------------------------------------------------------
		// FAKE_DATA_ACCESSED

		DDTree fakeDataAccessedInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"FAKE_DATA_ACCESSED"},
				new String[][] {
					{"yes", "0.0"},
					{"no", "1.0"}
				});

		// ----------------------------------------------------------------------------
		
		// ----------------------------------------------------------------------------
		// EXFIL_ONGOING

		DDTree exfilOngoingInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"EXFIL_ONGOING"},
				new String[][] {
					{"yes", "0.0"},
					{"no", "1.0"}
				});

		// ----------------------------------------------------------------------------
		
		this.bSPUDD = BeliefSPUDDFactory.getBeliefSPUDD(this.varContext,
				
				new String[] {"HAS_ROOT_VULN",
							  "HAS_C_DATA",
							  "HAS_FAKE_DATA",
							  "SESSION_PRIVS",
							  "PERSIST_GAINED",
							  "C_DATA_ACCESSED",
							  "FAKE_DATA_ACCESSED",
							  "EXFIL_ONGOING"},
				
				new DDTree[] {hasRootVulnInit,
							  hasCDataInit,
							  hasFakeDataInit,
							  sessionPrivsInit,
							  persistGainedInit,
							  cDataAccessedInit,
							  fakeDataAccessedInit,
							  exfilOngoingInit});
		
		System.out.println(bSPUDD.toSPUDD());

	}

	@Test
	void testBeliefSPUDDAdjunct() {
		System.out.println("Running testBeliefSPUDDAdjunct()");
		
//		DDTree dontKnowSessionPrivsInit = this.ddmaker.getDDTreeFromSequence(
//				new String[] {"SESSION_PRIVS"},
//				new String[][] {
//					{"user", "1.0"},
//					{"admin", "0.0"} });
//		
//		BeliefSPUDD dontKnowPrivsSPUDD = BeliefSPUDDFactory.getAdjunctBeliefSPUDD(
//				this.bSPUDD, new String[] {"SESSION_PRIVS"}, new DDTree[] {dontKnowSessionPrivsInit}, 
//				"dontKnowPrivs");
//		
//		System.out.println(dontKnowPrivsSPUDD.toSPUDD());
		
	} // void testActionSPUDDFactory

}
