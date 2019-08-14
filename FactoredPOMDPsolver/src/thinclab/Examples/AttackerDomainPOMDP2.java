/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.Examples;

import java.util.HashMap;

import thinclab.domainMaker.Domain;
import thinclab.domainMaker.SPUDDHelpers.ActionSPUDD;
import thinclab.domainMaker.SPUDDHelpers.ActionSPUDDFactory;
import thinclab.domainMaker.SPUDDHelpers.BeliefSPUDD;
import thinclab.domainMaker.SPUDDHelpers.BeliefSPUDDFactory;
import thinclab.domainMaker.SPUDDHelpers.VariablesContext;
import thinclab.domainMaker.ddHelpers.DDTree;

/*
 * @author adityas
 *
 */
public class AttackerDomainPOMDP2 extends Domain {

	/*
	 * Defines L0 domain for attacker with CHECK_DATA action
	 */
	public HashMap<String, DDTree> ddMap = new HashMap<String, DDTree>();
	
	public AttackerDomainPOMDP2() {
		
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
							  "C_DATA_TAMPERED",
							  "FAKE_DATA_TAMPERED",
							  "EXFIL_ONGOING"};
		
		String[][] varValues = 
				new String[][] {{"yes", "no"},
								{"yes", "no"},
								{"yes", "no"},
								{"user", "admin"},
								{"none", "user", "admin"},
								{"yes", "no"},
								{"yes", "no"},
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
		// C_DATA_TAMPERED

		DDTree cDataTamperedInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"C_DATA_TAMPERED"},
				new String[][] {
					{"yes", "0.0"},
					{"no", "1.0"}
				});

		// ----------------------------------------------------------------------------
		
		// ----------------------------------------------------------------------------
		// FAKE_DATA_TAMPERED

		DDTree fakeDataTamperedInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"FAKE_DATA_TAMPERED"},
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
							  "C_DATA_TAMPERED",
							  "FAKE_DATA_TAMPERED",
							  "EXFIL_ONGOING"},
				
				new DDTree[] {hasRootVulnInit,
							  hasCDataInit,
							  hasFakeDataInit,
							  sessionPrivsInit,
							  persistGainedInit,
							  cDataAccessedInit,
							  fakeDataAccessedInit,
							  cDataTamperedInit,
							  fakeDataTamperedInit,
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
		
//		this.actionSPUDDs.add(nopSPUDD);
		this.actionSPUDDMap.put(actName, nopSPUDD);
		
		// -----------------------------------------------------------------
		
		// -----------------------------------------------------------------
		// action NOP
		
		actName = "CHECK_DATA";
		
		// make observation DD
		DDTree checkDataObsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"C_DATA_ACCESSED'", 
							  "FAKE_DATA_ACCESSED'", 
							  "OBS'"},
				new String[][] {
					{"yes", "no", "data_c", "1.0"},
					{"yes", "yes", "data_nc", "0.9"},
					{"yes", "yes", "data_c", "0.1"},
					{"no", "yes", "data_nc", "0.8"},
					{"no", "yes", "data_c", "0.2"},
					{"no", "no", "none", "1.0"}
				});
		
		ActionSPUDD checkDataObsSPUDD = ActionSPUDDFactory.getActionSPUDD(
				this.varContext, actName,
				new String[] {"OBS"},
				new DDTree[] {checkDataObsDD},
				0.1);
		
//				this.actionSPUDDs.add(nopSPUDD);
		this.actionSPUDDMap.put(actName, checkDataObsSPUDD);
		
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
		
//		this.actionSPUDDs.add(vulnScanSPUDD);
		this.actionSPUDDMap.put(actName, vulnScanSPUDD);
		
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
		
//		this.actionSPUDDs.add(permScanSPUDD);
		this.actionSPUDDMap.put(actName, permScanSPUDD);
		
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
		
//		this.actionSPUDDs.add(privEscSPUDD);
		this.actionSPUDDMap.put(actName, privEscSPUDD);
		
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
		
//		this.actionSPUDDs.add(persistSPUDD);
		this.actionSPUDDMap.put(actName, persistSPUDD);
		
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
		
		DDTree fakeDataAccessedDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"HAS_FAKE_DATA",
							  "SESSION_PRIVS",
							  "FAKE_DATA_ACCESSED",
							  "FAKE_DATA_ACCESSED'"},
				
				new String[][] {
					{"yes", "*", "yes", "yes", "1.0"},
					{"yes", "*", "no", "yes", "1.0"},
////					{"yes", "user", "no", "yes", "0.1"},
//					{"yes", "admin", "no", "yes", "0.9"},
//					{"yes", "admin", "no", "no", "0.1"},
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
				new String[] {"C_DATA_ACCESSED", "OBS", "FAKE_DATA_ACCESSED"},
				new DDTree[] {cDataAccessedDD, fileReconObsDD, fakeDataAccessedDD},
				0.1);
		
//		this.actionSPUDDs.add(fileReconSPUDD);
		this.actionSPUDDMap.put(actName, fileReconSPUDD);
		
		// -----------------------------------------------------------------
		
		// -----------------------------------------------------------------
		// action MODIFY_FILE
		
		actName = "MODIFY_FILE";
		
		DDTree cDataTamperedDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS",
							  "C_DATA_ACCESSED",
							  "C_DATA_TAMPERED",
							  "C_DATA_TAMPERED'"},
				
				new String[][] {
					{"admin", "yes", "yes", "yes", "1.0"},
					{"admin", "yes", "no", "yes", "0.9"},
					{"admin", "yes", "no", "no", "0.1"},
					{"admin", "no", "*", "no", "1.0"},
					{"user", "yes", "yes", "yes", "1.0"},
					{"user", "yes", "no", "yes", "0.4"},
					{"user", "yes", "no", "no", "0.6"},
					{"user", "no", "*", "no", "1.0"},
				});
		
		DDTree fakeDataTamperedDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS",
							  "FAKE_DATA_ACCESSED",
							  "FAKE_DATA_TAMPERED",
							  "FAKE_DATA_TAMPERED'"},
				
				new String[][] {
					{"admin", "yes", "yes", "yes", "1.0"},
					{"admin", "yes", "no", "yes", "0.9"},
					{"admin", "yes", "no", "no", "0.1"},
					{"admin", "no", "*", "no", "1.0"},
					{"user", "yes", "yes", "yes", "1.0"},
					{"user", "yes", "no", "yes", "0.9"},
					{"user", "yes", "no", "no", "0.1"},
					{"user", "no", "*", "no", "1.0"},
				});
				
		
		DDTree modifyFileObsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"C_DATA_TAMPERED'",
							  "FAKE_DATA_TAMPERED'",
							  "OBS'"},
				new String[][] {
					{"yes", "yes", "success", "1.0"},
					{"no", "yes", "success", "1.0"},
					{"yes", "no", "success", "1.0"},
					{"no", "no", "none", "1.0"}
				});
		
		ActionSPUDD modifyFileSPUDD = ActionSPUDDFactory.getActionSPUDD(
				this.varContext, actName,
				new String[] {"C_DATA_TAMPERED", "FAKE_DATA_TAMPERED", "OBS"},
				new DDTree[] {cDataTamperedDD, fakeDataTamperedDD, modifyFileObsDD},
				0.5);
		
//				this.actionSPUDDs.add(exfilSPUDD);
		this.actionSPUDDMap.put(actName, modifyFileSPUDD);
		
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
		
//		this.actionSPUDDs.add(exfilSPUDD);
		this.actionSPUDDMap.put(actName, exfilSPUDD);
		
		// -----------------------------------------------------------------

	}

	@Override
	public void makeRewardDD() {
		
		// For EXFIL
		this.rewardFn = this.ddmaker.getDDTreeFromSequence(
				new String[] {"C_DATA_TAMPERED",
							  "FAKE_DATA_TAMPERED"},
				new String[][] {
					{"yes", "no", "2.0"},
					{"yes", "yes", "-1.0"},
					{"no", "yes", "-2.0"}
				});
	}
}
