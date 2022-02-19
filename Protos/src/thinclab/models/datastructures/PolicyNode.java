/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.datastructures;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.policy.AlphaVectorPolicy;

/*
 * @author adityas
 *
 */
public class PolicyNode extends ReachabilityNode {

	final public String actName;
	
	public PolicyNode(int alphaId, int actId, String actName) {

		super(alphaId, actId, null);
		
		this.actName = actName;
	}
	
	public static ModelGraph<ReachabilityNode> expandFSM(List<DD> beliefs, ModelGraph<ReachabilityNode> g, PBVISolvablePOMDPBasedModel m, AlphaVectorPolicy p) {
		
//		var alphas = beliefs.parallelStream().map(b -> {
//			
//			var i_a = p.getBestActionIndex(b, m.i_S());
//			var alphaId = DDOP.bestAlphaIndex(p.aVecs, b, m.i_S());
//			
//			var nextBs = g.edgeIndexMap.keySet().parallelStream().filter(k -> k._0() == i_a).map(k -> m.beliefUpdate(b, i_a, k._1()));
//			
//		});
		
		return g;
	}
	
	public static ModelGraph<ReachabilityNode> makeFSM(List<ReachabilityNode> startNodes, PBVISolvablePOMDPBasedModel m, AlphaVectorPolicy p) {
		
		var G = ModelGraph.fromDecMakingModel(m);
		startNodes.forEach(G::addNode);
		
		var beliefs = startNodes.stream().flatMap(s -> s.beliefs.stream()).collect(Collectors.toList());
		
		return G;
	}
	
	@Override
	public String toString() {
		
		return new StringBuilder().append("{ \"alpha\" : ").append(alphaId)
				.append(" , \"i_a\" : ").append(i_a)
				.append(" , \"actName\" : \"").append(actName).append("\" }").toString();
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof PolicyNode))
			return false;

		var node = (PolicyNode) obj;

		if (node.alphaId == alphaId && node.i_a == i_a && node.actName.contentEquals(actName))
			return true;

		return false;
	}

	@Override
	public int hashCode() {

		var builder = new HashCodeBuilder();
		builder.append(alphaId).append(i_a).append(actName);

		return builder.toHashCode();
	}
}
