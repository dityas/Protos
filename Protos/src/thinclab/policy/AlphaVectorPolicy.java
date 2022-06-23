/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.utils.Jsonable;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class AlphaVectorPolicy implements Policy<DD>, Jsonable {

	public List<Tuple<Integer, DD>> aVecs;

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

	public static AlphaVectorPolicy fromR(List<DD> R) {

		return new AlphaVectorPolicy(
				IntStream.range(0, R.size()).mapToObj(i -> Tuple.of(i, R.get(i))).collect(Collectors.toList()));
	}

}
