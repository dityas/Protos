/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.belief;

import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.frameworks.Framework;
import thinclab.frameworks.IPOMDP;
import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public class FullInteractiveBeliefExpansion extends FullBeliefExpansion {
	/*
	 * Search the full interactive belief space for IPOMDPs
	 */
	
	private static final Logger logger = Logger.getLogger(FullInteractiveBeliefExpansion.class);
	
	// --------------------------------------------------------------------------------------------
	
	public FullInteractiveBeliefExpansion(Framework f) {
		/*
		 * Set properties and all that
		 */
		super(f, ((IPOMDP) f).mjLookAhead);
		
		logger.debug("Initialized full interactive belief search");
	}
	
	// --------------------------------------------------------------------------------------------
	
	@Override
	public DD beliefUpdate(
			Framework f, 
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
