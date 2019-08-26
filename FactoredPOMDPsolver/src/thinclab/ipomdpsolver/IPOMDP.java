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

import thinclab.domainMaker.ddHelpers.DDMaker;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.ParserException;
import thinclab.exceptions.SolverException;
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
	public ParseSPUDD parser;
	public List<String> Ai = new ArrayList<String>();
	public List<String> Aj = new ArrayList<String>();
	
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
	/* Interactive state space staging */
//	public List<StateVar> IS = new ArrayList<StateVar>();
//	public List<StateVar> Omegai = new ArrayList<StateVar>();
	
	/* 
	 * O_j
	 */
	
	/* Staging area for j's observation functions */
	public List<HashMap<String, HashMap<String, DDTree>>> OjTheta = 
			new ArrayList<HashMap<String, HashMap<String, DDTree>>>();
	
	/* Actual reordered observation function for j's obsVars */
//	public DDTree Oj;
//	
//	/* O_i */
//	public HashMap<String, HashMap<String, DDTree>> Oi = 
//			new HashMap<String, HashMap<String, DDTree>>(); 
	
	// ----------------------------------------------------------------------------------------

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

		this.initializeAFromParser(this.parser);
		
		/* Split actions to Ai and Aj */
		this.A.forEach(a -> {	
			if (!this.Ai.contains(a.split("__")[0]))
				this.Ai.add(a.split("__")[0]);
			if (!this.Aj.contains(a.split("__")[1]))
				this.Aj.add(a.split("__")[1]);
			});
		
		this.initializeTFromParser(this.parser);
		this.initializeOFromParser(this.parser);
		this.initializeRFromParser(this.parser);
		
		this.initializeDiscountFactorFromParser(this.parser);		
		this.initializeBeliefsFromParser(this.parser);
//		this
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
		
	}
	
	public OpponentModel getOpponentModel() throws SolverException {
		/*
		 * Computes the models of the lower level agents and makes
		 * (reachable beliefs X frames) number of opponent models 
		 */
		this.solveOpponentModels();		
		OpponentModel oppModel = new OpponentModel(this.lowerLevelFrames);

		return oppModel;
	}
	
	public void solveIPBVI(int rounds, int numDpBackups) {
		/*
		 * Runs the interactive PBVI loop for solving the IPOMDP
		 */
		
		try {
		
			/* Solve lower frames and make opponent model */
			this.oppModel = this.getOpponentModel();
			
			/* 
			 * Stage and commit additional state and variables to populate global 
			 * arrays
			 */
			this.setUpIS();
			this.setUpOmegaI();
			this.commitVariables();
			
			/*
			 * These will need to be constructed at every belief update
			 */
			
			/* Make M_j transition DD */
			this.makeOpponentModelTransitionDD();
			
			/* Make O (observation functions) */
			this.makeOi();
//			DDTree Oj = this.getOj();
		} 
		
		catch (SolverException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		
	}
	
	// ------------------------------------------------------------------------------------------
	
	public void setUpIS() {
		/*
		 * Constructs the IS space from unique opponent models and physical states
		 * 
		 * First stages the physical states S parsed from the domain file, then stages the
		 * opponent's model M_j
		 */
		
		/* Add S from parser obj */
		this.logger.info("Staging IS vars");
		this.S.clear();
		this.initializeSFromParser(this.parser);
		
		/* Set up M_j */
		this.setUpMj();
		this.logger.info("IS vars staged to: " + this.S);
	}
	
	public void setUpMj() {
		/*
		 * Constructs opponents' models 
		 * 
		 * We are considering the physical states S to be independent of the belief 
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
		
		/* Set oppModelVarIndex */
		this.oppModelVarIndex = this.S.size();
		
		/* Finally, add the oppModel state var to the staging list */
		this.S.add(this.oppModel.getOpponentModelStateVar(this.oppModelVarIndex));
	}
	
	public void setUpOmegaI() {
		/*
		 * Set up i's observation space.
		 * 
		 * This includes extracting and including j's observation space.
		 * 
		 * WARNING: Currently only works for a single frame
		 */
		
		this.Omega.clear();
		this.logger.info("Staging Omega");
		
		/* Add Omega_i from the parser */
		this.initializeOmegaFromParser(this.parser);
		
		/* Add Omega_j */
		this.setUpOmegaJ();
		
		this.logger.info("Omega_i vars staged to :" + this.Omega);
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
	
	public void makeOpponentModelTransitionDD() {
		/*
		 * Construct Mj transition DD from OpponentModel triples 
		 */
		
		this.logger.info("Making M_j transition DD");
		
		/* Make DDMaker */
		DDMaker ddMaker = new DDMaker();
		ddMaker.addVariable(
				"M_j", 
				this.oppModel.nodeIndex.keySet().toArray(
						new String[this.oppModel.nodeIndex.size()]));
		
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
		
		this.MjTFn = OP.reorder(MjDD.toDD());
		
		this.logger.info("M_j transition DD created");
	}
	
	public HashMap<String, HashMap<String, DDTree>> makeOi() {
		/*
		 * Constructs agent i's observation function Oi from extracted DDTree representation
		 * 
		 */
		this.logger.info("Making Oi");
		HashMap<String, HashMap<String, DDTree>> Oi = new HashMap<String, HashMap<String, DDTree>>();
		
//		/* Populate O for agent i's observations */
//		this.initializeOFromParser(this.parser);
		
		/* Create Ai -> Aj -> Oi -> S' mapping */
		HashMap<String, HashMap<String, HashMap<String, DDTree>>> AiAjOiS = 
				new HashMap<String, HashMap<String, HashMap<String, DDTree>>>();
		
		/* For each joint action, split j's and i's action and arrange Oi accordingly */
		for (String jointAction : this.Oi.keySet()) {
			
			String Ai = jointAction.split("__")[0];
			String Aj = jointAction.split("__")[1];
			
			HashMap<String, HashMap<String, DDTree>> AjOiS = 
					new HashMap<String, HashMap<String, DDTree>>();
			AjOiS.put(Aj, this.Oi.get(jointAction));
			
			if (AiAjOiS.containsKey(Ai))
				AiAjOiS.get(Ai).putAll(AjOiS);
			
			else AiAjOiS.put(Ai, AjOiS);
		}
		
		/* Make Ai -> f(Oi, S', Mj) */
		for (String Ai : AiAjOiS.keySet()) {
			
			HashMap<String, HashMap<String, DDTree>> AjOiS = AiAjOiS.get(Ai);
			HashMap<String, DDTree> OiSMj = new HashMap<String, DDTree>();
			
			List<String> OmegaI = 
					this.Omega.stream()
						.filter(o -> !o.name.contains("_j"))
						.map(f -> f.name)
						.collect(Collectors.toList());
			
			for (String oi : OmegaI) {
				
				DDTree mjDDTree = this.ddMaker.getDDTreeFromSequence(new String[] {"M_j"});
				for (String childName : mjDDTree.children.keySet()) {

					mjDDTree.addChild(
							childName, 
							AjOiS.get(
									this.oppModel.getPolicyNode(childName).actName).get(oi));
				}
				
				OiSMj.put(oi, OP.reorder(mjDDTree.toDD()).toDDTree());
			}
			
			Oi.put(Ai, OiSMj);
		}
		
		/*
		 * For L0, Oj will not depend on Ai. It will depend only on Aj. So add Oj here to each Ai.
		 */
		Oi.forEach((a, dd) -> dd.putAll(this.getOj()));
		this.logger.info("Finished making Oi");
		
		return Oi;
	}
	
	public HashMap<String, DDTree> getOj() {
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
		
		HashMap<String, DDTree> Oj = new HashMap<String, DDTree>();
		
		/* Create new HashMap for Oj of the variable order oj' -> mj -> s' */
		List<String> omegaJList = 
				this.Omega.stream()
						  .filter(o -> o.name.substring(o.name.length() - 2).contains("_j"))
						  .map(f -> f.name)
						  .collect(Collectors.toList());
		
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
			
			Oj.put(oj, OP.reorder(mjDDTree.toDD()).toDDTree());
		}
		
		return Oj;
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

}
