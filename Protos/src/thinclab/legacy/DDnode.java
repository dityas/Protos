package thinclab.legacy;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import thinclab.utils.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.IntStream;

public class DDnode extends DD {

    private static final long serialVersionUID = 8410391598527547020L;

    private int numLeaves;
    private DD children[];

    private final int hash;
    private final TreeSet<Integer> varSet;

    private static final Logger LOGGER = LogManager.getLogger(DDnode.class);

    private DDnode(int var, DD children[]) {

        this.X = var;
        this.children = children;
        this.numLeaves = 0; // lazy temporary value

        this.hash = this.computeHash();
        this.varSet = this.getVarSet(); 
    }

    private int computeHash() {
        /*
         * Precomputes the hash code to avoid repeated computations and save time.
         * 
         * This could be dangerous if the object attributes are changed in between
         */

        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(this.X);

        for (int i = 0; i < children.length; i++) {

            /* check for null children, if this happens, something is wrong */
            if (this.children[i] != null)
                builder.append(this.children[i].hashCode());

            else 
                LOGGER.error("Null child at " + i + " something might be seriously wrong.");
            
        }

        return builder.toHashCode();
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
        //WeakReference<DD> storedNode = ((WeakReference<DD>)
        //        Global.nodeHashtable.get(node));

        //if (storedNode != null && storedNode.get() != null)
        //    return (DDnode) storedNode.get();

        //// store node in nodeHashtable
        //Global.nodeHashtable.put(node, new WeakReference<DD>(node));
        return node;
    }

    public static DD getDistribution(int var, List<Tuple<String, Float>> probs) {

        /*
         * For making DDs when all children are not defined.
         */
        var childNames = Global.valNames.get(var - 1);
        DD[] children = new DD[childNames.size()];
        for (int i = 0; i < children.length; i++)
            children[i] = DDleaf.getDD(0.0f);

        probs.stream().forEach(i ->
                {

                    int index = Collections.binarySearch(childNames, i._0());
                    if (index < 0) {

                        LOGGER.error(
                                String.format("Child %s does not exist in var %s: %s", i, var, Global.varNames.get(var)));
                        System.exit(-1);
                    }

                    else
                        children[index] = DDleaf.getDD(i._1());
                });

        return DDnode.getDD(var, children);
    }

    public void setChild(int child, DD childDD) {

        if (child >= children.length || child <= 0) {

            LOGGER.error(
                    String.format("Child %s does not exist for variable values", child, Global.valNames.get(X - 1)));
            System.exit(-1);
        }

        children[child - 1] = childDD;

    }

    @Override
    public boolean equals(Object obj) {

        //if (!(obj instanceof DDnode))
        //    return false;

        //DDnode node = (DDnode) obj;

        //if (var != node.var)
        //    return false;

        //for (int i = 0; i < children.length; i++) {

        //    if (!children[i].equals(node.children[i]))
        //        return false;
        //}

        //return true;

        if (obj == this)
            return true;
        
        else if (obj instanceof DDnode node) {

            if (X != node.X)
                return false;

            else {
                for (int i = 0; i < children.length; i++) {

                    if (!children[i].equals(node.children[i]))
                        return false;
                }
            }

            return true;
        }

        else
            return false;
    }

    @Override
    public int hashCode() {

        return hash;
    }

    public DD store() {

        DD[] children = new DD[this.children.length];

        for (int i = 0; i < this.children.length; i++) {

            children[i] = this.children[i].store();
        }

        return DDnode.getDD(X, children);
    }

    public DD[] getChildren() {

        return children;
    }

    public int getNumLeaves() {

        if (numLeaves == 0) {

            for (int i = 0; i < children.length; i++) {

                numLeaves = numLeaves + children[i].getNumLeaves();
            }
        }

        return numLeaves;
    }

    // --------------------------------------------------------------------

    @Override
    public String toString() {

        return this.toSPUDD();
    }

    @Override
    public String toSPUDD(int spaces) {
        /*
         * Returns tree as SPUDD string
         */

        var childNames = Global.valNames.get(this.X - 1);

        StringBuilder builder = new StringBuilder(100);
        builder.append("  ".repeat(spaces)).append("(").append(Global.varNames.get(this.X - 1));

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
    public String toDot() {

        var builder = new StringBuilder();

        builder.append(this.hashCode()).append(" [label=\"");
        builder.append(this.toLabel()).append("\"];");

        return builder.toString();
    }

    @Override
    public JsonElement toJson() {

        var _json = new JsonObject();
        _json.add("name", new JsonPrimitive(Global.varNames.get(X - 1)));

        var _array = new JsonObject();

        for (int i = 0; i < children.length; i++) 
            _array.add(
                    Global.valNames.get(X - 1).get(i), 
                    children[i].toJson());

        _json.add("value", _array);

        return _json;
    }

    private TreeSet<Integer> getVarSet() {

        var varSet = new TreeSet<Integer>();
        varSet.add(X);

        for (int i = 0; i < this.children.length; i++) {

            if (children[i] instanceof DDnode)
                varSet.addAll(children[i].getVars());
        }

        return varSet;
    }

    @Override
    public TreeSet<Integer> getVars() {

        return varSet;
    }

    @Override
    public String toLabel() {

        var builder = new StringBuilder();

        builder.append("(").append(Global.varNames.get(this.X - 1));

        for (int i = 0; i < this.children.length; i++) {

            if (this.children[i].equals(DD.zero))
                continue;

            if (this.children[i] instanceof DDleaf)
                builder.append(" ( ").append(Global.valNames.get(this.X - 1).get(i)).append("  ")
                    .append(this.children[i].toLabel()).append(" ) ");

            else {
                builder.append(" ( ").append("<DD for ")
                    .append(Global.varNames.get(children[i].X - 1)).append("> ) ");
            }
        }

        builder.append(" )");
        return builder.toString();
    }

    @Override
    public float getVal() {

        return 0;
    }

    @Override
    public Object toLisp() {
        
        var objList = new ArrayList<Object>();
        objList.add(Global.varNames.get(this.X - 1));

        for (int i = 0; i < children.length; i++) {
            var childList = new ArrayList<Object>();
            childList.add(Global.valNames.get(this.X).get(i));
            childList.add(children[i].toLisp());

            objList.add(childList);
        }

        return objList;
    }

}
