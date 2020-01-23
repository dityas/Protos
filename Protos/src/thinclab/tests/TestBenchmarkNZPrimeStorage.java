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
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.spi.DirStateFactory.Result;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.decisionprocesses.IPOMDP;
import thinclab.legacy.NextBelState;
import thinclab.parsers.IPOMDPParser;
import thinclab.utils.CacheDB;
import thinclab.utils.CustomConfigurationFactory;

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
		LOGGER = Logger.getLogger(TestIPOMDP.class);
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testNZPrimeComputation() throws Exception {
		
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/cybersec.5S.2O.L1.2F.domain");
		
		
		parser.parseDomain();
		
		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10);
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
//		ipomdp.step(ipomdp.getCurrentBelief(), "listen", new String[] {"growl-left", "silence"});
		
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
	void testNZPrimeSerialization() throws Exception {
		
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/cybersec.5S.2O.L1.2F.domain");
		
		
		parser.parseDomain();
		
		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10);
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
		
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/cybersec.5S.2O.L1.2F.domain");
		
		
		parser.parseDomain();
		
		LOGGER.info("Calling empty constructor");
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10);
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
		
//		IPOMDPParser parser = 
//				new IPOMDPParser(
//						"/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt");
		
		IPOMDPParser parser = 
				new IPOMDPParser(
						"/home/adityas/UGA/THINCLab/DomainFiles/final_domains/cybersec.5S.2O.L1.2F.domain");
		
		
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 3, 10);
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
			
		}
		
		LOGGER.debug("That took " + times.stream().mapToDouble(b -> b).average().getAsDouble() 
				+ " msec");
	}

}
