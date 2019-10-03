package thinclab.domainMaker.SPUDDHelpers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import thinclab.ddhelpers.DDTree;
import thinclab.exceptions.VariableNotFoundException;

public class BeliefSPUDD {
	/*
	 * SPUDD helper for init belief and adjunct beliefs
	 */
	
	public VariablesContext varContext;
	public HashMap<String, DDTree> varToDDMap = new HashMap<String, DDTree>();
	public boolean isAdjunct = false;
	public String adjName = "NoName";
	public String newLine = "\r\n";
	
	public BeliefSPUDD(VariablesContext varContext) {
		this.varContext = varContext;
	}
	
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
	
	public void setAdjName(String adjName) {
		this.isAdjunct = true;
		this.adjName = adjName;
	}
	
	// --------------------------------------------------------------------------
	// Make SPUDD string
	
	public String toSPUDD() {
		/*
		 * Returns SPUDD representation of the action DD
		 */
		String SPUDD = "";
		SPUDD += this.newLine;
		
		if (this.isAdjunct) {
			SPUDD += "adjunct" + this.newLine;
			SPUDD += this.adjName + " [*" + this.newLine;
		}
		
		else {
			SPUDD += "init [*" + this.newLine;
		}
		

		Iterator<Entry<String, DDTree>> mapIter = this.varToDDMap.entrySet().iterator();
		while (mapIter.hasNext()) {
			SPUDD += this.newLine;
			SPUDD += mapIter.next().getValue().toSPUDD() + this.newLine;
		}
		
		
		SPUDD += "]" + this.newLine;
		return SPUDD;
	}	
	// -----------------------------------------------------------------------------------
	
	public BeliefSPUDD getCopy() {
		BeliefSPUDD copySPUDD = new BeliefSPUDD(this.varContext);
		
		Iterator<Entry<String, DDTree>> mapIter = this.varToDDMap.entrySet().iterator();
		while (mapIter.hasNext()) {
			Entry<String, DDTree> entry = mapIter.next();
			
			try {
				copySPUDD.putDD(entry.getKey(), entry.getValue().getCopy());
			} 
			catch (VariableNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return copySPUDD;
	} // public BeliefSPUDD getCopy
}
