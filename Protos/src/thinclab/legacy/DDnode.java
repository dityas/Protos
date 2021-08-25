package thinclab.legacy;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.ddinterface.DDTree;
import java.lang.ref.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.IntStream;

public class DDnode extends DD {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8410391598527547020L;

	private int[] varSet;
	private int numLeaves;
	private DD children[];
	private float sum;

	private int hash;

	private static final Logger LOGGER = LogManager.getLogger(DDnode.class);

	private DDnode(int var, DD children[]) {

		this.var = var;
		this.children = children;
		this.varSet = null; // lazy temporary value
		this.numLeaves = 0; // lazy temporary value
		this.sum = Float.NaN; // lazy temporary value

		this.precomputeHash();
	}

	private void precomputeHash() {
		/*
		 * Precomputes the hash code to avoid repeated computations and save time.
		 * 
		 * This could be dangerous if the object attributes are changed in between
		 */

		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.var);

		for (int i = 0; i < children.length; i++) {

			/* check for null children, if this happens, something is wrong */
			if (this.children[i] != null)
				builder.append(this.children[i].hashCode());

			else {

				LOGGER.error("Null child at " + i + " something might be seriously wrong.");
				LOGGER.error("Error causing DD is " + this.toDDTree());
			}
		}

		this.hash = builder.toHashCode();
	}

	public static DD getDD(List<Integer> vars, List<Integer> vals, float val) {

		if (vars.size() == 0 && vals.size() == 0)
			return DDleaf.getDD(val);

		else {

			var _vars = new ArrayList<>(vars);
			var _vals = new ArrayList<>(vals);

			var _var = _vars.remove(0);
			var _val = _vals.remove(0);

			var childDDs = IntStream.range(0, Global.varDomSize.get(_var - 1))
					.mapToObj(i -> i == (_val - 1) ? DDnode.getDD(_vars, _vals, val) : DDleaf.getDD(0.0f))
					.toArray(DD[]::new);

			// LOGGER.debug(String.format("Childs are %s", Arrays.toString(childDDs)));

			var dd = DDnode.getDD(_var, childDDs);
			return dd;
		}

	}

	public static DD getDDForChild(int var, int child) {

		DD[] childDDs = new DD[Global.varDomSize.get(var - 1)];

		for (int i = 0; i < childDDs.length; i++) {

			if (i == child)
				childDDs[i] = DDleaf.getDD(1.0f);

			else
				childDDs[i] = DDleaf.getDD(0.0f);
		}

		var dd = DDnode.getDD(var, childDDs);

		return dd;
	}

	public static DD getDDForChild(String varName, String childName) {

		int varIndex = Global.varNames.indexOf(varName);

		if (varIndex < 0) {

			LOGGER.error("Variable " + varName + " does not exist.");
			System.exit(-1);
		}

		int var = varIndex + 1;
		int childIndex = Global.valNames.get(varIndex).indexOf(childName);

		if (childIndex < 0) {

			LOGGER.error("Variable " + varName + " does not hold value " + childName);
			System.exit(-1);
		}

		return DDnode.getDDForChild(var, childIndex);
	}

	public static DD getUniformDist(int var) {

		if (var < 1) {

			LOGGER.error("Invalid varName/var while making uniform distribution");
			return null;
		}

		int numVals = Global.varDomSize.get(var - 1);
		float prob = 1.0f / numVals;

		DD[] childDDs = new DD[numVals];

		for (int i = 0; i < numVals; i++) {

			childDDs[i] = DDleaf.getDD(prob);
		}

		return DDnode.getDD(var, childDDs);
	}

	public static DD getUniformDist(String varName) {

		return DDnode.getUniformDist(Global.varNames.indexOf(varName) + 1);
	}

	public static DD getDD(int var, DD[] children) {

		// try to aggregate children
		boolean aggregate = true;
		for (int i = 1; i < children.length; i++) {

			if (!children[0].equals(children[i])) {

				aggregate = false;
				break;
			}
		}

		if (aggregate)
			return children[0];

		// try look up node in nodeHashtable
		DDnode node = new DDnode(var, children);
		/*
		WeakReference<DD> storedNode = ((WeakReference<DD>) Global.nodeHashtable.get(node));
		if (storedNode != null)
			return (DDnode) storedNode.get();
		
		// store node in nodeHashtable
		Global.nodeHashtable.put(node, new WeakReference<DD>(node)); */
		return node;
	}

	public void setChild(int child, DD childDD) {

		if (child >= children.length || child <= 0) {

			LOGGER.error(
					String.format("Child %s does not exist for variable values", child, Global.valNames.get(var - 1)));
			System.exit(-1);
		}

		children[child - 1] = childDD;

	}

	@Override
	public boolean equals(Object obj) {

		if (obj.getClass() != getClass())
			return false;

		DDnode node = (DDnode) obj;

		if (var != node.var)
			return false;

		for (int i = 0; i < children.length; i++) {

			if (!children[i].equals(node.children[i]))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {

		return this.hash;
	}

	public DD store() {

		DD[] children = new DD[this.children.length];

		for (int i = 0; i < this.children.length; i++) {

			children[i] = this.children[i].store();
		}

		return DDnode.getDD(var, children);
	}

	public DD[] getChildren() {

		return children;
	}

	public int[] getVarSet() {

		if (varSet == null) {

			varSet = new int[1];
			varSet[0] = var;

			for (int childId = 0; childId < children.length; childId++) {

				varSet = MySet.unionOrdered(children[childId].getVarSet(), varSet);
			}
		}

		return varSet;
	}

	public int getNumLeaves() {

		if (numLeaves == 0) {

			for (int i = 0; i < children.length; i++) {

				numLeaves = numLeaves + children[i].getNumLeaves();
			}
		}

		return numLeaves;
	}

	public float getSum() {

		if (sum == Double.NaN) {

			sum = 0;

			int[] childrenVars = MySet.remove(this.getVarSet(), var);
			for (int i = 0; i < children.length; i++) {

				int[] remainingVars = MySet.diff(childrenVars, children[i].getVarSet());
				int multiplicativeFactor = 1;
				for (int j = 0; j < remainingVars.length; j++)
					multiplicativeFactor *= Global.varDomSize.get(remainingVars[j] - 1);
				sum = sum + multiplicativeFactor * children[i].getSum();
			}
		}

		return sum;
	}

	// ---------------------------------------------------------------------------------------

	public DDTree toDDTree() {
		/*
		 * Converts the DD to a DDTree representation.
		 */

		DDTree ddTree = null;

		/* Make childless DDTree */
		ddTree = new DDTree(Global.varNames.get(this.var - 1));

		/* Get children and add each to DDTree */
		var valNames = Global.valNames.get(this.var - 1);

		for (int c = 0; c < valNames.size(); c++)
			ddTree.addChild(valNames.get(c), this.children[c].toDDTree());

		/* Return DDTree obj */
		return ddTree;
	}

	@Override
	public String toString() {

		return this.toSPUDD();
	}

	@Override
	public String toSPUDD(int spaces) {
		/*
		 * Returns tree as SPUDD string
		 */

		var childNames = Global.valNames.get(this.var - 1);

		StringBuilder builder = new StringBuilder(100);
		builder.append("  ".repeat(spaces)).append("(").append(Global.varNames.get(this.var - 1));

		for (int i = 0; i < this.children.length; i++) {

			if (this.children[i] instanceof DDleaf) {

				if (children[i].getVal() != 0.0f)
					builder.append("  (").append(childNames.get(i)).append(" ").append(this.children[i].toSPUDD())
							.append(")");
			}

			else
				builder.append("\r\n").append("  ".repeat(spaces + 1)).append("(").append(childNames.get(i))
						.append("\r\n").append(this.children[i].toSPUDD(spaces + 2)).append(")");
		}

		builder.append(")");

		return builder.toString();
	}

	@Override
	public String toSPUDD() {

		return this.toSPUDD(0);
	}

	@Override
	public TreeSet<Integer> getVars() {

		var varSet = new TreeSet<Integer>(Collections.singleton(this.var));
		Arrays.stream(this.children).forEach(c -> varSet.addAll(c.getVars()));

		return varSet;
	}

}