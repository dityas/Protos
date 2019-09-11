/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;

import thinclab.domainMaker.ddHelpers.DDMaker;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.ParserException;
import thinclab.exceptions.SolverException;
import thinclab.ipomdpsolver.InteractiveBelief.LookAheadTree;
import thinclab.policyhelper.PolicyNode;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.Global;
import thinclab.symbolicperseus.OP;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.ParseSPUDD;
import thinclab.symbolicperseus.StateVar;
import thinclab.symbolicperseus.Belief.Belief;
import thinclab.symbolicperseus.Belief.BeliefSet;
import thinclab.utils.LoggerFactory;

/*
 * @author adityas
 *
 */
public class IPOMDP extends POMDP {

	private static final long serialVersionUID = 4973485302724576384L;
	private Logger logger = LoggerFactory.getNewLogger("IPOMDP Main: ");
	
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
			
			this.logger.info("Initializing IPOMDP from parser.");
			
			this.initializeFromParsers(parsedFrame);
			this.setMjDepth(mjMaxDepth);
			this.setMjLookAhead(mjlookAhead);
			
			this.logger.info("IPOMDP initialized");
		}
		
		catch (Exception e) {
			this.logger.severe("While parsing " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public IPOMDP(String fileName) {
		super(fileName);
		this.logger.info("IPOMDP initialised from file: " + fileName);
	}
	
	public IPOMDP() {
		super();
		this.logger.info("IPOMDP initialised");
	}
	
	// -----------------------------------------------------------------------------------------
	
	public void initializeFromParsers(IPOMDPParser parsedFrame) throws ParserException {
		/*
		 * Initializes the IPOMDP from the thinclab.ipomdpsolver.IPOMDPParser object
		 */
		
		/* Store parser obj reference for future access */
		this.parser = parsedFrame;
		this.logger.fine("Parser reference stored");
		
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
			this.logger.info("Lower frame " + i + "parsed and stored.");
			
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
		
		this.uniquePolicy = new boolean[this.Ai.size()]; 
		this.initializeTFromParser(this.parser);
		this.initializeOFromParser(this.parser);
		this.initializeRFromParser(this.parser);
		
		this.initializeDiscountFactorFromParser(this.parser);		
		this.initializeBeliefsFromParser(this.parser);
		
		this.currentStateBeliefs.add(this.initBeliefDdTree);
		this.currentStateBeliefs.addAll(this.adjunctBeliefs);
		
		this.obsCombinations = this.getAllObservationsList();
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
				this.logger.info("Solved lower frame " + opponentModel);
				
				/*
				 * NOTE: After this point, extract all the required information
				 * from the lower level frame. The frame should not be accessed after
				 * exiting this function because the Global arrays will be changed.
				 */
				
				/* store opponent's Oj */
				this.OjTheta.add(opponentModel.getOi());
				this.logger.info("Extracted Oj for " + opponentModel);
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
		
		/* Start the initial look ahead */
		this.oppModel.expandFromRoots(this.mjLookAhead);
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
		
		this.logger.info("Staging obs vars for j");
		
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
		
		this.logger.info("Making M_j transition DD");
		
		/* Make DDMaker */
		DDMaker ddMaker = new DDMaker();
		ddMaker.addVariable(
				"M_j", 
				this.oppModel.currentNodes.toArray(
						new String[this.oppModel.currentNodes.size()]));
		
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
		
		this.logger.info("M_j transition DD created");
		
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
		this.logger.info("Making Oi");
		
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
				
				/* Make M_j factor */
				DDTree mjDDTree = this.ddMaker.getDDTreeFromSequence(new String[] {"M_j"});

				/* 
				 * Collapse Aj into Mj to create f(Mj, S', O')
				 */
				for (String childName : mjDDTree.children.keySet()) {
					mjDDTree.addChild(
							childName, 
							this.Oi.get(
									Ai + "__" 
									+ this.oppModel.getPolicyNode(childName).actName).get(o));
				}
				
				this.logger.info("Made f(O', Mj, S') for O=" + o + " and Ai=" + Ai);
				ddTrees[O.indexOf(o)] = OP.reorder(mjDDTree.toDD());
			}
			
			Oi.put(Ai, ddTrees);
		}
		
		this.logger.info("Finished making Oi");
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
		
		/* Create new HashMap for Oj of the variable order oj' -> mj -> s' */
		List<String> omegaJList = 
				this.Omega.stream()
						  .filter(o -> o.name.substring(o.name.length() - 2).contains("_j"))
						  .map(f -> f.name)
						  .collect(Collectors.toList());
		
		DD[] Oj = new DD[omegaJList.size()]; 
		
		for (String oj : omegaJList) {
			
			/* Make DD of all Mjs */
			DDTree mjDDTree = this.ddMaker.getDDTreeFromSequence(new String[] {"M_j"});

			
			for (String childName : mjDDTree.children.keySet()) {
				/* 
				 * Each mj has a single optimal action, so we map the action to mj and
				 * set the corresponding Oj for that action to the mj.
				 */

				DDTree ojDDTree = this.OjTheta.get(0)
										 	  .get(this.oppModel.getPolicyNode(childName).actName)
										 	  .get(oj);
				
				mjDDTree.addChild(childName, ojDDTree);
			}
			
			Oj[omegaJList.indexOf(oj)] = OP.reorder(mjDDTree.toDD());
		}
		
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
		this.logger.info("Making Ti");
		
		HashMap<String, DD[]> Ti = 
				new HashMap<String, DD[]>();
		
		List<String> S = 
				this.S.stream()
					.filter(s -> !s.name.contains("M_j"))
					.map(f -> f.name)
					.collect(Collectors.toList());
		
		/* For each action */
		for (String Ai : this.Ai) {

			DD[] ddTrees = new DD[this.S.size() - 1];
			
			for (String s : S) {
				
				/* Make M_j factor */
				DDTree mjDDTree = this.ddMaker.getDDTreeFromSequence(new String[] {"M_j"});
				
				/* 
				 * Collapse Aj into Mj to create f(Mj, S, S')
				 */
				for (String childName : mjDDTree.children.keySet()) {
					mjDDTree.addChild(
							childName, 
							this.Ti.get(
									Ai + "__" 
									+ this.oppModel.getPolicyNode(childName).actName).get(s));
				}
				
				this.logger.info("Made f(S', Mj, S) for S=" + s + " and Ai=" + Ai);
				ddTrees[S.indexOf(s)] = OP.reorder(mjDDTree.toDD());
			}
			
			Ti.put(Ai, ddTrees);
		}
		
		this.logger.info("Finished making Ti");
		return Ti;
	}
	
	public HashMap<String, DD> makeRi() {
		/*
		 * Construct's i's reward function based on joint actions (Mj)
		 */
		
		this.logger.info("Making reward funtion");
		
		/* First condition rewards on Mj for each Ai */
		HashMap<String, DD> Ri = new HashMap<String, DD>(); 
		HashMap<String, DD> actionCosts = new HashMap<String, DD>(); 
		
		for (String Ai : this.Ai) {
			
			DDTree mjDDTree = this.ddMaker.getDDTreeFromSequence(new String[] {"M_j"});
			
			for (String child : mjDDTree.children.keySet()) {
				
				try {
					mjDDTree.setDDAt(
							child, 
							OP.sub(
									this.R.toDD(), 
									this.parser.costMap.get(
											Ai + "__" 
											+ this.oppModel
												.getPolicyNode(child)
												.actName)
												.toDD()).toDDTree());
				} 
				
				catch (Exception e) {
					this.logger.severe("While making cost for action " + Ai + " : " + e.getMessage());
					e.printStackTrace();
					System.exit(-1);
				}
			} /* for each mj */
			
			actionCosts.put(Ai, mjDDTree.toDD());
		} /* for each Ai */
		
		this.logger.info("Done conditioning costs on Mj");
		
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
		
		this.logger.info("Renaming Oj DDTrees");
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
		
		this.logger.info("Done renaming Oj");
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
			logger.severe("While recording IS indices " + e.getMessage());
			System.exit(-1);
		}
	}
	
	public void initializeIS() {
		/*
		 * Reinitialize IS, Mj transition, Oi, Oj and Ti after new look ahead on Mj.
		 * 
		 * After every look ahead step with the Opponent Model, the interactive state space will
		 * change. This affects the DDs defined for Mj transitions and Oi and Ti.
		 */
		
		this.logger.info("Initializing according to Mj");
		
		/* First initialize new IS */
		this.S.set(
				this.oppModelVarIndex, 
				this.oppModel.getOpponentModelStateVar(
						this.oppModelVarIndex));
		this.commitVariables();
		Global.clearHashtables();
		
		this.logger.info("IS initialized to " + this.S);
		
		this.currentMjTfn = this.makeOpponentModelTransitionDD();
		this.logger.info("MjTfn initialized");
		
		this.currentOi = this.makeOi();
		this.logger.info("Oi initialized");
		
		this.currentTi = this.makeTi();
		this.logger.info("Ti initialized");
		
		this.currentOj = this.makeOj();
		this.logger.info("Oj initialized");
		
		DDTree mjRootBelief = this.oppModel.getOpponentModelInitBelief(this.ddMaker);
		
		this.currentStateBeliefs.stream()
			.forEach(s -> this.lookAheadRootInitBeliefs.add(
					OP.multN(new DD[] {s.toDD(), mjRootBelief.toDD()})));
		
		this.logger.info("Current look ahead init beliefs are " + this.lookAheadRootInitBeliefs);
		
		this.currentLookAheadTree = new LookAheadTree(this);
		this.logger.info("Look ahead tree initialized for " + this.mjLookAhead + " time steps.");
		
		this.currentRi = this.makeRi();
		this.logger.info("Ri initialized for current look ahead horizon");
	}
	
	// -----------------------------------------------------------------------------------------
	
	@Override
	public List<List<String>> getAllObservationsList() {
		return this.recursiveObsCombinations(
				this.Omega.subList(
						0, this.Omega.size() - this.OmegaJNames.size()));
	}
	
	public LookAheadTree getLookAheadTree() {
		return this.currentLookAheadTree;
	}
	
	public List<DD> getCurrentLookAheadBeliefs() {
		/*
		 * Returns a list of currently seen look ahead beliefs
		 */
		List<DD> currentBeliefs = new ArrayList<DD>();
		
		for (DD belief : this.currentLookAheadTree.iBeliefPoints)
			if (this.currentLookAheadTree.iBeliefTree.containsKey(belief))
				currentBeliefs.add(belief);
		
		return currentBeliefs;
	}
	
	public HashMap<String, HashMap<String, Float>> getLowerLevelBelief(String valName) {
		/*
		 * Gets the lower level belief state map for the given valName
		 */
		
		PolicyNode node = this.oppModel.getPolicyNode(valName);
		return node.factoredBelief;
	}
	
	public String getOptimalActionAtMj(String mjNode) {
		/*
		 * Returns j's optimal action at model mj
		 */
		PolicyNode node = this.oppModel.getPolicyNode(mjNode);
		return node.actName;
	}
}
