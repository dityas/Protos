package thinclab.ipomdpsolver;

import thinclab.symbolicperseus.POMDP;
//import thinclab.symbolicperseus.Solver;
import thinclab.policyhelper.PolicyExtractor;
import thinclab.policyhelper.PolicyVisualizer;
import thinclab.policyhelper.PolicyGraph;

public class IPOMDPSolver {
	
	public static int nRounds = 40;
    public static int nIterations = 100;  // backup iterations  per round
    public static int maxAlphaSetSize = 1000;
    public static int numBelStates = 100;
    public static int maxBelStates = 100;
    public static int episodeLength = 50;  // when generating belief points
    public static double threshold = 0.01;
    public static double explorProb=0.0;
    
    private static PolicyVisualizer visualizer;
    
	public static void main(String[] args) {
		
		// Attacker SPUDD
//		String attacker_SPUDD = "/home/adityas/git/repository/FactoredPOMDPsolver/src/coffee3po.dat";
//		String attacker_SPUDD = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.95.SPUDD.txt";
		String attacker_SPUDD = "/home/adityas/git/repository/FactoredPOMDPsolver/src/attacker_new_domain.txt";
//		String attacker_SPUDD = "/home/adityas/git/repository/FactoredPOMDPsolver/src/new_attacker.txt";
//		String attacker_SPUDD = "/home/adityas/git/repository/FactoredPOMDPsolver/src/attacker.txt";
		
		// Solve attacker POMDP
		POMDP attackerPomdp = new POMDP(attacker_SPUDD, false);
//		attackerPomdp.solve(nRounds, 
//							numBelStates, 
//							maxBelStates, 
//							episodeLength, 
//							threshold, 
//							explorProb, 
//							nIterations, 
//							maxAlphaSetSize, 
//							attacker_SPUDD.substring(0,attacker_SPUDD.lastIndexOf(".")), false);
		
//		attackerPomdp.solvePBVI(nRounds, nIterations);
		attackerPomdp.solvePBVI(5, nIterations);
		
		// Extract attacker policy
		PolicyExtractor attackerPolicy = new PolicyExtractor(attackerPomdp);
		
		// Make Policy Graph
		System.out.println("Making graph");
		PolicyGraph attackerPolicyGraph = new PolicyGraph(attackerPolicy.policyNodes);
//		attackerPolicyGraph.printPrettyPolicyGraph();
		
		visualizer = new PolicyVisualizer(attackerPolicyGraph);
//		attackerPolicyGraph.toTriples();
		
	}
}
