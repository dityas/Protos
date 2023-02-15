/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policy;

import java.util.ArrayList;
import java.util.Collection;
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
import thinclab.legacy.DDleaf;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.utils.Jsonable;
import thinclab.utils.LispExpressible;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class AlphaVectorPolicy implements 
Policy<DD>, Jsonable, LispExpressible {

    public List<Tuple<Integer, DD>> aVecs;

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(AlphaVectorPolicy.class);

    public AlphaVectorPolicy(List<Tuple<Integer, DD>> alphaVectors) {
        this.aVecs = alphaVectors;
    }

    public void printPolicyValuationAtBelief(DD b, 
            List<String> A, List<Integer> vars) {

        for (var aVec: aVecs)
            System.out.println(String.format("Alpha: %s, a: %s, V: %s", 
                        aVec._0(), A.get(aVec._0()), 
                        DDOP.dotProduct(aVec._1(), b, vars)));
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
        policyList.add("alphaVectorPolicy");
        for (var alpha: aVecs) {

            var vecList = new ArrayList<Object>(2);
            vecList.add(alpha._0());
            vecList.add(alpha._1().toLisp());

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
                    LOGGER.error("Error while loading %s", _jo);
            });

            return new AlphaVectorPolicy(aVecs);
        }

        return null;
    }

    public static AlphaVectorPolicy fromR(List<DD> R) {

        return new AlphaVectorPolicy(
                IntStream.range(0, R.size())
                .mapToObj(i -> Tuple.of(i, R.get(i)))
                .collect(Collectors.toList()));
    }

    public static AlphaVectorPolicy randomPolicy(int sizeA) {
        return new AlphaVectorPolicy(
                IntStream.range(0, sizeA)
                .mapToObj(i -> 
                    Tuple.of(i, 
                        DDleaf.getDD(0.0f)))
                .collect(Collectors.toList()));
    }

    public float getEvalDifferenceAtBelief(AlphaVectorPolicy p,
            DD b, List<Integer> vars) {
        
        return DDOP.value_b(aVecs, b, vars) 
            - DDOP.value_b(p.aVecs, b, vars);

    }

    public Collection<String> getActions(PBVISolvablePOMDPBasedModel m) {

        var solnSet = aVecs.parallelStream()
            .map(a -> m.A().get(a._0())).collect(Collectors.toSet());

        return solnSet;
    }
}
