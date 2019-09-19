/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.Examples;

import static org.junit.jupiter.api.Assertions.fail;

import cern.colt.Arrays;
import edu.uci.ics.jung.graph.util.EdgeType;
import thinclab.ipomdpsolver.IPOMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.ipomdpsolver.InteractiveBelief.LookAheadTree;
import thinclab.utils.visualizers.Visualizer;
import thinclab.utils.visualizers.VizEdge;
import thinclab.utils.visualizers.VizGraph;
import thinclab.utils.visualizers.VizNode;

/*
 * @author adityas
 *
 */
public class LATreeVisualizer {
	/*
	 * Just a runner for testing LATree visualizations
	 */
	
	public static void main(String[] args) {
		
		IPOMDPParser parser = 
				new IPOMDPParser("/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt");
		
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 1);
		
		try {
			
//			tigerL1IPOMDP.solveOpponentModels();
//			tigerL1IPOMDP.initializeIS();
			
			LookAheadTree lt = new LookAheadTree(tigerL1IPOMDP);
			Visualizer viz = 
					new Visualizer(
							VizGraph.getVizGraphFromLATreeTriples(
									lt.toStringTriples()));
			
			
		}

		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
