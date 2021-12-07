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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
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
public class PartiallObservableEnv<S extends DD> implements Environment<S> {

	public String name;
	
	public final List<String> S;
	public final List<String> Om;
	
	public final List<Integer> i_S;
	public final List<Integer> i_Om;
	
	public final List<DD> T;
	
	public S s;
	private static final Logger LOGGER = LogManager.getLogger(PartiallObservableEnv.class);

	public PartiallObservableEnv(List<String> S, List<String> O, DBN dynamics, HashMap<String, DD> R) {
		
		this.S = Global.sortByVarOrdering(S, Global.varNames);
		this.Om = Global.sortByVarOrdering(O, Global.varNames);
		
		this.i_S = this.S.stream().map(s -> Global.varNames.indexOf(s) + 1).collect(Collectors.toList());
		this.i_Om = this.Om.stream().map(o -> Global.varNames.indexOf(o) + 1).collect(Collectors.toList());
		
		T = i_S.stream().map(s -> dynamics.cpds.containsKey(s) ? dynamics.cpds.get(s) : DBN.getSameTransitionDD(Global.varNames.get(s - 1))).collect(Collectors.toList());

	}

	@Override
	public void init(S s) {

		this.s = s;
		LOGGER.info(String.format("Initial state for env %s set to %s", name, s));
	}

	@Override
	public List<S> step(List<Tuple<Integer, Integer>> actions) {

		// TODO Auto-generated method stub
		return null;
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
	
}
