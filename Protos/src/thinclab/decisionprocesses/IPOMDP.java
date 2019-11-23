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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import thinclab.belief.IBeliefOps;
import thinclab.belief.SSGABeliefExpansion;
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
import thinclab.representations.modelrepresentations.MJ;
import thinclab.representations.modelrepresentations.MultiFrameMJ;
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
	public List<String> Aj = new ArrayList<String>();
	public List<String> ThetaJ = new ArrayList<String>();
	public List<String> OmegaJNames = new ArrayList<String>();
	
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
	public int MjVarPosition;
	public int MjVarIndex;
	public int AjStartIndex = -1;
	public int AjVarStartPosition;
	
	public int thetaVarPosition = -1;
	
	/* actions costs stored locally to avoid storing the full parser object */
	private HashMap<String, DDTree> costMap;
	
	/* Staging area for j's observation functions */
	/*
	 * format should be theta -> Aj -> S -> Oj
	 */
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
	public DD currentMjPGivenMjOjPAj;
	public DD currentThetajGivenMj;
	public DD currentTau;
	public DD currentAjGivenMj;
	public DD currentBelief = null;
	
	public HashMap<String, DD[]> currentOi;
	public HashMap<String, DD[]> currentTi;
	public HashMap<String, DD> currentRi;
	public DD[] currentOj;
	
	public List<DDTree> currentStateBeliefs = new ArrayList<DDTree>();
	
	/*
	 * generate a list of all possible observations and store it to avoid
	 * computing it repeatedly during belief tree expansions.
	 * Same for J's actions
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
	
	public IPOMDP(IPOMDPParser parsedFrame, int mjlookAhead) {
		/*
		 * Initialize from a IPOMDPParser object
		 */
		
		try {
			
			logger.info("Initializing IPOMDP from parser.");
			
			this.initializeFromParsers(parsedFrame);
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
		this.MjVarPosition = this.S.size();
		this.MjVarIndex = this.MjVarPosition + 1;
		
		/* Put a dummy state var for M_j */
		this.S.add(new StateVar(1, "M_j", this.MjVarIndex));
		
		/* Make a var for ThetaJ */
		this.thetaVarPosition = this.S.size();
		
		this.S.add(
				new StateVar(
						"Theta_j", 
						this.S.size(), 
						this.lowerLevelFrames.stream()
							.map(f -> "theta/" + f.frameID)
							.toArray(String[]::new)));
		this.ThetaJ.addAll(Arrays.asList(this.S.get(this.thetaVarPosition).valNames));
		
		this.initializeOmegaFromParser(this.parser);
		this.setUpOmegaJ();
		
		this.initializeAFromParser(this.parser);
		
		/* Split actions to Ai and Aj */
		this.A.forEach(a -> {	
			if (!this.Ai.contains(a.split("__")[0]))
				this.Ai.add(a.split("__")[0]);
			});
		
		/* Aj set */
		HashSet<String> Ajs = new HashSet<String>();
		for (DecisionProcess lowerFrame : this.lowerLevelFrames)
			Ajs.addAll(lowerFrame.getActions());
		
		this.Aj.addAll(Ajs);
		
		/* Add Aj as a stateVar */
		this.S.add(
				new StateVar(
						"A_j", 
						this.S.size(),
						this.Aj.stream().toArray(String[]::new)));

		this.AjStartIndex = this.S.size();
		this.AjVarStartPosition = this.S.size() - 1;
		
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
		
		/* set belief operations handler */
		this.bOPs = new IBeliefOps(this);
	}
	
	public void setAi(List<String> actionNames) {
		/*
		 * Sets the names for agent i's actions
		 */
		this.Ai = actionNames;
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
	
	private void unRollWildCards() {
		/*
		 * The domain file can have wild cards to specify multiple transitions in a single action
		 * def using wild cards. This methods expands and unrolls them
		 */

		for (String ADef : this.A) {
			
			String[] actionDefs = ADef.split("__");
			
			if (actionDefs[1].contentEquals("ALL")) {
				
				/* create a list of actions to be unrolled */
				List<String> actions = new ArrayList<String>(this.Aj);
				
				logger.debug("Found wildcard actionDef " + ADef 
						+ " actions to be unrolled are " + actions);
				
				/*
				 * unroll wildcard by appending aj to each Ai for which wildcard is used
				 */
				List<String> allActions = 
						actions.stream().map(a -> (actionDefs[0] + "__" + IPOMDP.getLocalName(a)))
										.collect(Collectors.toList());
				
				for (String jointAction : allActions) {
					
					if (this.A.contains(jointAction)) continue;
					
					logger.debug("Applying DDTree for " + ADef + " to " 
							+ jointAction);
					
					this.Ti.put(jointAction, this.Ti.get(ADef));
					this.Oi.put(jointAction, this.Oi.get(ADef));
					this.costMap.put(jointAction, this.costMap.get(ADef));
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
				solver.expansionStrategy.clearMem();
				
				/*
				 * NOTE: After this point, extract all the required information
				 * from the lower level frame. The frame should not be accessed after
				 * exiting this function because the Global arrays will be changed.
				 */
				
				String frameName = IPOMDP.getCanonicalName(mj.frameID, "theta");
				
				/* store opponent's Oj */
				HashMap<String, HashMap<String,DDTree>> oj = ((POMDP) mj).getOi();
				
				for (String aj : oj.keySet()) {
					
					if (!this.OjTheta.containsKey(frameName))
						this.OjTheta.put(
								frameName, 
								new HashMap<String, HashMap<String, DDTree>>());
						
					this.OjTheta.get(frameName).put(aj, oj.get(aj));
					
				}
				
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
		
		/* Call GC to free up memory used by lower frame solvers and belief searches */
		logger.debug("Calling GC after solving lower frames");
		System.gc();
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
		this.S.add(this.Mj.getOpponentModelStateVar(this.MjVarPosition));
	}
	
	public void setUpOmegaJ() throws ParserException {
		/*
		 * Adds observation for agent j
		 * 
		 * WARNING: Currently only works for a single frame.
		 */
		
		logger.debug("Staging obs vars for j");
		
		List<List<StateVar>> obsJ = new ArrayList<List<StateVar>>();
		
		for (POMDP frame : this.lowerLevelFrames) {
			List<StateVar> obsj = Arrays.asList(frame.obsVars);
			obsj.stream().forEach(o -> o.setName(o.name + "_j"));
			obsJ.add(obsj);
		}
		
		/* check if obsJ vars are same across all frames */
		for (int i = 0; i < obsJ.size(); i++) {
			
			List<StateVar> obsJcurrentFrame = obsJ.get(i);
			
			if (obsJcurrentFrame.size() != obsJ.get(0).size())
				throw new ParserException("Observation vars for all frames should be same");
			
			for (int j = 0; j < obsJcurrentFrame.size(); j++) {
				
				if ((!obsJcurrentFrame.get(j).name.contentEquals(obsJ.get(0).get(j).name)) || 
						(obsJcurrentFrame.get(j).arity != obsJ.get(0).get(j).arity)) {
					throw new ParserException("Observation vars for all frames should be same:"
							+ " " + obsJcurrentFrame.get(j)  + " != " + obsJ.get(0).get(j));
				}
			}
		}
		
		/* if they are same, populate this.Omega and this.OmegaJ */
		this.Omega.addAll(obsJ.get(0));
		this.OmegaJNames.addAll(
				obsJ.get(0).stream()
					.map(oj -> oj.name)
					.collect(Collectors.toList()));
		
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
		
		DD PMjPGivenOjPAj = 
				this.multiFrameMJ.getPMjPGivenMjOjPAj(this.ddMaker, this.Aj, this.OmegaJNames);
		
		logger.debug("f(Mj', Mj, Oj', Aj) contains variables " 
				+ Arrays.toString(PMjPGivenOjPAj.getVarSet()));
		
		return PMjPGivenOjPAj;
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
						this.ddMaker.getDDTreeFromSequence(new String[] {"A_j"});
				
				/* for all of J's actions */
				for (String aj : this.Aj) {
					
					String ajPath = Ai + "__" + aj;
					
					DDTree oiGivenaj = this.Oi.get(IPOMDP.getLocalName(ajPath)).get(o);
					
					try {
						/* avoid passing the original actionCombination because it is mutable */
						ajDDTree.setDDAt(aj, oiGivenaj);
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
		 * Returns Oj in the format Thetaj -> Oj[]
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
			
			/* Make DD of all Aj */
			DDTree ajDDTree = 
					this.ddMaker.getDDTreeFromSequence(new String[] {"Theta_j", "A_j"});
			
			for (String thetaj : this.ThetaJ) {
			
				for (String aj : this.Aj) {
					
					if (this.lowerLevelFrames.get(
							IPOMDP.getFrameIDFromVarName(thetaj))
								.getActions()
								.contains(aj)) {
						DDTree ojGivenaj = this.OjTheta.get(thetaj).get(aj).get(oj);
						
						try {
							ajDDTree.setDDAt(
									Arrays.asList(new String[] {thetaj, aj}), 
									ojGivenaj);
						} 
						
						catch (Exception e) {
							logger.error("While setting Oj");
							e.printStackTrace();
							System.exit(-1);
						}
					}
					
					else {
						
						logger.error("All frames need to have the same actions");
						System.exit(-1);
					}
				}
			
			}
			
			int ojIndex = OmegaJNames.indexOf(oj);
			Oj[ojIndex] = OP.reorder(ajDDTree.toDD());
			
			logger.debug("For oj=" + oj + " OjDD contains vars " 
					+ Arrays.toString(Oj[ojIndex].getVarSet()));
		}

		logger.debug("Oj initialized");
		logger.debug("Clearing parsed OmegaJ to save memory");
		this.OjTheta.clear();
		
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
		 * We need to map that to the form Ai -> f(S', Aj, S)
		 */
		logger.debug("Making Ti");
		
		HashMap<String, DD[]> Ti = 
				new HashMap<String, DD[]>();
		
		List<String> S = 
				this.S.subList(0, this.MjVarPosition).stream()
					.map(f -> f.name)
					.collect(Collectors.toList());

		/* For each action */
		for (String Ai : this.Ai) {

			DD[] ddTrees = new DD[this.MjVarPosition];
			
			for (String s : S) {
				
				DDTree ajDDTree = 
						this.ddMaker.getDDTreeFromSequence(new String[] {"A_j"});
				
				/* for all of J's actions */
				for (String aj : this.Aj) {
					
					String ajPath = Ai + "__" + aj;
					
					DDTree tiGivenaj = this.Ti.get(ajPath).get(s);
					
					try {
						/* avoid passing the original actionCombination because it is mutable */
						ajDDTree.setDDAt(aj, tiGivenaj);
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
		logger.debug("Clearning parsed T to save memory");
		this.Ti.clear();
		
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
			
			DDTree ajDDTree = 
					this.ddMaker.getDDTreeFromSequence(new String[] {"A_j"});
			
			for (String action : ajDDTree.children.keySet()) {
				
				try {
					
					ajDDTree.setDDAt(
							action, 
							OP.sub(
									OP.reorder(this.R.toDD()), 
									OP.reorder(this.costMap.get(
											Ai + "__" 
											+ action)
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
							new DD[] {this.currentAjGivenMj, actionCosts.get(Ai)},
							this.AjStartIndex);
			
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
	
		for (String f : this.OjTheta.keySet()) {
			
			HashMap<String, HashMap<String, DDTree>> ojThetaPerF = this.OjTheta.get(f);
			
			for (String aj : ojThetaPerF.keySet()) {
					
				/* For observation function for a specific action */
				HashMap<String, DDTree> Oj_a = ojThetaPerF.get(aj);
				HashMap<String, DDTree> renamedOj_a = new HashMap<String, DDTree>();
				
				/* Rename the observation vars */
				for (String o : Oj_a.keySet()) {
					
					DDTree oDDTree = Oj_a.get(o);
					
					oDDTree.renameVar(o, o + "_j");
					oDDTree.renameVar(o + "'", o + "_j'");
					
					renamedOj_a.put(o + "_j", oDDTree);
				}
				
				Oj_a = null;
				ojThetaPerF.put(aj, renamedOj_a);
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
		this.currentAjGivenMj = this.multiFrameMJ.getAjGivenMj(this.ddMaker, this.Aj);
		logger.debug("f(Aj, Mj) for all Ajs for current look ahead horizon initialized");
		
		/* rebuild  P(Thetaj | Mj) */
		this.currentThetajGivenMj = this.multiFrameMJ.getThetajGivenMj(this.ddMaker, this.ThetaJ);
		logger.debug("f(Thetaj, Mj) for all Thetajs for current look ahead horizon initialized");
		
		/* rebuild  P(Mj' | Mj, Aj, Oj') */
		this.currentMjPGivenMjOjPAj = this.makeOpponentModelTransitionDD();
		logger.debug("f(Mj', Aj, Mj, Oj') initialized");
		
		/* check if P(Mj' | Mj, Aj, Oj') CPD is valid */
		DD cpdSum = 
				OP.addMultVarElim(
						this.currentMjPGivenMjOjPAj, 
						this.MjVarIndex + (this.S.size() + this.Omega.size()));
		
		if (OP.maxAll(OP.abs(OP.sub(DD.one, cpdSum))) < 1e-8)
			logger.debug("f(Mj', Aj, Mj, Oj') distribution verified");
		
		else {
			logger.error("f(Mj', Aj, Mj, Oj') sums out to " + cpdSum);
			System.exit(-1);
		}
		
		if (this.currentBelief == null) {
			DD mjInit = this.multiFrameMJ.getMjInitBelief(this.ddMaker, null).toDD();
			DD initS = this.initBeliefDdTree.toDD();
			
			this.currentBelief = OP.reorder(OP.mult(mjInit, initS));
		}
		logger.debug("Current belief set to: \r\n" + this.currentBelief.toDDTree());
		
		/* compute tau and store */
			
		this.currentTau = 
				OP.addMultVarElim(
						ArrayUtils.add(this.currentOj, this.currentMjPGivenMjOjPAj), 
						this.obsJVarPrimeIndices);
		
		/* null this.currentMjPGivenMjOjPAj to save memory */
		this.currentMjPGivenMjOjPAj = null;
		
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
		
		logger.info("Taking action " + action + "\r\n"
				+ " at belief " + this.toMapWithTheta(belief) + "\r\n"
				+ " with observation " + Arrays.toString(obs));
		
		/* perform belief update */
		DD nextBelief = 
				this.beliefUpdate( 
						belief, 
						action, 
						obs);
		
		logger.debug("Next belief is " + this.toMapWithTheta(nextBelief));
		
		/* transform Mj space to include new models in the next belief with non zero probabilities */
		this.transformMjSpace(nextBelief);
		logger.debug("Transformed belief space");
		
		/* re initialize IS */
		this.reinitializeOnlineFunctions();
		logger.debug("IS reinitialized");
		
		/* Call GC here in hopes that it will free some memory */
		logger.debug("Calling GC");
		System.gc();
	}
	
	public void transformMjSpace(DD belief) {
		/*
		 * Changes the belief space according to the new policy nodes in the current belief 
		 */
		
		/* convert current belief to DDTree */
		DDTree beliefDDTree = belief.toDDTree();
		
		HashSet<String> nonZeroMj = 
				new HashSet<String>(this.toMap(belief).get("M_j").keySet());
		
		/* Expand from non zero Mj to create new Mj space */
		this.multiFrameMJ.step(belief, this.mjLookAhead, nonZeroMj);
		
		/* initialize new IS and commit variables */
		this.updateMjInIS();
		
		/* make current belief */
		this.currentBelief = 
				OP.reorder(
						this.multiFrameMJ.getMjInitBelief(
								this.ddMaker, beliefDDTree).toDD());
	}
	
	private void updateMjInIS() {
		/*
		 * Commits new Mj variable to globals
		 * 
		 * Should be called every time opponent model is changed or when it traverses the next
		 * time steps
		 */
		this.S.set(
				this.MjVarPosition, 
				this.multiFrameMJ.getOpponentModelStateVar(
						this.MjVarPosition));
		
		logger.debug("IS initialized to " + this.S);
		logger.debug("Mj is tracking " + this.S.get(this.MjVarPosition).arity + " models");
		
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
	
	@Override
	public DD getRewardFunctionForAction(String action) {
		/*
		 * Returns the reward function for the particular action
		 */
		
		return this.currentRi.get(action);
	}
	
	public String getLowerLevelBeliefLabel(String valName) {
		/*
		 * Gets the lower level belief state map for the given valName
		 */
		
		return this.multiFrameMJ.getBeliefTextAtNode(valName);
	}
	
	public String getOptimalActionAtMj(String mjNode) {
		/*
		 * Just a wrapped up call to OpponentModels getOptimalActionAtNode method 
		 */
		return this.Mj.getOptimalActionAtNode(mjNode);
	}
	
	public HashMap<String, HashMap<String, Float>> toMapWithTheta(DD belief) {
		/*
		 * Access to IBeliefOps method
		 */
		return ((IBeliefOps) this.bOPs).toMapWithTheta(belief);
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
		/*
		 * Return the beliefs as a JSON string
		 */
		
		/* convert belief to map */
		HashMap<String, HashMap<String, Float>> map = this.toMapWithTheta(belief);
		
		/* initialize JSON handler */
		Gson gsonHandler = 
				new GsonBuilder()
					.disableHtmlEscaping()
					.create();
		
		/* make lower level beliefs */
		HashMap<HashMap<String, String>, String> lowerLevelBeliefMap = 
				new HashMap<HashMap<String, String>, String>();
		
		for (String node : map.get("M_j").keySet()) {
			
			HashMap<String, String> beliefMap = new HashMap<String, String>();
			
			beliefMap.put("belief_j", this.getLowerLevelBeliefLabel(node));
			beliefMap.put("A_j", this.multiFrameMJ.getOptimalActionAtNode(node));
			beliefMap.put("Theta_j", "theta/" + IPOMDP.getFrameIDFromVarName(node));
			
			lowerLevelBeliefMap.put(beliefMap, map.get("M_j").get(node).toString());
		}
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("M_j", gsonHandler.toJsonTree(lowerLevelBeliefMap));
		
		/* add rest of the states */
		for (String key : map.keySet()) {
			
			if (key.contentEquals("M_j")) continue;
			
			jsonObject.add(key, gsonHandler.toJsonTree(map.get(key)));
		}
		
		return gsonHandler.toJson(jsonObject);
	}
	
	// -----------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "IPOMDP [frameID=" + this.frameID + ", level=" + this.level
				+ ", nFrames=" + this.lowerLevelFrames.size()
				+ ", S=" + this.S
				+ ", Omega=" + this.Omega;
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
