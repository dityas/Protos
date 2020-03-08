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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.AlphaVector;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.legacy.RandomPermutation;
import thinclab.utils.Diagnostics;

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
	
	private static final Logger LOGGER = 
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
		
		DD[] beliefsArray = beliefs.stream().toArray(DD[]::new);
		beliefs.clear();
		beliefs = null;
		
		LOGGER.debug("Solving for " + beliefsArray.length + " belief points."); 
		
		/* Make a default alphaVectors as rewards to start with */
		this.alphaVectors = 
				this.ipomdp.currentRi.values().stream()
					.map(a -> OP.reorder(a))
					.collect(Collectors.toList())
					.toArray(new DD[this.ipomdp.currentRi.size()]);
		
		/* try running interactive symbolic perseus */
		try {
				
			boundedPerseusStartFromCurrent(
					10, 
					0, 
					this.dpBackups,
					beliefsArray,
					false);

			this.currentPointBasedValues = null;
			this.newPointBasedValues = null;
			
			this.alphaVectors = this.bestAlphaVectors;
			this.policy = this.bestPolicy;

		}
		
		catch (Exception e) {
			LOGGER.error("While running solver: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void boundedPerseusStartFromCurrent(
			int maxAlpha, 
			int firstStep,
			int nSteps,
			DD[] unfactoredBeliefRegion,
			boolean debug) throws ZeroProbabilityObsException, VariableNotFoundException {
		
		double bellmanErr;
		double steptolerance;
		
		int numBeliefs = unfactoredBeliefRegion.length;
		
		int[] ipomdpVars = 
				ArrayUtils.subarray(
						((IPOMDP) this.f).stateVarIndices, 
						0, 
						((IPOMDP) this.f).thetaVarPosition);

		int maxAlphaSetSize = maxAlpha;
		bellmanErr = 20 * this.ipomdp.tolerance;
		
		this.currentPointBasedValues = 
				OP.dotProduct(
						unfactoredBeliefRegion, this.alphaVectors, ipomdpVars);
		
		LOGGER.debug("Computed PBVs");
		
		Diagnostics.logMemConsumption("After PBV computation");
		
		if (debug) {
			
			LOGGER.debug("printing belief region");
			
			for (int i = 0; i < numBeliefs; i++) {
				LOGGER.debug("Belief:" + i+ " " 
						+ this.ipomdp.toMap(unfactoredBeliefRegion[i]));
			}
			LOGGER.debug("Ri : " + this.ipomdp.currentRi);
		}
		
		DD[] primedV;
		double maxAbsVal = 0;
		
		for (int stepId = firstStep; stepId < firstStep + nSteps; stepId++) {
			
			/* if caches are huge, clear them, unless you get your hands on 128 GB RAM */
			if (Global.multHashtable.size() > 5000000)
				Global.clearHashtables();
			
			long totalMem = Runtime.getRuntime().totalMemory();
			long freeMem = Runtime.getRuntime().freeMemory();
			
			if (((totalMem - freeMem) / 1000000000) > 40) {
				LOGGER.debug("JVM consuming more than 40 GB. Clearing caches");
				Global.clearHashtables();
				System.gc();
			}
			
			if (debug) {
				LOGGER.debug("STEP:=====================================================================");
				LOGGER.debug("A vecs are: " + Arrays.toString(this.alphaVectors));
				LOGGER.debug("Ri : " + this.ipomdp.currentRi);
			}
			
			steptolerance = ipomdp.tolerance;

			primedV = new DD[this.alphaVectors.length];
			
			for (int i = 0; i < this.alphaVectors.length; i++) {
				primedV[i] = 
						OP.primeVars(
								this.alphaVectors[i], 
								ipomdp.getNumVars());
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
					Global.random, numBeliefs, false);
			
			this.newAlphaVectors = new AlphaVector[maxAlphaSetSize + 1];
			this.newPointBasedValues = new double[numBeliefs][maxAlphaSetSize + 1];
			this.numNewAlphaVectors = 0;
						
			AlphaVector newVector;
			double[] diff = new double[numBeliefs];
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
					this.computeMaxMinImprovement(numBeliefs);
					if (this.bestImprovement > this.ipomdp.tolerance
							&& this.bestImprovement > -2 * this.worstDecline) {
						
						if (debug) {
							LOGGER.warn("Breaking because bestImprovement " 
									+ ipomdp.bestImprovement + ""
									+ "> tolerance " + ipomdp.tolerance 
									+ " && bestImprovement " + ipomdp.bestImprovement 
									+ " > -2 * worstDecline " + (-2 * ipomdp.worstDecline));
						}
						
						break;
					}
				}

//				Global.newHashtables();
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
						
						if (debug) LOGGER.warn("Breaking because no belief points...");
						
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
					
					long beforeBackup = System.nanoTime();
					
					/* dpBackup */
					newVector = 
							AlphaVector.dpBackup2(
									this.ipomdp,
									unfactoredBeliefRegion[i],
									/*beliefRegion[i],*/
									primedV,
									maxAbsVal,
									this.alphaVectors.length);
					
					long afterBackup = System.nanoTime();
				
					/* record backup computation time */
					Diagnostics.BACKUP_TIME.add((afterBackup - beforeBackup));

					newVector.alphaVector = OP.approximate(
							newVector.alphaVector, bellmanErr * (1 - ipomdp.discFact)
									/ 2.0, new double[] {0});
					
//					newVector.alphaVector = OP.approximate(newVector.alphaVector, 0.001);
					
					newVector.setWitness(i);

					nDpBackups = nDpBackups + 1;
					
					/* merge and trim */
					newValues = 
							OP.dotProduct(
									unfactoredBeliefRegion, 
									newVector.alphaVector, ipomdpVars);	
						
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
							LOGGER.debug("Improvement after backup is " + improvement);
							LOGGER.debug("Adding the new Alpha Vector with vars " 
									+ Arrays.toString(newVector.alphaVector.getVarSet()));
						}

						for (int belid = 0; belid < numBeliefs; belid++) {
							this.newPointBasedValues[belid][this.numNewAlphaVectors] = 
								newValues[belid];
						}
						
						this.newAlphaVectors[this.numNewAlphaVectors] = newVector;
						this.numNewAlphaVectors++;
					}
				}
			}

			this.computeMaxMinImprovement(numBeliefs);

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

			for (int j = 0; j < unfactoredBeliefRegion.length; j++) {
				System.arraycopy(
						this.newPointBasedValues[j], 
						0, 
						this.currentPointBasedValues[j], 
						0, 
						this.numNewAlphaVectors);
			}

			bellmanErr = Math.max(this.bestImprovement, -this.worstDecline);
			float errorVar = this.getErrorVariance((float) bellmanErr);
			
			LOGGER.info("I: " + stepId 
					+ " \tB ERROR: " + String.format(Locale.US, "%.03f", bellmanErr) 
					+ " \tUSED/TOTAL BELIEFS: " + numIter + "/" + numBeliefs
					+ " \tA VECTORS: " + this.alphaVectors.length
					+ " \tAPPROX. CONV PATIENCE: " + this.numSimilar
					+ " \tNON DEC ERROR PATIENCE: " + this.errorPatience);
			
			/* report diagnostics on exec times */
			Diagnostics.reportDiagnostics();
			Diagnostics.reportCacheSizes();
			
			/* report num nodes in alpha vectors */
			int numLeaves = 
					Arrays.stream(this.alphaVectors)
						.map(d -> d.getNumLeaves())
						.mapToInt(x -> x).sum();
			
			LOGGER.info("Total nodes in all Alpha vectors are: " + numLeaves);
			
			/* check memory consumption */
			long free = Runtime.getRuntime().freeMemory();
			long total = Runtime.getRuntime().totalMemory();
			LOGGER.debug("JVM reported mem consumption is: " + (total - free) / 1000000 + "MB");
			
			if (bellmanErr < this.bestBellmanError) {
				this.bestBellmanError = bellmanErr;
				
				this.bestAlphaVectors = new DD[this.alphaVectors.length];
				System.arraycopy(
						this.alphaVectors, 0, this.bestAlphaVectors, 0, this.bestAlphaVectors.length);
				
				this.bestPolicy = new int[this.policy.length];
				System.arraycopy(this.policy, 0, this.bestPolicy, 0, this.policy.length);
			}
			
			if (bellmanErr < 0.01) {
				LOGGER.warn("BELLMAN ERROR LESS THAN 0.01. COVERGENCE! SOFTWARE VERSION 7.0... "
						+ "LOOKING AT LIFE THROUGH THE EYES OF A TIRED HEART.");
				
				this.declareConvergence();
				
				if (debug) this.logAlphaVectors();
				break;
			}
			
			if (stepId > 20 && errorVar < 1e-8) {
				LOGGER.warn("DECLARING APPROXIMATE CONVERGENCE AT ERROR: " + bellmanErr
						+ " BECAUSE OF LOW ERROR VARIANCE " + errorVar);
				break;
			}
			
			if (stepId > 20) {
				if (this.isErrorNonDecreasing((float) bellmanErr)) {
					LOGGER.warn("DECLARING APPROXIMATE CONVERGENCE AT ERROR: " + bellmanErr
							+ " BECAUSE OF NON DECREASING ERROR");
					break;
				}
			}
			
			if (this.declareApproxConvergenceForAlphaVectors(
					this.alphaVectors.length, numIter, numBeliefs) && bellmanErr < 1.0) {
				LOGGER.warn("DECLARING APPROXIMATE CONVERGENCE AT ERROR: " + bellmanErr
						+ " BECAUSE ALL BELIEFS ARE BEING USED AND NUM ALPHAS IS CONSTANT");
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
			LOGGER.debug("Alpha Vector " + i + " is " + this.alphaVectors[i] +
					" for action " + this.ipomdp.getActions().get(this.ipomdp.policy[i]));
		}
	}
}
