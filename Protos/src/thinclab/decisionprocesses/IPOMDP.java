/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.decisionprocesses;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.belief.InteractiveBelief;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.ddinterface.DDMaker;
import thinclab.ddinterface.DDTree;
import thinclab.exceptions.ParserException;
import thinclab.exceptions.SolverException;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.legacy.StateVar;
import thinclab.parsers.IPOMDPParser;
import thinclab.parsers.ParseSPUDD;
import thinclab.representations.MJ;
import thinclab.representations.MultiFrameMJ;
import thinclab.solvers.OfflineSymbolicPerseus;

/*
 * @author adityas
 *
 */
public class IPOMDP extends POMDP {

	private static final long serialVersionUID = 4973485302724576384L;
	private static final Logger logger = Logger.getLogger(IPOMDP.class);
	
	/*
	 * Reference to Parser object and info extract from parser
	 */
	public IPOMDPParser parser;
	public List<String> Ai = new ArrayList<String>();
	public HashMap<String, List<String>> Ajs = new HashMap<String, List<String>>();
	public List<String> OmegaJNames = new ArrayList<String>();
	public HashMap<Integer, List<String>> OmegaJs = new HashMap<Integer, List<String>>(); 
	
	/*
	 * Store lower level frames
	 */
	public List<POMDP> lowerLevelFrames = new ArrayList<POMDP>();
	
	/*
	 * Store a local reference to OpponentModel object to get easier access to node
	 * indices and all that
	 */
	public MJ Mj;
	public MultiFrameMJ multiFrameMJ;
	
	/*
	 * We will need to know the varIndex for the opponentModel statevar to replace it after
	 * every belief update
	 */
	public int oppModelVarIndex;
	public int AjStartIndex = -1;
	public int AjVarStartPosition;
	public int AjSize = 0;
	
	/* Mj's transition DD */
	public DD MjTFn;
	
	/* actions costs stored locally to avoid storing the full parser object */
	private HashMap<String, DDTree> costMap;
	
	/* Staging area for j's observation functions */
	public HashMap<String, HashMap<String, HashMap<String, DDTree>>> OjTheta = 
			new HashMap<String, HashMap<String, HashMap<String, DDTree>>>();
		
	/*
	 * Variables to decide Opponent Model depth
	 */
	public int mjDepth;
	public int mjLookAhead;
	
	/*
	 * Variables for current look ahead horizon
	 */
	public DD currentMjTfn;
	public DD currentTau;
	public DD[] currentAjGivenMj;
	public DD currentBelief = null;
	
	public HashMap<String, DD[]> currentOi;
	public HashMap<String, DD[]> currentTi;
	public HashMap<String, DD> currentRi;
	public HashMap<Integer, DD[]> currentOj;
	
	public List<DDTree> currentStateBeliefs = new ArrayList<DDTree>();
	
	/*
	 * generate a list of all possible observations and store it to avoid
	 * computing it repeatedly during belief tree expansions.
	 * Same for J's actions
	 */
	public List<List<String>> obsCombinations;
	public List<List<String>> actionCombinations;
	
	/*
	 * Arrays to record current IS var indices
	 */
	public int[] stateVarIndices;
	public int[] stateVarPrimeIndices;
	public int[] obsIVarIndices;
	public int[] obsJVarIndices;
	public int[] obsIVarPrimeIndices;
	public int[] obsJVarPrimeIndices;
	
	/*
	 * Hard coded tolerance
	 */
	public double tolerance = 0.01;
	
	// ----------------------------------------------------------------------------------------
	
	public IPOMDP(IPOMDPParser parsedFrame, int mjMaxDepth, int mjlookAhead) {
		/*
		 * Initialize from a IPOMDPParser object
		 */
		
		try {
			
			logger.info("Initializing IPOMDP from parser.");
			
			this.initializeFromParsers(parsedFrame);
			this.setMjDepth(mjMaxDepth);
			this.setMjLookAhead(mjlookAhead);
			
			logger.info("IPOMDP initialized");
			
			/* Solve opponent model and create internal representation */
			this.solveMj();
			
			/* initialize all DDs */
			this.updateMjInIS();
			this.initializeOfflineFunctions();
			this.reinitializeOnlineFunctions();
			
		}
		
		catch (Exception e) {
			logger.error("While parsing " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public IPOMDP(String fileName) {
		super(fileName);
		logger.info("IPOMDP initialised from file: " + fileName);
	}
	
	public IPOMDP() {
		super();
		logger.info("IPOMDP initialised");
	}
	
	// -----------------------------------------------------------------------------------------
	
	public void initializeFromParsers(IPOMDPParser parsedFrame) throws ParserException {
		/*
		 * Initializes the IPOMDP from the thinclab.ipomdpsolver.IPOMDPParser object
		 */
		
		/* Store parser obj reference for future access */
		this.parser = parsedFrame;
		logger.debug("Parser reference stored");
		
		/*
		 * Initialize each child frame
		 */
		for (int i=0; i < parsedFrame.childFrames.size(); i++) {
			
			ParseSPUDD parsedLowerFrame = parsedFrame.childFrames.get(i);
			POMDP lowerFrame;
			
			/*
			 * If lower frame is IPOMDPParser, initialize an IPOMDP for the lower frame,
			 * else, initialize a POMDP
			 */
			if (parsedLowerFrame instanceof IPOMDPParser) lowerFrame = new IPOMDP();
				
			else if (parsedLowerFrame instanceof ParseSPUDD) lowerFrame = new POMDP();
			
			else throw new ParserException("Parser object at " + i + " is not a POMDP or an IPOMDP");
			
			/*
			 * Populate lower frame from the parser object and add it to the set of child frames.
			 */
			lowerFrame.initializeFromParsers(parsedLowerFrame);
			this.lowerLevelFrames.add(lowerFrame);
			logger.debug("Lower frame " + i + "parsed and stored.");
			
		} /* for all child frames */
		
		/* Initialize vars from the domain file */
		this.initializeSFromParser(this.parser);
		
		/* Find the index of M_j */
		this.oppModelVarIndex = this.S.size();
		
		/* Put a dummy state var for M_j */
		this.S.add(new StateVar(1, "M_j", this.oppModelVarIndex));
		
		this.initializeOmegaFromParser(this.parser);
		this.setUpOmegaJ();
		
		this.initializeAFromParser(this.parser);
		
		/* Split actions to Ai and Aj */
		this.A.forEach(a -> {	
			if (!this.Ai.contains(a.split("__")[0]))
				this.Ai.add(a.split("__")[0]);
			});
		
		for (DecisionProcess lowerFrame : this.lowerLevelFrames) {
			
			List<String> AjForCurrentFrame = new ArrayList<String>();
			
			AjForCurrentFrame.addAll(lowerFrame.getActions());
			this.Ajs.put("A_j/" + lowerFrame.frameID, AjForCurrentFrame);
			
			/* Add Aj as a stateVar */
			this.S.add(
					new StateVar(
							"A_j/" + lowerFrame.frameID, 
							this.S.size(),
							AjForCurrentFrame.toArray(
									new String[AjForCurrentFrame.size()])));
			
			if (this.AjStartIndex == -1) {
				this.AjStartIndex = this.S.size();
				this.AjVarStartPosition = this.S.size() - 1;
			}
			
			this.AjSize += 1;
		}
		
		this.uniquePolicy = new boolean[this.Ai.size()]; 
		this.initializeTFromParser(this.parser);
		this.initializeOFromParser(this.parser);
		this.initializeRFromParser(this.parser);
		
		this.initializeDiscountFactorFromParser(this.parser);		
		this.initializeBeliefsFromParser(this.parser);
		
		this.currentStateBeliefs.add(this.initBeliefDdTree);
		this.currentStateBeliefs.addAll(this.adjunctBeliefs);
		
		this.obsCombinations = 
				super.recursiveObsCombinations(
						this.Omega.subList(0, this.Omega.size() - this.OmegaJNames.size()));
		this.actionCombinations =
				super.recursiveObsCombinations(
						this.S.subList(
								this.AjVarStartPosition, 
								this.AjVarStartPosition + this.AjSize));
		
		this.costMap = this.parser.costMap;
		
		/* unroll all actions defined through wildcards */
		this.unRollWildCards();
		
		/* Null parser reference after parsing is done */
		this.parser = null;
		
		/*
		 * Set level and frame manually
		 * 
		 * TODO: recursively parse frames to assign IPOMDP frames automatically
		 */
		this.frameID = 0;
		this.level = 1;
	}
	
	public void setAi(List<String> actionNames) {
		/*
		 * Sets the names for agent i's actions
		 */
		this.Ai = actionNames;
	}
	
//	public void setAj(List<String> actionNames) {
//		/*
//		 * Sets the names for agent j's actions
//		 */
//		this.Aj = actionNames;
//	}
	
	public void setMjDepth(int depth) {
		/*
		 * Sets the mjDepth property
		 */
		this.mjDepth = depth;
	}
	
	public void setMjLookAhead(int horizon) {
		/*
		 * Sets the horizon for OpponentModel look ahead. 
		 */
		this.mjLookAhead = horizon;
	}
	
	private void unRollWildCards() {
		/*
		 * The domain file can have wild cards to specify multiple transitions in a single action
		 * def using wild cards. This methods expands and unrolls them
		 */
		
		for (String ADef : this.A) {
			
			String[] actionDefs = ADef.split("__");
			
			if (actionDefs[actionDefs.length - 1].contentEquals("ALL")) {
				
				/* create a list of actions to be unrolled */
				List<StateVar> actions = 
						this.S.subList(
								AjVarStartPosition + actionDefs.length - 2, 
								AjVarStartPosition + this.AjSize);
				
				logger.debug("Found wildcard actionDef " + ADef 
						+ " actions to be unrolled are " + actions);
				
				/*
				 * get all action value combinations and apply the DD associated with actionDef to
				 * all of them.
				 * 
				 * Reusing the same function used to make observation combinations for creating 
				 * joint action combinations.
				 */
				List<List<String>> allActions = this.recursiveObsCombinations(actions);
				
				for (List<String> actionCombination : allActions) {
					
					String[] actionPath = 
							ArrayUtils.addAll(
									ArrayUtils.subarray(
											actionDefs, 
											0, actionDefs.length - 1), 
									actionCombination.toArray(
											new String[actionCombination.size()]));
					
					if (this.A.contains(String.join("__", actionPath))) continue;
					
					logger.debug("Applying DDTree for " + ADef + " to " 
							+ Arrays.toString(actionPath));
					
					this.Ti.put(String.join("__", actionPath), this.Ti.get(ADef));
					this.Oi.put(String.join("__", actionPath), this.Oi.get(ADef));
					this.costMap.put(String.join("__", actionPath), this.costMap.get(ADef));
				}
				
			}
		}
	}
	
	// ------------------------------------------------------------------------------------------
	
	public void solveMj() throws SolverException {
		/*
		 * Calls IPBVI or PBVI on the lower level frames depending on whether they are IPOMDPs
		 * or POMDPs
		 * 
		 * WARNING: Only work for a single frame
		 */
		
		List<MJ> solvedFrames = new ArrayList<MJ>();
		
		/*
		 * Check if lower frame is POMDP or IPOMDP and call the solve method accordingly
		 */
		for (DecisionProcess mj : this.lowerLevelFrames) {
		
			Global.clearHashtables();
			
			if (mj.level > 0) ((IPOMDP) mj).solveIPBVI(15, 100);
			
			else if (mj.level == 0) {
				
				/* For solving the POMDP at lowest level, set the globals */
				mj.setGlobals();
				
				OfflineSymbolicPerseus solver = 
						new OfflineSymbolicPerseus(
								(POMDP) mj, 
								new SSGABeliefExpansion((POMDP) mj, 100, 1), 
								10, 100);
				
				/* modification for new solver API */
				solver.solve();
				logger.debug("Solved lower frame " + mj);
				
				/*
				 * NOTE: After this point, extract all the required information
				 * from the lower level frame. The frame should not be accessed after
				 * exiting this function because the Global arrays will be changed.
				 */
				
				/* store opponent's Oj */
				HashMap<String, HashMap<String,DDTree>> oj = ((POMDP) mj).getOi();
				
				/* append frameID to obsVars */
				for (String ajName : new ArrayList<String>(oj.keySet())) {
					
					/* rename action vars */
					HashMap<String, DDTree> o = oj.remove(ajName);
					
					/* rename objs vars and DDs */
					for (String oldOj : new ArrayList<String>(o.keySet())) {
						
						DDTree dd = o.remove(oldOj);
						dd.renameVar(oldOj + "'", mj.getCanonicalName(oldOj + "'"));
						
						o.put(mj.getCanonicalName(oldOj), dd);
					}
					
					oj.put(ajName, o);
				}
				
				this.OjTheta.put("A_j/" + mj.frameID, oj);
				logger.debug("Extracted Oj for " + mj);
				
				/* Make Opponent Model object */
				solvedFrames.add(new MJ(solver, this.mjLookAhead));
			}
			
			else 
				throw new SolverException("Frame " + 
					this.lowerLevelFrames.indexOf(mj) + 
					" is not a POMDP or IPOMDP");
		
		}
		
		/* Rename extracted functions */
		this.renameOjDDTrees();
		
		logger.info("Solved lower frames");
		
		/* initialize MJ */
		this.multiFrameMJ = new MultiFrameMJ(solvedFrames);
		
		/* make all obs J combinations */
		List<StateVar> obsJVars = 
				this.Omega.stream()
					.filter(i -> this.OmegaJNames.contains(i.name))
					.collect(Collectors.toList());
		
		this.multiFrameMJ.makeAllObsCombinations(obsJVars);
	}
	
	public void solveIPBVI(int rounds, int numDpBackups) {
		/*
		 * Runs the interactive PBVI loop for solving the IPOMDP
		 */		
	}
	
	// ------------------------------------------------------------------------------------------
	
	public void setUpMj() {
		/*
		 * Constructs opponents' models 
		 * 
		 * We are assuming the physical states S to be independent of the belief 
		 * over opponents' beliefs. So the opponents' model M will be a separate state
		 * variable in addition to the physical states S.
		 */
		
		/*
		 * Use i's parsed state variables for physical states S. For M_j, use OpponentModel
		 * API 
		 * 
		 * TODO: in case of IPOMDP frames, remove lower level agent models from the
		 * state var staging list before using it to create state vars for current level.
		 */
		
		/* Finally, add the oppModel state var to the staging list */
		this.S.add(this.Mj.getOpponentModelStateVar(this.oppModelVarIndex));
	}
	
	public void setUpOmegaJ() {
		/*
		 * Adds observation for agent j
		 * 
		 * WARNING: Currently only works for a single frame.
		 */
		
		logger.debug("Staging obs vars for j");
		
		for (POMDP frame : this.lowerLevelFrames) {
			List<StateVar> obsj = Arrays.asList(frame.obsVars);
			obsj.stream().forEach(o -> o.setName(frame.getCanonicalName(o.name) + "_j"));
			this.Omega.addAll(obsj);
			
			this.OmegaJNames.addAll(obsj.stream().map(oj -> oj.name).collect(Collectors.toList()));
			
			if (this.OmegaJs.get(frame.frameID) == null)
				this.OmegaJs.put(frame.frameID, new ArrayList<String>());
			
			this.OmegaJs.get(frame.frameID).addAll(
					obsj.stream().map(oj -> oj.name).collect(Collectors.toList()));
		}
			
		/* Offset obsVarIndices */
		int nStateVars = this.S.size();
		
		IntStream.range(
				nStateVars, 
				nStateVars + this.Omega.size())
					.forEach(i -> this.Omega.get(i - nStateVars).setId(i));
		
		this.renameOjDDTrees();
	}
	
	// -------------------------------------------------------------------------------------

	public DD makeOpponentModelTransitionDD() {
		/*
		 * Construct Mj transition DD from OpponentModel triples 
		 */
		
		logger.debug("Making M_j transition DD");
		
		/* Make DDMaker */
		DDMaker ddMaker = new DDMaker();
		
		StateVar MjVar = this.S.get(this.oppModelVarIndex);
		ddMaker.addVariable(
				MjVar.name, 
				MjVar.valNames);
		
		ddMaker.addVariable(
				"A_j", 
				this.Aj.toArray(new String[this.Aj.size()]));
		
		/*
		 * Add obsVars.
		 * 
		 * NOTE: This assumes that the observation space and variables sequence is the 
		 * same for all frames. So we will just get the obsVars from the first lower frame.
		 * If the assumption is not true, this method will break. 
		 * 
		 * TODO: Implement a more general Mj transition DD for frames with different observation
		 * spaces.
		 */

		List<StateVar> obsSeq = 
				this.Omega.stream()
					.filter(o -> o.name.substring(o.name.length()-2).contains("_j"))
					.collect(Collectors.toList());
		
		obsSeq.stream().forEach(v -> ddMaker.addVariable(v.name, v.valNames));
		
		ddMaker.primeVariables();
		
		/* Make variables sequence for DDMaker */
		List<String> varSequence = new ArrayList<String>();
		varSequence.add("M_j");
		varSequence.add("A_j");
		varSequence.addAll(
				obsSeq.stream()
					.map(o -> o.name + "'").collect(Collectors.toList()));
		varSequence.add("M_j'");
		
		/* Get triples */
		String[][] triples = this.Mj.getMjTransitionTriples();
		
		/* Get the DDTree from the DDMaker */
		DDTree MjDD = ddMaker.getDDTreeFromSequence(
				varSequence.toArray(new String[varSequence.size()]), 
				triples);
		
		DD MjTFn = OP.reorder(MjDD.toDD());
		
		logger.debug("f(Mj', Mj, Oj', Aj) contains variables " + Arrays.toString(MjTFn.getVarSet()));
		
		return MjTFn;
	}
	
	public HashMap<String, DD[]> makeOi() {
		/*
		 * Constructs agent i's observation function Oi from extracted DDTree representation
		 * 
		 */

		/*
		 * The parsed Oi is in the form of a joint action observation function of the form -
		 * (Ai - Aj) -> O -> f(O', S')
		 * 
		 * We need to map that to the form Ai -> f(O', Mj, S')
		 */
		logger.debug("Making Oi");
		
		HashMap<String, DD[]> Oi = 
				new HashMap<String, DD[]>();
		
		List<String> O = 
				this.Omega.stream()
					.filter(s -> !s.name.contains("_j"))
					.map(f -> f.name)
					.collect(Collectors.toList());
		
		/* For each action */
		for (String Ai : this.Ai) {

			DD[] ddTrees = new DD[O.size()];
			
			for (String o : O) {
				
				DDTree ajDDTree = 
						this.ddMaker.getDDTreeFromSequence(
								this.S.subList(
										this.AjVarStartPosition, 
										this.AjVarStartPosition + this.AjSize).stream()
											.map(s -> s.name)
											.toArray(String[]::new));
				
				/* for all combinations of J's actions */
				for (List<String> actionCombination : this.actionCombinations) {
					
					String ajPath = String.join("__", actionCombination);
					
					DDTree oiGivenaj = this.Oi.get(Ai + "__" + ajPath).get(o);
					
					try {
						/* avoid passing the original actionCombination because it is mutable */
						ajDDTree.setDDAt(new ArrayList<String>(actionCombination), oiGivenaj);
					}
					
					catch (Exception e) {
						logger.error("While setting " + oiGivenaj + " at " + ajDDTree);
						e.printStackTrace();
						System.exit(-1);
					}
				}
				
				int oIndex = O.indexOf(o);
				
				ddTrees[oIndex] = OP.reorder(ajDDTree.toDD());
				
				logger.debug("Made f(O', " 
						+ this.S.stream()
							.filter(s -> s.name.contains("A_j"))
							.map(i -> i.name)
							.collect(Collectors.toList())
						+ ", S') for O=" + o + " and Ai=" 
						+ Ai + " with vars " + Arrays.toString(ddTrees[oIndex].getVarSet()));
			}
			
			Oi.put(Ai, ddTrees);
		}
		
		logger.debug("Finished making Oi");
		return Oi;
	}
	
	public HashMap<Integer, DD[]> makeOj() {
		/*
		 * Makes DDs for Oj, renames the vars, and conditions them on Mj.
		 * 
		 * The DBN structure is as follows:
		 * 
		 * 		[Mj] -------------> [Oj']
		 * 							   ^	
		 * 		[S'] -----------------/	
		 * 
		 * WARNING: Will only work for level 0 for now.
		 * 
		 * TODO: Generalize Oj creation for joint actions
		 */
		
		logger.debug("Making Oj");
		
		/* Create new HashMap for Oj of the variable order oj' -> mj -> s' */
		List<String> omegaJList = 
				this.Omega.stream()
						  .filter(o -> o.name.substring(o.name.length() - 2).contains("_j"))
						  .map(f -> f.name)
						  .collect(Collectors.toList());
		
//		DD[] Oj = new DD[omegaJList.size()];
		HashMap<Integer, DD[]> Oj = new HashMap<Integer, DD[]>();
		
		for (String oj : omegaJList) {
			
			/* find which frame Oj belongs to */
			int frameID = IPOMDP.getFrameIDFromVarName(oj);
			
			String Aj = 
					this.Ajs.keySet().stream()
						.filter(i -> IPOMDP.getFrameIDFromVarName(i) == frameID)
						.collect(Collectors.toList()).get(0);
				
			/* Make DD of all Aj */
			DDTree ajDDTree = 
					this.ddMaker.getDDTreeFromSequence(new String[] {Aj});
			
			for (String aj : this.Ajs.get(Aj)) {
				
				DDTree ojGivenaj = this.OjTheta.get(Aj).get(aj).get(oj);
				
				try {
					ajDDTree.setDDAt(aj, ojGivenaj);
				} 
				
				catch (Exception e) {
					logger.error("While setting Oj");
					e.printStackTrace();
					System.exit(-1);
				}
				
			}
			
			if (Oj.get(frameID) == null) 
				Oj.put(frameID, new DD[] {OP.reorder(ajDDTree.toDD())});
			
			else 
				Oj.put(frameID, ArrayUtils.add(Oj.get(frameID), OP.reorder(ajDDTree.toDD())));
			
			logger.debug("For oj=" + oj + " OjDD contains vars " 
					+ Arrays.toString(Oj.get(frameID)));
		}
		
		logger.debug("Oj initialized");
		return Oj;
	}
	
	public HashMap<String, DD[]> makeTi() {
		/*
		 * Constructs i's transition function.
		 * 
		 * The DBN structure is:
		 * 
		 * 		[Mj] -------------------.
		 * 								|
		 * 								v
		 * 		[S]  ----------------> [S']
		 * 
		 */
		
		/*
		 * The parsed Ti is in the form of a joint action transition function of the form -
		 * (Ai - Aj) -> S -> f(S', S)
		 * 
		 * We need to map that to the form Ai -> f(S', Mj, S)
		 */
		logger.debug("Making Ti");
		
		HashMap<String, DD[]> Ti = 
				new HashMap<String, DD[]>();
		
		List<String> S = 
				this.S.stream()
					.filter(s -> (!s.name.contains("M_j") && !s.name.contains("A_j")))
					.map(f -> f.name)
					.collect(Collectors.toList());

		/* For each action */
		for (String Ai : this.Ai) {

			DD[] ddTrees = new DD[this.S.size() - this.AjSize - 1];
			
			for (String s : S) {
				
				DDTree ajDDTree = 
						this.ddMaker.getDDTreeFromSequence(
								this.S.subList(
										this.AjVarStartPosition, 
										this.AjVarStartPosition + this.AjSize).stream()
											.map(i -> i.name)
											.toArray(String[]::new));
				
				/* for all combinations of J's actions */
				for (List<String> actionCombination : this.actionCombinations) {
					
					String ajPath = String.join("__", actionCombination);
					
					DDTree tiGivenaj = this.Ti.get(Ai + "__" + ajPath).get(s);
					
					try {
						/* avoid passing the original actionCombination because it is mutable */
						ajDDTree.setDDAt(new ArrayList<String>(actionCombination), tiGivenaj);
					}
					
					catch (Exception e) {
						logger.error("While setting " + tiGivenaj + " at " + ajDDTree);
						e.printStackTrace();
						System.exit(-1);
					}
				}
				
				logger.debug("Made f(S', "
						+ this.S.stream()
							.filter(v -> v.name.contains("A_j"))
							.map(i -> i.name)
							.collect(Collectors.toList())
						+ ", S) for S=" + s + " and Ai=" + Ai);
				
				DD TiForS = OP.reorder(ajDDTree.toDD());
				
				ddTrees[S.indexOf(s)] = TiForS;
			}
			
			Ti.put(Ai, ddTrees);
		}
		
		logger.debug("Finished making Ti");
		return Ti;
	}
	
	public HashMap<String, DD> makeRi() {
		/*
		 * Construct's i's reward function based on joint actions (Mj)
		 */
		
		logger.debug("Making reward funtion");
		
		/* First condition rewards on Mj for each Ai */
		HashMap<String, DD> Ri = new HashMap<String, DD>(); 
		HashMap<String, DD> actionCosts = new HashMap<String, DD>(); 
		
		for (String Ai : this.Ai) {
			
			DDTree ajDDTree = this.ddMaker.getDDTreeFromSequence(new String[] {"A_j"});
			
			for (String child : ajDDTree.children.keySet()) {
				
				try {
					ajDDTree.setDDAt(
							child, 
							OP.sub(
									OP.reorder(this.R.toDD()), 
									OP.reorder(this.costMap.get(
											Ai + "__" 
											+ child)
												.toDD())).toDDTree());
				} 
				
				catch (Exception e) {
					logger.error("While making cost for action " + Ai + " : " + e.getMessage());
					e.printStackTrace();
					System.exit(-1);
				}
			} /* for each aj */
			
			actionCosts.put(Ai, OP.reorder(ajDDTree.toDD()));
		} /* for each Ai */
		
		logger.debug("Done conditioning costs on Aj");
		
		/*
		 * Build rewTranFn like in POMDP setDynamics.
		 * 
		 * This computation is essentially the expected reward considering Aj for each Ai.
		 * 
		 * ER(S, Ai) = Sumout[Aj] f(S, Ai, Aj) 
		 */
		for (String Ai : actionCosts.keySet()) {
			
			DD RSMj = 
					OP.addMultVarElim(
							new DD[] {
									actionCosts.get(Ai), 
									this.currentAjGivenMj},
							this.AjIndex);
			
			logger.debug("For Ai=" + Ai + " R(S,Mj) has vars " 
					+ Arrays.toString(RSMj.getVarSet()));
			
			Ri.put(Ai, RSMj);
		}
		
		return Ri;
	}
	
	public void renameOjDDTrees() {
		/*
		 * Constructs opponents observation function Oj from extracted DDTree representation
		 * 
		 * WARNING: will only work for a single lower level frame
		 */
		
		logger.debug("Renaming Oj DDTrees");
	
		for (String aj : this.OjTheta.keySet()) {
			
			for (String actName : this.OjTheta.get(aj).keySet()) {
				
				/* For observation function for a specific action */
				HashMap<String, DDTree> Oj_a = this.OjTheta.get(aj).get(actName);
				HashMap<String, DDTree> renamedOj_a = new HashMap<String, DDTree>();
				
				/* Rename the observation vars */
				for (String o : Oj_a.keySet()) {
					
					DDTree oDDTree = Oj_a.get(o);
					
					oDDTree.renameVar(o, o + "_j");
					oDDTree.renameVar(o + "'", o + "_j'");
					
					renamedOj_a.put(o + "_j", oDDTree);
				}
				
				Oj_a = null;
				this.OjTheta.get(aj).put(actName, renamedOj_a);
			}
		}
		
		logger.debug("Done renaming Oj");
	}
	
	// ---------------------------------------------------------------------------------------------
	
	@Override
	public void commitVariables() {
		super.commitVariables();
		this.recordISVarIndices();
	}
	
	public void recordISVarIndices() {
		/*
		 * Populates arrays which record varIndices for the global variables
		 */
		
		logger.debug("Recording varIndices for IS");
		
		List<Integer> stateVarIndicesList = new ArrayList<Integer>();
		List<Integer> stateVarPrimeIndicesList = new ArrayList<Integer>();
		
		List<Integer> obsIVarIndicesList = new ArrayList<Integer>();
		List<Integer> obsIVarPrimeIndicesList = new ArrayList<Integer>();
		
		List<Integer> obsJVarIndicesList = new ArrayList<Integer>();
		List<Integer> obsJVarPrimeIndicesList = new ArrayList<Integer>();
			
		try {
			
			/*
			 * Populate IS indices
			 */
			for (StateVar stateVar : this.S) {
			
				stateVarIndicesList.add(IPOMDP.getVarIndex(stateVar.name));
				stateVarPrimeIndicesList.add(IPOMDP.getVarIndex(stateVar.name + "'"));
			}
			
			this.stateVarIndices = 
					stateVarIndicesList.stream().mapToInt(Integer::intValue).toArray();
			this.stateVarPrimeIndices = 
					stateVarPrimeIndicesList.stream().mapToInt(Integer::intValue).toArray();
			
			/*
			 * Populate OmegaI indices
			 */
			for (StateVar obsIVar : 
				this.Omega.stream()
					.filter(oi -> !oi.name.contains("_j"))
					.collect(Collectors.toList())) {
				
				obsIVarIndicesList.add(IPOMDP.getVarIndex(obsIVar.name));
				obsIVarPrimeIndicesList.add(IPOMDP.getVarIndex(obsIVar.name + "'"));
			}
			
			this.obsIVarIndices = 
					obsIVarIndicesList.stream().mapToInt(Integer::intValue).toArray();
			this.obsIVarPrimeIndices = 
					obsIVarPrimeIndicesList.stream().mapToInt(Integer::intValue).toArray();
			
			/*
			 * Populate OmegaJ indices
			 */
			for (StateVar obsJVar : 
				this.Omega.stream()
					.filter(oi -> oi.name.contains("_j"))
					.collect(Collectors.toList())) {
				
				obsJVarIndicesList.add(IPOMDP.getVarIndex(obsJVar.name));
				obsJVarPrimeIndicesList.add(IPOMDP.getVarIndex(obsJVar.name + "'"));
			}
			
			this.obsJVarIndices = 
					obsJVarIndicesList.stream().mapToInt(Integer::intValue).toArray();
			this.obsJVarPrimeIndices = 
					obsJVarPrimeIndicesList.stream().mapToInt(Integer::intValue).toArray();
		} 
		
		catch (Exception e) {
			logger.error("While recording IS indices " + e.getMessage());
			System.exit(-1);
		}
		
		logger.debug("State vars are " + Arrays.toString(this.stateVarIndices));
		logger.debug("Prime state vars are " + Arrays.toString(this.stateVarPrimeIndices));
		logger.debug("Obs vars are " + Arrays.toString(this.obsIVarIndices));
		logger.debug("Prime obs vars are " + Arrays.toString(this.obsIVarPrimeIndices));
		logger.debug("ObsJ vars are " + Arrays.toString(this.obsJVarIndices));
		logger.debug("Prime obsJ vars are " + Arrays.toString(this.obsJVarPrimeIndices));
	}
	
	public void reinitializeOnlineFunctions() {
		/*
		 * Reconstructs all the functions which depend on M_j
		 * 
		 * Since M_j is traversed online, the nodes consisting of that random variable
		 * are different at every time step. So the functions which depend on M_j have
		 * to be reinitialized after every step.
		 */
		
		logger.debug("Reinitializing Mj dependents according to new Mj");
		
		/* rebuild  P(Aj | Mj) */
		this.currentAjGivenMj = this.multiFrameMJ.getAjGivenMj(this.ddMaker, this.Ajs);
		logger.debug("f(Aj, Mj) for all Ajs for current look ahead horizon initialized");
		
		/* rebuild  P(Mj' | Mj, Aj, Oj') */
		this.currentMjTfn = this.makeOpponentModelTransitionDD();
		logger.debug("f(Mj', Aj, Mj, Oj') initialized");
		
		if (this.currentBelief == null) {
			DD mjInit = this.Mj.getMjInitBelief(this.ddMaker, null).toDD();
			DD initS = this.initBeliefDdTree.toDD();
			
			this.currentBelief = OP.reorder(OP.mult(mjInit, initS));
		}
		
		/* compute tau and store */
		this.currentTau = 
				OP.addMultVarElim(
						ArrayUtils.add(this.currentOj, this.currentMjTfn), 
						this.obsJVarPrimeIndices);
		logger.debug("TAU contains vars " + Arrays.toString(this.currentTau.getVarSet()));
		
		this.currentRi = this.makeRi();
		logger.debug("Ri initialized for current look ahead horizon");
	}
	
	public void initializeOfflineFunctions() {
		/*
		 * Initializes Oi, Oj and Ti functions
		 * 
		 * Oi, Oj and Ti do not depend on Mj, so they don't need to be rebuilt after every time
		 * step
		 */
		
		this.currentOi = this.makeOi();
		logger.debug("Oi initialized");
		
		this.currentTi = this.makeTi();
		logger.debug("Ti initialized");
		
		this.currentOj = this.makeOj();
		logger.debug("Oj initialized");

	}
	
	//------------------------------------------------------------------------------------------
	
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
		
		logger.info("Taking action " + action + "\r\n"
				+ " at belief " + InteractiveBelief.toStateMap(this, belief) + "\r\n"
				+ " with observation " + Arrays.toString(obs));
		
		/* perform belief update */
		DD nextBelief = 
				InteractiveBelief.staticL1BeliefUpdate(
						this, 
						belief, 
						action, 
						obs);
		
		logger.debug("Next belief is " + InteractiveBelief.toStateMap(this, nextBelief));
		
		/* transform Mj space to include new models in the next belief with non zero probabilities */
		this.transformMjSpace(nextBelief);
		logger.debug("Transformed belief space");
		
		/* re initialize IS */
		this.reinitializeOnlineFunctions();
		logger.debug("IS reinitialized");
	}
	
	public void transformMjSpace(DD belief) {
		/*
		 * Changes the belief space according to the new policy nodes in the current belief 
		 */
		
		/* convert current belief to DDTree */
		DDTree beliefDDTree = belief.toDDTree();
		
		HashSet<String> nonZeroMj = 
				new HashSet<String>(InteractiveBelief.toStateMap(this, belief).get("M_j").keySet());
		
		/* Expand from non zero Mj to create new Mj space */
		this.Mj.step(belief, this.mjLookAhead, nonZeroMj);
		
		/* initialize new IS and commit variables */
		this.updateMjInIS();
		
		/* make current belief */
		this.currentBelief = OP.reorder(this.Mj.getMjInitBelief(this.ddMaker, beliefDDTree).toDD());
	}
	
	private void updateMjInIS() {
		/*
		 * Commits new Mj variable to globals
		 * 
		 * Should be called every time opponent model is changed or when it traverses the next
		 * time steps
		 */
		this.S.set(
				this.oppModelVarIndex, 
				this.multiFrameMJ.getOpponentModelStateVar(
						this.oppModelVarIndex));
		
		logger.debug("IS initialized to " + this.S);
		
		Global.clearHashtables();
		commitVariables();
	}
	
	public void callUpdateIS() {
		/*
		 * calls updateMJInIS
		 * 
		 * WARNING: SHOULD NOT USE OUTSIDE TESTING.
		 */
		logger.warn("Calling a testing method. "
				+ "This shouldn't be happening during actual execution");
		this.updateMjInIS();
	}
	
	// -----------------------------------------------------------------------------------------
	
	@Override
	public List<List<String>> getAllPossibleObservations() {
		/*
		 * Observation combinations will only contain combinations of values of
		 * agent i's observation variables
		 */
		return this.obsCombinations;
	}
	
	@Override
	public List<String> getActions() {
		return this.Ai;
	}
	
	@Override
	public List<String> getStateVarNames() {
		return this.S.stream()
					.map(s -> s.name)
					.filter(n -> !n.contains("_j"))
					.collect(Collectors.toList());
	}
	
	@Override
	public List<String> getObsVarNames() {
		return this.Omega.stream()
					.map(o -> o.name)
					.filter(n -> !n.contains("_j"))
					.collect(Collectors.toList());
	}
	
	@Override
	public List<DD> getInitialBeliefs() {
		List<DD> initBelief = new ArrayList<DD>();
		initBelief.add(this.currentBelief);
		
		return initBelief;
	}
	
	@Override
	public int[] getStateVarIndices() {
		return ArrayUtils.subarray(this.stateVarIndices, 0, this.stateVarIndices.length - 1);
	}
	
	@Override
	public int[] getObsVarIndices() {
		return this.obsIVarIndices;
	}
	
	@Override
	public String getType() {
		return "IPOMDP";
	}
	
	public String getLowerLevelBeliefLabel(String valName) {
		/*
		 * Gets the lower level belief state map for the given valName
		 */
		
		return this.Mj.getBeliefTextAtNode(valName);
	}
	
	public String getOptimalActionAtMj(String mjNode) {
		/*
		 * Just a wrapped up call to OpponentModels getOptimalActionAtNode method 
		 */
		return this.Mj.getOptimalActionAtNode(mjNode);
	}
	
	@Override
	public DD getCurrentBelief() {
		/*
		 * Returns the current belief of the IPOMDP
		 */
		return this.currentBelief;
	}
	
	@Override
	public String getBeliefString(DD belief) {
		
		HashMap<String, HashMap<String, Float>> map = InteractiveBelief.toStateMap(this, belief);
		
		HashMap<String, Float> lowerBeliefs = new HashMap<String, Float>();
		
		for (String node : map.get("M_j").keySet())
			lowerBeliefs.put(this.getLowerLevelBeliefLabel(node), map.get("M_j").get(node));
		
		map.replace("M_j", lowerBeliefs);
		
		return map.toString();
	}
	
	// -----------------------------------------------------------------------------
	/*
	 * Serialization functions
	 */
	
	public static void saveIPOMDP(IPOMDP ipomdp, String filename) {
		/*
		 * Serializes the IPOMDP object and saves it to the given filename.
		 */
		
		try {
			ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(filename));
			
			objOut.writeObject(ipomdp);
			objOut.flush();
			objOut.close();
			
			logger.info("Saved IPOMDP object to " + filename);
		}
		
		catch (Exception e) {
			logger.error("While saving IPOMDP state in " + filename + " :");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static IPOMDP loadIPOMDP(String filename) {
		/*
		 * Load the serialized IPOMDP object from the given file
		 */
		
		try {
			ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(filename));
			IPOMDP ipomdp = (IPOMDP) objIn.readObject();
			objIn.close();
			
			logger.info("Loaded IPOMDP from " + filename);
			
			return ipomdp;
		}
		
		catch (Exception e) {
			logger.error("While loading IPOMDP from " + filename + ": ");
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}
}
