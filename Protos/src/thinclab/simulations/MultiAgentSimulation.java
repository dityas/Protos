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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.ddinterface.DDTree;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.Config;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.BaseSolver;
import thinclab.solvers.OnlineIPBVISolver;
import thinclab.solvers.OnlineSolver;

/*
 * @author adityas
 *
 */
public class MultiAgentSimulation extends Simulation {

	/*
	 * Makes simulation API for multi agent setting
	 */
	
	private OnlineSolver l1Solver;
	
	/* some lists for storing run stats */
	List<String> stateSequence = new ArrayList<String>();
	List<String> l1BeliefSequence = new ArrayList<String>();
	List<String> l0BeliefSequence = new ArrayList<String>();
	List<String> l1ActionSequence = new ArrayList<String>();
	List<String> l0ActionSequence = new ArrayList<String>();
	List<String> l1ObsSequence = new ArrayList<String>();
	List<String> l0ObsSequence = new ArrayList<String>();
	List<Double> l1ExpectedReward = new ArrayList<Double>();
	List<Double> l0ExpectedReward = new ArrayList<Double>();
	List<Double> l1TrueReward = new ArrayList<Double>();
	List<Double> l0TrueReward = new ArrayList<Double>();
	
	private static final long serialVersionUID = -7628758614982159814L;
	private static final Logger LOGGER = Logger.getLogger(MultiAgentSimulation.class);
	
	// ---------------------------------------------------------------------------------------
	
	public MultiAgentSimulation(
			OnlineSolver ipomdpSolver, BaseSolver pomdpSolver, int interactions) {
		
		super();
		
		this.solver = pomdpSolver;
		this.l1Solver = ipomdpSolver;
		this.iterations = interactions;
		
		this.initializeState();
	}
	
	// ----------------------------------------------------------------------------------------
	
	@Override
	public void initializeState() {
		
		/*
		 * Sample state from one of the agents initial belief
		 */
		this.l1Solver.f.setGlobals();
		
		DD belief = this.l1Solver.f.getCurrentBelief();
		
		/* sample state from initial belief */
		int[][] stateConfig = 
				OP.sampleMultinomial(
						belief, 
						ArrayUtils.subarray(
								this.l1Solver.f.getStateVarIndices(),
								0, ((IPOMDP) this.l1Solver.f).thetaVarPosition));
		
		DD state = Config.convert2dd(stateConfig);
		
		HashMap<String, HashMap<String, Float>> stateMap = this.l1Solver.f.toMap(state);
		stateMap.remove("M_j");
		
		state = OP.addout(state, ((IPOMDP) this.l1Solver.f).MjVarIndex);
		
		this.states.add(state.toDDTree());
		
		LOGGER.debug("Initial state sampled. Set to " + stateMap);
	}
	
	public int step(int currentNode) {
		
		try {
			
			/* Solve current step for solvers */
			if (this.l1Solver instanceof OnlineSolver) {
				this.l1Solver.f.setGlobals();
				((OnlineSolver) solver).solveCurrentStep();
			}
			
			if (this.solver instanceof OnlineSolver) {
				this.solver.f.setGlobals();
				((OnlineSolver) solver).solveCurrentStep();
			}
			
//			/* record actions and observations and all that at current step */
			
			/* record the L1 step */
			this.l1Solver.f.setGlobals();
			DD currentL1Belief = this.getPolicyNode(currentNode).getBelief();
			
			this.l1BeliefSequence.add(this.l1Solver.f.getBeliefString(currentL1Belief));
			LOGGER.info("At L1 belief " 
					+ this.l1BeliefSequence.get(this.l1BeliefSequence.size() - 1));
			
			/* optimal action for L1 */
			String l1Action = this.l1Solver.getActionForBelief(currentL1Belief);
			this.getPolicyNode(currentNode).setActName(l1Action);
			
			/* record L0 belief */
			this.solver.f.setGlobals();
			DD currentL0Belief = this.getPolicyNode(currentNode + 2).getBelief();
			
			this.l0BeliefSequence.add(this.solver.f.getBeliefString(currentL0Belief));
			LOGGER.info("L0 belief is " 
					+ this.l0BeliefSequence.get(this.l0BeliefSequence.size() - 1));
			
			/* optimal action for L0 */
			String l0Action = this.solver.getActionForBelief(currentL0Belief);
			this.getPolicyNode(currentNode + 2).setActName(l0Action);
			
			/* record state */
			DD currentState = this.states.get(this.states.size() - 1).toDD();
			
			this.stateSequence.add(
					this.solver.f.getBeliefString(currentState));
			LOGGER.info("Real state is " + this.stateSequence.get(this.stateSequence.size() - 1));
			
			/* take action */
//			String[] obs = this.act(solver.f, currentBelief, action);
			String[] obs = this.envStep(l1Action + "__" + l0Action);
			
			if (this.solver instanceof OnlineIPBVISolver) {
				DD[] aVecs = ((OnlineIPBVISolver) this.solver).alphaVectors;
				int[] policy = ((OnlineIPBVISolver) this.solver).policy;
				
				for (int v = 0; v < aVecs.length; v++) {
					LOGGER.info("For A vec. " + v + " representing action " 
							+ this.solver.f.getActions().get(policy[v]));
					LOGGER.info("Reward is: " + 
							OP.dotProduct(
									currentBelief, 
									aVecs[v], this.solver.f.getStateVarIndices()));
				}
			}
			
			/* L0 stuff */
			DD currentL0Belief = this.getPolicyNode(currentNode + 2).getBelief();
			
			
			/* record action and obs */
			this.actionSequence.add(action);
			this.obsSequence.add(Arrays.toString(obs));
			
			/* compute reward */
			double reward = 
					OP.factoredExpectationSparse(
							solver.f.factorBelief(currentBelief), 
							solver.f.getRewardFunctionForAction(action));
			
			double realReward = 
					OP.factoredExpectationSparse(
							solver.f.factorBelief(currentState), 
							solver.f.getRewardFunctionForAction(action));
			
			this.immediateRewardSequence.add(reward);
			LOGGER.info("Immediate expected reward is " 
					+ this.immediateRewardSequence.get(
							this.immediateRewardSequence.size() - 1));
			
			this.trueRewardSequence.add(realReward);
			LOGGER.info("Immediate True reward is " 
					+ this.trueRewardSequence.get(
							this.trueRewardSequence.size() - 1));
			
			double lastReward = 0.0;
			double lastTrueReward = 0.0;
			
			if (this.cumulativeRewardSequence.size() > 0) {
				lastReward = 
					this.cumulativeRewardSequence.get(this.cumulativeRewardSequence.size() - 1);
				
				lastTrueReward = 
						this.trueCumulativeReward.get(this.trueCumulativeReward.size() - 1);
			}
			
			this.cumulativeRewardSequence.add(lastReward + reward);
			LOGGER.info("Cumulative reward is " + (lastReward + reward));
			
			this.trueCumulativeReward.add(lastTrueReward + realReward);
			LOGGER.info("True reward so far is " + (lastTrueReward + realReward));
			
			if (solver instanceof OnlineSolver)
				((OnlineSolver) solver).nextStep(action, Arrays.asList(obs));
			
			else
				solver.f.step(currentBelief, action, obs);
			
			/* make policy node for next belief */
			PolicyNode nextNode = new PolicyNode();
			nextNode.setBelief(solver.f.getCurrentBelief());
			nextNode.setsBelief(solver.f.getBeliefString(solver.f.getCurrentBelief()));
			nextNode.setId(this.currentPolicyNodeCounter++);
			this.putPolicyNode(nextNode.getId(), nextNode);
			
			/* make next state node */
			PolicyNode nextStateNode = new PolicyNode();
			nextStateNode.setBelief(this.states.get(this.states.size() - 1).toDD());
			nextStateNode.setsBelief(
					solver.f.getBeliefString(this.states.get(this.states.size() - 1).toDD()));
			nextStateNode.setId(this.currentPolicyNodeCounter++);
			this.putPolicyNode(nextStateNode.getId(), nextStateNode);
			
			/* make path */
			this.putEdge(currentNode, Arrays.asList(obs), nextNode.getId());
			this.putEdge(currentNode + 1, Arrays.asList(obs), nextStateNode.getId());
//			this.edgeMap.get(currentNode).put(Arrays.asList(obs), nextNode.getId());
			
			return nextNode.getId();
		}
		
		catch (ZeroProbabilityObsException o) {
			return -1;
		}
		
		catch (Exception e) {
			LOGGER.error("While running belief update " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
			
			return -1;
		}
	}
	
	@Override
	public String[] envStep(String action) {
		
		/*
		 * Simulate single step of interaction
		 */
		
		String[] actions = action.split("__");
		
		LOGGER.debug("Agent i took action " + actions[0] + ", agent j took action " + actions[1] 
				+ " in state " + this.solver.f.toMap(this.states.get(this.states.size() - 1).toDD()));
		
		/* get next state */
		DDTree nextState = this.getNextState(action).toDDTree();
		this.states.add(nextState);
		
		String[] obs = this.getObservation(action);
		LOGGER.debug("Observation sampled from state is " + Arrays.toString(obs));
		
		return obs;
	}
	
	@Override
	public DD getNextState(String action) {
		/*
		 * Computes next state from current state and given action
		 */
		
		String[] actions = action.split("__");
		int[] varIndices = 
				ArrayUtils.subarray(
						this.l1Solver.f.getStateVarIndices(), 
						0, ((IPOMDP) this.l1Solver.f).MjVarPosition);
		
		/* set context for L1 agent */
		this.l1Solver.f.setGlobals();
		DD currentState = this.states.get(this.states.size() - 1).toDD();
		
		/* first make state transition based on action */
		DD[] Ti = this.l1Solver.f.getTiForAction(actions[0]);

		DD nextStateDD = 
				OP.addMultVarElim(
						ArrayUtils.add(Ti, currentState), 
						varIndices);
		
		/* restrict transition to Aj */
		int[] actVal = new int[] {this.solver.f.getActions().indexOf(actions[1]) + 1};
		int[] actVarConfig = new int[] {((IPOMDP) this.l1Solver.f).AjStartIndex};
		int[][] actConfig = IPOMDP.stackArray(actVarConfig, actVal); 
		
		nextStateDD = OP.restrict(nextStateDD, actConfig);
		
		nextStateDD = OP.primeVars(
				nextStateDD,
				-(this.l1Solver.f.getNumVars()));
		
		int[][] stateConfig = OP.sampleMultinomial(nextStateDD, this.solver.f.getStateVarIndices());
		DD state = Config.convert2dd(stateConfig);
		
		LOGGER.debug("State transitioned to " + this.solver.f.toMap(state));
		
		return state;
	}

}
