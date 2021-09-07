/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.datastructures;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.Global;
import thinclab.models.POSeqDecMakingModel;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class ModelGraph<N extends ReachabilityNode, A, O> extends AbstractAOGraph<N, A, O> {

	private static final Logger LOGGER = LogManager.getLogger(ModelGraph.class);
	
	public ModelGraph(final List<Tuple<A, O>> AOSpace) {

		this.connections = new ConcurrentHashMap<>(10);
		this.edgeIndexMap = new ConcurrentHashMap<>(10);

		AOSpace.stream().forEach(i -> this.edgeIndexMap.put(i, this.edgeIndexMap.size()));
		LOGGER.info(String.format("Initialized policy graph for branching factor %s", AOSpace.size()));
	}

	public static ModelGraph<ReachabilityNode, Integer, List<Integer>> fromDecMakingModel(POSeqDecMakingModel<?> m) {

		// Make action observation space for agent I
		var obsVars = m.i_Om().stream().map(i -> IntStream.range(1, Global.valNames.get(i - 1).size() + 1)
				.mapToObj(j -> j).collect(Collectors.toList())).collect(Collectors.toList());

		var oSpace = DDOP.cartesianProd(obsVars);
		var aoSpace = IntStream.range(0, m.A().size()).mapToObj(i -> i)
				.flatMap(i -> oSpace.stream().map(o -> Tuple.of(i, o))).collect(Collectors.toList());

		return new ModelGraph<>(aoSpace);
	}

}
