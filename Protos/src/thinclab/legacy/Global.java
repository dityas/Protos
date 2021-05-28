package thinclab.legacy;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.*;
import java.sql.Connection;

public class Global {

	public static String storagDir = null;
	public static boolean showProgressBar = false;

	public static int[] varDomSize = null;
	public static String[] varNames = null;
	public static String[][] valNames = null;

	/* identify which frame and level has the current context */
	public static int CONTEXT_LEVEL_ID;
	public static int CONTEXT_FRAME_ID;

	// hash tables
	public static CacheMap leafHashtable = new CacheMap();
	public static CacheMap nodeHashtable = new CacheMap();
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

	public static void main(String args[]) {

		/*
		 * HashMap config = new HashMap(); config.put(new Integer(1), new Integer(1));
		 * config.put(new Integer(4), new Integer(3)); int[][] config2 = new int[2][2];
		 * config2[0][0] = 1; config2[1][0] = 1; config2[0][1] = 4; config2[1][1] = 3;
		 * int[][] config3 = Config.clone(config2); config3 = Config.add(config3,2,1);
		 * config3 = Config.add(config3,2,1); int[][] config4 =
		 * Config.merge(config3,config2); System.out.println(config.hashCode());
		 * System.out.println(config.toString());
		 * System.out.println(Config.hashCode(config4));
		 * System.out.println(Config.toString(config2));
		 * System.out.println(Config.toString(config3));
		 * System.out.println(Config.toString(config4));
		 * System.out.println(Config.equals(config2,config3)); return;
		 */

		// int N = Integer.parseInt(args[0]);
		// int iter = Integer.parseInt(args[1]);
		int N = 100;
		int iter = 1;

		Random numberGenerator = new Random(10101);

		for (long j = 0; j < iter; j++) {

			DD dd1 = DDleaf.myNew(numberGenerator.nextDouble());
			for (int i = 1; i <= N; i++) {
				DD[] children = new DD[3];
				if (numberGenerator.nextInt(2) == 0) {
					children[0] = DDleaf.myNew(numberGenerator.nextDouble());
					children[1] = dd1;
					children[2] = dd1;
				} else {
					children[0] = dd1;
					children[1] = dd1;
					children[2] = DDleaf.myNew(numberGenerator.nextDouble());
				}
				dd1 = DDnode.myNew(i, children);
			}

			DD dd2 = DDleaf.myNew(numberGenerator.nextDouble());
			for (int i = 1; i <= N; i++) {
				DD[] children = new DD[3];
				if (numberGenerator.nextInt(2) == 0) {
					children[0] = DDleaf.myNew(numberGenerator.nextDouble());
					children[1] = dd2;
					children[2] = dd2;
				} else {
					children[0] = dd2;
					children[1] = dd2;
					children[2] = DDleaf.myNew(numberGenerator.nextDouble());
				}
				dd2 = DDnode.myNew(i, children);
			}

			DD[] ddArray = new DD[2];
			ddArray[0] = dd1;
			ddArray[1] = dd2;
			// for (int i=0; i<100; i++) {
			// ddArray[i] = dd1;
			// }

			int[] varSet = new int[N];
			int[][] config = new int[2][N];
			for (int i = 1; i <= N; i++) {
				varSet[i - 1] = i;
				config[0][i - 1] = i;
				config[1][i - 1] = i % 2 + 1;
			}

			// System.out.println("dd1.display");
			// dd1.display();
			Global.multHashtable.clear();
			Global.addHashtable.clear();
			Global.leafHashtable.clear();
			Global.nodeHashtable.clear();
			for (int k = 0; k < 10000; k++) {
				DD dd1r = OP.restrict(dd1, config);
				DD dd2r = OP.restrict(dd2, config);
			}
		}
		Global.dotProductHashtable.clear();
		Global.multHashtable.clear();
		Global.addHashtable.clear();
		Global.leafHashtable.clear();
		Global.nodeHashtable.clear();
		System.out.println("done");
	}

	public static void setVarDomSize(int[] newVarDomSize) {
		Global.varDomSize = newVarDomSize;
	}

	public static void setVarNames(String[] newVarNames) {
		Global.varNames = newVarNames;
	}

	public static void setSeed(long seed) {
		random.setSeed(seed);
	}

	public static void setValNames(int varId, String[] newValNames) {
		if (Global.valNames == null) {
			Global.valNames = new String[varId][];
			Global.valNames[varId - 1] = newValNames;
		} else if (Global.valNames.length < varId) {
			String[][] tempValNames = new String[varId][];
			for (int i = 0; i < Global.valNames.length; i++) {
				tempValNames[i] = Global.valNames[i];
			}
			tempValNames[varId - 1] = newValNames;
			Global.valNames = tempValNames;
		} else {
			Global.valNames[varId - 1] = newValNames;
		}
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

	public static void newHashtables() {
		// Global.leafHashtable = new WeakHashMap();
		// Global.nodeHashtable = new WeakHashMap();

		LOGGER.warn("Clearing caches. This will reduce performance");

		Global.leafHashtable = new CacheMap();
		Global.nodeHashtable = new CacheMap();
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

	public static int[] getKeyHashCodeSet(HashMap hashMap) {
		Set keySet = hashMap.keySet();
		Iterator iterator = keySet.iterator();
		int[] hashCodeCollection = new int[hashMap.size()];
		int i = 0;
		while (iterator.hasNext()) {
			hashCodeCollection[i] = iterator.next().hashCode();
			i += 1;
		}
		return hashCodeCollection;
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
