package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.domainMaker.AttackerDomainMaker;

class TestAttackerDomain {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testAttackerDomainActionSPUDD() {
		System.out.println("Running testAttackerDomainActionSPUDD()");
		AttackerDomainMaker AD = new AttackerDomainMaker();
		AD.makeActionsSPUDD();
		AD.writeActionsDef();
		System.out.println(AD.actionsDef);
	}

}
