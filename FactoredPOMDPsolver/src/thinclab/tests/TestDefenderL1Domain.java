package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.ddmaker.AttackerDomainMaker;
import thinclab.ddmaker.DefenderL1DomainMaker;
import thinclab.policyhelper.PolicyExtractor;
import thinclab.policyhelper.PolicyGraph;
import thinclab.symbolicperseus.POMDP;

class TestDefenderL1Domain {
	
	PolicyGraph attackerPolicyGraph;
	POMDP attackerPomdp;

	@BeforeEach
	void setUp() throws Exception {
		String attackerL0 = "/home/adityas/git/repository/FactoredPOMDPsolver/src/attacker_l0.txt";
		AttackerDomainMaker attl0Domain = new AttackerDomainMaker();
		attl0Domain.makeDomain();
		attl0Domain.writeToFile(attackerL0);
		this.attackerPomdp = new POMDP(attackerL0, false);
		attackerPomdp.solvePBVI(10, 100);
		
		PolicyExtractor attackerPolicy = new PolicyExtractor(attackerPomdp);
		this.attackerPolicyGraph = new PolicyGraph(attackerPolicy.policyNodes);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testMakeDomain() {
		System.out.println("Running testMakeDomain");
		DefenderL1DomainMaker defDomain = new DefenderL1DomainMaker(
				this.attackerPolicyGraph.getGraphAsDD(this.attackerPomdp),
				this.attackerPolicyGraph.getGraphNodeVarVals(),
				this.attackerPomdp.getObsVarsArray(),
				this.attackerPomdp.getObsValArray());
		defDomain.makeDomain();
		System.out.println(defDomain.domainString);
	}

}
