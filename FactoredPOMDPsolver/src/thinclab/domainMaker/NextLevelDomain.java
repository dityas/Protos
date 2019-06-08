package thinclab.domainMaker;

import thinclab.domainMaker.SPUDDHelpers.NextLevelVariablesContext;
import thinclab.domainMaker.ddHelpers.DDMaker;
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
		this.ddmaker = new DDMaker();
		this.ddmaker.addFromNextLevelVariablesContext(this.nextLevelVarContext);
	}
	
	// ----------------------------------------------------------------------------
	// Override domain init writers
	
	public void writeVariablesDef() {
		/*
		 * Populates the variablesDef String in SPUDD format
		 */
		this.variablesDef = this.newLine;
		this.variablesDef += "(variables" + this.newLine;
		
		for (int v=0; v < this.nextLevelVarContext.getVarNames().length; v++) {
			this.variablesDef += "(" + this.nextLevelVarContext.getVarNames()[v] + " " 
					+ String.join(" ", this.nextLevelVarContext.getVarValNames()[v]) + ")" + this.newLine;
		}
		
		for (int v=0; v < this.nextLevelVarContext.getOppObsNames().length; v++) {
			this.variablesDef += "(" + this.nextLevelVarContext.getOppObsNames()[v] + " " 
					+ String.join(" ", this.nextLevelVarContext.getOppObsValNames()[v]) + ")" + this.newLine;
		}
		
		this.variablesDef += "(" + this.nextLevelVarContext.getOppPolicyName() + " "
				+ String.join(" ", this.nextLevelVarContext.getOppPolicyValNames()) + ")" + this.newLine;
		
		this.variablesDef += ")" + this.newLine;
	}
	
	public void writeObsDef() {
		/*
		 * Populates the obsDef String in SPUDD format
		 */
		this.obsDef = this.newLine;
		this.obsDef += "(observations" + this.newLine;
		
		for (int v=0; v < this.nextLevelVarContext.getObsNames().length; v++) {
			this.obsDef += "(" + this.nextLevelVarContext.getObsNames()[v] + " " 
					+ String.join(" ", this.nextLevelVarContext.getObsValNames()[v]) + ")" + this.newLine;
		}
		
		this.obsDef += ")" + this.newLine;
	}
	
	// ----------------------------------------------------------------------------
	
}
