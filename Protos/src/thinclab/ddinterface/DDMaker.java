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
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DDMaker implements Serializable {
	
	/*
	 * Helper class for making DDs from variable sequences and triples and all that
	 */
	
	private static final long serialVersionUID = -3426937338090541871L;

	private static final Logger logger = LogManager.getLogger(DDMaker.class);
	
	private HashMap<String, String[]> variablesHashMap = new HashMap<String, String[]>();
	private boolean primed = false;
	
	// --------------------------------------------------------------------------------------
	
	public DDMaker() {
		
	}
	
	// ----------------------------------------------------------------------------
	/*
	 * Methods for variables addition
	 */
	
	public void addVariable(String varName, String[] children) {
		/*
		 * Stores the state or observation variable info.
		 */
		if (this.variablesHashMap.containsKey(varName)) {
			logger.warn(varName 
					+ "already exists. Won't be added again.");
		}
		
		if (this.primed) {
			logger.error(
					"Variables have already been primed. Restart and add all variables first");			
		}
		
		else this.variablesHashMap.put(varName, children);
		
	} // public void addVariable
	
	public void primeVariables() {
		/*
		 * Creates new records for primes of already known variables
		 */
		HashMap<String, String[]> primeHashMap = 
				new HashMap<String, String[]>();
		Iterator<Entry<String, String[]>> varMapIterator = 
				this.variablesHashMap.entrySet().iterator();
		
		while (varMapIterator.hasNext()) {
			
			Entry<String, String[]> entry = varMapIterator.next();
			primeHashMap.put(entry.getKey() + "'", entry.getValue());
			
		}
		
		/* add primeHashMap entries to variable hashMap */
		this.variablesHashMap.putAll(primeHashMap);
		this.primed = true;
	}
	
	public void addAllAndPrime(String[] varNames,
			String[][] varValNames,
			String[] obsNames,
			String[][] obsValNames) {
		/*
		 * Adds all variables at once and primes them
		 */
		
		for (int i=0; i < varNames.length; i++) {
			this.addVariable(varNames[i], varValNames[i]);
		}
		
		for (int o=0; o < obsNames.length; o++) {
			this.addVariable(obsNames[o], obsValNames[o]);
		}
		
		this.primeVariables();
		
	} // public void addAll
	
	public void clearContext() {
		/* Clears all current variables */
		logger.warn("Clearing current context!");
		
		this.variablesHashMap.clear();
		this.primed = false;
	}
	
	// -----------------------------------------------------------------------------
	
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
				logger.error(varName + " not in known set of variables");
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
		
		/* for all records */
		for (int record=0; record < values.length; record ++) {
			
			boolean star = false;
			String[] currentRecord = values[record];
			DDTree currentNode = topTreeRef;
			
			/* for each child value */
			for (int c=0; c < currentRecord.length-2; c++) {

				if (currentRecord[c] == "*") {
				
					String[] children = this.variablesHashMap.get(varSequence[c]);
					
					for (int i=0; i<children.length;i++) {
						String[] copyRecord = currentRecord.clone();
						copyRecord[c] = children[i];
						ArrayList<String[]> valuesList = new ArrayList<String[]>(Arrays.asList(values));
						valuesList.add(copyRecord);
						values = valuesList.toArray(new String[valuesList.size()][]);
					}
					star = true;
					break;
					
				}
			
				try {
//					logger.debug("Record " + record + " is " + Arrays.toString(currentRecord));
					currentNode = currentNode.atChild(currentRecord[c]);
				}
				catch (Exception e) {
					e.printStackTrace();
					System.exit(-1);
				}
			} // for (int c=0; c < currentRecord.length; c++)
			
			if (!star) {
				
				try {
					currentNode.setValueAt(currentRecord[currentRecord.length-2],
									   	   Double.valueOf(currentRecord[currentRecord.length-1]));
				}
				
				catch (Exception e) {
					logger.error("While setting value at " + currentRecord[currentRecord.length-2]
							+ " " + e.getMessage());
					System.exit(-1);
				}
			}
			
		} // for (int record=0; record < values.length; record ++)
		
		return topTreeRef;
	} // public DDTree getDDTreeFromSequence
	
	public DDTree appendDDtoEnd(DDTree parent,
								String[][] childSequence,
								DDTree ddToAppend) {
		/*
		 * Adds given DD symmetrically to all children of the param
		 */
		DDTree topRef = parent;
		DDTree currNode = topRef;
		for (int s=0; s < childSequence.length; s++) {
			
			currNode = topRef;
			String[] seq = childSequence[s];

			for (int c=0; c < seq.length - 1; c++) {
				
				try {
					currNode = currNode.atChild(seq[c]);
				} 
				
				catch (Exception e) {
					logger.error("Error while visiting child " + seq[c]);
					e.printStackTrace();
					System.exit(-1);
				}
				
			}
			
			try {
				currNode.setDDAt(seq[seq.length - 1], ddToAppend);
			} 
			
			catch (Exception e) {
				logger.error("Error while setting DD");
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return parent;
	}
	
	// --------------------------------------------------------------------------------------------
	
	public HashMap<String, String[]> getVariablesMap() {
		return this.variablesHashMap;
	}

}
