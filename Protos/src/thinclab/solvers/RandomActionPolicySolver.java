/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public class RandomActionPolicySolver extends BaseSolver {
	
	/*
	 * Suggests a random action for every belief
	 */
	
	private Random rng = new Random();

	private static final long serialVersionUID = -5867916136386899113L;
	private static final Logger LOGGER = LogManager.getLogger(RandomActionPolicySolver.class);
	
	// --------------------------------------------------------------------------------------
	
	public RandomActionPolicySolver(DecisionProcess DP) {
		this.f = DP;
		LOGGER.info("initialised Random action policy solver");
	}
	
	// --------------------------------------------------------------------------------------

	@Override
	public String getActionForBelief(DD belief) {
		return this.f.getActions().get(this.rng.nextInt(this.f.getActions().size()));
	}

	@Override
	public boolean hasSolution() {
		return true;
	}

	@Override
	public void solveForBeliefs(List<DD> beliefs) {
		LOGGER.warn("Calling a solve method on a random solver. Nothing will happen");
	}

	@Override
	public void solve() {
		LOGGER.warn("Calling a solve method on a random solver. Nothing will happen");
	}

	@Override
	public float evaluatePolicy(int trials, int evalDepth, boolean verbose) {
		return this.f.evaluateRandomPolicy(trials, evalDepth, verbose);
	}

	@Override
	public void nextStep(String action, List<String> obs) throws ZeroProbabilityObsException {
		
		try {
			this.f.step(this.f.getCurrentBelief(), action, obs.stream().toArray(String[]::new));
		} 
		
		catch (Exception e) {
			LOGGER.error("While stepping the solver " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public String getActionAtCurrentBelief() {
		return this.f.getActions().get(this.rng.nextInt(this.f.getActions().size()));
	}

}
