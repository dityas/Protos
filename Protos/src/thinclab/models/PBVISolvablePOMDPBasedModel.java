/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.legacy.TypedCacheMap;
import thinclab.solver.PBVISolvable;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;

/*
 * @author adityas
 *
 */
public abstract class PBVISolvablePOMDPBasedModel implements PBVISolvable, POSeqDecMakingModel<DD> {

	public final List<String> S;
	public final List<String> O;
	public final List<String> A;
	public final float discount;

	public final List<Integer> i_S;
	public final List<Integer> i_Om;
	public final int i_A;

	public final List<Integer> i_S_p;
	public final List<Integer> i_Om_p;
	public final List<List<Integer>> oAll;

	public final List<List<DD>> TF;
	public final List<List<DD>> OF;
	public List<DD> R;
	
	public String name;

	protected TypedCacheMap<DD, HashMap<Integer, List<Tuple3<Integer, DD, Float>>>> 
        belCache = new TypedCacheMap<>(1000);
	protected TypedCacheMap<Tuple<DD, Integer>, DD> lCache = new TypedCacheMap<>(1000);
	private static final Logger LOGGER = 
        LogManager.getLogger(PBVISolvablePOMDPBasedModel.class);

	public PBVISolvablePOMDPBasedModel(
            List<String> S, List<String> O, String A, 
            HashMap<String, Model> dynamics,
			HashMap<String, DD> R, float discount) {

		// variable names
		this.S = Global.sortByVarOrdering(S, Global.varNames);
		this.O = Global.sortByVarOrdering(O, Global.varNames);
		this.A = Global.valNames.get(Global.varNames.indexOf(A));

		// variable indices
		this.i_S = this.S.stream()
            .map(s -> Global.varNames.indexOf(s) + 1)
            .collect(Collectors.toList());
		this.i_Om = this.O.stream()
            .map(o -> Global.varNames.indexOf(o) + 1)
            .collect(Collectors.toList());
		this.i_A = Global.varNames.indexOf(A) + 1;

		// primed variable indices
		this.i_S_p = this.i_S.stream()
            .map(i -> i + (Global.NUM_VARS / 2))
            .collect(Collectors.toList());
		this.i_Om_p = this.i_Om.stream()
            .map(i -> i + (Global.NUM_VARS / 2))
            .collect(Collectors.toList());

		// all possible observations
		this.oAll = DDOP.cartesianProd(i_Om.stream()
                .map(o -> IntStream.range(1, Global.valNames.get(o - 1).size() + 1)
                    .boxed()
                    .collect(Collectors.toList()))
				.collect(Collectors.toList()));

		// take out DBNs from set of models
		var dyn = new HashMap<String, DBN>(5);
		dyn.putAll(dynamics.entrySet().stream()
                .filter(e -> e.getValue() instanceof DBN)
				.collect(Collectors.toMap(e -> e.getKey(), e -> (DBN) e.getValue())));

		// Populate dynamics for missing actions
		Global.valNames.get(this.i_A - 1).stream().forEach(a ->
			{

				if (!dyn.containsKey(a)) {

					LOGGER.warn(String.format("Dynamics not defined for action %s. "
							+ "Will apply with SAME transitions and random observations for that action", a));
					dyn.put(a, new DBN(new HashMap<Integer, DD>(1)));
				}
			});

		this.TF = this.A.stream()
            .map(a -> this.getTransitionFunction(dyn.get(a)))
            .collect(Collectors.toList());
		this.OF = this.A.stream()
            .map(a -> this.getObsFunction(dyn.get(a)))
            .collect(Collectors.toList());

		var _R = this.A.stream()
            .map(a -> R.containsKey(a) ? R.get(a) : DD.zero)
            .collect(Collectors.toList());
		
		this.R = IntStream.range(0, _R.size()).boxed().map(i -> {
			var r = new ArrayList<DD>(this.i_S.size() + 1);
			r.addAll(this.TF.get(i));
			r.add(_R.get(i));
			
			return DDOP.addMultVarElim(r, i_S_p());
		}).collect(Collectors.toList());
		
		this.discount = discount;

	}

	protected List<DD> getTransitionFunction(DBN dbn) {

		var Ta = i_S.stream()
            .map(s -> dbn.cpds.containsKey(s) ? 
                    dbn.cpds.get(s) 
                    : DBN.getSameTransitionDD(Global.varNames.get(s - 1)))
				.collect(Collectors.toList());

		return Ta;
	}

	protected List<DD> getObsFunction(DBN dbn) {

		var Oa = i_Om.stream()
				.map(o -> dbn.cpds.containsKey(o) ? 
                        dbn.cpds.get(o) 
                        : DDnode.getUniformDist(o + (Global.NUM_VARS / 2)))
				.collect(Collectors.toList());

		return Oa;
	}
		
	public void clearBackupCache() {
		belCache.clear();
	}

	// ------------------------------------------------------------------------------
	// POSeqDecMakingModel implementations

	@Override
	public List<Integer> i_S() {

		return this.i_S;
	}

	@Override
	public List<Integer> i_S_p() {

		return this.i_S_p;
	}

	@Override
	public List<Integer> i_Om_p() {

		return this.i_Om_p;
	}

	@Override
	public List<String> S() {

		return this.S;
	}

	@Override
	public List<Integer> i_Om() {

		return this.i_Om;
	}

	@Override
	public int i_A() {

		return this.i_A;
	}

	@Override
	public List<String> Om() {

		return this.O;
	}

	@Override
	public List<String> A() {

		return this.A;
	}

	@Override
	public List<List<DD>> O() {

		return this.OF;
	}

	@Override
	public List<List<DD>> T() {

		return this.TF;
	}

	@Override
	public List<DD> R() {

		return this.R;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

}
