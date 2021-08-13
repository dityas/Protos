/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.datastructures;

import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import thinclab.legacy.DD;
import thinclab.model_ops.belief_exploration.BreadthFirstExploration;
import thinclab.model_ops.belief_exploration.SSGAExploration;

/*
 * @author adityas
 *
 */
public class PBVISolvableFrameSolution {

	// horizon
	public final int H;

	// models
	public int frame;
	public ReachabilityGraph RG;
	public List<DD> b_is;
	public PBVISolvablePOMDPBasedModel m;
	public SymbolicPerseusSolver<PBVISolvablePOMDPBasedModel> s;
	public AlphaVectorPolicy<DD> Vn;

	public PBVISolvableFrameSolution(int f, PBVISolvablePOMDPBasedModel _m, int H) {

		this.H = H;
		this.frame = f;
		this.m = _m;
		Vn = AlphaVectorPolicy.fromR(m.R());
		s = new SymbolicPerseusSolver<>();
		b_is = List.of(m.b_i());
	}

	public void solve() {

		RG = ReachabilityGraph.fromDecMakingModel(m);
		b_is.stream().forEach(RG::addNode);
		RG = new BreadthFirstExploration<DD, PBVISolvablePOMDPBasedModel, ReachabilityGraph, AlphaVectorPolicy<DD>>(
				1000).expand(RG, m, H, Vn);

		Vn = s.solve(b_is, m, 100, H, new SSGAExploration<>(0.1f), Vn);

		// set next beliefs
		b_is = new ArrayList<>(RG.getChildren(b_is));
	}

	public List<Tuple3<Integer, DD, Integer>> mjList() {

		return RG.getAllNodes().stream().map(d -> Tuple.of(frame, d, Vn.getBestActionIndex(d)))
				.collect(Collectors.toList());
	}

}
