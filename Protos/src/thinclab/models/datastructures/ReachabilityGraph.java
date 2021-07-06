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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public class ReachabilityGraph extends AbstractAOGraph<DD, List<String>> {

	private static final Logger LOGGER = LogManager.getLogger(ReachabilityGraph.class);

	public ReachabilityGraph(final List<List<String>> AOSpace) {

		this.connections = new ConcurrentHashMap<>(10);
		this.edgeIndexMap = new ConcurrentHashMap<>(10);

		AOSpace.stream().forEach(i -> this.edgeIndexMap.put(i, this.edgeIndexMap.size()));
		LOGGER.info(String.format("Initialized reachability graph for action-observation space %s", AOSpace));
	}

	@Override
	public String toString() {

		var builder = new StringBuilder();
		builder.append("Reachability Graph: [").append("\r\n").append("edges: {\r\n");

		this.edgeIndexMap.entrySet().stream().forEach(e -> {

			builder.append("\t").append(e.getKey()).append(" -> ").append(e.getValue()).append("\r\n");
		});

		builder.append("}\r\n");
		builder.append("nodes: {\r\n");

		this.connections.entrySet().stream().forEach(e -> {

			builder.append("\t").append(e.getKey()).append(" -> ").append(e.getValue()).append("\r\n");
		});

		builder.append("}\r\n]");

		return builder.toString();
	}

}
