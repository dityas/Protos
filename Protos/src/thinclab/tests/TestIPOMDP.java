/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.FullBeliefExpansion;
import thinclab.belief.IBeliefOps;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.NextBelState;
import thinclab.legacy.OP;
import thinclab.parsers.IPOMDPParser;
import thinclab.representations.StructuredTree;
import thinclab.simulations.StochasticSimulation;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;
import thinclab.utils.Diagnostics;
import thinclab.utils.NextBelStateCache;

/*
 * @author adityas
 *
 */
class TestIPOMDP {

	public String l1DomainFile;
	
	private static Logger LOGGER;
	
	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		LOGGER = Logger.getLogger(TestIPOMDP.class);
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void testBenchMarkBeliefUpdate() throws Exception {
		
		LOGGER.info("Running belief update benchmarks");
		
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/deception.5S.2O.L1.2F.domain");
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		
		parser.parseDomain();
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);
		
		LOGGER.debug("IPOMDP Tau size is " + ipomdp.currentTau.getNumLeaves());
		
		DD afterBelief = OP.mult(ipomdp.getCurrentBelief(), ipomdp.currentTau);
		
		LOGGER.debug("After multiplying with belief, size is " + afterBelief.getNumLeaves());
		
		DD afterActions = OP.mult(ipomdp.currentAjGivenMj, ipomdp.currentTau);
		
		LOGGER.debug("After multiplying with P(Aj | Mj), size is " + afterActions.getNumLeaves());
		
		DD afterActionsAfterBelief = OP.mult(afterActions, ipomdp.getCurrentBelief());
		
		LOGGER.debug("After multiplying with P(Aj | Mj) and belief, size is " 
				+ afterActionsAfterBelief.getNumLeaves());
		
		LOGGER.debug("Checking normal belief update");
		List<Double> times = new ArrayList<Double>();
		
		for (int i = 0; i < 10; i ++) {
			Global.clearHashtables();
			
			long then = System.nanoTime();
			
			DD notImportant = 
					ipomdp.beliefUpdate(
							ipomdp.getCurrentBelief(), 
							ipomdp.getActions().get(0), 
							ipomdp.getAllPossibleObservations().get(0).stream().toArray(String[]::new));
			
			long now = System.nanoTime();
			
			times.add((double) (now - then) / 1000000);
		}
		
		LOGGER.debug("That took " + times.stream().mapToDouble(a -> a).average().getAsDouble() 
				+ " msec");
		
//		ipomdp.step(ipomdp.getCurrentBelief(), 
//					ipomdp.getActions().get(0), 
//					ipomdp.getAllPossibleObservations().get(0).stream().toArray(String[]::new));
//		
//		LOGGER.debug("IPOMDP Tau size is " + ipomdp.currentTau.getNumLeaves());
//		
//		times.clear();
//		
//		for (int i = 0; i < 10; i ++) {
//			Global.clearHashtables();
//			
//			long then = System.nanoTime();
//			
//			DD notImportant = 
//					ipomdp.beliefUpdate(
//							ipomdp.getCurrentBelief(), 
//							ipomdp.getActions().get(0), 
//							ipomdp.getAllPossibleObservations().get(0).stream().toArray(String[]::new));
//			
//			long now = System.nanoTime();
//			
//			times.add((double) (now - then) / 1000000);
//		}
//		
//		LOGGER.debug("That took " + times.stream().mapToDouble(a -> a).average().getAsDouble() 
//				+ " msec");
		
		
		
		
	}
	
	@Test
	void testBenchMarkPreMultipliedBeliefUpdate() throws Exception {
		
		LOGGER.info("Running pre multiplied belief update benchmarks");
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/"
//						+ "final_domains/deception.5S.2O.L1.2F.domain");
		
		parser.parseDomain();
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);
		
		LOGGER.debug("Checking after pre multiplying belief with Tau");
		List<Double> differentTimes = new ArrayList<Double>();
		
		IBeliefOps op = (IBeliefOps) ipomdp.bOPs;
		
		for (int i = 0; i < 10; i ++) {
			Global.clearHashtables();
			
			long then = System.nanoTime();
			
			DD notImportant = 
					op.beliefUpdate(
							ipomdp.getCurrentBelief(), 
							ipomdp.getActions().get(0), 
							ipomdp.getAllPossibleObservations().get(0).stream().toArray(String[]::new));
			
			long now = System.nanoTime();
			
			differentTimes.add((double) (now - then) / 1000000);
		}
		
		LOGGER.debug("That took " 
				+ differentTimes.stream().mapToDouble(a -> a).average().getAsDouble() 
				+ " msec");
	}
	
	
	
	
	@Test
	void testMJCompression() throws Exception {
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		parser.parseDomain();
		
		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP(parser, 4, 10);
		
//		LOGGER.debug(ipomdp.multiFrameMJ.MJs.get(0).getDotStringForPersistent());
//		LOGGER.debug(ipomdp.multiFrameMJ.MJs.get(1).getDotStringForPersistent());
		
		LOGGER.debug(ipomdp.multiFrameMJ.MJs.get(0).getDotString());
		LOGGER.debug(ipomdp.multiFrameMJ.MJs.get(1).getDotString());
		
		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "creak-right"});
		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "creak-right"});
		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "creak-right"});
		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "creak-right"});
		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
	}

	@Test
	void testIPOMDPL1Init() throws Exception {
		/*
		 * Test POMDP creation from L0Frame
		 */
		
		/* single frame init */
		LOGGER.info("Running testIPOMDPL1Init()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP();
		
		LOGGER.info("Parsing the domain");
		ipomdp.initializeFromParsers(parser);
		ipomdp.setMjDepth(3);
		ipomdp.setMjLookAhead(3);
		
		LOGGER.info("Solve MJs");
		ipomdp.solveMj();
		ipomdp.callUpdateIS();
		
		LOGGER.info("Checking Oi creation");
		ipomdp.currentOi = ipomdp.makeOi();
		
		assertEquals(ipomdp.currentOi.size(), ipomdp.getActions().size());
		
		for (String ai : ipomdp.getActions()) {
			
			for (DD oi : ipomdp.currentOi.get(ai))
				LOGGER.debug("for Ai = " + ai + " Oi is \r\n" + oi.toDDTree());
			
			assertEquals(
					ipomdp.currentOi.get(ai).length, 
					ipomdp.Omega.size() - ipomdp.OmegaJNames.size());
		}
		
		LOGGER.info("Checking Oi DD CPD");
		for (String Ai : ipomdp.currentOi.keySet()) {
			for (int s = 0; s < ipomdp.Omega.size() - ipomdp.OmegaJNames.size(); s++) {
				LOGGER.debug("For Ai " + Ai + " and o " + ipomdp.Omega.get(s));
				assertTrue(
						OP.maxAll(
								OP.abs(
									OP.sub(
										DD.one, 
										OP.addMultVarElim(
											ipomdp.currentOi.get(Ai)[s],
											IPOMDP.getVarIndex(
													ipomdp.Omega.get(s).name + "'"))))) < 1e-8);
			}
		}
		
		LOGGER.info("Checking Ti creation");
		ipomdp.currentTi = ipomdp.makeTi();
		
		assertEquals(ipomdp.currentTi.size(), ipomdp.getActions().size());
		
		for (String ai : ipomdp.getActions()) {
			
			for (DD ti : ipomdp.currentTi.get(ai))
				LOGGER.debug("For Ai=" + ai + " Ti=" + ti.toDDTree());
			
			assertEquals(
					ipomdp.currentTi.get(ai).length, 
					ipomdp.MjVarPosition);
		}
		
		LOGGER.info("Checking Ti DD factor CPD");
		
		for (String Ai : ipomdp.currentTi.keySet()) {
			for (int s = 0; s < ipomdp.MjVarPosition; s++) {
				LOGGER.debug("Checking for Ai " + Ai + " and s " + ipomdp.S.get(s));
				
				LOGGER.debug(OP.addMultVarElim(
						ipomdp.currentTi.get(Ai)[s],
						IPOMDP.getVarIndex(
								ipomdp.S.get(s).name + "'")));

				assertTrue(
						OP.maxAll(
								OP.abs(
									OP.sub(
										DD.one, 
										OP.addMultVarElim(
											ipomdp.currentTi.get(Ai)[s],
											IPOMDP.getVarIndex(
													ipomdp.S.get(s).name + "'"))))) < 1e-8);
			}
		}
		
		LOGGER.info("Checking Oj creation");
		ipomdp.currentOj = ipomdp.makeOj();
		
		for (int i = 0; i < ipomdp.currentOj.length; i++)
			LOGGER.debug("Oj for oj= " 
					+ ipomdp.OmegaJNames.get(i) + " is \r\n" 
					+ ipomdp.currentOj[i].toDDTree());
		
		LOGGER.info("Checking Oj DD CPD");
		
		for (int s = 0; s < ipomdp.currentOj.length; s++) {
			LOGGER.debug("For and OmegaJ " + ipomdp.OmegaJNames.get(s));
		
			assertTrue(
					OP.maxAll(
							OP.abs(
								OP.sub(
									DD.one, 
									OP.addMultVarElim(
										ipomdp.currentOj[s],
										IPOMDP.getVarIndex(
												ipomdp.OmegaJNames.get(s) + "'"))))) < 1e-8);
		}
			
		LOGGER.info("Check P(Aj| Mj) creation");
		ipomdp.currentAjGivenMj = ipomdp.multiFrameMJ.getAjGivenMj(ipomdp.ddMaker, ipomdp.Aj);
		LOGGER.debug("P(Aj| Mj) is \r\n" + ipomdp.currentAjGivenMj.toDDTree());
		LOGGER.debug(OP.addMultVarElim(ipomdp.currentAjGivenMj, IPOMDP.getVarIndex("A_j")));
		
		assertTrue(
				OP.maxAll(
						OP.abs(
							OP.sub(
								DD.one, 
								OP.addMultVarElim(
									ipomdp.currentAjGivenMj,
									IPOMDP.getVarIndex(
											"A_j"))))) < 1e-8);

		LOGGER.info("Check P(Thetaj| Mj) creation");
		ipomdp.currentThetajGivenMj = 
				ipomdp.multiFrameMJ.getThetajGivenMj(ipomdp.ddMaker, ipomdp.ThetaJ);
		LOGGER.debug("P(Thetaj| Mj) is \r\n" + ipomdp.currentThetajGivenMj.toDDTree());
		LOGGER.debug(
				OP.addMultVarElim(
						ipomdp.currentThetajGivenMj, IPOMDP.getVarIndex("Theta_j")));
		
		assertTrue(
				OP.maxAll(
						OP.abs(
							OP.sub(
								DD.one, 
								OP.addMultVarElim(
									ipomdp.currentThetajGivenMj,
									IPOMDP.getVarIndex(
											"Theta_j"))))) < 1e-8);
		
		LOGGER.info("Check P(Mj'| Mj, Oj', Aj) transition creation");
		ipomdp.currentMjPGivenMjOjPAj = ipomdp.makeOpponentModelTransitionDD();
		
		LOGGER.debug(
				OP.addMultVarElim(ipomdp.currentMjPGivenMjOjPAj, IPOMDP.getVarIndex("M_j'")));
		
		assertTrue(
				OP.maxAll(
						OP.abs(
							OP.sub(
								DD.one, 
								OP.addMultVarElim(
									ipomdp.currentMjPGivenMjOjPAj,
									IPOMDP.getVarIndex("M_j'"))))) < 1e-8);
		
		LOGGER.debug(ipomdp.currentMjPGivenMjOjPAj.toDDTree());
		
		for (List<String> row : ipomdp.currentMjPGivenMjOjPAj.toDDTree().getCPT())
			LOGGER.debug(row);
		
		LOGGER.info("Check initial belief");
		DD mjInit = ipomdp.multiFrameMJ.getMjInitBelief(ipomdp.ddMaker, null).toDD();
		DD initS = ipomdp.initBeliefDdTree.toDD();
		ipomdp.currentBelief = OP.reorder(OP.mult(mjInit, initS));
		LOGGER.debug(ipomdp.currentBelief);
		assertTrue(
				OP.maxAll(
						OP.abs(
							OP.sub(
								DD.one, 
								OP.addMultVarElim(
									ipomdp.currentBelief,
									ArrayUtils.subarray(
											ipomdp.stateVarIndices, 
											0, ipomdp.thetaVarPosition))))) < 1e-8);
		
		LOGGER.info("Checking Ri");
		ipomdp.currentRi = ipomdp.makeRi();
		
		for (String ai : ipomdp.Ai)
			LOGGER.debug("Ri for Ai=" + ai + " is " + ipomdp.currentRi.get(ai));

	}
	
	@Test
	void testIPOMDPL1Init2Frames() throws Exception {
		
		/* 2 frames init */
		LOGGER.info("Running testIPOMDPL1Init()");
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		parser.parseDomain();
		
		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP();
		
		LOGGER.info("Parsing the domain");
		ipomdp.initializeFromParsers(parser);
//		ipomdp.setMjDepth(10);
		ipomdp.setMjLookAhead(3);
		
		LOGGER.info("Solve MJs");
		ipomdp.solveMj();
		ipomdp.callUpdateIS();
		
		LOGGER.info("Check ObsJ combinations");
		LOGGER.debug("Oj is " + ipomdp.OjTheta);
		
		LOGGER.info("Checking Oi creation");
		ipomdp.currentOi = ipomdp.makeOi();
		
		assertEquals(ipomdp.currentOi.size(), ipomdp.getActions().size());
		
		for (String ai : ipomdp.getActions()) {
			
			for (DD oi : ipomdp.currentOi.get(ai))
				LOGGER.debug("for Ai = " + ai + " Oi is \r\n" + oi.toDDTree());
			
			assertEquals(
					ipomdp.currentOi.get(ai).length, 
					ipomdp.Omega.size() - ipomdp.OmegaJNames.size());
		}
		
		LOGGER.info("Checking Oi DD CPD");
		for (String Ai : ipomdp.currentOi.keySet()) {
			for (int s = 0; s < ipomdp.Omega.size() - ipomdp.OmegaJNames.size(); s++) {
				LOGGER.debug("For Ai " + Ai + " and o " + ipomdp.Omega.get(s));
				assertTrue(
						OP.maxAll(
								OP.abs(
									OP.sub(
										DD.one, 
										OP.addMultVarElim(
											ipomdp.currentOi.get(Ai)[s],
											IPOMDP.getVarIndex(
													ipomdp.Omega.get(s).name + "'"))))) < 1e-8);
			}
		}
		
		LOGGER.info("Checking Ti creation");
		ipomdp.currentTi = ipomdp.makeTi();
		
		assertEquals(ipomdp.currentTi.size(), ipomdp.getActions().size());
		
		for (String ai : ipomdp.getActions()) {
			
			for (DD ti : ipomdp.currentTi.get(ai))
				LOGGER.debug("For Ai=" + ai + " Ti=" + ti.toDDTree());
			
			assertEquals(
					ipomdp.currentTi.get(ai).length, 
					ipomdp.MjVarPosition);
		}
		
		LOGGER.info("Checking Ti DD factor CPD");
		
		for (String Ai : ipomdp.currentTi.keySet()) {
			for (int s = 0; s < ipomdp.MjVarPosition; s++) {
				LOGGER.debug("Checking for Ai " + Ai + " and s " + ipomdp.S.get(s));
				
				LOGGER.debug(OP.addMultVarElim(
						ipomdp.currentTi.get(Ai)[s],
						IPOMDP.getVarIndex(
								ipomdp.S.get(s).name + "'")));

				assertTrue(
						OP.maxAll(
								OP.abs(
									OP.sub(
										DD.one, 
										OP.addMultVarElim(
											ipomdp.currentTi.get(Ai)[s],
											IPOMDP.getVarIndex(
													ipomdp.S.get(s).name + "'"))))) < 1e-8);
			}
		}
		
		LOGGER.info("Checking Oj creation");
		ipomdp.currentOj = ipomdp.makeOj();
		
		for (int i = 0; i < ipomdp.currentOj.length; i++)
			LOGGER.debug("Oj for oj= " 
					+ ipomdp.OmegaJNames.get(i) + " is \r\n" 
					+ ipomdp.currentOj[i].toDDTree());
		
		LOGGER.info("Checking Oj DD CPD");
		
		for (int s = 0; s < ipomdp.currentOj.length; s++) {
			LOGGER.debug("For and OmegaJ " + ipomdp.OmegaJNames.get(s));
		
			assertTrue(
					OP.maxAll(
							OP.abs(
								OP.sub(
									DD.one, 
									OP.addMultVarElim(
										ipomdp.currentOj[s],
										IPOMDP.getVarIndex(
												ipomdp.OmegaJNames.get(s) + "'"))))) < 1e-8);
		}
			
		LOGGER.info("Check P(Aj| Mj) creation");
		ipomdp.currentAjGivenMj = ipomdp.multiFrameMJ.getAjGivenMj(ipomdp.ddMaker, ipomdp.Aj);
		LOGGER.debug("P(Aj| Mj) is \r\n" + ipomdp.currentAjGivenMj.toDDTree());
		LOGGER.debug(OP.addMultVarElim(ipomdp.currentAjGivenMj, IPOMDP.getVarIndex("A_j")));
		
		assertTrue(
				OP.maxAll(
						OP.abs(
							OP.sub(
								DD.one, 
								OP.addMultVarElim(
									ipomdp.currentAjGivenMj,
									IPOMDP.getVarIndex(
											"A_j"))))) < 1e-8);

		LOGGER.info("Check P(Thetaj| Mj) creation");
		ipomdp.currentThetajGivenMj = 
				ipomdp.multiFrameMJ.getThetajGivenMj(ipomdp.ddMaker, ipomdp.ThetaJ);
		LOGGER.debug("P(Thetaj| Mj) is \r\n" + ipomdp.currentThetajGivenMj.toDDTree());
		LOGGER.debug(
				OP.addMultVarElim(
						ipomdp.currentThetajGivenMj, IPOMDP.getVarIndex("Theta_j")));
		
		assertTrue(
				OP.maxAll(
						OP.abs(
							OP.sub(
								DD.one, 
								OP.addMultVarElim(
									ipomdp.currentThetajGivenMj,
									IPOMDP.getVarIndex(
											"Theta_j"))))) < 1e-8);
		
		LOGGER.info("Check P(Mj'| Mj, Oj', Aj) transition creation");
		ipomdp.currentMjPGivenMjOjPAj = ipomdp.makeOpponentModelTransitionDD();
		
		LOGGER.debug(
				OP.addMultVarElim(ipomdp.currentMjPGivenMjOjPAj, IPOMDP.getVarIndex("M_j'")));
		
		assertTrue(
				OP.maxAll(
						OP.abs(
							OP.sub(
								DD.one, 
								OP.addMultVarElim(
									ipomdp.currentMjPGivenMjOjPAj,
									IPOMDP.getVarIndex("M_j'"))))) < 1e-8);
		
		LOGGER.debug(ipomdp.currentMjPGivenMjOjPAj.toDDTree());
		
//		for (List<String> row : ipomdp.currentMjPGivenMjOjPAj.toDDTree().getCPT())
//			LOGGER.debug(row);
		
		LOGGER.info("Check initial belief");
		DD mjInit = ipomdp.multiFrameMJ.getMjInitBelief(ipomdp.ddMaker, null).toDD();
		DD initS = ipomdp.initBeliefDdTree.toDD();
		ipomdp.currentBelief = OP.reorder(OP.mult(mjInit, initS));
		LOGGER.debug(ipomdp.currentBelief);
		assertTrue(
				OP.maxAll(
						OP.abs(
							OP.sub(
								DD.one, 
								OP.addMultVarElim(
									ipomdp.currentBelief,
									ArrayUtils.subarray(
											ipomdp.stateVarIndices, 
											0, ipomdp.thetaVarPosition))))) < 1e-8);
		
		LOGGER.info("Checking Ri");
		ipomdp.currentRi = ipomdp.makeRi();
		
		for (String ai : ipomdp.Ai)
			LOGGER.debug("Ri for Ai=" + ai + " is " + ipomdp.currentRi.get(ai));
		
	}
	
	@Test
	void testIPOMDPsteppingSingleFrame() throws Exception {
		LOGGER.info("Running testIPOMDPsteppingSingleFrame()");
		
		IPOMDPParser parser = 
				new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 3, 20);
			
		/* start from current belief */
		DD start = tigerL1IPOMDP.getCurrentBelief();
		LOGGER.debug("Current belief is " + tigerL1IPOMDP.toMapWithTheta(start));
		
		String action1 = "listen";
		String[] obs1 = new String[] {"growl-right", "silence"};
		LOGGER.debug("Taking action " + action1 + " and observing " + Arrays.toString(obs1));
		
		DD nextBelief1 = tigerL1IPOMDP.beliefUpdate(start, action1, obs1);
		HashMap<String, HashMap<String, Float>> beliefMapFromUpdate = 
				tigerL1IPOMDP.toMapWithTheta(nextBelief1);
		LOGGER.debug("next belief from beliefUpdate is " 
				+ tigerL1IPOMDP.toMapWithTheta(nextBelief1));
		
		tigerL1IPOMDP.step(start, action1, obs1);
		LOGGER.debug("next belief from step is " 
				+ tigerL1IPOMDP.toMapWithTheta(tigerL1IPOMDP.getCurrentBelief()));
		
		LOGGER.debug("Assert equality between beliefUpdate and steps");
		HashMap<String, HashMap<String, Float>> beliefMapFromStep = 
				tigerL1IPOMDP.toMapWithTheta(tigerL1IPOMDP.getCurrentBelief());
		
		for (String varName : beliefMapFromUpdate.keySet()) {
			for (String child : beliefMapFromUpdate.get(varName).keySet()) {
				LOGGER.debug("Checking var=" + varName + " value=" + child);
				
				float bel2;
				
				if (beliefMapFromStep.get(varName).containsKey(child)) 
					bel2 = beliefMapFromStep.get(varName).get(child);
				else
					bel2 = 0;
				
				double bel1 = beliefMapFromUpdate.get(varName).get(child);
//				bel2 = beliefMapFromStep3.get(varName).get(child);
				
				LOGGER.debug("Difference between update and step is " + Math.abs(bel1 - bel2));
				
				assertTrue(
						Math.abs(bel1 - bel2) < 0.01);
			}
		}
		
		/* next step */
		start = tigerL1IPOMDP.getCurrentBelief();
		LOGGER.debug("Current belief is " + tigerL1IPOMDP.toMapWithTheta(start));
		
		String action2 = "listen";
		String[] obs2 = new String[] {"growl-right", "silence"};
		LOGGER.debug("Taking action " + action2 + " and observing " + Arrays.toString(obs2));
		
		DD nextBelief2 = tigerL1IPOMDP.beliefUpdate(start, action2, obs2);
		HashMap<String, HashMap<String, Float>> beliefMapFromUpdate2 = 
				tigerL1IPOMDP.toMapWithTheta(nextBelief2);
		LOGGER.debug("next belief from beliefUpdate is " 
				+ tigerL1IPOMDP.toMapWithTheta(nextBelief2));
		
		tigerL1IPOMDP.step(start, action2, obs2);
		LOGGER.debug("next belief from step is " 
				+ tigerL1IPOMDP.toMapWithTheta(tigerL1IPOMDP.getCurrentBelief()));
		
		LOGGER.debug("Assert equality between beliefUpdate and steps");
		HashMap<String, HashMap<String, Float>> beliefMapFromStep2 = 
				tigerL1IPOMDP.toMapWithTheta(tigerL1IPOMDP.getCurrentBelief());
		
		for (String varName : beliefMapFromUpdate2.keySet()) {
			for (String child : beliefMapFromUpdate2.get(varName).keySet()) {
				LOGGER.debug("Checking var=" + varName + " value=" + child);
				
				float bel2;
				
				if (beliefMapFromStep2.get(varName).containsKey(child)) 
					bel2 = beliefMapFromStep2.get(varName).get(child);
				else
					bel2 = 0;
				
				double bel1 = beliefMapFromUpdate2.get(varName).get(child);
//				bel2 = beliefMapFromStep2.get(varName).get(child);
				
				LOGGER.debug("Difference between update and step is " + Math.abs(bel1 - bel2));
				
				assertTrue(
						Math.abs(bel1 - bel2) < 0.01);
			}
		}
		
		/* next step */
		start = tigerL1IPOMDP.getCurrentBelief();
		LOGGER.debug("Current belief is " + tigerL1IPOMDP.toMapWithTheta(start));
		
		String action3 = "listen";
		String[] obs3 = new String[] {"growl-right", "creak-left"};
		LOGGER.debug("Taking action " + action3 + " and observing " + Arrays.toString(obs3));
		
		DD nextBelief3 = tigerL1IPOMDP.beliefUpdate(start, action3, obs3);
		HashMap<String, HashMap<String, Float>> beliefMapFromUpdate3 = 
				tigerL1IPOMDP.toMapWithTheta(nextBelief3);
		LOGGER.debug("next belief from beliefUpdate is " 
				+ tigerL1IPOMDP.toMapWithTheta(nextBelief3));
		
		tigerL1IPOMDP.step(start, action3, obs3);
		LOGGER.debug("next belief from step is " 
				+ tigerL1IPOMDP.toMapWithTheta(tigerL1IPOMDP.getCurrentBelief()));
		
		LOGGER.debug("Assert equality between beliefUpdate and steps");
		HashMap<String, HashMap<String, Float>> beliefMapFromStep3 = 
				tigerL1IPOMDP.toMapWithTheta(tigerL1IPOMDP.getCurrentBelief());
		
		for (String varName : beliefMapFromUpdate3.keySet()) {
			for (String child : beliefMapFromUpdate3.get(varName).keySet()) {
				LOGGER.debug("Checking var=" + varName + " value=" + child);
				
				float bel2;
				
				if (beliefMapFromStep3.get(varName).containsKey(child)) 
					bel2 = beliefMapFromStep3.get(varName).get(child);
				else
					bel2 = 0;
				
				double bel1 = beliefMapFromUpdate3.get(varName).get(child);
//				bel2 = beliefMapFromStep3.get(varName).get(child);
				
				LOGGER.debug("Difference between update and step is " + Math.abs(bel1 - bel2));
				
				assertTrue(
						Math.abs(bel1 - bel2) < 0.01);
			}
		}
		
//		LOGGER.info("Running random steps");
//		
//		parser = new IPOMDPParser(this.l1DomainFile);
//		parser.parseDomain();
//		
//		IPOMDP ipomdp = new IPOMDP(parser, 3, 20);
//		
//		Random rand = new Random();
//		
//		/* run for 100 iters */
//		for (int i = 0; i < 5; i++) {
//			
//			List<String> actions = ipomdp.getActions();
//			String action = actions.get(rand.nextInt(actions.size()));
//			
//			DD obsdist = ipomdp.getObsDist(ipomdp.getCurrentBelief(), action);
//			
//			int[][] obsConfig = OP.sampleMultinomial(obsdist, ipomdp.obsIVarPrimeIndices);
//			String[] obs = new String[obsConfig[0].length];
//			
//			for (int varI = 0; varI < obsConfig[0].length; varI ++) {
//				obs[varI] = Global.valNames[obsConfig[0][varI] - 1][obsConfig[1][varI] - 1];
//			}
//			
//			ipomdp.step(ipomdp.getCurrentBelief(), action, obs);
//		}
	}
	
	@Test
	void testIPOMDPstepping2Frames() throws Exception {
		LOGGER.info("Running testIPOMDPstepping2Frames()");
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/"
						+ "domains/tiger.L1multiple_new_parser.txt");
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 3, 20);
			
		/* start from current belief */
		DD start = tigerL1IPOMDP.getCurrentBelief();
		LOGGER.debug("Current belief is " + tigerL1IPOMDP.toMapWithTheta(start));
		
		String action1 = "listen";
		String[] obs1 = new String[] {"growl-right", "silence"};
		LOGGER.debug("Taking action " + action1 + " and observing " + Arrays.toString(obs1));
		
		DD nextBelief1 = tigerL1IPOMDP.beliefUpdate(start, action1, obs1);
		HashMap<String, HashMap<String, Float>> beliefMapFromUpdate = 
				tigerL1IPOMDP.toMapWithTheta(nextBelief1);
		LOGGER.debug("next belief from beliefUpdate is " 
				+ tigerL1IPOMDP.toMapWithTheta(nextBelief1));
		
		tigerL1IPOMDP.step(start, action1, obs1);
		LOGGER.debug("next belief from step is " 
				+ tigerL1IPOMDP.toMapWithTheta(tigerL1IPOMDP.getCurrentBelief()));
		
		LOGGER.debug("Assert equality between beliefUpdate and steps");
		HashMap<String, HashMap<String, Float>> beliefMapFromStep = 
				tigerL1IPOMDP.toMapWithTheta(tigerL1IPOMDP.getCurrentBelief());
		
		for (String varName : beliefMapFromUpdate.keySet()) {
			for (String child : beliefMapFromUpdate.get(varName).keySet()) {
				LOGGER.debug("Checking var=" + varName + " value=" + child);
				
				float bel2;
				
				if (beliefMapFromStep.get(varName).containsKey(child)) 
					bel2 = beliefMapFromStep.get(varName).get(child);
				else
					bel2 = 0;
				
				double bel1 = beliefMapFromUpdate.get(varName).get(child);
//				bel2 = beliefMapFromStep3.get(varName).get(child);
				
				LOGGER.debug("Difference between update and step is " + Math.abs(bel1 - bel2));
				
				assertTrue(
						Math.abs(bel1 - bel2) < 0.01);
			}
		}
		
		/* next step */
		start = tigerL1IPOMDP.getCurrentBelief();
		LOGGER.debug("Current belief is " + tigerL1IPOMDP.toMapWithTheta(start));
		
		String action2 = "listen";
		String[] obs2 = new String[] {"growl-right", "silence"};
		LOGGER.debug("Taking action " + action2 + " and observing " + Arrays.toString(obs2));
		
		DD nextBelief2 = tigerL1IPOMDP.beliefUpdate(start, action2, obs2);
		HashMap<String, HashMap<String, Float>> beliefMapFromUpdate2 = 
				tigerL1IPOMDP.toMapWithTheta(nextBelief2);
		LOGGER.debug("next belief from beliefUpdate is " 
				+ tigerL1IPOMDP.toMapWithTheta(nextBelief2));
		
		tigerL1IPOMDP.step(start, action2, obs2);
		LOGGER.debug("next belief from step is " 
				+ tigerL1IPOMDP.toMapWithTheta(tigerL1IPOMDP.getCurrentBelief()));
		
		LOGGER.debug("Assert equality between beliefUpdate and steps");
		HashMap<String, HashMap<String, Float>> beliefMapFromStep2 = 
				tigerL1IPOMDP.toMapWithTheta(tigerL1IPOMDP.getCurrentBelief());
		
		for (String varName : beliefMapFromUpdate2.keySet()) {
			for (String child : beliefMapFromUpdate2.get(varName).keySet()) {
				LOGGER.debug("Checking var=" + varName + " value=" + child);
				
				float bel2;
				
				if (beliefMapFromStep2.get(varName).containsKey(child)) 
					bel2 = beliefMapFromStep2.get(varName).get(child);
				else
					bel2 = 0;
				
				double bel1 = beliefMapFromUpdate2.get(varName).get(child);
//				bel2 = beliefMapFromStep3.get(varName).get(child);
				
				LOGGER.debug("Difference between update and step is " + Math.abs(bel1 - bel2));
				
				assertTrue(
						Math.abs(bel1 - bel2) < 0.01);
			}
		}
		
		/* next step */
		start = tigerL1IPOMDP.getCurrentBelief();
		LOGGER.debug("Current belief is " + tigerL1IPOMDP.toMapWithTheta(start));
		
		String action3 = "listen";
		String[] obs3 = new String[] {"growl-right", "creak-left"};
		LOGGER.debug("Taking action " + action3 + " and observing " + Arrays.toString(obs3));
		
		DD nextBelief3 = tigerL1IPOMDP.beliefUpdate(start, action3, obs3);
		HashMap<String, HashMap<String, Float>> beliefMapFromUpdate3 = 
				tigerL1IPOMDP.toMapWithTheta(nextBelief3);
		LOGGER.debug("next belief from beliefUpdate is " 
				+ tigerL1IPOMDP.toMapWithTheta(nextBelief3));
		
		tigerL1IPOMDP.step(start, action3, obs3);
		LOGGER.debug("next belief from step is " 
				+ tigerL1IPOMDP.toMapWithTheta(tigerL1IPOMDP.getCurrentBelief()));
		
		LOGGER.debug("Assert equality between beliefUpdate and steps");
		HashMap<String, HashMap<String, Float>> beliefMapFromStep3 = 
				tigerL1IPOMDP.toMapWithTheta(tigerL1IPOMDP.getCurrentBelief());
		
		for (String varName : beliefMapFromUpdate3.keySet()) {
			for (String child : beliefMapFromUpdate3.get(varName).keySet()) {
				LOGGER.debug("Checking var=" + varName + " value=" + child);
				
				float bel2;
				
				if (beliefMapFromStep3.get(varName).containsKey(child)) 
					bel2 = beliefMapFromStep3.get(varName).get(child);
				else
					bel2 = 0;
				
				double bel1 = beliefMapFromUpdate3.get(varName).get(child);
//				bel2 = beliefMapFromStep3.get(varName).get(child);
				
				LOGGER.debug("Difference between update and step is " + Math.abs(bel1 - bel2));
				
				assertTrue(
						Math.abs(bel1 - bel2) < 0.1);
			}
		}
		
//		LOGGER.info("Running random steps");
//		
//		parser = new IPOMDPParser(
//						"/home/adityas/git/repository/Protos/"
//						+ "domains/tiger.L1multiple_new_parser.txt");
//		parser.parseDomain();
//		
//		IPOMDP ipomdp = new IPOMDP(parser, 3, 20);
//		
//		Random rand = new Random();
//		
//		/* run for 100 iters */
//		for (int i = 0; i < 5; i++) {
//			
//			List<String> actions = ipomdp.getActions();
//			String action = actions.get(rand.nextInt(actions.size()));
//			
//			DD obsdist = ipomdp.getObsDist(ipomdp.getCurrentBelief(), action);
//			
//			int[][] obsConfig = OP.sampleMultinomial(obsdist, ipomdp.obsIVarPrimeIndices);
//			String[] obs = new String[obsConfig[0].length];
//			
//			for (int varI = 0; varI < obsConfig[0].length; varI ++) {
//				obs[varI] = Global.valNames[obsConfig[0][varI] - 1][obsConfig[1][varI] - 1];
//			}
//			
//			ipomdp.step(ipomdp.getCurrentBelief(), action, obs);
//		}
		
	}
	
//	@Test
//	void testIPOMDPSerialization() 
//			throws Exception {
//		LOGGER.info("Running testIPOMDPSerialization()");
//		
//		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
//		parser.parseDomain();
//		
//		/* Initialize IPOMDP */
//		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 3, 20);
//		
//		tigerL1IPOMDP.step(
//				tigerL1IPOMDP.getInitialBeliefs().get(0), 
//				"listen", 
//				new String[] {"growl-left", "silence"});
//		
//		IPOMDP.saveIPOMDP(tigerL1IPOMDP, "/tmp/tigerIPOMDP.obj");
//		
//		IPOMDP ipomdp = IPOMDP.loadIPOMDP("/tmp/tigerIPOMDP.obj");
//		
//		ipomdp.step(
//				ipomdp.getInitialBeliefs().get(0), 
//				"listen", 
//				new String[] {"growl-left", "silence"});
//	}
	
	@Test
	void testBeliefToJSON() {
		
		LOGGER.info("Testing testBeliefToJSON()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/* Initialize IPOMDP */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 3, 20);
		
		LOGGER.debug(tigerL1IPOMDP.getBeliefString(tigerL1IPOMDP.getCurrentBelief()));
		LOGGER.debug(
				StructuredTree.jsonBeliefStringToDotNode(
						tigerL1IPOMDP.getBeliefString(tigerL1IPOMDP.getCurrentBelief()), ""));
	}
	
//	@Test
//	void testIPOMDPSS() {
//		
//		LOGGER.info("Testing IPOMDP stochastic sim");
//		
//		IPOMDPParser parser = 
//				new IPOMDPParser("/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
//		parser.parseDomain();
//		
//		/* Initialize IPOMDP */
//		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 3, 20);
//		
//		OnlineInteractiveSymbolicPerseus sp = 
//				new OnlineInteractiveSymbolicPerseus(
//						tigerL1IPOMDP, 
//						new FullBeliefExpansion(tigerL1IPOMDP), 
//						1, 100);
//		
//		StochasticSimulation ss = new StochasticSimulation(sp, 10);
//		ss.runSimulation();
//		LOGGER.info("\r\n" + ss.getDotString());
//		
//		ss.logResults();
//	}
	
	@Test
	void testNZPrimesTime() throws Exception {
		
		LOGGER.info("Testing IPOMDP NZ prime computation time");
		
		IPOMDPParser parser = 
				new IPOMDPParser("/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		parser.parseDomain();
		
		NextBelStateCache.useCache();
		
		/* Initialize IPOMDP */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 4, 20);
		
		FullBeliefExpansion fb = new FullBeliefExpansion(tigerL1IPOMDP);
//		fb.expand();
//		
//		NextBelStateCache.populateCache(tigerL1IPOMDP, fb.getBeliefPoints());
//		LOGGER.debug(NextBelStateCache.NEXT_BELSTATE_CACHE);
		
		OnlineInteractiveSymbolicPerseus sp = 
				new OnlineInteractiveSymbolicPerseus(
						tigerL1IPOMDP, 
						fb, 
						1, 100);
		
		sp.solveCurrentStep();
		sp.nextStep(
				sp.getActionAtCurrentBelief(), 
				Arrays.asList(new String[] {"growl-left", "silence"}));
		
		sp.solveCurrentStep();
		sp.nextStep(
				sp.getActionAtCurrentBelief(), 
				Arrays.asList(new String[] {"growl-left", "silence"}));
		
		sp.solveCurrentStep();
		
		sp.nextStep(
				sp.getActionAtCurrentBelief(), 
				Arrays.asList(new String[] {"growl-left", "silence"}));
		
		sp.solveCurrentStep();

//		StochasticSimulation ss = new StochasticSimulation(sp, 5);
//		ss.runSimulation();
//		LOGGER.info("\r\n" + ss.getDotString());
//		
//		ss.logResults();
		
	}
	
	@Test
	void testJointActionTi() {
		
		LOGGER.info("Testing joint action Ti creation");
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/"
//						+ "final_domains/deception.5S.2O.L1.2F.domain");
		
//		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/* Initialize IPOMDP */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 3, 20);
		
//		tigerL1IPOMDP.convertToJointActionTi();
		
	}
	
}
