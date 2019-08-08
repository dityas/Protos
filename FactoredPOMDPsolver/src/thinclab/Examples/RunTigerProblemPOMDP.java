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

import thinclab.policyhelper.PolicyExtractor;
import thinclab.policyhelper.PolicyGraph;
import thinclab.policyhelper.PolicyTree;
import thinclab.policyhelper.PolicyVisualizer;
import thinclab.symbolicperseus.POMDP;

/*
 * @author adityas
 *
 */
public class RunTigerProblemPOMDP {
	/*
	 * Runner for the Tiger Domain POMDP
	 */

	public static void main(String[] args) {
		TigerProblemPOMDP tigerPOMDP = new TigerProblemPOMDP();
		
		try {
			File domainFile = File.createTempFile("TigerPOMDP", ".POMDP");
			tigerPOMDP.makeAll();
//			System.out.println(tigerPOMDP.domainString);
			tigerPOMDP.writeToFile(domainFile.getAbsolutePath());
			POMDP pomdp = new POMDP(domainFile.getAbsolutePath());
			pomdp.solvePBVI(15, 100);
			domainFile.deleteOnExit();
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
