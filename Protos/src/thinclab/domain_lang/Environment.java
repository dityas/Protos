
package thinclab.domain_lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import thinclab.RandomVariable;
import thinclab.utils.Tuple;

public class Environment {

    public List<RandomVariable> vars = new ArrayList<>();
    public HashMap<String, Object> map = new HashMap<>();

    public Environment() {}

    public Environment(
            List<RandomVariable> vars, 
            HashMap<String, Object> map) {

        this.vars.addAll(vars);
        this.map.putAll(map);
    }

    @Override
    public String toString() {
        return Tuple.of(vars, map).toString();
    }
}
