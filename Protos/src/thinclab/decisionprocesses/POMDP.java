package thinclab.decisionprocesses;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import thinclab.belief.BeliefOps;
import thinclab.ddinterface.DDMaker;
import thinclab.ddinterface.DDTree;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.Action;
import thinclab.legacy.AlphaVector;
import thinclab.legacy.Config;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;
import thinclab.legacy.MySet;
import thinclab.legacy.OP;
import thinclab.legacy.StateVar;
import thinclab.parsers.ParseSPUDD;

public class POMDP extends DecisionProcess implements Serializable {

	private static final long serialVersionUID = -1805200931586799142L;

	public List<DD[]> beliefRegionList = new ArrayList<DD[]>();
	
	/*
	 * For use as IPOMDP frames at level 0
	 */
	
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
	public float discFact, tolerance, maxRewVal;
	public DD initialBelState;
	public DD ddDiscFact;
	public String[] adjunctNames;
	public int nAdjuncts;
	public DD[] adjuncts; // some additional DDs that can be used
	public DD[] initialBelState_f;
	public DD[] qFn;
	public int[] qPolicy;
	public DD[][] belRegion;
	public List<List<String>> obsCombinations = new ArrayList<List<String>>();
	public int[][] obsCombinationsIndices;
	
	/*
	 * These three should really be combined into AlphaVector class
	 */
	public int[] policy;
	public boolean[] uniquePolicy;
	public float[] policyvalue;
	public DD[] alphaVectors;
	public DD[] origAlphaVectors;

	public float[][] currentPointBasedValues, newPointBasedValues;
	public AlphaVector[] newAlphaVectors;
	public int numNewAlphaVectors;
	public float bestImprovement, worstDecline;
	
	public ParseSPUDD parser;
	
	private static final Logger LOGGER = LogManager.getLogger(POMDP.class);
	
	// ---------------------------------------------------------------------
	/*
	 * Storage variables for DDTree representation
	 * 
	 * The DDTree representation is being used as an easy intermediate representation
	 * between the SPUDD format and DD objects. This is because the DD objects rely on
	 * Global arrays. 
	 */
	public List<StateVar> S = new ArrayList<StateVar>();
    public List<StateVar> Omega = new ArrayList<StateVar>();
    
    public List<String> A = new ArrayList<String>();
    
    public HashMap<String, HashMap<String, DDTree>> Oi = 
    		new HashMap<String, HashMap<String, DDTree>>();
    
    public HashMap<String, HashMap<String, DDTree>> Ti = 
			new HashMap<String, HashMap<String, DDTree>>();
    
    public List<DDTree> costs = new ArrayList<DDTree>();
    public HashMap<String, DDTree> costMap = new HashMap<String, DDTree>();
    
    public DDTree R;
    public DDTree initBeliefDdTree;
    
    public List<DDTree> adjunctBeliefs = new ArrayList<DDTree>();
    public List<DD> initialBeliefs = new ArrayList<DD>();
    public DD currentBelief;
    
    /*
     * Keep a DDMaker in case new DDs need to be made
     */
    public DDMaker ddMaker = new DDMaker();
	
	// ---------------------------------------------------------------------
	
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

	public static float[] concatenateArray(float[] a, float[] b) {
		float[] d = new float[b.length + a.length];
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

	public POMDP() {
		/*
		 * Empty constructor to allow for manipulation of object construction
		 * by other objects
		 */
	}

	public POMDP(String fileName) {
		readFromFile(fileName, false);
	}

//	public POMDP(String fileName, boolean debb, boolean ig, boolean abd) {
//		readFromFile(fileName, debb);
//		ignoremore = ig;
//		addbeldiff = abd;
//	}
//
//	public POMDP(String fileName, boolean debb, boolean ig) {
//		readFromFile(fileName, debb);
//		ignoremore = ig;
//	}
//
//	public POMDP(String fileName, boolean debb) {
//		readFromFile(fileName, debb);
//	}

	public void readFromFile(String fileName) {
		readFromFile(fileName, false);
	}

	public void setIgnoreMore(boolean ig) {
		ignoremore = ig;
	}
	
	// -----------------------------------------------------------------------------------------------
	/*
	 * Initialization methods
	 */
	
	public void initializeFromParsers(ParseSPUDD parserObj) {
		/*
		 * Populates requried fields and attributes of the POMDP from the parser object.
		 * 
		 * The orignal POMDP implementation by Hoey does this in the parsePOMDP method. I have split
		 * it into a separate method so that initialization can be done separately after upper frames
		 * or lower frames have done required changes if any. 
		 */
		
		LOGGER.info("Begin POMDP initialisation from parser");
		
		debug = false;
		
		this.parser = parserObj;
		
		this.frameID = parserObj.frameID;
		this.level = parserObj.level;
		this.initializeFrameFromParser(parserObj);
		
		this.initializeSFromParser(parserObj);
		this.initializeOmegaFromParser(parserObj);
		
		this.initializeAFromParser(parserObj);
		this.initializeTFromParser(parserObj);
		this.initializeOFromParser(parserObj);
		this.initializeRFromParser(parserObj);
		
		this.initializeDiscountFactorFromParser(parserObj);		
		this.initializeBeliefsFromParser(parserObj);
		this.initializeAdjunctsFromParser(parserObj);
		
		this.commitVariables();
		this.setDynamics();
		this.setBeliefs();
		
		LOGGER.info("POMDP initialised");
		
		/* Null parser reference after parsing is done */
		this.parser = null;
		
		/* set belief operations handler */
		this.bOPs = new BeliefOps(this);
		
		/* compute obscombinations to avoid computing it repeatedly later */
		this.computeAllPossibleObsCombinations();
	}
	
	public void initializeFrameFromParser(ParseSPUDD parserObj) {
		/*
		 * Initializes this as an IPOMDP frame from the parser
		 */
		this.frameID = parserObj.frameID;
		this.level = parserObj.level;
		
		LOGGER.debug("frame ID set to " + this.frameID + " at level " + this.level);
	}
	
	public void initializeSFromParser(ParseSPUDD parserObj) {
		/*
		 * Stage the parsed variables from the domain file
		 */
		
		this.S.addAll(parserObj.S);
		
		LOGGER.debug("S staged to " + this.S);
	}
	
	public void initializeOmegaFromParser(ParseSPUDD parserObj) {
		/*
		 * Stage the parsed variables from the domain file
		 */

		this.Omega.addAll(parserObj.Omega);
		
		LOGGER.debug("Omega staged to " + this.Omega);
	}
	
	public void initializeOFromParser(ParseSPUDD parserObj) {
		/*
		 * Initializes the observation function
		 */
		this.Oi = parserObj.Oi;
		
		LOGGER.debug("O initialized to " + this.Oi);
	}
	
	public void initializeTFromParser(ParseSPUDD parserObj) {
		/*
		 * Initializes the transition function
		 */
		this.Ti = parserObj.Ti;
		
		LOGGER.debug("T initialized to " + this.Ti);
	}
	
	public void initializeAFromParser(ParseSPUDD parserObj) {
		/*
		 * Initializes the Action space from parser
		 */
		this.A.addAll(parserObj.A);
		this.costs.addAll(parserObj.costs);
		
		for (String action: this.A) {
			int i = this.A.indexOf(action);
			this.costMap.put(action, this.costs.get(i));
		}
		
		Collections.sort(this.A);
		
		LOGGER.debug("A initialized to " + this.A);
		LOGGER.debug("Costs for A: " + this.costs);
	}
	
	public void initializeRFromParser(ParseSPUDD parserObj) {
		/*
		 * Initializes the Action space from parser
		 */
		this.R = parserObj.R;
		LOGGER.debug("R initialized to " + this.R);
	}
	
//	public void initializeActionsFromParser(ParseSPUDD parserObj) {
//		/*
//		 * Initializes the dynamics of the POMDP
//		 */
//		
//		nActions = parserObj.actTransitions.size();
//		actions = new Action[nActions];
//		uniquePolicy = new boolean[nActions];
//
//		qFn = new DD[nActions];
//
//		for (int a = 0; a < nActions; a++) {
//			
//			actions[a] = new Action(parserObj.actNames.get(a));
//			actions[a].addTransFn(parserObj.actTransitions.get(a));
//			actions[a].addObsFn(parserObj.actObserve.get(a));
//			actions[a].rewFn = 
//					OP.sub(parserObj.reward, parserObj.actCosts.get(a));
//			actions[a].buildRewTranFn();
//			actions[a].rewFn = 
//					OP.addMultVarElim(actions[a].rewTransFn, primeVarIndices);
//			
//		}
//		
//		/*
//		 * Max and Min reward
//		 */
//		float maxVal = Double.NEGATIVE_INFINITY;
//		float minVal = Double.POSITIVE_INFINITY;
//		
//		for (int a = 0; a < nActions; a++) {
//			maxVal = Math.max(maxVal, OP.maxAll(OP.addN(actions[a].rewFn)));
//			minVal = Math.min(minVal, OP.minAll(OP.addN(actions[a].rewFn)));
//		}
//		
//		maxRewVal = maxVal / (1 - discFact);
//		
//		/*
//		 * Set Tolerance
//		 */
//		if (parserObj.tolerance == null) {
//			float maxDiffRew = maxVal - minVal;
//			float maxDiffVal = maxDiffRew / (1 - Math.min(0.95, discFact));
//			tolerance = 1e-5 * maxDiffVal;
//		} 
//		
//		else tolerance = parserObj.tolerance.getVal();
//	}
	
	public void initializeDiscountFactorFromParser(ParseSPUDD parserObj) {
		/*
		 * Sets the discount value for the POMDP
		 */
		
		discFact = parserObj.discount.getVal();

		/*
		 *  make a DD version
		 */
		ddDiscFact = DDleaf.getDD((float) discFact);
	}
	
	public void initializeAdjunctsFromParser(ParseSPUDD parserObj) {
		/*
		 * Set adjunct beliefs
		 */
		
		this.adjunctBeliefs.addAll(parserObj.adjunctBeliefs);
		LOGGER.debug("Adjunct beliefs set to " + this.adjunctBeliefs);
	}
	
	public void initializeBeliefsFromParser(ParseSPUDD parserObj) {
		/*
		 * Set beliefs
		 */
		
		this.initBeliefDdTree = parserObj.initBeliefDdTree;
		LOGGER.debug("Initial belief set to " + this.initBeliefDdTree);
	}
	
	public void initializeToleranceFromParser(ParseSPUDD parserObj) {
		/*
		 * Set Tolerance
		 */ 
		if (parserObj.tolerance != null) this.tolerance = parserObj.tolerance.getVal();
		
		LOGGER.debug("Tolerance set to " + this.tolerance);
	}
	
	// -----------------------------------------------------------------------------------------------
	
	public void setDynamics() {
		/*
		 * Starts building up DDs which define system dynamics based on Ti and Oi
		 */
		
		LOGGER.debug("Setting dynamics");
		
		this.nActions = this.A.size();
		this.actions = new Action[nActions];
		this.uniquePolicy = new boolean[nActions];

		qFn = new DD[nActions];

		for (int a = 0; a < nActions; a++) {
			
			String actName = this.A.get(a);
			actions[a] = new Action(actName);
			
			/* Re order T and O as arrays according to variable sequence */
			HashMap<String, DDTree> Ti_a = this.Ti.get(actName);
			List<DD> TiaArray = this.S.stream()
					.map(s -> OP.reorder(Ti_a.get(s.name).toDD()))
					.collect(Collectors.toList());
			
			HashMap<String, DDTree> Oi_a = this.Oi.get(actName);
			List<DD> OiaArray = this.Omega.stream()
					.map(o -> OP.reorder(Oi_a.get(o.name).toDD()))
					.collect(Collectors.toList());
			
			actions[a].addTransFn(TiaArray.toArray(new DD[TiaArray.size()]));
			actions[a].addObsFn(OiaArray.toArray(new DD[OiaArray.size()]));
			actions[a].rewFn = 
					OP.sub(OP.reorder(this.R.toDD()), OP.reorder(this.costMap.get(actName).toDD()));
			actions[a].buildRewTranFn();
			actions[a].rewFn = 
					OP.addMultVarElim(actions[a].rewTransFn, primeVarIndices);
		}
		
		/*
		 * Max and Min reward
		 */
		float maxVal = Float.NEGATIVE_INFINITY;
		float minVal = Float.POSITIVE_INFINITY;
		
		for (int a = 0; a < nActions; a++) {
			maxVal = Math.max(maxVal, OP.maxAll(OP.addN(actions[a].rewFn)));
			minVal = Math.min(minVal, OP.minAll(OP.addN(actions[a].rewFn)));
		}
		
		maxRewVal = maxVal / (1 - discFact);
		
		/*
		 * Set Tolerance
		 */
		float maxDiffRew = maxVal - minVal;
		float maxDiffVal = maxDiffRew / (1 - Math.min(0.95f, discFact));
		tolerance = 1e-5f * maxDiffVal;
	}
	
	public void setAdjuncts() {
		/*
		 * Populates the adjuncts array from the DDTree list
		 */
		nAdjuncts = this.adjunctBeliefs.size();
		
		if (nAdjuncts > 0) {
			
			adjuncts = new DD[nAdjuncts];
			adjunctNames = new String[nAdjuncts];
			
			for (int a = 0; a < nAdjuncts; a++) {
				adjuncts[a] = OP.reorder(this.adjunctBeliefs.get(a).toDD());
//				adjunctNames[a] = this.adjunctBeliefs.get(a);
				this.initialBeliefs.add(adjuncts[a]);
			}
		}
		
	}
	
	public void setBeliefs() {
		/*
		 * Populates the POMDP DD variables which hold the initial and adjunct beliefs
		 */
		
		this.initialBelState = OP.reorder(this.initBeliefDdTree.toDD());
		this.initialBeliefs.add(initialBelState);
		
		this.currentBelief = this.initialBelState;
		
		/*
		 * factored initial belief state
		 */
		this.initialBelState_f = new DD[nStateVars];
		
		for (int varId = 0; varId < this.nStateVars; varId++) {
			
			initialBelState_f[varId] = 
					OP.addMultVarElim(
							initialBelState, 
							MySet.remove(varIndices, varId + 1));
		}
		
		this.setAdjuncts();
	}
	
	@Override
	public void setGlobals() {
		/*
		 * Sets the globals statics according to the current frame
		 * 
		 * This actually done during initialization. But since the lower frames will overwrite
		 * the globals during their initialization, this method has to be called manually whenever
		 * the current frame is being solved for an IPOMDP
		 */
		
		Global.clearHashtables();
		Global.setVarDomSize(varDomSize);
		Global.setVarNames(varName);

		for (int i = 0; i < nStateVars; i++) {
			Global.setValNames(
					i + 1, 
					stateVars[i].valNames);
		}
		
		for (int i = 0; i < nObsVars; i++) {
			Global.setValNames(
					nStateVars + i + 1, 
					obsVars[i].valNames);
		}
		
		for (int i = 0; i < nStateVars; i++) {
			Global.setValNames(
					nVars + i + 1, 
					stateVars[i].valNames);
		}
		
		for (int i = 0; i < nObsVars; i++) {
			Global.setValNames(
					nVars + nStateVars + i + 1, 
					obsVars[i].valNames);
		}
			
	}
	
	public void commitVariables() {
		/*
		 * Move variables from staging area and populate global variables based on them
		 * 
		 * Ideally, no changes should be made to the state or obs variables after
		 * this method is called. 
		 */
		this.nStateVars = this.S.size();
		this.nObsVars = this.Omega.size();
		
		/*
		 * initialize arrays for domain size and other information
		 */
		this.nVars = this.nStateVars + this.nObsVars;
		this.stateVars = new StateVar[this.nStateVars];
		this.obsVars = new StateVar[this.nObsVars];
		this.varDomSize = new int[2 * (this.nStateVars + this.nObsVars)];
		this.varName = new String[2 * (this.nStateVars + this.nObsVars)];
		this.varIndices = new int[this.nStateVars];
		this.primeVarIndices = new int[this.nStateVars];
		this.obsIndices = new int[this.nObsVars];
		this.primeObsIndices = new int[this.nObsVars];
		
		/*
		 * Make state variables.
		 */
		this.stateVars = 
				this.S.toArray(
						new StateVar[this.S.size()]);
		
		/*
		 * Legacy code to create matlab like indices
		 */
		int k = 0;
		for (int i = 0; i < nStateVars; i++) {

			this.varIndices[i] = i + 1;
			this.primeVarIndices[i] = i + nVars + 1;
			this.varDomSize[k] = stateVars[i].arity;
			this.varName[k++] = stateVars[i].name;
		}
		
		/*
		 * Make observation variables
		 */
		
		this.obsVars = 
				this.Omega.toArray(
						new StateVar[this.Omega.size()]);
		
		/*
		 * Legacy code to set matlab-like indices
		 */
		
		this.nObservations = 1;
		this.obsVarsArity = new int[nObsVars];
		
		for (int i = 0; i < nObsVars; i++) {
			
			this.obsVarsArity[i] = obsVars[i].arity;
			this.nObservations = nObservations * obsVars[i].arity;
			this.obsIndices[i] = i + nStateVars + 1;
			this.primeObsIndices[i] = i + nVars + nStateVars + 1;
			this.varDomSize[k] = obsVars[i].arity;
			this.varName[k++] = obsVars[i].name;
		}
		
		/*
		 * More legacy code to set up primed variables
		 */
		
		for (int i = 0; i < nStateVars; i++) {
			
			varDomSize[k] = stateVars[i].arity;
			varName[k++] = stateVars[i].name + "_P";
		}
		
		for (int i = 0; i < nObsVars; i++) {
			
			varDomSize[k] = obsVars[i].arity;
			varName[k++] = obsVars[i].name + "_P";
		}
		
		setGlobals();
		
		/* In the end, add all variables to the DDMaker and prime them */
		this.ddMaker.clearContext();
		this.S.stream().forEach(s -> this.ddMaker.addVariable(s.name, s.valNames));
		this.Omega.stream().forEach(o -> this.ddMaker.addVariable(o.name, o.valNames));
		this.ddMaker.primeVariables();
		
		LOGGER.debug("Context belongs to frame " + this.frameID + " at level " + this.level);
	}
	
	// -----------------------------------------------------------------------------------------------

	public void readFromFile(String fileName, boolean debb) {
		/*
		 * Read the POMDP directly from a domain file
		 */
		ParseSPUDD rawpomdp = new ParseSPUDD(fileName);
		rawpomdp.parsePOMDP(false);

		this.initializeFromParsers(rawpomdp);
		
		this.bOPs = new BeliefOps(this);
	}
	
	public int findObservationByName(int ob, String oname) {
		for (int o = 0; o < obsVars[ob].arity; o++) {
			if (oname.equalsIgnoreCase(obsVars[ob].valNames[o]))
				return o;
		}
		return -1;
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
		obj_out.flush();
		obj_out.close();
	}

	public static POMDP load(String filename) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(
				filename));
		POMDP pomdp = (POMDP) input.readObject();
		input.close();
		
		return pomdp;
	}

	public static int[] statedecode(int statenum, int n) {
		int[] bases = new int[n];
		for (int i = 0; i < n; i++)
			bases[i] = 2;
		return statedecode(statenum, n, bases);
	}

	public static int[] statedecode(int statenum, int n, int[] bases) {
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
	
	// -------------------------------------------------------------------------------
	
	public HashMap<String, HashMap<String, DDTree>> getOi() {
		/*
		 * Decouples the observation function DDs from Globals and returns a
		 * general representation using DDTree
		 */
		return this.Oi;
	}
	
	@Override
	public void step(
			DD belief, 
			String action, 
			String[] obs) throws 
	
	ZeroProbabilityObsException, 
	VariableNotFoundException {
		
		/*
		 * Performs a static belief update from current belief by taking action and observing obs
		 * 
		 * The belief space is transformed according to the next belief. 
		 */
		
		LOGGER.info("Taking action " + action + "\r\n"
				+ " at belief " + this.toMap(belief) + "\r\n"
				+ " with observation " + Arrays.toString(obs));
		
		/* perform belief update */
		DD nextBelief = 
				this.beliefUpdate( 
						belief, 
						action, 
						obs);
		
		LOGGER.debug("Next belief is " + this.toMap(nextBelief));
		
		this.currentBelief = nextBelief;
	}
	
	// ----------------------------------------------------------------------------------
	
	private void recursiveObsGen(
			List<List<String>> obsComb, 
			List<StateVar> obsVars, 
			List<String> obsVector, 
			int finalLen, 
			int varIndex){
		/* 
		 *  Recursively generates a list of all possible combinations of values 
		 *  of the observation variables
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
	
	public List<List<String>> recursiveObsCombinations(List<StateVar> obsVars){
		/*
		 * Driver program for generating observations recursively
		 */
		int finalLen = obsVars.size();
		List<String> obsVec = new ArrayList<String>();
		List<List<String>> obsComb = new ArrayList<List<String>>();
		
		recursiveObsGen(obsComb, obsVars, obsVec, finalLen, 0);
		
		return obsComb;
	} // private List<List<String>> recursiveObsCombinations
	
	public void computeAllPossibleObsCombinations() {
		/*
		 * Populates the obsCombinations and obsCombinationsIndices properties of
		 * the POMDP
		 */
		
		this.obsCombinationsIndices = new int[nObservations][nObsVars];
		
		/* first populate the indices */
		for (int i = 0; i < nObservations; i++) {
			this.obsCombinationsIndices[i] = 
					POMDP.statedecode(i + 1, this.nObsVars, this.obsVarsArity);
		}
		
		LOGGER.debug("Obs Combination Indices: " 
				+ Arrays.deepToString(this.obsCombinationsIndices));
		
		for (int o = 0; o < this.obsCombinationsIndices.length; o++) {
			
			List<String> obs = new ArrayList<String>();
			
			for (int oVar = 0; oVar < this.obsCombinationsIndices[o].length; oVar++) {
				obs.add(this.obsVars[oVar].valNames[this.obsCombinationsIndices[o][oVar] - 1]);
			}
			
			this.obsCombinations.add(obs);
		}
		
		LOGGER.debug("Obs Combinations: " + this.obsCombinations);
	}
	
	@Override
	public List<List<String>> getAllPossibleObservations() {
		return this.obsCombinations;
	}
	
	// -------------------------------------------------------------------------------------------------
	
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

	// -------------------------------------------------------------------------------
	
	@Override
	public List<String> getActions() {
		return Arrays.stream(this.actions).map(a -> a.name).collect(Collectors.toList());
	}
	
	@Override
	public List<String> getStateVarNames() {
		return this.S.stream().map(s -> s.name).collect(Collectors.toList());
	}
	
	@Override
	public List<String> getObsVarNames() {
		return this.Omega.stream().map(o -> o.name).collect(Collectors.toList());
	}
	
	@Override
	public List<DD> getInitialBeliefs() {
		return this.initialBeliefs;
	}
	
	@Override
	public int[] getStateVarIndices() {
		return this.varIndices;
	}
	
	@Override
	public DD getCurrentBelief() {
		/*
		 * In case of online solvers, the POMDP will have to maintain a reference
		 * to its current belief
		 */
		return this.currentBelief;
	}
	
	@Override
	public int[] getObsVarIndices() {
		return this.obsIndices;
	}
	
	@Override
	public String getType() {
		return "POMDP";
	}
	
	@Override
	public String getBeliefString(DD belief) {
		/*
		 * Returns current belief as a string
		 * 
		 * Mostly useful printing out the beliefs for policy graphs and trees
		 */
		
		/* initialize JSON handler */
		Gson gsonHandler = 
				new GsonBuilder()
					.disableHtmlEscaping()
					.create();
		
		/* get belief hashmap */
		HashMap<String, HashMap<String, Float>> map = this.toMap(belief);
		
		/* create JSON container */
		JsonObject jsonObject = new JsonObject();
		
		/* add the states */
		for (String key : map.keySet()) {
			jsonObject.add(key, gsonHandler.toJsonTree(map.get(key)));
		}
		
		return gsonHandler.toJson(jsonObject);
	}
	
	@Override
	public DD getRewardFunctionForAction(String action) {
		/*
		 * Returns the reward function for the given action
		 */
		
		int actId = this.getActions().indexOf(action);
		
		return this.actions[actId].rewFn;
		
	}
	
	@Override
	public void setTi(String action, DD[] Ti) {
		/*
		 * Sets the given Ti as the transition function for the action
		 */
		
		int actId = this.getActions().indexOf(action);
		
		this.actions[actId].addTransFn(Ti);
		this.actions[actId].buildRewTranFn();
		this.actions[actId].rewFn = 
				OP.addMultVarElim(
						actions[actId].rewTransFn, 
						primeVarIndices);
	}
	
	// -------------------------------------------------------------------------------

	@Override
	public String toString() {
		return "POMDP [frameID=" + frameID + ", level=" + level + 
				", nStateVars=" + nStateVars + ", nObsVars=" + nObsVars + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + frameID;
		result = prime * result + level;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		POMDP other = (POMDP) obj;
		if (frameID != other.frameID)
			return false;
		if (level != other.level)
			return false;
		return true;
	}

	@Override
	public DD[] getTiForAction(String action) {
		
		return this.actions[this.getActions().indexOf(action)].transFn;
	}
	
	@Override
	public DD[] getOiForAction(String action) {
		
		return this.actions[this.getActions().indexOf(action)].obsFn;
	}

	@Override
	public int[] getStateVarPrimeIndices() {
		// TODO Auto-generated method stub
		return this.primeVarIndices;
	}

	@Override
	public int[] getObsVarPrimeIndices() {
		// TODO Auto-generated method stub
		return this.primeObsIndices;
	}
	
	@Override
	public int getNumVars() {
		return this.nStateVars + this.nObsVars;
	}
	
	@Override
	public float evaluatePolicy(
			DD[] alphaVectors, int[] policy, int trials, int evalDepth, boolean verbose) {
		/*
		 * Run given number of trials of evaluation upto the given depth and return
		 * average reward
		 * 
		 * ****** This is completely based on Hoey's evalPolicyStationary function
		 */
		
		List<Float> rewards = new ArrayList<Float>();
		DDTree currentBeliefTree = this.getCurrentBelief().toDDTree();
		
		try {
			
			Global.clearHashtables();
			
			for (int n = 0; n < trials; n++) {
				DD currentBelief = currentBeliefTree.toDD();
				
				float totalReward = 0.0f;
				float totalDiscount = 1.0f;
				
				int[][] stateConfig = OP.sampleMultinomial(currentBelief, this.getStateVarIndices());
				
				for (int t = 0; t < evalDepth; t++) {
					
					String action = 
							DecisionProcess.getActionFromPolicy(
									this, currentBelief, alphaVectors, policy);
					
					if (verbose) {
						LOGGER.debug("Best Action in state " 
								+ Arrays.toString(POMDP.configToStrings(stateConfig)) + " "
								+ "is " + action);
					}
					
					/* evaluate action */
					float currentReward = OP.eval(this.getRewardFunctionForAction(action), stateConfig);
					totalReward = totalReward + (totalDiscount * currentReward);
					totalDiscount = totalDiscount * this.discFact;
					
					/* get next state and generate observation */
					DD[] TforS = OP.restrictN(this.getTiForAction(action), stateConfig);
					int[][] nextStateConfig = OP.sampleMultinomial(TforS, this.getStateVarPrimeIndices());
					
					DD[] obsDist = 
							OP.restrictN(
									this.getOiForAction(action), 
									POMDP.concatenateArray(stateConfig, nextStateConfig));
					
					/* sample obs from distribution */
					int[][] obsConfig = 
							OP.sampleMultinomial(
									obsDist, 
									this.getObsVarPrimeIndices());
					
					String[] obs = POMDP.configToStrings(obsConfig);
					
					currentBelief = this.beliefUpdate(currentBelief, action, obs);
					stateConfig = Config.primeVars(nextStateConfig, -this.getNumVars());
				}
				
				if (verbose)
					LOGGER.debug("Run: " + n + " total reward is " + totalReward);
				
				if ((n % 1000) == 0)
					LOGGER.debug("Finished " + n + " trials,"
							+ " avg. reward is: " 
							+ rewards.stream().mapToDouble(r -> r).average().orElse(Double.NaN));
				
				rewards.add(totalReward);
			}
			
			Global.clearHashtables();
		}
		
		catch (ZeroProbabilityObsException o) {
			LOGGER.error("Evaluation crashed: " + o.getMessage());
			return -1;
		}
		
		catch (Exception e) {
			LOGGER.error("Evaluation crashed: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
			
			return -1;
		}
		
		float avgReward = (float) rewards.stream().mapToDouble(r -> r).average().getAsDouble();
		return avgReward;
	}

	@Override
	public float evaluateDefaultPolicy(
			String defaultAction, int trials, int evalDepth, boolean verbose) {
		
		List<Float> rewards = new ArrayList<Float>();
		DDTree currentBeliefTree = this.getCurrentBelief().toDDTree();
		
		try {
			
			Global.clearHashtables();
			
			for (int n = 0; n < trials; n++) {
				DD currentBelief = currentBeliefTree.toDD();
				
				float totalReward = 0.0f;
				float totalDiscount = 1.0f;
				
				int[][] stateConfig = OP.sampleMultinomial(currentBelief, this.getStateVarIndices());
				
				for (int t = 0; t < evalDepth; t++) {
					
					String action = defaultAction;
					
					if (verbose) {
						LOGGER.debug("Taking default action in state " 
								+ Arrays.toString(POMDP.configToStrings(stateConfig)) + " "
								+ "is " + action);
					}
					
					/* evaluate action */
					float currentReward = OP.eval(this.getRewardFunctionForAction(action), stateConfig);
					totalReward = totalReward + (totalDiscount * currentReward);
					totalDiscount = totalDiscount * this.discFact;
					
					/* get next state and generate observation */
					DD[] TforS = OP.restrictN(this.getTiForAction(action), stateConfig);
					int[][] nextStateConfig = OP.sampleMultinomial(TforS, this.getStateVarPrimeIndices());
					
					DD[] obsDist = 
							OP.restrictN(
									this.getOiForAction(action), 
									POMDP.concatenateArray(stateConfig, nextStateConfig));
					
					/* sample obs from distribution */
					int[][] obsConfig = 
							OP.sampleMultinomial(
									obsDist, 
									this.getObsVarPrimeIndices());
					
					String[] obs = POMDP.configToStrings(obsConfig);
					
					currentBelief = this.beliefUpdate(currentBelief, action, obs);
					stateConfig = Config.primeVars(nextStateConfig, -this.getNumVars());
				}
				
				if (verbose)
					LOGGER.debug("Run: " + n + " total reward is " + totalReward);
				
				if ((n % 1000) == 0)
					LOGGER.debug("Finished " + n + " trials,"
							+ " avg. reward is: " 
							+ rewards.stream().mapToDouble(r -> r).average().orElse(Double.NaN));
				
				rewards.add(totalReward);
			}
			
			Global.clearHashtables();
		}
		
		catch (ZeroProbabilityObsException o) {
			LOGGER.error("Evaluation crashed: " + o.getMessage());
			return -1;
		}
		
		catch (Exception e) {
			LOGGER.error("Evaluation crashed: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
			
			return -1;
		}
		
		float avgReward = (float) rewards.stream().mapToDouble(r -> r).average().getAsDouble();
		return avgReward;
	}

	@Override
	public float evaluateRandomPolicy(int trials, int evalDepth, boolean verbose) {
		
		List<Float> rewards = new ArrayList<Float>();
		DDTree currentBeliefTree = this.getCurrentBelief().toDDTree();
		
		try {
			
			Global.clearHashtables();
			
			for (int n = 0; n < trials; n++) {
				DD currentBelief = currentBeliefTree.toDD();
				
				float totalReward = 0.0f;
				float totalDiscount = 1.0f;
				
				int[][] stateConfig = OP.sampleMultinomial(currentBelief, this.getStateVarIndices());
				
				for (int t = 0; t < evalDepth; t++) {
					
					Random rnd = new Random();
					String action = this.getActions().get(rnd.nextInt(this.getActions().size()));
					
					if (verbose) {
						LOGGER.debug("Taking random action in state " 
								+ Arrays.toString(POMDP.configToStrings(stateConfig)) + " "
								+ "is " + action);
					}
					
					/* evaluate action */
					float currentReward = OP.eval(this.getRewardFunctionForAction(action), stateConfig);
					totalReward = totalReward + (totalDiscount * currentReward);
					totalDiscount = totalDiscount * this.discFact;
					
					/* get next state and generate observation */
					DD[] TforS = OP.restrictN(this.getTiForAction(action), stateConfig);
					int[][] nextStateConfig = OP.sampleMultinomial(TforS, this.getStateVarPrimeIndices());
					
					DD[] obsDist = 
							OP.restrictN(
									this.getOiForAction(action), 
									POMDP.concatenateArray(stateConfig, nextStateConfig));
					
					/* sample obs from distribution */
					int[][] obsConfig = 
							OP.sampleMultinomial(
									obsDist, 
									this.getObsVarPrimeIndices());
					
					String[] obs = POMDP.configToStrings(obsConfig);
					
					currentBelief = this.beliefUpdate(currentBelief, action, obs);
					stateConfig = Config.primeVars(nextStateConfig, -this.getNumVars());
				}
				
				if (verbose)
					LOGGER.debug("Run: " + n + " total reward is " + totalReward);
				
				if ((n % 100) == 0)
					LOGGER.debug("Finished " + n + " trials,"
							+ " avg. reward is: " 
							+ rewards.stream().mapToDouble(r -> r).average().orElse(Double.NaN));
				
				rewards.add((float) totalReward);
			}
			
			Global.clearHashtables();
		}
		
		catch (ZeroProbabilityObsException o) {
			LOGGER.error("Evaluation crashed: " + o.getMessage());
			return -1;
		}
		
		catch (Exception e) {
			LOGGER.error("Evaluation crashed: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
			
			return -1;
		}
		
		float avgReward = (float) rewards.stream().mapToDouble(r -> r).average().getAsDouble();
		return avgReward;
	}
}
