package thinclab.legacy;

import java.io.Serializable;
import java.util.Optional;
import java.util.TreeSet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.utils.Graphable;
import thinclab.utils.Jsonable;
import thinclab.utils.LispExpressible;

public abstract class DD implements Serializable, Jsonable, Graphable, LispExpressible {

    public static DD one = DDleaf.getDD(1);
    public static DD zero = DDleaf.getDD(0);

    protected int X;

    private static final long serialVersionUID = 2478730562973454848L;
    private static final Logger LOGGER = LogManager.getFormatterLogger(DD.class);

    public int getVar() {
        return X;
    }

    public int getAddress() {

        return super.hashCode();
    }

    public DD[] getChildren() {

        return null;
    } // should throw exception

    abstract public float getVal();
    abstract public int getNumLeaves();
    abstract public TreeSet<Integer> getVars();

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

                if (ddVar == 0) {
                    LOGGER.error("Could not find %s in %s",
                            jo.get("name").getAsString(),
                            Global.varNames);

                    return Optional.empty();
                }

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
