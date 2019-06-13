package thinclab.domainMaker.SPUDDHelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class NextLevelVariablesContext extends VariablesContext {
	
	public String oppPolicyName = "OPP_POLICY";
	public String[] oppPolicyValNames;
	
	public String[] oppObsNames;
	public String[][] oppObsValNames;
	
	public VariablesContext lowerContext;
	
	/*
	 * Handles all variable naming and assignments for higher level domain
	 */

	public NextLevelVariablesContext(VariablesContext lowerContext,
			String[] obsNames,
			String[][] obsValNames,
			String[] oppPolicyValNames,
			String[] oppObsNames,
			String[][] oppObsValNames) {
		
		super();
		
		this.lowerContext = lowerContext;
		this.varNames = this.lowerContext.getVarNames();
		this.varValNames = this.lowerContext.getVarValNames();
		this.obsNames = obsNames;
		this.obsValNames = obsValNames;
		this.oppPolicyValNames = oppPolicyValNames;
		
		for (int i = 0; i < oppObsNames.length; i++) {
			oppObsNames[i] = "OPP_" + oppObsNames[i];
		}
		
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
		
		for (int i = 0; i < this.oppObsNames.length; i++) {
			this.varNameSet.add(this.oppObsNames[i]);
		}
		
		this.varNameSet.add(this.oppPolicyName);
//		System.out.println(this.varNameSet.toString());
	} // public VariablesContext

	/**
	 * @return the varNames
	 */
	public String[] getStateVarNames() {
		List<String> varNamesList = new ArrayList<String>();
		varNamesList.addAll(Arrays.asList(this.varNames));
		varNamesList.addAll(Arrays.asList(this.oppObsNames));
		varNamesList.add(this.oppPolicyName);
		return varNamesList.toArray(new String[varNamesList.size()]);
	}
	
	@Override
	public String[] getVarNames() {
		return this.varNames;
	}

	/**
	 * @return the varValNames
	 */
	@Override
	public String[][] getVarValNames() {
		return varValNames;
	}

	/**
	 * @return the obsNames
	 */
	@Override
	public String[] getObsNames() {
		return obsNames;
	}

	/**
	 * @return the obsValNames
	 */
	@Override
	public String[][] getObsValNames() {
		return obsValNames;
	}

	/**
	 * @return the oppPolicyName
	 */
	public String getOppPolicyName() {
		return oppPolicyName;
	}

	/**
	 * @return the oppPolicyValNames
	 */
	public String[] getOppPolicyValNames() {
		return oppPolicyValNames;
	}

	/**
	 * @return the oppObsNames
	 */
	public String[] getOppObsNames() {
		return oppObsNames;
	}

	/**
	 * @return the oppObsValNames
	 */
	public String[][] getOppObsValNames() {
		return oppObsValNames;
	}
	
//	public boolean hasVariable(String varName) {
//		return this.varNameSet.contains(varName);
//	}
	
	public String getOppObsDDName(String obsName) {
		return "opp" + obsName.toLowerCase();
	}
	
}
