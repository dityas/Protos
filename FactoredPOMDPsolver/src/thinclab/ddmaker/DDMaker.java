package thinclab.ddmaker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DDMaker {
	/*
	 * Helper class for making DDs from variable sequences and triples and all that
	 */
	private static final Logger theLogger = Logger.getLogger("DDMaker");
	private HashMap<String, String[]> variablesHashMap = new HashMap<String, String[]>();
	private boolean primed = false;
	
	public DDMaker() {
		ConsoleHandler cHandler = new ConsoleHandler();
		cHandler.setLevel(Level.ALL);
		theLogger.setUseParentHandlers(false);
		theLogger.addHandler(cHandler);
	}
	
	public void addVariable(String varName, String[] children) {
		/*
		 * Stores the state or observation variable info.
		 */
		if (this.variablesHashMap.containsKey(varName)) {
			this.theLogger.warning(varName + "already exists. Won't be added again.");
		}
		
		if (this.primed) {
			this.theLogger.severe("Variables have already been primed. Restart and add all variables first");			
		}
		
		else {
			this.variablesHashMap.put(varName, children);
		}
	} // public void addVariable
	
	public void primeVariables() {
		/*
		 * Creates new records for primes of already known variables
		 */
		HashMap<String, String[]> primeHashMap = new HashMap<String, String[]>();
		Iterator<Entry<String, String[]>> varMapIterator = this.variablesHashMap.entrySet().iterator();
		while (varMapIterator.hasNext()) {
			Entry<String, String[]> entry = varMapIterator.next();
			primeHashMap.put(entry.getKey() + "'", entry.getValue());
		}
		
		// add primeHashMap entries to variable hashMap
		this.variablesHashMap.putAll(primeHashMap);
		this.primed = true;
	}
	
	public DDTree getDDTreeFromSequence(String[] varSequence) {
		/*
		 * Makes a uniform DD from the given variable sequence with each leaf zero
		 */
		DDTree previousTree = null;
		DDTree topTree = null;
		
		for (int i = varSequence.length-1; i >= 0 ; i --) {

			String varName = varSequence[i];
			if (this.variablesHashMap.containsKey(varName)) {
				topTree = new DDTree(varName);
				
				String[] children = this.variablesHashMap.get(varName);
				
				// Make children
				for (int c=0; c < children.length; c++) {
					if (previousTree == null) {
						topTree.children.put(children[c], new DDTreeLeaf(0.0));
					}
					
					else {
						topTree.children.put(children[c], previousTree.getCopy());
					}
				} // for (int c=0; c < children.length; c++)
				
				previousTree = topTree;
			}
			
			else {
				this.theLogger.severe(varName + " not in known set of variables");
			}
		} // for (int i = varSequence.length-1; i >= 0 ; i --)
		
		return topTree;
	} // public DDTree getDDTreeFromSequence
	
	public DDTree getDDTreeFromSequence(String[] varSequence, String[][] values) {
		/*
		 * Makes a uniform DD and then changes the leaf values according to param values
		 */
		DDTree defaultTree = this.getDDTreeFromSequence(varSequence);
		DDTree topTreeRef = defaultTree;
		
		// for all records
		for (int record=0; record < values.length; record ++) {
			
			String[] currentRecord = values[record];
			DDTree currentNode = topTreeRef;
			
			// for each child value
			for (int c=0; c < currentRecord.length-2; c++) {
				try {
					currentNode = currentNode.atChild(currentRecord[c]);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			} // for (int c=0; c < currentRecord.length; c++)
			
			try {
				currentNode.setValueAt(currentRecord[currentRecord.length-2],
								   	   new Double(currentRecord[currentRecord.length-1]));
			}
			
			catch (Exception e) {
				e.printStackTrace();
			}
			
		} // for (int record=0; record < values.length; record ++)
		
		return topTreeRef;
	} // public DDTree getDDTreeFromSequence

}
