/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.simulations;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.legacy.DD;
import thinclab.legacy.OP;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class CyberDeceptionSimulation extends MultiAgentSimulation {

	/*
	 * Provides decision making interface for an actual agent
	 * 
	 * The interaction takes place over HTTP
	 */
	
	private CyberDeceptionEnvironmentConnector attEnvConnector;
	private CyberDeceptionEnvironmentConnector defEnvConnector;
	
	private static final long serialVersionUID = 9022031217500704107L;
	private static final Logger LOGGER = Logger.getLogger(CyberDeceptionSimulation.class);
	
	public CyberDeceptionSimulation(
			BaseSolver ipomdpSolver, BaseSolver pomdpSolver, int interactions,
			String envIP,
			int envPort) {
		
		super(ipomdpSolver, pomdpSolver, interactions);
		this.attEnvConnector = new CyberDeceptionEnvironmentConnector(envIP, 2003);
		this.defEnvConnector = new CyberDeceptionEnvironmentConnector(envIP, 2004);
		
		LOGGER.info("Starting agent based simulation");
	}
	
	@Override
	public void runSimulation() {
		/*
		 * Runs the simulation for the given number of iterations
		 */
		
		LOGGER.info("Running simulation for " + this.iterations + " iterations...");
		this.attEnvConnector.establishSync();
		this.defEnvConnector.establishSync();
		
		int previousNode = 0;
		
		for (int i = 0; i < this.iterations; i++) {
			int nextNode = this.step(previousNode);
			previousNode = nextNode;
		}
		
		this.attEnvConnector.closeConnection();
		this.defEnvConnector.closeConnection();
		
		this.logResults();
	}
	
//	@Override
//	public String[] act(DecisionProcess DP, DD belief, String action) {
//		/*
//		 * Override to send actions to an actual agent and get observations
//		 */
//		
//		LOGGER.info("Sending action " + action + " to the environment connector");
//		String[] obs = this.envConnector.step(action);
//		
//		return obs;
//	}
	
//	@Override
//	public int step(int currentNode) {
//		
//		String[] obs1 = this.getL1Observation("ACTION_FROM_DEFENDER");
//		LOGGER.debug("Defender got observation " + Arrays.toString(obs1));
//		
//		String[] obs2 = this.getL0Observation("ACTION_FROM_ATTACKER");
//		LOGGER.debug("Attacker got observation " + Arrays.toString(obs2));
//		
//		return currentNode + 1;
//	}
	
	@Override
	public String[] getL1Observation(String action) {
		/*
		 * Sample observation from the state
		 */
		
		/* relevant varIndices */
//		String[] actions = action.split("__");
		String[] obs = this.defEnvConnector.step(action);
		
		return new String[] {"none", "user"};
	}
	
	@Override
	public String[] getL0Observation(String action) {
		/*
		 * Sample observation from the state
		 */
		
		/* relevant varIndices */
//		String[] actions = action.split("__");
		String[] obs = this.attEnvConnector.step(action);
		
		return obs;
	}

}
