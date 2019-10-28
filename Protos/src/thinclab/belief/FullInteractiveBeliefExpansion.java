/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.belief;

import java.util.List;

import org.apache.log4j.Logger;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public class FullInteractiveBeliefExpansion extends FullBeliefExpansion {
	/*
	 * Search the full interactive belief space for IPOMDPs
	 */
	
	private static final long serialVersionUID = -5492971155889649353L;
	private static final Logger logger = Logger.getLogger(FullInteractiveBeliefExpansion.class);
	
	// --------------------------------------------------------------------------------------------
	
	public FullInteractiveBeliefExpansion(DecisionProcess f) {
		/*
		 * Set properties and all that
		 */
		super(f, ((IPOMDP) f).mjLookAhead);
		
		logger.debug("Initialized full interactive belief search");
	}
	
	// --------------------------------------------------------------------------------------------
	
	@Override
	public DD beliefUpdate(
			DecisionProcess f, 
			DD previousBelief, 
			String action,
			List<String> obs) {
		
		IPOMDP ip = (IPOMDP) f;
		try {
			
			DD nextBelief = 
					InteractiveBelief.staticL1BeliefUpdate(
							ip, 
							previousBelief, 
							action, 
							obs.toArray(
									new String[obs.size()]));
			
			logger.debug(InteractiveBelief.toStateMap(ip, previousBelief) + 
					" -[" + action + "]-" + obs + "-> " 
					+ InteractiveBelief.toStateMap(ip, nextBelief));
			
			return nextBelief;
		} 
		
		catch (ZeroProbabilityObsException e) { return null; }
		catch (VariableNotFoundException e) { 
			
			logger.error("While doing L1 belief update: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
			
	}

}
