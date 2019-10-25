package thinclab.policyhelper;
import java.util.Map;

import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.policyhelper.PolicyNode;

import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Collections;
import java.util.*;
import java.io.*;

public class PolicyExtractor {
		/*
		 * Creates policy graph from the given solved POMDP
		 * 
		 * Technique is based on Gary Turovsky's flyhighplato policy extractor written in groovy
		 * 
		 * Author: Aditya Shinde
		 */

		public List<PolicyNode> policyNodes = new ArrayList<PolicyNode>();
		public List<PolicyNode> printablePolicyNodes = new ArrayList<PolicyNode>();
		private POMDP p = null;
		
		// ----------------------------------------------------------------------------------------
		
		public PolicyExtractor(POMDP p) {
			this.p = p;
			System.out.println("Policy has " + p.alphaVectors.length + " a vectors");
			
			/*
			 * Start graph with initial belief
			 */
			PolicyNode nodeCurr = new PolicyNode(this.p, this.p.initialBelState);
			nodeCurr.startNode = true;
			
			List<PolicyNode> policyLeaves = new ArrayList<PolicyNode>();
			policyLeaves.add(nodeCurr);
			
			/*
			 *  Add other initial beliefs
			 */
			if (this.p.adjuncts != null) {
				for (int i=0; i < this.p.adjuncts.length; i ++) {
					/*
					 * Add adjunct
					 */
					PolicyNode other = new PolicyNode(this.p, this.p.adjuncts[i]);
					other.startNode = true;
					
					policyLeaves.add(other);
				}
			}
			
			/*
			 *  Generate all possible observations
			 */
//			List<StateVar> obsVars = new LinkedList<StateVar>(Arrays.asList(p.obsVars));
			List<List<String>> obs = this.p.getAllPossibleObservations();
			
			/*
			 *  Do till there are no terminal policy leaves
			 */
			while(!policyLeaves.isEmpty()) {
				
				nodeCurr = policyLeaves.remove(0);
				List<PolicyNode> newLeaves = new ArrayList<PolicyNode>();
				
				/*
				 *  For all observations, perform belief updates and get best action nodes
				 */
				Enumeration<List<String>> obsEnum = Collections.enumeration(obs);
				while(obsEnum.hasMoreElements()) {
					
					List<String> theObs = obsEnum.nextElement();
					
					try {
						DD nextBel = p.safeBeliefUpdate(nodeCurr.belief, nodeCurr.actId, theObs.toArray(new String[0]));
						PolicyNode nodeNext = new PolicyNode(this.p, nextBel);
						
						/*
						 *  Link new node to parent
						 */
						nodeCurr.nextNode.put(theObs, nodeNext.alphaId);
						
						/*
						 *  Add nextNode to list of newly generated leaves
						 */
						newLeaves.add(nodeNext);
					}
					
					catch (ZeroProbabilityObsException e) {
					}
					
					/*
					 *  Zero probability observations
					 */

				} // while(obsEnum.hasMoreElements())
				
				Iterator<PolicyNode> policyNodeIter = policyNodes.iterator();
				boolean hasDuplicate = false;
				while(policyNodeIter.hasNext()) {
					PolicyNode existingNode = policyNodeIter.next();
					if (nodeCurr.actId == existingNode.actId && nodeCurr.alphaId == existingNode.alphaId) {
						hasDuplicate = true;
					}
				}
				
				/*
				 *  If node is unique, add to policy
				 */
				if (!hasDuplicate) {
					policyNodes.add(nodeCurr);
					policyLeaves.addAll(newLeaves);
				}
				
				else {
				}
				
			} // while(!policyLeaves.isEmpty())
		} // public PolicyExtractor

		// ----------------------------------------------------------------------------------------		

		@Override
		public String toString() {
			return "PolicyExtractor [policyNodes=" + policyNodes + "]";
		}
		
		public void writeDot(PrintWriter dotFileWriter) {
		/*
		 * Writes the policy graph in a dot file for visualization
		 */
			dotFileWriter.println("digraph Policy {");
			Iterator<PolicyNode> nodeIterator = policyNodes.iterator();
			
			while(nodeIterator.hasNext()) {
				PolicyNode theNode = nodeIterator.next();
				String action = this.p.actions[theNode.actId].name;
				String nodeDefn = theNode.getDotHeader(action);
				dotFileWriter.println(nodeDefn);
			} // while(nodeIterator.hasNext())
			
			nodeIterator = policyNodes.iterator();
			while(nodeIterator.hasNext()) {
				PolicyNode theNode = nodeIterator.next();
				Iterator<Map.Entry<List<String>, Integer>> mapIter = theNode.nextNode.entrySet().iterator();
				while(mapIter.hasNext()) {
					Map.Entry<List<String>, Integer> entry = mapIter.next();
					dotFileWriter.println(" " + theNode.alphaId + " -> " + entry.getValue());
				}
			} // while(nodeIterator.hasNext())
			dotFileWriter.println("}");
			dotFileWriter.close();
		} // public void writeDot
		
}
