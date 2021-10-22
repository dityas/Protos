/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers.basiccyberdeceptionsolvers;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.decisionprocesses.IPOMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class ReactiveSolver extends BaseSolver {

	/*
	 * Reactive solver.
	 * 
	 * Simulates a basic deception strategy. If the agent believes the attacker is
	 * of a particular type, it deploys the relevant deception.
	 */

	private boolean deceptionDeployed = false;
	private boolean fakeVulnDeployed = false;

	private static final long serialVersionUID = -9200255582382484951L;
	private static final Logger LOGGER = LogManager.getLogger(ReactiveSolver.class);

	// ---------------------------------------------------------------------------------------

	public ReactiveSolver(IPOMDP ipomdp) {

		this.f = ipomdp;
		LOGGER.info("Reactive solver initialised");
	}

	// ---------------------------------------------------------------------------------------

	@Override
	public String getActionForBelief(DD belief) {
		return this.getDeceptionActionFromBelief(
				((IPOMDP) this.f).toMapWithTheta(this.f.getCurrentBelief()).get("Theta_j"));
	}

	@Override
	public boolean hasSolution() {
		return true;
	}

	@Override
	public void solveForBeliefs(List<DD> beliefs) {
		LOGGER.warn("Calling a solve method on DefaultPolicySolver. Nothing will happen");
	}

	@Override
	public void solve() {
		LOGGER.warn("Calling a solve method on DefaultPolicySolver. Nothing will happen");
	}

	@Override
	public float evaluatePolicy(int trials, int evalDepth, boolean verbose) {
		return 0.0f;
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
		return null;
	}

	private String getDeceptionActionFromBelief(HashMap<String, Float> frameBelief) {

		if (frameBelief.get("theta/0") > 0.51 && !this.deceptionDeployed) {
			this.deceptionDeployed = true;
			return "DEPLOY_HONEY_FILES";
		}

		else if (!this.fakeVulnDeployed) {
			this.fakeVulnDeployed = true;
			return "DEPLOY_FAKE_VULN_INDICATORS";
		}

		else
			return "NOP";
	}

}
