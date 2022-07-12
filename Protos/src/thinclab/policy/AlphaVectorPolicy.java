/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.utils.Jsonable;
import thinclab.utils.LispExpressible;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class AlphaVectorPolicy implements Policy<DD>, 
       Jsonable, LispExpressible {

	public List<Tuple<Integer, DD>> aVecs;

    private static final Logger LOGGER = 
        LogManager.getLogger(AlphaVectorPolicy.class);

	public AlphaVectorPolicy(List<Tuple<Integer, DD>> alphaVectors) {

		this.aVecs = alphaVectors;
	}

	@Override
	public int getBestActionIndex(DD belief, List<Integer> S) {

		int i = DDOP.bestAlphaIndex(aVecs, belief, S);
		return aVecs.get(i)._0();
	}

	@Override
	public String toString() {
		return aVecs.toString();
	}

    @Override
    public JsonElement toJson() {

        var json = new JsonArray();

        aVecs.stream()
            .forEach(a -> {
                
                var _json = new JsonObject();
                _json.add("actId", new JsonPrimitive(a._0()));
                _json.add("alpha", a._1().toJson());

                json.add(_json);

            });

        return json;
    }

    @Override
    public Object toLisp() {

        var policyList = new ArrayList<Object>(aVecs.size());
        policyList.add("list");

        for (var alpha: aVecs) {

            var vecList = new ArrayList<Object>(2);
            vecList.add("list");
            vecList.add(alpha._0());
            vecList.add(alpha._1());

            policyList.add(vecList);
        }

        return policyList;
    }

    public static AlphaVectorPolicy fromJson(JsonElement json) {

        if (json instanceof JsonArray ja) {
            
            var aVecs = new ArrayList<Tuple<Integer, DD>>();

            ja.forEach(j -> {

                var _jo = j.getAsJsonObject();
                var actId = _jo.get("actId").getAsInt();
                var alpha = DD.fromJson(_jo.get("alpha"));

                if (alpha.isPresent())
                    aVecs.add(Tuple.of(actId, alpha.get()));

                else
                    LOGGER.error(
                            String.format(
                                "Error while loading %s", _jo));
            });

            return new AlphaVectorPolicy(aVecs);
        }

        return null;
    }

	public static AlphaVectorPolicy fromR(List<DD> R) {

		return new AlphaVectorPolicy(
				IntStream.range(0, R.size()).mapToObj(i -> Tuple.of(i, R.get(i))).collect(Collectors.toList()));
	}

}
