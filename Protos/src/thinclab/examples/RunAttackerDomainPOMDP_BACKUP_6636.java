/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.examples;

import java.io.File;
import java.io.IOException;

import thinclab.frameworks.POMDP;
import thinclab.policyhelper.PolicyExtractor;
import thinclab.policyhelper.PolicyGraph;
import thinclab.policyhelper.PolicyVisualizer;

/*
 * @author adityas
 *
 */
public class RunAttackerDomainPOMDP {
	/*
	 * Runner for the Attacker Domain POMDP
	 */

	public static void main(String[] args) {
<<<<<<< HEAD:FactoredPOMDPsolver/src/thinclab/Examples/RunAttackerDomainPOMDP.java
//		AttackerDomainPOMDP attackerPOMDP = new AttackerDomainPOMDP();
		AttackerDomainPOMDP2 attackerPOMDP = new AttackerDomainPOMDP2();
||||||| merged common ancestors
		AttackerDomainPOMDP attackerPOMDP = new AttackerDomainPOMDP();
		
=======
		
		AttackerDomainPOMDP attackerPOMDP = new AttackerDomainPOMDP();
		
>>>>>>> ipomdp:Protos/src/thinclab/examples/RunAttackerDomainPOMDP.java
		try {
			File domainFile = File.createTempFile("AttackerPOMDPTamperData", ".POMDP");
			attackerPOMDP.makeAll();
			attackerPOMDP.writeToFile(domainFile.getAbsolutePath());
			POMDP pomdp = new POMDP(domainFile.getAbsolutePath());
			pomdp.solvePBVI(10, 100);
			
			PolicyExtractor policyExtractor = new PolicyExtractor(pomdp);
//			PolicyTree pTree = new PolicyTree(pomdp, 3);
			PolicyGraph policyGraph = new PolicyGraph(policyExtractor.policyNodes);
			PolicyVisualizer viz = new PolicyVisualizer(policyGraph);

		} 
		
		catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

	}

}
