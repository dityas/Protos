/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations.belieftreerepresentations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import thinclab.representations.PersistentStructuredTree;
import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.AlphaVectorPolicySolver;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class StrictlyOptimalDynamicBeliefGraph extends DynamicBeliefTree {

	/*
	 * Only does full belief expansion for the first step to maintain complete information
	 * about the immediate next step. After that, does policy graph expansion to produce Ajs for
	 * look ahead. 
	 */
	
	private static final long serialVersionUID = -4519925553640062581L;
	private static final Logger LOGGER = Logger.getLogger(StrictlyOptimalDynamicBeliefGraph.class);
	
	// ---------------------------------------------------------------------------------------------

	public StrictlyOptimalDynamicBeliefGraph(BaseSolver solver, int lookAhead) {
		
		super(solver, lookAhead);
	}
	
	// --------------------------------------------------------------------------------------
	
	@Override
	public void buildTree() {
		/*
		 * Builds a lookahead belief tree upto T = 1, and then merges the policy graph for the
		 * rest of the lookahead depth
		 */
		
		this.solver.f.setGlobals();
		List<Integer> prevNodes = new ArrayList<Integer>();
		prevNodes.addAll(leafNodes);
		
		/*
		 * build lookahead tree for first step
		 */
		
		this.leafNodes.clear();
		this.leafNodes.addAll(this.getNextPolicyNodes(prevNodes, 1));
		
		prevNodes.clear();
		prevNodes.addAll(this.leafNodes);
		
		LOGGER.debug(this.getDotStringForPersistent());
		
		for (int t = 2; t < this.maxT; t++) {
			prevNodes = this.expandPolicyGraph(prevNodes, (AlphaVectorPolicySolver) this.solver, t);
		}
		
		if (this instanceof PersistentStructuredTree)
			this.commitChanges();
		
		this.stripBeliefInfo();
	}
	
	private void stripBeliefInfo() {
		/*
		 * Removes the information about beliefs from the policy nodes
		 */
		
		for (int nodeId: this.getAllNodeIds()) {
			
			PolicyNode node = this.getPolicyNode(nodeId);
			if (node.getH() < 2) continue;
			
			this.removeNode(nodeId);
			node.setsBelief("{\"N/A\":\"N/A\"}");
			node.setBelief(null);
			this.putPolicyNode(nodeId, node);
		}
	}
	
	// ---------------------------------------------------------------------------------------------
	
	@Override
	public String getDotStringForPersistent() {
		/*
		 * Converts to graphviz compatible dot string
		 */
		String endl = "\r\n";
		String dotString = "digraph G{ " + endl;
		
		dotString += "graph [ranksep=3];" + endl;
		
		/* Make nodes */
		for (int id : this.getAllNodeIds()) {
			
			PolicyNode node = this.getPolicyNode(id);
			
			if (node.isStartNode())
				dotString += " " + id + " [shape=Mrecord, label=\"";
			else
				dotString += " " + id + " [shape=record, label=\"";
			
			dotString += node.getActName()
					+ "\"];" + endl;
		}
		
		/* write MEU */
//		dotString += -1 
//				+ " [shape=record, label=\"{Avg. discounted reward=" + this.MEU + "}\"];" + endl;
		
		dotString += endl;
		
		for (int edgeSource: this.getAllEdgeIds()) {
			
			for (Entry<List<String>, Integer> ends : this.getEdges(edgeSource).entrySet()) {
				
				dotString += " " + edgeSource + " -> " + ends.getValue()
					+ " [label=\"" + ends.getKey().toString() 
					+ "\"]" + endl;
			}
		}
		
		dotString += "}" + endl;
		
		return dotString;
	}
	
}
