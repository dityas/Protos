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
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.representations.StructuredTree;
import thinclab.solvers.BaseSolver;
import thinclab.solvers.OnlineSolver;

/*
 * @author adityas
 *
 */
public class Simulation extends StructuredTree {
	
	/*
	 * Base class for defining simulations
	 * 
	 * All simulations should generally be able to write dot files representing the simulation
	 * trace and the beliefs of the decision process as it was stepping through it. All simulations
	 * should also record some stats about the expected reward at each state and the cumulative
	 * reward.
	 * 
	 * The sub classing from structured tree is done because it already has the data structures to
	 * store any arbitrary path through the conditional plan and write dot files. So we'll just 
	 * use that and extend it
	 * 
	 */
	
	/* some lists for storing run stats */
	List<String> beliefSequence = new ArrayList<String>();
	List<String> actionSequence = new ArrayList<String>();
	List<String> obsSequence = new ArrayList<String>();
	List<Double> immediateRewardSequence = new ArrayList<Double>();
	List<Double> cumulativeRewardSequence = new ArrayList<Double>();
	
	private static final long serialVersionUID = 4431341545771143166L;
	private static final Logger LOGGER = Logger.getLogger(Simulation.class);
	
	// ------------------------------------------------------------------------------------------
	
	public void step(BaseSolver solver) {
		
		try {
			
			DD currentBelief = solver.f.getCurrentBelief();
			
			/* optimal action */
			String action = solver.getActionForBelief(currentBelief);
			
			String[] obs = this.sampleObservation(solver.f, currentBelief, action);
			
			/* record the step */
			this.beliefSequence.add(solver.f.getBeliefString(currentBelief));
			LOGGER.info("At belief " + this.beliefSequence.get(this.beliefSequence.size() - 1));
			
			this.actionSequence.add(action);
			LOGGER.info("Agent took action " 
					+ this.actionSequence.get(
							this.actionSequence.size() - 1));
			
			this.obsSequence.add(Arrays.toString(obs));
			LOGGER.info("Agent observed " + this.obsSequence.get(this.obsSequence.size() - 1));
			
			/* compute reward */
			double reward = 
					OP.factoredExpectationSparse(
							solver.f.factorBelief(currentBelief), 
							solver.f.getRewardFunctionForAction(action));
			
			this.immediateRewardSequence.add(reward);
			LOGGER.info("Immediate expected reward is " 
					+ this.immediateRewardSequence.get(
							this.immediateRewardSequence.size() - 1));
			
			double lastReward = 0.0;
			
			if (this.cumulativeRewardSequence.size() > 0)
				lastReward = 
					this.cumulativeRewardSequence.get(this.cumulativeRewardSequence.size() - 1);
			
			this.cumulativeRewardSequence.add(lastReward + reward);
			LOGGER.info("Cumulative reward is " + (lastReward + reward));
			
			if (solver instanceof OnlineSolver)
				((OnlineSolver) solver).nextStep(action, Arrays.asList(obs));
			
			else
				solver.f.step(currentBelief, action, obs);
		}
		
		catch (ZeroProbabilityObsException o) {
			
		}
		
		catch (Exception e) {
			LOGGER.error("While running belief update " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public String[] sampleObservation(DecisionProcess DP, DD belief, String action) {
		/*
		 * Sample observation based on observation probabilities
		 */
		
		/* sample observation */
		DD obsDist = DP.norm(belief, action);
		int[] obsIndices = null;
		
		if (DP.getType().contentEquals("IPOMDP"))
			obsIndices = ((IPOMDP) DP).obsIVarPrimeIndices;
		
		else obsIndices = ((POMDP) DP).primeObsIndices;
		
		int[][] obsConfig = OP.sampleMultinomial(obsDist, obsIndices);
		String[] obs = new String[obsConfig[0].length];
		for (int varI = 0; varI < obsConfig[0].length; varI ++) {
			obs[varI] = Global.valNames[obsConfig[0][varI] - 1][obsConfig[1][varI] - 1];
		}
		
		return obs;
	}

}
