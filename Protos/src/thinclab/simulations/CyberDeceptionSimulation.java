/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.simulations;

import org.apache.log4j.Logger;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.legacy.DD;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class CyberDeceptionSimulation extends StochasticSimulation {

	/*
	 * Provides decision making interface for an actual agent
	 * 
	 * The interaction takes place over HTTP
	 */
	
	private CyberDeceptionEnvironmentConnector envConnector;
	
	private static final long serialVersionUID = 9022031217500704107L;
	private static final Logger LOGGER = Logger.getLogger(CyberDeceptionSimulation.class);
	
	public CyberDeceptionSimulation(
			BaseSolver solver, 
			int iterations,
			String envIP,
			int envPort) {
		
		super(solver, iterations);
		this.envConnector = new CyberDeceptionEnvironmentConnector(envIP, envPort);
		
		LOGGER.info("Starting agent based simulation");
	}
	
	@Override
	public void runSimulation() {
		/*
		 * Runs the simulation for the given number of iterations
		 */
		
		LOGGER.info("Running simulation for " + this.iterations + " iterations...");
		this.envConnector.establishSync();
		
		int previousNode = 0;
		
		for (int i = 0; i < this.iterations; i++) {
			int nextNode = this.step(previousNode);
			previousNode = nextNode;
		}
		
		this.envConnector.closeConnection();
		
		this.logResults();
	}
	
	@Override
	public String[] act(DecisionProcess DP, DD belief, String action) {
		/*
		 * Override to send actions to an actual agent and get observations
		 */
		
		LOGGER.info("Sending action " + action + " to the environment connector");
		String[] obs = this.envConnector.step(action);
		
		return obs;
	}

}
