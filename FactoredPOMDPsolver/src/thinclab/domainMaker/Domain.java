package thinclab.domainMaker;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import thinclab.domainMaker.SPUDDHelpers.ActionSPUDD;
import thinclab.domainMaker.SPUDDHelpers.BeliefSPUDD;
import thinclab.domainMaker.SPUDDHelpers.VariablesContext;
import thinclab.domainMaker.ddHelpers.DDMaker;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.policyhelper.PolicyExtractor;
import thinclab.policyhelper.PolicyGraph;
import thinclab.symbolicperseus.POMDP;

public abstract class Domain {
	/*
	 * Abstract class for L0Domains
	 */
	public String newLine = "\r\n";
	
	/*
	 * These strings contain the actual sections written in SPUDD format. Methods defined
	 * later are supposed to fill them in. In the end, a single method will just append these
	 * to the domainString variable seperated by new lines. The whole point of this is to make it
	 * more flexible for the subclasses to write these sections but still maintain some
	 * automation so that the subclass doesn't end up writing the whole domain by itself.
	 */
	public String variablesDef;
	public String obsDef;
	public String beliefSection;
	public String actionSection;
	public String rewardSection;
	
	// The string containing the whole SPUDD file.
	public String domainString;
	
	// --------------------------------------------------------------------
	// SPUDD objects
	
	/*
	 * Belief SPUDD objects
	 * 
	 * The belief SPUDD objects contain the definitions of the initial belief and adjunct beliefs.
	 * Has to be populated by the makeBeliefsSPUDD method to be implemented in the subclass
	 */
	public BeliefSPUDD beliefSPUDD;
	public List<BeliefSPUDD> otherBeliefSPUDDs = new ArrayList<BeliefSPUDD>();
	
	/*
	 * Action SPUDD map
	 * 
	 * This map contains a mapping from actionNames to ActionSPUDD objects. 
	 * Should be populated by the makeActionsSPUDD method implemented in the subclass
	 */
	public HashMap<String, ActionSPUDD> actionSPUDDMap = new HashMap<String, ActionSPUDD>();
	
	
	public DDTree rewardFn;
	
	
	public DDMaker ddmaker = new DDMaker();
	public VariablesContext varContext;
	
	// --------------------------------------------------------------------
	
	// Solver stuff
	public POMDP pomdp;
	
	// Policy Stuff
	
	public PolicyExtractor policyExtractor;
	public PolicyGraph policyGraph;
	
	
	// --------------------------------------------------------------------
	
	// Domain creation methods
	
	public void writeVariablesDef() {
	/*
	 * Populates the variablesDef String in SPUDD format
	 */
		this.variablesDef = this.newLine;
		this.variablesDef += "(variables" + this.newLine;
		
		for (int v=0; v < this.varContext.getVarNames().length; v++) {
			this.variablesDef += "(" + this.varContext.getVarNames()[v] + " " 
					+ String.join(" ", this.varContext.getVarValNames()[v]) + ")" + this.newLine;
		}
		
		this.variablesDef += ")" + this.newLine;
	}

	public void writeObsDef() {
	/*
	 * Populates the obsDef String in SPUDD format
	 */
		this.obsDef = this.newLine;
		this.obsDef += "(observations" + this.newLine;
		
		for (int v=0; v < this.varContext.getObsNames().length; v++) {
			this.obsDef += "(" + this.varContext.getObsNames()[v] + " " 
					+ String.join(" ", this.varContext.getObsValNames()[v]) + ")" + this.newLine;
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
		
		Iterator<Entry<String, ActionSPUDD>> actSPUDD = this.actionSPUDDMap.entrySet().iterator();
		while (actSPUDD.hasNext()) {
			this.actionSection += this.newLine;
			this.actionSection += actSPUDD.next().getValue().toSPUDD();
		}

	}
	
	public void writeReward() {
		this.rewardSection = this.newLine;
		this.rewardSection += "reward" + this.newLine;
		this.rewardSection += this.rewardFn.toSPUDD() + this.newLine;
	}
	
	// ----------------------------------------------------------------------------------------
	/*
	 * Abstract methods to be implemented in the subclass
	 */
	public abstract void makeVarContext();
	public abstract void makeBeliefsSPUDD();
	public abstract void makeActionsSPUDD();
	public abstract void makeRewardDD();
	
	// ----------------------------------------------------------------------------------------
	
	public void makeAll() {
		this.makeVarContext();
		this.makeDDMaker();
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
	
	public void makeDDMaker() {
		
		this.ddmaker = new DDMaker();
		this.ddmaker.addFromVariablesContext(this.varContext);
	} // public void makeDDMaker
	
	// --------------------------------------------------------------------------
}
