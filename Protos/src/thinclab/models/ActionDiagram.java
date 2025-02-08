package thinclab.models;

import java.util.List;
import thinclab.legacy.DD;


public class ActionDiagram {
    /*
     * A container class for the complete action diagram. This is analogous 
     * to the transition function for factored (I-)(PO)MDP.
     */

    public List<Integer> vars;
    public List<DD> dds;

    public ActionDiagram(List<Integer> vars, List<DD> dds) {

        this.vars = vars;
        this.dds = dds;
    }
}
