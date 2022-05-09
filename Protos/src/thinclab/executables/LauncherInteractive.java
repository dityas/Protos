/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.executables;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import thinclab.DDOP;
import thinclab.agents.OfflineAgent;
import thinclab.env.StochasticSimulation;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.models.POMDP;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.models.datastructures.PolicyGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.spuddx_parser.SpuddXMainParser;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class LauncherInteractive {

    public static void main(String[] args) {

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

        var s = DDnode.getDDForChild("TigerLoc", "TR");


        // LOGGER.debug("Initializing StochasticSimulation");
        var b = I.getECDDFromMjDD(DDOP.mult(DDleaf.getDD(0.5f), DDnode.getDistribution(I.i_Mj, List.of(Tuple.of("m0", 1.0f)))));


        var policy = new SymbolicPerseusSolver<>().solve(List.of(b), I, 100, I.H,
                AlphaVectorPolicy.fromR(I.R()));

        var allObs = I.oAll;

        var allObsLabels = allObs.stream()
            .map(os -> IntStream.range(0, os.size()).boxed()
                    .map(i -> Global.valNames.get(I.i_Om_p.get(i) - 1).get(os.get(i) - 1))
                    .collect(Collectors.toList()))
            .collect(Collectors.toList());
        var in = new Scanner(System.in);
        while (true) {

            System.out.println(String.format("Belief Agent J frame is %s", DDOP.getFrameBelief(b, I.PThetajGivenEC, I.i_EC, I.i_S())));
            System.out.println(String.format("Belief Agent I is %s", DDOP.factors(b, I.i_S())));

            System.out.println(String.format("Enter action from %s (suggested %s)", I.A(), I.A().get(policy.getBestActionIndex(b, I.i_S))));
            int aIndex = in.nextInt();

            System.out.println(String.format("Enter observations from %s", allObsLabels));
            int oIndex = in.nextInt();

            System.out.println(String.format("Action: %s, obs: %s", I.A().get(aIndex), allObsLabels.get(oIndex)));
            b = I.beliefUpdate(b, aIndex, allObs.get(oIndex));


        }
    }

}
