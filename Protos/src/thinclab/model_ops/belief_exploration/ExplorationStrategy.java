/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops.belief_exploration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.google.common.graph.MutableNetwork;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.model_ops.belief_update.BeliefUpdate;
import thinclab.models.Model;
import thinclab.models.POMDP;
import thinclab.policy.Policy;

/*
 * @author adityas
 *
 */
public class ExplorationStrategy {
	
	public static MutableNetwork<DD, List<String>> exploreFullReachabilityGraph(final POMDP M,
			final BeliefUpdate<Model> BU, MutableNetwork<DD, List<String>> RG,
			final int depth, final int maxB) {
		
		int _depth = 0;
		var edges = Arrays.stream(M.Ovars).mapToObj(i -> Global.valNames.get(i - 1)).collect(Collectors.toList());
		edges.add(M.A);
		
		List<DD> beliefs = new ArrayList<>();
		beliefs.add(M.b);
		
		while (_depth < depth) {
			
			if (beliefs.size() <= 0) break;
			
			DD b = beliefs.remove(0);
			for (var edge : edges) {
				
				
				
			}
			
		}
		
		return null;
	}

	public static MutableNetwork<DD, List<String>> getExpandedReachabilityGraph(final POMDP M,
			final BeliefUpdate<POMDP> BU, MutableNetwork<DD, List<String>> BR, final Policy pi,
			final float explorationProb) {

		final float policyProb = 1 - explorationProb;
		DD b = M.b;
		
		// make an array of all state vars to compute observation likelihoods later
		int[] SPvars = Arrays.stream(M.Svars).map(i -> i + (Global.NUM_VARS / 2)).toArray();
		int[] allSvars = new int[2 * M.Svars.length];
		System.arraycopy(M.Svars, 0, allSvars, 0, M.Svars.length);
		System.arraycopy(SPvars, 0, allSvars, M.Svars.length, SPvars.length);
		
		/*
		 * If sampled prob > 1 - exploration prob, random action, else use best action
		 * according to policy for exploring the belief space
		 */
		int act;

		if (Global.random.nextFloat() > policyProb)
			act = Global.random.nextInt(M.A.size());

		else
			act = pi.getBestActionIndex(b);
		
		// Sample observation according to likelihood at belief b
		DD[] allFactors = new DD[1 + M.Svars.length + M.Ovars.length];
		allFactors[0] = b;
		System.arraycopy(M.TF[act], 0, allFactors, 1, M.TF[act].length);
		System.arraycopy(M.OF[act], 0, allFactors, 1 + M.Svars.length, M.OF[act].length);
		
		DD likelihoods = OP.addMultVarElim(allFactors, allSvars);

		return null;
	}

}
