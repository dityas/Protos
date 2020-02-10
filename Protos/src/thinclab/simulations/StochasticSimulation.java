/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.simulations;

import org.apache.log4j.Logger;

import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class StochasticSimulation extends Simulation {

	/*
	 * Runs simulation by sampling observations based on the current belief
	 * 
	 * Does not really maintain an explicit state of the system
	 */
	
	private static final long serialVersionUID = 6158849903755845368L;
	private static final Logger LOGGER = Logger.getLogger(StochasticSimulation.class);
	
	public StochasticSimulation(BaseSolver solver, int iterations) {
		/*
		 * Initialize properties and attributes
		 */
		
		super(solver, iterations);
		
		/* make start policy node from initial belief */
		PolicyNode node = new PolicyNode();
		node.setBelief(this.solver.f.getCurrentBelief());
		node.setsBelief(this.solver.f.getBeliefString(this.solver.f.getCurrentBelief()));
		node.setId(this.currentPolicyNodeCounter++);
		
		/* add to node map */
		this.putPolicyNode(node.getId(), node);
		
		LOGGER.debug("Stochastic simulation initialized");
	}
	
	public void runSimulation() {
		/*
		 * Runs the simulation for the given number of iterations
		 */
		
		LOGGER.info("Running simulation for " + this.iterations + " iterations...");
		
		int previousNode = 0;
		
		for (int i = 0; i < this.iterations; i++) {
			int nextNode = this.step(previousNode);
			previousNode = nextNode;
		}
		
		this.logResults();
	}

}
