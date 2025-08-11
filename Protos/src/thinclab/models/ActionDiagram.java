package thinclab.models;

import java.util.List;
import java.util.stream.Collectors;

import thinclab.legacy.DD;
import thinclab.utils.Tuple;


public class ActionDiagram {
    /*
     * A container class for the complete action diagram. This is analogous 
     * to the transition function for factored (I-)(PO)MDP.
     */

    public int actionIndex;
    public List<Integer> vars;
    public List<DD> dds;

    public ActionDiagram(int actionIndex, List<Integer> vars, List<DD> dds) {

        this.actionIndex = actionIndex;
        this.vars = vars;
        this.dds = dds;
    }

    public static ActionDiagram getActionDiagram(int actionIndex,
            List<Tuple<Integer, DD>> transitions) {

        var indices = transitions.stream()
            .map(t -> t._0())
            .collect(Collectors.toList());

        var dds = transitions.stream()
            .map(t -> t._1())
            .collect(Collectors.toList());

        return new ActionDiagram(actionIndex, indices, dds);
    }
}
