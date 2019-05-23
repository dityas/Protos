package thinclab.ddmaker;

import thinclab.ddmaker.DDMaker;

public class AttackerDomainMaker extends DomainMaker {
	/*
	 * Defines L0 domain for attacker
	 */
	
	public AttackerDomainMaker() {
		
		// State Variables
		this.variables = 
				new String[] {"HAS_ROOT_VULN",
							  "HAS_C_DATA",
							  "HAS_FAKE_DATA",
							  "C_DATA_VIS",
							  "FAKE_DATA_VIS",
							  "SESSION_PRIVS",
							  "PERSIST_LEVEL",
							  "C_DATA_ACCESSED",
							  "FAKE_DATA_ACCESSED",
							  "EXFIL_ONGOING"};
		
		this.varValues = 
				new String[][] {{"yes", "no"},
								{"yes", "no"},
								{"yes", "no"},
								{"user", "admin"},
								{"user", "admin"},
								{"user", "admin"},
								{"none", "user", "admin"},
								{"yes", "no"},
								{"yes", "no"},
								{"yes", "no"}
							   };
						
		this.observations = new String[] {"OBS"};
		
		this.obsValues =
				new String[][] {
									{"success",
									 "none",
									 "vuln",
									 "data_c",
									 "data_nc",
									 "user",
									 "admin"}
							   };
		
		this.addVariablesToDDMaker();
	}

	@Override
	public void writeinitDef() {

		this.initDef = this.newLine;
	}

	@Override
	public void writeActionsDef() {

		this.actionsDef = this.newLine;
		
		// NOP
		this.actionsDef += "action NOP" + this.newLine;
		
		// same states
		for (int v=0; v<this.variables.length; v++) {
			this.actionsDef += this.variables[v] + " " + "(SAME" + this.variables[v]
					+ ")" + this.newLine;
		}
		
		this.actionsDef += "observe" + this.newLine;
				
		// observation DD
		DDTree obsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"OBS'"},
				new String[][] {
					{"none", "1.0"}
				});
		
		this.actionsDef += "OBS " + obsDD.toSPUDD() + this.newLine;
		
		this.actionsDef += "endobserve" + this.newLine;
		this.actionsDef += "endaction" + this.newLine + this.newLine;
		
		// end NOP
		
		// VULN_SCAN
		this.actionsDef += "action VULN_SCAN" + this.newLine;
		
		// same states
		for (int v=0; v<this.variables.length; v++) {
			this.actionsDef += this.variables[v] + " " + "(SAME" + this.variables[v]
					+ ")" + this.newLine;
		}
		
		this.actionsDef += "observe" + this.newLine;
				
		// observation DD
		obsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"HAS_ROOT_VULN'", "OBS'"},
				new String[][] {
					{"yes", "vuln", "0.9"},
					{"yes", "none", "0.1"}
				});
		
		this.actionsDef += "OBS " + obsDD.toSPUDD() + this.newLine;
		
		this.actionsDef += "endobserve" + this.newLine;
		this.actionsDef += "endaction" + this.newLine + this.newLine;
		// end VULN_SCAN
		
		// PERM_SCAN
		this.actionsDef += "action PERM_SCAN" + this.newLine;
		
		// same states
		for (int v=0; v<this.variables.length; v++) {
			this.actionsDef += this.variables[v] + " " + "(SAME" + this.variables[v]
					+ ")" + this.newLine;
		}
		
		this.actionsDef += "observe" + this.newLine;
				
		// observation DD
		obsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS'", "OBS'"},
				new String[][] {
					{"user", "user", "1.0"},
					{"admin", "admin", "1.0"}
				});
		
		this.actionsDef += "OBS " + obsDD.toSPUDD() + this.newLine;
		
		this.actionsDef += "endobserve" + this.newLine;
		this.actionsDef += "endaction" + this.newLine + this.newLine;
		// end PERM_SCAN
		
		// PRIV_ESC
		this.actionsDef += "action PRIV_ESC" + this.newLine;
		
		// same states
		for (int v=0; v<this.variables.length; v++) {
			
			// SESSION_PRIVS TRANSITION
			if (this.variables[v] == "SESSION_PRIVS") {
				DDTree sessPrivsDD = this.ddmaker.getDDTreeFromSequence(
						new String[] {"HAS_ROOT_VULN",
									  "SESSION_PRIVS",
									  "SESSION_PRIVS'"},
						
						new String[][] {
							{"yes", "user", "admin", "0.9"},
							{"yes", "user", "user", "0.1"},
							{"yes", "admin", "admin", "1.0"},
							{"no", "user", "user", "1.0"},
							{"no", "admin", "admin", "1.0"}
						});
				
				this.actionsDef += this.variables[v] + " " + sessPrivsDD.toSPUDD() + this.newLine;
			}
			
			else {
			
				this.actionsDef += this.variables[v] + " " + "(SAME" + this.variables[v]
						+ ")" + this.newLine;
			}
		}
		
		this.actionsDef += "observe" + this.newLine;
				
		// observation DD
		obsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS'", "OBS'"},
				new String[][] {
					{"user", "none", "1.0"},
					{"admin", "success", "1.0"}
				});
		
		this.actionsDef += "OBS " + obsDD.toSPUDD() + this.newLine;
		
		this.actionsDef += "endobserve" + this.newLine;
		this.actionsDef += "endaction" + this.newLine + this.newLine;
		// end PRIV_ESC
		
	}

	@Override
	public void writeRewardDef() {

		this.rewardDef = this.newLine;
	}

}
