package thinclab.ipomdpsolver;

import thinclab.symbolicperseus.POMDP;
//import thinclab.symbolicperseus.Solver;
import thinclab.policyhelper.PolicyExtractor;

public class IPOMDPSolver {
	
	public static int nRounds = 5;
    public static int nIterations =30;  // backup iterations  per round
    public static int maxAlphaSetSize = 100;
    public static int numBelStates = 100;
    public static int maxBelStates = 10000;
    public static int episodeLength = 50;  // when generating belief points
    public static double threshold = 0.001;
    public static double explorProb=0.4;
    
	public static void main(String[] args) {
		
		// Attacker SPUDD
		String attacker_SPUDD = "/home/adityas/git/repository/FactoredPOMDPsolver/src/coffee3po.dat";
		
		// Solve attacker POMDP
		POMDP attackerPomdp = new POMDP(attacker_SPUDD, false);
		attackerPomdp.solve(nRounds, 
							numBelStates, 
							maxBelStates, 
							episodeLength, 
							threshold, 
							explorProb, 
							nIterations, 
							maxAlphaSetSize, 
							attacker_SPUDD.substring(0,attacker_SPUDD.lastIndexOf("."))+".pomdp", false);
		
		// Extract attacker policy
		PolicyExtractor attackerPolicy = new PolicyExtractor(attackerPomdp);
		System.out.println(attackerPolicy.policyNodes);
		
	}
}
