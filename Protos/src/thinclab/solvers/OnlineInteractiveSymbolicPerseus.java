/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.belief.InteractiveBelief;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.AlphaVector;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.legacy.RandomPermutation;

/*
 * @author adityas
 *
 */
public class OnlineInteractiveSymbolicPerseus extends OnlineIPBVISolver {
	/*
	 * Value iteration solver for IPOMDPs with finite look ahead horizon
	 * 
	 * 
	 */
	
	private static final long serialVersionUID = 3646574134759239287L;
	
	private static final Logger logger = 
			Logger.getLogger(OnlineInteractiveSymbolicPerseus.class);
	
	// --------------------------------------------------------------------------------------

	public OnlineInteractiveSymbolicPerseus(
			IPOMDP ipomdp, 
			BeliefRegionExpansionStrategy b,
			int maxRounds,
			int dpBackups) {
		/*
		 * Initialize with an IPOMDP
		 */
		super(ipomdp, b, maxRounds,dpBackups);

	}
	
	// ---------------------------------------------------------------------------------------
	
	@Override
	public void solveForBeliefs(List<DD> beliefs) {
		/*
		 * Use online symbolic perseus to get solution for given beliefs
		 */
		
		logger.debug("Solving for " + beliefs.size() + " belief points.");
		
		DD[][] factoredBeliefRegion = 
				InteractiveBelief.factorInteractiveBeliefRegion(
						(IPOMDP) this.f, 
						beliefs);
		
		/* Make a default alphaVectors as rewards to start with */
		this.alphaVectors = 
				this.ipomdp.currentRi.values().stream()
					.map(a -> OP.reorder(a))
					.collect(Collectors.toList())
					.toArray(new DD[this.ipomdp.currentRi.size()]);
		
		/* try running interactive symbolic perseus */
		try {
			
			for (int r = 0; r < this.maxRounds; r++) {
				
				boundedPerseusStartFromCurrent(
						1000, 
						r * this.dpBackups, 
						this.dpBackups, 
						factoredBeliefRegion,
						false);
				
			}
		}
		
		catch (Exception e) {
			logger.error("While running solver: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void boundedPerseusStartFromCurrent(
			int maxAlpha, 
			int firstStep,
			int nSteps,
			DD[][] beliefRegion,
			boolean debug) throws ZeroProbabilityObsException, VariableNotFoundException {
		
		double bellmanErr;
		double[] onezero = { 0 };
		double steptolerance;

		int maxAlphaSetSize = maxAlpha;

		bellmanErr = 20 * this.ipomdp.tolerance;
		
		this.currentPointBasedValues = 
				OP.factoredExpectationSparseNoMem(
						beliefRegion, this.alphaVectors);
		
		if (debug) {
			
			logger.debug("printing belief region");
			
			for (int i = 0; i < beliefRegion.length; i++) {
				logger.debug("Belief:" + i+ " " 
						+ InteractiveBelief.toStateMap(
								this.ipomdp, OP.reorder(OP.multN(beliefRegion[i]))));
			}
			logger.debug("Ri : " + this.ipomdp.currentRi);
		}
		
		DD[] primedV;
		double maxAbsVal = 0;
		
		for (int stepId = firstStep; stepId < firstStep + nSteps; stepId++) {
			
			if (debug) {
				logger.debug("STEP:=====================================================================");
				logger.debug("A vecs are: " + Arrays.toString(this.alphaVectors));
				logger.debug("Ri : " + this.ipomdp.currentRi);
			}
			
			steptolerance = ipomdp.tolerance;

			primedV = new DD[this.alphaVectors.length];
			
			for (int i = 0; i < this.alphaVectors.length; i++) {
				primedV[i] = 
						OP.primeVars(
								this.alphaVectors[i], 
								ipomdp.S.size() + ipomdp.Omega.size());
			}
			
			maxAbsVal = 
					Math.max(
							OP.maxabs(
									IPOMDP.concatenateArray(
											OP.maxAllN(this.alphaVectors), 
											OP.minAllN(this.alphaVectors))), 1e-10);

			int count = 0;
			int choice;
			int nDpBackups = 0;
			
			RandomPermutation permutedIds = new RandomPermutation(
					Global.random, beliefRegion.length, false);
			
			this.newAlphaVectors = new AlphaVector[maxAlphaSetSize + 1];
			this.newPointBasedValues = new double[beliefRegion.length][maxAlphaSetSize + 1];
			this.numNewAlphaVectors = 0;
						
			AlphaVector newVector;
			double[] diff = new double[beliefRegion.length];
			double[] maxcurrpbv;
			double[] maxnewpbv;
			double[] newValues;
			double improvement;

			/*
			 * we allow the number of new alpha vectors to get one bigger than the maximum allowed size, 
			 * since we may be able to cull more than one alpha vector when trimming, bringing us back 
			 * below the cutoff
			 */
			int numIter = 0;

			while (this.numNewAlphaVectors < maxAlphaSetSize
					&& !permutedIds.isempty()) {
				
				if (nDpBackups >= 2 * this.alphaVectors.length) {
					this.computeMaxMinImprovement(beliefRegion);
					if (this.bestImprovement > this.ipomdp.tolerance
							&& this.bestImprovement > -2 * this.worstDecline) {
						
						if (debug) {
							logger.warn("Breaking because bestImprovement " 
									+ ipomdp.bestImprovement + ""
									+ "> tolerance " + ipomdp.tolerance 
									+ " && bestImprovement " + ipomdp.bestImprovement 
									+ " > -2 * worstDecline " + (-2 * ipomdp.worstDecline));
						}
						
						break;
					}
				}

				Global.newHashtables();
				count = count + 1;
				
				if (this.numNewAlphaVectors == 0)
					choice = 0;
				
				else {

					maxcurrpbv = OP.getMax(this.currentPointBasedValues,
							permutedIds.permutation);
					maxnewpbv = OP.getMax(this.newPointBasedValues,
							this.numNewAlphaVectors, permutedIds.permutation);
					permutedIds.getNewDoneIds(maxcurrpbv, maxnewpbv,
							steptolerance);
					diff = permutedIds.getDiffs(maxcurrpbv, maxnewpbv,
							steptolerance);
					
					if (permutedIds.isempty()) {
						
						if (debug) logger.warn("Breaking because no belief points...");
						
						break;
					}
					
					choice = OP.sampleMultinomial(diff);
				}

				int i = permutedIds.getSetDone(choice);
				
				/* count belief point used */
				numIter += 1;
				
				if (this.numNewAlphaVectors < 1
						|| (OP.max(this.newPointBasedValues[i], this.numNewAlphaVectors)
								- OP.max(this.currentPointBasedValues[i]) < steptolerance)) {
					
					/* dpBackup */
					newVector = 
							AlphaVector.dpBackup(
									this.ipomdp,
									beliefRegion[i], 
									primedV, 
									maxAbsVal,
									this.alphaVectors.length);

					newVector.alphaVector = OP.approximate(
							newVector.alphaVector, bellmanErr * (1 - ipomdp.discFact)
									/ 2.0, onezero);
					newVector.setWitness(i);

					nDpBackups = nDpBackups + 1;
					
					/* merge and trim */
					newValues = 
							OP.factoredExpectationSparseNoMem(
									beliefRegion, 
									newVector.alphaVector);	
						
					if (this.numNewAlphaVectors < 1)
						improvement = Double.POSITIVE_INFINITY; 
					
					else
						improvement = 
							OP.max(
									OP.sub(
											newValues, 
											OP.getMax(
													this.newPointBasedValues, 
													this.numNewAlphaVectors)));
					
					if (improvement > ipomdp.tolerance) {
						
						if (debug) {
							logger.debug("Improvement after backup is " + improvement);
							logger.debug("Adding the new Alpha Vector with vars " 
									+ Arrays.toString(newVector.alphaVector.getVarSet()));
						}

						for (int belid = 0; belid < beliefRegion.length; belid++) {
							this.newPointBasedValues[belid][this.numNewAlphaVectors] = 
								newValues[belid];
						}
						
						this.newAlphaVectors[this.numNewAlphaVectors] = newVector;
						this.numNewAlphaVectors++;
					}
				}
			}

			this.computeMaxMinImprovement(beliefRegion);

			/*
			 * save data and copy over new to old
			 */
			this.alphaVectors = new DD[this.numNewAlphaVectors];
			this.currentPointBasedValues = 
					new double[this.newPointBasedValues.length][this.numNewAlphaVectors];

			this.policy = new int[this.numNewAlphaVectors];
			this.policyvalue = new double[this.numNewAlphaVectors];
			
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
						0, 
						this.currentPointBasedValues[j], 
						0, 
						this.numNewAlphaVectors);
			}

			bellmanErr = Math.min(10, Math.max(this.bestImprovement, -this.worstDecline));
			float errorVar = this.getErrorVariance((float) bellmanErr);
			
			logger.info("I: " + stepId 
					+ " \tB ERROR: " + String.format(Locale.US, "%.03f", bellmanErr) 
					+ " \tUSED/TOTAL BELIEFS: " + numIter + "/" + beliefRegion.length
					+ " \tA VECTORS: " + this.alphaVectors.length);
			
			if (stepId % 100 < 5)
				continue;
			
			if (bellmanErr < 0.01) {
				logger.warn("BELLMAN ERROR LESS THAN 0.01. COVERGENCE! SOFTWARE VERSION 7.0... "
						+ "LOOKING AT LIFE THROUGH THE EYES OF A TIRED HEART.");
				
				if (debug) this.logAlphaVectors();
				break;
			}
			
			if (stepId > 75 && errorVar < 0.0000001) {
				logger.warn("DECLARING APPROXIMATE CONVERGENCE AT ERROR: " + bellmanErr
						+ " BECAUSE OF LOW ERROR VARIANCE " + errorVar);
				break;
			}
			
		}

	}
	
	// ---------------------------------------------------------------------------------------------
	
	public IPOMDP getIPOMDP() {
		/*
		 * Getter to access the IPOMDP
		 */
		return this.ipomdp;
	}
	
	public DD[] getAlphaVectors() {
		/*
		 * Returns the alpha vectors
		 */
		return this.alphaVectors;
	}
	
	public void logAlphaVectors() {
		
		for (int i = 0; i < this.ipomdp.policy.length; i++) {
			logger.debug("Alpha Vector " + i + " is " + this.alphaVectors[i] +
					" for action " + this.ipomdp.getActions().get(this.ipomdp.policy[i]));
		}
	}
}
