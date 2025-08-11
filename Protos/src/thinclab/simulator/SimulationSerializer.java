
package thinclab.simulator;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.POMDP;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.models.datastructures.Observation;
import thinclab.utils.Jsonable;

public class SimulationSerializer implements Jsonable {

    /*
     * Help serialize all simulation information to a JSON object that can
     * be written to a file
     */

    public final PBVISolvablePOMDPBasedModel agentI;
    public final PBVISolvablePOMDPBasedModel agentJ;

    public JsonArray recorder = new JsonArray();

    public SimulationSerializer(PBVISolvablePOMDPBasedModel agentI,
            PBVISolvablePOMDPBasedModel agentJ) {

        this.agentI = agentI;
        this.agentJ = agentJ;
    }

    public JsonObject beliefStateToJson(POMDP agent, DD belief) {
    
        var beliefJSON = DDOP.toJson(belief, agent.i_S());
        return beliefJSON.getAsJsonObject();
    }

    public JsonObject beliefStateToJson(IPOMDP agent, DD belief) {
    
        var beliefJSON = DDOP.toJson(belief, agent.i_S()).getAsJsonObject();

        // Add P(Theta_j)
        var beliefThetaj = 
            DDOP.addMultVarElim(
                    List.of(agent.PThetajGivenEC, belief), 
                    agent.i_S());
        var thetajJSON = DDOP.toJson(beliefThetaj, agent.i_Thetaj);
        beliefJSON.add("thetaj", thetajJSON);

        // Add P(Aj)
        var beliefAj = 
            DDOP.addMultVarElim(
                    List.of(agent.PAjGivenEC, belief), 
                    agent.i_S());
        var AjJSON = DDOP.toJson(beliefAj, agent.i_Aj);
        beliefJSON.add("Aj", AjJSON);

        return beliefJSON;
    }

    public JsonObject beliefStateToJson(PBVISolvablePOMDPBasedModel agent,
            DD belief) {
    
        if (agent instanceof IPOMDP agentIPOMDP)
            return beliefStateToJson(agentIPOMDP, belief);

        else
            return beliefStateToJson((POMDP) agent, belief);
    }

    public JsonObject buildAgentJson(PBVISolvablePOMDPBasedModel agent,
            DD belief, int action, Observation obs) {
    
        var beliefJSON = beliefStateToJson(agent, belief);
        beliefJSON.addProperty("action", agent.A().get(action));
        beliefJSON.add("observation", obs.toJson());
        
        return beliefJSON;
    }

    public void recordStep(JsonObject stateJSON, JsonObject agentIJSON,
            JsonObject agentJJSON) {
    
        var step = new JsonObject();
        step.add("state", stateJSON);
        step.add("agent_i", agentIJSON);
        step.add("agent_j", agentJJSON);

        recorder.add(step);
    }

    public void recordStep(DD state, List<Integer> stateIndices,
            DD beliefI, DD beliefJ,
            int actionI, int actionJ,
            Observation obsI, Observation obsJ) {

        var stateJSON = DDOP.toJson(state, stateIndices).getAsJsonObject();
        var agentIJSON = buildAgentJson(agentI, beliefI, actionI, obsI);
        var agentJJSON = buildAgentJson(agentJ, beliefJ, actionJ, obsJ);

        recordStep(stateJSON, agentIJSON, agentJJSON);

    }

    @Override
    public JsonObject toJson() {
        return recorder.getAsJsonObject();
    }
}
