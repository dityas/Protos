/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */

package thinclab.ddinterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.legacy.Config;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;

public class DDTreeLeaf extends DDTree {
	
	private static final long serialVersionUID = 5543304119075972676L;
	
	public double val = 0.0;
	
	public DDTreeLeaf(double val) {
		super("LeafVar");
		this.val = val;
	}
	
	public String toSPUDD(String prefix) {
		return prefix + "(" + this.val + ")";
	}
	
	public DDTreeLeaf getCopy() {
		/*
		 * Deep copy implementation for leaves
		 */
		return new DDTreeLeaf(this.val);
	}
	
	public boolean equals(DDTreeLeaf other) {
		/*
		 * Checks equality of DDTree leaves
		 */
		if (other.val != this.val) {
			return false;
		}
		
		return true;
	}
	
	// --------------------------------------------------------------------------------
	/*
	 * Overrides for proper hashing
	 */
	
	@Override
	public boolean equals(Object obj) {

		if (obj.getClass() != getClass())
			return false;

		DDTreeLeaf leaf = (DDTreeLeaf) obj;

		if (this.val == leaf.val)
			return true;

		else
			return false;
	}
	
	@Override
	public int hashCode() {
		Double valD = new Double(val);
		return valD.hashCode();
	}
	
	// ----------------------------------------------------------------------------------
	
	@Override
	public List<List<String>> getCPT(List<String> previousVals) {
		
		List<List<String>> cpt = new ArrayList<List<String>>();
		previousVals.add(Double.toString(this.val));
		
		cpt.add(previousVals);
		return cpt;
	}
	
	// ----------------------------------------------------------------------------------
	
	@Override
	public DD toDD() {
		/*
		 * Overrides the DDTree implementation to convert leaves
		 */
		return DDleaf.myNew(this.val);
	}
}
