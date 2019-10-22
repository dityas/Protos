/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.belief;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.frameworks.POMDP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.legacy.StateVar;

/*
 * @author adityas
 *
 */
public class BeliefSet {
	/*
	 * Contains a set of belief points
	 * 
	 * Useful for creating belief regions while solving POMDPs
	 */
	
	/*
	 * Store the starting points for the belief trees
	 */
	public List<DD> initialBeliefs = new ArrayList<DD>();
	
	/*
	 * Also make a HashSet of beliefs computed so far.
	 */
	public HashSet<DD> beliefSet = new HashSet<DD>();
	
	/*
	 * For belief expansion functions, a queue lowest belief points has to be
	 * maintained from which the expansion will be continued
	 */
	public LinkedList<DD> leafBeliefs = new LinkedList<DD>();
	
	// --------------------------------------------------------------------------------
	
	public BeliefSet(List<DD> initialBeliefs) {
		/*
		 * Add initial beliefs and populated the belief set with them
		 */
		this.initialBeliefs.addAll(initialBeliefs);
		this.beliefSet.addAll(initialBeliefs);
		this.leafBeliefs.addAll(initialBeliefs);
	}
	
	// --------------------------------------------------------------------------------
	
	public boolean checkBeliefPointExists(DD point) {
		/*
		 * Check if point exists in the current belief region
		 */
		
		return this.beliefSet.contains(point); 
	}
	
	public boolean checkBeliefPointExists(DD[] point) {
		/*
		 * Check if point exists in the current belief region
		 */
		
		return this.beliefSet.contains(OP.multN(point)); 
	}
	
	// --------------------------------------------------------------------------------
	
	private void recursiveObsGen(List<List<String>> obsComb, 
			List<StateVar> obsVars, 
			List<String> obsVector, 
			int finalLen, 
			int varIndex) {
		/* 
		 *  Recursively generates a list of all possible combinations of values of 
		 *  the observation variables
		 */
		
		if (varIndex < obsVars.size()) {
			
			if (obsVector.size() == finalLen) {
				obsComb.add(obsVector);
			}
			
			else {
				
				List<String> obsVectorCopy = new ArrayList<String>(obsVector);
				StateVar obs = obsVars.get(varIndex);
				for (int i=0;i<obs.valNames.length;i++) {
					List<String> anotherObsVecCopy = new ArrayList<String>(obsVectorCopy);
					anotherObsVecCopy.add(obs.valNames[i]);
					recursiveObsGen(obsComb, obsVars, anotherObsVecCopy, finalLen, varIndex + 1);
				}
			}
			
		}
		
		else {
			obsComb.add(obsVector);
		}
	} // private void recursiveObsGen
	
	private List<List<String>> recursiveObsCombinations(List<StateVar> obsVars){
		/*
		 * Driver program for generating observations recursively
		 */
		int finalLen = obsVars.size();
		List<String> obsVec = new ArrayList<String>();
		List<List<String>> obsComb = new ArrayList<List<String>>();
		
		recursiveObsGen(obsComb, obsVars, obsVec, finalLen, 0);
		
		return obsComb;
	} // private List<List<String>> recursiveObsCombinations
	
	public List<List<String>> getAllObservationsList(POMDP p) {
		return recursiveObsCombinations(Arrays.asList(p.obsVars));
	}
	
	// --------------------------------------------------------------------------------
	/*
	 * Belief expansion functions
	 */
	
	public void expandBeliefRegionBF(POMDP p, int horizon) {
		/*
		 * Adds next level of belief points to the belief region using 
		 * Breadth First Expansion
		 */
		
		for (int i=0; i < horizon; i++) {
			/*
			 * Initialize linked list to store new beliefs 
			 */
			LinkedList<DD> newLeaves = new LinkedList<DD>();
			
			/*
			 * Expand from all current leaves
			 */
			Iterator<DD> leafIterator = this.leafBeliefs.iterator();
			while (leafIterator.hasNext()) {
				DD leaf = leafIterator.next();
				
				/*
				 * For all actions
				 */
				for (int a=0; a < p.nActions; a++) {
					/*
					 * Recursively get all possible values and combinations of
					 * all observation variables
					 */
					List<List<String>> obsList = this.getAllObservationsList(p);
					Iterator<List<String>> obsIterator = obsList.iterator();
					
					while (obsIterator.hasNext()) {
						
						List<String> observation = obsIterator.next();
						DD nextBelief;
						
						try {
							nextBelief = Belief.beliefUpdate(p, leaf, a, 
									observation.toArray(new String[observation.size()]));
						} 
						
						catch (ZeroProbabilityObsException e) {
							continue;
						}
						
						if (!this.beliefSet.contains(nextBelief)) {
							this.beliefSet.add(nextBelief);
							newLeaves.add(nextBelief);
						}
						
					} /* obsIterator */
					
				} /* for nActions */
				
			} /* while leafIterator */
			
			this.leafBeliefs = newLeaves;
			
		} /* for horizon */
		
	} // public void expandBeliefRegionBF
	
	public void expandBeliefRegionSSGA(POMDP p, int depth) {
		/*
		 * Traverses the belief tree using an SSGA strategy and extracts unique belief points
		 */
		
		/*
		 * Create multinomial for sampling actions
		 */
		double[] explore = new double[2];
		explore[0] = 0.6;
		explore[1] = 0.4;
		
		/*
		 * Start traversal from initial beliefs
		 */
		Iterator<DD> initIterator = this.initialBeliefs.iterator();
		while (initIterator.hasNext()) {
			
			DD belief = initIterator.next();
			
			for (int i=0; i < depth; i++) {
				/*
				 * Initialize linked list to store new beliefs 
				 */
				int usePolicy = OP.sampleMultinomial(explore);
				
				/*
				 *  action sampling
				 */
				int act;
				
				if (usePolicy == 0) act = p.policyQuery(belief);
				
				else act = Global.random.nextInt(p.nActions);

				/*
				 *  sample obs
				 */
				DD obsDist = OP.addMultVarElim(POMDP.concatenateArray(belief,
						p.actions[act].transFn,
						p.actions[act].obsFn),
					POMDP.concatenateArray(p.varIndices, 
						p.primeVarIndices));

				int[][] obsConfig = OP.sampleMultinomial(obsDist, p.primeObsIndices);
				
				/*
				 *  Get next belief
				 */
				try {
					DD nextBelief = Belief.beliefUpdate(p, belief,
						act, 
						obsConfig);
					
					/*
					 * Add belief point if it doesn't already exist
					 */
					if (!this.beliefSet.contains(nextBelief)) {
						this.beliefSet.add(nextBelief);
					}
					
					belief = nextBelief;
				} 
				
				catch (ZeroProbabilityObsException e) {
					System.err.println(e.getMessage());
					continue;
				}
				
			} /* while leafIterator */
			
		} /* for horizon */
		
	} // public void expandBeliefRegionSSGA
	
	// --------------------------------------------------------------------------------
	
	public static List<DD[]> getReachableBeliefsFromBelief(POMDP p,
			DD belief,
			int horizon) {
		/*
		 * Perform breadth first expansion from the given belief
		 */
		BeliefSet reachabilitySet = new BeliefSet(Arrays.asList(new DD[] {belief}));
		reachabilitySet.expandBeliefRegionBF(p, horizon);
		
		return reachabilitySet.getFactoredBeliefRegionList(p);
	}
	
	public static List<DD[]> getInitialReachableBeliefs(POMDP p,
			int horizon) {
		/*
		 * Initialize an empty belief set with initial beliefs and do breadth
		 * first expansion
		 */
		BeliefSet reachabilitySet = new BeliefSet(p.getInitialBeliefsList());
		reachabilitySet.expandBeliefRegionBF(p, horizon);
		
		return reachabilitySet.getFactoredBeliefRegionList(p);
	}
	
	// --------------------------------------------------------------------------------
	
	public List<DD[]> getFactoredBeliefRegionList(POMDP p) {
		/*
		 * Factors the beliefs in the beliefSet and returns a list of them
		 */
		List<DD[]> factoredBelRegion = new ArrayList<DD[]>();
		Iterator<DD> beliefSetIterator = this.beliefSet.iterator();
		
		while (beliefSetIterator.hasNext()) {
			factoredBelRegion.add(Belief.factorBelief(p, beliefSetIterator.next()));
		}
		
		return factoredBelRegion;
	}
	
	public DD[][] getFactoredBeliefRegionArray(POMDP p) {
		/*
		 * Returns a POMDP compatible belRegion from the beliefSet
		 */
		List<DD[]> factoredBelRegion = this.getFactoredBeliefRegionList(p);
		return factoredBelRegion.toArray(new DD[factoredBelRegion.size()][]);
	}
}
