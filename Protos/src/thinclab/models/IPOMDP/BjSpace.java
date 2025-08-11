/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.IPOMDP;

import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.POMDP;
import thinclab.models.datastructures.ModelGraph;
import thinclab.models.datastructures.ReachabilityNode;
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
import thinclab.legacy.Global;
import thinclab.model_ops.belief_exploration.MjSpaceExpansion;

/*
 * @author adityas
 *
 */
public class BjSpace {

//	final public int frame;
//	final public ModelGraph<ReachabilityNode> MG;
//	final public PBVISolvablePOMDPBasedModel m;
//	final public SymbolicPerseusSolver<PBVISolvablePOMDPBasedModel> s;
//	final public AlphaVectorPolicy Vn;
//
//	private static final Logger LOGGER = LogManager.getLogger(BjSpace.class);
//
//	public BjSpace(List<ReachabilityNode> mj_i, int f, PBVISolvablePOMDPBasedModel _m, int H) {
//
//		this.frame = f;
//		this.m = _m;
//		Vn = AlphaVectorPolicy.fromR(m.R());
//		s = new SymbolicPerseusSolver<>();
//	}
//
//	public void solve() {
//
//		if (mj_i.size() > 0) {
//
//			LOGGER.info("Solving opponent");
//			var b_list = mj_i.stream().flatMap(m -> m.beliefs.stream()).collect(Collectors.toList());
//			Vn = s.solve(b_list, m, 100, H, Vn);
//
//			MG = ModelGraph.fromDecMakingModel(m);
//			mj_i.stream().forEach(MG::addNode);
//			
//			MG = new MjSpaceExpansion<>().expand(mj_i, MG, m, H, Vn);
//		}
//	}
//
//	public List<Tuple<Integer, ReachabilityNode>> mjList() {
//
//		return MG.getAllNodes().stream().map(d -> Tuple.of(frame, d)).collect(Collectors.toList());
//	}
//
//	public List<Tuple<Integer, ReachabilityNode>> bMjList() {
//
//		return mj_i.stream().map(b -> Tuple.of(frame, b)).collect(Collectors.toList());
//	}
//
//	public Map<Tuple<Integer, ReachabilityNode>, Integer> mjToOPTAjMap() {
//
//		return MG.getAllNodes().stream()
//				.map(d -> Tuple.of(frame, d, Vn.getBestActionIndex(d.beliefs.stream().findAny().get(), m.i_S())))
//				.collect(Collectors.toMap(t -> Tuple.of(t._0(), t._1()), t -> t._2()));
//	}
//
//	public List<Tuple3<Tuple<Integer, ReachabilityNode>, List<Integer>, Tuple<Integer, ReachabilityNode>>> getTriples() {
//
//		return MG.getTriples().stream().map(t -> Tuple.of(Tuple.of(frame, t._0()), t._1(), Tuple.of(frame, t._2())))
//				.collect(Collectors.toList());
//	}
//
////	public void step(Set<Tuple<Integer, ReachabilityNode>> modelFilter) {
////
////		if (m instanceof POMDP) {
////
////			var mjs = MG.getChildren(mj_i).stream().filter(m -> modelFilter.contains(Tuple.of(frame, m)))
////					.collect(Collectors.toList());
////			mjs.forEach(m -> m.h = 0);
////			mj_i = new ArrayList<>(mjs);
////		}
////
////		else {
////
////			LOGGER.error("Stepping for L1+ IPOMDPs is not implemented yet");
////			System.exit(-1);
////		}
////	}
//
//	public void step() {
//
////		if (m instanceof POMDP) {
////
////			var mjs = MG.getChildren(mj_i).stream()//.filter(m -> modelFilter.contains(Tuple.of(frame, m)))
////					.collect(Collectors.toList());
////			mjs.forEach(m -> m.h = 0);
////			mj_i = new ArrayList<>(mjs);
////		}
////
////		else {
////			
////			var _m = (IPOMDP) m;
////			
////			var mjs = MG.getChildren(mj_i).stream().map(n -> Tuple.of(n.i_a, n.alphaId, Global.decoupleMj(n.beliefs.stream().findFirst().get(), _m.i_Mj)))
////					.collect(Collectors.toList());
////		
////			LOGGER.debug(String.format("Next nodes are %s", MG.getChildren(mj_i)));
////			
////			m.step();
////			
////			var new_mjs = mjs.stream().map(n -> Global.assemblebMj(_m.i_Mj, n._2())).collect(Collectors.toList());
////			
////			LOGGER.debug(String.format("Mj space successfully reassambled to %s", new_mjs));
////			
////			LOGGER.error("Stepping for L1+ IPOMDPs is not implemented yet");
////			System.exit(-1);
////		}
//	}
}
