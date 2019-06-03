package thinclab.domainMaker;

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
	public String[] varNames;
	public String[][] varValNames;
	public String[] obsNames;
	public String[][] obsValNames;
	
	public HashSet<String> varNameSet = new HashSet<String>();
	
	public HashMap<String, DDTree> varToDDMap = new HashMap<String, DDTree>();
	
	public ActionSPUDD(String actionName,
					   String[] varNames,
					   String[][] varValNames,
					   String[] obsNames,
					   String[][] obsValNames) {
		
		this.actionName = actionName;
		this.varNames = varNames;
		this.varValNames = varValNames;
		this.obsNames = obsNames;
		this.obsValNames = obsValNames;
		
		// populate state var keys
		for (int i=0; i < this.varNames.length; i++) {
			this.varToDDMap.put(this.varNames[i], null);
			this.varNameSet.add(this.varNames[i]);
		}
		
		// populate obs var keys
		for (int i=0; i < this.obsNames.length; i++) {
			this.varToDDMap.put(this.obsNames[i], null);
			this.varNameSet.add(this.obsNames[i]);
		}
		
	}
	
	// ------------------------------------------------------------------------------
	// Methods to edit DD mappings
	
	public void putDD(String varName, DDTree dd) throws VariableNotFoundException {
		/*
		 * Map given DD to the variable name
		 */
		if (this.varNameSet.contains(varName)) {
			this.varToDDMap.put(varName, dd);
		}
		
		else {
			throw new VariableNotFoundException(varName + " unknown");
		}
	}
	
	public void fillNullDDs() {
		/*
		 * Applies SAMEdd to all variables which do not have 
		 * already specified DDs
		 */
		
		Iterator<Entry<String, DDTree>> ddMapIter = this.varToDDMap.entrySet().iterator();
		while (ddMapIter.hasNext()) {
			Entry<String, DDTree> entry = ddMapIter.next();
			
			if (entry.getValue() == null) {
				this.varToDDMap.put(entry.getKey(), new SameDDTree(entry.getKey()));
			}
			
		} // while (ddMapIter.hasNext())
	} // public void fillNullDDs
	
	// -----------------------------------------------------------------------------------
}
