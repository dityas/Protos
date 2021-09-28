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
import java.util.Map;
import java.util.stream.Collectors;
import thinclab.legacy.DD;
import thinclab.model_ops.belief_exploration.BreadthFirstExploration;

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
	public AlphaVectorPolicy Vn;

	public PBVISolvableFrameSolution(List<DD> b_is, int f, PBVISolvablePOMDPBasedModel _m, int H) {

		this.H = H;
		this.frame = f;
		this.m = _m;
		Vn = AlphaVectorPolicy.fromR(m.R());
		s = new SymbolicPerseusSolver<>();
		this.b_is = b_is;
	}

	public void solve() {

		RG = ReachabilityGraph.fromDecMakingModel(m);
		b_is.stream().forEach(RG::addNode);
		RG = new BreadthFirstExploration<DD, PBVISolvablePOMDPBasedModel, ReachabilityGraph, AlphaVectorPolicy>(10000)
				.expand(b_is, RG, m, H, Vn);
	
		Vn = s.solve(b_is, m, 100, H, Vn);
	}

	public List<Tuple<Integer, DD>> mjList() {

		return RG.getAllNodes().stream().map(d -> Tuple.of(frame, d)).collect(Collectors.toList());
	}

	public List<Tuple<Integer, DD>> bMjList() {

		return b_is.stream().map(b -> Tuple.of(frame, b)).collect(Collectors.toList());
	}

	public Map<Tuple<Integer, DD>, Integer> mjToOPTAjMap() {

		return RG.getAllNodes().stream().map(d -> Tuple.of(frame, d, Vn.getBestActionIndex(d, m.i_S())))
				.collect(Collectors.toMap(t -> Tuple.of(t._0(), t._1()), t -> t._2()));
	}

	public List<Tuple3<Tuple<Integer, DD>, List<Integer>, Tuple<Integer, DD>>> getTriples() {

		return RG.getTriples().stream().map(t -> Tuple.of(Tuple.of(frame, t._0()), t._1(), Tuple.of(frame, t._2())))
				.collect(Collectors.toList());
	}

	public void step() {

		// set next beliefs
		b_is = new ArrayList<>(RG.getChildren(b_is));
	}

}
