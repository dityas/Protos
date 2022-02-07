/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.env;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import thinclab.legacy.Global;
import thinclab.utils.Graphable;
import thinclab.utils.Jsonable;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class Episode implements Graphable, Jsonable {

	public List<Tuple<SimState, List<Tuple<Integer, Integer>>>> trace = new ArrayList<>();
	
	@Override
	public String toString() {
		
		
		
		return null;
	}

	@Override
	public String toJson() {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toDot() {

		var builder = new StringBuilder();

		var obs = trace.stream()
				.map(t -> t._1().stream().map(
						o -> Tuple.of(Global.varNames.get(o._0() - 1), Global.valNames.get(o._0() - 1).get(o._1() - 1)))
						.collect(Collectors.toList()))
				.collect(Collectors.toList());

		for (var state : trace)
			builder.append(state._0().toDot());

		IntStream.range(0, trace.size() - 1).boxed().forEach(i ->
			{

				builder.append(trace.get(i)._0().s.hashCode()).append(i);
				builder.append(" -> ");
				builder.append(trace.get(i + 1)._0().s.hashCode()).append(i + 1);
				builder.append(" [label=\"").append(obs.get(i)).append("\"];");
				builder.append("\r\n");

			});

		return builder.toString();
	}

	@Override
	public String toLabel() {

		return toDot();
	}

}
