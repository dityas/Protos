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

import cern.colt.Arrays;
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
			OpponentModel oppModel = this.getOpponentModel();
		} 
		
		catch (SolverException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		
	}
	
	// ------------------------------------------------------------------------------------------
	
	public List<InteractiveStateVar> makeInteractiveStateSpace(HashSet<OpponentModel> uniqueModels) {
		/*
		 * Constructs the IS space from unique opponent models and physical states 
		 * 
		 * For the look ahead solver, the state space will differ at every t. So we will need
		 * to construct a new IS space for every horizon
		 */
		List<InteractiveStateVar> ISVars = new ArrayList<InteractiveStateVar>();
		
		/*
		 * For each unique model and state var, make a new IS
		 */
		Iterator<OpponentModel> uniqueModelsIter = uniqueModels.iterator();
		while (uniqueModelsIter.hasNext()) {
			OpponentModel opponentModel = (OpponentModel) uniqueModelsIter.next();
			
			for (int s=0; s < this.nStateVars; s++) {
				ISVars.add(new InteractiveStateVar(this.stateVars[s], opponentModel));
			}
			
		} /* opponentModels iterator */
		
		return ISVars;
	}

}
