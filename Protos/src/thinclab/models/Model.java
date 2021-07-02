/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models;

import java.util.List;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public interface Model {
	
	
	default MutableNetwork<DD, List<String>> getEmptyReachabilityGraph() {
		return NetworkBuilder
				.directed()
				.allowsParallelEdges(true)
				.allowsSelfLoops(true)
				.build();
	}
}
