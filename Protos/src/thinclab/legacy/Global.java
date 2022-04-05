package thinclab.legacy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.RandomVariable;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.models.IPOMDP.MjRepr;
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
import java.util.Set;
import java.util.stream.Collectors;


public class Global {

	public static List<Integer> varDomSize = new ArrayList<>(10);
	public static List<String> varNames = new ArrayList<>(10);
	public static List<List<String>> valNames = new ArrayList<>(10);

	// public static TypedCacheMap<String, HashMap<Tuple<Integer, DD>, String>>
	// modelVars = new TypedCacheMap<>();
	public static TypedCacheMap<String, HashMap<MjRepr<ReachabilityNode>, String>> modelVars = new TypedCacheMap<>();

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

	// random number generator
	public static Random random = new Random();

	public static final boolean DEBUG = false;

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

	public static void clearHashtablesIfFull() {

		Global.nodeHashtable.clearIfFull();
		Global.addCache.clearIfFull();
		Global.multCache.clearIfFull();
		Global.addOutCache.clearIfFull();
		Global.dotProductCache.clearIfFull();

		if (Global.leafHashtable.clearIfFull()) {

			Global.leafHashtable.put(DD.zero, new WeakReference<DD>(DD.zero));
			Global.leafHashtable.put(DD.one, new WeakReference<DD>(DD.one));
		}

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

//	public static List<Tuple<Tuple<Integer, ReachabilityNode>, DD>> decoupleMj(DD b, int i_Mj) {
//
//		var varSet = b.getVars();
//
//		if (!Global.modelVars.containsKey(Global.varNames.get(i_Mj - 1))) {
//
//			LOGGER.error(String.format("Var %s index %s passed to decoupleMj does not look like a modelvar",
//					Global.varNames.get(i_Mj - 1), i_Mj));
//
//			System.exit(-1);
//			return null;
//		}
//
//		else if (varSet.size() == 0 || varSet.last() == i_Mj || !varSet.contains(i_Mj)) {
//
//			var mjVals = IntStream
//					.range(0,
//							b.getChildren().length)
//					.boxed().filter(b_ -> !b
//							.getChildren()[b_]
//									.equals(DD.zero))
//					.map(i -> Tuple.of(Global.modelVars.get(Global.varNames.get(i_Mj - 1)).entrySet().stream()
//							.filter(kv -> kv.getValue() == Global.valNames.get(i_Mj - 1).get(i)).map(kv -> kv.getKey())
//							.findFirst().get(), b.getChildren()[i]))
//					.collect(Collectors.toList());
//
//			return mjVals;
//		}
//
//		else {
//
//			LOGGER.error(String.format("%s has to be at the top of the DD.", i_Mj));
//			System.exit(-1);
//			return null;
//		}
//	}
//
//	public static Tuple<Set<Tuple<Integer, ReachabilityNode>>, Set<Tuple<Integer, ReachabilityNode>>> pruneModels(DD b,
//			int i_Mj) {
//
//		if (b.getVar() != i_Mj) {
//
//			LOGGER.error(String.format("Var %s in DD %s is not a model var", Global.varNames.get(b.getVar() - 1), b));
//			System.exit(-1);
//		}
//
//		var allModels = Global.modelVars.get(Global.varNames.get(i_Mj - 1)).entrySet().stream()
//				.filter(e -> e.getKey()._1().h == 1).map(e ->
//					{ // Get actual model from Mj value
//
//						var m = e.getKey();
//						var mv = e.getValue();
//
//						int index = Collections.binarySearch(Global.valNames.get(i_Mj - 1), mv);
//
//						if (index < 0) {
//
//							LOGGER.error(
//									String.format("Could not find model %s in %s", mv, Global.valNames.get(i_Mj - 1)));
//							System.exit(-1);
//						}
//
//						return Tuple.of(m, b.getChildren()[index].getVal());
//					})
//				.collect(Collectors.toList());
//
//		var validModels = allModels.stream().filter(m ->
//			{ // Remove all models with P(mj) < 0.01
//
//				if (m._1() >= 0.0f)
//					return true;
//
//				else {
//
//					LOGGER.info(String.format("Pruning model %s with probability %s", m._0(), m._1()));
//					return false;
//				}
//			}).map(m -> m._0()).collect(Collectors.toSet());
//
//		var invalidModels = allModels.stream().map(m -> m._0()).filter(m -> !validModels.contains(m))
//				.collect(Collectors.toSet());
//
//		var errorModels = allModels.stream()
//				.filter(m -> !(validModels.contains(m._0()) || invalidModels.contains(m._0())))
//				.collect(Collectors.toList());
//
//		if (errorModels.size() > 0) {
//
//			errorModels.forEach(m ->
//				{
//
//					LOGGER.error(String.format("Model %s with P(mj)=%s is not in valid models or pruned models", m._0(),
//							m._1()));
//				});
//
//			System.exit(-1);
//		}
//
//		else
//			LOGGER.info("Model pruning verified successfully.");
//
//		LOGGER.info(String.format("Pruned a total of %s models from %s models of the opponent", invalidModels.size(),
//				allModels.size()));
//
//		return Tuple.of(validModels, invalidModels);
//	}

	public static DD assemblebMj(int i_Mj, List<Tuple<Tuple<Integer, ReachabilityNode>, DD>> mjs,
			Set<Tuple<Integer, ReachabilityNode>> lowProbModels) {

		final var mjSpace = Global.modelVars.get(Global.varNames.get(i_Mj - 1));

		var _mjs = mjs.stream().map(m ->
			{

				if (!mjSpace.containsKey(m._0()) && !lowProbModels.contains(m._0())) {

					LOGGER.error(String.format(
							"Fatal error! %s is not in mj space and in the set of low probabolity models", m._0()));
					LOGGER.debug(String.format("j is %s", m._0()));

					LOGGER.debug(String.format("Differences are %s",
							m._0()._1().beliefs.stream().map(b1 -> lowProbModels.stream()
									.filter(_m -> _m._0() == m._0()._0()).filter(_m -> _m._1().h == m._0()._1().h)
									.filter(_m -> _m._1().alphaId == m._0()._1().alphaId)
									.map(b2 -> Tuple.of(b2._0(),
											DDOP.maxAll(DDOP
													.abs(DDOP.sub(b1, b2._1().beliefs.stream().findFirst().get())))))
									.collect(Collectors.toList())).collect(Collectors.toList())));

					System.exit(-1);
					return null;
				}

				return Tuple.of(mjSpace.get(m._0()), m._1());
			}).collect(Collectors.toMap(m -> m._0(), m -> m._1()));

		var childDDs = Global.valNames.get(i_Mj - 1).stream().map(m -> _mjs.containsKey(m) ? _mjs.get(m) : DD.zero)
				.toArray(DD[]::new);

		return DDOP.reorder(DDnode.getDD(i_Mj, childDDs));
	}

	public static DD assemblebMj(int i_Mj, List<Tuple<Tuple<Integer, ReachabilityNode>, DD>> mjs) {

		final var mjSpace = Global.modelVars.get(Global.varNames.get(i_Mj - 1));

		var _mjs = mjs.stream().map(m ->
			{

				if (!mjSpace.containsKey(m._0())) {

					LOGGER.error(String.format("Fatal error! %s is not in mj space", m._0()));
					LOGGER.debug(String.format("j is %s", m._0()));
					LOGGER.debug(String.format("Mj space is %s", mjSpace));

					var distances = mjSpace.keySet().stream().filter(k -> k._0() == m._0()._0())
							.map(k -> DDOP.maxAll(DDOP.abs(DDOP.sub(k._1().beliefs.stream().findFirst().get(),
									m._0()._1().beliefs.stream().findFirst().get()))))
							.collect(Collectors.toList());
					
					LOGGER.debug(String.format("Distances are %s", distances));

					System.exit(-1);
					return null;
				}

				return Tuple.of(mjSpace.get(m._0()), m._1());
			}).collect(Collectors.toMap(m -> m._0(), m -> m._1()));

		var childDDs = Global.valNames.get(i_Mj - 1).stream().map(m -> _mjs.containsKey(m) ? _mjs.get(m) : DD.zero)
				.toArray(DD[]::new);

		return DDOP.reorder(DDnode.getDD(i_Mj, childDDs));
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
					builder.append("A = ").append(m.framesj.get(_m._0())._1().A().get(_m._1().i_a));

				builder.append("}\"]\r\n");
			});

		builder.append("}\r\n");
		// m.framesjSoln.forEach(f -> builder.append(ModelGraph.toDot(f.MG,
		// f.m)).append("\r\n"));

		return builder.toString();
	}
}
