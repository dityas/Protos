/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */

package thinclab.ddhelpers;

public class SameDDTree extends DDTree {
	
	public SameDDTree(String varName) {
		super(varName);
	}
	
	@Override
	public String toSPUDD(String prefix) {
		return " (SAME" + this.varName + ")";
	}
	
	@Override
	public SameDDTree getCopy() {
		return new SameDDTree(this.varName);
	}
	
	public boolean equals(SameDDTree other) {
		if (other.varName == this.varName) {
			return true;
		}
		
		else {
			return false;
		}
	}
}
