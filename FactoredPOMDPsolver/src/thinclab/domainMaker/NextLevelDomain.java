package thinclab.domainMaker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import thinclab.domainMaker.SPUDDHelpers.ActionSPUDD;
import thinclab.domainMaker.SPUDDHelpers.NextLevelVariablesContext;
import thinclab.domainMaker.ddHelpers.DDMaker;
import thinclab.domainMaker.ddHelpers.DDTools;
import thinclab.domainMaker.ddHelpers.DDTree;

public abstract class NextLevelDomain extends Domain {
	/*
	 * Base class for L1 domains
	 */
	public DDTree oppPolicy;
	public DDTree oppObs;
	
	public String oppPolicyDDDef;
	public String oppObsDDDef;
	
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
	
	// Set opponent policy variables
	
	public void setOppPolicyDD() {
		this.oppPolicy = this.lowerDomain.getPolicyGraphDD();
	}
	
	public abstract void setOppObsDD();
	
	public void writeOppPolicyDD() {
		this.oppPolicyDDDef = "" + this.newLine;
		this.oppPolicyDDDef += DDTools.defineDDInSPUDD("oppPolicy", this.oppPolicy);
		this.oppPolicyDDDef += this.newLine;
	}
	
	public void writeOppObsDD() {
		this.oppObsDDDef = "" + this.newLine;
		this.oppObsDDDef += DDTools.defineDDInSPUDD("oppObs", this.oppObs);
		this.oppObsDDDef += this.newLine;
	}
	
	// ------------------------------------------------------------------------------
	
	public void makeAll() {
		this.makeVarContext();
		this.makeDDMaker();
		this.setOppPolicyDD();
		this.setOppObsDD();
		this.writeVariablesDef();
		this.writeObsDef();
		this.makeBeliefsSPUDD();
		this.makeActionsSPUDD();
		this.makeRewardDD();
		
		this.writeBeliefs();
		this.writeOppPolicyDD();
		this.writeActions();
		this.writeReward();
		
		this.domainString = "";
		this.domainString += this.variablesDef + this.newLine;
		this.domainString += this.obsDef + this.newLine;
		this.domainString += this.beliefSection + this.newLine;
		this.domainString += "unnormalized" + this.newLine;
		this.domainString += this.oppPolicyDDDef + this.newLine;
		this.domainString += this.actionSection + this.newLine;
		this.domainString += this.rewardSection;
		this.domainString += "tolerance 0.001" + this.newLine;
		this.domainString += "discount 0.9";
	}
	
}
