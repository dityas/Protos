package thinclab.domainMaker.SPUDDHelpers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Iterator;

import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.domainMaker.ddHelpers.SameDDTree;
import thinclab.exceptions.VariableNotFoundException;

public class ActionSPUDD {

	public String actionName;
	public VariablesContext varContext;
//	public NextLevelVariablesContext nextLevelVarContext;
	public double cost;
	public DDTree costDD;

	public String newLine = "\r\n";

	public HashMap<String, DDTree> varToDDMap = new HashMap<String, DDTree>();

	public ActionSPUDD(String actionName, VariablesContext varContext, double cost) {

		this.actionName = actionName;
		this.varContext = varContext;
		this.cost = cost;

		// populate DD map
		Iterator<String> varIter = this.varContext.varNameSet.iterator();
		while (varIter.hasNext()) {
			this.varToDDMap.put(varIter.next(), null);
		}
	}

	public ActionSPUDD(String actionName, NextLevelVariablesContext varContext) {

		this.actionName = actionName;
		this.varContext = varContext;

		// populate DD map
//		System.out.println(this.varContext.varNameSet.toString());
		Iterator<String> varIter = this.varContext.varNameSet.iterator();
		while (varIter.hasNext()) {
			this.varToDDMap.put(varIter.next(), null);
		}
	}

	// ------------------------------------------------------------------------------
	// Methods to edit DD mappings

	public void putDD(String varName, DDTree dd) throws VariableNotFoundException {
		/*
		 * Map given DD to the variable name
		 */
//		System.out.println(this.varContext.varNameSet.toString());
		if (this.varContext.hasVariable(varName)) {
			this.varToDDMap.put(varName, dd);
		}

		else {
			throw new VariableNotFoundException(varName + " unknown");
		}
	}
	
	public void overwriteDDForNode(
			String varName,
			String policyNode,
			DDTree ddToReplace) {
		/*
		 * Replaces DD for state var varName at policyNode child with ddToReplace
		 * 
		 * Used mostly with higher level ActionSPUDD objects in the even of clashes between opponent actions
		 * and agent actions
		 * 
		 * For instance if agent's action changes the state var only for certain opponent actions, then this
		 * method can be used to make such an ActionSPUDD 
		 */
		
		try {
			this.varToDDMap.get(varName).setDDAt(policyNode, ddToReplace.getCopy());
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	public void fillNullDDs() {
		/*
		 * Applies SAMEdd to all variables which do not have already specified DDs
		 */

		Iterator<Entry<String, DDTree>> ddMapIter = this.varToDDMap.entrySet().iterator();
		while (ddMapIter.hasNext()) {
			Entry<String, DDTree> entry = ddMapIter.next();

			if (entry.getValue() == null) {
				this.varToDDMap.put(entry.getKey(), new SameDDTree(entry.getKey()));
			}

		} // while (ddMapIter.hasNext())
	} // public void fillNullDDs
	
	public void setCostDD(DDTree costDD) {
		/*
		 * Used to set cost DD for the action in case float costs are not enough
		 */
		this.costDD = costDD;
	}

	// -----------------------------------------------------------------------------------

	// -----------------------------------------------------------------------------------
	// Methods to write SPUDD strings

	public String toSPUDD() {
		/*
		 * Returns SPUDD representation of the action DD
		 */
		String SPUDD = "";
		SPUDD += this.newLine;
		SPUDD += "action" + " " + this.actionName + this.newLine;

		// Write states
		String[] stateVars = this.varContext.getStateVarNames();
		for (int s = 0; s < stateVars.length; s++) {
			String state = stateVars[s];
			SPUDD += state + "\t" + this.varToDDMap.get(state).toSPUDD() + this.newLine;
		}

		SPUDD += "observe" + this.newLine;

		// Write observations
		for (int o = 0; o < this.varContext.obsNames.length; o++) {
			String obs = this.varContext.obsNames[o];
			SPUDD += obs + "\t" + this.varToDDMap.get(obs).toSPUDD() + this.newLine;
		}

		SPUDD += "endobserve" + this.newLine;
		
		if (this.costDD == null) {
			if (this.cost > 0.0) {
				SPUDD += "cost (" + this.cost + ")" + this.newLine;
			}
		}
		
		else if (this.costDD != null) {
			SPUDD += "cost " + this.costDD.toSPUDD() + this.newLine;
		}

		SPUDD += "endaction" + this.newLine;
		return SPUDD;
	}

	// -----------------------------------------------------------------------------------

	// Getters for state and obs DDs

	public HashMap<String, DDTree> getObsVarToDDMap() {
		HashMap<String, DDTree> obsVarDDs = new HashMap<String, DDTree>();
		String[] obsNames = this.varContext.getObsNames();

		for (int i = 0; i < obsNames.length; i++) {
			obsVarDDs.put(obsNames[i], this.varToDDMap.get(obsNames[i]));
		}

		return obsVarDDs;
	}
	
	public DDTree getDDForVar(String varName) throws VariableNotFoundException {
		/*
		 * Returns a copy of the DD corresponding to the given variable name 
		 */
		if (this.varToDDMap.containsKey(varName)) {
			return this.varToDDMap.get(varName).getCopy();
		}
		
		else {
			throw new VariableNotFoundException("Unknown variable " + varName);
		}
	}

	// -----------------------------------------------------------------------------------
}
