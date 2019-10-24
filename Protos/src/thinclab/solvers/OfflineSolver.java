/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.List;

import org.apache.commons.collections15.buffer.CircularFifoBuffer;
import org.apache.log4j.Logger;

import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.frameworks.Framework;
import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public abstract class OfflineSolver extends BaseSolver {

	/*
	 * Defines the basic skeleton and structure for implementing offline solvers
	 * 
	 * Offline solvers do not need to maintain framework state and compute a global 
	 * infinite horizon policy. This mean the POMDP or the IPOMDP solved by an offline
	 * solver does not need to keep track of its current belief. Any belief is relevant
	 * in the current context and the next belief can be computed from any belief. 
	 */
	
	private static final long serialVersionUID = 1927693473988528606L;

	public BeliefRegionExpansionStrategy expansionStrategy;
	
	CircularFifoBuffer<Float> bErrorVals = new CircularFifoBuffer<Float>(5);
	
	private static final Logger logger = Logger.getLogger(OfflineSolver.class);

	// ------------------------------------------------------------------------------------------
	
	public OfflineSolver(Framework f, BeliefRegionExpansionStrategy b) {
		/*
		 * Set properties and all that
		 */
		
		this.f = f;
		this.expansionStrategy = b;
	}
	
	// ------------------------------------------------------------------------------------------
	
	public abstract void solve();
	
	/* Actual solution method */
	public abstract void solveForBeliefs(List<DD> beliefs);
	
	// -----------------------------------------------------------------------------------------
	
	public void resetBeliefExpansion() {
		/*
		 * Reset the belief search to th current belief of the framework
		 */
		logger.warn("Trying to reset belief expansion on an offline solver."
				+ " This operation will have no effect");
	}
	
	public Framework getFramework() {
		/*
		 * Getter for the framework object
		 */
		return this.f;
	}
	
	public void setFramework(Framework f) {
		/*
		 * Setter for the framework object
		 */
		this.f = f;
		this.expansionStrategy.setFramework(f);
	}
	
	public float getErrorVariance(float bellManError) {
		/*
		 * Computes the variance of the last 5 iterations of the solver and
		 * returns the variance
		 */
		bErrorVals.add(bellManError);
		
		float mean = 
				(float) bErrorVals.stream()
					.mapToDouble(Double::valueOf)
					.average()
					.getAsDouble();
		
		float variance = 
				(float) (bErrorVals.stream()
					.map(v -> Math.pow((v - mean), 2))
					.mapToDouble(Double::valueOf)
					.sum() / (float) bErrorVals.size());
		
		return variance;
					
	}

}
