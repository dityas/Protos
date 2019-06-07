package thinclab.domainMaker;

import java.util.HashMap;
import java.util.Iterator;

import thinclab.domainMaker.SPUDDHelpers.ActionSPUDD;
import thinclab.domainMaker.SPUDDHelpers.ActionSPUDDFactory;
import thinclab.domainMaker.SPUDDHelpers.BeliefSPUDD;
import thinclab.domainMaker.SPUDDHelpers.BeliefSPUDDFactory;
import thinclab.domainMaker.SPUDDHelpers.VariablesContext;
import thinclab.domainMaker.ddHelpers.DDMaker;
import thinclab.domainMaker.ddHelpers.DDTree;

public class AttackerDomain extends Domain {
	/*
	 * Defines L0 domain for attacker
	 */
	public HashMap<String, DDTree> ddMap = new HashMap<String, DDTree>();
	
	public AttackerDomain() {
		
	}
	
	public void makeVarContext() {
		
		// State Variables
		String[] variables = 
				new String[] {"HAS_ROOT_VULN",
							  "HAS_C_DATA",
							  "HAS_FAKE_DATA",
							  "SESSION_PRIVS",
							  "PERSIST_GAINED",
							  "C_DATA_ACCESSED",
							  "FAKE_DATA_ACCESSED",
							  "EXFIL_ONGOING"};
		
		String[][] varValues = 
				new String[][] {{"yes", "no"},
								{"yes", "no"},
								{"yes", "no"},
								{"user", "admin"},
								{"none", "user", "admin"},
								{"yes", "no"},
								{"yes", "no"},
								{"yes", "no"}
							   };
						
		String[] observations = new String[] {"OBS"};
		
		String[][] obsValues =
				new String[][] {
									{"success",
									 "none",
									 "vuln",
									 "data_c",
									 "data_nc",
									 "user",
									 "admin"}
							   };
		this.varContext = new VariablesContext(variables, varValues, observations, obsValues);
	}
	
	@Override
	public void makeBeliefsSPUDD() {
		/*
		 * Make Belief SPUDD
		 */
		// ----------------------------------------------------------------------------
		// HAS_ROOT_VULN

		DDTree hasRootVulnInit = this.ddmaker.getDDTreeFromSequence(
				new String[] { "HAS_ROOT_VULN" },
				new String[][] { 
					{ "yes", "0.5" }, { "no", "0.5" } });

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
		
		BeliefSPUDD bSPUDD = BeliefSPUDDFactory.getBeliefSPUDD(this.varContext,
				
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
		this.beliefSPUDD = bSPUDD;
		
		// ----------------------------------------------------------------------------
		// Adjuncts
		
		// Don't know privs
		
		DDTree dontKnowSessionPrivsInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS"},
				new String[][] {
					{"user", "0.5"},
					{"admin", "0.5"} });
		
		BeliefSPUDD dontKnowPrivsBelief = BeliefSPUDDFactory.getAdjunctBeliefSPUDD(
				bSPUDD, 
				new String[] {"SESSION_PRIVS"}, 
				new DDTree[] {dontKnowSessionPrivsInit},
				"dontKnowPrivs");
		
		this.otherBeliefSPUDDs.add(dontKnowPrivsBelief);
		
		
		// Yes C Data
		
		DDTree yesCDataInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"HAS_C_DATA"},
				new String[][] {
					{"yes", "1.0"},
					{"no", "0.0"} });
		
		BeliefSPUDD yesCDataBelief = BeliefSPUDDFactory.getAdjunctBeliefSPUDD(
				bSPUDD, 
				new String[] {"HAS_C_DATA"}, 
				new DDTree[] {yesCDataInit},
				"yesCData");
		
		this.otherBeliefSPUDDs.add(yesCDataBelief);
		
		// No Root Vuln
		
		DDTree noRootVulnInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"HAS_ROOT_VULN"},
				new String[][] {
					{"yes", "0.0"},
					{"no", "1.0"} });
		
		BeliefSPUDD noRootVulnBelief = BeliefSPUDDFactory.getAdjunctBeliefSPUDD(
				bSPUDD, 
				new String[] {"HAS_ROOT_VULN"}, 
				new DDTree[] {noRootVulnInit},
				"noRootVuln");
		
		this.otherBeliefSPUDDs.add(noRootVulnBelief);
		
	}

	// --------------------------------------------------------------------
	// Actions

	@Override
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
				0.1);
		
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
					{"user", "none", "1.0"},
					{"admin", "success", "1.0"}
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
				0.2);
		
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
//					{"yes", "user", "no", "yes", "0.1"},
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
				0.1);
		
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
	public void makeRewardDD() {
		
		// For EXFIL
		this.rewardFn = this.ddmaker.getDDTreeFromSequence(
				new String[] {"C_DATA_ACCESSED",
							  "EXFIL_ONGOING"},
				new String[][] {
					{"yes", "yes", "2.0"},
					{"no", "yes", "0.0"}
				});
		
		// For PERSIST
//		this.rewardFn = this.ddmaker.getDDTreeFromSequence(
//				new String[] {"PERSIST_GAINED"},
//				new String[][] {
//					{"user", "1.0"},
//					{"admin", "2.0"}
//				});
		
		// For EXFIL AND PERSIST
//		this.rewardFn = this.ddmaker.getDDTreeFromSequence(
//				new String[] {"C_DATA_ACCESSED",
//							  "EXFIL_ONGOING",
//							  "PERSIST_GAINED"},
//				new String[][] {
//					{"yes", "yes", "admin", "5.0"},
//					{"yes", "yes", "user", "2.0"}
//				});
	}

}
