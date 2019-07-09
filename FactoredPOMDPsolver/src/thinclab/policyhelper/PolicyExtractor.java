package thinclab.policyhelper;
import java.util.Map;

import thinclab.symbolicperseus.Belief;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.OP;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.StateVar;
import thinclab.domainMaker.ddHelpers.DDMaker;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.policyhelper.PolicyNode;

import java.util.HashMap;
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
		
		private boolean checkZeroProbabObs(DD belstate) {
			/*
			 * method to check if the belief state was a result of a zero probability obs
			 */
			DD uniformBelState = DD.one;
			uniformBelState = OP.div(uniformBelState,
					OP.addMultVarElim(uniformBelState, this.p.varIndices));
			
			if (belstate == uniformBelState) {
				return true;
			}
			
			else {
				return false;
			}
			
		} // private boolean checkZeroProbabObs(DD belstate)
		
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
						recursiveObsGen(obsComb, obsVars, anotherObsVecCopy, finalLen, varIndex + 1);
					}
				}
				
			}
			
			else {
				obsComb.add(obsVector);
			}
		} // private void recursiveObsGen
		
		private List<List<String>> recursiveObsCombinations(List<StateVar> obsVars){
			/*
			 * Driver program for generating observations recursively
			 */
			int finalLen = obsVars.size();
			List<String> obsVec = new ArrayList<String>();
			List<List<String>> obsComb = new ArrayList<List<String>>();
			
			recursiveObsGen(obsComb, obsVars, obsVec, finalLen, 0);
			
			return obsComb;
		} // private List<List<String>> recursiveObsCombinations
		
		public PolicyExtractor(POMDP p) {
			this.p = p;
			System.out.println("Policy has " + p.alphaVectors.length + " a vectors");
			PolicyNode nodeCurr = new PolicyNode();
			
			// Start policy graph from the initial belief
			// Get relevant alpha vector and action related to the node in the graph
			nodeCurr.belief = p.initialBelState;
			nodeCurr.alphaId = p.policyBestAlphaMatch(nodeCurr.belief, p.alphaVectors, p.policy);
			nodeCurr.actId = p.policy[nodeCurr.alphaId];
			nodeCurr.actName = this.p.actions[nodeCurr.actId].name;
			nodeCurr.factoredBelief = Belief.toStateMap(this.p, nodeCurr.belief);
			nodeCurr.startNode = true;
//			System.out.println("Suggesting action " + p.actions[nodeCurr.actId].name);
			
			List<PolicyNode> policyLeaves = new ArrayList<PolicyNode>();
			policyLeaves.add(nodeCurr);
			
			// Add other initial beliefs
			
			if (this.p.adjuncts != null) {
				for (int i=0; i < this.p.adjuncts.length; i ++) {
					PolicyNode other = new PolicyNode();
					other.belief = this.p.adjuncts[i];
					other.alphaId = this.p.policyBestAlphaMatch(other.belief, 
							this.p.alphaVectors, 
							this.p.policy);
					other.actId = this.p.policy[other.alphaId];
					other.actName = this.p.actions[other.actId].name;
					other.factoredBelief = Belief.toStateMap(this.p, other.belief);
					other.startNode = true;
					
					policyLeaves.add(other);
				}
			}
			
			// Generate all possible observations
			List<StateVar> obsVars = new LinkedList<StateVar>(Arrays.asList(p.obsVars));
			List<List<String>> obs = recursiveObsCombinations(obsVars);
//			System.out.println("Total possible observations: " + obs.size());
			
			// Do till there are no terminal policy leaves
			while(!policyLeaves.isEmpty()) {
//				System.out.println(policyNodes);
//				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//				System.out.println("LEAVES: " + policyLeaves);
				nodeCurr = policyLeaves.remove(0);
				List<PolicyNode> newLeaves = new ArrayList<PolicyNode>();
//				System.out.println("CURRNODE: " + nodeCurr);
//				System.out.println("LEAVES: " + policyLeaves);
//				System.out.println("POLICY: " + policyNodes);
				
				// For all observations, perform belief updates and get best action nodes
				Enumeration<List<String>> obsEnum = Collections.enumeration(obs);
				while(obsEnum.hasMoreElements()) {
//					System.out.println("==============================================================================");
//					System.out.println("Policy has " + policyNodes.size() + " nodes and " + policyLeaves.size() + " unexplored leaves ");
					List<String> theObs = obsEnum.nextElement();
					
//					System.out.println("COMPUTING NEXT NODES FOR NODE " + nodeCurr.alphaId + 
//							" TAKING ACTION " + this.p.actions[nodeCurr.actId].name +
//							" AND OBSERVING " + theObs);
					// Perform belief update and make next policy node
//					System.out.println("CURRENT BELIEF: " + this.p.getBeliefStateMap(nodeCurr.belief));
					PolicyNode nodeNext = new PolicyNode();
					
					try {
						nodeNext.belief = p.safeBeliefUpdate(nodeCurr.belief, nodeCurr.actId, theObs.toArray(new String[0]));
//						System.out.println("FOR OBSERVATION: " + theObs + " ACTION: " + nodeCurr.actId);
//						System.out.println("NEXTNODE BELIEF: " + this.p.getBeliefStateMap(nodeNext.belief));
						
//						System.out.println("Showing belief state");
						nodeNext.alphaId = p.policyBestAlphaMatch(nodeNext.belief, p.alphaVectors, p.policy);
						nodeNext.actId = p.policy[nodeNext.alphaId];
						nodeNext.factoredBelief = Belief.toStateMap(this.p, nodeNext.belief);
						nodeNext.actName = this.p.actions[nodeNext.actId].name;
//						System.out.println("NEXT ALPHA: " + nodeNext.alphaId);
//						System.out.println("NEXT ACT: " + nodeNext.actId);
						
						// Link new node to parent
						nodeCurr.nextNode.put(theObs, nodeNext.alphaId);
						
						// Add nextNode to list of newly generated leaves
						newLeaves.add(nodeNext);
					}
					
					catch (ZeroProbabilityObsException e) {
//						System.out.println("Exception " + e.getMessage());
//						System.out.println("Skipping node");
					}
					// Zero probability observations
//					if (!this.checkZeroProbabObs(nodeNext.belief)) {
				} // while(obsEnum.hasMoreElements())
				
				nodeCurr.belief = null;
				// Check for duplicate node in policyNodes
//				System.out.println("CURRENT POLICY: " + policyNodes);
//				System.out.println("LOOKING FOR: " + nodeCurr);
				Iterator<PolicyNode> policyNodeIter = policyNodes.iterator();
				boolean hasDuplicate = false;
				while(policyNodeIter.hasNext()) {
					PolicyNode existingNode = policyNodeIter.next();
					if (nodeCurr.actId == existingNode.actId && nodeCurr.alphaId == existingNode.alphaId) {
//					if (existingNode.shallowEquals(nodeCurr)) {
						hasDuplicate = true;
//						System.out.println("SAME NODE: " + existingNode);
//						policyNodes.remove(existingNode);
					}
				}
				
				// If node is unique, add to policy
				if (!hasDuplicate) {
//					System.out.println("UNIQUE NODE: " + nodeCurr);
					policyNodes.add(nodeCurr);
					policyLeaves.addAll(newLeaves);
				}
				
				else {
//					System.out.println("Found same node");
				}
				
//				System.out.println("" + policyNodes.size() + " policy nodes");
			} // while(!policyLeaves.isEmpty())
		} // public PolicyExtractor
		
		public void makePolicyRepresentation() {
			/*
			 * Combines observations leading to the same next nodes in the policy in order to display it properly
			 */
			
		}

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
