/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.simulations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.ddinterface.DDTree;
import thinclab.legacy.Config;
import thinclab.legacy.DD;
import thinclab.legacy.OP;
import thinclab.representations.StructuredTree;
import thinclab.solvers.BaseSolver;
import thinclab.decisionprocesses.POMDP;

/*
 * @author adityas
 *
 */
public class StateSimulator extends StructuredTree {

	/*
	 * Simulator which samples states from the beliefs and then samples observations from
	 * the states.
	 */
	
	private int interactions = -1;
	private BaseSolver agent;
	
	private List<DDTree> states = new ArrayList<DDTree>();
	
	private static final long serialVersionUID = -1793195906458574199L;
	private static final Logger LOGGER = Logger.getLogger(StateSimulator.class);
	
	// -----------------------------------------------------------------------------------------
	
	public StateSimulator(BaseSolver pomdpSolver, int interactions) {
		
		this.agent = pomdpSolver;
		this.interactions = interactions;
		
		LOGGER.info("State sim initialized");
		
		this.initializeState();
	}
	
	// -----------------------------------------------------------------------------------------
	
	public void initializeState() {
		
		/*
		 * Sample state from one of the agents initial belief
		 */
		
		this.agent.f.setGlobals();
		
		DD belief = this.agent.f.getCurrentBelief();
		
		/* sample state from initial belief */
		int[][] stateConfig = OP.sampleMultinomial(belief, this.agent.f.getStateVarIndices());
		
		DD state = Config.convert2dd(stateConfig);
		this.states.add(state.toDDTree());
		
		LOGGER.debug("Initial state sampled. Set to " + this.agent.f.toMap(state));
	}
	
	public String[] getObservation(String action) {
		/*
		 * Sample observation from the state
		 */
		
		/* get observation distribution */
		DD obsDist = 
				this.agent.f.getObsDist(
						this.states.get(this.states.size() - 1).toDD(), 
						action);
		
		/* sample from distribution */
		int[][] obsConfig = 
				OP.sampleMultinomial(
						OP.primeVars(
								obsDist, 
								-(this.agent.f.getStateVarIndices().length + this.agent.f.getObsVarIndices().length)), 
						this.agent.f.getObsVarIndices());
		
		return POMDP.configToStrings(obsConfig);
	}
	
	public DD getNextState(String action) {
		/*
		 * Computes next state from current state and given action
		 */
		DD currentState = this.states.get(this.states.size() - 1).toDD();
		
		/* first make state transition based on action */
		DD[] Ti = this.agent.f.getTiForAction(action);
		DD nextStateDD = 
				OP.addMultVarElim(
						ArrayUtils.add(Ti, currentState), 
						this.agent.f.getStateVarIndices());
		nextStateDD = OP.primeVars(
				nextStateDD, 
				-(this.agent.f.getStateVarIndices().length + this.agent.f.getObsVarIndices().length));
		
		int[][] stateConfig = OP.sampleMultinomial(nextStateDD, this.agent.f.getStateVarIndices());
		DD state = Config.convert2dd(stateConfig);
		LOGGER.debug("Next state computed to be " + this.agent.f.toMap(state));
		
		return state;
	}
	
	public String[] envStep(String action) {
		
		/*
		 * Simulate single step of interaction
		 */

		LOGGER.debug("Taking action " + action 
				+ " in state " + this.agent.f.toMap(this.states.get(this.states.size() - 1).toDD()));
		
		/* get next state */
		DDTree nextState = this.getNextState(action).toDDTree();
		this.states.add(nextState);
		
		String[] obs = this.getObservation(action);
		LOGGER.debug("Observation sampled from state is " + Arrays.toString(obs));
		
		return obs;
	}
}
