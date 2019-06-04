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
	public VariablesContext varContext;
	public double cost;
	
	public String newLine = "\r\n";
	
	public HashMap<String, DDTree> varToDDMap = new HashMap<String, DDTree>();
	
	public ActionSPUDD(String actionName,
					   VariablesContext varContext,
					   double cost) {
		
		this.actionName = actionName;
		this.varContext = varContext;
		this.cost = cost;
		
		// populate DD map
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
		if (this.varContext.hasVariable(varName)) {
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
		for (int s=0; s < this.varContext.varNames.length; s++) {
			String state = this.varContext.varNames[s];
			SPUDD += state + "\t" + this.varToDDMap.get(state).toSPUDD() + this.newLine;
		}
		
		SPUDD += "observe" + this.newLine;
		
		// Write observations
		for (int o=0; o < this.varContext.obsNames.length; o++) {
			String obs = this.varContext.obsNames[o];
			SPUDD += obs + "\t" + this.varToDDMap.get(obs).toSPUDD() + this.newLine;
		}
		
		SPUDD += "endobserve" + this.newLine;
		
		if (this.cost > 0.0) {
			SPUDD += "cost (" + this.cost + ")" + this.newLine;
		}
		
		SPUDD += "endaction" + this.newLine;
		return SPUDD;
	}
	
	// -----------------------------------------------------------------------------------
}
