package thinclab.legacy;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import thinclab.ddinterface.DDTree;
import thinclab.ddinterface.DDTreeLeaf;

import java.lang.ref.*;
import java.util.Set;
import java.util.TreeSet;
import java.io.*;

public class DDleaf extends DD {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2608205879751348514L;
	private float val;
	private int[][] config;

	/* precomputed hash, this may be a bad idea */
	private int hash;

	private DDleaf(float val) {
		this.val = val;
		this.var = 0;
		this.config = null;

		this.precomputeHash();
	}

	private DDleaf(float val, int[][] config) {
		this.val = val;
		this.var = 0;
		this.config = config;

		this.precomputeHash();
	}

	private void precomputeHash() {
		/*
		 * Precomputes the hash code to avoid repeated computations and save time.
		 * 
		 * This could be dangerous if the object attributes are changed in between
		 */

		this.hash = new HashCodeBuilder().append(this.val).append(Config.hashCode(this.config)).toHashCode();
	}

	public static DD getDD(float val) {

		// create new leaf
		DDleaf leaf = new DDleaf(val);

		// try to lookup leaf in leafHashtable
		WeakReference<DD> weakReference = (WeakReference<DD>) Global.leafHashtable.get(leaf);
		WeakReference<DD> storedLeaf = weakReference;
		if (storedLeaf != null)
			return (DDleaf) storedLeaf.get();

		// store leaf in leafHashtable
		Global.leafHashtable.put(leaf, new WeakReference<DD>(leaf));
		return leaf;
	}

	public static DD getDD(float val, int[][] config) {

		// create new leaf
		DDleaf leaf = new DDleaf(val, config);

		// try to lookup leaf in leafHashtable
		WeakReference<DD> storedLeaf = (WeakReference<DD>) Global.leafHashtable.get(leaf);
		if (storedLeaf != null)
			return (DDleaf) storedLeaf.get();

		// store leaf in leafHashtable
		Global.leafHashtable.put(leaf, new WeakReference<DD>(leaf));
		return leaf;
	}

	// public SortedSet getScope() {
	// return new TreeSet();
	// }

	public int[] getVarSet() {
		return new int[0];
	}

	public float getSum() {
		return val;
	}

	public float getVal() {
		return val;
	}

	public int[][] getConfig() {
		return config;
	}

	public int getNumLeaves() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj.getClass() != getClass())
			return false;

		DDleaf leaf = (DDleaf) obj;

		if (val == leaf.val && Config.equals(config, leaf.config))
			return true;

		else
			return false;
	}

	@Override
	public int hashCode() {
//		Double valD = new Double(val);
//		return valD.hashCode() + Config.hashCode(config);

		return this.hash;
	}

	public DD store() {
		return DDleaf.getDD(val, config);
	}

	public void display(String space) {
		System.out.println(space + "leaf: " + Double.toString(val) + "  " + Config.toString(config));
	}

	public void display(String space, String prefix) {
		System.out.println(space + prefix + Double.toString(val) + "  " + Config.toString(config));
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

	public DDTree toDDTree() {
		/*
		 * Return DDTree leaf of current value
		 */
		return new DDTreeLeaf(this.val);
	}
	
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
		builder.append("  ".repeat(spaces)).append("(").append(this.val)
			.append(")");
		
		return builder.toString();
	}

	@Override
	public TreeSet<Integer> getVars() {

		// TODO Auto-generated method stub
		return new TreeSet<Integer>();
	}
}
