package thinclab.symbolicperseus;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class DD implements Serializable {
		public static DD one = DDleaf.myNew(1);
		public static DD zero = DDleaf.myNew(0);

		protected int var;

    public int getVar() { return var; }

		public int getAddress() {
				return super.hashCode();
		}

		public DD[] getChildren() { return null; }  // should throw exception
		public double getVal() { return Double.NEGATIVE_INFINITY; }  // should throw exception
		public int[][] getConfig() { return null; }  // should throw exception

    public void display() {
				if (getNumLeaves() > 10000)
						System.out.println("Cannot display trees with more than 10,000 leaves (this tree has " + getNumLeaves() + " leaves)");
				else {
						display(""); 
						System.out.println();
				}
		}
		abstract public void display(String space);
		abstract public void display(String space, String prefix);
    abstract public void printSpuddDD(PrintStream ps);
    abstract public void printDotDD(PrintStream ps);
    abstract public void printDotDD(PrintStream ps,String name);

		abstract public int getNumLeaves();
		//abstract public SortedSet getScope();
		abstract public int[] getVarSet();
        abstract public double getSum();
		abstract public DD store();

		public static DD cast(DDleaf leaf) { return (DD)leaf; }
		public static DD cast(DDnode node) { return (DD)node; }

		@Override
		public String toString() {
			/*
			 * Using printSpuddDD to return String instead of printing.
			 * 
			 * Ref:
			 * 	https://stackoverflow.com/questions/1760654/java-printstream-to-string
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
		
		
//		@Override
//		public boolean equals(Object other) {
//			/*
//			 * Override for proper hashing
//			 */
//			if (!(other instanceof DD)) return false;
//			
//			DD otherDD = (DD) other;
//			if (otherDD == null) return false;
//			
//			if (this.getVar() != otherDD.getVar()) {
//				System.out.println("Var not equal");
//				return false;
//			}
//			
//			if (this.getVal() != otherDD.getVal()) {
//				System.out.println("Val not equal");
//				return false;
//			}
//			
//			if (this.getChildren().length != otherDD.getChildren().length) {
//				System.out.println("Children length not equal");
//				return false;
//			}
//			
//			DD[] children = this.getChildren();
//			for (int i=0; i < children.length; i++) {
//				if (!children[i].equals(otherDD.getChildren()[i])) {
//					System.out.println("Children not equal");
//					return false;
//				}
//			}
//			
//			System.out.println("equal");
//			return true;
//		}
}
