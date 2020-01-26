/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.decisionprocesses.IPOMDP;
import thinclab.legacy.DD;
import thinclab.legacy.NextBelState;

/*
 * @author adityas
 *
 */
public class NextBelStateCache {
	
	/*
	 * Maintains caches for precomputed NextBelState objects
	 */
	
	private static boolean USE_CACHE = false;
	private static String USE_DB = "";
	private static CacheDB DB = null;
	private static int CACHE_LIMIT = 50;
	
	private static HashMap<DD, HashMap<String, NextBelState>> NEXT_BELSTATE_CACHE =
			new HashMap<DD, HashMap<String, NextBelState>>();
	
	private static HashMap<DD, Integer> BELIEF_ID_MAP = new HashMap<DD, Integer>();
	
	private static Logger LOGGER = Logger.getLogger(NextBelStateCache.class);
	
	// -------------------------------------------------------------------------------------
	
	public static void cacheNZPrime(DD belief, HashMap<String, NextBelState> nzPrime) {
		
		if (!NextBelStateCache.BELIEF_ID_MAP.containsKey(belief)) {
			NextBelStateCache.BELIEF_ID_MAP.put(
					belief, 
					NextBelStateCache.BELIEF_ID_MAP.size());
		}
		
		int belief_id = NextBelStateCache.BELIEF_ID_MAP.get(belief);
		NextBelStateCache.DB.insertNewNZPrime(belief_id, nzPrime);
		
	}
	
	public static void populateCache(IPOMDP ipomdp, List<DD> exploredBeliefs) {
		/*
		 * Computes and caches NextBelState objects for all given objects
		 */
		
		LOGGER.info("Precomputing NextBelStates for " + exploredBeliefs.size() + " beliefs");
		
		if (NextBelStateCache.DB != null)
			LOGGER.debug("Using SQLite DB to store NZ primes");
		
		for (DD belief: exploredBeliefs) {
			
			try {
				
				/* if using external storage */
				if (NextBelStateCache.DB != null) {
					
					/* compute NextBelState */
					HashMap<String, NextBelState> computedState = 
							NextBelState.oneStepNZPrimeBelStates(
									ipomdp, belief, true, 1e-8);
					
					NextBelStateCache.cacheNZPrime(belief, computedState);
					
					if (NextBelStateCache.BELIEF_ID_MAP.size() % 5 == 0)
						LOGGER.debug("Cached " + NextBelStateCache.BELIEF_ID_MAP.size() +
								" NZ primes in DB");
			
					
				}
				
				/* if using RAM */
				else {
					if (NextBelStateCache.NEXT_BELSTATE_CACHE.containsKey(belief)) 
						continue;
					
					/* compute NextBelState */
					HashMap<String, NextBelState> computedState = 
							NextBelState.oneStepNZPrimeBelStates(
									ipomdp, belief, true, 1e-8);
					
					if (NextBelStateCache.NEXT_BELSTATE_CACHE.size() 
							>= NextBelStateCache.CACHE_LIMIT) {
						
						/* remove an entry cache limit crossed */
						DD toRemove = 
								new ArrayList<DD>(
										NextBelStateCache.NEXT_BELSTATE_CACHE.keySet()).get(0);
						
						HashMap<String, NextBelState>  a = 
								NextBelStateCache.NEXT_BELSTATE_CACHE.remove(toRemove);
						
						a.clear();
						a = null;
						
						System.gc();
					}
						
					NextBelStateCache.NEXT_BELSTATE_CACHE.put(belief, computedState);
					
					if (NextBelStateCache.NEXT_BELSTATE_CACHE.size() % 5 == 0)
						LOGGER.debug("Computed NextBelStates for " + 
								NextBelStateCache.NEXT_BELSTATE_CACHE.size() + " beliefs");
				}
			} 
			
			catch (Exception e) {
				LOGGER.error("while caching next bel states");
				e.printStackTrace();
			}
		}
		
	}
	
	public static void setDB(String filename) {
		LOGGER.info("Setting NextBelStateCache DB to " + filename);
		NextBelStateCache.USE_DB = filename;
		NextBelStateCache.DB = new CacheDB(NextBelStateCache.USE_DB);
	}
	
	public static void clearCache() {
		LOGGER.info("Clearing NextBelState Cache");
		NextBelStateCache.NEXT_BELSTATE_CACHE.clear();
		
		if (NextBelStateCache.DB != null) {
			NextBelStateCache.DB.clearDB();
			NextBelStateCache.BELIEF_ID_MAP.clear();
		}
	}
	
	public static void useCache() {
		NextBelStateCache.USE_CACHE = true;
	}
	
	public static boolean cachingAllowed() {
		return NextBelStateCache.USE_CACHE;
	}
	
	public static HashMap<String, NextBelState> getCachedEntry(DD belief) {
		
		if (NextBelStateCache.DB != null) {
			
			if (!NextBelStateCache.BELIEF_ID_MAP.containsKey(belief))
				return null;
			
			int belief_id = NextBelStateCache.BELIEF_ID_MAP.get(belief);
			
			/* if not in DB return null */
			if (!NextBelStateCache.BELIEF_ID_MAP.containsKey(belief)) {
				LOGGER.error("DB does not contain belief.");
				return null;
			}
			
			return NextBelStateCache.DB.getNZPrime(belief_id);
		}
		
		if (NextBelStateCache.NEXT_BELSTATE_CACHE.containsKey(belief))
			return NextBelStateCache.NEXT_BELSTATE_CACHE.get(belief);
		
		else return null;
	}
	
	public static void showTable() {
		
		/*
		 * Logs the table entries to terminal
		 */
		
		ResultSet res = NextBelStateCache.DB.getTable();
		
		try {
			while(res.next()) {
				LOGGER.debug("id: " + res.getInt("belief_id") 
					+ " data: " + res.getBytes("nz_prime"));
			}
		} 
		
		catch (SQLException e) {
			LOGGER.error("While showing table.");
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
