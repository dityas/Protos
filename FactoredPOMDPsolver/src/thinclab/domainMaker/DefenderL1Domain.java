package thinclab.domainMaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import thinclab.domainMaker.SPUDDHelpers.ActionSPUDD;
import thinclab.domainMaker.SPUDDHelpers.ActionSPUDDFactory;
import thinclab.domainMaker.SPUDDHelpers.BeliefSPUDD;
import thinclab.domainMaker.SPUDDHelpers.BeliefSPUDDFactory;
import thinclab.domainMaker.SPUDDHelpers.NextLevelVariablesContext;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.VariableNotFoundException;

public class DefenderL1Domain extends NextLevelDomain {
	
	// Set lowerDomain variable (Step 1) 
	public DefenderL1Domain(Domain lowerDomain) {
		this.lowerDomain = lowerDomain;
	}
	
	// Implement abstract method makeVarContext (Step 2)
	public void makeVarContext() {
		String[] obsNames = 
				new String[] {"DATA_ACCESSED",
						"PRIVS",
						"NET_TX",
						"EXEC_RESULT"};
		
		String[][] obsValNames = 
				new String[][] {
						{"c", "nc", "fake", "none"},
						{"user", "admin"},
						{"high", "low"},
						{"success", "fail"}
						};
						
		this.nextLevelVarContext = new NextLevelVariablesContext(
				this.lowerDomain.getVarContext(), 
				obsNames, 
				obsValNames, 
				this.lowerDomain.getPolicyValNames());
	}
		
	public void makeRewardDD() {
		
	}
	
	/*
	 * Populate actionSPUDDMap with ActionSPUDD objects keyed by action names
	 * 
	 * Currently defined actions for defender:
	 * 		
	 * 		NOP	->	Do nothing
	 * 				No states are affected
	 * 				Gets null observations
	 * 
	 * 		INTERPRET_LOGS	->	Read logs and get observations about what attacker is doing
	 * 							No states are affected
	 * 							Observations related to current state of the system are received
	 * 
	 * 		MOVE_C_DATA		->	Move critical data out of the system
	 * 							HAS_C_DATA will be affected
	 * 							Observation will indicate if this operation was successful
	 * 							Taking this action if attacker is not trying to exfil will incur a cost
	 * 		
	 * 		DROP_PRIVS		->	Drop privileges of the attacker to user
	 * 							SESSION_PRIVS will be affected
	 * 							Observation will indicate if the command was successfully executed
	 * 							Taking action when session is already in user privs will incur a cost
	 * 							
	 */
	public void makeActionsSPUDD() {
		
		/*
		 * Make policy DD prefix for use with all ActionSPUDD objects and default observation DDs
		 * so that they can be reused
		 */
		
		// ---------------------------------------------------------------------------------------
		DDTree policyPrefix = this.ddmaker.getDDTreeFromSequence(
				new String[] {this.nextLevelVarContext.getOppPolicyName()});
		
		DDTree defaultDataAccessedDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"DATA_ACCESSED'"}, 
				new String[][] {
					{"none", "1.0"}
				});
		
		DDTree defaultPrivsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"PRIVS'"}, 
				new String[][] {
					{"user", "1.0"}
				});
		
		DDTree defaultNetTxDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"NET_TX'"}, 
				new String[][] {
					{"low", "1.0"}
				});
		
		DDTree defaultExecResultDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {"EXEC_RESULT'"},
				new String[][] {
					{"fail", "0.5"},
					{"success", "0.5"}
				});
		/*
		 * Also initialize the ActionSPUDD factory to use class methods instead of statics to avoid
		 * rewriting args for every call
		 */
		ActionSPUDDFactory spuddFactory = new ActionSPUDDFactory(
				this.nextLevelVarContext, 
				policyPrefix, 
				this.oppObsStateToOppObsDDRef, 
				this.policyNodetoAction, 
				this.actToVarToDDMap);
		
		// ---------------------------------------------------------------------------------------
		/*
		 * Make NOP ActionSPUDD
		 */
		
		ActionSPUDD nopSPUDD = spuddFactory.getPrefixedActSPUDDWithOppTransitions("NOP");
		
		try {
			nopSPUDD.putDD("DATA_ACCESSED", defaultDataAccessedDD);
			nopSPUDD.putDD("PRIVS", defaultPrivsDD);
			nopSPUDD.putDD("NET_TX", defaultNetTxDD);
			nopSPUDD.putDD("EXEC_RESULT", defaultExecResultDD);
		} 
		
		catch (VariableNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		
		this.actionSPUDDMap.put("NOP", nopSPUDD);
		
		// ---------------------------------------------------------------------------------------
		/*
		 * Make INTERPRET_LOGS ActionSPUDD
		 */
		
		ActionSPUDD interpretLogsSPUDD = 
				spuddFactory.getPrefixedActSPUDDWithOppTransitions("INTERPRET_LOGS");
		
		DDTree obsDataAccessedDD = 
				this.ddmaker.getDDTreeFromSequence(
						new String[] {"C_DATA_ACCESSED'",
									  "FAKE_DATA_ACCESSED'",
									  "DATA_ACCESSED'"}, 
						new String[][] {
							{"yes", "yes", "c", "0.9"},
							{"yes", "yes", "none", "0.1"},
							{"no", "no", "none", "1.0"},
							{"no", "yes", "fake", "1.0"},
							{"yes", "no", "c", "0.7"},
							{"yes", "no", "none", "0.3"},
							{"no", "yes", "fake", "1.0"}
						});
		
		DDTree obsPrivsDD = 
				this.ddmaker.getDDTreeFromSequence(
						new String[] {"SESSION_PRIVS'",
									  "PRIVS'"}, 
						new String[][] {
							{"user", "user", "1.0"},
							{"admin", "admin", "0.8"},
							{"admin", "user", "0.2"}
						});
		
		DDTree obsNetTxDD = 
				this.ddmaker.getDDTreeFromSequence(
						new String[] {"EXFIL_ONGOING'",
									  "NET_TX'"}, 
						new String[][] {
							{"yes", "high", "1.0"},
							{"no", "low", "1.0"}
						});
		
		try {
			interpretLogsSPUDD.putDD("DATA_ACCESSED", obsDataAccessedDD);
			interpretLogsSPUDD.putDD("PRIVS", obsPrivsDD);
			interpretLogsSPUDD.putDD("NET_TX", obsNetTxDD);
			interpretLogsSPUDD.putDD("EXEC_RESULT", defaultExecResultDD);
		}
		
		catch (VariableNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		
		this.actionSPUDDMap.put("INTERPRET_LOGS", interpretLogsSPUDD);
		
		// ---------------------------------------------------------------------------------------
		
		// ---------------------------------------------------------------------------------------
		/*
		 * Make INTERPRET_LOGS ActionSPUDD
		 */
		
		ActionSPUDD dropPrivsSPUDD = 
				spuddFactory.getPrefixedActSPUDDWithOppTransitions("DROP_PRIVS");
		
		DDTree sessPrivsDropPrivsDDNoClash = 
				this.ddmaker.getDDTreeFromSequence(
						new String[] {"SESSION_PRIVS",
									  "SESSION_PRIVS'"}, 
						new String[][] {
							{"user", "user", "1.0"},
							{"admin", "user", "0.8"},
							{"admin", "admin", "0.2"},
						});
		
		/*
		 * If opponent does PRIV_ESC exactly when agent does DROP_PRIVS, opponent wins. So all actions
		 * except PRIV_ESC should have the above DD for SESSION_PRIVS state transition
		 */
		System.out.println("Running replace");
		this.replaceStateTransDDForOppAction(
				"SESSION_PRIVS", 
				"NOP", 
				dropPrivsSPUDD, 
				sessPrivsDropPrivsDDNoClash);
		
		System.out.println("Running replace");
		this.replaceStateTransDDForOppAction(
				"SESSION_PRIVS", 
				"VULN_SCAN", 
				dropPrivsSPUDD, 
				sessPrivsDropPrivsDDNoClash);
		
		this.replaceStateTransDDForOppAction(
				"SESSION_PRIVS", 
				"PERM_SCAN", 
				dropPrivsSPUDD, 
				sessPrivsDropPrivsDDNoClash);
		
		this.replaceStateTransDDForOppAction(
				"SESSION_PRIVS", 
				"PERSIST", 
				dropPrivsSPUDD, 
				sessPrivsDropPrivsDDNoClash);
		
		this.replaceStateTransDDForOppAction(
				"SESSION_PRIVS", 
				"FILE_RECON", 
				dropPrivsSPUDD, 
				sessPrivsDropPrivsDDNoClash);
		
		this.replaceStateTransDDForOppAction(
				"SESSION_PRIVS", 
				"EXFIL", 
				dropPrivsSPUDD, 
				sessPrivsDropPrivsDDNoClash);
		
		DDTree obsExecResultDropPrivsDD = 
				this.ddmaker.getDDTreeFromSequence(
						new String[] {"SESSION_PRIVS'",
									  "EXEC_RESULT'"}, 
						new String[][] {
							{"user", "success", "1.0"},
							{"admin", "fail", "1.0"}
						});
		
		DDTree obsPrivsDropPrivsDD = 
				this.ddmaker.getDDTreeFromSequence(
						new String[] {"SESSION_PRIVS'",
									  "PRIVS'"}, 
						new String[][] {
							{"user", "user", "1.0"},
							{"admin", "admin", "0.8"},
							{"admin", "user", "0.2"}
						});
		
		try {
			dropPrivsSPUDD.putDD("DATA_ACCESSED", defaultDataAccessedDD);
			dropPrivsSPUDD.putDD("PRIVS", obsPrivsDropPrivsDD);
			dropPrivsSPUDD.putDD("NET_TX", defaultNetTxDD);
			dropPrivsSPUDD.putDD("EXEC_RESULT", obsExecResultDropPrivsDD);
		}
		
		catch (VariableNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		
		this.actionSPUDDMap.put("DROP_PRIVS", dropPrivsSPUDD);
		
		// ---------------------------------------------------------------------------------------
		
	}
	
	public void makeBeliefsSPUDD() {
		/*
		 * Contains the initial beliefs for the defender.
		 * 
		 * TODO: Reuse BeliefSPUDD objects from the lower level domain
		 */
		
		/*
		 *  HAS_ROOT_VULN
		 */
		DDTree hasRootVulnInit = this.ddmaker.getDDTreeFromSequence(
				new String[] { "HAS_ROOT_VULN" },
				new String[][] { 
					{ "yes", "0.5" }, { "no", "0.5" } });

		// ----------------------------------------------------------------------------

		// ----------------------------------------------------------------------------
		/*
		 *  HAS_C_DATA
		 */
		DDTree hasCDataInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"HAS_C_DATA"},
				new String[][] {
					{"yes", "0.5"},
					{"no", "0.5"} });

		// ----------------------------------------------------------------------------

		// ----------------------------------------------------------------------------
		/*
		 *  HAS_FAKE_DATA
		 */
		DDTree hasFakeDataInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"HAS_FAKE_DATA"},
				new String[][] {
					{"yes", "0.5"},
					{"no", "0.5"} });

		// ----------------------------------------------------------------------------

		// ----------------------------------------------------------------------------
		/*
		 *  SESSION_PRIVS
		 */
		DDTree sessionPrivsInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"SESSION_PRIVS"},
				new String[][] {
					{"user", "1.0"},
					{"admin", "0.0"} });

		// ----------------------------------------------------------------------------
		
		// ----------------------------------------------------------------------------
		/*
		 *  PERSIST_GAINED
		 */
		DDTree persistGainedInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"PERSIST_GAINED"},
				new String[][] {
					{"none", "1.0"},
					{"user", "0.0"},
					{"admin", "0.0"}
				});

		// ----------------------------------------------------------------------------
		
		// ----------------------------------------------------------------------------
		/*
		 *  C_DATA_ACCESSED
		 */
		DDTree cDataAccessedInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"C_DATA_ACCESSED"},
				new String[][] {
					{"yes", "0.0"},
					{"no", "1.0"}
				});

		// ----------------------------------------------------------------------------
		
		// ----------------------------------------------------------------------------
		/*
		 *  FAKE_DATA_ACCESSED
		 */
		DDTree fakeDataAccessedInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"FAKE_DATA_ACCESSED"},
				new String[][] {
					{"yes", "0.0"},
					{"no", "1.0"}
				});

		// ----------------------------------------------------------------------------
		
		// ----------------------------------------------------------------------------
		/*
		 *  EXFIL_ONGOING
		 */
		DDTree exfilOngoingInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"EXFIL_ONGOING"},
				new String[][] {
					{"yes", "0.0"},
					{"no", "1.0"}
				});
		
		// ----------------------------------------------------------------------------
		/*
		 *  OPP_OBS
		 */
		DDTree oppObsInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"OPP_OBS"},
				new String[][] {
					{"none", "1.0"}
				});
		
		// ----------------------------------------------------------------------------
		/*
		 *  OPP_POLICY
		 *  
		 *  For opponent's policy, we need to go over the nodeInfoMap and have a belief over
		 *  those nodes which the opponent starts from. 
		 *  
		 *  For a temporary run, I am just giving a 1 probability to the first init belief
		 */
		
		String[][] policyInitTriple = new String[1][2];
		
		String[] nodes = this.nextLevelVarContext.getOppPolicyValNames();
		
		for (int i=0;i < nodes.length;i++) {
			if (this.initNodesMap.get(nodes[i])) {
				policyInitTriple = new String[][] {{nodes[i], "1.0"}};
				break;
			}
		}
		
		DDTree oppPolicyInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"OPP_POLICY"},
				policyInitTriple);

		// ----------------------------------------------------------------------------
		
		BeliefSPUDD bSPUDD = BeliefSPUDDFactory.getBeliefSPUDD(this.nextLevelVarContext,
				
				new String[] {"HAS_ROOT_VULN",
							  "HAS_C_DATA",
							  "HAS_FAKE_DATA",
							  "SESSION_PRIVS",
							  "PERSIST_GAINED",
							  "C_DATA_ACCESSED",
							  "FAKE_DATA_ACCESSED",
							  "EXFIL_ONGOING",
							  "OPP_OBS",
							  "OPP_POLICY"},
				
				new DDTree[] {hasRootVulnInit,
							  hasCDataInit,
							  hasFakeDataInit,
							  sessionPrivsInit,
							  persistGainedInit,
							  cDataAccessedInit,
							  fakeDataAccessedInit,
							  exfilOngoingInit,
							  oppObsInit,
							  oppPolicyInit});
		
		this.beliefSPUDD = bSPUDD;
		
	}

}
