package thinclab.legacy;

import java.io.Serializable;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import thinclab.utils.Graphable;
import thinclab.utils.Jsonable;

public abstract class DD implements Serializable, Jsonable, Graphable {

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

    abstract public float getVal();
    abstract public int getNumLeaves();

    // abstract public SortedSet getScope();
    //abstract public int[] getVarSet();

    abstract public TreeSet<Integer> getVars();

    //abstract public float getSum();

    abstract public DD store();

    public static DD cast(DDleaf leaf) {

        return (DD) leaf;
    }

    public static DD cast(DDnode node) {

        return (DD) node;
    }

    abstract public String toDot();

    abstract public String toSPUDD();
    abstract public String toSPUDD(int spaces);

    public static Optional<DD> fromJson(JsonElement json) {

        if (json instanceof JsonObject jo) {

            if (jo.has("name")) {

                var ddVar = Global.varNames
                    .indexOf(
                            jo.get("name")
                            .getAsString()) + 1;

                var values = jo.get("value").getAsJsonObject();
                var dds = Global.valNames.get(ddVar - 1)
                    .stream().map(v -> 
                            DD.fromJson(values.get(v))
                              .orElse(DDleaf.getDD(Float.NaN)))
                    .toArray(DD[]::new);

                return Optional.of(DDnode.getDD(ddVar, dds));
            }

            else
                return Optional.ofNullable(DDleaf.getDD(
                            jo.get("value")
                            .getAsFloat()));
        }

        else 
            return Optional.empty();

    }

}
