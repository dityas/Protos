/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.simulations;

import org.apache.log4j.Logger;

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
		
		LOGGER.debug("Stochastic simulation initialized");
	}
	
	public void runSimulation() {
		/*
		 * Runs the simulation for the given number of iterations
		 */
		
		LOGGER.info("Running simulation for " + this.nIterations + " iterations...");
		
		for (int i = 0; i < this.nIterations; i++) {
			this.step(this.solver);
		}
	}

}
