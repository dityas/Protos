/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import thinclab.exceptions.ParserException;
import thinclab.exceptions.SolverException;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.ParseSPUDD;
import thinclab.symbolicperseus.Belief.Belief;
import thinclab.symbolicperseus.Belief.BeliefSet;

/*
 * @author adityas
 *
 */
public class IPOMDP extends POMDP {

	private static final long serialVersionUID = 4973485302724576384L;
	
	/*
	 * The strategy level and frame ID of the frame represented by the IPOMDP object
	 */
	public int frameID;
	public int stratLevel;
	
	/*
	 * Tabular transition and observation functions
	 */
	public Vector<Vector<Vector<Float>>> T = new Vector<Vector<Vector<Float>>>();
	
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
	
	// ----------------------------------------------------------------------------------------

	public IPOMDP(String fileName) {
		super(fileName);
	}
	
	public IPOMDP() {
		super();
	}
	
	// -----------------------------------------------------------------------------------------
	
	public void initializeFromParsers(IPOMDPParser parsedFrame) throws ParserException {
		/*
		 * Initializes the IPOMDP from the thinclab.ipomdpsolver.IPOMDPParser object
		 */
		
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
			
		} /* for all child frames */
		
		/* Initialize state vars from the domain file */
		super.initializeStateVarsFromParser(parsedFrame);
		
		
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
			
			/* make new state space and keep it staged */
			this.makeStateSpace();
		} 
		
		catch (SolverException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		
	}
	
	// ------------------------------------------------------------------------------------------
	
	public void makeStateSpace() {
		/*
		 * Constructs the IS space from unique opponent models and physical states 
		 * 
		 * We are considering the physical states S to be independent of the belief 
		 * over opponents' beliefs. So the opponents' model M will be a separate state
		 * variable in addition to the physical states S
		 */
		
		/*
		 * Clear the staging list to delete any parsed state variables. Then add all 
		 * state variables from one of the lower level frames. The state variables are
		 * supposed to be the same for all frames.
		 * 
		 * TODO: in case of IPOMDP frames, remove lower level agent models from the
		 * state var staging list before using it to create state vars for current level.
		 */
		this.stateVarStaging.clear();
		this.stateVarStaging.addAll(this.lowerLevelFrames.get(0).stateVarStaging);
		
		/* Set oppModelVarIndex */
		this.oppModelVarIndex = this.stateVarStaging.size();
		
		/* Finally, add the oppModel state var to the staging list */
		this.stateVarStaging.add(this.oppModel.getOpponentModelStateVar(this.oppModelVarIndex));
	}

}
