/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.executables;

import java.util.List;
import thinclab.DDOP;
import thinclab.agents.OfflineAgent;
import thinclab.env.StochasticSimulation;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.models.POMDP;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.spuddx_parser.SpuddXMainParser;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class Launcher {

	public static void main(String args[]) {

		String domainFile = args[0];

		// Run domain
		var domainRunner = new SpuddXMainParser(domainFile);
		domainRunner.run();

		// Get agent J
		var J = (POMDP) domainRunner.getModel("agentJ").orElseGet(() ->
			{

				System.out.println("Model not found");
				System.exit(-1);
				return null;
			});

		// Get agent I
		var I = (IPOMDP) domainRunner.getModel("agentI").orElseGet(() ->
			{

				System.out.println("Model not found");
				System.exit(-1);
				return null;
			});

		var env = domainRunner.envs.get("maTigerEnv");

		var s = DDnode.getDDForChild("TigerLoc", "TR");

		var Isolver = new SymbolicPerseusSolver<>();
		var Jsolver = new SymbolicPerseusSolver<>();

		// LOGGER.debug("Initializing StochasticSimulation");
		var b = DDOP.mult(DDleaf.getDD(0.5f), DDnode.getDistribution(I.i_Mj, List.of(Tuple.of("m0", 1.0f))));

		var agentI = new OfflineAgent(I, I.getECDDFromMjDD(b), Isolver, 100, 10);

		var agentJ = new OfflineAgent(J, DDleaf.getDD(0.5f), Jsolver, 100, 10);

		var sim = new StochasticSimulation<>();
		var e = sim.run(env, s, List.of(agentI, agentJ), 3);
		System.out.println(e);

	}
}