/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policyhelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.Belief.Belief;

/*
 * @author adityas
 *
 */
public class PolicyTree {
	/*
	 * Constructs the policy tree for a solved POMDP
	 * 
	 * Similar to policy graph. But in this case, it computes the policy up to a given horizon
	 */
	
	public POMDP pomdp;
	public List<List<String>> allObsCombinations = new ArrayList<List<String>>();
	
	public List<PolicyNode> roots;
	public List<PolicyNode> policyNodes;
	
	// -------------------------------------------------------------------------------------
	
	public PolicyTree(POMDP p, int horizon) {
		/*
		 * Constructor makes the policy tree for the given horizon
		 */
		this.pomdp = p;
		
		this.roots = 
				this.pomdp.getInitialBeliefsList().stream()
												  .map(b -> new PolicyNode(
														  this.pomdp, 
														  b, 
														  0, 
														  horizon))
												  .collect(Collectors.toList());
		
		this.roots.stream().forEach(n -> n.setStartNode());
		this.expandForHorizon(horizon);
	}
	
	// --------------------------------------------------------------------------------------
	
	public List<PolicyNode> expandForSingleStep(List<PolicyNode> previousLeaves) {
		/*
		 * Expands the policy tree for a single time step
		 */
		List<PolicyNode> nextNodes = new ArrayList<PolicyNode>();
		
		Iterator<PolicyNode> leafIter = previousLeaves.iterator();
		while (leafIter.hasNext()) {
			PolicyNode policyNode = leafIter.next();
			
			/*
			 * Update for each observation 
			 */
			List<List<String>> obs = this.pomdp.getAllObservationsList();
			for (List<String> o : obs) {
				
				DD nextBelief;
				
				try {
					nextBelief = 
							Belief.beliefUpdate(
									this.pomdp, 
									policyNode.belief, 
									policyNode.actId, 
									o.toArray(new String[o.size()]));
				}
				
				catch (Exception e) {
					continue;
				}
				
				PolicyNode nextNode = new PolicyNode(this.pomdp, nextBelief);
				policyNode.nextPolicyNode.put(o, nextNode);
				nextNodes.add(nextNode);
			}
			
		}
		
		return nextNodes;
	}
	
	public void expandForHorizon(int horizons) {
		/*
		 * Expands the policy tree for given number of horizons
		 */
		this.policyNodes = new ArrayList<PolicyNode>();
		List<PolicyNode> previousLeaves = new ArrayList<PolicyNode>();
		
		previousLeaves.addAll(this.roots);
		this.policyNodes.addAll(this.roots);
		
		/*
		 * Expand the policy tree
		 */
		for (int h=0; h < horizons; h++) {
			List<PolicyNode> nextNodes = this.expandForSingleStep(previousLeaves);
			
			this.policyNodes.addAll(nextNodes);
			previousLeaves = nextNodes;
		}
	}
	
	public void indexNodes(int start) {
		/*
		 * Gives unique integer ids to policy nodes.
		 */
		IntStream.range(start, start + this.policyNodes.size()).forEach(i -> policyNodes.get(i).setId(i));
	}
	
}
