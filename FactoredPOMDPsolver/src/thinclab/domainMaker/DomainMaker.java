package thinclab.domainMaker;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import thinclab.domainMaker.ddHelpers.DDMaker;

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
				+ this.rewardDef
				+ this.newLine + "discount 0.9"
				+ this.newLine + "tolerance 0.001" + this.newLine;
	}
	
	public void writeToFile(String domainFile) {
		/*
		 * Writes out the SPUDD file 
		 */
		try {
			PrintWriter domWriter = new PrintWriter(new FileWriter(domainFile));
			domWriter.println(this.domainString);
			domWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("WRITE ERROR: " + domainFile);
		}
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
