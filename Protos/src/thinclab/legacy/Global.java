package thinclab.legacy;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.RandomVariable;

import java.lang.ref.*;
import java.sql.Connection;

public class Global {

	public static String storagDir = null;
	public static boolean showProgressBar = false;

	public static List<Integer> varDomSize = new ArrayList<>(10);
	public static List<String> varNames = new ArrayList<>(10);
	public static List<List<String>> valNames = new ArrayList<>(10);
	
	public static int NUM_VARS = 0;

	/* identify which frame and level has the current context */
	public static int CONTEXT_LEVEL_ID;
	public static int CONTEXT_FRAME_ID;

	// hash tables
	public static TypedCacheMap<DD, WeakReference<DD>> leafHashtable = new TypedCacheMap<>();
	public static TypedCacheMap<DD, WeakReference<DD>> nodeHashtable = new TypedCacheMap<>();
	public static TypedCacheMap<Pair, DD> addHashtable = new TypedCacheMap<Pair, DD>();
	public static TypedCacheMap<Pair, DD> multHashtable = new TypedCacheMap<Pair, DD>();
	public static CacheMap maxHashtable = new CacheMap();
	public static CacheMap minHashtable = new CacheMap();
	public static CacheMap dotProductHashtable = new CacheMap();
	public static CacheMap nEdgesHashtable = new CacheMap();
	public static CacheMap nLeavesHashtable = new CacheMap();
	public static CacheMap nNodesHashtable = new CacheMap();

	/* Caches for optimizing NZ prime computations */
	public static boolean USE_NEXT_BELSTATE_CACHES = false;

	public static HashMap<DD, HashMap<String, DD[][]>> NEXT_BELSTATES_CACHE = new HashMap<DD, HashMap<String, DD[][]>>();

	public static HashMap<DD, HashMap<String, double[]>> OBS_PROB_CACHE = new HashMap<DD, HashMap<String, double[]>>();

	// random number generator
	public static Random random = new Random();

	// --------------------------------------------------------------------------
	/*
	 * Cache DB stuff
	 */

	public static HashMap<Integer, Connection> MjDBConnections = new HashMap<Integer, Connection>();

	// --------------------------------------------------------------------------

	private static final Logger LOGGER = LogManager.getLogger(Global.class);

	public static void addVariable(String varName, List<String> valNames) {

		Global.varNames.add(varName);
		Global.valNames.add(valNames);
		Global.varDomSize.add(valNames.size());
		
		LOGGER.debug(String.format("Add var %s with values %s", varName, valNames));
		Global.NUM_VARS += 1;
	}
	
	public static void populateFromRandomVariables(List<RandomVariable> vars) {
		
		vars.stream()
			.forEach(v -> Global.addVariable(
						v.getVarName(), 
						v.getValNames()));
	}

	public static void setSeed(long seed) {
		random.setSeed(seed);
	}
	
	public static void primeVarsAndInitGlobals(List<RandomVariable> vars) {
		
		var primedVars = RandomVariable.primeVariables(vars);
		Global.populateFromRandomVariables(primedVars);
	}

	public static void clearHashtables() {

		LOGGER.warn("Clearing caches. This will reduce performance");

		Global.leafHashtable.clear();
		Global.nodeHashtable.clear();
		Global.addHashtable.clear();
		Global.multHashtable.clear();
		Global.maxHashtable.clear();
		Global.minHashtable.clear();
		Global.dotProductHashtable.clear();
		Global.nEdgesHashtable.clear();
		Global.nLeavesHashtable.clear();
		Global.nNodesHashtable.clear();
		Global.leafHashtable.put(DD.zero, new WeakReference<DD>(DD.zero));
		Global.leafHashtable.put(DD.one, new WeakReference<DD>(DD.one));

		System.gc();
	}
	
	public static void clearAll() {
		
		Global.varNames.clear();
		Global.valNames.clear();
		Global.varDomSize.clear();
		
		Global.clearHashtables();
	}

	public static void newHashtables() {
		// Global.leafHashtable = new WeakHashMap();
		// Global.nodeHashtable = new WeakHashMap();

		LOGGER.warn("Clearing caches. This will reduce performance");

		Global.leafHashtable = new TypedCacheMap<>();
		Global.nodeHashtable = new TypedCacheMap<>();
		Global.addHashtable = new TypedCacheMap<Pair, DD>();
		Global.multHashtable = new TypedCacheMap<Pair, DD>();
		Global.maxHashtable = new CacheMap();
		Global.minHashtable = new CacheMap();
		Global.dotProductHashtable = new CacheMap();
		Global.nEdgesHashtable = new CacheMap();
		Global.nLeavesHashtable = new CacheMap();
		Global.nNodesHashtable = new CacheMap();
		Global.leafHashtable.put(DD.zero, new WeakReference<DD>(DD.zero));
		Global.leafHashtable.put(DD.one, new WeakReference<DD>(DD.one));
	}

	public static void printProgressBar(int currentStep, int totalSteps, int totalRounds) {
		/*
		 * Build a progress bar to show progress for backups and all that
		 */
		int numBars = 50;
		String bar = "";
		int singleBlockSteps = totalSteps / numBars;
		int bars = currentStep / singleBlockSteps;

		for (int i = 0; i < numBars; i++) {

			if (i < bars)
				bar += "*";
			else
				bar += " ";
		}

		System.out
				.print("\rProgress: |" + bar + "|" + singleBlockSteps * bars + "%, Max. rounds: " + totalRounds + " ");
		if (bars == numBars)
			System.out.println();
	}

	public static void printProgressBarConvergence() {
		System.out.println("\rProgress: 100% Converged. Solving next round.\t\t\t\t\t\t\t\t\t");
	}
}
