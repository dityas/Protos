import java.util.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class PolicyExtractor {
		class PolicyNode {
			int alphaId=-1;
			int actId = 1;
			DD belief;
			Map<String,Integer> nextNode = new HashMap<String,Integer>();

			@Override
			public String toString() {
				return "PolicyNode [alphaId=" + alphaId + ", actId=" + actId + ", belief=" + belief + ", nextNode="
						+ nextNode.toString() + "]\r\n";
			}	}
		
		List<PolicyNode> policyNodes = new ArrayList<PolicyNode>();
		
		private void recursiveObsGen(List<List<String>> obsComb, List<StateVar> obsVars, List<String> obsVector, int finalLen, int varIndex){
			/* 
			 * Recursively generates a list of all possible combination values
			*/
			System.out.println("obsVector: " + obsVector);
			System.out.println("obsVars: " + obsVars);
			System.out.println("obsComb: " + obsComb);
			System.out.println("vecLen: " + obsVector.size());
			System.out.println("Reqd: " + finalLen);
			System.out.println("varIndex: " + varIndex);
			System.out.println();
			
			if (varIndex <= obsVars.size()) {
				if (obsVector.size() == finalLen) {
					obsComb.add(obsVector);
					varIndex = 0;
					obsVector = new ArrayList<String>();
				}
				
				StateVar obs = obsVars.get(varIndex);
				
				for (int i=0; i<obs.valNames.length; i++) {
					List<String> newObsVector = obsVector.subList(0, obsVector.size());
					newObsVector.add(obs.valNames[i]);
					System.out.println("obsVector: " + obsVector);
					System.out.println("newObsVector: " + newObsVector);
					recursiveObsGen(obsComb, obsVars, newObsVector, finalLen, varIndex + 1);
					break;
				}
			}
		}
		
		private List<List<String>> recursiveObsCombinations(List<StateVar> obsVars){
			int finalLen = obsVars.size();
			List<String> obsVec = new ArrayList<String>();
			List<List<String>> obsComb = new ArrayList<List<String>>();
			
			recursiveObsGen(obsComb, obsVars, obsVec, finalLen, 0);
			
			return obsComb;
		}
		
		public PolicyExtractor(POMDP p) {
			PolicyNode nodeCurr = new PolicyNode();
			
			nodeCurr.belief = p.initialBelState;
			nodeCurr.alphaId = p.policyQuery(nodeCurr.belief, p.alphaVectors, p.policy);
			nodeCurr.actId = p.policy[nodeCurr.alphaId];
			
			List<PolicyNode> policyLeaves = new ArrayList<PolicyNode>();
			policyLeaves.add(nodeCurr);
			
			// Generate all observations
			List<StateVar> obsVars = new LinkedList<StateVar>(Arrays.asList(p.obsVars));
			
			List<List<String>> obs = recursiveObsCombinations(obsVars);
			
			while(!policyLeaves.isEmpty()) {
				
				nodeCurr = policyLeaves.remove(policyLeaves.size() - 1);
				
				List<PolicyNode> newLeaves = new ArrayList<PolicyNode>();
				break;
			}
//				2.times { collNum ->
//					System.out.println(collNum);
//					4.times{ alocNum ->
//						System.out.println("=====================================");
//						System.out.println("belief");
//						nodeCurr.belief.printSpuddDD(System.out);
//						System.out.println("policy");
//						System.out.println(policyLeaves);
//						
//						List obs = ["aloc_$alocNum","coll_$collNum"]
//						System.out.println(obs as String[]);
//						PolicyNode nodeNext = new PolicyNode()
//						nodeNext.belief = p.beliefUpdate(nodeCurr.belief,nodeCurr.actId,obs as String[])
//						
//						if(nodeNext.belief!=DD.one) {
//							nodeNext.alphaId = p.policyBestAlphaMatch(nodeNext.belief, p.alphaVectors, p.policy);
//							nodeCurr.nextNode[obs] = nodeNext.alphaId
//							nodeNext.actId = p.policy[nodeNext.alphaId]
//							newLeaves << nodeNext
//						}
//						
//						System.out.println("=====================================\r\n\r\n");
//					}
//				}
//				
//				nodeCurr.belief = null
//				
//				
//				PolicyNode matchedNode = policyNodes.find{nodeOther -> 
//					nodeOther.alphaId == nodeCurr.alphaId &&
//					nodeOther.actId == nodeCurr.actId &&
//					mapsEqual(nodeOther.nextNode,nodeCurr.nextNode)	
//				}
//				
//				if(matchedNode) {
//					println "Found same policy node!"
//				}
//				else {
//					policyNodes << nodeCurr
//					policyLeaves.addAll(newLeaves)
//				}
//				
//				println "${policyNodes.size()} policy nodes"
//			}
//			
//			policyNodes.each{ node ->
//				print node.alphaId + ":" + p.actions[node.actId].name + " -> {"
//				node.nextNode.each{ k,v ->
//					print v + " "
//				}
//				println "}"
//			}
//		}
//		
//		public boolean mapsEqual(Map m1, Map m2) {
//			if(m1.size() != m2.size()) 
//				return false;
//				
//			m1.each{k1,v1 ->
//				m2.each{k2,v2 ->
//					if(m1[k1]!=m2[k2])
//						return false
//				}
//			}
//			
//			return true
		}

		@Override
		public String toString() {
			return "PolicyExtractor [policyNodes=" + policyNodes + "]";
		}
}
