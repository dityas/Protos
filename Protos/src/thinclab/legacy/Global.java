package thinclab.legacy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.RandomVariable;
import thinclab.models.IPOMDP;
import thinclab.models.POMDP;
import thinclab.models.POSeqDecMakingModel;
import thinclab.models.datastructures.ModelGraph;
import thinclab.models.datastructures.ReachabilityNode;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Global {

	public static String storagDir = null;
	public static boolean showProgressBar = false;

	public static List<Integer> varDomSize = new ArrayList<>(10);
	public static List<String> varNames = new ArrayList<>(10);
	public static List<List<String>> valNames = new ArrayList<>(10);

	// public static TypedCacheMap<String, HashMap<Tuple<Integer, DD>, String>>
	// modelVars = new TypedCacheMap<>();
	public static TypedCacheMap<String, HashMap<Tuple<Integer, ReachabilityNode>, String>> modelVars = new TypedCacheMap<>();

	public static int NUM_VARS = 0;

	/* identify which frame and level has the current context */
	public static int CONTEXT_LEVEL_ID;
	public static int CONTEXT_FRAME_ID;

	// hash tables
	public static TypedCacheMap<DD, WeakReference<DD>> leafHashtable = new TypedCacheMap<>(10000);
	public static TypedCacheMap<DD, WeakReference<DD>> nodeHashtable = new TypedCacheMap<>(10000);
	public static TypedCacheMap<Tuple<DD, DD>, DD> addCache = new TypedCacheMap<>(10000);
	public static TypedCacheMap<Tuple<DD, DD>, DD> multCache = new TypedCacheMap<>(10000);
	public static TypedCacheMap<Tuple<DD, Integer>, DD> addOutCache = new TypedCacheMap<>(10000);
	public static TypedCacheMap<Tuple3<DD, DD, HashSet<Integer>>, Float> dotProductCache = new TypedCacheMap<>(10000);
	/* Caches for optimizing NZ prime computations */
	public static boolean USE_NEXT_BELSTATE_CACHES = false;

	public static HashMap<DD, HashMap<String, DD[][]>> NEXT_BELSTATES_CACHE = new HashMap<DD, HashMap<String, DD[][]>>();

	public static HashMap<DD, HashMap<String, double[]>> OBS_PROB_CACHE = new HashMap<DD, HashMap<String, double[]>>();

	// random number generator
	public static Random random = new Random();

	// --------------------------------------------------------------------------

	private static final Logger LOGGER = LogManager.getLogger(Global.class);

	public static void addVariable(String varName, List<String> valNames) {

		Collections.sort(valNames);

		Global.varNames.add(varName);
		Global.valNames.add(valNames);
		Global.varDomSize.add(valNames.size());

		LOGGER.debug(String.format("Add var %s with values %s", varName, valNames));
		Global.NUM_VARS += 1;
	}

	public static void replaceValues(int var, List<String> vals) {

		LOGGER.warn(String.format("Values of RV %s are being changed", Global.varNames.get(var - 1)));
		Global.valNames.set(var - 1, vals);
		Global.varDomSize.set(var - 1, vals.size());
		Global.clearHashtables();
	}

	public static void populateFromRandomVariables(List<RandomVariable> vars) {

		vars.stream().forEach(v -> Global.addVariable(v.getVarName(), v.getValNames()));
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
		Global.addCache.clear();
		Global.multCache.clear();
		Global.addOutCache.clear();
		Global.dotProductCache.clear();
		Global.leafHashtable.put(DD.zero, new WeakReference<DD>(DD.zero));
		Global.leafHashtable.put(DD.one, new WeakReference<DD>(DD.one));

		System.gc();
	}

	public static void clearAll() {

		Global.varNames.clear();
		Global.valNames.clear();
		Global.varDomSize.clear();
		Global.NUM_VARS = 0;
		Global.clearHashtables();
		Global.modelVars.clear();
	}

	public static void newHashtables() {
		// Global.leafHashtable = new WeakHashMap();
		// Global.nodeHashtable = new WeakHashMap();

		LOGGER.warn("Clearing caches. This will reduce performance");

		Global.leafHashtable = new TypedCacheMap<>(10000);
		Global.nodeHashtable = new TypedCacheMap<>(10000);
		Global.addCache = new TypedCacheMap<>(10000);
		Global.multCache = new TypedCacheMap<>(10000);
		Global.addOutCache = new TypedCacheMap<>(10000);
		Global.dotProductCache = new TypedCacheMap<>(10000);
		Global.leafHashtable.put(DD.zero, new WeakReference<DD>(DD.zero));
		Global.leafHashtable.put(DD.one, new WeakReference<DD>(DD.one));
	}

	public static void logCacheSizes() {

		LOGGER.debug(String.format("Nodes Cache: %s", Global.nodeHashtable.size()));
		LOGGER.debug(String.format("Leaf Cache: %s", Global.leafHashtable.size()));
		LOGGER.debug(String.format("Add Cache: %s", Global.addCache.size()));
		LOGGER.debug(String.format("Mult Cache: %s", Global.multCache.size()));
		LOGGER.debug(String.format("Dot Product Cache: %s", Global.dotProductCache.size()));
		LOGGER.debug(String.format("Add Out Cache: %s", Global.addOutCache.size()));

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

	public static List<String> sortByVarOrdering(List<String> varList, List<String> ordering) {

		var unknownVar = varList.stream().filter(v -> ordering.indexOf(v) < 0).findFirst();
		if (unknownVar.isPresent()) {

			LOGGER.error(String.format("Symbol %s is not defined in %s", unknownVar.get(), ordering));
			return null;
		}

		Collections.sort(varList, (a, b) -> ordering.indexOf(a) - ordering.indexOf(b));
		return varList;
	}

	
	public static String modelVarsToDot(String v, IPOMDP m) {

		var builder = new StringBuilder();

		m.framesj.forEach(f ->
			{

				if (f._1() instanceof IPOMDP) {

					builder.append(
							Global.modelVarsToDot(Global.varNames.get(((IPOMDP) f._1()).i_Mj - 1), (IPOMDP) f._1()))
							.append("\r\n");
				}
			});

		// build subgraph
		builder.append("subgraph cluster_").append(v).append("{\r\n");
		builder.append("node [shape=record];\r\n");
		builder.append("label=\"").append(v).append("\";\r\n");

		Global.modelVars.get(v).keySet().forEach(_m ->
			{

				var _mName = Global.modelVars.get(v).get(_m);
				builder.append(v).append("_").append(_mName).append(" [label=\"{ ").append(_mName).append(" | --- | ");

				if (_m._1().beliefs.size() == 1) {

					var b = _m._1().beliefs.stream().findFirst().get();
					var frame = m.framesj.get(_m._0())._1();

					// if (frame instanceof IPOMDP) {

					// var varList = ((IPOMDP) frame).i_S();
					builder.append(DDOP.toDotRecord(b, frame.i_S())).append("| --- |");
					// }

				}

				if (_m._1().i_a >= 0)
					builder.append("A = ")
						   .append(
								   m.framesj.get(
										   _m._0())._1().A().get(
												   _m._1().i_a));

				builder.append("}\"]\r\n");
			});
		

		builder.append("}\r\n");
		//m.framesjSoln.forEach(f -> builder.append(ModelGraph.toDot(f.MG, f.m)).append("\r\n"));
		
		return builder.toString();
	}
}
