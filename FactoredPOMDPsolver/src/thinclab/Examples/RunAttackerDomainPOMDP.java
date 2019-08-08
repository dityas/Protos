/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.Examples;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import thinclab.policyhelper.PolicyExtractor;
import thinclab.policyhelper.PolicyGraph;
import thinclab.policyhelper.PolicyTree;
import thinclab.policyhelper.PolicyVisualizer;
import thinclab.symbolicperseus.POMDP;

/*
 * @author adityas
 *
 */
public class RunAttackerDomainPOMDP {
	/*
	 * Runner for the Attacker Domain POMDP
	 */

	public static void main(String[] args) {
		
		AttackerDomainPOMDP attackerPOMDP = new AttackerDomainPOMDP();
		
		try {
			File domainFile = File.createTempFile("AttackerPOMDP", ".POMDP");
			attackerPOMDP.makeAll();
			attackerPOMDP.writeToFile(domainFile.getAbsolutePath());
			POMDP pomdp = new POMDP(domainFile.getAbsolutePath());
			pomdp.solvePBVI(10, 100);
			
			PolicyExtractor policyExtractor = new PolicyExtractor(pomdp);
			PolicyGraph policyGraph = new PolicyGraph(policyExtractor.policyNodes);
			PolicyVisualizer viz = new PolicyVisualizer(policyGraph);

		} 
		
		catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

	}

}
