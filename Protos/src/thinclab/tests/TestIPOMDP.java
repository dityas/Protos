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
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.FullBeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.parsers.IPOMDPParser;
import thinclab.representations.StructuredTree;
import thinclab.simulations.StochasticSimulation;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;

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
		
		LOGGER.info("Check ObsJ combinations");
		LOGGER.debug(ipomdp.multiFrameMJ.obsCombinations);
		assertEquals(ipomdp.multiFrameMJ.obsCombinations.size(), 2);
		
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
		ipomdp.setMjDepth(3);
		ipomdp.setMjLookAhead(2);
		
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
				assertEquals(
						beliefMapFromUpdate.get(varName).get(child), 
						beliefMapFromStep.get(varName).get(child));
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
				assertEquals(
						beliefMapFromUpdate2.get(varName).get(child), 
						beliefMapFromStep2.get(varName).get(child));
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
				assertEquals(
						beliefMapFromUpdate3.get(varName).get(child), 
						beliefMapFromStep3.get(varName).get(child));
			}
		}
		
		LOGGER.info("Running random steps");
		
		parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 3, 20);
		
		Random rand = new Random();
		
		/* run for 100 iters */
		for (int i = 0; i < 10; i++) {
			
			List<String> actions = ipomdp.getActions();
			String action = actions.get(rand.nextInt(actions.size()));
			
			DD obsdist = ipomdp.getObsDist(ipomdp.getCurrentBelief(), action);
			
			int[][] obsConfig = OP.sampleMultinomial(obsdist, ipomdp.obsIVarPrimeIndices);
			String[] obs = new String[obsConfig[0].length];
			
			for (int varI = 0; varI < obsConfig[0].length; varI ++) {
				obs[varI] = Global.valNames[obsConfig[0][varI] - 1][obsConfig[1][varI] - 1];
			}
			
			ipomdp.step(ipomdp.getCurrentBelief(), action, obs);
		}
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
				assertEquals(
						beliefMapFromUpdate.get(varName).get(child), 
						beliefMapFromStep.get(varName).get(child));
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
				assertEquals(
						beliefMapFromUpdate2.get(varName).get(child), 
						beliefMapFromStep2.get(varName).get(child));
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
				assertEquals(
						beliefMapFromUpdate3.get(varName).get(child), 
						beliefMapFromStep3.get(varName).get(child));
			}
		}
		
		LOGGER.info("Running random steps");
		
		parser = new IPOMDPParser(
						"/home/adityas/git/repository/Protos/"
						+ "domains/tiger.L1multiple_new_parser.txt");
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 3, 20);
		
		Random rand = new Random();
		
		/* run for 100 iters */
		for (int i = 0; i < 10; i++) {
			
			List<String> actions = ipomdp.getActions();
			String action = actions.get(rand.nextInt(actions.size()));
			
			DD obsdist = ipomdp.getObsDist(ipomdp.getCurrentBelief(), action);
			
			int[][] obsConfig = OP.sampleMultinomial(obsdist, ipomdp.obsIVarPrimeIndices);
			String[] obs = new String[obsConfig[0].length];
			
			for (int varI = 0; varI < obsConfig[0].length; varI ++) {
				obs[varI] = Global.valNames[obsConfig[0][varI] - 1][obsConfig[1][varI] - 1];
			}
			
			ipomdp.step(ipomdp.getCurrentBelief(), action, obs);
		}
		
	}
	
	@Test
	void testIPOMDPSerialization() 
			throws Exception {
		LOGGER.info("Running testIPOMDPSerialization()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/* Initialize IPOMDP */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 3, 20);
		
		tigerL1IPOMDP.step(
				tigerL1IPOMDP.getInitialBeliefs().get(0), 
				"listen", 
				new String[] {"growl-left", "silence"});
		
		IPOMDP.saveIPOMDP(tigerL1IPOMDP, "/tmp/tigerIPOMDP.obj");
		
		IPOMDP ipomdp = IPOMDP.loadIPOMDP("/tmp/tigerIPOMDP.obj");
		
		ipomdp.step(
				ipomdp.getInitialBeliefs().get(0), 
				"listen", 
				new String[] {"growl-left", "silence"});
	}
	
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
	
	@Test
	void testIPOMDPSS() {
		
		LOGGER.info("Testing IPOMDP stochastic sim");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/* Initialize IPOMDP */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 3, 20);
		
		OnlineInteractiveSymbolicPerseus sp = 
				new OnlineInteractiveSymbolicPerseus(
						tigerL1IPOMDP, 
						new FullBeliefExpansion(tigerL1IPOMDP), 
						1, 100);
		
		StochasticSimulation ss = new StochasticSimulation(sp, 2);
		ss.runSimulation();
		LOGGER.info("\r\n" + ss.getDotString());
	}
	
	
	
}
