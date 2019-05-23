package thinclab.ddmaker;

public abstract class DomainMaker {
	/*
	 * Abstract class for L0Domains
	 */
	public String newLine = "\r\n";
	
	public String[] variables;
	public String[][] varValues;
	public String[] observations;
	public String[][] obsValues;
	
	public String variablesDef;
	public String obsDef;
	public String initDef;
	public String actionsDef;
	public String rewardDef;
	
	public String domainString;
	
	public DDMaker ddmaker = new DDMaker();
	
	public void writeVariablesDef() {
	/*
	 * Populates the variablesDef String in SPUDD format
	 */
		this.variablesDef = this.newLine;
		this.variablesDef += "(variables" + this.newLine;
		
		for (int v=0; v < this.variables.length; v++) {
			this.variablesDef += "(" + this.variables[v] + " " 
					+ String.join(" ", this.varValues[v]) + ")" + this.newLine;
		}
		
		this.variablesDef += ")" + this.newLine;
	}

	public void writeObsDef() {
	/*
	 * Populates the obsDef String in SPUDD format
	 */
		this.obsDef = this.newLine;
		this.obsDef += "(observations" + this.newLine;
		
		for (int v=0; v < this.observations.length; v++) {
			this.obsDef += "(" + this.observations[v] + " " 
					+ String.join(" ", this.obsValues[v]) + ")" + this.newLine;
		}
		
		this.obsDef += ")" + this.newLine;
	}

	public abstract void writeinitDef();
	public abstract void writeActionsDef();
	public abstract void writeRewardDef();
	
	public void makeDomain() {
		
		this.writeVariablesDef();
		this.writeObsDef();
		this.writeinitDef();
		this.writeActionsDef();
		this.writeRewardDef();
		
		this.domainString = this.variablesDef
				+ this.obsDef
				+ this.initDef
				+ this.newLine + "unnormalized" + this.newLine
				+ this.actionsDef
				+ this.rewardDef;
	}
	
	public void addVariablesToDDMaker() {
		for (int v=0; v<this.variables.length; v++) {
			this.ddmaker.addVariable(this.variables[v], this.varValues[v]);
		}
		
		for (int o=0; o<this.observations.length; o++) {
			this.ddmaker.addVariable(this.observations[o], this.obsValues[o]);
		}
		
		this.ddmaker.primeVariables();
	}

}
