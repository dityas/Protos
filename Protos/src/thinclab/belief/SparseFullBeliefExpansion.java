/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.belief;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.log4j.Logger;

import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public class SparseFullBeliefExpansion extends FullBeliefExpansion {
	
	/*
	 * Do sparse belief tree exploration
	 * 
	 * Expand for all actions but sample observations
	 */
	
	private int nIters = 30;
	
	private static final long serialVersionUID = 1571517162981801344L;
	
	private static final Logger LOGGER = Logger.getLogger(SparseFullBeliefExpansion.class);
	
	// ----------------------------------------------------------------------------------------

	public SparseFullBeliefExpansion(IPOMDP ip, int iterations) {
		super(ip);
		this.nIters = iterations;
	}

	// ----------------------------------------------------------------------------------------
	
	@Override
	public void expand() {
		
		for (int s = 0; s < this.nIters; s++) {
			
			LOGGER.debug("Starting new sampling iteration");
			
			this.leaves.clear();
			this.leaves.add(this.f.getCurrentBelief());
			
			/* H - 1 expansions to let the Solver reach H during solving */
			for (int i = 0; i < this.getHBound() - 1; i++)
				this.expandSingleStep();
		}
	}
	
	public void expandSingleStep() {
		/*
		 * Runs one step of expansion from leaves and adds to exploredBeliefs
		 */
		
		long totalMem = Runtime.getRuntime().totalMemory();
		long freeMem = Runtime.getRuntime().freeMemory();
		
		if (((totalMem - freeMem) / 1000000000) > 40)
			Global.clearHashtables();
		
		HashSet<DD> newLeaves = new HashSet<DD>();
		
		/* Expand from all current leaves */
		for (DD leaf : this.leaves) {
			
			/* For all actions */
			for (String action : this.f.getActions()) {
				
				/*
				 * Sample observations here
				 */
				DD obsDist = this.f.getObsDist(leaf, action);
				int[][] obsConfig = null;
				DD nextBelief = null;
				
				try {
					if (this.f.getType().contentEquals("POMDP")) {
						obsConfig = OP.sampleMultinomial(obsDist, ((POMDP) this.f).primeObsIndices);
						nextBelief = 
								((BeliefOps) this.f.bOPs).beliefUpdate(
										leaf, action, obsConfig);
					}
					
					else {
						obsConfig = 
							OP.sampleMultinomial(obsDist, ((IPOMDP) this.f).obsIVarPrimeIndices);
						nextBelief = 
								((IBeliefOps) this.f.bOPs).beliefUpdate(
										leaf, action, obsConfig);
					}
				}
				
				catch (ZeroProbabilityObsException e) {
					LOGGER.warn("Zero probability Observation encountered. Somthing"
							+ " might be seriously wrong");
				}
				
				catch (Exception e) {
					LOGGER.error("While doing belief update: " + e.getMessage());
					e.printStackTrace();
					System.exit(-1);
				}
				
				/* continue if observation probability was 0 */
				if (nextBelief == null) continue;
				
				if (!this.exploredBeliefs.contains(nextBelief)) {
					this.exploredBeliefs.add(nextBelief);
					newLeaves.add(nextBelief);
				}
				
			} /* for nActions */
		} /* while leafIterator */
		
		LOGGER.debug(this.exploredBeliefs.size() + " points explored in the belief space.");
		this.leaves = new ArrayList<DD>(newLeaves);
		newLeaves.clear();
	}
}
