/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.Belief;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.POMDP;

/*
 * @author adityas
 *
 */
public class FullBeliefExpansion extends BeliefRegionExpansionStrategy {
	
	/*
	 * Explore the full belief region using a breadth first search
	 */
	
	/* reference to the POMDP */
	private POMDP p;
	
	/* All possible combinations of observations */
	public List<List<String>> allPossibleObs;
	
	/* Currently explored beliefs and leaves for expansion */
	public List<DD> leaves;
	public HashSet<DD> exploredBeliefs;
	
	private static final Logger logger = Logger.getLogger(FullBeliefExpansion.class);
	
	// -------------------------------------------------------------------------------
	
	public FullBeliefExpansion(POMDP p, int maxDepth) {
		/*
		 * Set the class properties and call super to set maximum bound on the search
		 * 
		 * Param maxDepth represents the maximum depth upto which expansion can run
		 */
		
		super(maxDepth);
		
		this.p = p;
		this.allPossibleObs = p.getAllPossibleObservations();
		
		logger.debug("Initialized FullBeliefExpansion search till max depth " 
				+ this.getHBound());
		
		/* add initial beliefs to start expansion */
		this.leaves = new ArrayList<DD>();
		this.leaves.addAll(this.p.getInitialBeliefsList());
		
		this.exploredBeliefs = new HashSet<DD>();
		this.exploredBeliefs.addAll(this.p.getInitialBeliefsList());
	}
	
	public FullBeliefExpansion(int maxDepth) {
		/*
		 * Constructor for use with subclasses to avoid enforcing POMDP initialization
		 */
		
		super(maxDepth);
	}
	
	// -----------------------------------------------------------------------------------
	
	public DD beliefUpdate(
			POMDP p, 
			DD previousBelief, 
			int actId,
			List<String> obs) {
		
		/*
		 * Split the belief update into a separate function to enable
		 * reuse with interactive belief expanion
		 */
		
		try {
			
			DD nextBelief = 
					Belief.beliefUpdate(
							p, 
							previousBelief, 
							actId, 
							obs.toArray(
									new String[obs.size()]));
			
			return nextBelief;
		} 
		
		catch (ZeroProbabilityObsException e) { return null; }
		
	}
	
	// --------------------------------------------------------------------------------------

	@Override
	public void expand() {
		/*
		 * Runs one step of expansion from leaves and adds to exploredBeliefs
		 */
		
		logger.debug("Beginning expansion from " + this.leaves.size() + " beliefs.");
		
		HashSet<DD> newLeaves = new HashSet<DD>();
		
		/* Expand from all current leaves */
		for (DD leaf : this.leaves) {
			
			/* For all actions */
			for (int a=0; a < this.p.nActions; a++) {
				
				/*
				 * Recursively get all possible values and combinations of
				 * all observation variables
				 */
				for (List<String> observation : this.allPossibleObs) {
					
					DD nextBelief = 
							this.beliefUpdate(
									this.p, 
									leaf, 
									a, 
									observation);
					
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
		
		return new ArrayList<DD>(this.exploredBeliefs);
	}

}
