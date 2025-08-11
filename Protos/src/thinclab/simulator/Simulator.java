
package thinclab.simulator;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.models.datastructures.Observation;
import thinclab.utils.Tuple;

public class Simulator {

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(Simulator.class);

    public final List<Integer> stateIndices;
    public final List<Integer> statePrimeIndices;

    public final List<List<DD>> transition;
    public final List<List<DD>> observationI;
    public final List<List<DD>> observationJ;

    public final List<Integer> obsIPrimeIndices;
    public final List<Integer> obsJPrimeIndices;

    public final int actionVarI;
    public final int actionVarJ;

    public DD state;

    public Simulator(List<Integer> stateIndices,
            int actionVarI, int actionVarJ,
            List<Integer> obsIPrimeIndices,
            List<Integer> obsJPrimeIndices,
            List<List<DD>> transition, List<List<DD>> obsI,
            List<List<DD>> obsJ) {

        this.stateIndices = stateIndices;
        this.statePrimeIndices = Global.makePrimeIndices(this.stateIndices);

        this.actionVarI = actionVarI;
        this.actionVarJ = actionVarJ;

        this.obsIPrimeIndices = obsIPrimeIndices;
        this.obsJPrimeIndices = obsJPrimeIndices;

        this.transition = transition;
        this.observationI = obsI;
        this.observationJ = obsJ;
        LOGGER.info("Initialized simulator");
    }

    // Set simulator state
    public void setState(DD state) {
        this.state = state;
        LOGGER.info("Initial state set to %s", 
                DDOP.factors(state, stateIndices));
    }

    public DD getState() {
        return state;
    }

    // perform given actions and sample the next state after transition
    public void updateState(int actionI, int actionJ) {

        var factors = new ArrayList<>(transition.get(actionI));
        factors.add(state);

        // nextState = sample Sum(states, [state, T(actionI, actionJ)])
        var s_p = DDOP.addMultVarElim(factors, stateIndices);
        s_p = DDOP.restrict(s_p, List.of(actionVarJ), List.of(actionJ + 1));
        s_p = DDOP.primeVars(s_p, -(Global.NUM_VARS / 2));

        var nextState = DDOP.sample(List.of(s_p), stateIndices);
        state = DDOP.ddFromVals(nextState._0(), nextState._1());
    }

    public Tuple<Observation, Observation> step(int actionI,
            int actionJ) {
        /*
         * Update the state given actions and sample observations from the
         * next state for both agents
         */

        updateState(actionI, actionJ);

        var obsI = getObservation(
                observationI.get(actionI), 
                obsIPrimeIndices,
                actionVarJ, actionJ);

        var obsJ = getObservation(
                observationJ.get(actionJ), 
                obsJPrimeIndices,
                actionVarI, actionI);

        return Tuple.of(obsI, obsJ);
    }

    public Observation getObservation(List<DD> obsFn,
            List<Integer> obsPrimeIndices,
            int otherActionVar, int otherAction) {
        /*
         * Sample observation from a multi agent observation function
         */
    
        obsFn = DDOP.restrict(obsFn, 
                List.of(otherActionVar), List.of(otherAction + 1));

        return getObservation(obsFn, obsPrimeIndices);
    }

    public Observation getObservation(List<DD> obsFn,
            List<Integer> obsPrimeIndices) {
        /*
         * Sample an observation from the given observation function
         */

        var statePrimed = DDOP.primeVars(state, (Global.NUM_VARS / 2));
        
        var factors = new ArrayList<>(obsFn);
        factors.add(statePrimed);

        var obsDist = DDOP.addMultVarElim(factors, statePrimeIndices);
    
        return new Observation(
                DDOP.sample(List.of(obsDist), obsPrimeIndices));
    }

}
