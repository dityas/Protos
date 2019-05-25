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
//							  "C_DATA_VIS",
//							  "FAKE_DATA_VIS",
							  "SESSION_PRIVS",
							  "PERSIST_GAINED",
							  "C_DATA_ACCESSED",
							  "FAKE_DATA_ACCESSED",
							  "EXFIL_ONGOING"};
		
		this.varValues = 
				new String[][] {{"yes", "no"},
								{"yes", "no"},
								{"yes", "no"},
//								{"user", "admin"},
//								{"user", "admin"},
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
		this.initDef += "init[*" + this.newLine;
		
		DDTree stateInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"HAS_ROOT_VULN"},
				new String[][] {
					{"yes", "0.5"},
					{"no", "0.5"}
				}
				);
		this.initDef += stateInit.toSPUDD() + this.newLine;
		
		stateInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"HAS_C_DATA"},
				new String[][] {
					{"yes", "0.5"},
					{"no", "0.5"}
				}
				);
		this.initDef += stateInit.toSPUDD() + this.newLine;
		
		stateInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"HAS_FAKE_DATA"},
				new String[][] {
					{"yes", "0.5"},
					{"no", "0.5"}
				}
				);
		this.initDef += stateInit.toSPUDD() + this.newLine;
		
//		stateInit = this.ddmaker.getDDTreeFromSequence(
//				new String[] {"C_DATA_VIS"},
//				new String[][] {
//					{"user", "0.5"},
//					{"admin", "0.5"}
//				}
//				);
//		this.initDef += stateInit.toSPUDD() + this.newLine;
		
//		stateInit = this.ddmaker.getDDTreeFromSequence(
//				new String[] {"FAKE_DATA_VIS"},
//				new String[][] {
//					{"user", "0.5"},
//					{"admin", "0.5"}
//				}
//				);
//		this.initDef += stateInit.toSPUDD() + this.newLine;
		
		stateInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS"},
				new String[][] {
					{"user", "1.0"},
					{"admin", "0.0"}
				}
				);
		this.initDef += stateInit.toSPUDD() + this.newLine;
		
		stateInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"PERSIST_GAINED"},
				new String[][] {
					{"none", "1.0"},
					{"user", "0.0"},
					{"admin", "0.0"}
				}
				);
		this.initDef += stateInit.toSPUDD() + this.newLine;
		
		stateInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"C_DATA_ACCESSED"},
				new String[][] {
					{"yes", "0.0"},
					{"no", "1.0"}
				}
				);
		this.initDef += stateInit.toSPUDD() + this.newLine;
		
		stateInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"FAKE_DATA_ACCESSED"},
				new String[][] {
					{"yes", "0.0"},
					{"no", "1.0"}
				}
				);
		this.initDef += stateInit.toSPUDD() + this.newLine;
		
		stateInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"EXFIL_ONGOING"},
				new String[][] {
					{"yes", "0.0"},
					{"no", "1.0"}
				}
				);
		this.initDef += stateInit.toSPUDD() + this.newLine;
		
		this.initDef += "]" + this.newLine + this.newLine;
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
					{"no", "none", "1.0"},
					{"yes", "vuln", "0.9"},
					{"yes", "none", "0.1"}
				});
		
		this.actionsDef += "OBS " + obsDD.toSPUDD() + this.newLine;
		
		this.actionsDef += "endobserve" + this.newLine;
		this.actionsDef += "cost (0.1)" + this.newLine;
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
		this.actionsDef += "cost (0.1)" + this.newLine;
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
					{"admin", "success", "0.9"},
					{"admin", "none", "0.1"},
				});
		
		this.actionsDef += "OBS " + obsDD.toSPUDD() + this.newLine;
		
		this.actionsDef += "endobserve" + this.newLine;
		this.actionsDef += "cost (1.0)" + this.newLine;
		this.actionsDef += "endaction" + this.newLine + this.newLine;
		// end PRIV_ESC
		
		// PERSIST
		this.actionsDef += "action PERSIST" + this.newLine;
		
		// same states
		for (int v=0; v<this.variables.length; v++) {
			
			// PERSIST_GAINED TRANSITION
			if (this.variables[v] == "PERSIST_GAINED") {
				DDTree sessPrivsDD = this.ddmaker.getDDTreeFromSequence(
						new String[] {"SESSION_PRIVS",
									  "PERSIST_GAINED",
									  "PERSIST_GAINED'"},
						
						new String[][] {
							{"user", "none", "none", "0.1"},
							{"user", "none", "user", "0.9"},
							{"user", "user", "user", "1.0"},
							{"user", "admin", "admin", "1.0"},
							{"admin", "none", "admin", "1.0"},
							{"admin", "admin", "admin", "1.0"},
							{"admin", "user", "admin", "1.0"}
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
		DDTree pobsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"PERSIST_GAINED'", "OBS'"},
				new String[][] {
					{"none", "none", "1.0"},
					{"user", "success", "1.0"},
					{"admin", "success", "1.0"},
				});
		
		this.actionsDef += "OBS " + pobsDD.toSPUDD() + this.newLine;
		
		this.actionsDef += "endobserve" + this.newLine;
		this.actionsDef += "cost (0.5)" + this.newLine;
		this.actionsDef += "endaction" + this.newLine + this.newLine;
		// end PERSIST
		
		// action FILE_RECON
		this.actionsDef += "action FILE_RECON" + this.newLine;
		
		// same states
		for (int v=0; v<this.variables.length; v++) {
			
			// C_DATA_ACCESSED TRANSITION
			if (this.variables[v] == "C_DATA_ACCESSED") {
				DDTree sessPrivsDD = this.ddmaker.getDDTreeFromSequence(
						new String[] {"HAS_C_DATA",
									  "SESSION_PRIVS",
									  "C_DATA_ACCESSED",
									  "C_DATA_ACCESSED'"},
						
						new String[][] {
							{"yes", "*", "yes", "yes", "1.0"},
//							{"yes", "user", "no", "yes", "0.75"},
							{"yes", "user", "no", "no", "1.0"},
							{"yes", "admin", "no", "yes", "0.9"},
							{"yes", "admin", "no", "no", "0.1"},
							{"*", "*", "*", "no", "1.0"}
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
		DDTree fobsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"C_DATA_ACCESSED'",
							  "OBS'"},
				new String[][] {
					{"yes", "success", "1.0"},
					{"no", "none", "1.0"}
				});
		
		this.actionsDef += "OBS " + fobsDD.toSPUDD() + this.newLine;
		
		this.actionsDef += "endobserve" + this.newLine;
		this.actionsDef += "cost (0.5)" + this.newLine;
		this.actionsDef += "endaction" + this.newLine + this.newLine;
		// end FILE_RECON
		
		
		// action EXFIL
		this.actionsDef += "action EXFIL" + this.newLine;
		
		// same states
		for (int v=0; v<this.variables.length; v++) {
			
			// C_DATA_ACCESSED TRANSITION
			if (this.variables[v] == "EXFIL_ONGOING") {
				DDTree sessPrivsDD = this.ddmaker.getDDTreeFromSequence(
						new String[] {"C_DATA_ACCESSED",
									  "SESSION_PRIVS",
									  "EXFIL_ONGOING",
									  "EXFIL_ONGOING'"},
						
						new String[][] {
							{"yes", "*", "yes", "yes", "1.0"},
							{"yes", "user", "no", "yes", "0.75"},
							{"yes", "user", "no", "no", "0.25"},
							{"yes", "admin", "no", "yes", "0.9"},
							{"yes", "admin", "no", "no", "0.1"},
							{"no", "*", "*", "no", "1.0"}
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
		DDTree eobsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"EXFIL_ONGOING'",
							  "OBS'"},
				new String[][] {
					{"yes", "success", "1.0"},
					{"no", "none", "1.0"}
				});
		
		this.actionsDef += "OBS " + eobsDD.toSPUDD() + this.newLine;
		
		this.actionsDef += "endobserve" + this.newLine;
		this.actionsDef += "cost (0.1)" + this.newLine;
		this.actionsDef += "endaction" + this.newLine + this.newLine;
		// end FILE_RECON

	}

	@Override
	public void writeRewardDef() {

		this.rewardDef = this.newLine;
		this.rewardDef += "reward" + this.newLine;
		
		DDTree rewardDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"C_DATA_ACCESSED",
							  "EXFIL_ONGOING"},
				new String[][] {
					{"yes", "yes", "5.0"},
					{"no", "yes", "0.0"}
				});
		this.rewardDef += rewardDD.toSPUDD() + this.newLine;
	}

}
