package thinclab.domainMaker;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import thinclab.domainMaker.SPUDDHelpers.ActionSPUDD;
import thinclab.domainMaker.SPUDDHelpers.BeliefSPUDD;
import thinclab.domainMaker.SPUDDHelpers.VariablesContext;
import thinclab.domainMaker.ddHelpers.DDMaker;
import thinclab.domainMaker.ddHelpers.DDTree;

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
	public String beliefSection;
	public String actionSection;
	public String rewardSection;
	
	public String domainString;
	
	// SPUDD objects
	public BeliefSPUDD beliefSPUDD;
	public List<BeliefSPUDD> otherBeliefSPUDDs = new ArrayList<BeliefSPUDD>();
	public List<ActionSPUDD> actionSPUDDs = new ArrayList<ActionSPUDD>();
	public DDTree rewardFn;
	
	
	public DDMaker ddmaker = new DDMaker();
	public VariablesContext varContext;
	
	// --------------------------------------------------------------------
	
	// Domain creation methods
	
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
	
	public void writeBeliefs() {
		/*
		 * Populates belief SPUDD string
		 */
		this.beliefSection = "";
		this.beliefSection += this.newLine;
		
		this.beliefSection += this.beliefSPUDD.toSPUDD() + this.newLine;
		
		Iterator<BeliefSPUDD> beliefIter = this.otherBeliefSPUDDs.iterator();
		while (beliefIter.hasNext()) {
			this.beliefSection += this.newLine;
			this.beliefSection += beliefIter.next().toSPUDD();
		}
		
	}
	
	public void writeActions() {
		this.actionSection = "";
		this.actionSection += this.newLine;
		
		Iterator<ActionSPUDD> actSPUDD = this.actionSPUDDs.iterator();
		while (actSPUDD.hasNext()) {
			this.actionSection += this.newLine;
			this.actionSection += actSPUDD.next().toSPUDD();
		}

	}
	
	public void writeReward() {
		this.rewardSection = this.newLine;
		this.rewardSection += "reward" + this.newLine;
		this.rewardSection += this.rewardFn.toSPUDD() + this.newLine;
	}
	
	// ----------------------------------------------------------------------------------------

	public abstract void makeBeliefsSPUDD();
	public abstract void makeActionsSPUDD();
	public abstract void makeRewardDD();
	
	// ----------------------------------------------------------------------------------------
	
	public void makeAll() {
		this.writeVariablesDef();
		this.writeObsDef();
		this.makeBeliefsSPUDD();
		this.makeActionsSPUDD();
		this.makeRewardDD();
		
		this.writeBeliefs();
		this.writeActions();
		this.writeReward();
		
		this.domainString = "";
		this.domainString += this.variablesDef + this.newLine;
		this.domainString += this.obsDef + this.newLine;
		this.domainString += this.beliefSection + this.newLine;
		this.domainString += "unnormalized" + this.newLine;
		this.domainString += this.actionSection + this.newLine;
		this.domainString += this.rewardSection;
		this.domainString += "tolerance 0.001" + this.newLine;
		this.domainString += "discount 0.9";
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
	
	// --------------------------------------------------------------------------
	
	// --------------------------------------------------------------------------
	
	// Tool init methods
	
	public void addVariablesToDDMaker() {
		
		for (int v=0; v<this.variables.length; v++) {
			this.ddmaker.addVariable(this.variables[v], this.varValues[v]);
		}
		
		for (int o=0; o<this.observations.length; o++) {
			this.ddmaker.addVariable(this.observations[o], this.obsValues[o]);
		}
		
		this.ddmaker.primeVariables();
	} // public void addVariablesToDDMaker
	
	public void makeVarContext() {
		this.varContext = new VariablesContext(
				this.variables,
				this.varValues,
				this.observations,
				this.obsValues);
				
	} // public void makeVarContext
	
	// --------------------------------------------------------------------------

}
