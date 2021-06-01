package thinclab.legacy;

import thinclab.ddinterface.DDTree;

import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class DD implements Serializable {

	public static DD one = DDleaf.myNew(1);
	public static DD zero = DDleaf.myNew(0);

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

	public void display() {
		if (getNumLeaves() > 10000)
			System.out.println(
					"Cannot display trees with more than 10,000 leaves (this tree has " + getNumLeaves() + " leaves)");
		else {
			display("");
			System.out.println();
		}
	}

	abstract public void display(String space);

	abstract public void display(String space, String prefix);

	abstract public void printSpuddDD(PrintStream ps);

	abstract public void printDotDD(PrintStream ps);

	abstract public void printDotDD(PrintStream ps, String name);

	abstract public int getNumLeaves();

	// abstract public SortedSet getScope();
	abstract public int[] getVarSet();

	abstract public float getSum();

	abstract public DD store();

	public static DD cast(DDleaf leaf) {
		return (DD) leaf;
	}

	public static DD cast(DDnode node) {
		return (DD) node;
	}

	@Override
	public String toString() {
		/*
		 * Using printSpuddDD to return String instead of printing.
		 * 
		 * Ref: https://stackoverflow.com/questions/1760654/java-printstream-to-string
		 */
		final ByteArrayOutputStream stringOutStream = new ByteArrayOutputStream();

		try (PrintStream ps = new PrintStream(stringOutStream, true, "UTF-8")) {
			this.printSpuddDD(ps);
		}

		catch (UnsupportedEncodingException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		String data = new String(stringOutStream.toByteArray(), StandardCharsets.UTF_8);
		return "DD [] " + data;
	}

	abstract public DDTree toDDTree();
}
