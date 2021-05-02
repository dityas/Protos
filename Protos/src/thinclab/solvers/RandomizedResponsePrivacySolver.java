/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.ddinterface.DDTree;
import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.Config;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public class RandomizedResponsePrivacySolver extends BaseSolver {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5138358665121595077L;
	private List<DecisionProcess> frames = new ArrayList<>();
	private List<BaseSolver> solvers = new ArrayList<>();
	private int intendedFrameIndex = 0;
	private float p;
	private int rounds = 1;
	private int backups = 100;
	private BaseSolver mainSolver = null;
	
	private static final Logger LOGGER = Logger.getLogger(RandomizedResponsePrivacySolver.class);
	
	public RandomizedResponsePrivacySolver(
			List<DecisionProcess> frames, 
			int intendedFrame, 
			float probability,
			int rounds, 
			int backups) {
		
		this.frames.addAll(frames);
		this.intendedFrameIndex = intendedFrame;
		this.p = probability;
		this.rounds = rounds;
		this.backups = backups;
		
		this.f = this.frames.remove(this.intendedFrameIndex);
		
		LOGGER.info("RandomizedResponsePrivacy Agent initialized with " + this.frames.size() + " frames");
	}

	@Override
	public String getActionForBelief(DD belief) {
		
		Random rng = new Random();
		
		if (rng.nextFloat() < this.p)
			return this.mainSolver.getActionForBelief(belief);
		
		else if (this.solvers.size() > 1) {
			return this.solvers.get(rng.nextInt(this.solvers.size() - 1)).getActionForBelief(belief);
		}
		
		else {
			return this.solvers.get(0).getActionForBelief(belief);
		}
	}

	@Override
	public boolean hasSolution() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void solveForBeliefs(List<DD> beliefs) {
		LOGGER.error("Method not available for this solver");
		System.exit(-1);
	}

	@Override
	public void solve() {
		
		for (DecisionProcess dp: this.frames) {
			
			// Set context for new frame
			dp.setGlobals();
			
			// Solve new frame
			BaseSolver solver = OfflineSymbolicPerseus.createSolverWithSSGAExpansion(dp, 10, 100, this.rounds, this.backups);
			solver.solve();
			
			// Store solved frame
			this.solvers.add(solver);
		}
		
		// Solve main frame
		this.f.setGlobals();
		
		this.mainSolver = OfflineSymbolicPerseus.createSolverWithSSGAExpansion(this.f, 10, 100, this.rounds, this.backups);
		this.mainSolver.solve();
	}

	@Override
	public double evaluatePolicy(int trials, int evalDepth, boolean verbose) {
		/*
		 * Run given number of trials of evaluation upto the given depth and return
		 * average reward
		 * 
		 * ****** This is completely based on Hoey's evalPolicyStationary function
		 */
		
		List<Double> rewards = new ArrayList<Double>();
		DDTree currentBeliefTree = this.f.getCurrentBelief().toDDTree();
		
		try {
			
			Global.clearHashtables();
			
			for (int n = 0; n < trials; n++) {
				DD currentBelief = currentBeliefTree.toDD();
				
				double totalReward = 0.0;
				double totalDiscount = 1.0;
				
				int[][] stateConfig = OP.sampleMultinomial(currentBelief, this.f.getStateVarIndices());
				
				for (int t = 0; t < evalDepth; t++) {
					
					String action = this.getActionForBelief(currentBelief);
					
					if (verbose) {
						LOGGER.debug("Best Action in state " 
								+ Arrays.toString(POMDP.configToStrings(stateConfig)) + " "
								+ "is " + action);
					}
					
					/* evaluate action */
					double currentReward = OP.eval(this.f.getRewardFunctionForAction(action), stateConfig);
					totalReward = totalReward + (totalDiscount * currentReward);
					totalDiscount = totalDiscount * ((POMDP) this.f).discFact;
					
					/* get next state and generate observation */
					DD[] TforS = OP.restrictN(this.f.getTiForAction(action), stateConfig);
					int[][] nextStateConfig = OP.sampleMultinomial(TforS, this.f.getStateVarPrimeIndices());
					
					DD[] obsDist = 
							OP.restrictN(
									this.f.getOiForAction(action), 
									POMDP.concatenateArray(stateConfig, nextStateConfig));
					
					/* sample obs from distribution */
					int[][] obsConfig = 
							OP.sampleMultinomial(
									obsDist, 
									this.f.getObsVarPrimeIndices());
					
					String[] obs = POMDP.configToStrings(obsConfig);
					
					currentBelief = this.f.beliefUpdate(currentBelief, action, obs);
					stateConfig = Config.primeVars(nextStateConfig, -this.f.getNumVars());
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
	public void nextStep(String action, List<String> obs) throws ZeroProbabilityObsException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getActionAtCurrentBelief() {
		// TODO Auto-generated method stub
		return null;
	}

}
