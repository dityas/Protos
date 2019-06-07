package thinclab.domainMaker;

import thinclab.domainMaker.SPUDDHelpers.NextLevelVariablesContext;
import thinclab.domainMaker.ddHelpers.DDTree;

public abstract class NextLevelDomain extends Domain {
	/*
	 * Base class for L1 domains
	 */
	public DDTree oppPolicy;
	public DDTree oppObs;
	
	public NextLevelVariablesContext nextLevelVarContext;
	public Domain lowerDomain;
	
	// ----------------------------------------------------------------------------
	// Set values based on lower domain
	
	public void makeDDMaker() {
		
	}
	
}
