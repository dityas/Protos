package thinclab.ddmaker;

import thinclab.ddmaker.DDMaker;

public class AttackerDomainMaker extends DomainMaker {
	/*
	 * Defines L0 domain for attacker
	 */
	private DDMaker ddmaker;
	
	public AttackerDomainMaker() {
		this.ddmaker = new DDMaker();
		
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
		
	}

	@Override
	public void writeinitDef() {

		this.initDef = this.newLine;
	}

	@Override
	public void writeActionsDef() {

		this.actionsDef = this.newLine;
	}

	@Override
	public void writeRewardDef() {

		this.rewardDef = this.newLine;
	}

}
