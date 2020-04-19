/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.simulations;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

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
	private CyberDeceptionEnvironmentConnector controllerConnector;
	
	private static final long serialVersionUID = 9022031217500704107L;
	private static final Logger LOGGER = Logger.getLogger(CyberDeceptionSimulation.class);
	
	// ---------------------------------------------------------------------------------------------
	
	public CyberDeceptionSimulation(
			BaseSolver ipomdpSolver, BaseSolver pomdpSolver, int interactions,
			String envIP,
			int envPort) {
		
		super(ipomdpSolver, pomdpSolver, interactions);
		this.attEnvConnector = new CyberDeceptionEnvironmentConnector(envIP, 2003);
		this.defEnvConnector = new CyberDeceptionEnvironmentConnector(envIP, 2004);
		this.controllerConnector = new CyberDeceptionEnvironmentConnector(envIP, 2008);

		LOGGER.info("Starting agent based simulation");
	}
	
	// ---------------------------------------------------------------------------------------------
	
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
			if (nextNode == -1) break;
			
			previousNode = nextNode;
		}
		
		try {
			
			this.controllerConnector.createEnvStreams();
			this.controllerConnector.sendControlAction("restart");
			
			TimeUnit.SECONDS.sleep(5);
			
			this.attEnvConnector.closeConnection();
			this.defEnvConnector.closeConnection();
			
			TimeUnit.SECONDS.sleep(10);
		}
		
		catch (Exception e) {
			LOGGER.error("Exception: " + e);
			e.printStackTrace();
		}

	}

	@Override
	public String[][] doJointAction(String jointAction) {
		
		this.defEnvConnector.sendAction(this.mapL1Action(jointAction));
		this.attEnvConnector.sendAction(jointAction.split("__")[1]);
		
		String[] l1Obs = this.mapL1Obs(this.defEnvConnector.getObservation());
		String[] l0Obs = this.attEnvConnector.getObservation();
		
		if (jointAction.split("__")[1].contentEquals("EXIT"))
			this.endSimulation();
		
		/* stack observation arrays */
		String[][] obs = new String[2][];
		obs[0] = l1Obs;
		obs[1] = l0Obs;
		
		return obs;
	}
	
	public String mapL1Action(String action) {
		
		String[] actions = action.split("__");
		
		/*
		 * Map action to defender agent
		 */
		String defAction = actions[0];
		
		if (defAction.contentEquals("SHOW_LOWER_PRIVS"))
			defAction = "whoami";
		
		else if (defAction.contentEquals("SHOW_HIGHER_PRIVS"))
			defAction = "undo_whoami";
		
		else if (defAction.contentEquals("DEPLOY_HONEY_FILES"))
			defAction = "filebomb";
		
		else if (defAction.contentEquals("DEPLOY_REAL_ROOT_VULN"))
			defAction = "undo_uname";
		
		else if (defAction.contentEquals("DEPLOY_FAKE_VULN_INDICATORS"))
			defAction = "ps";
		
		return defAction;
	}
	
	public String[] mapL1Obs(String[] obs) {
		
		String obsString = obs[0].substring(1, obs[0].length() - 1);
		String[] obsStrings = obsString.split(",");
		
		for (String singleObs: obsStrings) {
			
			String[] obsMap = singleObs.split(":");
			
			if (obsMap[0].contentEquals("1") && obsMap[1].contentEquals("1"))
				return new String[] {"file_enum"};
			
			else if (obsMap[0].contentEquals("2") && obsMap[1].contentEquals("1"))
				return new String[] {"vuln_recon"};
			
			else if (obsMap[0].contentEquals("3") && obsMap[1].contentEquals("1"))
				return new String[] {"exfil_attempt"};
			
			else if (obsMap[0].contentEquals("4") && obsMap[1].contentEquals("1"))
				return new String[] {"persistence_attempt"};
		}
		
		return new String[] {"none"};
	}
	
	
	@Override
	public String[] getL1Observation(String action) {
		/*
		 * Sample observation from the state
		 */
		
		String defAction = this.mapL1Action(action);
		String[] obs = this.defEnvConnector.step(defAction);
		
		/*
		 * Observation is in format {1:0, 2:0, ...}
		 */
		
		String obsString = obs[0].substring(1, obs[0].length() - 1);
		String[] obsStrings = obsString.split(",");
		
		for (String singleObs: obsStrings) {
			
			String[] obsMap = singleObs.split(":");
			
			if (obsMap[0].contentEquals("1") && obsMap[1].contentEquals("1"))
				return new String[] {"file_enum"};
			
			else if (obsMap[0].contentEquals("2") && obsMap[1].contentEquals("1"))
				return new String[] {"vuln_recon"};
			
			else if (obsMap[0].contentEquals("3") && obsMap[1].contentEquals("1"))
				return new String[] {"exfil_attempt"};
			
			else if (obsMap[0].contentEquals("4") && obsMap[1].contentEquals("1"))
				return new String[] {"persistence_attempt"};
		}
		
		return new String[] {"none"};
	}
	
	@Override
	public String[] getL0Observation(String action) {
		/*
		 * Sample observation from the state
		 */
		
		/* relevant varIndices */
		String[] actions = action.split("__");
		String[] obs = this.attEnvConnector.step(actions[1]);
		
		return obs;
	}

}
