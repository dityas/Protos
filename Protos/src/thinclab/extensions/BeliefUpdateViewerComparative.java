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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import thinclab.belief.BeliefOps;
import thinclab.decisionprocesses.POMDP;
import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public class BeliefUpdateViewerComparative {

	/*
	 * View belief updates comparatively
	 */

	private static Logger LOGGER = Logger.getLogger(BeliefUpdateViewerComparative.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CommandLineParser cliParser = new DefaultParser();
		Options opt = new Options();

		/* domain file */
		opt.addOption("d", true, "path to the domain file");

		// Parse CLI
		CommandLine line = null;

		try {

			line = cliParser.parse(opt, args);

			// init POMDP object
			String domainFile = line.getOptionValue("d");
			POMDP pomdp = new POMDP(domainFile);

			System.out.println("\r\nInitial belief: " + pomdp.bOPs.toMap(pomdp.getCurrentBelief()));

			Scanner scanner = new Scanner(System.in);

			List<List<String>> allObs = pomdp.getAllPossibleObservations();

			int step = 0;
			DD belief = pomdp.getCurrentBelief();
			DD bBelief = pomdp.getCurrentBelief();
			DD bBelief2 = pomdp.getCurrentBelief();
			while (true) {

				System.out.println("\r\n=== Step " + step + " ===");
				System.out.println("Rational Belief: " + pomdp.bOPs.toMap(belief));
				System.out.println("Biased Belief: " + pomdp.bOPs.toMap(bBelief));
				//System.out.println("Biased Belief (elementary): " + pomdp.bOPs.toMap(bBelief2));

				System.out.println("Available actions: " + pomdp.getActions());
				System.out.print("Enter action: ");
				String action = scanner.nextLine();

				System.out.println("Possible observations:");
				for (int i = 0; i < allObs.size(); i++) {
					System.out.println(i + " : " + allObs.get(i));
				}

				System.out.print("Enter observation index: ");
				int obs = Integer.parseInt(scanner.nextLine());

				belief = pomdp.bOPs.beliefUpdate(belief, action, allObs.get(obs).toArray(String[]::new));
				
				bBelief = ((BeliefOps) pomdp.bOPs).biasedBeliefUpdate(bBelief, action,
						allObs.get(obs).toArray(String[]::new));
				
				//bBelief2 = ((BeliefOps) pomdp.bOPs).biasedBeliefUpdate2(bBelief2, action,
				//		allObs.get(obs).toArray(String[]::new));

				step += 1;

			}

		}

		catch (Exception e) {
			LOGGER.error("Some error");
			e.printStackTrace();
			System.exit(-1);
		}

	}

}
