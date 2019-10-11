package thinclab.legacy;

import java.util.*;

import org.apache.log4j.Logger;

import thinclab.ddhelpers.DDTree;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.frameworks.POMDP;

import java.lang.*;
import java.lang.ref.*;
import java.io.*;

public class DDnode extends DD {
    //private SortedSet scope;
    private int[] varSet;
    private int numLeaves;
    private DD children[];
    private double sum;
    
    private static final Logger logger = Logger.getLogger(DDnode.class);
		
    private DDnode(int var, DD children[]) {
	this.var = var;
	this.children = children;
	this.varSet = null;  // lazy temporary value
	this.numLeaves = 0; // lazy temporary value
	this.sum = Double.NaN; // lazy temporary value
    }

    public static DD myNew(int var, DD[] children) {

	// try to aggregate children
	boolean aggregate = true;
	for (int i=1; i<children.length; i++) {
	    if (children[0] != children[i]) {
		aggregate = false;
		break;
	    }
	}
	if (aggregate) return children[0];

	// try look up node in nodeHashtable
	DDnode node = new DDnode(var,children);
	WeakReference storedNode = (WeakReference)Global.nodeHashtable.get(node);
	if (storedNode != null) return (DDnode)storedNode.get();

	// store node in nodeHashtable
	Global.nodeHashtable.put(node,new WeakReference<DD>(node));
	return node;
    }

    @Override
    public boolean equals(Object obj) {

		if (obj.getClass() != getClass()) return false;

		DDnode node = (DDnode)obj;
	
		if (var != node.var) return false;
		    
		for (int i=0; i<children.length; i++) {
		    if (!children[i].equals(node.children[i])) return false;
		}
							
		return true;
    }

    @Override
    public int hashCode() {
		int hashCode = 0;
		for (int i=0; i<children.length; i++) {
		    hashCode += children[i].hashCode();
		}
		return hashCode + var;
    }

    public DD store() {
	DD[] children = new DD[this.children.length];
	for (int i=0; i<this.children.length; i++) {
	    children[i] = this.children[i].store();
	}
	return DDnode.myNew(var,children);
    }

    public DD[] getChildren() {
	return children;
    }

    public int[] getVarSet() {
	if (varSet == null) {
	    varSet = new int[1];
	    varSet[0] = var;
	    for (int childId=0; childId<children.length; childId++) {
                varSet = MySet.unionOrdered(children[childId].getVarSet(),varSet);
	    }
	}
	return varSet;
    }

    public int getNumLeaves() {
	if (numLeaves == 0) {
	    for (int i=0; i < children.length; i++) {
		numLeaves = numLeaves + children[i].getNumLeaves();
	    }
	}
	return numLeaves;
    }
        
    public double getSum() {
	if (sum == Double.NaN) {
	    sum = 0;
	    int[] childrenVars = MySet.remove(this.getVarSet(),var);
	    for (int i=0; i < children.length; i++) {
		int[] remainingVars = MySet.diff(childrenVars,children[i].getVarSet());
		int multiplicativeFactor = 1;
		for (int j=0; j<remainingVars.length; j++) 
		    multiplicativeFactor *= Global.varDomSize[remainingVars[j]-1];
		sum = sum + multiplicativeFactor * children[i].getSum();
	    }
	}
	return sum;
    }

    public void display(String space) {
	if (Global.varNames == null) {
	    System.out.println(space + "var: " + Integer.toString(var) + "  " + MySet.toString(varSet));
	}
	else {
	    System.out.println(space + Global.varNames[var-1] + "  " + MySet.toString(varSet));
	}

	space = space + "   ";
	if (Global.valNames == null) {
	    for (int i=0; i<children.length; i++) {
		children[i].display(space);
	    }
	}
	else {
	    for (int i=0; i<children.length; i++) {
		children[i].display(space, Global.valNames[var-1][i] + ": ");
	    }
	}
    } 

    public void display(String space, String prefix) {
	if (Global.varNames == null) {
	    System.out.println(space + prefix + "var: " + Integer.toString(var) + "  " + MySet.toString(varSet));
	}
	else {
	    System.out.println(space + prefix + Global.varNames[var-1] + "  " + MySet.toString(varSet));
	}

	space = space + "   ";
	if (Global.valNames == null) {
	    for (int i=0; i<children.length; i++) {
		children[i].display(space);
	    }
	}
	else {
	    for (int i=0; i<children.length; i++) {
		children[i].display(space, Global.valNames[var-1][i] + ": ");
	    }
	}
    } 
    public void printSpuddDD(PrintStream ps) {
	ps.print("(" + Global.varNames[var-1] + "\n");
	
	for (int i=0; i<children.length; i++) {
	    ps.print("  (" + Global.valNames[var-1][i]);
	    children[i].printSpuddDD(ps);
	    ps.print(")");
	}
	ps.print(")\n");
    }

    public void printDotDD(PrintStream ps) {
	ps.println("digraph \"DD\" {");
	ps.println("size = \"7.5,10\"\nratio=0.5;\ncenter = true;\nedge [dir = none];");
	printDotDD(ps,"r");
	ps.println("}");
    }
    
    public void printDotDD(PrintStream ps, String name) {
	
	ps.println("{rank = same; node [shape=ellipse, style=filled, color=cornflowerblue];\""+name+"\" [label=\""+Global.varNames[var-1]+"\"];}");
	String newname;
	for (int i=0; i<children.length; i++) {
	    newname = name+"c"+i;
	    children[i].printDotDD(ps,newname);
	    ps.println("\""+name+"\" -> \""+newname+"\" [label = \""+Global.valNames[var-1][i]+"\"];");
	}
    }
    
    // ---------------------------------------------------------------------------------------
    
    public DDTree toDDTree() {
    	/*
    	 * Converts the DD to a DDTree representation.
    	 */
    	
    	DDTree ddTree = null;
    	
    	try {
    		/* Make childless DDTree */
			ddTree = new DDTree(POMDP.getVarName(this.var));
			
			/* Get children and add each to DDTree */
			String[] valNames = Global.valNames[this.var - 1];
			
			for (int c = 0; c < valNames.length; c++)
				ddTree.addChild(valNames[c], this.children[c].toDDTree());
		} 
    	
    	catch (VariableNotFoundException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
    	
    	/* Return DDTree obj */
		return ddTree;
    }
}