/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.datastructures;

import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.POMDP;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.model_ops.belief_exploration.MjSpaceExpansion;

/*
 * @author adityas
 *
 */
public class BjSpace {

	// horizon
	public final int H;

	// models
	public int frame;
	public ModelGraph<ReachabilityNode> MG;
	public List<ReachabilityNode> mj_i;
	public PBVISolvablePOMDPBasedModel m;
	public SymbolicPerseusSolver<PBVISolvablePOMDPBasedModel> s;
	public AlphaVectorPolicy Vn;

	private static final Logger LOGGER = LogManager.getLogger(BjSpace.class);

	public BjSpace(List<ReachabilityNode> mj_i, int f, PBVISolvablePOMDPBasedModel _m, int H) {

		this.H = H;
		this.frame = f;
		this.m = _m;
		Vn = AlphaVectorPolicy.fromR(m.R());
		s = new SymbolicPerseusSolver<>();
		this.mj_i = mj_i;
	}

	public void solve() {

		if (mj_i.size() > 0) {

			LOGGER.info("Solving opponent");
			var b_list = mj_i.stream().flatMap(m -> m.beliefs.stream()).collect(Collectors.toList());
			Vn = s.solve(b_list, m, 100, H, Vn);

			MG = ModelGraph.fromDecMakingModel(m);

			mj_i.stream().forEach(MG::addNode);
			MG = new MjSpaceExpansion<>().expand(mj_i, MG, m, H, Vn);
		}
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

	public void step(Set<Tuple<Integer, ReachabilityNode>> modelFilter) {

		if (m instanceof POMDP) {

			var mjs = MG.getChildren(mj_i).stream().filter(m -> modelFilter.contains(Tuple.of(frame, m)))
					.collect(Collectors.toList());
			mjs.forEach(m -> m.h = 0);
			mj_i = new ArrayList<>(mjs);
		}

		else {

			LOGGER.error("Stepping for L1+ IPOMDPs is not implemented yet");
			System.exit(-1);
		}
	}

}
