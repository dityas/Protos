/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.simulations;

import org.apache.log4j.Logger;

import thinclab.legacy.Config;
import thinclab.legacy.DD;
import thinclab.legacy.OP;
import thinclab.solvers.BaseSolver;
import thinclab.solvers.OnlineSolver;

/*
 * @author adityas
 *
 */
public class MultiAgentSimulation extends Simulation {

	/*
	 * Makes simulation API for multi agent setting
	 */
	
	private OnlineSolver l1Solver;
	
	private static final long serialVersionUID = -7628758614982159814L;
	private static final Logger LOGGER = Logger.getLogger(MultiAgentSimulation.class);
	
	// ---------------------------------------------------------------------------------------
	
	public MultiAgentSimulation(
			OnlineSolver ipomdpSolver, BaseSolver pomdpSolver, int interactions) {
		
		super();
		
		this.solver = pomdpSolver;
		this.l1Solver = ipomdpSolver;
		this.iterations = interactions;
		
		this.initializeState();
	}
	
	// ----------------------------------------------------------------------------------------
	
	@Override
	public void initializeState() {
		
		/*
		 * Sample state from one of the agents initial belief
		 */
		this.l1Solver.f.setGlobals();
		
		DD belief = this.l1Solver.f.getCurrentBelief();
		
		/* sample state from initial belief */
		int[][] stateConfig = OP.sampleMultinomial(belief, this.l1Solver.f.getStateVarIndices());
		
		DD state = Config.convert2dd(stateConfig);
		this.states.add(state.toDDTree());
		
		LOGGER.debug("Initial state sampled. Set to " + this.solver.f.toMap(state));
	}

}
