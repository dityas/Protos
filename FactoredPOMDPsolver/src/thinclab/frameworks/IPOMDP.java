/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.frameworks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.Belief.InteractiveBelief;
import thinclab.domainMaker.ddHelpers.DDMaker;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.ParserException;
import thinclab.exceptions.SolverException;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.ipomdpsolver.OpponentModel;
import thinclab.ipomdpsolver.InteractiveBelief.LookAheadTree;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.Global;
import thinclab.symbolicperseus.OP;
import thinclab.symbolicperseus.ParseSPUDD;
import thinclab.symbolicperseus.StateVar;

/*
 * @author adityas
 *
 */
public class IPOMDP extends POMDP {

	private static final long serialVersionUID = 4973485302724576384L;
	private static final Logger logger = Logger.getLogger(IPOMDP.class);
	
	/*
	 * The strategy level and frame ID of the frame represented by the IPOMDP object
	 */
	public int frameID;
	public int stratLevel;
	
	/*
	 * Reference to Parser object and info extract from parser
	 */
	public IPOMDPParser parser;
	public List<String> Ai = new ArrayList<String>();
	public List<String> Aj = new ArrayList<String>();
	public List<String> OmegaJNames = new ArrayList<String>();
	
	/*
	 * Store lower level frames
	 */
	public List<POMDP> lowerLevelFrames = new ArrayList<POMDP>();
	
	/*
	 * Store a local reference to OpponentModel object to get easier access to node
	 * indices and all that
	 */
	public OpponentModel oppModel;
	
	/*
	 * We will need to know the varIndex for the opponentModel statevar to replace it after
	 * every belief update
	 */
	public int oppModelVarIndex;
	
	/*
	 * DDs useful for belief update computation
	 */
	
	/* Mj's transition DD */
	public DD MjTFn;
	
	/*
	 * Additional DDTree variables.
	 */	
	/* 
	 * O_j
	 */
	
	/* Staging area for j's observation functions */
	public List<HashMap<String, HashMap<String, DDTree>>> OjTheta = 
			new ArrayList<HashMap<String, HashMap<String, DDTree>>>();
		
	/*
	 * Variables to decide Opponent Model depth
	 */
	public int mjDepth;
	public int mjLookAhead;
	
	/*
	 * Variables for current look ahead horizon
	 */
	public DD currentMjTfn;
	public DD currentAjGivenMj;
	public List<DD> lookAheadRootInitBeliefs = new ArrayList<DD>();
	
	public HashMap<String, DD[]> currentOi;
	public HashMap<String, DD[]> currentTi;
	public HashMap<String, DD> currentRi;
	public DD[] currentOj;
	
	public List<DDTree> currentStateBeliefs = new ArrayList<DDTree>();
	public LookAheadTree currentLookAheadTree;
	
	/*
	 * generate a list of all possible observations and store it to avoid
	 * computing it repeatedly during belief tree expansions
	 */
	public List<List<String>> obsCombinations;
	
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
			this.solveOpponentModels();
			this.commitVariables();
			this.initializeOfflineFunctions();
			
			/* Initialize Mj dependents */
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
			if (!this.Aj.contains(a.split("__")[1]))
				this.Aj.add(a.split("__")[1]);
			});
		
		/* Add Aj as a stateVar */
		this.S.add(
				new StateVar(
						"A_j", 
						this.S.size(), 
						this.Aj.toArray(new String[this.Aj.size()])));
		
		this.uniquePolicy = new boolean[this.Ai.size()]; 
		this.initializeTFromParser(this.parser);
		this.initializeOFromParser(this.parser);
		this.initializeRFromParser(this.parser);
		
		this.initializeDiscountFactorFromParser(this.parser);		
		this.initializeBeliefsFromParser(this.parser);
		
		this.currentStateBeliefs.add(this.initBeliefDdTree);
		this.currentStateBeliefs.addAll(this.adjunctBeliefs);
		
		this.obsCombinations = this.getAllPossibleObservations();
	}
	
	public void setAi(List<String> actionNames) {
		/*
		 * Sets the names for agent i's actions
		 */
		this.Ai = actionNames;
	}
	
	public void setAj(List<String> actionNames) {
		/*
		 * Sets the names for agent j's actions
		 */
		this.Aj = actionNames;
	}
	
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
	
	// ------------------------------------------------------------------------------------------
	
	public void solveOpponentModels() throws SolverException {
		/*
		 * Calls IPBVI or PBVI on the lower level frames depending on whether they are IPOMDPs
		 * or POMDPs
		 */
		for (POMDP opponentModel : this.lowerLevelFrames) {
			
			/*
			 * Check if lower frame is POMDP or IPOMDP and call the solve method accordingly
			 */
			Global.clearHashtables();
			
			if (opponentModel.level > 0) ((IPOMDP) opponentModel).solveIPBVI(15, 100);
			
			else if (opponentModel.level == 0) {
				/*
				 * For solving the POMDP at lowest level, set the globals
				 */
				opponentModel.setGlobals();
				opponentModel.solvePBVI(15, 100);
				logger.debug("Solved lower frame " + opponentModel);
				
				/*
				 * NOTE: After this point, extract all the required information
				 * from the lower level frame. The frame should not be accessed after
				 * exiting this function because the Global arrays will be changed.
				 */
				
				/* store opponent's Oj */
				this.OjTheta.add(opponentModel.getOi());
				logger.debug("Extracted Oj for " + opponentModel);
			}
			
			else 
				throw new SolverException("Frame " + 
					this.lowerLevelFrames.indexOf(opponentModel) + 
					" is not a POMDP or IPOMDP");
			
		} /* frame iterator */
		
		/* Rename extracted functions */
		this.renameOjDDTrees();
		
		/* Set Opponent Model object */
		this.oppModel = new OpponentModel(this.lowerLevelFrames, this.mjDepth);
		
		logger.info("Solved lower frames");
		
		/* Start the initial look ahead */
		this.oppModel.buildLocalModel(this.mjLookAhead);
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
		this.S.add(this.oppModel.getOpponentModelStateVar(this.oppModelVarIndex));
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
			obsj.stream().forEach(o -> o.setName(o.name + "_j"));
			this.Omega.addAll(obsj);
			
			this.OmegaJNames.addAll(obsj.stream().map(oj -> oj.name).collect(Collectors.toList()));
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
		ddMaker.addVariable(
				"M_j", 
				this.oppModel.currentNodes.toArray(
						new String[this.oppModel.currentNodes.size()]));
		
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
		String[][] triples = this.oppModel.getOpponentModelTriples();
		
		/* Get the DDTree from the DDMaker */
		DDTree MjDD = ddMaker.getDDTreeFromSequence(
				varSequence.toArray(new String[varSequence.size()]), 
				triples);
		
		DD MjTFn = OP.reorder(MjDD.toDD());
		
		logger.debug("M_j transition DD is " + MjTFn);
		
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
				
				/* Make A_j factor */
				DDTree mjDDTree = this.ddMaker.getDDTreeFromSequence(new String[] {"A_j"});

				/* 
				 * Collapse Aj into Mj to create f(Mj, S', O')
				 */
				for (String childName : mjDDTree.children.keySet()) {
					mjDDTree.addChild(
							childName, 
							this.Oi.get(
									Ai + "__" 
									+ childName).get(o));
				}
				
				logger.debug("Made f(O', Aj, S') for O=" + o + " and Ai=" + Ai);
				ddTrees[O.indexOf(o)] = OP.reorder(mjDDTree.toDD());
			}
			
			Oi.put(Ai, ddTrees);
		}
		
		logger.debug("Finished making Oi");
		return Oi;
	}
	
	public DD[] makeOj() {
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

		DD[] Oj = new DD[omegaJList.size()]; 
		
		for (String oj : omegaJList) {
			
			/* Make DD of all Ajs */
			DDTree ajDDTree = this.ddMaker.getDDTreeFromSequence(new String[] {"A_j"});

			for (String childName : ajDDTree.children.keySet()) {
				/* 
				 * Each mj has a single optimal action, so we map the action to mj and
				 * set the corresponding Oj for that action to the mj.
				 */

				DDTree ojDDTree = this.OjTheta.get(0)
										 	  .get(childName)
										 	  .get(oj);

				ajDDTree.addChild(childName, ojDDTree);
			}

			Oj[omegaJList.indexOf(oj)] = OP.reorder(ajDDTree.toDD());
		}
		
		logger.debug("Oj is " + Oj);
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

			DD[] ddTrees = new DD[this.S.size() - 2];
			
			for (String s : S) {
				
				/* Make A_j factor */
				DDTree ajDDTree = this.ddMaker.getDDTreeFromSequence(new String[] {"A_j"});
				
				/* 
				 * Create f(A_j, S, S')
				 */
				for (String childName : ajDDTree.children.keySet()) {
					ajDDTree.addChild(
							childName, 
							this.Ti.get(
									Ai + "__" 
									+ childName).get(s));
				}
				
				logger.debug("Made f(S', Aj, S) for S=" + s + " and Ai=" + Ai);
				ddTrees[S.indexOf(s)] = OP.reorder(ajDDTree.toDD());
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
									this.R.toDD(), 
									this.parser.costMap.get(
											Ai + "__" 
											+ child)
												.toDD()).toDDTree());
				} 
				
				catch (Exception e) {
					logger.error("While making cost for action " + Ai + " : " + e.getMessage());
					e.printStackTrace();
					System.exit(-1);
				}
			} /* for each aj */
			
			actionCosts.put(Ai, ajDDTree.toDD());
		} /* for each Ai */
		
		logger.debug("Done conditioning costs on Aj");
		
		/*
		 * Build rewTranFn like in POMDP setDynamics.
		 * 
		 * This computation is essentially the expected reward considering Aj for each Ai.
		 * 
		 * ER(S, Ai) = Sumout[Mj] P(R, Ai, Mj) 
		 */
		for (String Ai : actionCosts.keySet()) {
			
			DD[] rewTranFn = ArrayUtils.add(currentTi.get(Ai), actionCosts.get(Ai));
			rewTranFn = ArrayUtils.add(rewTranFn, currentMjTfn);

			Ri.put(Ai, OP.addMultVarElim(rewTranFn, this.stateVarPrimeIndices));
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
		
		/* Iterate over all observation functions */
		for (HashMap<String, HashMap<String, DDTree>> ojDDTree : this.OjTheta) {
			
			for (String actName : ojDDTree.keySet()) {
				
				/* For observation function for a specific action */
				HashMap<String, DDTree> Oj_a = ojDDTree.get(actName);
				HashMap<String, DDTree> renamedOj_a = new HashMap<String, DDTree>();
				
				/* Rename the observation vars */
				for (String o : Oj_a.keySet()) {
					
					DDTree oDDTree = Oj_a.get(o);
					
					oDDTree.renameVar(o, o + "_j");
					oDDTree.renameVar(o + "'", o + "_j'");
					
					renamedOj_a.put(o + "_j", oDDTree);
				}
				
				Oj_a = null;
				ojDDTree.put(actName, renamedOj_a);
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
		
		/* First initialize new IS */
		this.S.set(
				this.oppModelVarIndex, 
				this.oppModel.getOpponentModelStateVar(
						this.oppModelVarIndex));
		
		logger.debug("IS initialized to " + this.S);
		
		Global.clearHashtables();
		commitVariables();
		
		/* rebuild  P(Aj | Mj) */
		this.currentAjGivenMj = this.oppModel.getAjFromMj(this.ddMaker, this.Aj);
		logger.debug("f(Aj, Mj) for current look ahead horizon is " + this.S);
		
		/* rebuild  P(Mj' | Mj, Aj, Oj') */
		this.currentMjTfn = this.makeOpponentModelTransitionDD();
		logger.debug("f(Mj', Aj, Mj, Oj') initialized");
		
		DDTree mjRootBelief = this.oppModel.getOpponentModelInitBelief(this.ddMaker);
		
		this.lookAheadRootInitBeliefs.clear();
		
		this.currentStateBeliefs.stream()
			.forEach(s -> this.lookAheadRootInitBeliefs.add(
					OP.reorder(
							OP.multN(
									new DD[] {
											s.toDD(), 
											mjRootBelief.toDD()}))));
		
		logger.debug("Current look ahead init beliefs are " + this.lookAheadRootInitBeliefs);
		
//		this.currentLookAheadTree = new LookAheadTree(this);
//		logger.debug("Look ahead tree initialized for " + this.mjLookAhead + " time steps.");
		
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
		
		/* remove all current Mj nodes from the opponent model object */
		this.oppModel.clearCurrentContext();
		
		/* factor the interactive belief to get belief of the physical state */
		DD[] factoredBelief = InteractiveBelief.factorInteractiveBelief(this, belief);
		
		/*
		 * For the current implementation, there is a single interactive state variable
		 * representing the opponent model. So the belief over physical states can be extracted
		 * by factoring the interactive belief and then multiplying the n - 1 beliefs.
		 * 
		 * In future implementations with multiple frames, there may be an implementation with
		 * multiple state variables for opponent models. In such cases, the number of physical
		 * states will have to be stored explicitly.
		 * 
		 * WARNING: the extracted belief over physical states has to be a DDTree object and not a DD
		 * object. This is because the transformation of Mj space will overwrite the Global variable 
		 * indices. So the DD object will no longer be valid after the overwrite.
		 */
		this.currentStateBeliefs.clear();
		
		this.currentStateBeliefs.add(
				OP.multN(
						ArrayUtils.subarray(
								factoredBelief, 
								0, 
								factoredBelief.length - 1)).toDDTree());
		
		/* Get mj nodes with non zero probabilities */
		HashMap<String, Float> mjBelief = InteractiveBelief.toStateMap(this, belief).get("M_j");
		
		/*
		 * add individual node names to mextMj for expand function.
		 * 
		 * This is a weird way to call the expand function.
		 * 
		 * TODO: fix opponent model expansion API to accommodate previous non zero belief
		 */
		
		/* Expand from non zero Mj to create new Mj space */
		this.oppModel.step(mjBelief, this.mjLookAhead);
	}
	
	// -----------------------------------------------------------------------------------------
	
	@Override
	public List<List<String>> getAllPossibleObservations() {
		/*
		 * Observation combinations will only contain combinations of values of
		 * agent i's observation variables
		 */
		return this.recursiveObsCombinations(
				this.Omega.subList(
						0, this.Omega.size() - this.OmegaJNames.size()));
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
		return this.lookAheadRootInitBeliefs;
	}
	
//	public LookAheadTree getLookAheadTree() {
//		return this.currentLookAheadTree;
//	}
//	
//	public List<DD> getCurrentLookAheadBeliefs() {
//		/*
//		 * Returns a list of currently seen look ahead beliefs
//		 */
//		List<DD> currentBeliefs = new ArrayList<DD>();
//		
//		for (DD belief : this.currentLookAheadTree.iBeliefPoints)
//			if (this.currentLookAheadTree.iBeliefTree.containsKey(belief))
//				currentBeliefs.add(belief);
//		
//		return currentBeliefs;
//	}
	
	public String getLowerLevelBeliefLabel(String valName) {
		/*
		 * Gets the lower level belief state map for the given valName
		 */
		
		return this.oppModel.getBeliefTextAtNode(valName);
	}
	
	public String getOptimalActionAtMj(String mjNode) {
		/*
		 * Just a wrapped up call to OpponentModels getOptimalActionAtNode method 
		 */
		return this.oppModel.getOptimalActionAtNode(mjNode);
	}
	
	public List<DD> getCurrentBeliefs() {
		/*
		 * Returns the current beliefs of the IPOMDP
		 */
		return this.lookAheadRootInitBeliefs;
	}
}
