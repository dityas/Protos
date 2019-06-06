package thinclab.domainMaker.SPUDDHelpers;

import java.util.HashSet;

public class NextLevelVariablesContext {
	
	public String[] varNames;
	public String[][] varValNames;
	public String[] obsNames;
	public String[][] obsValNames;
	
	public String oppPolicyName = "OPP_POLICY";
	public String[] oppPolicyValNames;
	
	public String[] oppObsNames;
	public String[][] oppObsValNames;

	public HashSet<String> varNameSet = new HashSet<String>();

	public NextLevelVariablesContext(String[] varNames, 
			String[][] varValNames, 
			String[] obsNames, 
			String[][] obsValNames,
			String[] oppPolicyValNames,
			String[] oppObsNames,
			String[][] oppObsValNames) {
		
		this.varNames = varNames;
		this.varValNames = varValNames;
		this.obsNames = obsNames;
		this.obsValNames = obsValNames;
		this.oppPolicyValNames = oppPolicyValNames;
		this.oppObsNames = oppObsNames;
		this.oppObsValNames = oppObsValNames;

		// populate state var keys
		for (int i = 0; i < this.varNames.length; i++) {
			this.varNameSet.add(this.varNames[i]);
		}

		// populate obs var keys
		for (int i = 0; i < this.obsNames.length; i++) {
			this.varNameSet.add(this.obsNames[i]);
		}
	} // public VariablesContext
}
