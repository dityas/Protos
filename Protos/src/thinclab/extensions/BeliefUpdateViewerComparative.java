/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.extensions;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.belief.BeliefOps;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.models.POMDP;
import thinclab.spuddx_parser.ModelsParser;
import thinclab.spuddx_parser.SpuddXParserWrapper;

/*
 * @author adityas
 *
 */
public class BeliefUpdateViewerComparative {

	/*
	 * View belief updates comparatively
	 */

	private static Logger LOGGER = LogManager.getLogger(BeliefUpdateViewerComparative.class);

	public static void main(String[] args) {

		// TODO Auto-generated method stub
		CommandLineParser cliParser = new DefaultParser();
		Options opt = new Options();

		/* domain file */
		opt.addOption("d", true, "path to the domain file");

		// Parse CLI
		CommandLine line = null;

		try {
			/*
			line = cliParser.parse(opt, args);

			// init POMDP object
			String domainFile = line.getOptionValue("d");
			var parser = new SpuddXParserWrapper(domainFile);

			var variables = parser.getVariableDeclarations();
			Global.primeVarsAndInitGlobals(variables);

			var models = parser.getModels();
			var pomdps = SpuddXParserWrapper.getPOMDPs(models);

			POMDP pomdp = pomdps.get("agentI");

			System.out.println("\r\nInitial belief: " + pomdp.b);

			Scanner scanner = new Scanner(System.in);

			var obs = pomdp.O.stream().map(o -> Global.valNames.get(Global.varNames.indexOf(o)))
					.collect(Collectors.toList());
			var allObs = OP.cartesianProd(obs);
			
			var updateFunciton = new POMDPBeliefUpdate();

			int step = 0;
			DD belief = pomdp.b;
			// DD bBelief = pomdp.getCurrentBelief();
			while (true) {

				System.out.println("\r\n=== Step " + step + " ===");
				System.out.println("Rational Belief: " + belief);
				// System.out.println("Biased Belief: " + pomdp.bOPs.toMap(bBelief));

				System.out.println("Available actions: " + pomdp.A);
				System.out.print("Enter action: ");
				String action = scanner.nextLine();

				System.out.println("Possible observations:");
				for (int i = 0; i < allObs.size(); i++) {

					System.out.println(i + " : " + allObs.get(i));
				}

				System.out.print("Enter observation index: ");
				int o = Integer.parseInt(scanner.nextLine());

				belief = 
						updateFunciton.beliefUpdate(
								pomdp,
								belief, 
								action, 
								allObs.get(o));
//				bBelief = 
//						((BeliefOps) pomdp.bOPs).biasedBeliefUpdate(
//								bBelief, 
//								action, 
//								allObs.get(obs).toArray(String[]::new));

				step += 1;

			}
			*/
		}

		catch (Exception e) {

			LOGGER.error("Some error");
			e.printStackTrace();
			System.exit(-1);
		}

	}

}
