/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.env;

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
import thinclab.models.DBN;
import thinclab.models.Model;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.POSeqDecMakingModel;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class PartiallyObservableEnv implements Environment<DD> {

	public String name;

	public final List<String> S;
	public final List<String> Om;

	public final List<Integer> i_S;
	public final List<Integer> i_S_p;
	public final List<Integer> i_Om_p;

	public final List<DD> T;
	public final List<DD> O;

	public DD s;
	private static final Logger LOGGER = LogManager.getLogger(PartiallyObservableEnv.class);

	public PartiallyObservableEnv(List<String> S, List<String> O, DBN dynamics, HashMap<String, DD> R) {

		this.S = Global.sortByVarOrdering(S, Global.varNames);
		this.Om = Global.sortByVarOrdering(O, Global.varNames);

		this.i_S = this.S.stream().map(s -> Global.varNames.indexOf(s) + 1).collect(Collectors.toList());
		this.i_S_p = this.i_S.stream().map(i -> i + (Global.NUM_VARS / 2)).collect(Collectors.toList());
		this.i_Om_p = this.Om.stream().map(o -> Global.varNames.indexOf(o) + 1 + (Global.NUM_VARS / 2)).collect(Collectors.toList());

		T = i_S.stream().map(s -> dynamics.cpds.containsKey(s) ? dynamics.cpds.get(s)
				: DBN.getSameTransitionDD(Global.varNames.get(s - 1))).collect(Collectors.toList());
		
		this.O = i_Om_p.stream().map(s -> dynamics.cpds.containsKey(s - (Global.NUM_VARS / 2)) ? dynamics.cpds.get(s - (Global.NUM_VARS / 2))
				: DDnode.getUniformDist(Global.varNames.get(s - 1))).collect(Collectors.toList());

	}

	@Override
	public void init(DD s) {

		this.s = s;
		LOGGER.info(String.format("Initial state for env %s set to %s", name, DDOP.factors(s, i_S)));
	}

	@Override
	public Tuple<List<Integer>, List<Integer>> step(List<Tuple<Integer, Integer>> actions) {

		LOGGER.info(String.format("Taking action %s in env %s with state %s",
				actions.stream().map(a -> Global.valNames.get(a._0() - 1).get(a._1() - 1)).collect(Collectors.toList()),
				name, DDOP.factors(s, i_S)));
		
		var actIndices = actions.stream().map(a -> a._0()).collect(Collectors.toList());
		var actVals = actions.stream().map(a -> a._1()).collect(Collectors.toList());

		var t = DDOP.restrict(T, actIndices, actVals);

		t.add(s);

		var s_p = DDOP.addMultVarElim(t, i_S);
		var _s = DDOP.sample(List.of(s_p), i_S_p);
	
		// sample and create next state
		s = DDOP.mult(IntStream.range(0, _s._0().size())
				.boxed()
				.map(i -> DDnode.getDDForChild(_s._0().get(i), _s._1().get(i) - 1))
				.collect(Collectors.toList()));
	
		var _o = DDOP.restrict(O, actIndices, actVals);
		_o.add(s);
		
		// sample and create observation
		var o_s = DDOP.sample(List.of(DDOP.addMultVarElim(_o, i_S_p)), i_Om_p);
		var obsList = IntStream.range(0, o_s._0().size())
				.boxed()
				.map(i -> Tuple.of(Global.varNames.get(o_s._0().get(i) - 1), Global.valNames.get(o_s._0().get(i) - 1).get(o_s._1().get(i) - 1)))
				.collect(Collectors.toList());
		
		// unprime next state to make it current state
		s = DDOP.primeVars(s, -(Global.NUM_VARS / 2));
		LOGGER.info(String.format("State transitions to %s and produces obs %s", 
				DDOP.factors(s, i_S), obsList));

		return o_s;
	}

	@Override
	public String toString() {

		var builder = new StringBuilder();
		builder.append("ENV ").append(name).append(" :[ ").append("\r\n");
		builder.append("\t S: ").append(S).append("\r\n");
		builder.append("\t Om: ").append(Om).append("\r\n");
		builder.append("]");

		return builder.toString();
	}

	@Override
	public DD getS() {

		// TODO Auto-generated method stub
		return s;
	}

	@Override
	public List<Integer> i_S() {

		return i_S;
	}

	@Override
	public List<Integer> i_Om_p() {

		return i_Om_p;
	}

}
