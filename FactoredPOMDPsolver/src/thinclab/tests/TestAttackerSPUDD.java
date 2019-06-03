package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.domainMaker.ActionSPUDD;
import thinclab.domainMaker.ddHelpers.DDMaker;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.VariableNotFoundException;

class TestAttackerSPUDD {
	
	public String[] variables;
	public String[][] varValues;
	public String[] observations;
	public String[][] obsValues;
	
	public DDMaker ddmaker;

	@BeforeEach
	void setUp() throws Exception {
		// State Variables
		this.variables = new String[] { "HAS_ROOT_VULN", "HAS_C_DATA", "HAS_FAKE_DATA",
										"SESSION_PRIVS", "PERSIST_GAINED", "C_DATA_ACCESSED", 
										"FAKE_DATA_ACCESSED", "EXFIL_ONGOING" };

		this.varValues = new String[][] { { "yes", "no" }, { "yes", "no" }, { "yes", "no" },
										  { "user", "admin" }, { "none", "user", "admin" }, 
										  { "yes", "no" }, { "yes", "no" }, { "yes", "no" } };

		this.observations = new String[] { "OBS" };

		this.obsValues = new String[][] { { "success", "none", "vuln",
											"data_c", "data_nc", "user", "admin" } };
											
		this.ddmaker = new DDMaker();
		this.ddmaker.addAllAndPrime(this.variables, 
				this.varValues,
				this.observations,
				this.obsValues);
	}

	@AfterEach
	void tearDown() throws Exception {
		
	}

	@Test
	void testActionSPUDDInit() {
		System.out.println("Running testActionSPUDDInit()");
		String actName = "NOP";
		ActionSPUDD nopSPUDD = new ActionSPUDD(actName,
				this.variables, 
				this.varValues, 
				this.observations, 
				this.obsValues);
		
		DDTree nopObsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"OBS'"},
				new String[][] {
					{"none", "1.0"}
				});
		
		try {
			nopSPUDD.putDD("OBS", nopObsDD);
		} 
		
		catch (VariableNotFoundException e) {
			e.printStackTrace();
		}
		
		nopSPUDD.fillNullDDs();
		
		System.out.println(nopSPUDD.varToDDMap.toString());
	}

}
