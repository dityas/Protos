package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.domainMaker.AttackerDomain;
import thinclab.domainMaker.DefenderL1Domain;
import thinclab.domainMaker.Domain;
import thinclab.exceptions.DDNotDefinedException;
import thinclab.policyhelper.PolicyExtractor;
import thinclab.policyhelper.PolicyGraph;
import thinclab.symbolicperseus.POMDP;

class TestDefenderL1Domain {
	
	PolicyGraph attackerPolicyGraph;
	POMDP attackerPomdp;
	Domain attl0Domain;

	@BeforeEach
	void setUp() throws Exception {
		String attackerL0 = "/home/adityas/git/repository/FactoredPOMDPsolver/src/attacker_l0.txt";
		this.attl0Domain = new AttackerDomain();
		this.attl0Domain.solve(attackerL0, 5, 100);
//		attl0Domain.makeAll();
//		attl0Domain.writeToFile(attackerL0);
//		this.attackerPomdp = new POMDP(attackerL0, false);
//		attackerPomdp.solvePBVI(10, 100);
		
//		PolicyExtractor attackerPolicy = new PolicyExtractor(attackerPomdp);
//		this.attackerPolicyGraph = new PolicyGraph(attackerPolicy.policyNodes);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testMakeDomain() {
		System.out.println("Running testMakeDomain");
		DefenderL1Domain defDomain = new DefenderL1Domain(this.attl0Domain);
		defDomain.makeVarContext();
		defDomain.makeDDMaker();
		defDomain.setOppPolicyDD();
		defDomain.writeVariablesDef();
		defDomain.writeObsDef();
		defDomain.writeOppPolicyDD();
		defDomain.setOppObsDDs();
		try {
			defDomain.writeOppObsDDs();
		} 
		catch (DDNotDefinedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		defDomain.makeActionsSPUDD();
		defDomain.writeActions();
		System.out.println(defDomain.variablesDef);
		System.out.println(defDomain.obsDef);
		System.out.println(defDomain.oppPolicyDDDef);
		System.out.println(defDomain.oppObsDDDef);
		System.out.println(defDomain.actionSection);
	}

}
