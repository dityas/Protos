package thinclab.legacy;

import org.apache.commons.lang3.builder.HashCodeBuilder;

class TripletSet {
    private DD dd1;
    private DD dd2;
    private int[] varSet;
    
    private int hash;

    public TripletSet(DD dd1, DD dd2, int[] varSet) {
		this.dd1 = dd1;
		this.dd2 = dd2;
		this.varSet = varSet; // MySet.clone(varSet);
		
		this.precomputeHash();
    }
    
    private void precomputeHash() {
		/*
		 * Compute hash after init to avoid computing it at every call to hashCode
		 */
		
		this.hash = 
				new HashCodeBuilder()
					.append(this.dd1.hashCode())
					.append(this.dd2.hashCode())
					.append(MySet.hashCode(this.varSet))
					.toHashCode();
	}

    public int hashCode() {
//	return dd1.getAddress() + dd2.getAddress() + MySet.hashCode(varSet);
    	return this.hash;
    }

    public boolean equals(Object obj) {
				
	if (obj.getClass() != getClass()) return false;
	TripletSet triplet = (TripletSet)obj;

	if (((dd1 == triplet.dd1 && dd2 == triplet.dd2) || 
	     (dd2 == triplet.dd1 && dd1 == triplet.dd2)) &&
	    MySet.equals(varSet, triplet.varSet))
	    return true;
	else return false;
    }
}
