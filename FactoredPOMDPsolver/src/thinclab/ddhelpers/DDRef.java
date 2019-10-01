package thinclab.ddhelpers;

public class DDRef extends DDTree {
	
	public DDRef(String refName) {
		super(refName);
	}
	
	@Override
	public String toSPUDD(String prefix) {
		return " (" + this.varName + ")";
	}
	
	@Override
	public DDRef getCopy() {
		return new DDRef(this.varName);
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
