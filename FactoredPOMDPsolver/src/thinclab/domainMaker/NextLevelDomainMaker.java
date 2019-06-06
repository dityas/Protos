package thinclab.domainMaker;

import thinclab.domainMaker.ddHelpers.DDTree;

public abstract class NextLevelDomainMaker extends DomainMaker {
	/*
	 * Base class for L1 domains
	 */
	public DDTree oppPolicy;
	public DDTree oppObs;
	
	public DomainMaker lowerDomain; 
	
	// ----------------------------------------------------------------------------
	// Set values based on lower domain
	
	public void setCommonVariables() {
		this.variables = this.lowerDomain.variables;
		this.varValues = this.lowerDomain.varValues;
		this.observations = this.lowerDomain.observations;
		this.obsValues = this.lowerDomain.obsValues;
	}
	
	public void setLowerDomain(DomainMaker lowerDomain) {
		this.lowerDomain = lowerDomain;
	}
	
	public void setOppPolicy(DDTree oppPolicy) {
		this.oppPolicy = oppPolicy;
	}
	
	public void setOppObs(DDTree oppObs) {
		this.oppObs = oppObs;
	}
	
}
