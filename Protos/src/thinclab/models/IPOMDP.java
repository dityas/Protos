/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.models.datastructures.PBVISolvableFrameSolution;
import thinclab.utils.Tuple;
import thinclab.utils.TwoWayMap;

/*
 * @author adityas
 *
 */
public class IPOMDP extends PBVISolvablePOMDPBasedModel {

	public final int H;

	public final List<String> Aj;
	public final int i_Aj;
	public final int i_Mj;
	public final int i_Mj_p;
	public final int i_Thetaj;
	public final List<String> Thetaj;

	public final List<String> Om_j;

	public final List<Integer> i_Om_j;
	public final List<Integer> i_Om_j_p;

	public DD PAjGivenMj;
	public DD PMj_pGivenMjAjOj_p;

	public List<Tuple<Integer, PBVISolvablePOMDPBasedModel>> frames_j;
	public List<PBVISolvableFrameSolution> frames_jSoln;

	// public HashMap<Tuple<Integer, DD>, Integer> mjMap = new HashMap<>(1000);
	public TwoWayMap<Tuple<Integer, DD>, String> mjMap = new TwoWayMap<>();

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
		this.i_Mj_p = this.i_Mj + (Global.NUM_VARS / 2);

		// random variable for frame of the opponent
		this.i_Thetaj = Global.varNames.indexOf(Thetaj) + 1;
		this.Thetaj = Global.valNames.get(i_Thetaj - 1);

		this.H = H;

		// initialize frames
		this.frames_j = frames_j.stream().map(
				t -> Tuple.of(Global.valNames.get(i_Thetaj - 1).indexOf(t._0()), (PBVISolvablePOMDPBasedModel) t._1()))
				.collect(Collectors.toList());

		// verify and prepare Oj
		var incorrectFrame = this.frames_j.stream().filter(f -> !f._1().Om().equals(this.frames_j.get(0)._1().Om()))
				.findAny();
		if (incorrectFrame.isPresent()) {

			LOGGER.error(String.format(
					"All frame should have the same set of observation variables. %s seems to be different.",
					incorrectFrame.get()));
			System.exit(-1);
		}

		Om_j = this.frames_j.get(0)._1().Om();
		i_Om_j = this.frames_j.get(0)._1().i_Om();
		i_Om_j_p = this.frames_j.get(0)._1().i_Om_p();

		// prepare structures for solving frames
		this.frames_jSoln = this.frames_j.stream().map(f -> new PBVISolvableFrameSolution(f._0(), f._1(), H))
				.collect(Collectors.toList());

		// create interactive state space using mjs
		updateIS();
	}

	public void updateIS() {

		createIS();

		// weird way to create initial belief the first time, but ok...
		if (PAjGivenMj == null)
			createFirstb_i();

		createPAjMj();
		createPMj_pMjAjOj_p();
	}

	public void createFirstb_i() {

		var b_js = frames_jSoln.stream().flatMap(f -> f.bMjList().stream().map(b -> mjMap.k2v.get(b)))
				.collect(Collectors.toList());
		var b_MjDD = b_js.stream()
				.map(b -> DDOP.mult(DDleaf.getDD(1.0f / b_js.size()),
						DDnode.getDDForChild(i_Mj,
								Collections.binarySearch(Global.valNames.get(i_Mj - 1), b.toString()))))
				.reduce(DD.zero, (d1, d2) -> DDOP.add(d1, d2));

		b_i = OP.mult(b_i, b_MjDD);
	}

	public void createIS() {

		this.frames_jSoln.stream().parallel().forEach(f ->
			{

				f.solve();
			});

		var mjsList = this.frames_jSoln.stream().flatMap(f -> f.mjList().stream()).collect(Collectors.toList());

		mjsList.stream().forEach(f ->
			{

				this.mjMap.put(Tuple.of(f._0(), f._1()), "m" + this.mjMap.k2v.size());
			});

		var sortedVals = mjMap.k2v.values().stream().collect(Collectors.toList());
		Collections.sort(sortedVals, (a, b) -> Integer.valueOf(a.split("m")[1]) - Integer.valueOf(b.split("m")[1]));

		Global.valNames.set(i_Mj - 1, sortedVals);
		Global.valNames.set(i_Mj_p - 1, sortedVals);
		Global.varDomSize.set(i_Mj - 1, sortedVals.size());
		Global.varDomSize.set(i_Mj_p - 1, sortedVals.size());

		LOGGER.debug(String.format("Interactive state space has %s models", Global.valNames.get(i_Mj - 1).size()));
	}

	public void createPAjMj() {

		var MjToOPTAj = frames_jSoln.stream().flatMap(f -> f.mjToOPTAjMap().entrySet().stream())
				.map(e -> Tuple.of(mjMap.k2v.get(e.getKey()), DDnode.getDDForChild(i_Aj, e.getValue() + 1)))
				.collect(Collectors.toList());

		Collections.sort(MjToOPTAj,
				(a, b) -> Integer.valueOf(a._0().split("m")[1]) - Integer.valueOf(b._0().split("m")[1]));

		var AjDDs = MjToOPTAj.stream().map(m -> m._1()).toArray(DD[]::new);
		PAjGivenMj = DDOP.reorder(DDnode.getDD(i_Mj, AjDDs));
	}

	public void createPMj_pMjAjOj_p() {

		var triples = frames_jSoln.stream().flatMap(f -> f.getTriples().stream())
				.map(f -> Tuple.of(mjMap.k2v.get(f._0()), f._1(), mjMap.k2v.get(f._2()))).map(t ->
					{

						// convert everything into list
						List<Integer> valList = new ArrayList<>(t._1().size() + 2);
						valList.add(Global.valNames.get(i_Mj - 1).indexOf(t._0()) + 1);
						valList.addAll(t._1());
						valList.add(Global.valNames.get(i_Mj_p - 1).indexOf(t._2()) + 1);

						return valList;
					})
				.collect(Collectors.toList());

		var varList = new ArrayList<Integer>(Om_j.size() + 2);
		varList.add(i_Mj);
		varList.add(i_Aj);
		varList.addAll(i_Om_j_p);
		varList.add(i_Mj_p);

		PMj_pGivenMjAjOj_p = triples.stream().map(t -> DDOP.reorder(DDnode.getDD(varList, t, 1.0f)))
				.reduce(DDleaf.getDD(0.0f), (d1, d2) -> DDOP.add(d1, d2));

	}

	public void printGraph(List<List<Integer>> triples) {

		var builder = new StringBuilder();
		builder.append("digraph {\r\n");
		triples.forEach(t ->
			{

				builder.append("\"" + mjMap.v2k.get(t.get(0)) + "\"").append(" -> ")
						.append("\"" + mjMap.v2k.get(t.get(t.size() - 1)) + "\"").append("\r\n");
			});

		builder.append("}");

		LOGGER.debug(builder.toString());
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
