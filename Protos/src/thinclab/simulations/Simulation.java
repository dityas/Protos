/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.simulations;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.representations.StructuredTree;
import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.BaseSolver;
import thinclab.solvers.OnlineIPBVISolver;
import thinclab.solvers.OnlineSolver;

/*
 * @author adityas
 *
 */
public class Simulation extends StructuredTree {
	
	/*
	 * Base class for defining simulations
	 * 
	 * All simulations should generally be able to write dot files representing the simulation
	 * trace and the beliefs of the decision process as it was stepping through it. All simulations
	 * should also record some stats about the expected reward at each state and the cumulative
	 * reward.
	 * 
	 * The sub classing from structured tree is done because it already has the data structures to
	 * store any arbitrary path through the conditional plan and write dot files. So we'll just 
	 * use that and extend it
	 * 
	 */
	
	/* some lists for storing run stats */
	List<String> beliefSequence = new ArrayList<String>();
	List<String> actionSequence = new ArrayList<String>();
	List<String> obsSequence = new ArrayList<String>();
	List<Double> immediateRewardSequence = new ArrayList<Double>();
	List<Double> cumulativeRewardSequence = new ArrayList<Double>();
	
	/* simulation attribs */
	public BaseSolver solver;
	public int iterations;
	
	private static final long serialVersionUID = 4431341545771143166L;
	private static final Logger LOGGER = Logger.getLogger(Simulation.class);
	
	// ------------------------------------------------------------------------------------------
	
	public Simulation(BaseSolver solver, int iterations) {
		/*
		 * Set solver instance and simulation iterations
		 */
		this.solver = solver;
		this.iterations = iterations;
	}
	
	// ------------------------------------------------------------------------------------------
	
	public int step(BaseSolver solver, int currentNode) {
		
		try {
			
			DD currentBelief = this.idToNodeMap.get(currentNode).getBelief();
			
			if (solver instanceof OnlineSolver)
				((OnlineSolver) solver).solveCurrentStep();
			
			/* optimal action */
			String action = solver.getActionForBelief(currentBelief);
			this.idToNodeMap.get(currentNode).setActName(action);
			
			String[] obs = this.act(solver.f, currentBelief, action);
			
			/* record the step */
			this.beliefSequence.add(solver.f.getBeliefString(currentBelief));
			LOGGER.info("At belief " + this.beliefSequence.get(this.beliefSequence.size() - 1));
			
			if (this.solver instanceof OnlineIPBVISolver) {
				DD[] aVecs = ((OnlineIPBVISolver) this.solver).alphaVectors;
				int[] policy = ((OnlineIPBVISolver) this.solver).policy;
				
				for (int v = 0; v < aVecs.length; v++) {
					LOGGER.info("For A vec. " + v + " representing action " 
							+ this.solver.f.getActions().get(policy[v]));
					LOGGER.info("Reward is: " + 
							OP.dotProduct(
									currentBelief, 
									aVecs[v], this.solver.f.getStateVarIndices()));
				}
			}
			
			
			this.actionSequence.add(action);
			LOGGER.info("Agent took action " 
					+ this.actionSequence.get(
							this.actionSequence.size() - 1));
			
			this.obsSequence.add(Arrays.toString(obs));
			LOGGER.info("Agent observed " + this.obsSequence.get(this.obsSequence.size() - 1));
			
			/* compute reward */
			double reward = 
					OP.factoredExpectationSparse(
							solver.f.factorBelief(currentBelief), 
							solver.f.getRewardFunctionForAction(action));
			
			this.immediateRewardSequence.add(reward);
			LOGGER.info("Immediate expected reward is " 
					+ this.immediateRewardSequence.get(
							this.immediateRewardSequence.size() - 1));
			
			double lastReward = 0.0;
			
			if (this.cumulativeRewardSequence.size() > 0)
				lastReward = 
					this.cumulativeRewardSequence.get(this.cumulativeRewardSequence.size() - 1);
			
			this.cumulativeRewardSequence.add(lastReward + reward);
			LOGGER.info("Cumulative reward is " + (lastReward + reward));
			
			if (solver instanceof OnlineSolver)
				((OnlineSolver) solver).nextStep(action, Arrays.asList(obs));
			
			else
				solver.f.step(currentBelief, action, obs);
			
			/* make policy node for next belief */
			PolicyNode nextNode = new PolicyNode();
			nextNode.setBelief(solver.f.getCurrentBelief());
			nextNode.setsBelief(solver.f.getBeliefString(solver.f.getCurrentBelief()));
			nextNode.setId(this.currentPolicyNodeCounter++);
			this.idToNodeMap.put(nextNode.getId(), nextNode);
			
			/* make path */
			this.edgeMap.put(currentNode, new HashMap<List<String>, Integer>());
			this.edgeMap.get(currentNode).put(Arrays.asList(obs), nextNode.getId());
			
			return nextNode.getId();
		}
		
		catch (ZeroProbabilityObsException o) {
			return -1;
		}
		
		catch (Exception e) {
			LOGGER.error("While running belief update " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
			
			return -1;
		}
	}
	
	public String[] act(DecisionProcess DP, DD belief, String action) {
		/*
		 * Sample observation based on observation probabilities
		 */
		
		/* sample observation */
		DD obsDist = DP.getObsDist(belief, action);
		int[] obsIndices = null;
		
		if (DP.getType().contentEquals("IPOMDP"))
			obsIndices = ((IPOMDP) DP).obsIVarPrimeIndices;
		
		else obsIndices = ((POMDP) DP).primeObsIndices;
		
		int[][] obsConfig = OP.sampleMultinomial(obsDist, obsIndices);
		String[] obs = new String[obsConfig[0].length];
		for (int varI = 0; varI < obsConfig[0].length; varI ++) {
			obs[varI] = Global.valNames[obsConfig[0][varI] - 1][obsConfig[1][varI] - 1];
		}
		
		return obs;
	}
	
	public void logResults() {
		/*
		 * Writes all results to the logger
		 */
		
		LOGGER.info("Sim results:");
		LOGGER.info("belief, action, obs, immediate R, cumulative R");
		
		for (int i = 0; i < this.beliefSequence.size(); i++) {
			LOGGER.info("\"" + this.beliefSequence.get(i).replace("\"", "\\\"") + "\"" + ", "
					+ this.actionSequence.get(i) + ", "
					+ "\"" + this.obsSequence.get(i) + "\"" + ", "
					+ this.immediateRewardSequence.get(i) + ", "
					+ this.cumulativeRewardSequence.get(i));
		}
	}
	
	public void logToFile(String fileName) {
		/*
		 * Writes simulation results to csv file
		 */
		
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(fileName));
			LOGGER.debug("Writing sim results to file " + fileName);
			
			/* initialize JSON handler */
			Gson gsonHandler = 
					new GsonBuilder()
						.setPrettyPrinting()
						.disableHtmlEscaping()
						.create();
			
			JsonArray recordsArray = new JsonArray();
			
			for (int i = 0; i < this.beliefSequence.size(); i++) {
				
				/* init a new record */
				JsonObject record = new JsonObject();
				
				/* write belief */
				record.add(
						"belief", 
						gsonHandler.fromJson(
								this.beliefSequence.get(i), 
								JsonObject.class));
				
				/* write action */
				record.add("action", new JsonPrimitive(this.actionSequence.get(i)));
				
				/* write observations */
				record.add("obs", new JsonPrimitive(this.obsSequence.get(i)));
				
				/* write rewards */
				record.add("immediate R", 
						new JsonPrimitive(this.immediateRewardSequence.get(i)));				
				record.add("cumulative R", 
						new JsonPrimitive(this.cumulativeRewardSequence.get(i)));
				
				recordsArray.add(record);
			}
			
			writer.println(gsonHandler.toJson(recordsArray));
			writer.flush();
			writer.close();
			
		}
		
		catch (Exception e) {
			LOGGER.error("While writing results to JSON file: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
