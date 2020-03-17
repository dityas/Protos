/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.utils.MjDB;

/*
 * @author adityas
 *
 */
public class PersistentStructuredTree extends StructuredTree implements Serializable {

	private MjDB DB = new MjDB();
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(PersistentStructuredTree.class);
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
	
	public List<Integer> getAllNodesAtHorizon(int horizon) {
		return this.DB.getAllNodesAtHorizon(horizon);
	}
	
	public HashMap<Integer, HashMap<List<String>, Integer>> getEdgesEndingAt(int dest_id) {
		return this.DB.getEdgesEndingAt(dest_id);
	}
	
	public void updateEdgeDest(int src_id, List<String> label, int old_dest_id, int new_dest_id) {
		this.DB.updateEdgeDest(src_id, label, old_dest_id, new_dest_id);
	}
	
	@Override
	public void removeNode(int id) {
		this.DB.removeNode(id);
	}
	
	@Override
	public void removeEdge(int srcTd) {
		this.DB.removeEdge(srcTd);
	}
	
	public void removeEdgeWithDestId(int destId) {
		this.DB.removeEdgeWithDestId(destId);
	}
	
	@Override
	public boolean containsEdge(int id) {
		return this.DB.getAllEdgeIds().contains(id);
	}
	
	@Override
	public boolean containsNode(int id) {
		return this.DB.getAllNodeIds().contains(id);
	}
	
	@Override
	public void putEdge(int src, List<String> edgeLabel, int dest) {
		this.DB.putEdge(src, edgeLabel, dest);
	}
	
	@Override
	public HashMap<List<String>, Integer> getEdges(int srcId) {
		return this.DB.getEdges(srcId);
	}
	
	@Override
	public List<String> getNodeLabels() {
		return this.DB.getAllNodeIds().stream()
					.map(i -> "m" + i)
					.collect(Collectors.toList());
	}
	
	@Override
	public int getNumNodes() {
		return this.getAllNodeIds().size();
	}
	
	public void commitChanges() {
		this.DB.commitChanges();
	}
	
	// ----------------------------------------------------------------------------------------

}
