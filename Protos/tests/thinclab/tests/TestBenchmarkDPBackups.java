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
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.FullBeliefExpansion;
import thinclab.belief.IBeliefOps;
import thinclab.belief.SparseFullBeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.legacy.AlphaVector;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.parsers.IPOMDPParser;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;
import thinclab.utils.Diagnostics;
import thinclab.utils.NextBelStateCache;

/*
 * @author adityas
 *
 */

@SuppressWarnings("unused")
class TestBenchmarkDPBackups {

	public String l1DomainFile;
	private static Logger LOGGER;

	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		LOGGER = LogManager.getLogger(TestBenchmarkDPBackups.class);
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testDPBackups() throws Exception {

//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/"
//						+ "final_domains/deception.5S.2O.L1.2F.domain");
		IPOMDPParser parser = new IPOMDPParser(
				"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");

		parser.parseDomain();

		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);

		LOGGER.debug("TAU contains " + ipomdp.currentTau.getNumLeaves() + " DD nodes");

		DD currentTauxPAjGivenMj = OP.mult(ipomdp.currentTau, ipomdp.currentAjGivenMj);
		LOGGER.debug("TAU x P(Aj | Mj) contains " + currentTauxPAjGivenMj.getNumLeaves() + " DD nodes");

		DD TauXPAjGivenMjXThetajGivenMj = OP.mult(currentTauxPAjGivenMj, ipomdp.currentThetajGivenMj);
		LOGGER.debug("TAU x P(Aj | Mj) x P(Thetaj | Mj)contains " + TauXPAjGivenMjXThetajGivenMj.getNumLeaves()
				+ " DD nodes");

		SparseFullBeliefExpansion BE = new SparseFullBeliefExpansion(ipomdp, 10);
		BE.expand();
		List<DD> exploredBeliefs = BE.getBeliefPoints();

		LOGGER.debug("Found " + exploredBeliefs.size() + " beliefs");

		OnlineInteractiveSymbolicPerseus solver = new OnlineInteractiveSymbolicPerseus(ipomdp, BE, 1, 100);

		DD[] alphaVectors = solver.getAlphaVectors();
		LOGGER.debug("Making primed V");

		/* make primed V */
		DD[] primedV = new DD[alphaVectors.length];

		for (int i = 0; i < alphaVectors.length; i++) {
			primedV[i] = OP.primeVars(alphaVectors[i], ipomdp.S.size() + ipomdp.Omega.size());
		}

		/* compute maxAbsVal */
		float maxAbsVal = Math
				.max(OP.maxabs(IPOMDP.concatenateArray(OP.maxAllN(alphaVectors), OP.minAllN(alphaVectors))), 1e-10f);

		DD[][] factoredBeliefs = ipomdp.factorBeliefRegion(exploredBeliefs);

		List<Float> times = new ArrayList<Float>();

		/* do all those iterations of DP backup and check computation time */
		for (int i = 0; i < exploredBeliefs.size(); i++) {

			long then = System.nanoTime();

			AlphaVector vec = AlphaVector.dpBackup2(ipomdp, exploredBeliefs.get(i), /* factoredBeliefs[i], */ primedV,
					maxAbsVal, alphaVectors.length);

			long now = System.nanoTime();
			times.add((float) (now - then) / 1000000);
		}

		LOGGER.debug("DP backup took " + times.stream().mapToDouble(a -> a).average().getAsDouble() + " msec");

		Diagnostics.reportDiagnostics();
	}

	@Test
	void testDPBackupsCorrectness() throws Exception {

		IPOMDPParser parser = new IPOMDPParser(
				"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");

//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/"
//						+ "final_domains/deception.5S.2O.L1.2F.domain");

		parser.parseDomain();

		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);

		LOGGER.debug("TAU contains " + ipomdp.currentTau.getNumLeaves() + " DD nodes");

		DD currentTauxPAjGivenMj = OP.mult(ipomdp.currentTau, ipomdp.currentAjGivenMj);
		LOGGER.debug("TAU x P(Aj | Mj) contains " + currentTauxPAjGivenMj.getNumLeaves() + " DD nodes");

		DD TauXPAjGivenMjXThetajGivenMj = OP.mult(currentTauxPAjGivenMj, ipomdp.currentThetajGivenMj);
		LOGGER.debug("TAU x P(Aj | Mj) x P(Thetaj | Mj)contains " + TauXPAjGivenMjXThetajGivenMj.getNumLeaves()
				+ " DD nodes");

		SparseFullBeliefExpansion BE = new SparseFullBeliefExpansion(ipomdp, 10);
		BE.expand();
		List<DD> exploredBeliefs = BE.getBeliefPoints();

		LOGGER.debug("Found " + exploredBeliefs.size() + " beliefs");

		OnlineInteractiveSymbolicPerseus solver = new OnlineInteractiveSymbolicPerseus(ipomdp, BE, 1, 100);

		DD[] alphaVectors = solver.getAlphaVectors();
		LOGGER.debug("Making primed V");

		/* make primed V */
		DD[] primedV = new DD[alphaVectors.length];

		for (int i = 0; i < alphaVectors.length; i++) {
			primedV[i] = OP.primeVars(alphaVectors[i], ipomdp.S.size() + ipomdp.Omega.size());
		}

		/* compute maxAbsVal */
		float maxAbsVal = Math
				.max(OP.maxabs(IPOMDP.concatenateArray(OP.maxAllN(alphaVectors), OP.minAllN(alphaVectors))), 1e-10f);

		DD[][] factoredBeliefs = ipomdp.factorBeliefRegion(exploredBeliefs);

		/* do all those iterations of DP backup and check computation time */
		for (int i = 0; i < exploredBeliefs.size(); i++) {

			AlphaVector vec1 = AlphaVector.dpBackup(ipomdp, exploredBeliefs.get(i), factoredBeliefs[i], primedV,
					maxAbsVal, alphaVectors.length);

			AlphaVector vec2 = AlphaVector.dpBackup2(ipomdp, exploredBeliefs.get(i), factoredBeliefs[i], primedV,
					maxAbsVal, alphaVectors.length);

			LOGGER.debug("Legacy A Vec VAR SET is: " + Arrays.toString(vec1.alphaVector.getVarSet()));
			LOGGER.debug("Legacy A Vec is " + vec1.alphaVector.toDDTree());
			LOGGER.debug("New A Vec VAR SET is: " + Arrays.toString(vec2.alphaVector.getVarSet()));
			LOGGER.debug("New A Vec is " + vec2.alphaVector.toDDTree());

			float diff = OP.maxAll(OP.abs(OP.sub(vec1.alphaVector, vec2.alphaVector)));
			LOGGER.debug("Diff is " + diff);
			assertTrue(diff < 1e-8);
		}
	}

	@Test
	void testDPBackupsCorrectnessWithoutFactoredBelState() throws Exception {

		IPOMDPParser parser = new IPOMDPParser(
				"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");

//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/"
//						+ "final_domains/deception.5S.2O.L1.2F.domain");

		parser.parseDomain();

		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);

		SparseFullBeliefExpansion BE = new SparseFullBeliefExpansion(ipomdp, 10);
		BE.expand();
		List<DD> exploredBeliefs = BE.getBeliefPoints();

		LOGGER.debug("Found " + exploredBeliefs.size() + " beliefs");

		OnlineInteractiveSymbolicPerseus solver = new OnlineInteractiveSymbolicPerseus(ipomdp, BE, 1, 100);

		DD[] alphaVectors = solver.getAlphaVectors();
		LOGGER.debug("Making primed V");

		/* make primed V */
		DD[] primedV = new DD[alphaVectors.length];

		for (int i = 0; i < alphaVectors.length; i++) {
			primedV[i] = OP.primeVars(alphaVectors[i], ipomdp.S.size() + ipomdp.Omega.size());
		}

		/* compute maxAbsVal */
		float maxAbsVal = Math
				.max(OP.maxabs(IPOMDP.concatenateArray(OP.maxAllN(alphaVectors), OP.minAllN(alphaVectors))), 1e-10f);

		DD[][] factoredBeliefs = ipomdp.factorBeliefRegion(exploredBeliefs);

		/* do all those iterations of DP backup and check computation time */
		for (int i = 0; i < exploredBeliefs.size(); i++) {

			AlphaVector vec1 = AlphaVector.dpBackup2(ipomdp, exploredBeliefs.get(i), factoredBeliefs[i], primedV,
					maxAbsVal, alphaVectors.length);

			AlphaVector vec2 = AlphaVector.dpBackup2(ipomdp, exploredBeliefs.get(i), /* factoredBeliefs[i], */primedV,
					maxAbsVal, alphaVectors.length);

			float diff = OP.maxAll(OP.abs(OP.sub(vec1.alphaVector, vec2.alphaVector)));
			LOGGER.debug("Diff is " + diff);
			assertTrue(diff < 1e-8);
		}
	}

	@Test
	void testDPBackupsCorrectnessWithoutFactoredBelStateForPOMDPs() throws Exception {

//		POMDP pomdp = new POMDP("/home/adityas/UGA/THINCLab/DomainFiles/final_domains/"
//				+ "exfil.6S.L0.obs_deception.domain");

		POMDP pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");

		FullBeliefExpansion BE = new FullBeliefExpansion(pomdp, 4);
		BE.expand();

		List<DD> exploredBeliefs = BE.getBeliefPoints();

		LOGGER.debug("Found " + exploredBeliefs.size() + " beliefs");

		OfflineSymbolicPerseus solver = new OfflineSymbolicPerseus(pomdp, BE, 3, 50);

		DD[] alphaVectors = solver.getAlphaVectors();
		LOGGER.debug("Making primed V");

		/* make primed V */
		DD[] primedV = new DD[alphaVectors.length];

		for (int i = 0; i < alphaVectors.length; i++) {
			primedV[i] = OP.primeVars(alphaVectors[i], pomdp.getNumVars());
		}

		/* compute maxAbsVal */
		float maxAbsVal = Math
				.max(OP.maxabs(POMDP.concatenateArray(OP.maxAllN(alphaVectors), OP.minAllN(alphaVectors))), 1e-10f);

		DD[][] factoredBeliefs = pomdp.factorBeliefRegion(exploredBeliefs);

		/* do all those iterations of DP backup and check computation time */
		for (int i = 0; i < exploredBeliefs.size(); i++) {

			AlphaVector vec1 = AlphaVector.dpBackup(pomdp, factoredBeliefs[i], primedV, maxAbsVal, alphaVectors.length);

			AlphaVector vec2 = AlphaVector.dpBackup(pomdp, exploredBeliefs.get(i), primedV, maxAbsVal,
					alphaVectors.length);

			float diff = OP.maxAll(OP.abs(OP.sub(vec1.alphaVector, vec2.alphaVector)));
			LOGGER.debug("Diff is " + diff);
			assertTrue(diff < 1e-8);
		}
	}

	@Test
	void testDPBackupsForPOMDPs() throws Exception {

//		POMDP pomdp = new POMDP("/home/adityas/UGA/THINCLab/DomainFiles/final_domains/"
//				+ "exfil.6S.L0.obs_deception.domain");

		POMDP pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");

		NextBelStateCache.useCache();

		FullBeliefExpansion BE = new FullBeliefExpansion(pomdp, 3);
		BE.expand();

		List<DD> exploredBeliefs = BE.getBeliefPoints();

		LOGGER.debug("Found " + exploredBeliefs.size() + " beliefs");

		OfflineSymbolicPerseus solver = new OfflineSymbolicPerseus(pomdp, BE, 3, 50);

		DD[] alphaVectors = solver.getAlphaVectors();
		LOGGER.debug("Making primed V");

		/* make primed V */
		DD[] primedV = new DD[alphaVectors.length];

		for (int i = 0; i < alphaVectors.length; i++) {
			primedV[i] = OP.primeVars(alphaVectors[i], pomdp.getNumVars());
		}

		/* compute maxAbsVal */
		float maxAbsVal = Math
				.max(OP.maxabs(IPOMDP.concatenateArray(OP.maxAllN(alphaVectors), OP.minAllN(alphaVectors))), 1e-10f);

		DD[][] factoredBeliefs = pomdp.factorBeliefRegion(exploredBeliefs);

		List<Float> times = new ArrayList<Float>();

		/* do all those iterations of DP backup and check computation time */
		for (int i = 0; i < exploredBeliefs.size(); i++) {

			long then = System.nanoTime();

			AlphaVector vec = AlphaVector.dpBackup(pomdp, exploredBeliefs.get(i), /* factoredBeliefs[i], */ primedV,
					maxAbsVal, alphaVectors.length);

			long now = System.nanoTime();
			times.add((float) (now - then) / 1000000);
		}

		LOGGER.debug("DP backup took " + times.stream().mapToDouble(a -> a).average().getAsDouble() + " msec");

		Diagnostics.reportDiagnostics();
	}

	@Test
	void testPreMultFactors() throws Exception {

//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/"
//						+ "final_domains/deception.5S.2O.L1.2F.domain");

		IPOMDPParser parser = new IPOMDPParser(
				"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");

		parser.parseDomain();

		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);

		LOGGER.debug("TAU contains " + ipomdp.currentTau.getNumLeaves() + " DD nodes");
		LOGGER.debug("TAU x P(Aj | Mj) x P(Thetaj | Mj) contains "
				+ ipomdp.currentTauXPAjGivenMjXPThetajGivenMj.getNumLeaves() + " DD nodes");

		List<Float> times = new ArrayList<Float>();

		IBeliefOps belOps = (IBeliefOps) ipomdp.bOPs;

		Global.clearHashtables();

		/* do all those iterations of DP backup and check computation time */
		for (int i = 0; i < 100; i++) {

			Random rand = new Random(100);

			int a = rand.nextInt(ipomdp.getActions().size());
			int o = rand.nextInt(ipomdp.getAllPossibleObservations().size());

			long then = System.nanoTime();

			DD nextBel = belOps.beliefUpdate(ipomdp.getCurrentBelief(), ipomdp.getActions().get(a),
					ipomdp.getAllPossibleObservations().get(o).stream().toArray(String[]::new));

			long now = System.nanoTime();
			times.add((float) (now - then) / 1000000);
		}

		LOGGER.debug(
				"Legacy belief updates took " + times.stream().mapToDouble(a -> a).average().getAsDouble() + " msec");

	}

	@Test
	void testObsDist() throws Exception {

//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/"
//						+ "final_domains/deception.5S.2O.L1.2F.domain");

		IPOMDPParser parser = new IPOMDPParser(
				"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");

		parser.parseDomain();

		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);

		LOGGER.debug("TAU contains " + ipomdp.currentTau.getNumLeaves() + " DD nodes");
		LOGGER.debug("TAU x P(Aj | Mj) x P(Thetaj | Mj) contains "
				+ ipomdp.currentTauXPAjGivenMjXPThetajGivenMj.getNumLeaves() + " DD nodes");

		List<Float> times = new ArrayList<Float>();

		IBeliefOps belOps = (IBeliefOps) ipomdp.bOPs;

		Global.clearHashtables();

		/* do all those iterations of DP backup and check computation time */
		for (int i = 0; i < 100; i++) {

			Random rand = new Random(100);

			int a = rand.nextInt(ipomdp.getActions().size());

			long then = System.nanoTime();

			DD obsDist = belOps.getObsDist2(ipomdp.getCurrentBelief(), ipomdp.getActions().get(a));

			long now = System.nanoTime();
			times.add((float) (now - then) / 1000000);
		}

		LOGGER.debug("Legacy obs dist computation took " + times.stream().mapToDouble(a -> a).average().getAsDouble()
				+ " msec");

	}

	@Test
	void testObsDistCorrectness() throws Exception {

//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/"
//						+ "final_domains/deception.5S.2O.L1.2F.domain");

		IPOMDPParser parser = new IPOMDPParser(
				"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");

		parser.parseDomain();

		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);

		LOGGER.debug("TAU contains " + ipomdp.currentTau.getNumLeaves() + " DD nodes");
		LOGGER.debug("TAU x P(Aj | Mj) x P(Thetaj | Mj) contains "
				+ ipomdp.currentTauXPAjGivenMjXPThetajGivenMj.getNumLeaves() + " DD nodes");

		List<Float> times = new ArrayList<Float>();

		IBeliefOps belOps = (IBeliefOps) ipomdp.bOPs;

		Global.clearHashtables();

		/* do all those iterations of DP backup and check computation time */
		for (int i = 0; i < 100; i++) {

			Random rand = new Random(100);

			int a = rand.nextInt(ipomdp.getActions().size());

			DD obsDist2 = belOps.getObsDist2(ipomdp.getCurrentBelief(), ipomdp.getActions().get(a));

			DD obsDist = belOps.getObsDist(ipomdp.getCurrentBelief(), ipomdp.getActions().get(a));

			float diff = OP.maxAll(OP.abs(OP.sub(obsDist, obsDist2)));
			LOGGER.debug("Diff is: " + diff);
			assertTrue(diff < 1e-8);
		}

	}

	@Test
	void testPreMultFactorsCorrectness() throws Exception {

//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/"
//						+ "final_domains/deception.5S.2O.L1.2F.domain");

		IPOMDPParser parser = new IPOMDPParser(
				"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");

		parser.parseDomain();

		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);

		LOGGER.debug("TAU contains " + ipomdp.currentTau.getNumLeaves() + " DD nodes");
		LOGGER.debug("TAU x P(Aj | Mj) x P(Thetaj | Mj) contains "
				+ ipomdp.currentTauXPAjGivenMjXPThetajGivenMj.getNumLeaves() + " DD nodes");

		IBeliefOps belOps = (IBeliefOps) ipomdp.bOPs;

		Global.clearHashtables();

		/* do all those iterations of DP backup and check computation time */
		for (int i = 0; i < 100; i++) {

			Random rand = new Random(100);

			int a = rand.nextInt(ipomdp.getActions().size());
			int o = rand.nextInt(ipomdp.getAllPossibleObservations().size());

			DD nextBel1 = belOps.beliefUpdate(ipomdp.getCurrentBelief(), ipomdp.getActions().get(a),
					ipomdp.getAllPossibleObservations().get(o).stream().toArray(String[]::new));

			DD nextBel2 = belOps.beliefUpdate(ipomdp.getCurrentBelief(), ipomdp.getActions().get(a),
					ipomdp.getAllPossibleObservations().get(o).stream().toArray(String[]::new));

			float diff = OP.maxAll(OP.abs(OP.sub(nextBel1, nextBel2)));
			LOGGER.debug("difference is: " + diff);
			assertTrue(diff < 1e-8);

		}

	}

}
