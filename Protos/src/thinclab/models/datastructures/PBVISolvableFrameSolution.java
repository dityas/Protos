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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
import thinclab.model_ops.belief_exploration.BreadthFirstExploration;
import thinclab.model_ops.belief_exploration.MjSpaceExpansion;

/*
 * @author adityas
 *
 */
public class PBVISolvableFrameSolution {

	// horizon
	public final int H;

	// models
	public int frame;
	public ModelGraph<ReachabilityNode> MG;
	public List<ReachabilityNode> mj_i;
	public PBVISolvablePOMDPBasedModel m;
	public SymbolicPerseusSolver<PBVISolvablePOMDPBasedModel> s;
	public AlphaVectorPolicy Vn;
	
	private static final Logger LOGGER = LogManager.getLogger(PBVISolvableFrameSolution.class);

	public PBVISolvableFrameSolution(List<ReachabilityNode> mj_i, int f, PBVISolvablePOMDPBasedModel _m, int H) {

		this.H = H;
		this.frame = f;
		this.m = _m;
		Vn = AlphaVectorPolicy.fromR(m.R());
		s = new SymbolicPerseusSolver<>();
		this.mj_i = mj_i;
	}

	public void solve() {

		MG = ModelGraph.fromDecMakingModel(m);
		mj_i.stream().forEach(MG::addNode);
		MG = new MjSpaceExpansion<>().expand(mj_i, MG, m, H, Vn);
		
		// LOGGER.debug(String.format("Graphs is %s", ModelGraph.toDot(MG, m)));

		Vn = s.solve(mj_i.stream().flatMap(m -> m.beliefs.stream()).collect(Collectors.toList()), m, 100, H, Vn);
	}

	public List<Tuple<Integer, ReachabilityNode>> mjList() {

		return MG.getAllNodes().stream().map(d -> Tuple.of(frame, d)).collect(Collectors.toList());
	}

	public List<Tuple<Integer, ReachabilityNode>> bMjList() {

		return mj_i.stream().map(b -> Tuple.of(frame, b)).collect(Collectors.toList());
	}

	public Map<Tuple<Integer, ReachabilityNode>, Integer> mjToOPTAjMap() {

		return MG.getAllNodes().stream()
				.map(d -> Tuple.of(frame, d, Vn.getBestActionIndex(d.beliefs.stream().findAny().get(), m.i_S())))
				.collect(Collectors.toMap(t -> Tuple.of(t._0(), t._1()), t -> t._2()));
	}

	public List<Tuple3<Tuple<Integer, ReachabilityNode>, List<Integer>, Tuple<Integer, ReachabilityNode>>> getTriples() {

		return MG.getTriples().stream().map(t -> Tuple.of(Tuple.of(frame, t._0()), t._1(), Tuple.of(frame, t._2())))
				.collect(Collectors.toList());
	}

	public void step() {

		// set next beliefs
		//b_is = new ArrayList<>(RG.getChildren(b_is));
	}

}
