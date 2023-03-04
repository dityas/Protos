package thinclab.legacy;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.TreeSet;

public class FQDDleaf extends DD {

    /**
     * 
     */
    private final int val;
    public static final int BINS = 255;

    /* precomputed hash, this may be a bad idea */
    private final int hash;
    private final TreeSet<Integer> varSet = new TreeSet<>();

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(FQDDleaf.class);

    private FQDDleaf(int val) {

        this.val = val;
        X = 0;

        hash = new HashCodeBuilder().append(this.val).toHashCode();
    }

    public static FQDDleaf of(DDleaf dd) {

        var val = (int) (BINS * dd.getVal());
        var leaf = Global.qCache.get(val);

        if (leaf != null)
            return leaf;

        else {
            leaf = new FQDDleaf(val);
            Global.qCache.put(val, leaf);
            return leaf;
        }
    }

    public static DD quantize(DD dd) {

        if (dd instanceof FQDDleaf)
            return dd;
        
        // if leaf, quantize and return
        else if (dd instanceof DDleaf _dd)
            return FQDDleaf.of(_dd);

        // recursively quantize till you get quantized leaves
        else {

            var children = new DD[dd.getChildren().length];
            for (int c = 0; c < dd.getChildren().length; c++)
                children[c] = quantize(dd.getChildren()[c]);

            return DDnode.getDD(dd.getVar(), children);
        }
    }

    public static DD unquantize(DD dd) {

        if (dd instanceof FQDDleaf _dd)
            return _dd.toDDleaf();
        
        // if leaf, quantize and return
        else if (dd instanceof DDleaf)
            return dd;

        // recursively quantize till you get quantized leaves
        else {

            var children = new DD[dd.getChildren().length];
            for (int c = 0; c < dd.getChildren().length; c++)
                children[c] = unquantize(dd.getChildren()[c]);

            return DDnode.getDD(dd.getVar(), children);
        }
    }

    public DD toDDleaf() {
        return DDleaf.getDD((float) (val / (float) BINS));
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this)
            return true;

        else if (obj instanceof FQDDleaf leaf) {

            if (val == leaf.val)
                return true;

            else
                return false;
        }

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
        builder.append("  ".repeat(spaces)).append("~").append(this.val);

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

        LOGGER.error("varSet being called on a quantized DD!!!");
        var e = new Exception("Cannot operate on quantized DD");
        e.printStackTrace();
        System.exit(-1);
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

    // -----------------------------------------------------------------
    // Useless abstract methods

    public float getVal() {
        return (float) val;
    }

    public int getNumLeaves() { return 1; }

    @Override
    public Object toLisp() {
        return val;
    }
}
