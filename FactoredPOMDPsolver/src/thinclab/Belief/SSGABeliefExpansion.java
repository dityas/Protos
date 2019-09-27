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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.Global;
import thinclab.symbolicperseus.OP;
import thinclab.symbolicperseus.POMDP;

/*
 * @author adityas
 *
 */
public class SSGABeliefExpansion extends BeliefRegionExpansionStrategy {
	
	/*
	 * Runs an SSGA belief expansion starting from the initial belief of the given POMDP
	 */
	
	/* reference to the POMDP */
	public POMDP p;
	
	/* All possible combinations of observations */
	public List<List<String>> allPossibleObs;
	
	/* Currently explored beliefs */
	private List<DD> initialBeliefs;
	private HashSet<DD> exploredBeliefs;
	
	/* number of iterations of SSGA during each expansion */
	private int nIterations; 
	
	private static final Logger logger = Logger.getLogger(SSGABeliefExpansion.class);
	
	// ----------------------------------------------------------------------------------------
	
	public SSGABeliefExpansion(POMDP p, int maxDepth, int iterations) {
		/*
		 * Set properties and initialize super
		 */
		
		super(maxDepth);
		
		this.p = p;
		this.allPossibleObs = this.p.getAllPossibleObservations();
		this.nIterations = iterations;
		
		/* add initial beliefs from the POMDP */
		this.initialBeliefs = new ArrayList<DD>();
		this.initialBeliefs.addAll(this.p.getInitialBeliefsList());
		
		this.exploredBeliefs = new HashSet<DD>();
		this.exploredBeliefs.addAll(this.p.getInitialBeliefsList());
		
		logger.debug("SSGA expansion search initialized");
	}
	
	// ----------------------------------------------------------------------------------------

	@Override
	public void expand() {
		/*
		 * Run SSGA expansion for nIterations
		 */
		
		/* Create multinomial for sampling actions */
		double[] explore = new double[2];
		explore[0] = 0.6;
		explore[1] = 0.4;
		
		/* Start traversal from initial beliefs */
		for (DD belief : this.initialBeliefs) {
			
			for (int i=0; i < this.getHBound(); i++) {
				
				/* Initialize linked list to store new beliefs */
				int usePolicy = OP.sampleMultinomial(explore);
				
				/* action sampling */
				int act;
				
				if (usePolicy == 0) act = p.policyQuery(belief);
				
				else act = Global.random.nextInt(p.nActions);

				/* sample obs */
				DD obsDist = OP.addMultVarElim(POMDP.concatenateArray(belief,
						p.actions[act].transFn,
						p.actions[act].obsFn),
					POMDP.concatenateArray(p.varIndices, 
						p.primeVarIndices));

				int[][] obsConfig = OP.sampleMultinomial(obsDist, p.primeObsIndices);
				
				/* Get next belief */
				try {
					DD nextBelief = Belief.beliefUpdate(p, belief,
						act, 
						obsConfig);
					
					/* Add belief point if it doesn't already exist */
					if (!this.exploredBeliefs.contains(nextBelief))
						this.exploredBeliefs.add(nextBelief);
					
					belief = nextBelief;
				} 
				
				catch (ZeroProbabilityObsException e) {
					System.err.println(e.getMessage());
					continue;
				}
				
			} /* while leafIterator */
			
		} /* for horizon */

	}

	@Override
	public List<DD> getBeliefPoints() {
		/*
		 * Return list of currently explored unique belief points
		 */
		return new ArrayList<DD>(this.exploredBeliefs);
	}

}
