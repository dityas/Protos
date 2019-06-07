package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.domainMaker.AttackerDomain;

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
		AttackerDomain AD = new AttackerDomain();
		AD.makeAll();
		System.out.println(AD.domainString);
	}

}
