package thinclab.domainMaker.SPUDDHelpers;

import java.util.HashSet;

public class VariablesContext {
	/*
	 * Just a container for state and observation variables and their values
	 */
	public String[] varNames;
	public String[][] varValNames;
	public String[] obsNames;
	public String[][] obsValNames;

	public HashSet<String> varNameSet = new HashSet<String>();
	
	/*
	 * Contains the following fields
	 * varNames - Names of state variables
	 * varValNames - values of state variables
	 * obsNames - Names of observation variables
	 * obsValNames - Values taken by observation variables
	 */
	
	public VariablesContext() {
		
	}

	public VariablesContext(String[] varNames, String[][] varValNames, String[] obsNames, String[][] obsValNames) {
		this.varNames = varNames;
		this.varValNames = varValNames;
		this.obsNames = obsNames;
		this.obsValNames = obsValNames;

		// populate state var keys
		for (int i = 0; i < this.varNames.length; i++) {
			this.varNameSet.add(this.varNames[i]);
		}

		// populate obs var keys
		for (int i = 0; i < this.obsNames.length; i++) {
			this.varNameSet.add(this.obsNames[i]);
		}
	} // public VariablesContext
	
	// ---------------------------------------------------------
	
	public boolean hasVariable(String varName) {
		return this.varNameSet.contains(varName);
	}
	
	// ---------------------------------------------------------
	// Getters
	
	public String[] getVarNames() {
		return this.varNames;
	}

	/**
	 * @return the varValNames
	 */
	public String[][] getVarValNames() {
		return varValNames;
	}

	/**
	 * @return the obsNames
	 */
	public String[] getObsNames() {
		return obsNames.clone();
	}

	/**
	 * @return the obsValNames
	 */
	public String[][] getObsValNames() {
		return obsValNames;
	}

	/**
	 * @return the varNameSet
	 */
	public HashSet<String> getVarNameSet() {
		return varNameSet;
	}
	
	public String[] getStateVarNames() {
		return this.varNames;
	}

}
