package thinclab.models.datastructures;

import java.util.List;

import thinclab.utils.Tuple;

public class Observation extends Tuple<List<Integer>, List<Integer>> {

    public Observation(Tuple<List<Integer>, List<Integer>> obs) {
        super(obs._0(), obs._1());
    }
}
