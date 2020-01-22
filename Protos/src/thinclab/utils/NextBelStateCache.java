/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils;

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
	
	public static HashMap<DD, HashMap<String, NextBelState>> NEXT_BELSTATE_CACHE =
			new HashMap<DD, HashMap<String, NextBelState>>();
	
	private static Logger LOGGER = Logger.getLogger(NextBelStateCache.class);
	
	// -------------------------------------------------------------------------------------
	
	public static void populateCache(IPOMDP ipomdp, List<DD> exploredBeliefs) {
		/*
		 * Computes and caches NextBelState objects for all given objects
		 */
		
		LOGGER.info("Precomputing NextBelStates for " + exploredBeliefs.size() + " beliefs");
		
		for (DD belief: exploredBeliefs) {
			
			try {
				HashMap<String, NextBelState> computedState = 
						NextBelState.oneStepNZPrimeBelStates(
								ipomdp, belief, true, 1e-8);
				
				NextBelStateCache.NEXT_BELSTATE_CACHE.put(belief, computedState);
				
				if (NextBelStateCache.NEXT_BELSTATE_CACHE.size() % 5 == 0)
					LOGGER.debug("Computed NextBelStates for " + 
							NextBelStateCache.NEXT_BELSTATE_CACHE.size() + " beliefs");
			} 
			
			catch (Exception e) {
				LOGGER.error("while caching next bel states");
				e.printStackTrace();
			}
		}
		
	}
	
	public static void clearCache() {
		LOGGER.info("Clearing NextBelState Cache");
		NextBelStateCache.NEXT_BELSTATE_CACHE.clear();
	}
	
	public static void useCache() {
		NextBelStateCache.USE_CACHE = true;
	}
	
	public static boolean cachingAllowed() {
		return NextBelStateCache.USE_CACHE;
	}

}
