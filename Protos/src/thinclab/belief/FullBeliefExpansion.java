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
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public class FullBeliefExpansion extends BeliefRegionExpansionStrategy {
	
	/*
	 * Explore the full belief region using a breadth first search
	 */
	
	private static final long serialVersionUID = -2061648460611553584L;

	/* All possible combinations of observations */
	public List<List<String>> allPossibleObs;
	
	/* Currently explored beliefs and leaves for expansion */
	public List<DD> leaves;
	public HashSet<DD> exploredBeliefs;
	
	private static final Logger logger = Logger.getLogger(FullBeliefExpansion.class);
	
	// -------------------------------------------------------------------------------
	
	public FullBeliefExpansion(DecisionProcess f, int maxDepth) {
		/*
		 * Set the class properties and call super to set maximum bound on the search
		 * 
		 * Param maxDepth represents the maximum depth upto which expansion can run
		 */
		
		super(maxDepth);
		
		this.setFramework(f);
		this.allPossibleObs = f.getAllPossibleObservations();
		
		logger.debug("Initialized FullBeliefExpansion search till max depth " 
				+ this.getHBound());
		
		/* add initial beliefs to start expansion */
		this.leaves = new ArrayList<DD>();
		this.leaves.addAll(this.f.getInitialBeliefs());
		
		this.exploredBeliefs = new HashSet<DD>();
		this.exploredBeliefs.addAll(this.f.getInitialBeliefs());
		
		logger.debug("Starting beliefs are " + this.leaves);
	}
	
	public FullBeliefExpansion(int maxDepth) {
		/*
		 * Constructor for use with subclasses to avoid enforcing POMDP initialization
		 */
		
		super(maxDepth);
	}
	
	public FullBeliefExpansion(IPOMDP ipomdp) {
		/*
		 * Constructor for use with IPOMDPs.
		 */
		this(ipomdp, ipomdp.mjLookAhead);
	}
	
	// -----------------------------------------------------------------------------------
	
	public DD beliefUpdate(
			DecisionProcess f, 
			DD previousBelief, 
			String action,
			List<String> obs) {
		
		/*
		 * Split the belief update into a separate function to enable
		 * reuse with interactive belief expanion
		 */
		
		try {
			
			DD nextBelief = 
					f.beliefUpdate(previousBelief, action, obs.stream().toArray(String[]::new));
	
			return nextBelief;
		} 
		
		catch (ZeroProbabilityObsException e) { return null; }
		
	}
	
	// --------------------------------------------------------------------------------------
	/*
	 * Overrides from super
	 */
	
	@Override
	public void expand() {
		
		/* H - 1 expansions to let the Solver reach H during solving */
		for (int i = 0; i < this.getHBound() - 1; i++)
			this.expandSingleStep();
	}
	
	public void expandSingleStep() {
		/*
		 * Runs one step of expansion from leaves and adds to exploredBeliefs
		 */
		
		long totalMem = Runtime.getRuntime().totalMemory();
		long freeMem = Runtime.getRuntime().freeMemory();
		
		if (((totalMem - freeMem) / 1000000000) > 40)
			Global.clearHashtables();
		
		logger.debug("Beginning expansion from " + this.leaves.size() + " beliefs.");
		
		HashSet<DD> newLeaves = new HashSet<DD>();
		
		/* Expand from all current leaves */
		for (DD leaf : this.leaves) {
			
			/* For all actions */
			for (String action : this.f.getActions()) {
				
				/*
				 * Recursively get all possible values and combinations of
				 * all observation variables
				 */
				List<DD[]> factoredNextBels = new ArrayList<DD[]>();
				
				for (List<String> observation : this.allPossibleObs) {
					
					DD nextBelief = 
							this.beliefUpdate(
									this.f, 
									leaf, 
									action, 
									observation);
					
					/* compute obs probs and next bel states for IPOMDP */
					if (this.f.getType().contentEquals("IPOMDP")) {
						
						factoredNextBels.add(
								OP.primeVarsN(
										this.f.factorBelief(nextBelief), 
										((IPOMDP) this.f).S.size() + ((IPOMDP) this.f).Omega.size()));
					}
					
					/* continue if observation probability was 0 */
					if (nextBelief == null) continue;
					
					if (!this.exploredBeliefs.contains(nextBelief)) {
						this.exploredBeliefs.add(nextBelief);
						newLeaves.add(nextBelief);
					}
				} /* obsIterator */
				
			} /* for nActions */

		} /* while leafIterator */
		
		logger.debug("Found " + newLeaves.size() + " more beliefs in the expansion phase.");
		logger.debug(this.exploredBeliefs.size() + " points explored in the belief space.");
		this.leaves = new ArrayList<DD>(newLeaves);
	}

	@Override
	public List<DD> getBeliefPoints() {
		
		List<DD> beliefs = new ArrayList<DD>(this.exploredBeliefs);
		this.exploredBeliefs.clear();
		
		return beliefs;
	}
	
	@Override
	public void resetToNewInitialBelief() {
		/*
		 * Used to reset the root of the search to the new initial beliefs after the
		 * POMDP or IPOMDP transitions to next step
		 */
		this.leaves.clear();
		this.leaves.add(this.f.getCurrentBelief());
		
		this.exploredBeliefs.clear();
		this.exploredBeliefs.addAll(this.leaves);
		
		logger.debug("Belief root reset to framework's initial belief");
	}

	@Override
	public void clearMem() {
		/*
		 * Remove all stored beliefs
		 */
		this.exploredBeliefs.clear();
		this.leaves.clear();
	}
	
}
