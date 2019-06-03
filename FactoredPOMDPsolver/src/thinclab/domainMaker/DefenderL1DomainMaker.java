package thinclab.domainMaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefenderL1DomainMaker extends DomainMaker {

	DDTree policyDD;
	
	public DefenderL1DomainMaker(DDTree oppPolicy, 
								 String[] policyVarVals,
								 String[] oppObsVars,
								 String[][] oppObsVals) {
		
		this.policyDD = oppPolicy;
		
		List<String> vars = new ArrayList<String>();
		List<String[]> vals = new ArrayList<String[]>();
		
		
		for (int i = 0; i < oppObsVars.length; i++) {
			oppObsVars[i] = "OPP_" + oppObsVars[i];
			vars.add(oppObsVars[i]);
			vals.add(oppObsVals[i]);
			
		}

		// State Variables
		this.variables = 
				new String[] {"HAS_ROOT_VULN",
							  "HAS_C_DATA",
							  "HAS_FAKE_DATA",
							  "SESSION_PRIVS",
							  "PERSIST_GAINED",
							  "C_DATA_ACCESSED",
							  "FAKE_DATA_ACCESSED",
							  "EXFIL_ONGOING",
							  "OPP_POLICY"};
		
		vars.addAll(Arrays.asList(this.variables));
		
		this.variables = vars.toArray(new String[vars.size()]);
		
		this.varValues = 
				new String[][] {{"yes", "no"},
								{"yes", "no"},
								{"yes", "no"},
								{"user", "admin"},
								{"none", "user", "admin"},
								{"yes", "no"},
								{"yes", "no"},
								{"yes", "no"},
								policyVarVals
							   };
							   
		vals.addAll(Arrays.asList(this.varValues));
		
		this.varValues = vals.toArray(new String[vals.size()][]);
						
		this.observations = new String[] {"OBS"};
		
		this.obsValues =
				new String[][] {
									{"yes",
									 "no",
									 "success",
									 "none",
									 "user",
									 "admin"}
							   };
		
		this.addVariablesToDDMaker();
	} // public DefenderL1DomainMaker
	
	@Override
	public void writeinitDef() {
		this.initDef = this.newLine;

	}

	@Override
	public void writeActionsDef() {
		this.actionsDef = this.newLine;
		this.actionsDef += "dd opp_policy_dd" + this.newLine;
		this.actionsDef += this.policyDD.toSPUDD() + this.newLine;
		this.actionsDef += "enddd" + this.newLine;
		
		DDTree attStateTrans = this.ddmaker.getDDTreeFromSequence(
				new String[] {"OPP_POLICY"});
		this.actionsDef += this.newLine;
		this.actionsDef += "dd attacker_state_trans" + this.newLine;
		this.actionsDef += attStateTrans.toSPUDD();
		this.actionsDef += "enddd" + this.newLine;

	}

	@Override
	public void writeRewardDef() {
		this.rewardDef = this.newLine;
	}

}
