package thinclab.domainMaker;

import java.util.HashMap;
import java.util.Iterator;

import thinclab.domainMaker.ddHelpers.DDMaker;
import thinclab.domainMaker.ddHelpers.DDTree;

public class AttackerDomainMaker extends DomainMaker {
	/*
	 * Defines L0 domain for attacker
	 */
	public HashMap<String, DDTree> ddMap = new HashMap<String, DDTree>();
	
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
		this.makeVarContext();
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
		this.actionsDef += this.newLine;
		
		Iterator<ActionSPUDD> actionIter = this.actionSPUDDs.iterator();
		
		while (actionIter.hasNext()) {
			this.actionsDef += actionIter.next().toSPUDD() + this.newLine;
		}
	}

	public void makeActionsSPUDD() {
		
		// -----------------------------------------------------------------
		// action NOP
		
		String actName = "NOP";
		
		// make observation DD
		DDTree nopObsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"OBS'"},
				new String[][] {
					{"none", "1.0"}
				});
		
		ActionSPUDD nopSPUDD = ActionSPUDDFactory.getActionSPUDD(
				this.varContext, actName,
				new String[] {"OBS"},
				new DDTree[] {nopObsDD},
				0.0);
		
		this.actionSPUDDs.add(nopSPUDD);
		
		// -----------------------------------------------------------------
		
		// -----------------------------------------------------------------
		// action VULN_SCAN
		
		actName = "VULN_SCAN";
		
		DDTree vulnScanObsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"HAS_ROOT_VULN'", "OBS'"},
				new String[][] {
					{"no", "none", "1.0"},
					{"yes", "vuln", "0.9"},
					{"yes", "none", "0.1"}
				});
		
		ActionSPUDD vulnScanSPUDD = ActionSPUDDFactory.getActionSPUDD(
				this.varContext, actName,
				new String[] {"OBS"},
				new DDTree[] {vulnScanObsDD},
				1.0);
		
		this.actionSPUDDs.add(vulnScanSPUDD);
		
		// -----------------------------------------------------------------
		
		// -----------------------------------------------------------------
		// action PERM_SCAN
		
		actName = "PERM_SCAN";
		
		DDTree permScanObsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS'", "OBS'"},
				new String[][] {
					{"user", "user", "1.0"},
					{"admin", "admin", "1.0"}
				});
		
		ActionSPUDD permScanSPUDD = ActionSPUDDFactory.getActionSPUDD(
				this.varContext, actName,
				new String[] {"OBS"},
				new DDTree[] {permScanObsDD},
				0.1);
		
		this.actionSPUDDs.add(permScanSPUDD);
		
		// -----------------------------------------------------------------
		
		// -----------------------------------------------------------------
		// action PRIV_ESC
		
		actName = "PRIV_ESC";
		
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
		
		DDTree privEscObsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS'", "OBS'"},
				new String[][] {
					{"user", "user", "1.0"},
					{"admin", "admin", "1.0"}
				});
		
		ActionSPUDD privEscSPUDD = ActionSPUDDFactory.getActionSPUDD(
				this.varContext, actName,
				new String[] {"SESSION_PRIVS", "OBS"},
				new DDTree[] {sessPrivsDD, privEscObsDD},
				0.5);
		
		this.actionSPUDDs.add(privEscSPUDD);
		
		// -----------------------------------------------------------------
		
		// -----------------------------------------------------------------
		// action PERSIST
		
		actName = "PERSIST";
		
		DDTree persistGainedDD = this.ddmaker.getDDTreeFromSequence(
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
		
		DDTree persistObsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"PERSIST_GAINED'", "OBS'"},
				new String[][] {
					{"none", "none", "1.0"},
					{"user", "success", "1.0"},
					{"admin", "success", "1.0"},
				});
		
		ActionSPUDD persistSPUDD = ActionSPUDDFactory.getActionSPUDD(
				this.varContext, actName,
				new String[] {"PERSIST_GAINED", "OBS"},
				new DDTree[] {persistGainedDD, persistObsDD},
				0.5);
		
		this.actionSPUDDs.add(persistSPUDD);
		
		// -----------------------------------------------------------------
		
		// -----------------------------------------------------------------
		// action FILE_RECON
		
		actName = "FILE_RECON";
		
		DDTree cDataAccessedDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"HAS_C_DATA",
							  "SESSION_PRIVS",
							  "C_DATA_ACCESSED",
							  "C_DATA_ACCESSED'"},
				
				new String[][] {
					{"yes", "*", "yes", "yes", "1.0"},
					{"yes", "user", "no", "no", "1.0"},
					{"yes", "admin", "no", "yes", "0.9"},
					{"yes", "admin", "no", "no", "0.1"},
					{"no", "*", "*", "no", "1.0"}
				});

		DDTree fileReconObsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"C_DATA_ACCESSED'",
							  "OBS'"},
				new String[][] {
					{"yes", "success", "1.0"},
					{"no", "none", "1.0"}
				});
		
		ActionSPUDD fileReconSPUDD = ActionSPUDDFactory.getActionSPUDD(
				this.varContext, actName,
				new String[] {"C_DATA_ACCESSED", "OBS"},
				new DDTree[] {cDataAccessedDD, fileReconObsDD},
				0.5);
		
		this.actionSPUDDs.add(fileReconSPUDD);
		
		// -----------------------------------------------------------------
		
		// -----------------------------------------------------------------
		// action EXFIL
		
		actName = "EXFIL";
		
		DDTree exfilOngoingDD = this.ddmaker.getDDTreeFromSequence(
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
				
		
		DDTree exfilObsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"EXFIL_ONGOING'",
							  "OBS'"},
				new String[][] {
					{"yes", "success", "1.0"},
					{"no", "none", "1.0"}
				});
		
		ActionSPUDD exfilSPUDD = ActionSPUDDFactory.getActionSPUDD(
				this.varContext, actName,
				new String[] {"EXFIL_ONGOING", "OBS"},
				new DDTree[] {exfilOngoingDD, exfilObsDD},
				0.5);
		
		this.actionSPUDDs.add(exfilSPUDD);
		
		// -----------------------------------------------------------------

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
