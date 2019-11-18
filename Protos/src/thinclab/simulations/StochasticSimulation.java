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
	
	public BaseSolver solver;
	public int nIterations;
	
	private static final long serialVersionUID = 6158849903755845368L;
	private static final Logger LOGGER = Logger.getLogger(StochasticSimulation.class);
	
	public StochasticSimulation(BaseSolver solver, int iterations) {
		/*
		 * Initialize properties and attributes
		 */
		
		this.solver = solver;
		this.nIterations = iterations;
		
		/* make start policy node from initial belief */
		PolicyNode node = new PolicyNode();
		node.belief = this.solver.f.getCurrentBelief();
		node.sBelief = this.solver.f.getBeliefString(this.solver.f.getCurrentBelief());
		node.id = this.currentPolicyNodeCounter++;
		
		/* add to node map */
		this.idToNodeMap.put(node.id, node);
		
		LOGGER.debug("Stochastic simulation initialized");
	}
	
	public void runSimulation() {
		/*
		 * Runs the simulation for the given number of iterations
		 */
		
		LOGGER.info("Running simulation for " + this.nIterations + " iterations...");
		
		int previousNode = 0;
		
		for (int i = 0; i < this.nIterations; i++) {
			int nextNode = this.step(this.solver, previousNode);
			previousNode = nextNode;
		}
	}

}
