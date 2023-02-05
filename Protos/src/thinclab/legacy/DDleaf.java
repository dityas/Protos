package thinclab.legacy;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.TreeSet;
import java.io.*;
import java.lang.ref.WeakReference;

public class DDleaf extends DD {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2608205879751348514L;
	private float val;

	/* precomputed hash, this may be a bad idea */
	private final int hash;
    private final TreeSet<Integer> varSet = new TreeSet<>();

	private DDleaf(float val) {

		this.val = val;
		this.var = 0;

		this.hash = this.computeHash();
	}
	
	private int computeHash() {
	    return new HashCodeBuilder().append(this.val).toHashCode();
	}

	public static DD getDD(float val) {

		// create new leaf
		DDleaf leaf = new DDleaf(val);

		// try to lookup leaf in leafHashtable
		//WeakReference<DD> weakReference = 
        //    (WeakReference<DD>) Global.leafHashtable.get(leaf);
		//WeakReference<DD> storedLeaf = weakReference;
		//if (storedLeaf != null)
		//	return (DDleaf) storedLeaf.get();

		//// store leaf in leafHashtable
		//Global.leafHashtable.put(leaf, new WeakReference<DD>(leaf));
		return leaf;
	}

	public int[] getVarSet() {

		return new int[0];
	}

	public float getSum() {

		return val;
	}

	public float getVal() {

		return val;
	}
	
	public int getNumLeaves() {

		return 1;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj.getClass() != getClass())
			return false;

		DDleaf leaf = (DDleaf) obj;

		if (val == leaf.val)
			return true;

		else
			return false;
	}

	@Override
	public int hashCode() {
		return this.hash;
	}

	public DD store() {

		return DDleaf.getDD(val);
	}

	public void display(String space) {

		System.out.println(space + "leaf: " + Double.toString(val));
	}

	public void display(String space, String prefix) {

		System.out.println(space + prefix + Double.toString(val));
	}

	public void printSpuddDD(PrintStream ps) {

		ps.print("(" + Double.toString(val) + ")");
	}

	// for printing a single leaf as a diagram
	public void printDotDD(PrintStream ps) {

		ps.println("digraph \"DD\" {");
		ps.println("size = \"7.5,10\"\nratio=0.5;\ncenter = true;\nedge [dir = none];");
		printDotDD(ps, "r");
		ps.println("}");
	}

	public void printDotDD(PrintStream ps, String name) {

		ps.println("{ rank = same; node [shape=box, style=filled, color=goldenrod];\"" + name + "\" [label=\""
				+ Double.toString(val) + "\"];}");
	}

	// -------------------------------------------------------------------------

	@Override
	public String toString() {

		return this.toSPUDD();
	}

	@Override
	public String toSPUDD() {

		return this.toSPUDD(0);
	}

	@Override
	public String toSPUDD(int spaces) {

		var builder = new StringBuilder(10);
		builder.append("  ".repeat(spaces)).append("(").append(this.val).append(")");

		return builder.toString();
	}
	
	@Override
	public String toDot() {
		
		var builder = new StringBuilder();
		builder.append(this.hashCode()).append(" ");
		builder.append(" [label=\"").append(toLabel()).append("\"];\r\n");
		
		return builder.toString();
	}

	@Override
	public TreeSet<Integer> getVars() {

		return this.varSet;
	}

	@Override
	public JsonElement toJson() {

        var _json = new JsonObject();
        _json.add("value", new JsonPrimitive(val));

        return _json;
	}

	@Override
	public String toLabel() {

		return String.format("%.5f", this.val);
	}
}
