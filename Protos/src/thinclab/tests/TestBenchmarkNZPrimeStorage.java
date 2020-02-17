/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.FloatBuffer;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.naming.spi.DirStateFactory.Result;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.FullBeliefExpansion;
import thinclab.belief.SparseFullBeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.NextBelState;
import thinclab.legacy.OP;
import thinclab.parsers.IPOMDPParser;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.utils.CacheDB;
import thinclab.utils.CustomConfigurationFactory;
import thinclab.utils.NextBelStateCache;

/*
 * @author adityas
 *
 */
class TestBenchmarkNZPrimeStorage {
	
	public String l1DomainFile;
	private static Logger LOGGER;

	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		LOGGER = Logger.getLogger(TestBenchmarkNZPrimeStorage.class);
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void testPBVComputation() throws Exception {
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						this.l1DomainFile);
		
		
		parser.parseDomain();
		
		LOGGER.info("IPOMDP init");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);
		
		SparseFullBeliefExpansion BE = new SparseFullBeliefExpansion(ipomdp, 30);
		BE.expand();
		
		OnlineInteractiveSymbolicPerseus SP = 
				new OnlineInteractiveSymbolicPerseus(ipomdp, BE, 1, 1);
		
		List<DD> beliefs = BE.getBeliefPoints();
		DD[] beliefsArray = beliefs.stream().toArray(DD[]::new);
		DD[][] fBeliefs = ipomdp.factorBeliefRegion(beliefs);
		int[] vars = ArrayUtils.subarray(ipomdp.stateVarIndices, 0, ipomdp.thetaVarPosition);
		
		List<Double> times = new ArrayList<Double>();
		
		double[][] opPBVs = null;
		
		for (int i = 0; i < 10; i ++) {
			long then = System.nanoTime();
			
			opPBVs = OP.factoredExpectationSparseNoMem(fBeliefs, SP.alphaVectors);
//			double[][] PBVs = OP.dotProduct(beliefsArray, SP.alphaVectors, vars);
			
			long now = System.nanoTime();
			
			times.add((double) (now - then) / 1000000);
		}
		
		LOGGER.debug("Factoring took " + times.stream().mapToDouble(a -> a).average().getAsDouble() 
				+ " msec");
		
		List<Double> times2 = new ArrayList<Double>();
		double[][] dotPBVs = null;
		for (int i = 0; i < 10; i ++) {
			long then = System.nanoTime();
			
//			double[] PBVs = OP.factoredExpectationSparseNoMem(beliefsArray, SP.alphaVectors);
			dotPBVs = OP.dotProduct(beliefsArray, SP.alphaVectors, vars);
			
			long now = System.nanoTime();
			
			times2.add((double) (now - then) / 1000000);
		}
		
		LOGGER.debug("Dot took " + times2.stream().mapToDouble(a -> a).average().getAsDouble() 
				+ " msec");
		
		LOGGER.debug("Checking correctness");
		
		for (int i = 0; i < dotPBVs.length; i++) {
			for (int j = 0; j < dotPBVs[i].length; j++) {
				double diff = Math.abs(opPBVs[i][j] - dotPBVs[i][j]);
				LOGGER.debug("Diff is " + diff);
				assertTrue(diff < 1e-4);
			}
		}
		
	}
	
	@Test
	void testPBVComputationForPOMDPs() throws Exception {
		
//		POMDP pomdp = new POMDP("/home/adityas/UGA/THINCLab/DomainFiles/final_domains/"
//				+ "exfil.6S.L0.obs_deception.domain");
		POMDP pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");
		
		FullBeliefExpansion BE = new FullBeliefExpansion(pomdp, 3);
		BE.expand();
		
		OfflineSymbolicPerseus solver = new OfflineSymbolicPerseus(pomdp, BE, 3, 50);
		
		List<DD> beliefs = BE.getBeliefPoints();
		DD[] beliefsArray = beliefs.stream().toArray(DD[]::new);
		DD[][] fBeliefs = pomdp.factorBeliefRegion(beliefs);
		int[] vars = pomdp.getStateVarIndices();
		
		List<Double> times = new ArrayList<Double>();
		
		double[][] opPBVs = null;
		
		for (int i = 0; i < 10; i ++) {
			long then = System.nanoTime();
			
			opPBVs = OP.factoredExpectationSparseNoMem(fBeliefs, solver.alphaVectors);
//			double[][] PBVs = OP.dotProduct(beliefsArray, SP.alphaVectors, vars);
			
			long now = System.nanoTime();
			
			times.add((double) (now - then) / 1000000);
		}
		
		LOGGER.debug("Factoring took " + times.stream().mapToDouble(a -> a).average().getAsDouble() 
				+ " msec");
		
		List<Double> times2 = new ArrayList<Double>();
		double[][] dotPBVs = null;
		for (int i = 0; i < 10; i ++) {
			long then = System.nanoTime();
			
//			double[] PBVs = OP.factoredExpectationSparseNoMem(beliefsArray, SP.alphaVectors);
			dotPBVs = OP.dotProduct(beliefsArray, solver.alphaVectors, vars);
			
			long now = System.nanoTime();
			
			times2.add((double) (now - then) / 1000000);
		}
		
		LOGGER.debug("Dot took " + times2.stream().mapToDouble(a -> a).average().getAsDouble() 
				+ " msec");
		
		LOGGER.debug("Checking correctness");
		
		for (int i = 0; i < dotPBVs.length; i++) {
			for (int j = 0; j < dotPBVs[i].length; j++) {
				double diff = Math.abs(opPBVs[i][j] - dotPBVs[i][j]);
				LOGGER.debug("Diff is " + diff);
				assertTrue(diff < 1e-4);
			}
		}
		
	}

	@Test
	void testNZPrimeComputation() throws Exception {
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/deception.5S.2O.L1.2F.domain");
		
		
		parser.parseDomain();
		
		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
		
//		DD[] belief = ipomdp.factorBelief(ipomdp.getCurrentBelief());
		
		List<Double> times = new ArrayList<Double>();
		
		for (int i = 0; i < 10; i ++) {
			long then = System.nanoTime();
			
			HashMap<String, NextBelState> a = 
					NextBelState.oneStepNZPrimeBelStates(
							ipomdp, 
							ipomdp.getCurrentBelief(), false, 1e-8);
			
			long now = System.nanoTime();
			
			times.add((double) (now - then) / 1000000);
		}
		
		LOGGER.debug("That took " + times.stream().mapToDouble(a -> a).average().getAsDouble() 
				+ " msec");
		
	}
	
	@Test
	void testoneStepNextBelStates2() throws Exception {
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/deception.5S.2O.L1.2F.domain");
		
		
		parser.parseDomain();
		
		LOGGER.info("Checking nextBelStates2 computation");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);
		DD[] belief = ipomdp.factorBelief(ipomdp.getCurrentBelief());
		
		long then1 = System.nanoTime();
		/* compute real NextBelStates */
		HashMap<String, NextBelState> a = 
				NextBelState.oneStepNZPrimeBelStates(
						ipomdp, 
						belief, false, 1e-8);
		long now1 = System.nanoTime();
		LOGGER.debug("Original implementation took " + (now1 - then1) / 1000000 + " msecs.");
		
		long then2 = System.nanoTime();
		/* compute efficiently */
		HashMap<String, NextBelState> b = 
				NextBelState.oneStepNZPrimeBelStates2(
						ipomdp, 
						ipomdp.getCurrentBelief(), false, 1e-8);
		long now2 = System.nanoTime();
		LOGGER.debug("New implementation took " + (now2 - then2) / 1000000 + " msecs.");
		
		/* check results */
		LOGGER.debug("Checking for correctness");
		
		for (String act: a.keySet()) {
			
			NextBelState aNZ = a.get(act);
			NextBelState bNZ = b.get(act);
			
			for (int n = 0; n < aNZ.nextBelStates.length; n++) {
				for (int s = 0; s < aNZ.nextBelStates[n].length; s++) {
					
					double diff = 
							OP.maxAll(
									OP.abs(
											OP.sub(
													aNZ.nextBelStates[n][s], 
													bNZ.nextBelStates[n][s])));

					LOGGER.debug("Diff is: " + diff);
					assertTrue(diff < 1e-4);
				}
			}
		}
		
	}
	
	@Test
	void testoneStepNextBelStatesForPOMDPs() throws Exception {
		
//		POMDP pomdp = 
//				new POMDP(
//						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/"
//						+ "exfil.6S.L0.obs_deception.domain");
		POMDP pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");
		
		LOGGER.info("Checking nextBelStates2 computation");
		
		
		DD[] belief = pomdp.factorBelief(pomdp.getCurrentBelief());
		
		long then1 = System.nanoTime();
		
		/* compute real NextBelStates */
		NextBelState[] a = 
				NextBelState.oneStepNZPrimeBelStates(
						pomdp, 
						belief, false, 1e-8);
		
		long now1 = System.nanoTime();
		LOGGER.debug("Original implementation took " + (now1 - then1) / 1000000 + " msecs.");
		
		long then2 = System.nanoTime();
		
		/* compute efficiently */
		HashMap<String, NextBelState> b = 
				NextBelState.oneStepNZPrimeBelStates(
						pomdp, 
						pomdp.getCurrentBelief(), false, 1e-8);
		
		long now2 = System.nanoTime();
		
		LOGGER.debug("New implementation took " + (now2 - then2) / 1000000 + " msecs.");
		
		/* check results */
		LOGGER.debug("Checking for correctness");
		
		for (int i = 0;i < a.length; i++) {
			
			NextBelState aNZ = a[i];
			NextBelState bNZ = b.get(pomdp.getActions().get(i));
			
			for (int n = 0; n < aNZ.nextBelStates.length; n++) {
				for (int s = 0; s < aNZ.nextBelStates[n].length; s++) {
					
					double diff = 
							OP.maxAll(
									OP.abs(
											OP.sub(
													aNZ.nextBelStates[n][s], 
													bNZ.nextBelStates[n][s])));

					LOGGER.debug("Diff is: " + diff);
					assertTrue(diff < 1e-4);
				}
			}
		}
		
	}
	
	@Test
	void testManualNZPrimeComputation() throws Exception {
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/deception.5S.2O.L1.2F.domain");
		
		
		parser.parseDomain();
		
		LOGGER.info("Running manual computations");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
		
		List<Double> times = new ArrayList<Double>();
		
		HashMap<String, DD[][]> nextBelStatesManual = new HashMap<String, DD[][]>(); 
		
		for (int i = 0; i < 10; i ++) {
			long then = System.nanoTime();
			
			for (String act: ipomdp.getActions()) {
				
				List<DD[]> nextBelStatesForAct = new ArrayList<DD[]>();
				
				List<List<String>> allObs = ipomdp.getAllPossibleObservations();
				DD obsDist = ipomdp.getObsDist(ipomdp.getCurrentBelief(), act);
				double[] obsProbs = OP.convert2array(obsDist, ipomdp.obsIVarPrimeIndices);
				
				for (int o = 0; o < allObs.size(); o++) {
					
					DD nextBelief = 
							ipomdp.beliefUpdate(
									ipomdp.getCurrentBelief(), 
									act, 
									allObs.get(o).stream().toArray(String[]::new));
					
					DD[] factoredNextBel = ipomdp.factorBelief(nextBelief);
					factoredNextBel = 
							OP.primeVarsN(factoredNextBel, ipomdp.S.size() + ipomdp.Omega.size());
					
					factoredNextBel = 
							ArrayUtils.add(
									factoredNextBel, 
									DDleaf.myNew(obsProbs[o]));
					
					nextBelStatesForAct.add(factoredNextBel);
				}
				
				nextBelStatesManual.put(act, nextBelStatesForAct.stream().toArray(DD[][]::new));
			}
			
			long now = System.nanoTime();
			
			times.add((double) (now - then) / 1000000);
		}
		
		LOGGER.debug("That took " + times.stream().mapToDouble(a -> a).average().getAsDouble() 
				+ " msec");
		
		/* compute real NextBelStates */
		HashMap<String, NextBelState> a = 
				NextBelState.oneStepNZPrimeBelStates(
						ipomdp, 
						ipomdp.getCurrentBelief(), false, 1e-8);
		
		LOGGER.debug("Checking for correctness");
		
		for (String act: a.keySet()) {
			
			NextBelState aNZ = a.get(act);
			DD[][] bNZ = nextBelStatesManual.get(act);
			
			for (int n = 0; n < aNZ.nextBelStates.length; n++) {
				for (int s = 0; s < aNZ.nextBelStates[n].length; s++) {
					
					double diff = OP.maxAll(OP.abs(OP.sub(aNZ.nextBelStates[n][s], bNZ[n][s])));
					LOGGER.debug("Diff is: " + diff);
					assertTrue(diff < 1e-4);
				}
			}
		}
	}
	
	@Test
	void testNZPrimeSerialization() throws Exception {
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/deception.5S.2O.L1.2F.domain");
		
		
		parser.parseDomain();
		
		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
		
		HashMap<String, NextBelState> a = 
				NextBelState.oneStepNZPrimeBelStates(
						ipomdp, 
						ipomdp.getCurrentBelief(), false, 1e-8);
		
		List<Double> times = new ArrayList<Double>();
		
		for (int i = 0; i < 10; i ++) {
			long then = System.nanoTime();
			
			FileOutputStream fStream = new FileOutputStream("test.obj");
			ObjectOutputStream oStream = new ObjectOutputStream(fStream);
			
			
			oStream.writeObject(a);
			
			fStream.close();
			oStream.close();
			
			long now = System.nanoTime();
			
			times.add((double) (now - then) / 1000000);
		}
		
		LOGGER.debug("That took " + times.stream().mapToDouble(b -> b).average().getAsDouble() 
				+ " msec");
	}
	
	
	@Test
	void testNZPrimeSerializationRead() throws Exception {
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/deception.5S.2O.L1.2F.domain");
		
		
		parser.parseDomain();
		
		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
		
		HashMap<String, NextBelState> a = 
				NextBelState.oneStepNZPrimeBelStates(
						ipomdp, 
						ipomdp.getCurrentBelief(), false, 1e-8);
		
		FileOutputStream fStream = new FileOutputStream("test.obj");
		ObjectOutputStream oStream = new ObjectOutputStream(fStream);
		
		oStream.writeObject(a);
		
		fStream.close();
		oStream.close();
		
		List<Double> times = new ArrayList<Double>();
		
		for (int i = 0; i < 10; i ++) {
			long then = System.nanoTime();
			
			FileInputStream fInStream = new FileInputStream("test.obj");
			ObjectInputStream oInStream = new ObjectInputStream(fInStream);
			
			HashMap<String, NextBelState> o = 
					(HashMap<String, NextBelState>) oInStream.readObject();
			
			fInStream.close();
			oInStream.close();
			
			long now = System.nanoTime();
			
			times.add((double) (now - then) / 1000000);
			
			for (String act: o.keySet())
				LOGGER.debug(act + " " + o.get(act));
		}
		
		LOGGER.debug("That took " + times.stream().mapToDouble(b -> b).average().getAsDouble() 
				+ " msec");
	}
	
	@Test
	void testNZPrimeCacheDBStorage() throws Exception {
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/deception.5S.2O.L1.2F.domain");
		
		
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
		
		HashMap<String, NextBelState> a = 
				NextBelState.oneStepNZPrimeBelStates(
						ipomdp, 
						ipomdp.getCurrentBelief(), false, 1e-8);
		
		CacheDB db = new CacheDB();
		db.insertNewNZPrime(1, a);
		
		List<Double> times = new ArrayList<Double>();
		
		for (int i = 0; i < 10; i ++) {
			long then = System.nanoTime();
			
			HashMap<String, NextBelState> b = db.getNZPrime(1);
			
			long now = System.nanoTime();
			
			times.add((double) (now - then) / 1000000);
			
			for (String act: b.keySet()) {
				NextBelState an = a.get(act);
				NextBelState bn = b.get(act);
				
				for (int t = 0; t < an.nextBelStates.length; t++) {
					for (int s = 0; s < an.nextBelStates[0].length; s++) {
						assertTrue(an.nextBelStates[t][s].equals(bn.nextBelStates[t][s]));
					}
				}
			}
			
		}
		
		LOGGER.debug("That took " + times.stream().mapToDouble(b -> b).average().getAsDouble() 
				+ " msec");
	}
	
	@Test
	void testNZPrimeCacheDBClear() throws Exception {
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/deception.5S.2O.L1.2F.domain");
		
		
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
		
		HashMap<String, NextBelState> a = 
				NextBelState.oneStepNZPrimeBelStates(
						ipomdp, 
						ipomdp.getCurrentBelief(), false, 1e-8);
		
		CacheDB db = new CacheDB();
		db.insertNewNZPrime(1, a);
		db.insertNewNZPrime(2, a);
		db.insertNewNZPrime(3, a);
		
		LOGGER.debug("Added 3 entries");
		
		ResultSet res = db.getTable();
		
		while(res.next()) {
			LOGGER.debug("id: " + res.getInt("belief_id") + " data: " + res.getBytes("nz_prime"));
		}
		
		db.clearDB();
		
		LOGGER.debug("Deleted entries");
		
		res = db.getTable();
		
		while(res.next()) {
			LOGGER.debug("id: " + res.getInt("belief_id") + " data: " + res.getBytes("nz_prime"));
		}
		
	}
	
	
	@Test
	void testNZPrimeCacheDBStorageForExploredBeliefs() throws Exception {
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/deception.5S.2O.L1.2F.domain");
		
		
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);
		
		NextBelStateCache.useCache();
//		NextBelStateCache.setDB("/tmp/nz_cache.db");
		
		SparseFullBeliefExpansion BE = new SparseFullBeliefExpansion(ipomdp, 1);
		BE.expand();
		
		List<DD> beliefs = BE.getBeliefPoints();
		
		NextBelStateCache.populateCache(ipomdp, beliefs);
		NextBelStateCache.showTable();
		
		for (DD bel: beliefs) {
			
			LOGGER.debug("Checking " + ipomdp.toMapWithTheta(bel));
			
			HashMap<String, NextBelState> a = 
					NextBelState.oneStepNZPrimeBelStates(
							ipomdp, 
							bel, false, 1e-8);
			
			HashMap<String, NextBelState> b = NextBelStateCache.getCachedEntry(bel);
			
			for (String act: a.keySet()) {
				
				NextBelState aNZ = a.get(act);
				NextBelState bNZ = b.get(act);
				
				for (int n = 0; n < aNZ.nextBelStates.length; n++) {
					for (int s = 0; s < aNZ.nextBelStates[n].length; s++) {
						assertTrue(aNZ.nextBelStates[n][s].equals(bNZ.nextBelStates[n][s]));
					}
				}
			}
		}
	}
	
	@Test
	void testCachedStepping() throws Exception {
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/UGA/THINCLab/DomainFiles/"
//						+ "final_domains/deception.5S.2O.L1.2F.domain");
		
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10, true);
		
		SparseFullBeliefExpansion BE = new SparseFullBeliefExpansion(ipomdp, 1);
		BE.expand();
		
		List<DD> beliefs = BE.getBeliefPoints();
		
		NextBelStateCache.useCache();
//		NextBelStateCache.setDB("/tmp/nz_cache.db");
		
		for (DD bel: beliefs) {
			
			LOGGER.debug("Checking " + ipomdp.toMapWithTheta(bel));
			long free = Runtime.getRuntime().freeMemory();
			long total = Runtime.getRuntime().totalMemory();
			LOGGER.debug("Mem is: " + (total - free) / 1000000 + "MB");
			
			long thenC = System.nanoTime();
			
			HashMap<String, NextBelState> a = 
					NextBelState.oneStepNZPrimeBelStatesCached(
							ipomdp, 
							bel, false, 1e-8);
			
			long nowC = System.nanoTime();
			
			LOGGER.debug("Computing took " + (nowC - thenC) / 1000000 + " msecs");
			
			long thenR = System.nanoTime();
			HashMap<String, NextBelState> b = 
					NextBelState.oneStepNZPrimeBelStatesCached(
							ipomdp, 
							bel, false, 1e-8);
			
			long nowR = System.nanoTime();
			LOGGER.debug("Cache fetching took " + (nowR - thenR) / 1000000 + " msecs");
			
			LOGGER.debug("Checking for correctness");
			
			for (String act: a.keySet()) {
				
				NextBelState aNZ = a.get(act);
				NextBelState bNZ = b.get(act);
				
				for (int n = 0; n < aNZ.nextBelStates.length; n++) {
					for (int s = 0; s < aNZ.nextBelStates[n].length; s++) {
						assertTrue(aNZ.nextBelStates[n][s].equals(bNZ.nextBelStates[n][s]));
					}
				}
			}
			
			LOGGER.debug("Correct!");
			
			LOGGER.debug("Checking with older result");
			
			HashMap<String, NextBelState> c = 
					NextBelState.oneStepNZPrimeBelStates(
							ipomdp, 
							bel, false, 1e-8);
			
//			b = 
//					NextBelState.oneStepNZPrimeBelStates2(
//							ipomdp, 
//							bel, false, 1e-8);
			
			for (String act: c.keySet()) {
				
				NextBelState cNZ = c.get(act);
				NextBelState bNZ = b.get(act);
				
				for (int n = 0; n < cNZ.nextBelStates.length; n++) {
					for (int s = 0; s < cNZ.nextBelStates[n].length; s++) {
						double diff = 
								OP.maxAll(
										OP.abs(
												OP.sub(
														cNZ.nextBelStates[n][s], 
														bNZ.nextBelStates[n][s])));
						
//						LOGGER.debug(OP.sub(
//														cNZ.nextBelStates[n][s], 
//														bNZ.nextBelStates[n][s]));
//						
//						LOGGER.debug("Checking");
//						LOGGER.debug(cNZ.nextBelStates[n][s].toDDTree());
//						LOGGER.debug(bNZ.nextBelStates[n][s].toDDTree());
						
						LOGGER.debug("Diff is: " + diff);
						assertTrue(diff < 0.001);
					}
				}
			}
			
			LOGGER.debug("Correct!");
		}
		
		NextBelStateCache.clearCache();
	}
	
	@Test
	void testCachedSteppingForPOMDPs() throws Exception {
		
//		POMDP pomdp = new POMDP("/home/adityas/UGA/THINCLab/DomainFiles/final_domains/"
//				+ "exfil.6S.L0.obs_deception.domain");
		
		POMDP pomdp = new POMDP("/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt");
		
		FullBeliefExpansion BE = new FullBeliefExpansion(pomdp, 3);
		BE.expand();
		
		List<DD> beliefs = BE.getBeliefPoints();
		
		NextBelStateCache.useCache();
//		NextBelStateCache.setDB("/tmp/nz_cache.db");
		
		for (DD bel: beliefs) {
			
			LOGGER.debug("Checking " + pomdp.toMap(bel));
			long free = Runtime.getRuntime().freeMemory();
			long total = Runtime.getRuntime().totalMemory();
			LOGGER.debug("Mem is: " + (total - free) / 1000000 + "MB");
			
			long thenC = System.nanoTime();
			
			HashMap<String, NextBelState> a = 
					NextBelState.oneStepNZPrimeBelStatesCached(
							pomdp, 
							bel, false, 1e-8);
			
			long nowC = System.nanoTime();
			
			LOGGER.debug("Computing took " + (nowC - thenC) / 1000000 + " msecs");
			
			long thenR = System.nanoTime();
			HashMap<String, NextBelState> b = 
					NextBelState.oneStepNZPrimeBelStatesCached(
							pomdp, 
							bel, false, 1e-8);
			
			long nowR = System.nanoTime();
			LOGGER.debug("Cache fetching took " + (nowR - thenR) / 1000000 + " msecs");
			
			LOGGER.debug("Checking for correctness");
			
			for (String act: a.keySet()) {
				
				NextBelState aNZ = a.get(act);
				NextBelState bNZ = b.get(act);
				
				for (int n = 0; n < aNZ.nextBelStates.length; n++) {
					for (int s = 0; s < aNZ.nextBelStates[n].length; s++) {
						assertTrue(aNZ.nextBelStates[n][s].equals(bNZ.nextBelStates[n][s]));
					}
				}
			}
			
			LOGGER.debug("Correct!");
			
			LOGGER.debug("Checking with older result");
			
			HashMap<String, NextBelState> c = 
					NextBelState.oneStepNZPrimeBelStates(
							pomdp, 
							bel, false, 1e-8);
			
//			b = 
//					NextBelState.oneStepNZPrimeBelStates2(
//							ipomdp, 
//							bel, false, 1e-8);
			
			for (String act: c.keySet()) {
				
				NextBelState cNZ = c.get(act);
				NextBelState bNZ = b.get(act);
				
				for (int n = 0; n < cNZ.nextBelStates.length; n++) {
					for (int s = 0; s < cNZ.nextBelStates[n].length; s++) {
						double diff = 
								OP.maxAll(
										OP.abs(
												OP.sub(
														cNZ.nextBelStates[n][s], 
														bNZ.nextBelStates[n][s])));
						
//						LOGGER.debug(OP.sub(
//														cNZ.nextBelStates[n][s], 
//														bNZ.nextBelStates[n][s]));
//						
//						LOGGER.debug("Checking");
//						LOGGER.debug(cNZ.nextBelStates[n][s].toDDTree());
//						LOGGER.debug(bNZ.nextBelStates[n][s].toDDTree());
						
						LOGGER.debug("Diff is: " + diff);
						assertTrue(diff < 0.001);
					}
				}
			}
			
			LOGGER.debug("Correct!");
		}
		
		NextBelStateCache.clearCache();
	}

}
