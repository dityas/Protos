/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.models.datastructures.PBVISolvableFrameSolution;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class IPOMDP extends PBVISolvablePOMDPBasedModel {

	public final int H;

	public final List<String> Aj;
	public final int i_Aj;
	public final int i_Mj;
	public final int i_Thetaj;
	public final List<String> Thetaj;
	
	public DD PAjMj;

	public List<Tuple<Integer, PBVISolvablePOMDPBasedModel>> frames_j;
	public List<PBVISolvableFrameSolution> frames_jSoln;

	public HashMap<Tuple<Integer, DD>, Integer> mjMap = new HashMap<>(1000);

	private static final Logger LOGGER = LogManager.getLogger(IPOMDP.class);

	public IPOMDP(List<String> S, List<String> O, String A, String Aj, String Mj, String Thetaj,
			List<Tuple<String, Model>> frames_j, HashMap<String, Model> dynamics, HashMap<String, DD> R,
			DD initialBelief, float discount, int H) {

		// initialize dynamics like POMDP
		super(S, O, A, dynamics, R, initialBelief, discount);

		// random variable for opponent's action
		this.i_Aj = Global.varNames.indexOf(Aj) + 1;
		this.Aj = Global.valNames.get(i_Aj - 1);

		// opponent's model variable
		this.i_Mj = Global.varNames.indexOf(Mj) + 1;

		// random variable for frame of the opponent
		this.i_Thetaj = Global.varNames.indexOf(Thetaj) + 1;
		this.Thetaj = Global.valNames.get(i_Thetaj - 1);

		this.H = H;

		// initialize frames
		this.frames_j = frames_j.stream().map(
				t -> Tuple.of(Global.valNames.get(i_Thetaj - 1).indexOf(t._0()), (PBVISolvablePOMDPBasedModel) t._1()))
				.collect(Collectors.toList());

		// prepare structures for solving frames
		this.frames_jSoln = this.frames_j.stream().map(f -> new PBVISolvableFrameSolution(f._0(), f._1(), H))
				.collect(Collectors.toList());

		// create interactive state space using mjs
		updateIS();
	}

	public void updateIS() {

		createIS();
		
		// weird way to create initial belief the first time, but ok...
		if (PAjMj == null) createFirstb_i();
		
		createPAjMj();
	}
	
	public void createFirstb_i() {
		
		var b_js = frames_jSoln.stream().flatMap(f -> f.bMjList().stream()).collect(Collectors.toList());
		
		
	}

	public void createIS() {

		this.frames_jSoln.stream().parallel().forEach(f ->
			{

				f.solve();
			});

		var mjsList = this.frames_jSoln.stream().flatMap(f -> f.mjList().stream()).collect(Collectors.toList());

		mjsList.stream().forEach(f ->
			{

				this.mjMap.put(Tuple.of(f._0(), f._1()), this.mjMap.size());
			});

		var sortedVals = mjMap.values().stream().map(i -> i.toString()).collect(Collectors.toList());
		Collections.sort(sortedVals, (a, b) -> Integer.valueOf(a) - Integer.valueOf(b));
		
		Global.valNames.set(i_Mj - 1, sortedVals);
		Global.varDomSize.set(i_Mj - 1, sortedVals.size());
		
		LOGGER.debug(String.format("Interactive state space has %s models", Global.valNames.get(i_Mj - 1).size()));
	}

	public void createPAjMj() {

		var MjToOPTAj = frames_jSoln.stream().flatMap(f -> f.mjToOPTAjMap().entrySet().stream())
				.map(e -> Tuple.of(mjMap.get(e.getKey()), DDnode.getDDForChild(i_Aj, e.getValue() + 1))).collect(Collectors.toList());
		LOGGER.debug(String.format("P(Aj|Mj) is %s", MjToOPTAj));
		
		Collections.sort(MjToOPTAj, (a, b) -> a._0() - b._0());
		
		var AjDDs = MjToOPTAj.stream().map(m -> m._1()).toArray(DD[]::new);
		PAjMj = DDOP.reorder(DDnode.getDD(i_Mj, AjDDs));
		LOGGER.debug(String.format("P(Aj|Mj) is %s", PAjMj));
		LOGGER.debug(String.format("b(Mj) is %s", DDOP.addMultVarElim(List.of(b_i), i_S)));
	}

	@Override
	public DD beliefUpdate(DD b, int a, List<Integer> o) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DD beliefUpdate(DD b, String a, List<String> o) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DD obsLikelihoods(DD b, int a) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tuple<Float, DD> Gaoi(DD b, int a, List<DD> alphaPrimes) {

		// TODO Auto-generated method stub
		return null;
	}

}
