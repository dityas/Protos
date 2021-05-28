/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.ddinterface.DDMaker;
import thinclab.ddinterface.DDTree;
import thinclab.decisionprocesses.POMDP;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestDDOps {

	private static boolean setUpDone = false;
	
	public POMDP pomdp;
	
	DD initBelief;
	DD bNOPNoneFromInit;
	DD bPRIVFromInit;
	DD bFILEFromInit;
	
	private static Logger LOGGER;
	
	@BeforeEach
	void setUp() throws Exception {
		
		CustomConfigurationFactory.initializeLogging();
		LOGGER = Logger.getLogger(TestDDOps.class);
		Global.clearHashtables();
			
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testDDEquals() {
		System.out.println("Running testDDEquals()");
		
		assertTrue(this.initBelief.equals(this.bNOPNoneFromInit));
		assertTrue(this.bNOPNoneFromInit.equals(this.initBelief));
	}
	
	@Test
	void testDDFactorAndUnFactor() {
		System.out.println("Running testDDFactorAndUnFactor()");
		
		DD[] initBeliefF = this.pomdp.factorBelief(initBelief);
		Global.clearHashtables();
		
		DD initBeliefUf = OP.multN(initBeliefF);
		assertTrue(this.initBelief.equals(initBeliefUf));
	}
	
	@Test
	void testDDHashSets() {
		System.out.println("Running testDDHashSets()");
		
		HashSet<DD> ddHashSet = new HashSet<DD>();
		
		ddHashSet.add(this.initBelief);
		
		System.out.println(pomdp.toMap(this.initBelief));
		System.out.println(pomdp.toMap(this.bNOPNoneFromInit));
		System.out.println(pomdp.toMap(this.bPRIVFromInit));
		assertTrue(ddHashSet.contains(this.bNOPNoneFromInit));
		assertFalse(ddHashSet.contains(this.bPRIVFromInit));
	}
	
	@Test
	public void testDDNorms() {
		System.out.println("Running testDDNorms");
		System.out.println(Arrays.toString(this.bNOPNoneFromInit.getVarSet()));
		System.out.println(Arrays.toString(this.pomdp.varIndices));
		System.out.println(this.bNOPNoneFromInit);
		DD norm = OP.addMultVarElim(this.bNOPNoneFromInit, this.pomdp.varIndices);
		System.out.println(norm);
		DD normed = OP.div(this.bNOPNoneFromInit, norm);
		System.out.println(normed);
		System.out.println(Arrays.asList(this.pomdp.factorBelief(normed)));
	}
	
	@Test
	public void testDDIndependentMult() {
		
		DDMaker ddMaker = pomdp.ddMaker;
		DDTree f1 = 
				ddMaker.getDDTreeFromSequence(
						new String[] {"EXFIL_ONGOING", "HAS_C_DATA", "EXFIL_ONGOING'"},
						new String[][] {
							{"no", "no", "yes", "1.0"},
							{"no", "yes", "no", "1.0"},
							{"yes", "yes", "yes", "1.0"},
							{"yes", "no", "yes", "1.0"},
						});
		
		DD f1dd = OP.reorder(f1.toDD());
		
		LOGGER.debug("f1 is " + f1dd.toDDTree());
	
		
		DDTree f2 = 
				ddMaker.getDDTreeFromSequence(
						new String[] {"EXFIL_ONGOING", "HAS_FAKE_DATA", "EXFIL_ONGOING'"},
						new String[][] {
							{"yes", "no", "yes", "1.0"},
							{"yes", "yes", "no", "1.0"},
							{"no", "yes", "no", "1.0"},
							{"no", "no", "no", "1.0"},
						});
		
		DD f2dd = OP.reorder(f2.toDD());
		
		LOGGER.debug("f2 is " + f2dd.toDDTree());

		LOGGER.debug("x is " + OP.mult(f1dd, f2dd).toDDTree());
	}
	
	@Test
	public void testVarIndependenceForDomainsWithSameTConnections() throws Exception {
		
//		String domain = "/home/adityas/UGA/THINCLab/DomainFiles/final_domains/"
//				+ "exfil.6S.L0.obs_deception.domain";
		
		String domain = "/home/adityas/UGA/THINCLab/DomainFiles/final_domains/"
				+ "exfil.6S.L0.obs_deception.domain";
		
		POMDP pomdp = new POMDP(domain);
		
		DD currentBelief = pomdp.getCurrentBelief();
		
		for (int i = 0; i < 100; i++) {
			currentBelief = pomdp.beliefUpdate(currentBelief, "VULN_RECON", new String[] {"success"});
			
	//		LOGGER.debug(currentBelief.toDDTree());
			
			DD[] factors = pomdp.factorBelief(currentBelief);
			
			DD joint = OP.multN(factors);
			
			LOGGER.debug(OP.maxAll(OP.abs(OP.sub(joint, currentBelief))));
		}
		
	}
	
	@Test
	public void testDDCreationBenchmarks() {
		
		Global.clearHashtables();
		Global.varNames = new String[] {"A", "B", "C"};
		Global.valNames = new String[][] {{"a1", "a2"}, {"b1", "b2", "b3"}, {"c1", "c2"}};
		Global.varDomSize = new int[] {2, 3, 2};
		
		DD a = DDnode.myNew(1, new DD[] {DD.one, DD.zero});
		
		LOGGER.debug("Initialized " + a);
		
		LOGGER.debug("Running a few measurements and tests.");
        Random rand = new Random();
        long then = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
        	a = DDnode.myNew(1, new DD[] {DDleaf.myNew(rand.nextDouble()), DDleaf.myNew(rand.nextDouble())});
        }

        long now = System.currentTimeMillis();
        LOGGER.info("Simple 2 child ADD creation for took: " + ((now - then) / 100000.0));
        LOGGER.info("Global Nodes Caches contains: " + (Global.nodeHashtable.size() + Global.leafHashtable.size()) + " nodes.");
        LOGGER.info("Total mem: " + (Runtime.getRuntime().totalMemory() / 1000000));
        LOGGER.info("Free mem: " + (Runtime.getRuntime().freeMemory() / 1000000));
        LOGGER.info("Max mem: " + (Runtime.getRuntime().maxMemory() / 1000000));
        
        // New test
        Global.clearHashtables();
        LOGGER.debug("Running measurements for repeated nodes.");
        rand = new Random();
        then = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
        	a = DDnode.myNew(1, new DD[] {DD.one, DD.one});
        }

        now = System.currentTimeMillis();
        LOGGER.info("Simple 2 child ADD creation for took: " + ((now - then) / 100000.0));
        LOGGER.info("Global Nodes Caches contains: " + (Global.nodeHashtable.size() + Global.leafHashtable.size()) + " nodes.");
        
        // Test restrict operation
        DDMaker ddMaker = new DDMaker();
        ddMaker.addVariable("A", new String[] {"a1", "a2"});
        ddMaker.addVariable("B", new String[] {"b1", "b2", "b3"});
        ddMaker.addVariable("C", new String[] {"c1", "c2"});
        ddMaker.primeVariables();
        
        DD b = ddMaker.getDDTreeFromSequence(
        			new String[] {"A", "B"},
        			new String[][] {
        					{"a1", "b1", "1.0"},
        					{"a1", "b2", "0.0"},
        					{"a1", "b3", "0.0"},
        					{"a2", "b1", "0.33"},
        					{"a2", "b2", "0.33"},
        					{"a2", "b3", "0.34"}
        			}).toDD();
        
        LOGGER.info("Made 2 variable DD " + b);
        
        DD res = OP.restrict(b, new int[][] {{2}, {3}});
        LOGGER.info("Result of restrict is " + res);
        
        LOGGER.info("Checking larger restrict operation");
        DD c = ddMaker.getDDTreeFromSequence(
        			new String[] {"A", "B", "C"},
        			new String[][] {
        					{"a1", "b1", "c1", "0.5"},
        					{"a1", "b1", "c2", "0.5"},
        					{"a1", "b2", "c1", "0.33"},
        					{"a1", "b2", "c2", "0.67"},
        					{"a1", "b3", "c1", "0.25"},
        					{"a1", "b3", "c2", "0.75"},
        					{"a2", "b1", "c1", "1.0"},
        					{"a2", "b1", "c2", "0.0"},
        					{"a2", "b2", "c1", "0.0"},
        					{"a2", "b2", "c2", "1.0"},
        					{"a2", "b3", "c1", "1.0"},
        					{"a2", "b3", "c2", "0.0"}
        			}).toDD();

        LOGGER.debug("DD C is " + c);
        DD resA1 = OP.restrict(c, new int[][] {{1}, {1}});
        LOGGER.debug("Restricting A=a1 gives " + resA1);
        DD resA2 = OP.restrict(c, new int[][] {{1}, {2}});
        LOGGER.debug("Restricting A=a2 gives " + resA2);
        
	}
	
	@Test
	public void testAdditionTimes() {
        
        LOGGER.debug("Checking avg. time for 3 variable addition");
        Global.clearHashtables();
		Global.varNames = new String[] {"A", "B", "C"};
		Global.valNames = new String[][] {{"a1", "a2"}, {"b1", "b2", "b3"}, {"c1", "c2"}};
		Global.varDomSize = new int[] {2, 3, 2};

        DDMaker ddMaker = new DDMaker();
        ddMaker.addVariable("A", new String[] {"a1", "a2"});
        ddMaker.addVariable("B", new String[] {"b1", "b2", "b3"});
        ddMaker.addVariable("C", new String[] {"c1", "c2"});
        ddMaker.primeVariables();
        
        long then = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
        	
        	DD c1 = ddMaker.getDDTreeFromSequence(
        			new String[] {"A", "B", "C"},
        			new String[][] {
        					{"a1", "b1", "c1", "0.5"},
        					{"a1", "b1", "c2", "0.5"},
        					{"a1", "b2", "c1", "0.33"},
        					{"a1", "b2", "c2", "0.67"},
        					{"a1", "b3", "c1", "0.25"},
        					{"a1", "b3", "c2", "0.75"},
        					{"a2", "b1", "c1", "1.0"},
        					{"a2", "b1", "c2", "0.0"},
        					{"a2", "b2", "c1", "0.0"},
        					{"a2", "b2", "c2", "1.0"},
        					{"a2", "b3", "c1", "1.0"},
        					{"a2", "b3", "c2", "0.0"}
        			}).toDD();
        	
        	DD c2 = ddMaker.getDDTreeFromSequence(
        			new String[] {"A", "B", "C"},
        			new String[][] {
        					{"a1", "b1", "c1", "0.5"},
        					{"a1", "b1", "c2", "0.5"},
        					{"a1", "b2", "c1", "0.33"},
        					{"a1", "b2", "c2", "0.67"},
        					{"a1", "b3", "c1", "0.25"},
        					{"a1", "b3", "c2", "0.75"},
        					{"a2", "b1", "c1", "1.0"},
        					{"a2", "b1", "c2", "0.0"},
        					{"a2", "b2", "c1", "0.0"},
        					{"a2", "b2", "c2", "1.0"},
        					{"a2", "b3", "c1", "1.0"},
        					{"a2", "b3", "c2", "0.0"}
        			}).toDD();
        	
        	DD c3 = OP.add(c1, c2);
        }

        long now = System.currentTimeMillis();
        LOGGER.info("Simple 2 child ADD creation for took: " + ((now - then) / 100000.0));
        LOGGER.info("Global Nodes Caches contains: " + (Global.nodeHashtable.size() + Global.leafHashtable.size()) + " nodes.");
	
	}
	
	@Test
	public void checkDifferentVariableOPs() {
		
		LOGGER.debug("Checking avg. time for 3 variable addition");
        Global.clearHashtables();
		Global.varNames = new String[] {"A", "B", "C"};
		Global.valNames = new String[][] {{"a1", "a2"}, {"b1", "b2", "b3"}, {"c1", "c2"}};
		Global.varDomSize = new int[] {2, 3, 2};

        DDMaker ddMaker = new DDMaker();
        ddMaker.addVariable("A", new String[] {"a1", "a2"});
        ddMaker.addVariable("B", new String[] {"b1", "b2", "b3"});
        ddMaker.addVariable("C", new String[] {"c1", "c2"});
        ddMaker.primeVariables();
        
        DD a = 
        		ddMaker.getDDTreeFromSequence(
        				new String[] {"A"},
        				new String[][] {
        					{"a1", "1.509"},
        					{"a2", "0.789870"}
        				}).toDD();
        
        DD b = 
        		ddMaker.getDDTreeFromSequence(
        				new String[] {"C"},
        				new String[][] {
        					{"c1", "2.8907"},
        					{"c2", "1.5"}
        				}).toDD();
        
        DD c = OP.add(a, b);
        
        LOGGER.debug(c.toDDTree().toString());

	}
	
}
