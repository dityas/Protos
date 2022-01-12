/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.env;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.Agent;
import thinclab.legacy.DD;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class StochasticSimulation<E extends Environment<DD>> implements Simulator<E> {

	private static final Logger LOGGER = LogManager.getLogger(StochasticSimulation.class);
	
	@Override
	public Episode run(E env, DD s,
			List<Agent> agents,
			int len) {
		
		// initialize the starting state
		env.init(s);
		
		var episode = new Episode();
		
		for (int i = 0; i < len; i++) {
			
			var simState = new SimState(i, env.getS(), agents);
			
			LOGGER.info(String.format("Simulation time step %s", i));
			LOGGER.info(String.format("Beliefs are %s", 
					agents.stream().map(a -> Tuple.of(a.m.name, a.b)).collect(Collectors.toList())));
			
			var actions = agents.stream()
					.map(a -> Tuple.of(
							a.m.i_A, 
							a.optA + 1))
					.collect(Collectors.toList());
			
			LOGGER.info(String.format("Optimal actions for agents %s are %s", agents, actions));
			
			var obs = env.step(actions);
			
			var _obs = IntStream.range(0, obs._0().size()).boxed()
					.map(_i -> Tuple.of(obs._0().get(_i), obs._1().get(_i)))
					.collect(Collectors.toList());
			
			episode.trace.add(Tuple.of(simState, _obs));
			
			if ((i + 1) == len) {
				LOGGER.info("Terminating simulation.");
				break;
			}
			
			var agentObsIndices = agents.stream()
					.map(a -> Tuple.of(a, IntStream.range(0, obs._0().size()).boxed()
							.filter(_i -> a.m.i_Om_p.contains(obs._0().get(_i)))
							.map(_i -> obs._1().get(_i))
							.collect(Collectors.toList())))
					.collect(Collectors.toList());
			
			var next_agents = agentObsIndices.stream()
					.map(ao -> Agent.step(ao._0(), ao._1()))
					.collect(Collectors.toList());
			
			agents = next_agents;
		}

		return episode;
	}

}
