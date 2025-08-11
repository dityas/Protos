package thinclab.models.datastructures;

import java.util.List;

import com.google.gson.JsonObject;

import thinclab.legacy.Global;
import thinclab.utils.Jsonable;
import thinclab.utils.Tuple;

public class Observation extends Tuple<List<Integer>, List<Integer>> 
    implements Jsonable {

    public Observation(Tuple<List<Integer>, List<Integer>> obs) {
        super(obs._0(), obs._1());
    }

    @Override
    public JsonObject toJson() {

        var json = new JsonObject();

        for (int i = 0; i < _0().size(); i++) {

            var varName = Global.varNames.get(_0().get(i) - 1);
            var obsName = Global.valNames
                .get(_0().get(i) - 1)
                .get(_1().get(i) - 1);

            json.addProperty(varName, obsName);
        }

        return json;
    }

    @Override
    public String toString() {

        return toJson().getAsString();
    }
}
