package thinclab.domainMaker;

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
}
