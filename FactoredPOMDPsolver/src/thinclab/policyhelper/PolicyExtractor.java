package thinclab.policyhelper;
import java.util.Map;

import thinclab.pomdpsolver.DD;
import thinclab.pomdpsolver.POMDP;
import thinclab.pomdpsolver.StateVar;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Collections;
import java.util.*;

public class PolicyExtractor {
		/*
		 * Creates policy graph from the given solved POMDP
		 * 
		 * Technique is based on Gary Turovsky's flyhighplato policy extractor written in groovy
		 * 
		 * Author: Aditya Shinde
		 */

		public List<PolicyNode> policyNodes = new ArrayList<PolicyNode>();
		
		private void recursiveObsGen(List<List<String>> obsComb, List<StateVar> obsVars, List<String> obsVector, int finalLen, int varIndex){
			/* 
			 *  Recursively generates a list of all possible combinations of values of the observation variables
			 */
			
			if (varIndex < obsVars.size()) {
				
				if (obsVector.size() == finalLen) {
					obsComb.add(obsVector);
				}
				
				else {
					
					List<String> obsVectorCopy = new ArrayList<String>(obsVector);
					StateVar obs = obsVars.get(varIndex);
					for (int i=0;i<obs.valNames.length;i++) {
						List<String> anotherObsVecCopy = new ArrayList<String>(obsVectorCopy);
						anotherObsVecCopy.add(obs.valNames[i]);
//						System.out.println("another obsVector: " + anotherObsVecCopy);
						recursiveObsGen(obsComb, obsVars, anotherObsVecCopy, finalLen, varIndex + 1);
					}
				}
				
			}
			
			else {
//				System.out.println("REACHED END of obsVars");
				obsComb.add(obsVector);
			}
		}
		
		private List<List<String>> recursiveObsCombinations(List<StateVar> obsVars){
			/*
			 * Driver program for generating observations recursively
			 */
			int finalLen = obsVars.size();
			List<String> obsVec = new ArrayList<String>();
			List<List<String>> obsComb = new ArrayList<List<String>>();
			
			recursiveObsGen(obsComb, obsVars, obsVec, finalLen, 0);
			
			return obsComb;
		}
		
		public PolicyExtractor(POMDP p) {
			PolicyNode nodeCurr = new PolicyNode();
			
			// Start policy graph from the initial belief
			// Get relevant alpha vector and action related to the node in the graph
			nodeCurr.belief = p.initialBelState;
			nodeCurr.alphaId = p.policyQuery(nodeCurr.belief, p.alphaVectors, p.policy);
			nodeCurr.actId = p.policy[nodeCurr.alphaId];
			
			List<PolicyNode> policyLeaves = new ArrayList<PolicyNode>();
			policyLeaves.add(nodeCurr);
			
			// Generate all possible observations
			List<StateVar> obsVars = new LinkedList<StateVar>(Arrays.asList(p.obsVars));
			List<List<String>> obs = recursiveObsCombinations(obsVars);
			System.out.println("Total possible observations: " + obs.size());
			
			// Do till there are no terminal policy leaves
//			int j = 5;
//			while(j!=0) {
			while(!policyLeaves.isEmpty()) {
				System.out.println("Policy has " + policyNodes.size() + " nodes... ");
				System.out.println("Computing for " + policyLeaves.size() + " leaves... ");
				nodeCurr = policyLeaves.remove(policyLeaves.size() - 1);
				List<PolicyNode> newLeaves = new ArrayList<PolicyNode>();
				
				// For all observations, perform belief updates and get best action nodes
				Enumeration<List<String>> obsEnum = Collections.enumeration(obs);
				while(obsEnum.hasMoreElements()) {
					List<String> theObs = obsEnum.nextElement();
					
					// Perform belief update and make next policy node
					PolicyNode nodeNext = new PolicyNode();
					nodeNext.belief = p.beliefUpdate(nodeCurr.belief, nodeCurr.actId, theObs.toArray(new String[0]));
					
					// Zero probability observations
					if (nodeNext.belief != DD.one) {
						nodeNext.alphaId = p.policyQuery(nodeNext.belief, p.alphaVectors, p.policy);
//						System.out.println(nodeNext.alphaId);
						nodeNext.actId = p.policy[nodeNext.alphaId];
						
						// Link new node to parent
						String obsString = String.join("|", theObs);
						nodeCurr.nextNode.put(obsString, nodeNext.alphaId);
						
						// Add nextNode to list of newly generated leaves
						newLeaves.add(nodeNext);
					}
					
					else {
						System.out.println("Skipping node");
					}
				}
				
				// Check for duplicate node in policyNodes
				Iterator<PolicyNode> policyNodeIter = policyNodes.iterator();
				boolean hasDuplicate = false;
				while(policyNodeIter.hasNext()) {
					PolicyNode existingNode = policyNodeIter.next();
					if (nodeCurr.shallowEquals(existingNode)) {
						hasDuplicate = true;
					}
				}
				
				// If node is unique, add to policy
				if (!hasDuplicate) {
					policyNodes.add(nodeCurr);
					policyLeaves.addAll(newLeaves);
				}
			}
		}

		@Override
		public String toString() {
			return "PolicyExtractor [policyNodes=" + policyNodes + "]";
		}
}
