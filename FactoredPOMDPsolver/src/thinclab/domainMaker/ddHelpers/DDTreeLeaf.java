package thinclab.domainMaker.ddHelpers;

import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.DDleaf;

public class DDTreeLeaf extends DDTree {
	
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
	
	// ----------------------------------------------------------------------------------
	
	@Override
	public DD toDD() {
		/*
		 * Overrides the DDTree implementation to convert leaves
		 */
		return DDleaf.myNew(this.val);
	}
}
