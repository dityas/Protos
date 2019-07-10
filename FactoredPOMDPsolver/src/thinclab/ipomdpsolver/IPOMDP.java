/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import thinclab.exceptions.ParserException;
import thinclab.exceptions.SolverException;
import thinclab.symbolicperseus.Belief;
import thinclab.symbolicperseus.BeliefSet;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.ParseSPUDD;

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
	
	public List<POMDP> lowerLevelFrames = new ArrayList<POMDP>();

	public IPOMDP(String fileName) {
		super(fileName);
	}
	
	public IPOMDP() {
		super();
	}
	
	public void initializeFromParsers(IPOMDPParser parsedFrame) throws ParserException {
		/*
		 * Initializes the IPOMDP from the thinclab.ipomdpsolver.IPOMDPParser object
		 */
		
		super.initializeFromParsers(parsedFrame);
		
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
			
		}
	}
	
	public void solveOpponentModels() throws SolverException {
		/*
		 * Calls IPBVI or PBVI on the lower level frames depending on whether they are IPOMDPs
		 * or POMDPs
		 */
		Iterator<POMDP> frameIterator = this.lowerLevelFrames.iterator();
		while (frameIterator.hasNext()) {
			POMDP opponentModel = frameIterator.next();
			
			/*
			 * Check if lower frame is POMDP or IPOMDP and call the solve method accordingly
			 */
			if (opponentModel instanceof IPOMDP) ((IPOMDP) opponentModel).solveIPBVI(15, 100);
			
			else if (opponentModel instanceof POMDP) {
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
	
	public OpponentModel[] getOpponentModels() throws SolverException {
		/*
		 * Computes the models of the lower level agents and makes
		 * (reachable beliefs X frames) number of opponent models 
		 */
		List<OpponentModel> oppModels = new ArrayList<OpponentModel>();
		
		this.solveOpponentModels();
		
		Iterator<POMDP> framIterator = this.lowerLevelFrames.iterator();
		while (framIterator.hasNext()) {
			POMDP opponentFrame = framIterator.next();
			
			List<DD[]> opponentBeliefSet = BeliefSet.getInitialReachableBeliefs(
					opponentFrame, 3);
			
			/*
			 * Make opponent model as (Belief X Frames). For each belief in belief set,
			 * make a new model for the belief and frame combination
			 */
			Iterator<DD[]> beliefIterator = opponentBeliefSet.iterator();
			while (beliefIterator.hasNext()) {
				DD[] beliefF = beliefIterator.next();
				/*
				 * Create new model
				 */
				oppModels.add(new OpponentModel(
						Belief.unFactorBeliefPoint(
								opponentFrame, 
								beliefF), 
						opponentFrame));
			} /* belief iterator */
			
		} /* frame iterator */
		
		return oppModels.toArray(new OpponentModel[oppModels.size()]);
	}
	
	public void solveIPBVI(int rounds, int numDpBackups) {
		/*
		 * Runs the interactive PBVI loop for solving the IPOMDP
		 */
		try {
			OpponentModel[] oppModels = this.getOpponentModels();
		} 
		
		catch (SolverException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		
	}

}
