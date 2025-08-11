/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.legacy.Global;

/*
 * @author adityas
 *
 */
public class Diagnostics {
	
	/*
	 * Contains arrays to record execution times of different operations
	 * 
	 * The arrays have to manually averaged and cleared after every report
	 */
	
	/* record exec time for backup operations */
	public static List<Long> BACKUP_TIME = new ArrayList<Long>();
	
	/* exec time for NZ prime bel states */
	public static List<Long> NZ_PRIME_TIME = new ArrayList<Long>();
	
	/* for obs strat and immediate reward */
	public static List<Long> IMMEDIATE_R_TIME = new ArrayList<Long>();
	
	/* for constructing alpha vector */
	public static List<Long> AVEC_TIME = new ArrayList<Long>();
	
	/* for computing next value function */
	public static List<Long> NEXT_VAL_FN_TIME = new ArrayList<Long>();
	
	/* for counting Cache hits */
	public static int CACHE_HITS = 0;
	
	private static final Logger LOGGER = LogManager.getLogger(Diagnostics.class);
	
	// --------------------------------------------------------------------------------------
	
	public static void reportDiagnostics() {
		/*
		 * Averages all arrays and report exec times for each
		 */
		
		/* for backup time */
		double avgBackupTime = 
				Diagnostics.BACKUP_TIME.stream()
					.map(i -> (double) i)
					.mapToDouble(Double::valueOf)
					.average()
					.orElse(Double.NaN);
		
		Diagnostics.BACKUP_TIME.clear();
		
		/* for immediate R */
		double avgRTime = 
				Diagnostics.IMMEDIATE_R_TIME.stream()
					.map(i -> (double) i)
					.mapToDouble(Double::valueOf)
					.average()
					.orElse(Double.NaN);
		
		Diagnostics.IMMEDIATE_R_TIME.clear();
		
		/* for nz prime time */
		double avgNZPrimeTime = 
				Diagnostics.NZ_PRIME_TIME.stream()
					.map(i -> (double) i)
					.mapToDouble(Double::valueOf)
					.average()
					.orElse(Double.NaN);
		
		Diagnostics.NZ_PRIME_TIME.clear();
		
		/* for A VEC construction time */
		double avgAVecTime = 
				Diagnostics.AVEC_TIME.stream()
					.map(i -> (double) i)
					.mapToDouble(Double::valueOf)
					.average()
					.orElse(Double.NaN);
		
		Diagnostics.AVEC_TIME.clear();
		Diagnostics.NEXT_VAL_FN_TIME.clear();
		
		LOGGER.debug("EXEC TIMES: "
				+ " BACKUP: " + String.format(Locale.US, "%.03f", (avgBackupTime / 1000000)) 
					+ " msec"
				+ " IMM_R: " + String.format(Locale.US, "%.03f", (avgRTime / 1000000)) 
					+ " msec"
				+ " NZ_PRIMES: " + String.format(Locale.US, "%.03f", (avgNZPrimeTime / 1000000)) 
					+ " msec"
				+ " A VEC CONST.: " + String.format(Locale.US, "%.03f", (avgAVecTime / 1000000)) 
					+ " msec"
				+ " CACHE HITS: " + Diagnostics.CACHE_HITS);
		
		Diagnostics.CACHE_HITS = 0;
	}
	
	public static void reportCacheSizes() {
		/*
		 * Logs the sizes of all global caches
		 */
		
		LOGGER.debug("Cache sizes: "
				+ " LEAF: " + Global.leafHashtable.size()
				+ " NODE: " + Global.nodeHashtable.size()
				+ " ADD: " + Global.addCache.size()
				+ " DOT: " + Global.dotProductCache.size()
				+ " MULT: " + Global.multCache.size());
	}
	
	public static void logMemConsumption(String mileStone) {
		
		long free = Runtime.getRuntime().freeMemory();
		long total = Runtime.getRuntime().totalMemory();
		
		LOGGER.debug(mileStone + " Mem used: " + (total - free) / 1000000 + " MB");
	}
}
