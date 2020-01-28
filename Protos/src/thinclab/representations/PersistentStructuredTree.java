/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.OP;
import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.BaseSolver;
import thinclab.utils.MjDB;

/*
 * @author adityas
 *
 */
public class PersistentStructuredTree extends StructuredTree implements Serializable {

	public int maxT;
	public int currentPolicyNodeCounter = 0;
	public List<List<String>> observations;
	
	private MjDB DB = new MjDB();
	
	private static final Logger LOGGER = Logger.getLogger(StructuredTree.class);
	private static final long serialVersionUID = 3354440539923303241L;
	
	// ----------------------------------------------------------------------------------------
	
	@Override
	public PolicyNode getPolicyNode(int id) {
		return this.DB.getNode(id);
	}
	
	@Override
	public void putPolicyNode(int id, PolicyNode node) {
		this.DB.putNode(id, node);
	}
	
	@Override
	public void setAllAsRoots() {
		this.DB.makeAllRoots();
	}
	
	@Override
	public void clearAllEdges() {
		this.DB.clearEdgesTable();
	}
	
	@Override
	public List<Integer> getAllNodeIds() {
		return this.DB.getAllNodeIds();
	}
	
	@Override
	public List<Integer> getAllEdgeIds() {
		return this.DB.getAllEdgeIds();
	}
	
	@Override
	public List<Integer> getAllRootIds() {
		return this.DB.getAllRoots();
	}
	
	@Override
	public void removeNode(int id) {
		this.DB.removeNode(id);
	}
	
	public boolean containsEdge(int id) {
		return this.edgeMap.containsKey(id);
	}
	
	public boolean containsNode(int id) {
		return this.idToNodeMap.containsKey(id);
	}
	
	@Override
	public void putEdge(int src, List<String> edgeLabel, int dest) {
		this.DB.putEdge(src, edgeLabel, dest);
	}
	
	public HashMap<List<String>, Integer> getEdges(int srcId) {
		return this.edgeMap.get(srcId);
	}
	
	public List<String> getNodeLabels() {
		return this.idToNodeMap.keySet().stream()
					.map(i -> "m" + i)
					.collect(Collectors.toList());
	}
	
	public int getNumNodes() {
		return this.idToNodeMap.size();
	}
	
	// ----------------------------------------------------------------------------------------

}
