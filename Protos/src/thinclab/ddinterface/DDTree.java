/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ddinterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.legacy.DD;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;

public class DDTree implements Serializable {
	
	private static final long serialVersionUID = -60184357181539883L;
	
	public String varName = "UnnamedVar";
	public HashMap<String, DDTree> children = new HashMap<String, DDTree>();
	
	// --------------------------------------------------------------------------------------
	
	public DDTree(String varName) {
		/*
		 * Constructor to set varName
		 */
		this.varName = varName;
	}
	
	public DDTree(String varName, HashMap<String, DDTree> children) {
		
		this.varName = varName;
		this.children = children;
	}
	
	// --------------------------------------------------------------------------------------
	
	public void addChild(String childName, DDTree childDD) {
		/*
		 * Create child for the current DD
		 * 
		 * TODO: error checking here.
		 */
		this.children.put(childName, childDD);
	}
	
	// --------------------------------------------------------------------------------------
	
	public String toSPUDD(String prefix) {
		/*
		 * Returns tree as SPUDD string
		 */
		String spuddOut = prefix + "(" + varName + "\r\n";
		
		for (Entry<String, DDTree> entry : children.entrySet()) {
			spuddOut += prefix + "\t(" + entry.getKey() + "\r\n" + prefix
					+ entry.getValue().toSPUDD(prefix + "\t") + ")\r\n";
		}
		
		spuddOut +=  prefix + ")";
		
		return spuddOut;
	}
	
	public String toSPUDD() {
		return this.toSPUDD("");
	}
	
	public DDTree getCopy() {
		/*
		 * Used for reusing same DD trees. Just another implementation of deep copy
		 */
		/* Copy variable */ 
		DDTree copyTree = new DDTree(this.varName);
		
		/* Copy children */
		for (Entry<String, DDTree> entry : children.entrySet())
			copyTree.children.put(entry.getKey(), entry.getValue().getCopy());
		
		return copyTree;
	}
	
	// --------------------------------------------------------------------------------
	
	public void renameVar(String oldVar, String newVar) {
		/*
		 * Renames all nodes with varName oldVar to newVar
		 */
		if (this.varName.contentEquals(oldVar)) this.varName = newVar;
		
		else {
			for (Entry<String, DDTree> child : this.children.entrySet())
				child.getValue().renameVar(oldVar, newVar);
		}
	}
	
	// --------------------------------------------------------------------------------
	/*
	 * Overrides for proper hashing
	 */
	
	@Override
	public boolean equals(Object obj) {
		/*
		 * Checks if two DD trees are equal
		 */
		if (obj.getClass() != this.getClass()) return false;
		
		DDTree other = (DDTree) obj;
		
		if (this.varName != other.varName) return false;
		if (this.children.size() != other.children.size()) return false;
		
		for (Entry<String, DDTree> entry: children.entrySet()){
			
			DDTree otherSubTree = other.children.get(entry.getKey());
			
			if (otherSubTree == null) return false;
			else if (!entry.getValue().equals(otherSubTree)) return false;
		}
		
		return true;
	}
	
	@Override
    public int hashCode() {
		
		try {
			int hashCode = 0;
			
			for (String child : this.children.keySet()) {
			    hashCode += this.children.get(child).hashCode();
			}
			
			return hashCode + DecisionProcess.getVarIndex(this.varName);
		}
		
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
			
			return 0;
		}
    }
	
	// -----------------------------------------------------------------------------------
	
	public DDTree atChild(String childName) throws Exception {
		/*
		 * Returns reference to subtree below given child.
		 */
		if (this.children.containsKey(childName)) return this.children.get(childName);
		
		/* Child not found */
		else throw new Exception(this.varName + " does not contain child " + childName);
	}
	
	public void setValueAt(String childName, float val) throws Exception {
		/*
		 * Sets the value of the leaf at childName to param val
		 */
		if (this.children.containsKey(childName)) {
			
			if (this.children.get(childName) instanceof DDTreeLeaf) 
				this.children.put(childName, new DDTreeLeaf(val));
			
			else throw new Exception(childName + " for " + varName + " is not a leaf.");
		}
		
		else throw new Exception(varName + " does not contain child " + childName);
	}
	
	public void setDDAt(String childName, DDTree ddToAppend) throws Exception {
		/*
		 * Sets the value of the leaf at childName to param val
		 */
		if (this.children.containsKey(childName)) 
			this.children.put(childName, ddToAppend/*.getCopy()*/);

		else throw new Exception(varName + " does not contain child " + childName);
	}
	
	public void setDDAt(List<String> childNames, DDTree ddToAppend) throws Exception {
		/*
		 * Recursively traverses childNames and sets ddToAppend at the last child
		 */
		
		List<String> childNamesList = new ArrayList<String>(childNames);
		String child = childNamesList.remove(0);
		
		if (childNamesList.size() == 0) this.setDDAt(child, ddToAppend);
		
		else this.children.get(child).setDDAt(childNamesList, ddToAppend);
	}
	
	// ---------------------------------------------------------------------------------
	
	public DD toDD() {
		/*
		 * Convert the DDTree to an actual DD as defined in symbolic perseus
		 */
		
		int varIndex = -1;
		
		/* 
		 * Get the variable index.
		 * This is a little hacky. symbolic perseus appends "_P" to the end
		 * of primed variable names. In case of DDTree, primed variables are
		 * represented by "'" at the end. So here, we manually check if the
		 * variable is primed and if it is, look for varName + _P in the
		 * globals.
		 */
		if (this.varName.contains("'"))
			varIndex = 
				Arrays.asList(
					Global.varNames).indexOf(
							this.varName.substring(
									0, 
									this.varName.length() - 1) + "_P");
		
		else varIndex = Arrays.asList(Global.varNames).indexOf(this.varName);
		
		String[] values = Global.valNames[varIndex];
		DD[] children = new DD[this.children.size()];
		
		/*
		 * For each child, recursively convert to DD.
		 */
		for (int c = 0; c < values.length; c++) {
			children[c] = this.children.get(values[c]).toDD();
		}
		
		/*
		 * varIndex should always be incremented by 1 because the global
		 * arrays use Matlab-like indices.
		 */
		return DDnode.myNew(varIndex + 1, children);
	}
	
	// ----------------------------------------------------------------------------------
	
	public List<List<String>> getCPT(List<String> previousVals) {
		/*
		 * Convert the DD into a CPT
		 */
		List<List<String>> cpt = new ArrayList<List<String>>();
		
		for (String child : this.children.keySet()) {
			
			/* Add child */
			List<String> row = new ArrayList<String>(previousVals);
			row.add(child);
			
			cpt.addAll(this.children.get(child).getCPT(row));
		}
		
		return cpt;
	}
	
	public List<List<String>> getCPT() {
		
		return this.getCPT(new ArrayList<String>());
	}
	
	// ----------------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return this.toSPUDD();
	}

} // public class DDTree
