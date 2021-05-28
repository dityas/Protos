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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import thinclab.ddinterface.DDTree;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.MAPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.Config;
import thinclab.legacy.DD;
import thinclab.legacy.OP;
import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.AlphaVectorPolicySolver;
import thinclab.solvers.BaseSolver;
import thinclab.solvers.HumanAgentSolver;

/*
 * @author adityas
 *
 */
public class HumanAgentSimulation extends Simulation {

	/*
	 * Makes simulation API for multi agent setting
	 */

	private BaseSolver l1Solver;

	private String mjDotDir = null;
	private PrintWriter summaryWriter = null;
	private int iterId = -1;
	private int jFrameID = -1;
	private boolean interactionOver = false;

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
	private static final Logger LOGGER = LogManager.getLogger(HumanAgentSimulation.class);

	// ---------------------------------------------------------------------------------------

	public HumanAgentSimulation(BaseSolver ipomdpSolver, HumanAgentSolver humanSolver, int interactions) {

		super();

		this.interactionOver = false;
		this.solver = humanSolver;

		this.jFrameID = -1;

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
		PolicyNode sNode = new PolicyNode();
		sNode.setBelief(this.states.get(this.states.size() - 1).toDD());
		sNode.setsBelief(this.l1Solver.f.getBeliefString(this.states.get(this.states.size() - 1).toDD()));
		sNode.setId(this.currentPolicyNodeCounter++);

		/* make start policy node for j */
		PolicyNode jNode = new PolicyNode();
		jNode.setBelief(null);
		jNode.setsBelief("");
		jNode.setId(this.currentPolicyNodeCounter++);

		/* add to node map */
		this.putPolicyNode(iNode.getId(), iNode);

	}

	public void setMjDotDir(String mjDotDir, int id) {
		this.mjDotDir = mjDotDir;
		this.iterId = id;

		try {
			this.summaryWriter = new PrintWriter(
					new FileOutputStream(this.mjDotDir + "/" + "interaction_summary" + id + ".txt"));
		}

		catch (Exception e) {
			LOGGER.error("Could not make summary file");
		}
	}

	public void endSimulation() {
		this.interactionOver = true;
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
		int[][] stateConfig = null;

		if (this.l1Solver.f.getType().contentEquals("IPOMDP")) {
			stateConfig = OP.sampleMultinomial(belief, ArrayUtils.subarray(this.l1Solver.f.getStateVarIndices(), 0,
					((IPOMDP) this.l1Solver.f).thetaVarPosition));
		}

		else {
			stateConfig = OP.sampleMultinomial(belief, ArrayUtils.subarray(this.l1Solver.f.getStateVarIndices(), 0,
					this.l1Solver.f.getStateVarIndices().length - 1));
		}

		DD state = Config.convert2dd(stateConfig);

		HashMap<String, HashMap<String, Float>> stateMap = this.l1Solver.f.toMap(state);
		stateMap.remove("Theta_j");

		if (this.l1Solver.f.getType().contentEquals("IPOMDP"))
			state = OP.addout(state, ((IPOMDP) this.l1Solver.f).MjVarIndex);

		this.states.add(state.toDDTree());

		LOGGER.debug("Initial state sampled. Set to " + stateMap);
	}

	public void dumpMj(int currentNode) {
		/*
		 * write the Mj look ahead graph for debugging policies
		 */
		if (this.mjDotDir != null) {
			for (int frameId : ((IPOMDP) this.l1Solver.f).multiFrameMJ.MJs.keySet()) {
				((IPOMDP) this.l1Solver.f).multiFrameMJ.MJs.get(frameId).writeDotFile(this.mjDotDir,
						"mj_" + frameId + "_step_" + currentNode + "_iter_" + this.iterId);
			}
		}

	}

	public int step(int currentNode) {

		try {

			/* Solve current step for solvers */
			if (this.l1Solver instanceof AlphaVectorPolicySolver) {
				this.l1Solver.f.setGlobals();
				((AlphaVectorPolicySolver) this.l1Solver).solveCurrentStep();
				((AlphaVectorPolicySolver) this.l1Solver).expansionStrategy.clearMem();

//				this.dumpMj(currentNode);
			}

			/* record actions and observations and all that at current step */

			// --------------------------------------------------------------------------------

			/* record the L1 step */
			this.l1Solver.f.setGlobals();

			double l1DiscountedReward = this.l1Solver.evaluatePolicy(1000, 5, false);

			DD currentL1Belief = this.getPolicyNode(currentNode).getBelief();
			String currentL1BeliefJson = this.l1Solver.f.getBeliefString(currentL1Belief);

			this.l1BeliefSequence.add(this.l1Solver.f.getBeliefString(currentL1Belief));
			LOGGER.info("At L1 belief " + this.l1BeliefSequence.get(this.l1BeliefSequence.size() - 1));

			/* optimal action for L1 */
			String l1Action = this.l1Solver.getActionForBelief(currentL1Belief);
			this.getPolicyNode(currentNode).setActName(l1Action);

			// ---------------------------------------------------------------------------------

			/* record L0 belief */
			DD currentL0Belief = null;
			String currentL0BeliefJson = "{}";

//			this.l0BeliefSequence.add(currentL0BeliefJson);
//			LOGGER.info("L0 belief is " 
//					+ this.l0BeliefSequence.get(this.l0BeliefSequence.size() - 1));

			/* optimal action for L0 */
			/* Show history first */
			System.out.println("\r\n==== HISTORY ====\r\n");

			for (int i = 0; i < this.l0ActionSequence.size(); i++) {
				System.out.println("Step: " + i + " Action: " + this.l0ActionSequence.get(i));
				System.out.println("Step: " + i + " Obs: " + String.join("\r\n", this.l0ObsSequence.get(i)));
				System.out.println();
			}

			System.out.println("\r\n==== END HISTORY ====\r\n");

			String l0Action = ((HumanAgentSolver) this.solver).getHumanAction();
			if (l0Action.contentEquals("EXIT")) {
				System.out.println("Ending simulation...");
				this.endSimulation();
			}
//			this.getPolicyNode(currentNode + 2).setActName(l0Action);

			// ----------------------------------------------------------------------------------

			/* record state */
//			DD currentState = this.states.get(this.states.size() - 1).toDD();
//			String stateJson = this.l1Solver.f.getBeliefString(currentState);
//			
//			this.stateSequence.add(
//					this.l1Solver.f.getBeliefString(currentState));
//			LOGGER.info("Real state is " + this.stateSequence.get(this.stateSequence.size() - 1));

			// ----------------------------------------------------------------------------------
//			}

			/* take action */
			new ProcessBuilder("clear").inheritIO().start().waitFor();
			System.out.println("Running action on target... May take a minute or two...");
			String[][] obs = this.multiAgentEnvStep(l1Action + "__" + l0Action);
			new ProcessBuilder("clear").inheritIO().start().waitFor();
			((HumanAgentSolver) this.solver).showObservation(String.join("\r\n", obs[1]));
			System.out.println("Preparing environment for next step." + " This will take around 5 mins...");

			/* record action and obs */
			this.l0ActionSequence.add(l0Action);
			this.l1ActionSequence.add(l1Action);

			this.l0ObsSequence.add(Arrays.toString(obs[1]));
			this.l1ObsSequence.add(Arrays.toString(obs[0]));

			// ------------------------------------------------------------------------------------

			/* compute L1 reward */
			this.l1Solver.f.setGlobals();

			this.l1ExpectedReward.add(l1DiscountedReward);
			LOGGER.info("Agent i's expected reward is " + this.l1ExpectedReward.get(this.l1ExpectedReward.size() - 1));

			/* step agent I */
			if (this.l1Solver instanceof AlphaVectorPolicySolver)
				((AlphaVectorPolicySolver) this.l1Solver).nextStep(l1Action, Arrays.asList(obs[0]));

			else
				this.l1Solver.f.step(currentL1Belief, l1Action, obs[0]);

			/* make next policy node for agent i */
			PolicyNode nextINode = new PolicyNode();
			nextINode.setBelief(this.l1Solver.f.getCurrentBelief());
			nextINode.setsBelief(this.l1Solver.f.getBeliefString(this.l1Solver.f.getCurrentBelief()));
			nextINode.setId(this.currentPolicyNodeCounter++);
			this.putPolicyNode(nextINode.getId(), nextINode);

			/* make state node here to maintain currentPolicyNodeCounter order */
//			PolicyNode nextStateNode = new PolicyNode();
//			nextStateNode.setId(this.currentPolicyNodeCounter++);
//			
//			// --------------------------------------------------------------------------------------
//			
//			double l0DiscountedReward = Double.NaN;
//			
//			this.l0ExpectedReward.add(l0DiscountedReward);
//			LOGGER.info("Agent j's expected reward is " 
//					+ this.l0ExpectedReward.get(
//							this.l0ExpectedReward.size() - 1));

			/* summarize the interaction */
//			if (this.summaryWriter != null) {
//				this.summarizeInteraction(
//						this.states.size(),
//						"{}",
//						currentL1BeliefJson, 
//						currentL0BeliefJson, 
//						l1Action, 
//						l0Action,
//						obs[0],
//						obs[1],
//						l1DiscountedReward,
//						Double.NaN);
//			}

//			/* make policy node for next belief of agent j */
//			PolicyNode nextJNode = new PolicyNode();
//			nextJNode.setBelief(null);
//			nextJNode.setsBelief("{}");
//			nextJNode.setId(this.currentPolicyNodeCounter++);
//			this.putPolicyNode(nextJNode.getId(), nextJNode);
//			
//			/* populate next state node */
//			nextStateNode.setBelief(this.states.get(this.states.size() - 1).toDD());
//			nextStateNode.setsBelief("{}");
//			this.putPolicyNode(nextStateNode.getId(), nextStateNode);

			// -------------------------------------------------------------------------------------

			/* make path */
			this.putEdge(currentNode, Arrays.asList(obs[0]), nextINode.getId());
//			this.putEdge(
//					currentNode + 1, 
//					Arrays.asList(new String[] {l1Action, l0Action}), 
//					nextStateNode.getId());
//			this.putEdge(currentNode + 2, Arrays.asList(obs[1]), nextJNode.getId());

			if (this.interactionOver)
				return -1;

//			if (l0Action.contentEquals("EXIT")) this.endSimulation();

			return nextINode.getId();
		}

		catch (ZeroProbabilityObsException o) {
			LOGGER.error("While running multi agent simulation " + o.getMessage());
			System.exit(-1);
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

//		if (!(this.solver instanceof HumanAgentSolver)) {

		LOGGER.debug("Agent i took action " + actions[0] + ", agent j took action " + actions[1]);

		/* get next state */
//		DDTree nextState = this.getNextState(action).toDDTree();
//		this.states.add(nextState);
//		}

		return this.doJointAction(action);
	}

	public String[][] doJointAction(String jointAction) {

		/* sample observation */
		String[] l1Obs = this.getL1Observation(jointAction);
		LOGGER.debug("L1 observation sampled from state is " + Arrays.toString(l1Obs));

		String[] l0Obs = this.getL0Observation(jointAction);
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

		if (this.l1Solver.f instanceof MAPOMDP || this.l1Solver.f.getType().contentEquals("POMDP")) {

			HashMap<String, DDTree> ODDTree = ((MAPOMDP) this.l1Solver.f).Oi.get(action);

			List<DD> O = new ArrayList<DD>();
			for (String o : ODDTree.keySet()) {
				O.add(OP.reorder(ODDTree.get(o).toDD()));
			}

			DD[] obsFunc = O.stream().toArray(DD[]::new);

			/* get observation distribution */
			DD obsDist = OP.addMultVarElim(
					ArrayUtils.add(obsFunc,
							OP.primeVars(this.states.get(this.states.size() - 1).toDD(), this.l1Solver.f.getNumVars())),
					this.l1Solver.f.getStateVarPrimeIndices());

			/* sample from distribution */
			int[][] obsConfig = OP.sampleMultinomial(obsDist, this.l1Solver.f.getObsVarPrimeIndices());

			return POMDP.configToStrings(obsConfig);
		}

		/* relevant varIndices */
		String[] actions = action.split("__");
		int[] varIndices = ArrayUtils.subarray(this.l1Solver.f.getStateVarPrimeIndices(), 0,
				((IPOMDP) this.l1Solver.f).MjVarPosition);

		/* create config structure to restrict Aj */
		int[] actVal = new int[] { this.solver.f.getActions().indexOf(actions[1]) + 1 };
		int[] actVarConfig = new int[] { ((IPOMDP) this.l1Solver.f).AjStartIndex };
		int[][] actConfig = IPOMDP.stackArray(actVarConfig, actVal);

		/* get observation distribution */
		DD obsDist = OP.addMultVarElim(
				ArrayUtils.add(this.l1Solver.f.getOiForAction(actions[0]),
						OP.primeVars(this.states.get(this.states.size() - 1).toDD(), this.l1Solver.f.getNumVars())),
				varIndices);

		obsDist = OP.restrict(obsDist, actConfig);

		/* sample from distribution */
		int[][] obsConfig = OP.sampleMultinomial(obsDist, this.l1Solver.f.getObsVarPrimeIndices());

		return IPOMDP.configToStrings(obsConfig);
	}

	public String[] getL0Observation(String action) {
		/*
		 * Sample observation from the state
		 */

		if (!(this.solver instanceof HumanAgentSolver))
			this.solver.f.setGlobals();

		String[] actions = action.split("__");

		/* get observation distribution */
		DD obsDist = OP.addMultVarElim(
				ArrayUtils.add(this.solver.f.getOiForAction(actions[1]),
						OP.primeVars(this.states.get(this.states.size() - 1).toDD(), this.solver.f.getNumVars())),
				this.solver.f.getStateVarPrimeIndices());

		/* sample from distribution */
		int[][] obsConfig = OP.sampleMultinomial(obsDist, this.solver.f.getObsVarPrimeIndices());

		return POMDP.configToStrings(obsConfig);
//		}
//		
//		else return new String[] {"human agent obs"};
	}

	@Override
	public DD getNextState(String action) {
		/*
		 * Computes next state from current state and given action
		 */

		String[] actions = action.split("__");
		int[] varIndices = ArrayUtils.subarray(this.l1Solver.f.getStateVarIndices(), 0,
				((IPOMDP) this.l1Solver.f).MjVarPosition);

		/* set context for L1 agent */
		this.l1Solver.f.setGlobals();
		DD currentState = this.states.get(this.states.size() - 1).toDD();

		/* first make state transition based on action */
		DD[] Ti = this.l1Solver.f.getTiForAction(actions[0]);

		DD nextStateDD = null;

		if (this.l1Solver.f.getType().contentEquals("IPOMDP")) {
			nextStateDD = OP.addMultVarElim(ArrayUtils.add(Ti, currentState), varIndices);
		}

		else {

			HashMap<String, DDTree> jointTi = ((MAPOMDP) this.l1Solver.f).Ti.get(action);

			List<DD> TiReconstructed = new ArrayList<DD>();
			for (String s : jointTi.keySet()) {
				TiReconstructed.add(OP.reorder(jointTi.get(s).toDD()));
			}

			nextStateDD = OP.addMultVarElim(ArrayUtils.add(TiReconstructed.stream().toArray(DD[]::new), currentState),
					ArrayUtils.subarray(this.l1Solver.f.getStateVarIndices(), 0,
							this.l1Solver.f.getStateVarIndices().length - 1));
		}

		/* restrict transition to Aj */
		int[] actVal = new int[] { ((IPOMDP) this.l1Solver.f).Aj.indexOf(actions[1]) + 1 };
		int[] actVarConfig = new int[] { ((IPOMDP) this.l1Solver.f).AjStartIndex };
		int[][] actConfig = IPOMDP.stackArray(actVarConfig, actVal);

		if (this.l1Solver.f.getType().contentEquals("IPOMDP"))
			nextStateDD = OP.restrict(nextStateDD, actConfig);

		nextStateDD = OP.primeVars(nextStateDD, -(this.l1Solver.f.getNumVars()));

		int[][] stateConfig = OP.sampleMultinomial(nextStateDD, this.l1Solver.f.getStateVarIndices());
		DD state = Config.convert2dd(stateConfig);

		LOGGER.debug("State transitioned");

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
			if (nextNode == -1)
				break;

			previousNode = nextNode;
		}

		this.logResults();
//		this.logToFile(fileName);
	}

	// ---------------------------------------------------------------------------------------------

	@Override
	public void logResults() {
		/*
		 * Writes all results to the logger
		 */

		LOGGER.info("Sim results:");
		LOGGER.info("I belief, state, J belief, Ai, Aj, Oi, Oj, ERi, ERj");

		for (int i = 0; i < this.l1BeliefSequence.size(); i++) {
			LOGGER.info("\"" + this.l1BeliefSequence.get(i).replace("\"", "\\\"") + "\"" + ", " + "\""
					+ this.stateSequence.get(i).replace("\"", "\\\"") + "\"" + ", " + "\""
					+ this.l0BeliefSequence.get(i).replace("\"", "\\\"") + "\"" + ", " + this.l1ActionSequence.get(i)
					+ ", " + this.l0ActionSequence.get(i) + ", " + "\"" + this.l1ObsSequence.get(i) + "\"" + ", " + "\""
					+ this.l0ObsSequence.get(i) + "\"" + ", " + this.l1ExpectedReward.get(i) + ", "
					+ this.l0ExpectedReward.get(i));
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
			Gson gsonHandler = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

			JsonArray recordsArray = new JsonArray();

			for (int i = 0; i < this.l1BeliefSequence.size(); i++) {

				/* init a new record */
				JsonObject record = new JsonObject();

				/* write belief of i */
				record.add("beliefI", gsonHandler.fromJson(this.l1BeliefSequence.get(i), JsonObject.class));

//				/* write belief of j */
//				record.add(
//						"beliefJ", 
//						gsonHandler.fromJson(
//								this.l0BeliefSequence.get(i), 
//								JsonObject.class));

//				/* write state */
//				record.add(
//						"state", 
//						gsonHandler.fromJson(
//								this.stateSequence.get(i), 
//								JsonObject.class));

				/* ThetaJ */
//				record.add("Theta_j", new JsonPrimitive(this.jFrameID));

				/* write action of i */
				record.add("Ai", new JsonPrimitive(this.l1ActionSequence.get(i)));

				/* write action of j */
				record.add("Aj", new JsonPrimitive(this.l0ActionSequence.get(i)));

				/* write observations of i */
				record.add("Oi", new JsonPrimitive(this.l1ObsSequence.get(i)));

				/* write observations of j */
				record.add("Oj", new JsonPrimitive(this.l0ObsSequence.get(i)));

//				/* write rewards of i */
//				record.add("ERi", 
//						new JsonPrimitive(this.l1ExpectedReward.get(i)));				
//				
//				/* write rewards of j */
//				record.add("ERj", 
//						new JsonPrimitive(this.l0ExpectedReward.get(i)));				

				recordsArray.add(record);
			}

			writer.println(gsonHandler.toJson(recordsArray));
			writer.flush();
			writer.close();

			if (this.summaryWriter != null)
				this.summaryWriter.close();
		}

		catch (Exception e) {
			LOGGER.error("While writing results to JSON file: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void summarizeInteraction(int numStep, String state, String l1belief, String l0belief, String l1Action,
			String l0Action, String[] l1Obs, String[] l0Obs, double l1Reward, double l0Reward) throws Exception {
		/*
		 * Makes a human readable summary of the details of the interaction
		 */
		int i = numStep - 1;

		LOGGER.debug("Interaction step " + i + " summary:");
		this.summaryWriter.println("Interaction step " + i + " summary:");
		this.summaryWriter.println();

		LOGGER.debug("State is,");
		this.summaryWriter.println("State is,");
		JsonElement stateJsonTree = JsonParser.parseString(state);
		JsonObject stateJsonObject = stateJsonTree.getAsJsonObject();
		for (String stateVar : stateJsonObject.keySet())
			this.summarizeBelief(stateVar, stateJsonObject.get(stateVar).getAsJsonObject(), false);

		this.summaryWriter.println();

		LOGGER.debug("Agent i at L1 believes,");
		this.summaryWriter.println("Agent i at L1 believes,");
		JsonElement l1JsonTree = JsonParser.parseString(l1belief);
		JsonObject l1JsonObject = l1JsonTree.getAsJsonObject();
		for (String l1StateVar : l1JsonObject.keySet()) {

			if (l1StateVar.contentEquals("M_j"))
				continue;

			this.summarizeBelief(l1StateVar, l1JsonObject.get(l1StateVar).getAsJsonObject(), true);
		}

		this.summarizeMjBelief(l1JsonObject.get("M_j").getAsJsonArray());
		this.summaryWriter.println();

		LOGGER.debug("Agent i takes action " + l1Action + " and observes " + Arrays.toString(l1Obs));
		this.summaryWriter.println("Agent i takes action " + l1Action + " and observes " + Arrays.toString(l1Obs));
		this.summaryWriter.println("i expects average discounted reward " + l1Reward);
		this.summaryWriter.println();

		LOGGER.debug("Agent j at L0 believes,");
		this.summaryWriter.println("Agent j at L0 believes,");
		JsonElement l0JsonTree = JsonParser.parseString(l0belief);
		JsonObject l0JsonObject = l0JsonTree.getAsJsonObject();
		for (String l0StateVar : l0JsonObject.keySet())
			this.summarizeBelief(l0StateVar, l0JsonObject.get(l0StateVar).getAsJsonObject(), true);

		this.summaryWriter.println();

		LOGGER.debug("Agent j takes action " + l0Action + " and observes " + Arrays.toString(l0Obs));
		LOGGER.debug("Interaction ends");
		this.summaryWriter.println("Agent j takes action " + l0Action + " and observes " + Arrays.toString(l0Obs));
		this.summaryWriter.println("j expects average discounted reward " + l0Reward);
		this.summaryWriter.println();
		this.summaryWriter.println("Interaction ends");
		this.summaryWriter.println();
		this.summaryWriter.flush();
	}

	private void summarizeBelief(String name, JsonObject jsonBelief, boolean showProbs) {
		/*
		 * Makes a summary of the belief over states
		 */
		String jsonVal = "";
		float maxProb = 0;

		for (String val : jsonBelief.keySet()) {

			float valProb = jsonBelief.get(val).getAsFloat();

			if (valProb > maxProb) {
				jsonVal = val;
				maxProb = valProb;
			}
		}

		if (showProbs) {
			LOGGER.debug(name + " is likely " + jsonVal + " with probability " + maxProb);
			this.summaryWriter.println(name + " is likely " + jsonVal + " with probability " + maxProb);
		}

		else {
			LOGGER.debug(name + " is " + jsonVal);
			this.summaryWriter.println(name + " is " + jsonVal);
		}
	}

	private void summarizeMjBelief(JsonArray jsonBelief) {
		/*
		 * Makes a summary of the belief over states
		 */
		JsonObject mostProbableMj = null;
		float maxProb = 0;

		for (JsonElement obj : jsonBelief) {

			float valProb = obj.getAsJsonObject().get("prob").getAsFloat();
//			LOGGER.debug(valProb + " for " + obj);
			if (valProb > maxProb) {
				mostProbableMj = obj.getAsJsonObject();
				maxProb = valProb;
			}
		}

		String mostProbableAj = mostProbableMj.get("model").getAsJsonObject().get("A_j").getAsString();
		mostProbableMj = mostProbableMj.get("model").getAsJsonObject().get("belief_j").getAsJsonObject();

		for (String varName : mostProbableMj.keySet()) {

			String jsonVal = "";
			float maxValProb = 0;

			for (String val : mostProbableMj.get(varName).getAsJsonObject().keySet()) {

				float valProb = mostProbableMj.get(varName).getAsJsonObject().get(val).getAsFloat();

				if (valProb > maxValProb) {
					jsonVal = val;
					maxValProb = valProb;
				}
			}

			LOGGER.debug("i believes that j believes " + varName + " is" + " likely " + jsonVal + " with probability "
					+ maxValProb + " with probability " + maxProb);

			this.summaryWriter.println("i believes that j believes " + varName + " is" + " likely " + jsonVal
					+ " with probability " + maxValProb + " with probability " + maxProb);
		}

		LOGGER.debug("This leads agent i to believe that agent j will perfrom action " + mostProbableAj
				+ " with a probability " + maxProb);
		this.summaryWriter.println("This leads agent i to believe that agent j will perfrom action " + mostProbableAj
				+ " with a probability " + maxProb);
	}

}
