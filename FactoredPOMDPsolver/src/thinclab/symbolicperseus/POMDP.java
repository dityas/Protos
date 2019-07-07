package thinclab.symbolicperseus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.HashMap;

import thinclab.domainMaker.L0Frame;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.policyhelper.PolicyCache;
import thinclab.symbolicperseus.StateVar;

public class POMDP implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1805200931586799142L;

	public List<DD[]> beliefRegionList = new ArrayList<DD[]>();
	
	public int nStateVars;
	public int nObsVars;
	public int nVars;
	public int nActions;
	public int nObservations;

	public int maxAlphaSetSize;

	public boolean debug;

	public boolean ignoremore;
	public boolean addbeldiff;

	public StateVar[] stateVars;
	public StateVar[] obsVars;
	public Action[] actions;
	public int[] varDomSize;
	public int[] primeVarIndices;
	public int[] varIndices;
	public int[] obsIndices;
	public int[] primeObsIndices;
	public int[] obsVarsArity;
	public String[] varName;
	public double discFact, tolerance, maxRewVal;
	public DD initialBelState;
	public DD ddDiscFact;
	public String[] adjunctNames;
	public int nAdjuncts;
	public DD[] adjuncts; // some additional DDs that can be used
	public DD[] initialBelState_f;
	public DD[] qFn;
	public int[] qPolicy;
	public DD[][] belRegion;
	// these three should really be combined into AlphaVector class
	public int[] policy;
	public boolean[] uniquePolicy;
	public double[] policyvalue;
	public DD[] alphaVectors;
	public DD[] origAlphaVectors;

	public double[][] currentPointBasedValues, newPointBasedValues;
	public AlphaVector[] newAlphaVectors;
	public int numNewAlphaVectors;
	public double bestImprovement, worstDecline;
	
	// class variables for PBVI belief expansion
	private List<DD> beliefLeaves = new ArrayList<DD>();
	private List<DD[]> beliefPoints = new ArrayList<DD[]>();
	// end PBVI belief expansion variables
	
	// Policy cache
	public PolicyCache pCache = new PolicyCache(5);

	public static DD[] concatenateArray(DD a, DD[] b, DD c) {
		DD[] d = new DD[b.length + 2];
		d[0] = a;
		System.arraycopy(b, 0, d, 1, b.length);
		d[b.length + 1] = c;

		return d;
	}

	// assumes they're the same size along the first dimension
	public static int[][] concatenateArray(int[][] a, int[][] b) {
		int[][] d = new int[a.length][a[0].length + b[0].length];
		for (int i = 0; i < a.length; i++)
			d[i] = concatenateArray(a[i], b[i]);
		return d;
	}

	public static int[] concatenateArray(int[] a, int[] b) {
		int[] d = new int[a.length + b.length];
		int k = 0;
		for (int i = 0; i < a.length; i++)
			d[k++] = a[i];
		for (int i = 0; i < b.length; i++)
			d[k++] = b[i];
		return d;
	}

	public static DD[] concatenateArray(DD a, DD b) {
		DD[] d = new DD[2];
		d[0] = a;
		d[1] = b;
		return d;
	}

	public static DD[] concatenateArray(DD a, DD b, DD c) {
		DD[] d = new DD[3];
		d[0] = a;
		d[1] = b;
		d[2] = c;
		return d;
	}

	public static DD[] concatenateArray(DD[] a, DD[] b) {
		DD[] d = new DD[b.length + a.length];
		System.arraycopy(a, 0, d, 0, a.length);
		System.arraycopy(b, 0, d, a.length, b.length);
		return d;
	}

	public static DD[] concatenateArray(DD[] a, DD[] b, DD c) {
		DD[] d = new DD[b.length + a.length + 1];
		System.arraycopy(a, 0, d, 0, a.length);
		System.arraycopy(b, 0, d, a.length, b.length);
		d[b.length + a.length] = c;
		return d;
	}

	public static DD[] concatenateArray(DD a, DD[] b, DD[] c) {
		DD[] d = new DD[b.length + c.length + 1];
		d[0] = a;
		System.arraycopy(b, 0, d, 1, b.length);
		System.arraycopy(c, 0, d, 1 + b.length, c.length);
		return d;
	}

	public static DD[] concatenateArray(DD[] a, DD[] b, DD[] c) {
		DD[] d = new DD[b.length + a.length + c.length];
		System.arraycopy(a, 0, d, 0, a.length);
		System.arraycopy(b, 0, d, a.length, b.length);
		System.arraycopy(c, 0, d, a.length + b.length, c.length);
		return d;
	}

	public static double[] concatenateArray(double[] a, double[] b) {
		double[] d = new double[b.length + a.length];
		System.arraycopy(a, 0, d, 0, a.length);
		System.arraycopy(b, 0, d, a.length, b.length);
		return d;
	}

	public static int[][] stackArray(int[] a, int[] b) {
		int[][] d = new int[2][a.length];
		System.arraycopy(a, 0, d[0], 0, a.length);
		System.arraycopy(b, 0, d[1], 0, b.length);
		return d;
	}

	public double[] getRewFnTabular(int actId) {
		return OP.convert2array(actions[actId].rewFn,
				concatenateArray(varIndices, primeVarIndices));
	}

	public double[] getInitBelStateTabular() {
		return OP.convert2array(initialBelState, varIndices);

	}

	public double[] getTransFnTabular(int actId) {
		// first, blow up the transition function
		// WARNING: this may cause bad things to happen memory wise
		DD fullTF = actions[actId].transFn[0];
		for (int i = 1; i < nStateVars; i++) {
			fullTF = OP.mult(actions[actId].transFn[i], fullTF);
		}

		return OP.convert2array(fullTF,
				concatenateArray(varIndices, primeVarIndices));
	}

	public double[] getObsFnTabular(int actId, int obsId) {
		int[] tmpidarray = new int[1];
		tmpidarray[0] = primeObsIndices[obsId];
		return OP.convert2array(actions[actId].obsFn[obsId],
				concatenateArray(primeVarIndices, tmpidarray));
	}

	public void solveQMDP() {
		solveQMDP(500);
	}

	// solves the POMDP as an MDP
	public void solveQMDP(int count) {
		
		System.out.println("Computing qMDP policy");
		double bellmanErr = 2 * tolerance;
		DD valFn = DD.zero;
		DD prevValFn;
		DD[] cdArray;
		int actId1, actId2, actId;
		double[] zerovalarray = new double[1];
		DD[] tempQFn = new DD[nActions];
		zerovalarray[0] = 0;
		int iter = 0;
		
		while (bellmanErr > tolerance && iter < count) {
			System.out.println("iteration " + iter++);
			prevValFn = valFn;
			valFn = OP.primeVars(valFn, nVars);
			
			for (actId = 0; actId < nActions; actId++) {
				cdArray = concatenateArray(ddDiscFact, actions[actId].transFn,
						valFn);
				tempQFn[actId] = OP.addMultVarElim(cdArray, primeVarIndices);
				tempQFn[actId] = OP.add(actions[actId].rewFn, tempQFn[actId]);
				tempQFn[actId] = OP.approximate(tempQFn[actId], bellmanErr
						* (1 - discFact) / 2.0, zerovalarray);
			}
			
			valFn = OP.maxN(tempQFn);
			bellmanErr = OP.maxAll(OP.abs(OP.sub(valFn, prevValFn)));
			System.out.println("Bellman error: " + bellmanErr);
			Global.newHashtables();
		}
		// remove dominated alphaVectors
		boolean dominated;
		boolean[] notDominated = new boolean[nActions];
		
		for (actId1 = 0; actId1 < nActions; actId1++)
			notDominated[actId1] = false;
		
		for (actId1 = 0; actId1 < nActions; actId1++) {
			dominated = false;
			actId2 = 0;
		
			while (!dominated && actId2 < nActions) {
			
				if (notDominated[actId2]
						&& OP.maxAll(OP.sub(tempQFn[actId1], tempQFn[actId2])) < tolerance)
					dominated = true;
				actId2++;
			}
			
			if (!dominated)
				notDominated[actId1] = true;
		}
		
		int numleft = 0;
		
		for (actId1 = 0; actId1 < nActions; actId1++) {
			
			if (notDominated[actId1])
				numleft++;
		}
		
		qFn = new DD[numleft];
		qPolicy = new int[numleft];
		numleft = 0;
		
		for (actId1 = 0; actId1 < nActions; actId1++) {
			
			if (notDominated[actId1]) {
				qFn[numleft] = tempQFn[actId1];
				qPolicy[numleft] = actId1;
				numleft++;
			}
		}
	}

	public void displayQFn() {
		for (int i = 0; i < qFn.length; i++) {
			System.out
					.println("--------------------------------------------------- dd "
							+ i);
			qFn[i].display();
		}
	}
	
	public POMDP() {
		/*
		 * Empty constructor to allow for manipulation of object construction
		 * by other objects
		 */
	}

	public POMDP(String fileName) {
		readFromFile(fileName, false);
	}
	
	public POMDP(String fileName, POMDP oldpomdp) {
		readFromFile(fileName, false);
		setAlphaVectors(oldpomdp.alphaVectors, oldpomdp.policy);
		setBelRegion(oldpomdp.belRegion);
	}

	public POMDP(String fileName, boolean debb, boolean ig, boolean abd) {
		readFromFile(fileName, debb);
		ignoremore = ig;
		addbeldiff = abd;
	}

	public POMDP(String fileName, boolean debb, boolean ig) {
		readFromFile(fileName, debb);
		ignoremore = ig;
	}

	public POMDP(String fileName, boolean debb) {
		readFromFile(fileName, debb);
	}

	public void readFromFile(String fileName) {
		readFromFile(fileName, false);
	}

	public void setIgnoreMore(boolean ig) {
		ignoremore = ig;
	}
	
	public void initializeFromParsers(ParseSPUDD parserObj) {
		/*
		 * Populates requried fields and attributes of the POMDP from the parser object.
		 * 
		 * The orignal POMDP implementation by Hoey does this in the parsePOMDP method. I have split
		 * it into a separate method so that initialization can be done separately after upper frames
		 * or lower frames have done required changes if any. 
		 */
		
		debug = false;

		this.ignoremore = false;
		this.addbeldiff = false;
		this.nStateVars = parserObj.nStateVars;
		this.nObsVars = parserObj.nObsVars;
		this.nVars = nStateVars + nObsVars;
		this.stateVars = new StateVar[nStateVars];
		this.obsVars = new StateVar[nObsVars];

		this.varDomSize = new int[2 * (nStateVars + nObsVars)];
		this.varName = new String[2 * (nStateVars + nObsVars)];
		this.varIndices = new int[nStateVars];
		this.primeVarIndices = new int[nStateVars];
		this.obsIndices = new int[nObsVars];
		this.primeObsIndices = new int[nObsVars];

		/*
		 * set up state variables
		 */
		int k = 0;
		for (int i = 0; i < nStateVars; i++) {
			this.stateVars[i] = new StateVar(parserObj.valNames.get(i).size(),
					parserObj.varNames.get(i), i);
			for (int j = 0; j < stateVars[i].arity; j++) {
				this.stateVars[i].addValName(j, 
						((String) parserObj.valNames.get(i).get(j)));
			}
			
			/*
			 * must be indices as in Matlab!
			 */
			this.varIndices[i] = i + 1;
			this.primeVarIndices[i] = i + nVars + 1;
			this.varDomSize[k] = stateVars[i].arity;
			this.varName[k++] = stateVars[i].name;
		}

		/*
		 * set up observation variables
		 */
		this.nObservations = 1;
		this.obsVarsArity = new int[nObsVars];
		
		for (int i = 0; i < nObsVars; i++) {
			
			obsVars[i] = new StateVar(parserObj.valNames.get(i + nStateVars)
					.size(), parserObj.varNames.get(i + nStateVars), i
					+ nStateVars);
			
			for (int j = 0; j < obsVars[i].arity; j++) {
				obsVars[i].addValName(
						j,
						((String) parserObj.valNames.get(nStateVars + i).get(j)));
			}
			
			this.obsVarsArity[i] = obsVars[i].arity;
			this.nObservations = nObservations * obsVars[i].arity;
			this.obsIndices[i] = i + nStateVars + 1;
			this.primeObsIndices[i] = i + nVars + nStateVars + 1;
			this.varDomSize[k] = obsVars[i].arity;
			this.varName[k++] = obsVars[i].name;
		}
		
		for (int i = 0; i < nStateVars; i++) {
			
			varDomSize[k] = stateVars[i].arity;
			varName[k++] = stateVars[i].name + "_P";
		}
		
		for (int i = 0; i < nObsVars; i++) {
			
			varDomSize[k] = obsVars[i].arity;
			varName[k++] = obsVars[i].name + "_P";
		}

		/*
		 * set up Globals
		 */
		Global.setVarDomSize(varDomSize);
		Global.setVarNames(varName);

		for (int i = 0; i < nStateVars; i++) {
			Global.setValNames(i + 1, stateVars[i].valNames);
		}
		for (int i = 0; i < nObsVars; i++) {
			Global.setValNames(nStateVars + i + 1, obsVars[i].valNames);
		}
		for (int i = 0; i < nStateVars; i++) {
			Global.setValNames(nVars + i + 1, stateVars[i].valNames);
		}
		for (int i = 0; i < nObsVars; i++) {
			Global.setValNames(nVars + nStateVars + i + 1, obsVars[i].valNames);
		}

		/*
		 * set up dynamics
		 */
		nActions = parserObj.actTransitions.size();
		actions = new Action[nActions];
		uniquePolicy = new boolean[nActions];

		qFn = new DD[nActions];

		for (int a = 0; a < nActions; a++) {
			actions[a] = new Action(parserObj.actNames.get(a));
			actions[a].addTransFn(parserObj.actTransitions.get(a));
			actions[a].addObsFn(parserObj.actObserve.get(a));
			actions[a].rewFn = OP
					.sub(parserObj.reward, parserObj.actCosts.get(a));
			actions[a].buildRewTranFn();
			actions[a].rewFn = OP.addMultVarElim(actions[a].rewTransFn,
					primeVarIndices);

			/*
			 * find stochastic transitions (and deterministic varMappings)
			 * not done yet - where is this used?
			 */
		}
		/*
		 *  discount factor
		 */
		discFact = parserObj.discount.getVal();

		/*
		 *  make a DD version
		 */
		ddDiscFact = DDleaf.myNew(discFact);

		/*
		 *  the adjunct models
		 */
		nAdjuncts = parserObj.adjuncts.size();
		if (nAdjuncts > 0) {
			adjuncts = new DD[nAdjuncts];
			adjunctNames = new String[nAdjuncts];
			for (int a = 0; a < nAdjuncts; a++) {
				adjuncts[a] = parserObj.adjuncts.get(a);
				adjunctNames[a] = parserObj.adjunctNames.get(a);
			}
		}

		/*
		 * max reward value
		 */

		double maxVal = Double.NEGATIVE_INFINITY;
		double minVal = Double.POSITIVE_INFINITY;
		for (int a = 0; a < nActions; a++) {
			maxVal = Math.max(maxVal, OP.maxAll(OP.addN(actions[a].rewFn)));
			minVal = Math.min(minVal, OP.minAll(OP.addN(actions[a].rewFn)));
		}
		maxRewVal = maxVal / (1 - discFact);
		
		/*
		 * tolerance
		 */
		if (parserObj.tolerance == null) {
			double maxDiffRew = maxVal - minVal;
			double maxDiffVal = maxDiffRew / (1 - Math.min(0.95, discFact));
			tolerance = 1e-5 * maxDiffVal;
		} 
		
		else {
			tolerance = parserObj.tolerance.getVal();
		}
		
		/*
		 * initial belief
		 */
		initialBelState = parserObj.init;

		/*
		 * factored initial belief state
		 */
		initialBelState_f = new DD[nStateVars];
		for (int varId = 0; varId < nStateVars; varId++) {
			initialBelState_f[varId] = OP.addMultVarElim(initialBelState,
					MySet.remove(varIndices, varId + 1));
		}
	}
	
	public void setGlobals() {
		/*
		 * Sets the globals statics according to the current frame
		 * 
		 * This actually done during initialization. But since the lower frames will overwrite
		 * the globals during their initialization, this method has to be called manually whenever
		 * the current frame is being solved for an IPOMDP
		 */
		Global.setVarDomSize(varDomSize);
		Global.setVarNames(varName);

		for (int i = 0; i < nStateVars; i++) {
			Global.setValNames(i + 1, stateVars[i].valNames);
		}
		
		for (int i = 0; i < nObsVars; i++) {
			Global.setValNames(nStateVars + i + 1, obsVars[i].valNames);
		}
		
		for (int i = 0; i < nStateVars; i++) {
			Global.setValNames(nVars + i + 1, stateVars[i].valNames);
		}
		
		for (int i = 0; i < nObsVars; i++) {
			Global.setValNames(nVars + nStateVars + i + 1, obsVars[i].valNames);
		}
	}

	public void readFromFile(String fileName, boolean debb) {
		/*
		 * Read the POMDP directly from a domain file
		 */
		ParseSPUDD rawpomdp = new ParseSPUDD(fileName);
		rawpomdp.parsePOMDP(false);

		this.initializeFromParsers(rawpomdp);
	}
	
	public DD safeBeliefUpdate(DD belState, int actId, String[] obsnames) throws ZeroProbabilityObsException {
		/*
		 * Throws exception on zero probability observations
		 */
		if (obsnames.length != nObsVars)
			return null;
		int[] obsvals = new int[obsnames.length];
		for (int o = 0; o < obsnames.length; o++) {
			obsvals[o] = findObservationByName(o, obsnames[o]) + 1;
			if (obsvals[o] < 0)
				return null;
		}
		
		int[][] obsVals = stackArray(primeObsIndices, obsvals);
		
		DD[] restrictedObsFn = OP.restrictN(actions[actId].obsFn, obsVals);
		DD nextBelState = OP.addMultVarElim(
				concatenateArray(belState, actions[actId].transFn,
						restrictedObsFn), varIndices);
		nextBelState = OP.primeVars(nextBelState, -nVars);
		DD obsProb = OP.addMultVarElim(nextBelState, varIndices);
		if (obsProb.getVal() < 1e-8) {
			throw new ZeroProbabilityObsException(
					"OBSERVATION " + obsnames + " is zero probability");
		}
		nextBelState = OP.div(nextBelState,
				OP.addMultVarElim(nextBelState, varIndices));
		return nextBelState;
		
	}

	public DD safeBeliefUpdate(DD belState, int actId, int[][] obsVals) throws ZeroProbabilityObsException {

		DD[] restrictedObsFn = OP.restrictN(actions[actId].obsFn, obsVals);
		DD nextBelState = OP.addMultVarElim(
				concatenateArray(belState, actions[actId].transFn,
						restrictedObsFn), varIndices);
		nextBelState = OP.primeVars(nextBelState, -nVars);
		DD obsProb = OP.addMultVarElim(nextBelState, varIndices);
		if (obsProb.getVal() < 1e-8) {
			throw new ZeroProbabilityObsException(
					"OBSERVATION is zero probability");
		}
		nextBelState = OP.div(nextBelState,
				OP.addMultVarElim(nextBelState, varIndices));
		return nextBelState;
	}
	
	public DD beliefUpdate(DD belState, int actId, String[] obsnames) {
		if (obsnames.length != nObsVars)
			return null;
		int[] obsvals = new int[obsnames.length];
		for (int o = 0; o < obsnames.length; o++) {
			obsvals[o] = findObservationByName(o, obsnames[o]) + 1;
			if (obsvals[o] < 0)
				return null;
		}
		return beliefUpdate(belState, actId, obsvals);
	}

	public DD beliefUpdate(DD belState, int actId, int[] obsvals) {
		return beliefUpdate(belState, actId,
				stackArray(primeObsIndices, obsvals));
	}

	public DD beliefUpdate(DD belState, int actId, int[][] obsvals) {
		// double[] zerovalarray = new double[1];

		DD[] restrictedObsFn = OP.restrictN(actions[actId].obsFn, obsvals);
		DD nextBelState = OP.addMultVarElim(
				concatenateArray(belState, actions[actId].transFn,
						restrictedObsFn), varIndices);
		nextBelState = OP.primeVars(nextBelState, -nVars);
		DD obsProb = OP.addMultVarElim(nextBelState, varIndices);
		if (obsProb.getVal() < 1e-8) {
//			System.out
//					.println("WARNING: Zero-probability observation, resetting belief state to a uniform distribution");
			nextBelState = DD.one;
		}
		nextBelState = OP.div(nextBelState,
				OP.addMultVarElim(nextBelState, varIndices));
		return nextBelState;
	}
	
//	public DD[] factorBeliefPoint(DD beliefPoint) {
//		/*
//		 * factors belief state into a DD array
//		 */
//		DD[] fbs = new DD[nStateVars];
//		for (int varId = 0; varId < nStateVars; varId++) {
//			fbs[varId] = OP.addMultVarElim(beliefPoint,
//					MySet.remove(varIndices, varId + 1));
//		}
//		
//		return fbs;
//	} // public DD[] factorBeliefPoint

	public DD getGoalDD() {
		double[] onezero = { 0 };
		// get the best reward over all actions

		DD[] therewfns = new DD[nActions];
		for (int a = 0; a < nActions; a++) {
			therewfns[a] = actions[a].rewFn;
		}
		// max reward available at each state
		DD themaxdd = OP.maxN(therewfns);
		// need to do this to avoid rounding errors
		themaxdd = OP.approximate(themaxdd, 1e-6, onezero);

		// get the max of that
		double themaxmax = OP.maxAll(themaxdd);

		DD themaxmaxdd = DDleaf.myNew(themaxmax);

		// threshold the max dd
		DD goalDD = OP.threshold(themaxdd, themaxmax, 0);
		goalDD = OP.div(goalDD, themaxmaxdd);
		// onlymaxdd has a 1 in the goal state, 0 everywhere else
		return goalDD;
	}

	public boolean checkGoal(DD goalDD, DD belState, double threshold) {
		DD belAtGoal = OP.addMultVarElim(concatenateArray(belState, goalDD),
				varIndices);
		// this should be a Leaf DD
		double goalbel = ((DDleaf) belAtGoal).getVal();
		System.out.println("Goal Bel is " + goalbel);
		return (goalbel > threshold);
	}

	public double[] evaluateObservations(int independentfeatures) {
		return evaluateObservations(independentfeatures, false);
	}

	public double[] evaluateObservations(int independentfeatures, boolean usemax) {
		// the old method
		if (independentfeatures < 2) {
			return evaluateObservations(usemax);
		}
		// computes a fitness score for all the observation variables based on
		// their ability to distinguish between conditional plans
		// return value is a double array where obsfit[i] is the fraction of
		// belief-action pairs that at least one value of observation variable i
		// disagrees
		// on the conditional plan to follow. If this is close to 1, this is a
		// very
		// good observation variable
		double[] obsfit = new double[nObsVars];
		int[][] obsval = new int[2][nObsVars];
		DD[] restrictedObsFn;
		int i, ii;
		double[] maxbval;
		DD tObsFn;
		DD[] nextBelState;
		double bval, maxvdiff, obscount, vdiff, totba;
		int maxa, currmaxa;
		boolean agree, done, onedone;

		for (i = 0; i < nObsVars; i++) {
			obsval[0][i] = primeObsIndices[i];
		}
		for (i = 0; i < nObsVars; i++) {
			obsfit[i] = 0.0;
			obscount = 0;
			// System.out.println("--------------- checking observation "+i);
			// reset all obsvar values in obsval
			for (ii = 0; ii < nObsVars; ii++)
				obsval[1][ii] = 1;
			done = false;
			totba = 0;
			while (!done) {
				for (int actId = 0; actId < nActions; actId++) {
					// eliminate all other observation variables from this
					// observation function
					tObsFn = OP.addMultVarElim(actions[actId].obsFn,
							MySet.remove(primeObsIndices, primeObsIndices[i]));

					// System.out.println("tObsFn for i "+i+" is .........");
					// tObsFn.display();
					for (int b = 0; b < belRegion.length; b++) {
						agree = true;
						currmaxa = 0;

						maxbval = new double[obsVars[i].arity];
						nextBelState = new DD[obsVars[i].arity];
						for (int j = 0; agree && j < obsVars[i].arity; j++) {
							// update the belief b on action actId based only on
							// jth value of ith observation
							// first compute a restricted observation function
							obsval[1][i] = j + 1;
							restrictedObsFn = OP.restrictN(tObsFn, obsval);
							// System.out.println(" obs var "+i+"  actId "+actId+" belief "+b+" j "+j+" primeObsIndex "+obsval[0][0]+"  value "+obsval[1][0]);
							// tObsFn.display();
							// restrictedObsFn[0].display();
							nextBelState[j] = OP.addMultVarElim(
									concatenateArray(belRegion[b],
											actions[actId].transFn,
											restrictedObsFn), varIndices);
							nextBelState[j] = OP.primeVars(nextBelState[j],
									-nVars);
							DD obsProb = OP.addMultVarElim(nextBelState[j],
									varIndices);
							if (obsProb.getVal() < 1e-8)
								nextBelState[j] = DD.one;
							nextBelState[j] = OP.div(nextBelState[j],
									OP.addMultVarElim(nextBelState[j],
											varIndices));
							// nextBelState[j].display();
							maxbval[j] = Double.NEGATIVE_INFINITY;
							maxa = 0;
							for (int a = 0; a < alphaVectors.length; a++) {
								bval = OP.dotProduct(nextBelState[j],
										alphaVectors[a], varIndices);
								// System.out.println("a "+a+"  bval "+bval);
								if (bval > maxbval[j]) {
									maxbval[j] = bval;
									maxa = a;
								}
							}
							if (j == 0)
								currmaxa = maxa;
							agree = agree && (maxa == currmaxa);
						}
						// find largest difference in value and belief
						maxvdiff = Double.NEGATIVE_INFINITY;

						double bdist, maxbdist;
						maxbdist = 0.0;
						for (int j = 0; j < obsVars[i].arity; j++) {
							for (int k = j + 1; k < obsVars[i].arity; k++) {
								bdist = OP.maxAll(OP.abs(OP.sub(
										nextBelState[j], nextBelState[k])));
								if (bdist > maxbdist)
									maxbdist = bdist;
								// System.out.println(" value of each "+maxbval[j]+" "+maxbval[k]);
								vdiff = Math.abs(maxbval[j])
										+ Math.abs(maxbval[k]);
								if (vdiff > 0)
									vdiff = Math.abs(maxbval[j] - maxbval[k])
											/ vdiff;
								else
									vdiff = 0.0;

								// vdiff =
								// Math.abs(maxbval[j]-maxbval[k])/(Math.abs(maxbval[j])+Math.abs(maxbval[k]));
								if (vdiff > maxvdiff)
									maxvdiff = vdiff;
							}
						}
						if (addbeldiff)
							obscount += maxbdist;
						if (!agree) {
							// System.out.println("they don't agree! on "+b+"  "+actId);
							obscount++;
						} else {
							// they do agree, but still might give large value
							// differences
							// System.out.println("they agree! on "+b+"   "+actId+
							// " with value "+maxvdiff+" and bdiff "+maxbdist);
							obscount += maxvdiff;
						}
						totba++;
					}
				}
				// increment observations by one
				ii = 0;
				onedone = false;
				while (!onedone) {
					if (ii != i && ii < nObsVars) {
						if (obsval[1][ii] == obsVars[ii].arity) {
							ii++;
						} else {
							obsval[1][ii]++;
							onedone = true;
						}
					}
					if (ii == i) {
						ii++;
					}
					if (ii == nObsVars) {
						onedone = true;
						done = true;
					}
				}
			}
			obsfit[i] = obscount / totba;
			// System.out.println("obsfit["+i+"]="+obsfit[i]+" ... "+obscount+"   "+totba);
		}
		return obsfit;

	}

	public double[] evaluateObservations() {
		return evaluateObservations(false);
	}

	public double[] evaluateObservations(boolean usemax) {
		// computes a fitness score for all the observation variables based on
		// their ability to distinguish between conditional plans
		// return value is a double array where obsfit[i] is the fraction of
		// belief-action pairs that at least one value of observation variable i
		// disagrees
		// on the conditional plan to follow. If this is close to 1, this is a
		// very
		// good observation variable
		double[] obsfit = new double[nObsVars];
		int[][] obsval = new int[2][1];
		DD[] restrictedObsFn;
		double[] maxbval;
		DD tObsFn;
		DD[] nextBelState;
		double bval, maxvdiff, obscount, vdiff, totba;
		int maxa, currmaxa;
		boolean agree;

		totba = nActions * belRegion.length;
		for (int i = 0; i < nObsVars; i++) {
			obsfit[i] = 0.0;
			obscount = 0;
			// System.out.println("--------------- checking observation "+i);
			for (int actId = 0; actId < nActions; actId++) {
				// eliminate all other observation variables from this
				// observation function
				tObsFn = OP.addMultVarElim(actions[actId].obsFn,
						MySet.remove(primeObsIndices, primeObsIndices[i]));

				// System.out.println("tObsFn for i "+i+" is .........");
				// tObsFn.display();
				for (int b = 0; b < belRegion.length; b++) {
					agree = true;
					currmaxa = 0;

					maxbval = new double[obsVars[i].arity];
					nextBelState = new DD[obsVars[i].arity];
					for (int j = 0; agree && j < obsVars[i].arity; j++) {
						// update the belief b on action actId based only on jth
						// value of ith observation
						// first compute a restricted observation function
						obsval[0][0] = primeObsIndices[i];
						obsval[1][0] = j + 1;
						restrictedObsFn = OP.restrictN(tObsFn, obsval);
						// System.out.println(" obs var "+i+"  actId "+actId+" belief "+b+" j "+j+" primeObsIndex "+obsval[0][0]+"  value "+obsval[1][0]);
						// tObsFn.display();
						// restrictedObsFn[0].display();
						nextBelState[j] = OP
								.addMultVarElim(
										concatenateArray(belRegion[b],
												actions[actId].transFn,
												restrictedObsFn), varIndices);
						nextBelState[j] = OP.primeVars(nextBelState[j], -nVars);
						DD obsProb = OP.addMultVarElim(nextBelState[j],
								varIndices);
						if (obsProb.getVal() < 1e-8)
							nextBelState[j] = DD.one;
						nextBelState[j] = OP.div(nextBelState[j],
								OP.addMultVarElim(nextBelState[j], varIndices));
						// nextBelState[j].display();
						maxbval[j] = Double.NEGATIVE_INFINITY;
						maxa = 0;
						for (int a = 0; a < alphaVectors.length; a++) {
							bval = OP.dotProduct(nextBelState[j],
									alphaVectors[a], varIndices);
							// System.out.println("a "+a+"  bval "+bval);
							if (bval > maxbval[j]) {
								maxbval[j] = bval;
								maxa = a;
							}
						}
						if (j == 0)
							currmaxa = maxa;
						agree = agree && (maxa == currmaxa);
					}
					// find largest difference in value and belief
					maxvdiff = Double.NEGATIVE_INFINITY;
					double bdist, maxbdist;
					maxbdist = 0.0;
					for (int j = 0; j < obsVars[i].arity; j++) {
						for (int k = j + 1; k < obsVars[i].arity; k++) {
							bdist = OP.maxAll(OP.abs(OP.sub(nextBelState[j],
									nextBelState[k])));
							if (bdist > maxbdist)
								maxbdist = bdist;
							// System.out.println(" value of each "+maxbval[j]+" "+maxbval[k]);
							// vdiff = Math.abs(maxbval[j]-maxbval[k]);
							// if (vdiff > 0)
							vdiff = Math.abs(maxbval[j]) + Math.abs(maxbval[k]);
							if (vdiff > 0)
								vdiff = Math.abs(maxbval[j] - maxbval[k])
										/ vdiff;
							else
								vdiff = 0.0;
							// vdiff =
							// Math.abs(maxbval[j]-maxbval[k])/(Math.abs(maxbval[j])+Math.abs(maxbval[k]));
							if (vdiff > maxvdiff)
								maxvdiff = vdiff;
						}
					}
					if (!addbeldiff)
						maxbdist = 0.0;

					if (usemax) {
						if (!agree) {
							obscount = Math.max(obscount, maxbdist + 1);
						} else {
							obscount = Math.max(obscount, maxbdist + maxvdiff);
						}
					} else {
						if (addbeldiff)
							obscount += maxbdist;
						if (!agree) {
							// System.out.println("they don't agree! on "+b+"  "+actId);
							obscount++;
						} else {
							// they do agree, but still might give large value
							// differences
							// System.out.println("they agree! on "+b+"   "+actId+
							// " with value "+maxvdiff+" and bdiff "+maxbdist);
							obscount += maxvdiff;
						}
					}
				}
			}
			obsfit[i] = obscount / totba;
			// System.out.println("obsfit["+i+"]="+obsfit[i]+" ... "+obscount+"   "+totba);
		}
		return obsfit;
	}

	// a heuristic policy for the handwashing problem
	public int policyQuery(DD belState, boolean heuristic) {
		if (!heuristic) {
			return policyQuery(belState);
		}
		/*
		 * (engaged no confused yes) (colrespond yes no) (cuerespond yes no)
		 * (completed yes no))
		 * 
		 * 0 action resetchange 1 give_motiv_prompt 2 add_color 3 nothing
		 */
		double lookingp = getSingleValue(belState, 1, 0);
		double engagedyp = getSingleValue(belState, 2, 2);
		/* double engagedcp = */getSingleValue(belState, 2, 1);
		double colrespondp = getSingleValue(belState, 3, 0);
		double cuerespondp = getSingleValue(belState, 4, 0);
		double completedp = getSingleValue(belState, 5, 0);

		if (engagedyp > 0.5) {
			return 3;
		} else {
			if (lookingp > 0.7) {
				if (completedp > 0.9) {
					return 0;
				}
				if (colrespondp > 0.7) {
					return 2;
				}
			} else {
				if (cuerespondp > 0.7) {
					return 1;
				} else {
					if (completedp > 0.9) {
						return Global.random.nextInt(nActions);
					} else {
						return 1 + Global.random.nextInt(nActions - 1);
					}
				}
			}
		}
		return 0;
	}

	public int policyQuery(DD belState) {
		return policyQuery(belState, alphaVectors, policy);
	}

	public int policyQuery(DD[] belState) {
		return policyQuery(belState, alphaVectors, policy);
	}

	public int findActionByName(String aname) {
		for (int a = 0; a < nActions; a++) {
			if (aname.equalsIgnoreCase(actions[a].name))
				return a;
		}
		return -1;
	}

	public int findObservationByName(int ob, String oname) {
		for (int o = 0; o < obsVars[ob].arity; o++) {
			if (oname.equalsIgnoreCase(obsVars[ob].valNames[o]))
				return o;
		}
		return -1;
	}

	public int policyQuery(DD belState, DD[] alphaVectors, int[] policy) {
		// single DD belief state
		double bestVal = Double.NEGATIVE_INFINITY;
		double val;
		int bestAlphaId = 0, bestActId;
		for (int alphaId = 0; alphaId < alphaVectors.length; alphaId++) {
			val = OP.dotProduct(belState, alphaVectors[alphaId], varIndices);
			if (val > bestVal) {
				bestVal = val;
				bestAlphaId = alphaId;
			}
		}
		bestActId = policy[bestAlphaId];
		return bestActId;
	}

	public int policyQuery(DD[] belState, DD[] alphaVectors, int[] policy) {
		// factored DD belief state
		double[] values = OP.factoredExpectationSparseNoMem(belState,
				alphaVectors);
		double bestVal = Double.NEGATIVE_INFINITY;
		int bestAlphaId = 0, bestActId;
		for (int alphaId = 0; alphaId < alphaVectors.length; alphaId++) {
			if (values[alphaId] > bestVal) {
				bestVal = values[alphaId];
				bestAlphaId = alphaId;
			}
		}
		bestActId = policy[bestAlphaId];
		return bestActId;
	}

	public int policyBestAlphaMatch(DD belState, DD[] alphaVectors, int[] policy) {
		// single DD belief state
		double bestVal = Double.NEGATIVE_INFINITY;
		double val;
		int bestAlphaId = 0;
		
		double[] values = new double[alphaVectors.length];
		for (int alphaId = 0; alphaId < alphaVectors.length; alphaId++) {
			val = OP.dotProduct(belState, alphaVectors[alphaId], varIndices);
			values[alphaId] = val;
			if (val >= bestVal) {
				bestVal = val;
				bestAlphaId = alphaId;
			}
		}
		
//		System.out.println("BELIEF VALS: " + Arrays.toString(values));
		return bestAlphaId;
	}
	
	public double evalBeliefStateQMDP(DD belState) {
		return evalBeliefState(belState, qFn, qPolicy);

	}

	public double evalBeliefState(DD belState) {
		return evalBeliefState(belState, alphaVectors, policy);
	}

	public double evalBeliefState(DD belState, DD[] alphaVectors, int[] policy) {
		double bestVal = Double.NEGATIVE_INFINITY;
		double val;
		for (int alphaId = 0; alphaId < alphaVectors.length; alphaId++) {
			val = OP.dotProduct(belState, alphaVectors[alphaId], varIndices);
			if (val > bestVal) {
				bestVal = val;
			}
		}
		return bestVal;
	}

	public double findSimilarFactBelief(DD[] belState, DD[][] belRegion,
			int count) {
		return findSimilarFactBelief(belState, belRegion, count, 0.001);
	}

	public double findSimilarFactBelief(DD[] belief, DD[][] belSet, int count,
			double threshold) {
		double smallestDist = Double.POSITIVE_INFINITY;

		double maxnorm, dist;
		boolean done1, done2;
		done1 = false;
		for (int i = 0; !done1 & i < count; i++) {
			maxnorm = Double.NEGATIVE_INFINITY;
			done2 = false;
			for (int varId = 0; !done2 & varId < belief.length; varId++) {
				dist = OP
						.maxAll(OP.abs(OP.sub(belSet[i][varId], belief[varId])));
				if (dist > maxnorm) {
					maxnorm = dist;
					if (maxnorm >= smallestDist) {
						done2 = true;
					}
				}
			}
			if (maxnorm < smallestDist) {
				smallestDist = maxnorm;

				if (smallestDist <= threshold) {
					done1 = true;
				}
			}
		}
		return smallestDist;
	}

	public double computeAdjunctValue(DD belState, String adjunctName)
			throws Exception {
		// find this adjunct to see if it exists
		int a = 0;
		while (a < nAdjuncts && !adjunctNames[a].equalsIgnoreCase(adjunctName))
			a++;
		if (a >= nAdjuncts)
			throw new Exception();
		// compute the dot product with the belief state
		double val = OP.dotProduct(belState, adjuncts[a], varIndices);
		return val;
	}

	public double getSingleValue(DD belState, int varId, int varVal) {
		DD fbs = OP.addMultVarElim(belState,
				MySet.remove(varIndices, varId + 1));
		int vid = fbs.getVar();
		if (vid == 0) {
			// its already a leaf
			return fbs.getVal();
		} else {
			DD[] fbsc = fbs.getChildren();
			return fbsc[varVal].getVal();
		}
	}

	public void printBeliefState(DD belState) {
		// first factor it
		DD[] fbs = new DD[nStateVars];
		for (int varId = 0; varId < nStateVars; varId++) {
			fbs[varId] = OP.addMultVarElim(belState,
					MySet.remove(varIndices, varId + 1));
		}
		printBeliefState(fbs);
	}
	
	public void prettyPrintBeliefRegion() {
		/*
		 * Mainly used for debugging. Prints the region as a hashmap.
		 */
		System.out.println("CURRENT BELIEF REGION:");
		for (int i = 0; i < belRegion.length; i++) {
//			System.out.println(belRegion);
			if (belRegion[i] != null)
				System.out.println(Belief.toStateMap(this, belRegion[i]));
			else {
				System.out.println("NULL ENTRY");
			}
		}
		System.out.println("ALLOCATED SIZE: " + belRegion.length);
		System.out.println("BELIEF REGION END.");
	}
	
	public void prettyPrintTmpBeliefRegion(DD[][] tmpBelRegion) {
		/*
		 * Mainly used for debugging. Prints the region as a hashmap.
		 */
		System.out.println("TEMP BELIEF REGION:");
		for (int i = 0; i < tmpBelRegion.length; i++) {
			if (tmpBelRegion[i] != null)
				System.out.println(Belief.toStateMap(this, tmpBelRegion[i]));
			else {
				System.out.println("ZERO LENGTH BELIEF");
				break;
			}
		}
		System.out.println("ALLOCATED SIZE: " + tmpBelRegion.length);
		System.out.println("TEMP BELIEF REGION END.");
	}
	

	public void printBeliefState(DD[] belState) {
		for (int j = 0; j < belState.length; j++) {
			belState[j].display();
		}
	}

	public void printBelRegion() {
		for (int i = 0; i < belRegion.length; i++) {
			System.out.println("belief " + i + ":");
			for (int j = 0; j < belRegion[i].length; j++) {
				belRegion[i][j].display();
//				System.out.println(getBeliefStateMap(belRegion[i][j]));
			}
		}
	}

	public void setBelRegionFromData(int maxSize, double threshold, int[][] a,
			int[][][] o) {
		int count;
		int actId;
		double distance;

		DD[] nextBelState = new DD[nStateVars];
		DD[] restrictedObsFn;
		DD[] belState;
		int[][] obsConfig;

		double[] zerovalarray = new double[1];
		zerovalarray[0] = 0;

		DD[][] tmpBelRegion = new DD[maxSize][];

		count = 0;

		tmpBelRegion[count] = new DD[initialBelState_f.length];

		System.arraycopy(initialBelState_f, 0, tmpBelRegion[count], 0,
				initialBelState_f.length);
		for (int epindex = 0; count < maxSize && epindex < a.length; epindex++) {
			belState = initialBelState_f;
			for (int stepId = 0; count < maxSize && stepId < a[epindex].length; stepId++) {
				// get action from data
				actId = a[epindex][stepId];
				obsConfig = stackArray(primeObsIndices, o[epindex][stepId]);
				restrictedObsFn = OP.restrictN(actions[actId].obsFn, obsConfig);

				// update belState

				for (int varId = 0; varId < nStateVars; varId++) {
					nextBelState[varId] = OP.addMultVarElim(
							concatenateArray(belState, actions[actId].transFn,
									restrictedObsFn),
							concatenateArray(
									MySet.remove(primeVarIndices, varId + nVars
											+ 1), varIndices));
					nextBelState[varId] = OP.approximate(nextBelState[varId],
							1e-6, zerovalarray);
					nextBelState[varId] = OP.div(nextBelState[varId], OP
							.addMultVarElim(nextBelState[varId],
									primeVarIndices[varId]));
				}

				belState = OP.primeVarsN(nextBelState, -nVars);

				// add belState to tmpBelRegion
				distance = findSimilarFactBelief(belState, tmpBelRegion,
						count + 1, threshold);
				// System.out.println("distance "+distance);
				if (!debug && distance > threshold) {
					count = count + 1;
					if (count < maxSize) {
						// System.out.println("bel State : count "+count+" distance "+distance+" threshold "+threshold);

						tmpBelRegion[count] = new DD[belState.length];
						System.arraycopy(belState, 0, tmpBelRegion[count], 0,
								belState.length);
						if (count % 10 == 0)
							System.out.println(" " + count
									+ " belief states sampled");
					}
				}
				Global.newHashtables();
			}
			// System.out.println("resetting to initial belief - "+count+" belief states so far");
		}
		// copy over
		if (count < maxSize)
			count = count + 1; // means we never found enough, so count is one
								// less than total we found
		System.out.println("finished sampling  " + count + " belief states  "
				+ tmpBelRegion.length);
		belRegion = new DD[count][];
		for (int i = 0; i < count; i++) {
			belRegion[i] = new DD[tmpBelRegion[i].length];
			System.arraycopy(tmpBelRegion[i], 0, belRegion[i], 0,
					tmpBelRegion[i].length);
		}

	}

	public void simulateGeneric(int nits) {

		// do simulation
		DD belState = initialBelState;
		int actId;
		String[] obsnames = new String[nObsVars];
		String inobs, inact;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));

			while (nits > 0) {
				System.out.println("current belief state: ");
				printBeliefState(belState);
				if (alphaVectors != null && alphaVectors.length > 0) {
					actId = policyQuery(belState);
					System.out.println("action suggested by policy: " + actId
							+ " which is " + actions[actId].name);
				} else {
					System.out.print("enter action :");
					inact = in.readLine();
					actId = findActionByName(inact);
				}
				for (int o = 0; o < nObsVars; o++) {
					System.out.print("enter observation " + obsVars[o].name
							+ ": ");
					inobs = in.readLine();
					obsnames[o] = inobs;
				}
				belState = beliefUpdate(belState, actId, obsnames);
				nits--;
			}
		} catch (IOException e) {
		}
	}

	public void solve(int nRounds, int maxSize, int maxTries,
			int episodeLength, double threshold, double explorProb,
			int nIterations, int maxAlpha, String basename) {
		solve(nRounds, maxSize, maxTries, episodeLength, threshold, explorProb,
				nIterations, maxAlpha, basename, false);
	}

	// solves the POMDP in nRounds, with
	// maxSize belief states,
	// episodeLength episode lengths while generating belief states
	// threshold as the threshold difference for accepting belief states
	// explorProb is the probability of exploration during belief point
	// generation
	// nIterations is the number of iterations of Perseus per round
	// maxAlpha is the bound on the number of alpha vectors
	// name is the file name
	public void solve(int nRounds, int maxSize, int maxTries,
			int episodeLength, double threshold, double explorProb,
			int nIterations, int maxAlpha, String basename, boolean multinits) {

		// first solve the MDP underlying
		if (!debug && explorProb < 1.0)
			solveQMDP();

		// then loop over rounds
		// probability of using the MDP policy over the POMDP policy if the
		// choice is to exploit
		double mdpprob = 1.0;
		int firstStep = 0;
		String fname;
		int totaliterations = 0;
		maxAlphaSetSize = maxAlpha;

		for (int r = 0; r < nRounds; r++) {
			// generate a set of reachable belief points
			if (!multinits) {
				System.out.println("=============================");
				System.out.println("EXPANDING BELIEF REGION");
				reachableBelRegionCurrentPolicy(maxSize, maxTries,
						episodeLength, threshold, explorProb, mdpprob);
//				this.expandBeliefRegion(100);
//				this.expandBeliefRegionSSGA(100);
//				prettyPrintBeliefRegion();
				System.out.println("BELIEF REGION HAS: " + belRegion.length + " POINTS.");
				System.out.println("=============================");
			} 
//			else {
//				reachableBelRegionCurrentPolicyMultipleInits(maxSize, maxTries,
//						episodeLength, threshold, explorProb, mdpprob);
//			}
			// possibly print this out (for debugging)
			// printBelRegion();
			// run symbolic Perseus
			if (debug)
				Global.setSeed(8837328);
			boundedPerseus(nIterations, maxAlphaSetSize, firstStep, nIterations);
			totaliterations = firstStep + nIterations;
			fname = basename + "-" + totaliterations + ".pomdp";
			System.out.println("saving current policy to " + fname);
			try {
				save(fname);
			} catch (FileNotFoundException err) {
				System.out.println("file not found error " + err);
				return;
			} catch (IOException terr) {
				System.out.println("file write error" + terr);
			}
			firstStep += nIterations;
			if (r == 0)
				mdpprob = 0.5;
		}
	}
	
	public void solvePBVI(int rounds, int numDpBackups) {
		
		expandBeliefRegion(100);
		
		// initialize T
		alphaVectors = new DD[1];
		alphaVectors[0] = DD.one;

		for (int r=0; r < rounds; r++) {
			this.pCache.resetOscillationTracking();
			this.pCache.resetAlphaVecsMap();

			boundedPerseusStartFromCurrent(100, r * numDpBackups, numDpBackups);
			expandBeliefRegionSSGA(100);
//			expandBeliefRegion(100);
		}
		
		this.alphaVectors = this.pCache.getMaxAlphaVecs();
		this.policy = this.pCache.getMaxPolicy();
	}

	public double evaluatePolicyStationary(int nRuns, int nSteps) {
		return evaluatePolicyStationary(nRuns, nSteps, false);
	}

	public double evaluatePolicyStationary(int nRuns, int nSteps,
			boolean verbose) {
		int[][] stateConfig, nextStateConfig, obsConfig;
		DD belState;
		double totRew, avRew, theRew;
		avRew = 0.0;
		double totdisc = 1.0;
		int runId, stepId, actId, j;
		DD[] restrictedTransFn, obsDistn;
		for (runId = 0; runId < nRuns; runId++) {
			totRew = 0.0;
			belState = initialBelState;
			totdisc = 1.0;
			stateConfig = OP.sampleMultinomial(belState, varIndices);
			for (stepId = 0; stepId < nSteps; stepId++) {
				if (alphaVectors != null && alphaVectors.length > 0) {
					actId = policyQuery(belState);
				} else {
					actId = Global.random.nextInt(nActions);
				}
				theRew = OP.eval(actions[actId].rewFn, stateConfig);
				totRew = totRew + totdisc * theRew;
				totdisc = totdisc * discFact;

				restrictedTransFn = OP.restrictN(actions[actId].transFn,
						stateConfig);
				nextStateConfig = OP.sampleMultinomial(restrictedTransFn,
						primeVarIndices);
				obsDistn = OP.restrictN(actions[actId].obsFn,
						concatenateArray(stateConfig, nextStateConfig));
				obsConfig = OP.sampleMultinomial(obsDistn, primeObsIndices);

				if (verbose) {
					System.out.print(" " + runId + " " + stepId + " state:");
					for (j = 0; j < stateConfig[1].length; j++)
						System.out.print(" " + stateConfig[1][j]);

					System.out.print(": " + actId + " " + theRew + " obs:");
					for (j = 0; j < obsConfig[1].length; j++)
						System.out.print(" " + obsConfig[1][j]);

					System.out.println(":" + " " + totRew + " " + totdisc);
				}

				belState = beliefUpdate(belState, actId, obsConfig);

				stateConfig = Config.primeVars(nextStateConfig, -nVars);
				Global.newHashtables();
			}
			avRew = (runId * avRew + totRew) / (runId + 1);
		}
		return avRew;
	}

	// compute the reachable belief region from the MDP policy
	public void reachableBelRegionMDPpolicy(int maxSize, int maxTries,
			int episodeLength, double threshold, double explorProb) {
		solveQMDP();
		reachableBelRegionCurrentPolicy(maxSize, maxTries, episodeLength,
				threshold, explorProb, 1.0);
	}

	// maxSize and maxTries here apply to each initial belief state - so there
	// will potentially be maxSize*number_of_inits belief states
	public void reachableBelRegionCurrentPolicyMultipleInits(int maxSize,
			int maxTries, int episodeLength, double threshold,
			double explorProb, double mdpp) {
		DD[] iBelState_f = new DD[nStateVars];
		belRegion = null;
		// search through adjuncts for names starting with init - these are the
		// multiple initial belief states
		for (int i = 0; i < adjunctNames.length; i++) {
			if (adjunctNames[i].startsWith("init")) {
				// adjuncts[i] is an initial belief state - do a simulation from
				// here
				for (int varId = 0; varId < nStateVars; varId++) {
					iBelState_f[varId] = OP.addMultVarElim(adjuncts[i],
							MySet.remove(varIndices, varId + 1));
				}
				System.out
						.println("generating belief region from initial belief "
								+ adjunctNames[i] + ":");
				printBeliefState(adjuncts[i]);
				reachableBelRegionCurrentPolicy(iBelState_f, belRegion,
						maxSize, maxTries, episodeLength, threshold,
						explorProb, mdpp);
			}
		}

	}

	// compute the reachable belief region starting from the initial belief
	// state and no initial belief region (the default)
	public void reachableBelRegionCurrentPolicy(int maxSize, int maxTries,
			int episodeLength, double threshold, double explorProb, double mdpp) {
		// factored initial belief state
		DD[] iBelState_f = new DD[nStateVars];
		for (int varId = 0; varId < nStateVars; varId++) {
			iBelState_f[varId] = OP.addMultVarElim(initialBelState,
					MySet.remove(varIndices, varId + 1));
		}
		reachableBelRegionCurrentPolicy(iBelState_f, null, maxSize, maxTries,
				episodeLength, threshold, explorProb, mdpp);
	}

	// computes the reachable belief region using the current valueFunction and
	// Policy
	// computes starting from ibel (unfactored belief state), and updates
	// belRegion by adding input bRegion + the ones generated with this
	// simulation
	public void reachableBelRegionCurrentPolicy(DD[] iBelState_f,
			DD[][] iBelRegion, int maxSize, int maxTries, int episodeLength,
			double threshold, double explorProb, double mdpp) {

		int count;
		int choice, actId;
		double distance;

		DD[] nextBelState = new DD[nStateVars];
		DD obsDist;
		DD[] obsDistn, restrictedObsFn;
		DD[] ddState, belState, restrictedTransFn;
		int[][] stateConfig, nextStateConfig, obsConfig;

		double[] zerovalarray = new double[1];
		int[][] oneConfig = new int[2][1];
		double[] eprob = new double[2];
		double[] mdpprob = new double[2];
		zerovalarray[0] = 0;
		DD[][] tmpBelRegion = new DD[maxSize][];
		eprob[0] = 1 - explorProb;
		eprob[1] = explorProb;
		mdpprob[0] = 1 - mdpp;
		mdpprob[1] = mdpp;

		boolean isMDP = false;
		count = 0;
		int numtries = 0;
		tmpBelRegion[count] = new DD[iBelState_f.length];
		System.arraycopy(iBelState_f, 0, tmpBelRegion[count], 0,
				iBelState_f.length);
		stateConfig = null;
		nextStateConfig = null;
		double maxbeldiff, beldiff;
		while (count < maxSize && numtries < maxTries) {
//			System.out.println("-------------");
//			System.out.println("NUMTRIES: " + numtries);
			belState = iBelState_f;
			// figure out if we'll use the mdp or pomdp
			if (mdpp < 1.0)
				choice = OP.sampleMultinomial(mdpprob);
			else
				choice = 1;
			if (choice == 0)
				isMDP = false;
			else
				isMDP = true;
			
//			System.out.println("MDPP: " + mdpp + " CHOICE: " + choice);
//			System.out.println("BELSTATE: " + getBeliefStateMap(belState));
			if (isMDP) {
//				System.out.println("USING MDP");
				stateConfig = OP.sampleMultinomial(belState, varIndices);
//				System.out.println("STATE CONFIG: " + Arrays.deepToString(stateConfig));
			}
			for (int stepId = 0; count < maxSize  & stepId < episodeLength; stepId++) {
				// sample action
//				System.out.println("STEPID: " + stepId + " COUNT: " + count);
				choice = OP.sampleMultinomial(eprob);
				actId = 1;
				if (choice == 0) {
					if (isMDP)
						actId = policyQuery(Config.convert2dd(stateConfig),
								qFn, qPolicy);
					else
						actId = policyQuery(belState);
				} 
				else {
					actId = Global.random.nextInt(nActions);
				}
				 
				// sample observation
				if (isMDP) {
					restrictedTransFn = OP.restrictN(actions[actId].transFn,
							stateConfig);
					nextStateConfig = OP.sampleMultinomial(restrictedTransFn,
							primeVarIndices);
					obsDistn = OP.restrictN(actions[actId].obsFn,
							concatenateArray(stateConfig, nextStateConfig));
					obsConfig = OP.sampleMultinomial(obsDistn, primeObsIndices);
				} 
				else {
					obsDist = OP.addMultVarElim(
							concatenateArray(belState, actions[actId].transFn,
									actions[actId].obsFn),
							concatenateArray(varIndices, primeVarIndices));
					obsConfig = OP.sampleMultinomial(obsDist, primeObsIndices);
				}
				
				restrictedObsFn = OP.restrictN(actions[actId].obsFn, obsConfig);
//				System.out.println("choice "+choice+" action "+actions[actId].name + " MDP? " + isMDP + " obs " + Arrays.deepToString(obsConfig));

				// update belState

				maxbeldiff = 0.0;
				for (int varId = 0; varId < nStateVars; varId++) {
					nextBelState[varId] = OP.addMultVarElim(
							concatenateArray(belState, actions[actId].transFn,
									restrictedObsFn),
							concatenateArray(
									MySet.remove(primeVarIndices, varId + nVars
											+ 1), varIndices));
					nextBelState[varId] = OP.approximate(nextBelState[varId],
							1e-6, zerovalarray);
					nextBelState[varId] = OP.div(nextBelState[varId], OP
							.addMultVarElim(nextBelState[varId],
									primeVarIndices[varId]));
					// nextBelState[varId].display();
					beldiff = OP.maxAll(OP.abs(OP.sub(
							OP.primeVars(nextBelState[varId], -nVars),
							belState[varId])));
					if (beldiff > maxbeldiff)
						maxbeldiff = beldiff;
				}
//				System.out.println("NEXT BELIEF: " + getBeliefStateMap(nextBelState));
				numtries++;
				// make sure belief state has changed
				// System.out.println(" maxbeldiff "+maxbeldiff+" threshold "+threshold);
				if (stepId > 0 && maxbeldiff < threshold) {
//					System.out.println("BREAKING: STEP ID: " + stepId + " maxbeldiff: " + maxbeldiff);
					break;
				}

				belState = OP.primeVarsN(nextBelState, -nVars);
				if (isMDP)
					stateConfig = Config.primeVars(nextStateConfig, -nVars);

				// add belState to tmpBelRegion
				// printBeliefState(belState);
				distance = findSimilarFactBelief(belState, tmpBelRegion,
						count + 1, threshold);
				// System.out.println("distance "+distance);
				if (!debug && distance > threshold) {
					count = count + 1;
					if (count < maxSize) {
						// System.out.println("bel State : count "+count+" distance "+distance+" threshold "+threshold);

						tmpBelRegion[count] = new DD[belState.length];
						System.arraycopy(belState, 0, tmpBelRegion[count], 0,
								belState.length);
//						prettyPrintTmpBeliefRegion(tmpBelRegion);
//						if (count % 10 == 0)
//							System.out.println(" " + count
//									+ " belief states sampled");
					}
				}
				// System.out.println("Count is "+count);
				// add pure state to tmpBelRegion
				// if we're doing this for the MDP policy only
				if ((debug || isMDP) && count < maxSize) {
					ddState = new DD[nStateVars];
					for (int varId = 0; varId < nStateVars; varId++) {
						oneConfig[0][0] = stateConfig[0][varId];
						oneConfig[1][0] = stateConfig[1][varId];
						ddState[varId] = Config.convert2dd(oneConfig);
					}
					// printBeliefState(ddState);
					distance = findSimilarFactBelief(ddState, tmpBelRegion,
							count + 1, threshold);
					if (distance > threshold) {
						count = count + 1;
						if (count < maxSize) {
							// System.out.println("ddState : count "+count+" distance "+distance+" threshold "+threshold);

							tmpBelRegion[count] = new DD[ddState.length];
							System.arraycopy(ddState, 0, tmpBelRegion[count],
									0, ddState.length);
//							if (count % 10 == 0)
//								System.out.println(" " + count
//										+ " belief states sampled");
						}
					}
				}
				Global.newHashtables();
			}
//			prettyPrintTmpBeliefRegion(tmpBelRegion);
//			System.out.println("LOOP DONE");
			// System.out.println("resetting to initial belief - "+count+" belief states so far");
		}
		// copy over
		
//		System.out.println("AFTER SAMPLING:");
//		if (belRegion != null)
//			prettyPrintBeliefRegion();
		if (count < maxSize)
			count = count + 1; // means we never found enough, so count is one
								// less than total we found
		System.out.println("finished sampling  " + count + " belief states  "
				+ tmpBelRegion.length);
		int ii = 0;
		if (iBelRegion != null) {
//			System.out.println("[!][!][!]iBel not none.");
			belRegion = new DD[count + iBelRegion.length][];
			// copy over the ones that were passed in
			for (ii = 0; ii < iBelRegion.length; ii++) {
				belRegion[ii] = new DD[iBelRegion[ii].length];
				System.arraycopy(iBelRegion[ii], 0, belRegion[ii], 0,
						iBelRegion[ii].length);
			}

		} else {
//			System.out.println("[!][!][!]iBel none.");
			belRegion = new DD[count][];
			ii = 0;
		}
		// copy over the new ones
		for (int i = ii; i < ii + count; i++) {
			belRegion[i] = new DD[tmpBelRegion[i - ii].length];
			System.arraycopy(tmpBelRegion[i - ii], 0, belRegion[i], 0,
					tmpBelRegion[i - ii].length);
		}
//		System.out.println("AFTER UPDATES");
//		prettyPrintBeliefRegion();
		
		// Attempt to maintain belief region across rounds
		
		// First add all tmp beliefs to list
		
		List<DD[]> newBeliefs = new ArrayList<DD[]>();
		for (int j=0; j< tmpBelRegion.length; j++) {
			if (tmpBelRegion[j] != null) {
				
				boolean unique = true;
				Iterator<DD[]> beliefIter = beliefRegionList.iterator();
				
				while (beliefIter.hasNext()) {
					DD[] currentBelief = beliefIter.next();
//					if (Belief.toStateMap(this, currentBelief).equals(Belief.toStateMap(this, tmpBelRegion[j]))) {
					if (currentBelief.equals(tmpBelRegion[j])) {
//						System.out.println("[!][!][!] Found same");
						unique = false;
					}
				}
				
				if (unique) {
					newBeliefs.add(tmpBelRegion[j]);
				}
//				if (!beliefRegionList.contains(tmpBelRegion[j])) {
//					beliefRegionList.add(tmpBelRegion[j]);
//				}
//				else {
//					System.out.println("[!][!][!] Found same");
//				}
			}
		}
		
		beliefRegionList.addAll(newBeliefs);
		// Then replace belregion with the list
		belRegion = beliefRegionList.toArray(new DD[beliefRegionList.size()][]);
		
//		System.out.println("ADDING TO LIST");
//		prettyPrintBeliefRegion();
	}
	
	private boolean checkBeliefPointExists(List<DD[]> existingPoints, DD point) {
		/*
		 * Returns true if arg point exists in existingPoints
		 */
		boolean unique = true;
		
		Iterator<DD[]> exisitingPointsIterator = existingPoints.iterator();
		while (exisitingPointsIterator.hasNext()) {
			
			DD[] existingPoint = exisitingPointsIterator.next();

			if (Belief.checkEquals(this, existingPoint, point)) unique = false;
		}
		
		return unique;

	} // private boolean checkBeliefPointExists
	
	// -----------------------------------------------------------------------------------------
	
	// Belief expansion stuff
	
	public void expandBeliefRegion(int count) {
		/*
		 * Adds next level of belief points to the belief region
		 */
		int numNewPoints = 0;
		List<DD> newBeliefPoints = new ArrayList<DD>();
		
		// If first iteration, add initial belief
		if (beliefLeaves.size() == 0 && beliefPoints.size() == 0) {
			beliefLeaves.add(this.initialBelState);
			beliefPoints.add(Belief.factorBeliefPoint(this, this.initialBelState));
			numNewPoints+=1;
		}
		
		// Build the next stage of the belief tree
		else {
//			System.out.println("EXPANDING FROM LEAVES");
			List<List<String>> obsList = this.getAllObservationsList();
			// For all belief leaves
//			Iterator<DD> beliefLeavesIterator = this.beliefLeaves.iterator();
			while (count >= 0 && this.beliefLeaves.size() >= 1) {
//				System.out.println(this.beliefLeaves.size());
				int index = new Random().nextInt(this.beliefLeaves.size());
				
				DD toExpand = beliefLeaves.remove(index);
				// For all actions
				for (int act=0; act < actions.length; act++) {
					// For all observations
					for (int obs=0; obs < obsList.size(); obs++) {
						
						List<String> currentObs = obsList.get(obs);

						// Get next belief
						try {
							DD nextBelief = safeBeliefUpdate(
									toExpand,
									act,
									currentObs.toArray(new String[currentObs.size()]));
							
							// Add if does not already exist
							if (this.checkBeliefPointExists(this.beliefPoints, nextBelief)) {
								newBeliefPoints.add(nextBelief);
								beliefPoints.add(Belief.factorBeliefPoint(this, nextBelief));
//								System.out.println("FOUND NEW BELIEF POINT");
								numNewPoints+=1;
							}
						} 
						
						catch (ZeroProbabilityObsException e) {
//							System.out.println("ZERO PROB OBS");
							continue;
						}
						
						count--;
					} // for obs
				} // for act
				
//				System.out.println("SAMPLED " + newBeliefPoints.size() + " NEW BELIEFS.");
			}// while beliefLeavesIterator
			
			// replace beliefLeaves
			this.beliefLeaves.addAll(newBeliefPoints);

		} // else
		
		// replace beliefRegion
		this.belRegion = this.beliefPoints.toArray(new DD[this.beliefPoints.size()][]);
		
		System.out.println("[*][*] ADDED " + numNewPoints + " NEW BELIEF POINTS | "
				+ this.beliefLeaves.size() + " NOT YET SAMPLED. ");
		
	} // public void expandBeliefRegion

	
	public void expandFromBeliefSSGA(DD startBel, int count) {
		/*
		 * Starts SSGA expansion from param startBel
		 */
		int numNewPoints = 0;
		DD currentLeaf = startBel;
		
//		System.out.println(Arrays.toString(this.adjunctNames));
		
		// multinomial for action sampling
		double[] explore = new double[2];
		explore[0] = 0.6;
		explore[1] = 0.4;
	
		// Add initial belief we are starting from scratch
		if (beliefPoints.size() == 0 && beliefLeaves.size() == 0) {
//			beliefLeaves.add(this.initialBelState);
			beliefPoints.add(Belief.factorBeliefPoint(this, startBel));
		}
		
		// Build the next stage of the belief tree 
//		System.out.println("USING SSGA belief expansion");

		while (count >= 0) {
//			System.out.println(Arrays.toString(explore));
			int usePolicy = OP.sampleMultinomial(explore);
			
			// action sampling
			int act;
			if (usePolicy == 0) {
				act = this.policyQuery(currentLeaf);
			}
			
			else {
//				System.out.println("USING RANDOM ACTION");
				act = Global.random.nextInt(nActions);
			}

			// sample obs
			DD obsDist = OP.addMultVarElim(concatenateArray(currentLeaf,
															actions[act].transFn,
															actions[act].obsFn),
										   concatenateArray(varIndices, primeVarIndices));

			int[][] obsConfig = OP.sampleMultinomial(obsDist, primeObsIndices);
//			DD[] restrictedObsFn = OP.restrictN(actions[act].obsFn, obsConfig);
			
			// Get next belief
			try {
				DD nextBelief = safeBeliefUpdate(currentLeaf,
												 act, 
												 obsConfig);
				
				// Add if does not already exist
				if (this.checkBeliefPointExists(this.beliefPoints, nextBelief)) {
//					newBeliefPoints.add(nextBelief);
					beliefPoints.add(Belief.factorBeliefPoint(this, nextBelief));
//								System.out.println("FOUND NEW BELIEF POINT");
					numNewPoints+=1;
				}
				currentLeaf = nextBelief;
			} 
			
			catch (ZeroProbabilityObsException e) {
				System.out.println("ZERO PROB OBS. STARTING FROM INITIAL BELIEF");
				currentLeaf = this.initialBelState;
				continue;
			}
					
			count--;
			
//				System.out.println("SAMPLED " + newBeliefPoints.size() + " NEW BELIEFS.");
		}// while beliefLeavesIterator
		
//		System.out.println("[*][*] ADDED " + numNewPoints + " NEW BELIEF POINTS");
	}
	
	
	
	public void expandBeliefRegionSSGA(int count) {
		/*
		 * Adds next level of belief points to the belief region
		 */
		
		this.expandFromBeliefSSGA(this.initialBelState, count);
		
		if (this.adjuncts != null) {
			for (int i=0; i < this.adjuncts.length; i++) {
				this.expandFromBeliefSSGA(this.adjuncts[i], count);
			}
		}
		
		// replace beliefRegion
		this.belRegion = this.beliefPoints.toArray(new DD[this.beliefPoints.size()][]);
//				+ this.beliefLeaves.size() + " NOT YET SAMPLED. ");
	} // public void expandBeliefRegionSSEA
	
	
	// -----------------------------------------------------------------------------------------
	
	
	public void boundedPerseus(int nIterations, int maxAlpha, int firstStep,
			int nSteps) {
//		if (firstStep == 0) {
			DD newAlpha, prevAlpha;
			double bellmanErr;
			double[] onezero = { 0 };
			boolean dominated;
	//		System.out.println("FirstStep: " + firstStep);
			// check if the value function exists yet
			DD[] tmpalphaVectors = new DD[nActions];
	
			maxAlphaSetSize = maxAlpha;
	
			numNewAlphaVectors = 0;
	
			// this is done in pureStrategies now- still to do
			for (int actId = 0; actId < nActions; actId++) {
				System.out.println("computing pure strategy for action " + actId);
				newAlpha = DD.zero;
				bellmanErr = tolerance;
				for (int i = 0; i < 50; i++) {
					// prevAlpha = OP.sub(newAlpha,DDleaf.myNew(2*tolerance+1));
					// while (OP.maxAll(OP.abs(OP.sub(newAlpha,prevAlpha))) >
					// tolerance) {
					prevAlpha = newAlpha;
					newAlpha = OP.primeVars(newAlpha, nVars);
					newAlpha = OP.addMultVarElim(
							concatenateArray(ddDiscFact, actions[actId].transFn,
									newAlpha), primeVarIndices);
					newAlpha = OP.addN(concatenateArray(actions[actId].rewFn,
							newAlpha));
					newAlpha = OP.approximate(newAlpha, bellmanErr * (1 - discFact)
							/ 2.0, onezero);
					bellmanErr = OP.maxAll(OP.abs(OP.sub(newAlpha, prevAlpha)));
					if (bellmanErr <= tolerance)
						break;
					Global.newHashtables();
				}
				// now add this vector only if not dominated
				dominated = false;
				int aid = 0;
				while (!dominated && aid < numNewAlphaVectors) {
					if (OP.maxAll(OP.sub(newAlpha, tmpalphaVectors[aid])) < tolerance)
						dominated = true;
					aid++;
				}
				if (!dominated) {
					tmpalphaVectors[numNewAlphaVectors] = newAlpha;
					numNewAlphaVectors++;
				}
			}
			alphaVectors = new DD[numNewAlphaVectors];
			origAlphaVectors = new DD[numNewAlphaVectors];
			for (int aid = 0; aid < numNewAlphaVectors; aid++) {
				alphaVectors[aid] = tmpalphaVectors[aid];
				origAlphaVectors[aid] = tmpalphaVectors[aid];
			}
//		}
		boundedPerseusStartFromCurrent(maxAlpha, firstStep, nSteps);
	}

	public DD[] getAlphaVectors() {
		return alphaVectors;
	}

	public int[] getPolicy() {
		return policy;
	}

	public DD[][] getBelRegion() {
		return belRegion;
	}

	public void setAlphaVectors(DD[] newAlphaVectors, int[] newpolicy) {
		alphaVectors = new DD[newAlphaVectors.length];
		policy = new int[newpolicy.length];
		for (int i = 0; i < newAlphaVectors.length; i++) {
			alphaVectors[i] = newAlphaVectors[i];
			policy[i] = newpolicy[i];
		}
	}

	public void setBelRegion(DD[][] newBelRegion) {
		belRegion = new DD[newBelRegion.length][];
		for (int i = 0; i < newBelRegion.length; i++) {
			belRegion[i] = new DD[newBelRegion[i].length];
			System.arraycopy(newBelRegion[i], 0, belRegion[i], 0,
					newBelRegion[i].length);
		}
	}

	public void boundedPerseusStartFromCurrent(int maxAlpha, int firstStep,
			int nSteps) {
		double bellmanErr;
		double[] onezero = { 0 };
		double steptolerance;

		maxAlphaSetSize = maxAlpha;

		bellmanErr = 20 * tolerance;

		currentPointBasedValues = OP.factoredExpectationSparseNoMem(belRegion,
				alphaVectors);
		DD[] primedV;
		double maxAbsVal = 0;
		for (int stepId = firstStep; stepId < firstStep + nSteps; stepId++) {
			steptolerance = tolerance;
//			System.out.println("STEP ID: " + stepId);
//			System.out.println(" there are " + alphaVectors.length
//					+ " alpha vectors:");

			primedV = new DD[alphaVectors.length];
			for (int i = 0; i < alphaVectors.length; i++) {
				primedV[i] = OP.primeVars(alphaVectors[i], nVars);
			}
			maxAbsVal = Math.max(
					OP.maxabs(concatenateArray(OP.maxAllN(alphaVectors),
							OP.minAllN(alphaVectors))), 1e-10);
//			System.out.println("maxAbsVal: " + maxAbsVal);

			int count = 0;
			int choice;
			int nDpBackups = 0;
			RandomPermutation permutedIds = new RandomPermutation(
					Global.random, belRegion.length, debug);
			// could be one more than the maximum number at most
			newAlphaVectors = new AlphaVector[maxAlphaSetSize + 1];
			newPointBasedValues = new double[belRegion.length][maxAlphaSetSize + 1];
			numNewAlphaVectors = 0;

			AlphaVector newVector;
			double[] diff = new double[belRegion.length];
			double[] maxcurrpbv;
			double[] maxnewpbv;
			double[] newValues;
			double improvement;

			// we allow the number of new alpha vectors to get one bigger than
			// the maximum allowed size, since we may be able to cull more than
			// one
			// alpha vector when trimming, bringing us back below the cutoff
			while (numNewAlphaVectors < maxAlphaSetSize
					&& !permutedIds.isempty()) {

				if (nDpBackups >= 2 * alphaVectors.length) {
					computeMaxMinImprovement();
					if (bestImprovement > tolerance
							&& bestImprovement > -2 * worstDecline)
						break;
				}
				Global.newHashtables();
				count = count + 1;
//				if (count % 100 == 0)
//					System.out.println("count is " + count);
				if (numNewAlphaVectors == 0) {
					choice = 0;
				} else {
					maxcurrpbv = OP.getMax(currentPointBasedValues,
							permutedIds.permutation);
					maxnewpbv = OP.getMax(newPointBasedValues,
							numNewAlphaVectors, permutedIds.permutation);
					permutedIds.getNewDoneIds(maxcurrpbv, maxnewpbv,
							steptolerance);
					diff = permutedIds.getDiffs(maxcurrpbv, maxnewpbv,
							steptolerance);

//					if (true) {
//						System.out.print("diff is ");
//						for (int k = 0; k < diff.length; k++)
//							System.out.print(" " + k + ":" + diff[k]);
//						System.out.println();
//					}
					if (permutedIds.isempty())
						break;
					choice = OP.sampleMultinomial(diff);
				}
//				if (true) {
//					permutedIds.display();
//				}
				int i = permutedIds.getSetDone(choice);
//				System.out.println(" num backups so far " + nDpBackups
//						+ " num belief points left " + permutedIds.getNumLeft()
//						+ " choice " + choice + " i " + i + "tolerance "
//						+ steptolerance);
				if (numNewAlphaVectors < 1
						|| (OP.max(newPointBasedValues[i], numNewAlphaVectors)
								- OP.max(currentPointBasedValues[i]) < steptolerance)) {
					// dpBackup
					newVector = dpBackup(belRegion[i], primedV, maxAbsVal);
					// newVector.alphaVector.display();

					newVector.alphaVector = OP.approximate(
							newVector.alphaVector, bellmanErr * (1 - discFact)
									/ 2.0, onezero);
					newVector.setWitness(i);

//					System.out.print(" " + OP.nEdges(newVector.alphaVector)
//							+ " edges, " + OP.nNodes(newVector.alphaVector)
//							+ " nodes, " + OP.nLeaves(newVector.alphaVector)
//							+ " leaves");
					nDpBackups = nDpBackups + 1;

					// merge and trim
					newValues = OP.factoredExpectationSparseNoMem(belRegion,
							newVector.alphaVector);
					if (numNewAlphaVectors < 1) {
						improvement = Double.POSITIVE_INFINITY;
					} else {
						improvement = OP.max(OP.sub(newValues, OP.getMax(
								newPointBasedValues, numNewAlphaVectors)));
					}
					if (improvement > tolerance) {
						for (int belid = 0; belid < belRegion.length; belid++)
							newPointBasedValues[belid][numNewAlphaVectors] = newValues[belid];
						newAlphaVectors[numNewAlphaVectors] = newVector;
						numNewAlphaVectors++;
					}
				}
			}
			// iteration is over,
//			System.out.println("iteration " + stepId
//					+ " is over...number of new alpha vectors: "
//					+ numNewAlphaVectors + "   numdp backupds " + nDpBackups);

			// compute statistics
			//
			computeMaxMinImprovement();

			// save data and copy over new to old
			alphaVectors = new DD[numNewAlphaVectors];
			currentPointBasedValues = new double[newPointBasedValues.length][numNewAlphaVectors];
//			System.out.println("policy/values are: ");
			policy = new int[numNewAlphaVectors];
			policyvalue = new double[numNewAlphaVectors];
			for (int j = 0; j < nActions; j++)
				uniquePolicy[j] = false;

			for (int j = 0; j < numNewAlphaVectors; j++) {
				alphaVectors[j] = newAlphaVectors[j].alphaVector;
//				System.out.println(" " + newAlphaVectors[j].actId + "/"
//						+ newAlphaVectors[j].value);
				policy[j] = newAlphaVectors[j].actId;
				policyvalue[j] = newAlphaVectors[j].value;
				uniquePolicy[policy[j]] = true;
			}
//			System.out.println("unique policy :");
//			for (int j = 0; j < nActions; j++)
//				if (uniquePolicy[j])
//					System.out.print(" " + j);
//			System.out.println();

//			for (int i = 0; i < alphaVectors.length; i++) {
//				double bval = OP.factoredExpectationSparseNoMem(
//						belRegion[newAlphaVectors[i].witness], alphaVectors[i]);
//				System.err.println(" " + stepId + " " + policy[i] + " " + bval);
//			}
			for (int j = 0; j < belRegion.length; j++) {
				System.arraycopy(newPointBasedValues[j], 0,
						currentPointBasedValues[j], 0, numNewAlphaVectors);
			}
//			System.out.println("best improvement: " + bestImprovement
//					+ "  worstDecline " + worstDecline);
			bellmanErr = Math.min(10, Math.max(bestImprovement, -worstDecline));
			System.out.println("STEP: " + stepId 
					+ " \tBELLMAN ERROR: " + bellmanErr
					+ " \tBELIEF POINTS: " + this.belRegion.length
					+ " \tA VECTORS: " + alphaVectors.length);
			
			if (stepId % 100 < 5) {
				continue;
			}
			
			else {
				this.pCache.cachePolicy(this.alphaVectors.length,
										this.alphaVectors,
										this.policy);
				
				if (this.pCache.isOscillating(new Float(bellmanErr))) {
					System.out.println("BELLMAN ERROR " + bellmanErr + " OSCILLATING. PROBABLY CONVERGED.");
					break;
				}
			} // else
			
			if (bellmanErr < 0.001) {
				System.out.println("BELLMAN ERROR LESS THAN 0.001. PROBABLY CONVERGED.");
				break;
			}
		}

	}

	public void computeMaxMinImprovement() {
		double imp;
		bestImprovement = Double.NEGATIVE_INFINITY;
		worstDecline = Double.POSITIVE_INFINITY;
		for (int j = 0; j < belRegion.length; j++) {
			// find biggest improvement at this belief point
			imp = OP.max(newPointBasedValues[j], numNewAlphaVectors)
					- OP.max(currentPointBasedValues[j]);
			if (imp > bestImprovement)
				bestImprovement = imp;
			if (imp < worstDecline)
				worstDecline = imp;
		}
	}

	public void save(String filename) throws FileNotFoundException, IOException {

		FileOutputStream f_out;
		// save to disk
		// Use a FileOutputStream to send data to a file
		// called myobject.data.
		f_out = new FileOutputStream(filename);

		// Use an ObjectOutputStream to send object data to the
		// FileOutputStream for writing to disk.
		ObjectOutputStream obj_out = new ObjectOutputStream(f_out);

		// Pass our object to the ObjectOutputStream's
		// writeObject() method to cause it to be written out
		// to disk.
		obj_out.writeObject(this);
	}

	public static POMDP load(String filename) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(
				filename));
		return (POMDP) input.readObject();
	}

	public void displayPolicy() {
		System.out.print("  " + alphaVectors.length
				+ " alpha vectors ... policy/values are: ");
		for (int j = 0; j < alphaVectors.length; j++) {
			System.out.println(" " + policy[j]);
			alphaVectors[j].display();
		}
		System.out.println("");
	}

	// this replaces newPointBasedValues based on the input argument
	// pointBasedValues
	// and newAlphaVectors based on the input in alphaVectors
	public int trim(AlphaVector[] alphaVectors, int nVectors,
			double[][] pointBasedValues, int maxSize, double minImprovement) {
		if (nVectors <= 1)
			return 0;

		double[] improvement = new double[nVectors];
		boolean[] toremove = new boolean[nVectors];
		int numremoved = 0;
		double maxv, maxd, minimp, thediff;
		int minimpi = 0;
		minimp = Double.POSITIVE_INFINITY;
		for (int i = 0; i < nVectors; i++) {
			// find the belief point at which the ith alpha vector
			// has the greatest improvement over all other alpha vectors
			// the amount of improvement is improvement[i]
			if (!toremove[i]) {
				maxd = Double.NEGATIVE_INFINITY;
				for (int j = 0; j < pointBasedValues.length; j++) {
					// get the maximum over all other vectors for jth belief
					// point
					maxv = Double.NEGATIVE_INFINITY;
					for (int k = 0; k < nVectors; k++) {
						if (!toremove[k] && !(k == i)) {
							if (pointBasedValues[j][k] > maxv)
								maxv = pointBasedValues[j][k];
						}
					}
					thediff = pointBasedValues[j][i] - maxv;
					if (thediff > maxd)
						maxd = thediff;
				}
				improvement[i] = maxd;
				if (improvement[i] < minImprovement) {
					toremove[i] = true;
					numremoved++;
				}
				if (improvement[i] < minimp) {
					minimp = improvement[i];
					minimpi = i;
				}
			}
		}
		// if none were removed, we may still have to cull one
		// cull the one with the smallest improvement.
		if (nVectors > maxSize && numremoved == 0) {
			toremove[minimpi] = true;
			numremoved = 1;
		}
		// System.out.println("min improvement "+minimp+" num removed "+numremoved);

		// finally, actually remove the vectors that should be
		int j = 0;
		for (int i = 0; i < nVectors; i++) {
			if (!toremove[i]) {
				newAlphaVectors[j] = alphaVectors[i];
				for (int k = 0; k < pointBasedValues.length; k++)
					newPointBasedValues[k][j] = pointBasedValues[k][i];
				j++;
			} else {
				System.out.print(" removed alpha vector " + i);
			}
		}
		return numremoved;
	}

	public void displayAlphaVectorSum(DD alphaVector, int varId) {
		DD tempDD = OP.addMultVarElim(alphaVector,
				MySet.remove(varIndices, varId + 1));
		tempDD.display();
	}

	public void displayAlphaVectorSums(DD alphaVector) {
		for (int i = 0; i < nStateVars; i++)
			displayAlphaVectorSum(alphaVector, i);
	}

	public void displayAlphaVectorPrimeSum(DD alphaVector, int varId) {
		DD tempDD = OP.addMultVarElim(alphaVector,
				MySet.remove(primeVarIndices, varId + nVars + 1));
		tempDD.display();
	}

	public void displayAlphaVectorPrimeSums(DD alphaVector) {
		for (int i = 0; i < nStateVars; i++)
			displayAlphaVectorPrimeSum(alphaVector, i);
	}

	// returns a structure consisting of the following elements
	// newAlpha is the new alpha vector computed by backup at this belief state
	// belState
	// bestValue is the value at this belief state
	// bestActId is the action to take at this belief state corresponding to
	// this newly computed alpha vector
	// bestObsStrat is tells which primed alpha vector this new alpha vector is
	// computed from for bestActId action and for each possible observation (of
	// the valid ones)

	public AlphaVector dpBackup(DD[] belState, DD[] primedV, double maxAbsVal) {
		NextBelState[] nextBelStates;
		// get next unnormalised belief states
		// System.out.println("smallestProb "+tolerance);
		double smallestProb;
		if (ignoremore)
			smallestProb = tolerance;
		else
			smallestProb = tolerance / maxAbsVal;
		nextBelStates = oneStepNZPrimeBelStates(belState, true, smallestProb);

		// precompute obsVals
		for (int actId = 0; actId < nActions; actId++) {
			nextBelStates[actId].getObsVals(primedV);
		}
		double bestValue = Double.NEGATIVE_INFINITY;
		double actValue;
		int bestActId = 0;
		int[] bestObsStrat = new int[nObservations];

		for (int actId = 0; actId < nActions; actId++) {
			actValue = 0.0;
			// compute immediate rewards
			actValue = actValue
					+ OP.factoredExpectationSparseNoMem(belState,
							actions[actId].rewFn);

			// compute observation strategy
			nextBelStates[actId].getObsStrat();
			actValue = actValue + discFact
					* nextBelStates[actId].getSumObsValues();
			// System.out.println(" actId "+actId+" actValue "+actValue+" sumobsvalues "+nextBelStates[actId].getSumObsValues());
			if (actValue > bestValue) {
				bestValue = actValue;
				bestActId = actId;
				bestObsStrat = nextBelStates[actId].obsStrat;
			}
		}
		// construct corresponding alpha vector
		DD newAlpha = DD.zero;
		DD nextValFn = DD.zero;
		DD obsDd;
		int[] obsConfig = new int[nObsVars];
		for (int alphaId = 0; alphaId < alphaVectors.length; alphaId++) {
			if (MySet.find(bestObsStrat, alphaId) >= 0) {
				// System.out.println("alphaId is "+alphaId);
				obsDd = DD.zero;
				// for (int obsId = 0; obsId < bestObsStrat.length; obsId++) {
				for (int obsId = 0; obsId < nObservations; obsId++) {
					if (bestObsStrat[obsId] == alphaId) {
						obsConfig = statedecode(obsId + 1, nObsVars,
								obsVarsArity);
						obsDd = OP.add(obsDd, Config.convert2dd(stackArray(
								primeObsIndices, obsConfig)));
					}
				}
				nextValFn = OP.add(nextValFn, OP.multN(concatenateArray(
						DDleaf.myNew(discFact), obsDd, primedV[alphaId])));
			}
		}
		newAlpha = OP.addMultVarElim(
				concatenateArray(actions[bestActId].transFn,
						actions[bestActId].obsFn, nextValFn),
				concatenateArray(primeVarIndices, primeObsIndices));
		newAlpha = OP
				.addN(concatenateArray(newAlpha, actions[bestActId].rewFn));
		bestValue = OP.factoredExpectationSparse(belState, newAlpha);
		// package up to return
		AlphaVector returnAlpha = new AlphaVector(newAlpha, bestValue,
				bestActId, bestObsStrat);
		return returnAlpha;
	}

	public NextBelState[] oneStepNZPrimeBelStates(DD[] belState,
			boolean normalize, double smallestProb) {
		int[][] obsConfig = new int[nObservations][nObsVars];
		double[] obsProbs;
		DD[] marginals = new DD[nStateVars + 1];
		DD dd_obsProbs;
		for (int obsId = 0; obsId < nObservations; obsId++) {
			obsConfig[obsId] = statedecode(obsId + 1, nObsVars, obsVarsArity);
		}
		Global.newHashtables();
		NextBelState[] nextBelStates = new NextBelState[nActions];
		for (int actId = 0; actId < nActions; actId++) {
			dd_obsProbs = OP.addMultVarElim(
					concatenateArray(belState, actions[actId].transFn,
							actions[actId].obsFn),
					concatenateArray(varIndices, primeVarIndices));
			obsProbs = OP.convert2array(dd_obsProbs, primeObsIndices);
			nextBelStates[actId] = new NextBelState(obsProbs, smallestProb);
			// compute marginals
			if (!nextBelStates[actId].isempty()) {
				marginals = OP.marginals(
						concatenateArray(belState, actions[actId].transFn,
								actions[actId].obsFn), primeVarIndices,
						varIndices);
				nextBelStates[actId].restrictN(marginals, obsConfig);
			}
		}
		return nextBelStates;

	}

	public class NextBelState {
		DD[][] nextBelStates;
		int[] nzObsIds;
		double[][] obsVals;
		int numValidObs;
		int[] obsStrat;
		double[] obsValues;
		double sumObsValues;

		public NextBelState(double[] obsProbs, double smallestProb) {
			numValidObs = 0;
			for (int i = 0; i < obsProbs.length; i++)
				if (obsProbs[i] > smallestProb)
					numValidObs++;
			// System.out.println("number of valid observations "+numValidObs);
			nextBelStates = new DD[numValidObs][nStateVars + 1];
			nzObsIds = new int[numValidObs];
			obsStrat = new int[nObservations];
			obsValues = new double[numValidObs];
			int j = 0;
			for (int i = 0; i < obsProbs.length; i++)
				if (obsProbs[i] > smallestProb)
					nzObsIds[j++] = i;
		}

		public NextBelState(NextBelState a) {
			nextBelStates = new DD[a.nextBelStates.length][a.nextBelStates[0].length];
			for (int i = 0; i < a.nextBelStates.length; i++) {
				for (int j = 0; j < a.nextBelStates[i].length; j++)
					nextBelStates[i][j] = a.nextBelStates[i][j];
			}
			obsVals = new double[a.obsVals.length][];
			for (int i = 0; i < a.obsVals.length; i++)
				obsVals[i] = a.obsVals[i];
			obsStrat = a.obsStrat;

			nzObsIds = a.nzObsIds;
			numValidObs = a.numValidObs;
			obsValues = a.obsValues;
			sumObsValues = a.sumObsValues;
		}

		public boolean isempty() {
			return (numValidObs == 0);
		}

		public void restrictN(DD[] marginals, int[][] obsConfig) {
			int obsId;
			for (int obsPtr = 0; obsPtr < numValidObs; obsPtr++) {
				obsId = nzObsIds[obsPtr];
				nextBelStates[obsPtr] = OP.restrictN(marginals,
						stackArray(primeObsIndices, obsConfig[obsId]));
			}
		}

		// get the observation values
		// obsVals[i][j] is the value expected if we see observation i
		// and then follow the conditional plan j
		public void getObsVals(DD[] primedV) {
			if (!isempty()) {
				obsVals = new double[numValidObs][primedV.length];
				obsVals = OP.factoredExpectationSparseNoMem(nextBelStates,
						primedV);
			}
		}

		public double getSumObsValues() {
			return sumObsValues;
		}

		// get observation strategy
		// obsStrat[i] is the best conditional to plan to follow
		// if observation i is seen
		// this is just the index that maximizes over the obsVals
		// and alphaValue is the value of that conditional plan given than
		// observation
		public void getObsStrat() {
			double alphaValue = 0;
			sumObsValues = 0;
			int obsId;
			double obsProb;
			for (int obsPtr = 0; obsPtr < nObservations; obsPtr++) {
				obsStrat[obsPtr] = 0;
			}
			for (int obsPtr = 0; obsPtr < numValidObs; obsPtr++) {
				obsId = nzObsIds[obsPtr];
				obsProb = nextBelStates[obsPtr][nStateVars].getVal();
				alphaValue = obsVals[obsPtr][0];
				for (int i = 1; i < obsVals[obsPtr].length; i++) {
					if (obsVals[obsPtr][i] > alphaValue) {
						alphaValue = obsVals[obsPtr][i];
						obsStrat[obsId] = i;
					}
				}
				obsValues[obsPtr] = obsProb * alphaValue;

				sumObsValues += obsValues[obsPtr];
			}
		}
	}

	public int[] statedecode(int statenum, int n) {
		int[] bases = new int[n];
		for (int i = 0; i < n; i++)
			bases[i] = 2;
		return statedecode(statenum, n, bases);
	}

	public int[] statedecode(int statenum, int n, int[] bases) {
		int[] statevec = new int[n];
		for (int i = 0; i < n; i++)
			statevec[i] = 0;

		if (statenum == 1) {
			for (int i = 0; i < n; i++)
				statevec[i] = 1;
			return statevec;
		}
		statenum--;
		int res = statenum;
		int remd;
		for (int i = 0; i < n; i++) {
			if (res == 1) {
				statevec[i] = 1;
				break;
			}
			remd = res % bases[i];
			res = ((int) Math.floor(res / bases[i]));
			statevec[i] = remd;
		}
		for (int i = 0; i < n; i++) {
			statevec[i]++;
		}
		return statevec;
	}
	
	private void recursiveObsGen(List<List<String>> obsComb, List<StateVar> obsVars, List<String> obsVector, int finalLen, int varIndex){
		/* 
		 *  Recursively generates a list of all possible combinations of values of the observation variables
		 */
		
		if (varIndex < obsVars.size()) {
			
			if (obsVector.size() == finalLen) {
				obsComb.add(obsVector);
			}
			
			else {
				
				List<String> obsVectorCopy = new ArrayList<String>(obsVector);
				StateVar obs = obsVars.get(varIndex);
				for (int i=0;i<obs.valNames.length;i++) {
					List<String> anotherObsVecCopy = new ArrayList<String>(obsVectorCopy);
					anotherObsVecCopy.add(obs.valNames[i]);
					recursiveObsGen(obsComb, obsVars, anotherObsVecCopy, finalLen, varIndex + 1);
				}
			}
			
		}
		
		else {
			obsComb.add(obsVector);
		}
	} // private void recursiveObsGen
	
	private List<List<String>> recursiveObsCombinations(List<StateVar> obsVars){
		/*
		 * Driver program for generating observations recursively
		 */
		int finalLen = obsVars.size();
		List<String> obsVec = new ArrayList<String>();
		List<List<String>> obsComb = new ArrayList<List<String>>();
		
		recursiveObsGen(obsComb, obsVars, obsVec, finalLen, 0);
		
		return obsComb;
	} // private List<List<String>> recursiveObsCombinations
	
	public List<List<String>> getAllObservationsList() {
		return recursiveObsCombinations(Arrays.asList(this.obsVars));
	}
	
	public String[] getObsVarsArray() {
		/*
		 * Returns an array of observation variable names
		 */
		String[] obsVarsArray = new String[this.obsVars.length];
		
		for (int i=0; i<this.obsVars.length;i++) {
			obsVarsArray[i] = this.obsVars[i].name;
		}
		
		return obsVarsArray;
	}
	
	public String[][] getObsValArray() {
		/*
		 * Returns array of observation variable values
		 */
		String[][] obsValsArray = new String[this.nObsVars][];
		for (int i=0 ; i < nObsVars ; i++) {
			obsValsArray[i] = this.obsVars[i].valNames;
		}
		
		return obsValsArray;
	}
}
