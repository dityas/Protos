package thinclab.legacy;

import java.io.Serializable;
import java.util.Set;
import thinclab.ddinterface.DDTree;

public abstract class DD implements Serializable {

	public static DD one = DDleaf.getDD(1);
	public static DD zero = DDleaf.getDD(0);

	protected int var;

	private static final long serialVersionUID = 2478730562973454848L;

	public int getVar() {

		return var;
	}

	public int getAddress() {

		return super.hashCode();
	}

	public DD[] getChildren() {

		return null;
	} // should throw exception

	public float getVal() {

		return Float.NEGATIVE_INFINITY;
	} // should throw exception

	public int[][] getConfig() {

		return null;
	} // should throw exception

	abstract public int getNumLeaves();

	// abstract public SortedSet getScope();
	abstract public int[] getVarSet();
	
	abstract public Set<Integer> getVars();

	abstract public float getSum();

	abstract public DD store();

	public static DD cast(DDleaf leaf) {

		return (DD) leaf;
	}

	public static DD cast(DDnode node) {

		return (DD) node;
	}

	abstract public DDTree toDDTree();

	abstract public String toSPUDD();
	abstract public String toSPUDD(int spaces);

	public DD normalize() {

		var norm = OP.addMultVarElim(this, this.getVarSet());
		return OP.div(this, norm);
	}
}
