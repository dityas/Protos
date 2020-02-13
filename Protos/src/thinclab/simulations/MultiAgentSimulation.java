/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.simulations;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import thinclab.ddinterface.DDTree;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.Config;
import thinclab.legacy.DD;
import thinclab.legacy.OP;
import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.BaseSolver;
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
		
		/*
		 * Make init nodes
		 */
		
		/* make start policy node from initial belief */
		this.l1Solver.f.setGlobals();
		PolicyNode iNode = new PolicyNode();
		iNode.setBelief(this.l1Solver.f.getCurrentBelief());
		iNode.setsBelief(this.l1Solver.f.getBeliefString(this.l1Solver.f.getCurrentBelief()));
		iNode.setId(this.currentPolicyNodeCounter++);
		
		/* make state node */
		this.solver.f.setGlobals();
		PolicyNode sNode = new PolicyNode();
		sNode.setBelief(this.states.get(this.states.size() - 1).toDD());
		sNode.setsBelief(this.solver.f.getBeliefString(this.states.get(this.states.size() - 1).toDD()));
		sNode.setId(this.currentPolicyNodeCounter++);
		
		/* make start policy node for j */
		PolicyNode jNode = new PolicyNode();
		jNode.setBelief(this.solver.f.getCurrentBelief());
		jNode.setsBelief(this.solver.f.getBeliefString(this.solver.f.getCurrentBelief()));
		jNode.setId(this.currentPolicyNodeCounter++);
		
		/* add to node map */
		this.putPolicyNode(iNode.getId(), iNode);
		this.putPolicyNode(sNode.getId(), sNode);
		this.putPolicyNode(jNode.getId(), jNode);
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
				((OnlineSolver) this.l1Solver).solveCurrentStep();
				((OnlineSolver) this.l1Solver).expansionStrategy.clearMem();
			}
			
			if (this.solver instanceof OnlineSolver) {
				this.solver.f.setGlobals();
				((OnlineSolver) this.solver).solveCurrentStep();
				((OnlineSolver) this.solver).expansionStrategy.clearMem();
			}
			
			/* record actions and observations and all that at current step */
			
			// --------------------------------------------------------------------------------
			
			/* record the L1 step */
			this.l1Solver.f.setGlobals();
			DD currentL1Belief = this.getPolicyNode(currentNode).getBelief();
			
			this.l1BeliefSequence.add(this.l1Solver.f.getBeliefString(currentL1Belief));
			LOGGER.info("At L1 belief " 
					+ this.l1BeliefSequence.get(this.l1BeliefSequence.size() - 1));
			
			/* optimal action for L1 */
			String l1Action = this.l1Solver.getActionForBelief(currentL1Belief);
			this.getPolicyNode(currentNode).setActName(l1Action);
			
			// ---------------------------------------------------------------------------------
			
			/* record L0 belief */
			this.solver.f.setGlobals();
			DD currentL0Belief = this.getPolicyNode(currentNode + 2).getBelief();
			
			this.l0BeliefSequence.add(this.solver.f.getBeliefString(currentL0Belief));
			LOGGER.info("L0 belief is " 
					+ this.l0BeliefSequence.get(this.l0BeliefSequence.size() - 1));
			
			/* optimal action for L0 */
			String l0Action = this.solver.getActionForBelief(currentL0Belief);
			this.getPolicyNode(currentNode + 2).setActName(l0Action);
			
			// ----------------------------------------------------------------------------------
			
			/* record state */
			DD currentState = this.states.get(this.states.size() - 1).toDD();
			DD[] currentFactoredState = this.solver.f.factorBelief(currentState);
			
			this.stateSequence.add(
					this.solver.f.getBeliefString(currentState));
			LOGGER.info("Real state is " + this.stateSequence.get(this.stateSequence.size() - 1));
			
			// ----------------------------------------------------------------------------------
			
			/* take action */
			String[][] obs = this.multiAgentEnvStep(l1Action + "__" + l0Action);
			
			/* record action and obs */
			this.l0ActionSequence.add(l0Action);
			this.l1ActionSequence.add(l1Action);
			
			this.l0ObsSequence.add(Arrays.toString(obs[1]));
			this.l1ObsSequence.add(Arrays.toString(obs[0]));
			
			// ------------------------------------------------------------------------------------
			
			/* compute L1 reward */
			this.l1Solver.f.setGlobals();
			double l1ExpectedReward = 
					OP.factoredExpectationSparse(
							this.l1Solver.f.factorBelief(currentL1Belief), 
							this.l1Solver.f.getRewardFunctionForAction(l1Action));
			
			double l1TrueReward = 
					OP.factoredExpectationSparse(
							currentFactoredState, 
							this.l1Solver.f.getRewardFunctionForAction(l1Action));
			
			this.l1ExpectedReward.add(l1ExpectedReward);
			LOGGER.info("Agent i's expected reward is " 
					+ this.l1ExpectedReward.get(
							this.l1ExpectedReward.size() - 1));
			
			this.l1TrueReward.add(l1TrueReward);
			LOGGER.info("Agent i's true reward is " 
					+ this.l1TrueReward.get(
							this.l1TrueReward.size() - 1));
			
			/* step agent I */
			if (this.l1Solver instanceof OnlineSolver)
				((OnlineSolver) this.l1Solver).nextStep(l1Action, Arrays.asList(obs[0]));
			
			else
				this.l1Solver.f.step(currentL1Belief, l1Action, obs[0]);
			
			/* make next policy node for agent i */
			PolicyNode nextINode = new PolicyNode();
			nextINode.setBelief(this.l1Solver.f.getCurrentBelief());
			nextINode.setsBelief(this.l1Solver.f.getBeliefString(this.l1Solver.f.getCurrentBelief()));
			nextINode.setId(this.currentPolicyNodeCounter++);
			this.putPolicyNode(nextINode.getId(), nextINode);
			
			/* make state node here to maintain currentPolicyNodeCounter order */
			PolicyNode nextStateNode = new PolicyNode();
			nextStateNode.setId(this.currentPolicyNodeCounter++);
			
			// --------------------------------------------------------------------------------------
			
			/* compute L0 reward */
			this.solver.f.setGlobals();
			double l0ExpectedReward = 
					OP.factoredExpectationSparse(
							this.solver.f.factorBelief(currentL0Belief), 
							this.solver.f.getRewardFunctionForAction(l0Action));
			
			double l0TrueReward = 
					OP.factoredExpectationSparse(
							this.solver.f.factorBelief(currentState), 
							this.solver.f.getRewardFunctionForAction(l0Action));
			
			this.l0ExpectedReward.add(l0ExpectedReward);
			LOGGER.info("Agent j's expected reward is " 
					+ this.l0ExpectedReward.get(
							this.l0ExpectedReward.size() - 1));
			
			this.l0TrueReward.add(l0TrueReward);
			LOGGER.info("Agent j's true reward is " 
					+ this.l0TrueReward.get(
							this.l0TrueReward.size() - 1));
			
			/* step agent J */
			if (this.solver instanceof OnlineSolver)
				((OnlineSolver) this.solver).nextStep(l0Action, Arrays.asList(obs[1]));
			
			else
				solver.f.step(currentL0Belief, l0Action, obs[1]);
			
			/* make policy node for next belief of agent j */
			PolicyNode nextJNode = new PolicyNode();
			nextJNode.setBelief(this.solver.f.getCurrentBelief());
			nextJNode.setsBelief(this.solver.f.getBeliefString(this.solver.f.getCurrentBelief()));
			nextJNode.setId(this.currentPolicyNodeCounter++);
			this.putPolicyNode(nextJNode.getId(), nextJNode);
			
			/* populate next state node */
			nextStateNode.setBelief(this.states.get(this.states.size() - 1).toDD());
			nextStateNode.setsBelief(
					this.solver.f.getBeliefString(this.states.get(this.states.size() - 1).toDD()));
			this.putPolicyNode(nextStateNode.getId(), nextStateNode);
			
			// -------------------------------------------------------------------------------------
			
			/* make path */
			this.putEdge(currentNode, Arrays.asList(obs[0]), nextINode.getId());
//			this.putEdge(currentNode, Arrays.asList(l1Action), currentNode + 1);
//			this.putEdge(currentNode + 2, Arrays.asList(l0Action), currentNode + 1);
//			this.putEdge(nextStateNode.getId(), Arrays.asList(obs[0]), currentNode);
//			this.putEdge(nextStateNode.getId(), Arrays.asList(obs[1]), currentNode + 2);
			this.putEdge(
					currentNode + 1, 
					Arrays.asList(new String[] {l1Action, l0Action}), 
					nextStateNode.getId());
			this.putEdge(currentNode + 2, Arrays.asList(obs[1]), nextJNode.getId());
			
			return nextINode.getId();
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
		LOGGER.error("Method not applicable");
		return null;
	}
	
	@Override
	public String[] getObservation(String action) {
		LOGGER.error("Method not applicable");
		return null;
	}
	
	public String[][] multiAgentEnvStep(String action) {
		
		/*
		 * Simulate single step of interaction
		 */
		
		String[] actions = action.split("__");
		
		LOGGER.debug("Agent i took action " + actions[0] + ", agent j took action " + actions[1] 
				+ " in state " + this.solver.f.toMap(this.states.get(this.states.size() - 1).toDD()));
		
		/* get next state */
		DDTree nextState = this.getNextState(action).toDDTree();
		this.states.add(nextState);
		
		/* sample observation */
		String[] l1Obs = this.getL1Observation(action);
		LOGGER.debug("L1 observation sampled from state is " + Arrays.toString(l1Obs));
		
		String[] l0Obs = this.getL0Observation(action);
		LOGGER.debug("L0 observation sampled from state is " + Arrays.toString(l0Obs));
		
		/* stack observation arrays */
		String[][] obs = new String[2][];
		obs[0] = l1Obs;
		obs[1] = l0Obs;
		
		return obs;
	}
	
	public String[] getL1Observation(String action) {
		/*
		 * Sample observation from the state
		 */
		
		/* relevant varIndices */
		String[] actions = action.split("__");
		int[] varIndices = 
				ArrayUtils.subarray(
						this.l1Solver.f.getStateVarPrimeIndices(), 
						0, ((IPOMDP) this.l1Solver.f).MjVarPosition);
		
		/* create config structure to restrict Aj */
		int[] actVal = new int[] {this.solver.f.getActions().indexOf(actions[1]) + 1};
		int[] actVarConfig = new int[] {((IPOMDP) this.l1Solver.f).AjStartIndex};
		int[][] actConfig = IPOMDP.stackArray(actVarConfig, actVal);
		
		/* get observation distribution */
		DD obsDist = 
				OP.addMultVarElim(
						ArrayUtils.add(
								this.l1Solver.f.getOiForAction(actions[0]), 
								OP.primeVars(
										this.states.get(this.states.size() - 1).toDD(), 
										this.l1Solver.f.getNumVars())),
						varIndices);
		
		obsDist = OP.restrict(obsDist, actConfig);
		
		/* sample from distribution */
		int[][] obsConfig = 
				OP.sampleMultinomial(
						obsDist, 
						this.l1Solver.f.getObsVarPrimeIndices());
		
		return IPOMDP.configToStrings(obsConfig);
	}
	
	public String[] getL0Observation(String action) {
		/*
		 * Sample observation from the state
		 */
		
		this.solver.f.setGlobals();
		String[] actions = action.split("__");
		
		/* get observation distribution */
		DD obsDist = 
				OP.addMultVarElim(
						ArrayUtils.add(
								this.solver.f.getOiForAction(actions[1]), 
								OP.primeVars(
										this.states.get(this.states.size() - 1).toDD(), 
										this.solver.f.getNumVars())),
						this.solver.f.getStateVarPrimeIndices());
		
		/* sample from distribution */
		int[][] obsConfig = 
				OP.sampleMultinomial(
						obsDist, 
						this.solver.f.getObsVarPrimeIndices());
		
		return POMDP.configToStrings(obsConfig);
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
	
	// -----------------------------------------------------------------------------------------------
	
	public void runSimulation() {
		/*
		 * Runs the simulation for the given number of iterations
		 */
		
		LOGGER.info("Running simulation for " + this.iterations + " iterations...");
		
		int previousNode = 0;
		
		for (int i = 0; i < this.iterations; i++) {
			int nextNode = this.step(previousNode);
			previousNode = nextNode;
		}
		
		this.logResults();
	}
	
	// ---------------------------------------------------------------------------------------------
	
	@Override
	public void logResults() {
		/*
		 * Writes all results to the logger
		 */
		
		LOGGER.info("Sim results:");
		LOGGER.info("I belief, state, J belief, Ai, Aj, Oi, Oj, ERi, Ri, ERj, Rj"
				+ "cumulative R, True cumulative R");
		
		for (int i = 0; i < this.l1BeliefSequence.size(); i++) {
			LOGGER.info("\"" + this.l1BeliefSequence.get(i).replace("\"", "\\\"") + "\"" + ", "
					+ "\"" + this.stateSequence.get(i).replace("\"", "\\\"") + "\"" + ", "
					+ "\"" + this.l0BeliefSequence.get(i).replace("\"", "\\\"") + "\"" + ", "
					+ this.l1ActionSequence.get(i) + ", "
					+ this.l0ActionSequence.get(i) + ", "
					+ "\"" + this.l1ObsSequence.get(i) + "\"" + ", "
					+ "\"" + this.l0ObsSequence.get(i) + "\"" + ", "
					+ this.l1ExpectedReward.get(i) + ", "
					+ this.l1TrueReward.get(i) + ", "
					+ this.l0ExpectedReward.get(i) + ", "
					+ this.l0TrueReward.get(i));
		}
	}
	
	@Override
	public void logToFile(String fileName) {
		/*
		 * Writes simulation results to csv file
		 */
		
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(fileName));
			LOGGER.debug("Writing sim results to file " + fileName);
			
			/* initialize JSON handler */
			Gson gsonHandler = 
					new GsonBuilder()
						.setPrettyPrinting()
						.disableHtmlEscaping()
						.create();
			
			JsonArray recordsArray = new JsonArray();
			
			for (int i = 0; i < this.stateSequence.size(); i++) {
				
				/* init a new record */
				JsonObject record = new JsonObject();
				
				/* write belief of i */
				record.add(
						"beliefI", 
						gsonHandler.fromJson(
								this.l1BeliefSequence.get(i), 
								JsonObject.class));
				
				/* write belief of j */
				record.add(
						"beliefJ", 
						gsonHandler.fromJson(
								this.l0BeliefSequence.get(i), 
								JsonObject.class));
				
				/* write state */
				record.add(
						"state", 
						gsonHandler.fromJson(
								this.stateSequence.get(i), 
								JsonObject.class));
				
				/* write action of i */
				record.add("Ai", new JsonPrimitive(this.l1ActionSequence.get(i)));
				
				/* write action of j */
				record.add("Aj", new JsonPrimitive(this.l0ActionSequence.get(i)));
				
				/* write observations of i */
				record.add("Oi", new JsonPrimitive(this.l1ObsSequence.get(i)));
				
				/* write observations of j */
				record.add("Oj", new JsonPrimitive(this.l0ObsSequence.get(i)));
				
				/* write rewards of i */
				record.add("ERi", 
						new JsonPrimitive(this.l1ExpectedReward.get(i)));				
				record.add("Ri", 
						new JsonPrimitive(this.l1TrueReward.get(i)));
				
				/* write rewards of j */
				record.add("ERj", 
						new JsonPrimitive(this.l0ExpectedReward.get(i)));				
				record.add("Rj", 
						new JsonPrimitive(this.l0TrueReward.get(i)));
				
				recordsArray.add(record);
			}
			
			writer.println(gsonHandler.toJson(recordsArray));
			writer.flush();
			writer.close();
			
		}
		
		catch (Exception e) {
			LOGGER.error("While writing results to JSON file: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
