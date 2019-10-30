/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policy;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import thinclab.belief.Belief;
import thinclab.belief.InteractiveBelief;
import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.policyhelper.PolicyNode;
import thinclab.solvers.BaseSolver;
import thinclab.solvers.OnlineSolver;

/*
 * @author adityas
 *
 */
public class StructuredTree implements Serializable {
	
	private static final long serialVersionUID = 3354440539923303241L;

	public int maxT;
	
	/* Store the tree structure as a map of nodes and edges */
	public HashMap<Integer, PolicyNode> idToNodeMap = new HashMap<Integer, PolicyNode>();
	public HashMap<Integer, HashMap<List<String>, Integer>> edgeMap =
			new HashMap<Integer, HashMap<List<String>, Integer>>();
	
	public int currentPolicyNodeCounter = 0;
	
	public List<List<String>> observations;
	
	private static final Logger logger = Logger.getLogger(StructuredTree.class);
	
	// ----------------------------------------------------------------------------------------
	
	public void makeNextBeliefNode(
			int parentNodeId, 
			DD parentNodeBelief,
			DecisionProcess f,
			String action,
			BaseSolver solver,
			List<String> obs,
			HashMap<DD, Integer> currentLevelBeliefSet,
			int level) {
		
		/* 
		 * Make the next node after taking action at parentNodeBelief and observing the
		 * specified observation
		 */
		DD belief = this.idToNodeMap.get(parentNodeId).belief;
		
		try {
			
			DD nextBelief;
			
			/*
			 * If the process is an IPOMDP, do an IPOMDP belief update
			 * else POMDP belief update
			 */
			if (f.getType().contentEquals("IPOMDP")) {
				nextBelief = 
						InteractiveBelief.staticL1BeliefUpdate(
								(IPOMDP) f, 
								belief, 
								action, 
								obs.toArray(new String[obs.size()]));
			}
			
			else {
				nextBelief = 
						Belief.beliefUpdate(
								(POMDP) f, 
								belief, 
								f.getActions().indexOf(action), 
								obs.toArray(new String[obs.size()]));
			}
			
			/* add to belief set if nextBelief is unique */
			if (!currentLevelBeliefSet.containsKey(nextBelief)) {
				currentLevelBeliefSet.put(nextBelief, this.currentPolicyNodeCounter);
				this.currentPolicyNodeCounter += 1;
				
				int nextNodeId = currentLevelBeliefSet.get(nextBelief);
				PolicyNode nextNode = new PolicyNode();
				
				nextNode.id = nextNodeId;
				nextNode.belief = nextBelief;
				nextNode.H = level;
				
				if (solver != null)
					nextNode.actName = solver.getActionForBelief(nextBelief);
				
				else
					nextNode.actName = "";
				
				if (f.getType().contentEquals("IPOMDP"))
					nextNode.sBelief = ((IPOMDP) f).getBeliefString(nextBelief);
				
				else nextNode.sBelief = f.getBeliefString(nextBelief);
				
				/* add next unique node to the nodes map */
				this.idToNodeMap.put(nextNodeId, nextNode);
			}
			
			int nextNodeId = currentLevelBeliefSet.get(nextBelief);
			
			if (!this.edgeMap.containsKey(parentNodeId))
				this.edgeMap.put(parentNodeId, new HashMap<List<String>, Integer>());
			
			/* construct edge as action + obs */
			List<String> edge = new ArrayList<String>();
			edge.add(action);
			edge.addAll(obs);
			
			this.edgeMap.get(parentNodeId).put(edge, nextNodeId);
			
		}
		
		catch (ZeroProbabilityObsException o) {
			
		}
		
		catch (Exception e) {
			logger.error("While running belief update " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void makeNextPolicyNode(
			int parentNodeId, 
			DD parentNodeBelief,
			BaseSolver solver,
			String action,
			List<String> obs,
			HashMap<String, Integer> currentLevelBeliefSet,
			int level) {
		
		/* 
		 * Make the next node after taking action at parentNodeBelief and observing the
		 * specified observation
		 */
		try {
			
			DD nextBelief;
			
			/*
			 * If the process is an IPOMDP, do an IPOMDP belief update
			 * else POMDP belief update
			 */
			if (solver.f.getType().contentEquals("IPOMDP")) {
				nextBelief = 
						InteractiveBelief.staticL1BeliefUpdate(
								(IPOMDP) solver.f, 
								parentNodeBelief, 
								action, 
								obs.toArray(new String[obs.size()]));
			}
			
			else {
				nextBelief = 
						Belief.beliefUpdate(
								(POMDP) solver.f, 
								parentNodeBelief, 
								solver.f.getActions().indexOf(action), 
								obs.toArray(new String[obs.size()])); 
			}
			
			String nextBeliefString = solver.f.getBeliefString(nextBelief);
			
			/* add to belief set if nextBelief is unique */
			if (!currentLevelBeliefSet.containsKey(nextBeliefString)) {
				currentLevelBeliefSet.put(nextBeliefString, this.currentPolicyNodeCounter);
				this.currentPolicyNodeCounter += 1;
				
				int nextNodeId = currentLevelBeliefSet.get(nextBeliefString);
				PolicyNode nextNode = new PolicyNode();
				
				nextNode.id = nextNodeId;
				nextNode.belief = nextBelief;
				nextNode.actName = solver.getActionForBelief(nextBelief);
				nextNode.H = level;
				nextNode.sBelief = nextBeliefString;
				
				/* add next unique node to the nodes map */
				this.idToNodeMap.put(nextNodeId, nextNode);
			}
			
			int nextNodeId = currentLevelBeliefSet.get(nextBeliefString);
			
			if (!this.edgeMap.containsKey(parentNodeId))
				this.edgeMap.put(parentNodeId, new HashMap<List<String>, Integer>());
			
			this.edgeMap.get(parentNodeId).put(obs, nextNodeId);
			
		}
		
		catch (ZeroProbabilityObsException o) {
			
		}
		
		catch (Exception e) {
			logger.error("While running belief update " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	// ----------------------------------------------------------------------------------------
	
	public String getJSONString() {
		/*
		 * Converts computed policy tree to JSON format
		 */
		
		Gson gsonHandler = 
				new GsonBuilder()
					.disableHtmlEscaping()
					.setPrettyPrinting()
					.create();
		
		/* Hold nodes and edges in HashMaps of Strings */
		HashMap<Integer, HashMap<String, String>> nodeJSONMap = 
				new HashMap<Integer, HashMap<String, String>>();
		HashMap<Integer, HashMap<String, String>> edgesJSONMap = 
				new HashMap<Integer, HashMap<String, String>>();
		
		/* populate nodes */
		for (Entry<Integer, PolicyNode> entry : this.idToNodeMap.entrySet()) {
			
			/* add optimal action and beliefs at node */
			nodeJSONMap.put(entry.getKey(), new HashMap<String, String>());
			nodeJSONMap.get(entry.getKey()).put("action", entry.getValue().actName);
			nodeJSONMap.get(entry.getKey()).put("belief", entry.getValue().sBelief);
		}
		
		/* populate edges */
		for (int fromNode : this.edgeMap.keySet()) {
			
			edgesJSONMap.put(fromNode, new HashMap<String, String>());
			
			for (Entry<List<String>, Integer> entry : this.edgeMap.get(fromNode).entrySet())
				edgesJSONMap.get(fromNode).put(
						entry.getKey().toString(), 
						entry.getValue().toString());
		}
		
		
		JsonObject jsonContainer = new JsonObject();
		jsonContainer.add("nodes", gsonHandler.toJsonTree(nodeJSONMap));
		jsonContainer.add("edges", gsonHandler.toJsonTree(edgesJSONMap));
		
		return gsonHandler.toJson(jsonContainer);
	}
	
	public String getDotString() {
		/*
		 * Converts to graphviz compatible dot string
		 */
		String endl = "\r\n";
		String dotString = "digraph G{ " + endl;
		
		dotString += "graph [ranksep=3];" + endl;
		
		/* Make nodes */
		for (Entry<Integer, PolicyNode> entry : this.idToNodeMap.entrySet()) {
			dotString += " " + entry.getKey() + " [shape=record, label=\"{"
					+ "Ai=" + entry.getValue().actName + " | "
					+ entry.getValue().sBelief
						.replace("{", "(")
						.replace("}", ")")
						.replace("),", ",||")
						.replace("=((", "=|")
						.replace(", (", "|(")
					+ "}\"];" + endl;
		}
		
		dotString += endl;
		
		for (Entry<Integer, HashMap<List<String>, Integer>> edges : this.edgeMap.entrySet()) {
			
			String from = edges.getKey().toString();
			
			for (Entry<List<String>, Integer> ends : edges.getValue().entrySet()) {
				
				dotString += " " + from + " -> " + ends.getValue()
					+ " [label=\"" + ends.getKey().toString() + "\"]" + endl;
			}
		}
		
		dotString += "}" + endl;
		
		return dotString;
	}

}
