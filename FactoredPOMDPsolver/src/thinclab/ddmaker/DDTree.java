package thinclab.ddmaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DDTree {
	
	public String varName = "UnnamedVar";
	public HashMap<String, DDTree> children = new HashMap<String, DDTree>();
	private static final Logger theLogger = Logger.getLogger("DDTree");
	
	private List<DDTree> nodesVisited = new ArrayList<DDTree>();
	
	public DDTree(String varName) {
		this.varName = varName;
		this.theLogger.setLevel(Level.ALL);
		this.theLogger.setUseParentHandlers(false);
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.INFO);
		this.theLogger.addHandler(consoleHandler);
		this.theLogger.log(Level.FINE, this + "DDTree rooted at variable " + varName + " initialised.");
	}
	
	public String toSPUDD(String prefix) {
		/*
		 * Returns tree as SPUDD string
		 */
		String spuddOut = prefix + "(" + varName + "\r\n";
		
		Iterator<Entry<String, DDTree>> childrenIterator = children.entrySet().iterator();
		while (childrenIterator.hasNext()) {
			Entry<String, DDTree> entry = childrenIterator.next();
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
		// Copy variable
		DDTree copyTree = new DDTree(this.varName);
		
		// Copy children
		Iterator<Entry<String, DDTree>> childrenIterator = children.entrySet().iterator();
		while (childrenIterator.hasNext()) {
			Entry<String, DDTree> entry = childrenIterator.next();
			
			copyTree.children.put(entry.getKey(), entry.getValue().getCopy());
		}
		
		
		return copyTree;
	}
	
	public boolean equals(DDTree other) {
		/*
		 * Checks if two DD trees are equal
		 */
		if (this.varName != other.varName) {
			this.theLogger.log(Level.FINEST, this + " VarNames " + this.varName + ", " + other.varName + " are not equal");
			return false;
		}
		
		if (this.children.size() != other.children.size()) {
			this.theLogger.log(Level.FINEST, this + " No. of children for " + this.varName + " and " + other.varName + " are not equal");
			return false;
		}
		
		Iterator<Entry<String, DDTree>> childrenIterator = children.entrySet().iterator();
		while (childrenIterator.hasNext()) {
			Entry<String, DDTree> entry = childrenIterator.next();
			DDTree otherSubTree = other.children.get(entry.getKey());
			
			if (otherSubTree == null) {
				this.theLogger.log(Level.FINE, this + " Child " + entry.getKey() + " does not exist in " + other);
				return false;
			}
			
			else if (!entry.getValue().equals(otherSubTree)) {
				this.theLogger.log(Level.FINE, this + " Child " + entry.getKey() + " is different in both");
				return false;
			}
			
		}
		
		return true;
	}
	
	public DDTree atChild(String childName) throws Exception {
		/*
		 * Returns reference to subtree below given child.
		 */
		if (this.children.containsKey(childName)) {
			this.theLogger.fine("Visiting " + childName);
			return this.children.get(childName);
		}
		
		// Child not found
		else {
			this.theLogger.warning(this + " does not contain child " + childName);
			throw new Exception(this + " does not contain child " + childName);
		}
	} // public DDTree atChild
	
	public void setValueAt(String childName, double val) throws Exception {
		/*
		 * Sets the value of the leaf at childName to param val
		 */
		if (this.children.containsKey(childName)) {
			if (this.children.get(childName) instanceof DDTreeLeaf) {
				this.theLogger.fine("Changing value of " + childName + " to " + val + " for " + this.varName);
				this.children.put(childName, new DDTreeLeaf(val));
			}
			
			else {
				this.theLogger.severe(childName + " for " + varName + " is not a leaf.");
				throw new Exception(childName + " for " + varName + " is not a leaf.");
			}
		}
		
		else {
			this.theLogger.severe(varName + " does not contain child " + childName);
			throw new Exception(varName + " does not contain child " + childName);
		}
	} // public void setValueAt

} // public class DDTree
