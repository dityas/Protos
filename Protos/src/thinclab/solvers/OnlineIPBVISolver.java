/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.AlphaVector;
import thinclab.legacy.DD;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public class OnlineIPBVISolver extends AlphaVectorPolicySolver {

	/*
	 * Implements interactive point based value iteration for solving IPOMDPs
	 */

	private static final long serialVersionUID = 4278938278257692053L;

	/* Variables to hold point based values */
	float[][] currentPointBasedValues;
	float[][] newPointBasedValues;
	float bestImprovement;
	float worstDecline;

	/* Store local reference to the IPOMDP */
	IPOMDP ipomdp;

	/* IPBVI hyper params */
	int dpBackups;

	private static final Logger LOGGER = LogManager.getLogger(OnlineIPBVISolver.class);

	// -----------------------------------------------------------------------------------------

	public OnlineIPBVISolver(
			IPOMDP ipomdp, 
			BeliefRegionExpansionStrategy b, 
			int maxRounds, 
			int dpBackups) {

		/* Call super */
		super(ipomdp, b);

		/* get rounds and dp backups */
		this.ipomdp = ipomdp;
		this.maxRounds = maxRounds;
		this.dpBackups = dpBackups;

		/* initialize unique policy */
		this.uniquePolicy = new boolean[this.ipomdp.getActions().size()];
	}

	// -------------------------------------------------------------------------------------------
	
	@Override
	public void setFramework(DecisionProcess ipomdp) {
		/*
		 * Replaces the IPOMDP reference with given arg
		 */
		this.ipomdp = (IPOMDP) ipomdp;
		super.setFramework(ipomdp);
	}

	@Override
	public void solveForBeliefs(List<DD> beliefs) {
		/*
		 * Use online IPBVI to get solution for given beliefs
		 */

		LOGGER.debug("Solving for " + beliefs.size() + " belief points.");

		DD[][] factoredBeliefRegion = this.f.factorBeliefRegion(beliefs);

		/* try running IPBVI */
		try {

			this.IPBVI(100, this.dpBackups, this.dpBackups, factoredBeliefRegion, beliefs);

			this.currentPointBasedValues = null;
			this.newPointBasedValues = null;
		}

		catch (Exception e) {
			LOGGER.error("While running solver: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	@Override
	public void solve() {
		LOGGER.error("the solve method is not applicable for this type of solver");
	}

	@Override
	public void nextStep(String action, List<String> obs) throws ZeroProbabilityObsException {

		try {
			/*
			 * Transition the IPOMDP to a new belief by taking the specified action and
			 * observing obs.
			 */
			this.ipomdp.step(
					this.ipomdp.getCurrentBelief(), 
					action, obs.toArray(new String[obs.size()]));

			/* Reset the search to new initial beliefs */
			this.expansionStrategy.resetToNewInitialBelief();
			
			this.setInitPolicy();
		}

		catch (VariableNotFoundException e) {
			LOGGER.error("While taking action " 
					+ action + " and observing " 
					+ obs + " got error: " + e.getMessage());
			System.exit(-1);
		}
	}

	public void computeMaxMinImprovement(int beliefRegionLen) {

		float imp;
		this.bestImprovement = Float.NEGATIVE_INFINITY;
		this.worstDecline = Float.POSITIVE_INFINITY;

		for (int j = 0; j < beliefRegionLen; j++) {
			/*
			 * find biggest improvement at this belief point
			 */
			imp = OP.max(this.newPointBasedValues[j], this.numNewAlphaVectors)
					- OP.max(this.currentPointBasedValues[j]);

			if (imp > this.bestImprovement)
				this.bestImprovement = imp;
			if (imp < this.worstDecline)
				this.worstDecline = imp;
		}
	}
	
	@Override
	public String getActionAtCurrentBelief() {

		return this.getActionForBelief(this.ipomdp.getCurrentBelief());
	}
	
	@Override
	public String getActionForBelief(DD belief) {
		/*
		 * Return best action at given belief
		 */
		return DecisionProcess.getActionFromPolicy(
				this.f, belief, this.alphaVectors, this.policy);
	}

	// --------------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	public void IPBVI(
			int maxAlpha, int firstStep, int nSteps, DD[][] beliefRegion, List<DD> beliefs)
			throws ZeroProbabilityObsException, VariableNotFoundException {

		float bellmanErr;
		float[] onezero = { 0 };
		float steptolerance = 0.0f;

		int maxAlphaSetSize = maxAlpha;

		bellmanErr = (float) (20 * this.ipomdp.tolerance);

		this.currentPointBasedValues = 
				OP.factoredExpectationSparseNoMem(beliefRegion, this.alphaVectors);

		DD[] primedV;
		float maxAbsVal = 0;
		
		/* For computation stats */
		List<Long> backupTimes = new ArrayList<Long>();

		for (int stepId = firstStep; stepId < firstStep + nSteps; stepId++) {

			steptolerance = (float) ipomdp.tolerance;

			primedV = new DD[this.alphaVectors.length];

			for (int i = 0; i < this.alphaVectors.length; i++) {
				primedV[i] = 
						OP.primeVars(this.alphaVectors[i], ipomdp.S.size() + ipomdp.Omega.size());
			}

			maxAbsVal = 
					Math.max(
							OP.maxabs(
									IPOMDP.concatenateArray(
											OP.maxAllN(this.alphaVectors), 
											OP.minAllN(this.alphaVectors))), 
							1e-10f);

			int nDpBackups = 0;

			/*
			 * could be one more than the maximum number at most
			 */
			this.newAlphaVectors = new AlphaVector[maxAlphaSetSize + 1];
			this.newPointBasedValues = new float[beliefRegion.length][maxAlphaSetSize + 1];
			this.numNewAlphaVectors = 0;

			AlphaVector newVector;

			float[] newValues;
			float improvement;

			/*
			 * we allow the number of new alpha vectors to get one bigger than the maximum
			 * allowed size, since we may be able to cull more than one alpha vector when
			 * trimming, bringing us back below the cutoff
			 */

			for (int i = 0; i < beliefRegion.length; i++) {
//				Global.newHashtables();

				long beforeBackup = System.nanoTime();
					
				/* dpBackup */
				newVector = 
						AlphaVector.dpBackup2(
								this.ipomdp, 
								beliefs.get(i), 
								beliefRegion[i],
								primedV, 
								maxAbsVal, 
								this.alphaVectors.length);
				
				long afterBackup = System.nanoTime();
				
				/* record backup computation time */
				backupTimes.add((afterBackup - beforeBackup));

				newVector.alphaVector = OP.approximate(newVector.alphaVector,
						(float) (bellmanErr * (1 - this.ipomdp.discFact) / 2.0f), onezero);
				newVector.setWitness(i);

				nDpBackups = nDpBackups + 1;

				/* merge and trim */
				newValues = OP.factoredExpectationSparseNoMem(beliefRegion, newVector.alphaVector);

				if (this.numNewAlphaVectors < 1)
					improvement = Float.POSITIVE_INFINITY;

				else
					improvement = 
						OP.max(
								OP.sub(
										newValues, 
										OP.getMax(
												this.newPointBasedValues, 
												this.numNewAlphaVectors)));

				if (improvement > ipomdp.tolerance) {
					for (int belid = 0; belid < beliefRegion.length; belid++)
						this.newPointBasedValues[belid][this.numNewAlphaVectors] = newValues[belid];

					this.newAlphaVectors[this.numNewAlphaVectors] = newVector;
					this.numNewAlphaVectors++;
				}
			}

			computeMaxMinImprovement(beliefRegion.length);

			/* save data and copy over new to old */
			this.alphaVectors = new DD[this.numNewAlphaVectors];
			this.currentPointBasedValues = 
					new float[this.newPointBasedValues.length][this.numNewAlphaVectors];

			this.policy = new int[this.numNewAlphaVectors];
			this.policyvalue = new float[this.numNewAlphaVectors];

			for (int j = 0; j < ipomdp.Ai.size(); j++)
				this.uniquePolicy[j] = false;

			for (int j = 0; j < this.numNewAlphaVectors; j++) {

				this.alphaVectors[j] = this.newAlphaVectors[j].alphaVector;
				this.policy[j] = this.newAlphaVectors[j].actId;
				this.policyvalue[j] = this.newAlphaVectors[j].value;
				this.uniquePolicy[this.policy[j]] = true;
			}

			for (int j = 0; j < beliefRegion.length; j++) {
				System.arraycopy(
						this.newPointBasedValues[j], 
						0, this.currentPointBasedValues[j], 
						0, this.numNewAlphaVectors);
			}

//			bellmanErr = Math.min(10, Math.max(this.bestImprovement, -this.worstDecline));
			bellmanErr = Math.max(this.bestImprovement, -this.worstDecline);
			float errorVar = this.getErrorVariance((float) bellmanErr);
			
			/* compute average backup time */
			float avgTime = (float) 
					backupTimes.stream()
						.map(i -> (float) i)
						.mapToDouble(Float::valueOf)
						.average()
						.orElse(Float.NaN);
			
			backupTimes.clear();
			
			LOGGER.info("I: " + stepId 
					+ "  B ERROR: " + String.format(Locale.US, "%.03f", bellmanErr) 
					+ "\t USED/TOTAL BELIEFS: " + nDpBackups 
					+ "/" + beliefRegion.length 
					+ "  A VECTORS: " + this.alphaVectors.length
					+ "  Avg. backup: " + (avgTime / 1000000) + " msec");

			if (stepId % 100 < 1)
				continue;

			if (bellmanErr < 0.01) {
				LOGGER.warn("BELLMAN ERROR LESS THAN 0.01. COVERGENCE! SOFTWARE VERSION 7.0... "
						+ "LOOKING AT LIFE THROUGH THE EYES OF A TIRED HEART.");
				break;
			}

			if (stepId > 20 && errorVar < 0.0001) {
				LOGGER.warn("DECLARING APPROXIMATE CONVERGENCE AT ERROR: " + bellmanErr
						+ " BECAUSE OF LOW ERROR VARIANCE " + errorVar);
				break;
			}

		}

	}
}
