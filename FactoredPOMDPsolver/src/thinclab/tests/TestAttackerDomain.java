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
	void testAttackerDomain() {
		System.out.println("Running testAttackerDomain()");
		AttackerDomainMaker AD = new AttackerDomainMaker();
		AD.makeDomain();
		System.out.println(AD.domainString);
		System.out.println(AD.ddMap);
	}

}
