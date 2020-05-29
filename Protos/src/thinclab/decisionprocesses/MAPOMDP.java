/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.decisionprocesses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import thinclab.belief.BeliefOps;
import thinclab.ddinterface.DDTree;
import thinclab.ddinterface.DDTreeLeaf;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.Action;
import thinclab.legacy.Config;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.parsers.IPOMDPParser;
import thinclab.solvers.AlphaVectorPolicySolver;

/*
 * @author adityas
 *
 */
public class MAPOMDP extends IPOMDP {

	/*
	 * Adds frames and static distributions for opponents to extend POMDPs to multi agent
	 * settings at same level
	 */
	
	private DDTree ajGivenThetajDDTree;
	private DD AjGivenTheta;
	private DDTree sameThetaDDTree;
	private DDTree initialThetaBelief;
	
	public HashMap<String, HashMap<String, DDTree>> jointTiStager = 
			new HashMap<String, HashMap<String, DDTree>>();
	public HashMap<String, HashMap<String, DDTree>> jointOiStager = 
			new HashMap<String, HashMap<String, DDTree>>();
	
	// ---------------------------------------------------------------------------------
	
	private static final long serialVersionUID = 3391360719456978181L;
	private static final Logger LOGGER = Logger.getLogger(MAPOMDP.class);
	
	// ---------------------------------------------------------------------------------
	
	public MAPOMDP(IPOMDPParser parsedFrame, int mjSearchDepth) {
		
		super(parsedFrame, 2, mjSearchDepth);
		LOGGER.info("MAPOMDP initialized");
		
		/* make same theta DD */
		this.sameThetaDDTree = 
				this.ddMaker.getDDTreeFromSequence(new String[] {"Theta_j", "Theta_j'"});
		
		try {
			
			/* initial belief for theta */
			this.initialThetaBelief = this.ddMaker.getDDTreeFromSequence(new String[] {"Theta_j"});
			for (String frameName: this.initialThetaBelief.children.keySet())
				this.initialThetaBelief.setValueAt(frameName, 1.0 / this.initialThetaBelief.children.size());
			
			/* same theta transition */
			for (String frame: this.sameThetaDDTree.children.keySet()) {
				this.sameThetaDDTree.setDDAt(
						Arrays.asList(new String[] {frame, frame}), new DDTreeLeaf(1.0));
			}
			
			/* get static aj distribution */
			this.makeAjGivenThetaj();
			
			/* remake dynamics */
			this.remakeTi();
			this.remakeOi();
			this.remakeRi();
			
			/* redo indices */
			this.reindexVars();
			
			/* set dynamics */
			this.setDynamics();
			
			this.bOPs = new BeliefOps(this);
			
			/* set initial belief */
			this.initialBeliefs.clear();
			this.currentBelief = null;
			
			this.initialBelState = 
					OP.reorder(OP.mult(
							OP.reorder(this.currentStateBeliefs.get(0).toDD()), 
							OP.reorder(this.initialThetaBelief.toDD())));
			
			LOGGER.debug("Initial belief set to " + this.initialBelState);
			
			this.initialBeliefs.add(this.initialBelState);
			this.initBeliefDdTree = this.initialBelState.toDDTree();
			this.currentBelief = this.initialBelState;
		}
		
		catch (Exception e) {
			LOGGER.error("While initializing MAPOMDP: ");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	// ------------------------------------------------------------------------------------
	
	public void makeAjGivenThetaj() throws Exception {
		/*
		 * Creates a static distribution of Aj given Thetaj
		 */
		
		DDTree AjGivenThetaj = this.ddMaker.getDDTreeFromSequence(new String[] {"Theta_j"});

		for (int frameId: this.multiFrameMJ.MJs.keySet()) {
			LOGGER.debug("Extracting Aj distribution from frame " + frameId);
			AlphaVectorPolicySolver solver = 
					(AlphaVectorPolicySolver) this.multiFrameMJ.MJs.get(frameId).solver;
			
			DD[] aVecs = solver.getAlphaVectors();
			HashMap<String, Double> ajProbs = new HashMap<String, Double>();
			int numAlpha = aVecs.length;
			int[] policy = solver.getPolicy();
			LOGGER.debug("frame " + frameId + " contains " + numAlpha + " vectors");
			LOGGER.debug("Policy is " + Arrays.toString(solver.getPolicy()));
			
			/* 
			 * populate number of alpha vector for each action in J and then divide
			 * by total to get static aj distribution
			 */
			for (int p = 0; p < aVecs.length; p++) {
				
				String ajName = solver.getFramework().getActions().get(policy[p]);
				
				if (ajProbs.containsKey(ajName)) ajProbs.put(ajName, ajProbs.get(ajName) + 1.0);
				else ajProbs.put(ajName, 1.0);
			}
			
			DDTree ajTree = this.ddMaker.getDDTreeFromSequence(new String[] {"A_j"});
			
			for (String aj: ajTree.children.keySet()) 
				ajTree.setValueAt(aj, (ajProbs.get(aj) / (float) numAlpha));
			
			AjGivenThetaj.setDDAt("theta/" + frameId, ajTree);
		}
		
		this.ajGivenThetajDDTree = AjGivenThetaj;
		this.AjGivenTheta = OP.reorder(this.ajGivenThetajDDTree.toDD());
	}
	
	@Override
	public void setDynamics() {
		/*
		 * Starts building up DDs which define system dynamics based on Ti and Oi
		 */
		
		LOGGER.debug("Setting dynamics");
		
		this.nActions = this.A.size();
		this.actions = new Action[nActions];
		this.uniquePolicy = new boolean[nActions];

		qFn = new DD[nActions];

		for (int a = 0; a < nActions; a++) {
			
			String actName = this.A.get(a);
			actions[a] = new Action(actName);
			
			/* Re order T and O as arrays according to variable sequence */
			HashMap<String, DDTree> Ti_a = this.Ti.get(actName);
			List<DD> TiaArray = this.S.stream()
					.map(s -> OP.reorder(Ti_a.get(s.name).toDD()))
					.collect(Collectors.toList());
			
			HashMap<String, DDTree> Oi_a = this.Oi.get(actName);
			List<DD> OiaArray = this.Omega.stream()
					.map(o -> OP.reorder(Oi_a.get(o.name).toDD()))
					.collect(Collectors.toList());
			
			actions[a].addTransFn(TiaArray.toArray(new DD[TiaArray.size()]));
			actions[a].addObsFn(OiaArray.toArray(new DD[OiaArray.size()]));
			
			actions[a].rewFn = OP.reorder(this.costMap.get(actName).toDD());
//					OP.sub(OP.reorder(this.R.toDD()), OP.reorder(this.costMap.get(actName).toDD()));
			actions[a].buildRewTranFn();
			actions[a].rewFn = 
					OP.addMultVarElim(actions[a].rewTransFn, primeVarIndices);
		}
		
		/*
		 * Max and Min reward
		 */
		double maxVal = Double.NEGATIVE_INFINITY;
		double minVal = Double.POSITIVE_INFINITY;
		
		for (int a = 0; a < nActions; a++) {
			maxVal = Math.max(maxVal, OP.maxAll(OP.addN(actions[a].rewFn)));
			minVal = Math.min(minVal, OP.minAll(OP.addN(actions[a].rewFn)));
		}
		
		maxRewVal = maxVal / (1 - discFact);
		
		/*
		 * Set Tolerance
		 */
		double maxDiffRew = maxVal - minVal;
		double maxDiffVal = maxDiffRew / (1 - Math.min(0.95, discFact));
		tolerance = 1e-5 * maxDiffVal;
	}
	
	// ------------------------------------------------------------------------------------
	
	public void remakeTi() {
		/*
		 * Sum out Aj according to the static distribution
		 */
		
		for (String action: this.getActions()) {
			
			DD[] oldTi = this.currentTi.get(action);
//			DDTree[] newTi = new DDTree[oldTi.length];
			
			List<String> states = 
					this.S.subList(0, this.MjVarPosition).stream()
						.map(s -> s.name)
						.collect(Collectors.toList());
			
			HashMap<String, DDTree> stateTiMap = new HashMap<String, DDTree>(); 
			
			for (int s = 0; s < states.size(); s++) {
				stateTiMap.put(
						states.get(s), 
						OP.addMultVarElim(
								new DD[] {oldTi[s], this.AjGivenTheta}, 
								this.AjStartIndex).toDDTree()); 
			}
			
			stateTiMap.put("Theta_j", this.sameThetaDDTree.getCopy());
			this.Ti.put(action, stateTiMap);
		}
		
	}
	
	public void remakeOi() {
		/*
		 * Sum out Aj according to the static distribution
		 */
		
		for (String action: this.getActions()) {
			
			DD[] oldOi = this.currentOi.get(action);
//			DDTree[] newOi = new DDTree[oldOi.length];
			
			List<String> O = 
					this.Omega.stream()
						.filter(s -> !s.name.contains("_j"))
						.map(f -> f.name)
						.collect(Collectors.toList());
			
			HashMap<String, DDTree> obsOiMap = new HashMap<String, DDTree>();
			
			for (int o = 0; o < O.size(); o++) {
				obsOiMap.put(
						O.get(o), 
						OP.addMultVarElim(
								new DD[] {oldOi[o], this.AjGivenTheta}, 
								this.AjStartIndex).toDDTree()); 
			}
			
			this.Oi.put(action, obsOiMap);
		}
		
	}
	
	public void remakeRi() {
		/*
		 * Reconstruct reward function according to theta
		 */
		
		for (String Ai : this.currentRi.keySet()) {
			
			DD RSMj = 
					OP.addMultVarElim( 
							new DD[] {this.AjGivenTheta, OP.reorder(this.costMap.get(Ai).toDD())},
							this.AjStartIndex);
			
			LOGGER.debug("For Ai=" + Ai + " R(S,Mj) has vars " 
					+ Arrays.toString(RSMj.getVarSet()));
//			LOGGER.debug("Ri is: " + RSMj.toDDTree());
			
			this.costMap.put(Ai, RSMj.toDDTree());
		}
	}
	
	@Override
	public HashMap<String, DD> makeRi() {
		/*
		 * Construct's i's reward function based on joint actions (Mj)
		 */
		
		LOGGER.debug("Making reward funtion");
		
		/* First condition rewards on Mj for each Ai */
		HashMap<String, DD> Ri = new HashMap<String, DD>(); 
		HashMap<String, DD> actionCosts = new HashMap<String, DD>(); 
		
		for (String Ai : this.Ai) {
			
			DDTree ajDDTree = 
					this.ddMaker.getDDTreeFromSequence(new String[] {"A_j"});
			
			for (String action : ajDDTree.children.keySet()) {
				
				try {
					
					ajDDTree.setDDAt(
							action, 
							OP.sub(
									OP.reorder(this.R.toDD()), 
									OP.reorder(this.costMap.get(
											Ai + "__" 
											+ action)
												.toDD())).toDDTree());
				} 
				
				catch (Exception e) {
					LOGGER.error("While making cost for action " + Ai + " : " + e.getMessage());
					e.printStackTrace();
					System.exit(-1);
				}
			} /* for each aj */
			
			actionCosts.put(Ai, OP.reorder(ajDDTree.toDD()));
			this.costMap.put(Ai, ajDDTree);
		} /* for each Ai */
		
		LOGGER.debug("Done conditioning costs on Aj");
		
		/*
		 * Build rewTranFn like in POMDP setDynamics.
		 * 
		 * This computation is essentially the expected reward considering Aj for each Ai.
		 * 
		 * ER(S, Ai) = Sumout[Aj] f(S, Ai, Aj) 
		 */
		for (String Ai : actionCosts.keySet()) {
			
			DD RSMj = 
					OP.addMultVarElim( 
							new DD[] {this.currentAjGivenMj, actionCosts.get(Ai)},
							this.AjStartIndex);
			
			LOGGER.debug("For Ai=" + Ai + " R(S,Mj) has vars " 
					+ Arrays.toString(RSMj.getVarSet()));
//			LOGGER.debug("Ri is: " + RSMj.toDDTree());
			
			Ri.put(Ai, RSMj);
		}
		
		return Ri;
	}
	
	public void reindexVars() {
		/*
		 * Fix indices after deleting Mj and Aj
		 */
		
		this.A.clear();
		this.A.addAll(this.Ai);
		
		this.Omega = 
				this.Omega.stream().filter(s -> !s.name.contains("_j")).collect(Collectors.toList());
		
		this.S.remove(this.AjVarStartPosition);
		this.S.remove(this.MjVarPosition);
		this.S.get(this.S.size() - 1).setId(this.S.size() - 1);
		this.thetaVarPosition = this.S.size() - 1;
		
		this.commitVariables();
		
		this.currentAjGivenMj = null;
		this.currentMjPGivenMjOjPAj = null;
		this.currentOi = null;
		this.currentRi = null;
		this.currentTi = null;
		this.currentTauXPAjGivenMjXPThetajGivenMj = null;
		this.currentThetajGivenMj = null;
		this.MjVarIndex = -1;
		this.MjVarPosition = -1;
		this.AjVarStartPosition = -1;
		this.AjStartIndex = -1;
	}
	
	// --------------------------------------------------------------------------------------
	
	@Override
	public HashMap<String, DD[]> makeTi() {
		/*
		 * Constructs i's transition function.
		 * 
		 * The DBN structure is:
		 * 
		 * 		[Mj] -------------------.
		 * 								|
		 * 								v
		 * 		[S]  ----------------> [S']
		 * 
		 */
		
		/*
		 * The parsed Ti is in the form of a joint action transition function of the form -
		 * (Ai - Aj) -> S -> f(S', S)
		 * 
		 * We need to map that to the form Ai -> f(S', Aj, S)
		 */
		LOGGER.debug("Making Ti");
		
		HashMap<String, DD[]> Ti = 
				new HashMap<String, DD[]>();
		
		List<String> S = 
				this.S.subList(0, this.MjVarPosition).stream()
					.map(f -> f.name)
					.collect(Collectors.toList());

		/* For each action */
		for (String Ai : this.Ai) {

			DD[] ddTrees = new DD[this.MjVarPosition];
			
			for (String s : S) {
				
				DDTree ajDDTree = 
						this.ddMaker.getDDTreeFromSequence(new String[] {"A_j"});
				
				/* for all of J's actions */
				for (String aj : this.Aj) {
					
					String ajPath = Ai + "__" + aj;
					
					DDTree tiGivenaj = this.Ti.get(ajPath).get(s);
					
					try {
						/* avoid passing the original actionCombination because it is mutable */
						ajDDTree.setDDAt(aj, tiGivenaj);
					}
					
					catch (Exception e) {
						LOGGER.error("While setting " + tiGivenaj + " at " + ajDDTree);
						e.printStackTrace();
						System.exit(-1);
					}
				}
				
				LOGGER.debug("Made f(S', "
						+ this.S.stream()
							.filter(v -> v.name.contains("A_j"))
							.map(i -> i.name)
							.collect(Collectors.toList())
						+ ", S) for S=" + s + " and Ai=" + Ai);
				
				DD TiForS = OP.reorder(ajDDTree.toDD());
				
				ddTrees[S.indexOf(s)] = TiForS;
			}
			
			Ti.put(Ai, ddTrees);
		}
		
		LOGGER.debug("Finished making Ti");
		
		return Ti;
	}
	
	@Override
	public String getType() {
		return "POMDP";
	}
	
	@Override
	public String getBeliefString(DD belief) {
		/*
		 * Returns current belief as a string
		 * 
		 * Mostly useful printing out the beliefs for policy graphs and trees
		 */
		
		/* initialize JSON handler */
		Gson gsonHandler = 
				new GsonBuilder()
					.disableHtmlEscaping()
					.create();
		
		/* get belief hashmap */
		HashMap<String, HashMap<String, Float>> map = this.toMap(belief);
		
		/* create JSON container */
		JsonObject jsonObject = new JsonObject();
		
		/* add the states */
		for (String key : map.keySet()) {
			jsonObject.add(key, gsonHandler.toJsonTree(map.get(key)));
		}
		
		return gsonHandler.toJson(jsonObject);
	}
	
	@Override
	public void setTi(String action, DD[] Ti) {
		/*
		 * Sets the given Ti as the transition function for the action
		 */
		
		int actId = this.getActions().indexOf(action);
		
		this.actions[actId].addTransFn(Ti);
		this.actions[actId].buildRewTranFn();
		this.actions[actId].rewFn = 
				OP.addMultVarElim(
						actions[actId].rewTransFn, 
						primeVarIndices);
	}
	
	@Override
	public void step(
			DD belief, 
			String action, 
			String[] obs) throws 
	
	ZeroProbabilityObsException, 
	VariableNotFoundException {
		
		/*
		 * Performs a static belief update from current belief by taking action and observing obs
		 * 
		 * The belief space is transformed according to the next belief. 
		 */
		
		LOGGER.info("Taking action " + action + "\r\n"
				+ " at belief " + this.toMap(belief) + "\r\n"
				+ " with observation " + Arrays.toString(obs));
		
		/* perform belief update */
		DD nextBelief = 
				this.beliefUpdate( 
						belief, 
						action, 
						obs);
		
		LOGGER.debug("Next belief is " + this.toMap(nextBelief));
		
		this.currentBelief = nextBelief;
	}
	
	@Override
	public List<String> getStateVarNames() {
		return this.S.stream().map(s -> s.name).collect(Collectors.toList());
	}
	
	@Override
	public List<String> getObsVarNames() {
		return this.Omega.stream().map(o -> o.name).collect(Collectors.toList());
	}
	
	@Override
	public List<DD> getInitialBeliefs() {
		return this.initialBeliefs;
	}
	
	@Override
	public int[] getStateVarIndices() {
		return this.varIndices;
	}
	
	@Override
	public DD getCurrentBelief() {
		/*
		 * In case of online solvers, the POMDP will have to maintain a reference
		 * to its current belief
		 */
		return this.currentBelief;
	}
	
	@Override
	public int[] getObsVarIndices() {
		return this.obsIndices;
	}
	
	@Override
	public DD[] getTiForAction(String action) {
		
		return this.actions[this.getActions().indexOf(action)].transFn;
	}
	
	@Override
	public DD[] getOiForAction(String action) {
		
		return this.actions[this.getActions().indexOf(action)].obsFn;
	}
	
	@Override
	public DD getRewardFunctionForAction(String action) {
		/*
		 * Returns the reward function for the given action
		 */
		
		int actId = this.getActions().indexOf(action);
		
		return this.actions[actId].rewFn;
		
	}

	@Override
	public int[] getStateVarPrimeIndices() {
		// TODO Auto-generated method stub
		return this.primeVarIndices;
	}

	@Override
	public int[] getObsVarPrimeIndices() {
		// TODO Auto-generated method stub
		return this.primeObsIndices;
	}
	
	@Override
	public int getNumVars() {
		return this.nStateVars + this.nObsVars;
	}
	
	@Override
	public double evaluatePolicy(
			DD[] alphaVectors, int[] policy, int trials, int evalDepth, boolean verbose) {
		/*
		 * Run given number of trials of evaluation upto the given depth and return
		 * average reward
		 * 
		 * ****** This is completely based on Hoey's evalPolicyStationary function
		 */
		
		List<Double> rewards = new ArrayList<Double>();
		DDTree currentBeliefTree = this.getCurrentBelief().toDDTree();
		
		try {
			
			Global.clearHashtables();
			
			for (int n = 0; n < trials; n++) {
				DD currentBelief = currentBeliefTree.toDD();
				
				double totalReward = 0.0;
				double totalDiscount = 1.0;
				
				int[][] stateConfig = OP.sampleMultinomial(currentBelief, this.getStateVarIndices());
				
				for (int t = 0; t < evalDepth; t++) {
					
					String action = 
							DecisionProcess.getActionFromPolicy(
									this, currentBelief, alphaVectors, policy);
					
					if (verbose) {
						LOGGER.debug("Best Action in state " 
								+ Arrays.toString(POMDP.configToStrings(stateConfig)) + " "
								+ "is " + action);
					}
					
					/* evaluate action */
					double currentReward = OP.eval(this.getRewardFunctionForAction(action), stateConfig);
					totalReward = totalReward + (totalDiscount * currentReward);
					totalDiscount = totalDiscount * this.discFact;
					
					/* get next state and generate observation */
					DD[] TforS = OP.restrictN(this.getTiForAction(action), stateConfig);
					int[][] nextStateConfig = OP.sampleMultinomial(TforS, this.getStateVarPrimeIndices());
					
					DD[] obsDist = 
							OP.restrictN(
									this.getOiForAction(action), 
									POMDP.concatenateArray(stateConfig, nextStateConfig));
					
					/* sample obs from distribution */
					int[][] obsConfig = 
							OP.sampleMultinomial(
									obsDist, 
									this.getObsVarPrimeIndices());
					
					String[] obs = POMDP.configToStrings(obsConfig);
					
					currentBelief = this.beliefUpdate(currentBelief, action, obs);
					stateConfig = Config.primeVars(nextStateConfig, -this.getNumVars());
				}
				
				if (verbose)
					LOGGER.debug("Run: " + n + " total reward is " + totalReward);
				
				if ((n % 1000) == 0)
					LOGGER.debug("Finished " + n + " trials,"
							+ " avg. reward is: " 
							+ rewards.stream().mapToDouble(r -> r).average().orElse(Double.NaN));
				
				rewards.add(totalReward);
			}
			
			Global.clearHashtables();
		}
		
		catch (ZeroProbabilityObsException o) {
			LOGGER.error("Evaluation crashed: " + o.getMessage());
			return -1;
		}
		
		catch (Exception e) {
			LOGGER.error("Evaluation crashed: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
			
			return -1;
		}
		
		double avgReward = rewards.stream().mapToDouble(r -> r).average().getAsDouble();
		return avgReward;
	}

	@Override
	public double evaluateDefaultPolicy(
			String defaultAction, int trials, int evalDepth, boolean verbose) {
		
		List<Double> rewards = new ArrayList<Double>();
		DDTree currentBeliefTree = this.getCurrentBelief().toDDTree();
		
		try {
			
			Global.clearHashtables();
			
			for (int n = 0; n < trials; n++) {
				DD currentBelief = currentBeliefTree.toDD();
				
				double totalReward = 0.0;
				double totalDiscount = 1.0;
				
				int[][] stateConfig = OP.sampleMultinomial(currentBelief, this.getStateVarIndices());
				
				for (int t = 0; t < evalDepth; t++) {
					
					String action = defaultAction;
					
					if (verbose) {
						LOGGER.debug("Taking default action in state " 
								+ Arrays.toString(POMDP.configToStrings(stateConfig)) + " "
								+ "is " + action);
					}
					
					/* evaluate action */
					double currentReward = OP.eval(this.getRewardFunctionForAction(action), stateConfig);
					totalReward = totalReward + (totalDiscount * currentReward);
					totalDiscount = totalDiscount * this.discFact;
					
					/* get next state and generate observation */
					DD[] TforS = OP.restrictN(this.getTiForAction(action), stateConfig);
					int[][] nextStateConfig = OP.sampleMultinomial(TforS, this.getStateVarPrimeIndices());
					
					DD[] obsDist = 
							OP.restrictN(
									this.getOiForAction(action), 
									POMDP.concatenateArray(stateConfig, nextStateConfig));
					
					/* sample obs from distribution */
					int[][] obsConfig = 
							OP.sampleMultinomial(
									obsDist, 
									this.getObsVarPrimeIndices());
					
					String[] obs = POMDP.configToStrings(obsConfig);
					
					currentBelief = this.beliefUpdate(currentBelief, action, obs);
					stateConfig = Config.primeVars(nextStateConfig, -this.getNumVars());
				}
				
				if (verbose)
					LOGGER.debug("Run: " + n + " total reward is " + totalReward);
				
				if ((n % 1000) == 0)
					LOGGER.debug("Finished " + n + " trials,"
							+ " avg. reward is: " 
							+ rewards.stream().mapToDouble(r -> r).average().orElse(Double.NaN));
				
				rewards.add(totalReward);
			}
			
			Global.clearHashtables();
		}
		
		catch (ZeroProbabilityObsException o) {
			LOGGER.error("Evaluation crashed: " + o.getMessage());
			return -1;
		}
		
		catch (Exception e) {
			LOGGER.error("Evaluation crashed: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
			
			return -1;
		}
		
		double avgReward = rewards.stream().mapToDouble(r -> r).average().getAsDouble();
		return avgReward;
	}

	@Override
	public double evaluateRandomPolicy(int trials, int evalDepth, boolean verbose) {
		
		List<Double> rewards = new ArrayList<Double>();
		DDTree currentBeliefTree = this.getCurrentBelief().toDDTree();
		
		try {
			
			Global.clearHashtables();
			
			for (int n = 0; n < trials; n++) {
				DD currentBelief = currentBeliefTree.toDD();
				
				double totalReward = 0.0;
				double totalDiscount = 1.0;
				
				int[][] stateConfig = OP.sampleMultinomial(currentBelief, this.getStateVarIndices());
				
				for (int t = 0; t < evalDepth; t++) {
					
					Random rnd = new Random();
					String action = this.getActions().get(rnd.nextInt(this.getActions().size()));
					
					if (verbose) {
						LOGGER.debug("Taking random action in state " 
								+ Arrays.toString(POMDP.configToStrings(stateConfig)) + " "
								+ "is " + action);
					}
					
					/* evaluate action */
					double currentReward = OP.eval(this.getRewardFunctionForAction(action), stateConfig);
					totalReward = totalReward + (totalDiscount * currentReward);
					totalDiscount = totalDiscount * this.discFact;
					
					/* get next state and generate observation */
					DD[] TforS = OP.restrictN(this.getTiForAction(action), stateConfig);
					int[][] nextStateConfig = OP.sampleMultinomial(TforS, this.getStateVarPrimeIndices());
					
					DD[] obsDist = 
							OP.restrictN(
									this.getOiForAction(action), 
									POMDP.concatenateArray(stateConfig, nextStateConfig));
					
					/* sample obs from distribution */
					int[][] obsConfig = 
							OP.sampleMultinomial(
									obsDist, 
									this.getObsVarPrimeIndices());
					
					String[] obs = POMDP.configToStrings(obsConfig);
					
					currentBelief = this.beliefUpdate(currentBelief, action, obs);
					stateConfig = Config.primeVars(nextStateConfig, -this.getNumVars());
				}
				
				if (verbose)
					LOGGER.debug("Run: " + n + " total reward is " + totalReward);
				
				if ((n % 100) == 0)
					LOGGER.debug("Finished " + n + " trials,"
							+ " avg. reward is: " 
							+ rewards.stream().mapToDouble(r -> r).average().orElse(Double.NaN));
				
				rewards.add(totalReward);
			}
			
			Global.clearHashtables();
		}
		
		catch (ZeroProbabilityObsException o) {
			LOGGER.error("Evaluation crashed: " + o.getMessage());
			return -1;
		}
		
		catch (Exception e) {
			LOGGER.error("Evaluation crashed: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
			
			return -1;
		}
		
		double avgReward = rewards.stream().mapToDouble(r -> r).average().getAsDouble();
		return avgReward;
	}
}
