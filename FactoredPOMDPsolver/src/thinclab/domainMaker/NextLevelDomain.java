package thinclab.domainMaker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import thinclab.domainMaker.SPUDDHelpers.ActionSPUDD;
import thinclab.domainMaker.SPUDDHelpers.NextLevelVariablesContext;
import thinclab.domainMaker.ddHelpers.DDMaker;
import thinclab.domainMaker.ddHelpers.DDTools;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.DDNotDefinedException;

public abstract class NextLevelDomain extends Domain {
	/*
	 * Base class for higher level domains
	 * 
	 * Procedure for making Next Level Domain:
	 * 
	 * 1) Subclass NextLevelDoman class
	 * 		Constructor should set the this.lowerDomain variable to
	 * 		the lower domain of the opponent
	 * 
	 * 2) Implement the *makeVarContext* abstract method of the Domain
	 * 	  superclass 
	 * 		This method should set the this.nextLevelVarContext variable
	 * 		to a NextLevelVariblesContext object containing information about
	 * 		the agents and his opponents variables
	 * 
	 * 3) Implement the *setObsDDs* abstract method
	 * 		This method should populate the this.oppObs hashmap. It contains the
	 * 		transitions for opponents observations. The hashmap should be of the format
	 * 		<ddName, obsDD>
	 */
	
	// ------------------------------------------------------------------------------
	
	// Vars for storing opponent information
	
	// DDs for opponents information
	public DDTree oppPolicy;
	public HashMap<String ,DDTree> oppObs = new HashMap<String, DDTree>();
	
	// Parallel String arrays for storing opponent observation information
	public String[] orignalOppObsNames;
	public String[] currentOppObsNames;
	
	
	// DD names for policy DDs and obs DDs of opponent
	public String oppPolicyDDDef;
	public String oppObsDDDef;
	
	// ------------------------------------------------------------------------------
	public NextLevelVariablesContext nextLevelVarContext;
	public Domain lowerDomain;
	
	// ------------------------------------------------------------------------------
	// Abstract methods
	
	/*
	 * This method should populate the this.oppObs hashmap. The hashmap contains DDs
	 * which handle the opponents observation variables 
	 */
	public abstract void setOppObsDDs();
	
	// ------------------------------------------------------------------------------
	
	/*
	 * When defining DDs for opponents observation transitions, the names have to be 
	 * specific so that the other functions can use those in ActionSPUDD or other DD
	 * manipulation places. This function makes it more easier. It simply lower cases
	 * the obs variables name and append "opp" to it.
	 */
	public String getObsDDRefName(String obsVarName) {
		return "opp" + obsVarName.toLowerCase();
	}
	
	/*
	 * The lower level actionSPUDD objects contain the state transitions for each state
	 * variable. In case of higher level adversary, unless his action overrides these transitions, these
	 * they still take place. So the following method maps these old transitions to the
	 * policy nodes which now represent the actions taken by the lower level adversary. And returns
	 * an ActionSPUDD object with these varibles prefixed with the policy nodes DD
	 */
	public void getActionSPUDDTemplateWithPrefixes(String actName) {
		
		// Make the prefix DDs defining adversary actions for next level state transitions
		DDTree policyDDHead = this.ddmaker.getDDTreeFromSequence(new String[] {"OPP_POLICY"});
		
		ActionSPUDD templateSPUDD = 
		
	}
	
	// ------------------------------------------------------------------------------
	
	// ----------------------------------------------------------------------------
	// Initialization methods to help sub class in domain definition
	
	// initialize dd makes
	public void makeDDMaker() {
		this.ddmaker = new DDMaker();
		this.ddmaker.addFromNextLevelVariablesContext(this.nextLevelVarContext);
	}
	
	// populate oppObs map with appropriate variable names and null DDs
	public void initializeOppObsDDMap() {
		String[] oppObsNames = this.nextLevelVarContext.getOppObsNames();
		
		for (int i=0; i < oppObsNames.length; i++) {
			this.oppObs.put(oppObsNames[i].toLowerCase(), null);
		}
	}
	
	// makes opponents obs var -> higher level state var -> DDRef mapping
	public void makeOppObsVarMappings() {
		
	}
	
	// Driver function for calling all initialization methods
	public void initializationDriver() {
		this.makeVarContext();
		this.makeDDMaker();
		this.initializeOppObsDDMap();
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
	
	public void writeOppPolicyDD() {
		this.oppPolicyDDDef = "" + this.newLine;
		this.oppPolicyDDDef += DDTools.defineDDInSPUDD("oppPolicy", this.oppPolicy);
		this.oppPolicyDDDef += this.newLine;
	}
	
	public void writeOppObsDDs() throws DDNotDefinedException {
		Iterator<Entry<String, DDTree>> obsDDs = this.oppObs.entrySet().iterator();
		
		while (obsDDs.hasNext()) {
			Entry<String, DDTree> entry = obsDDs.next();
			
			if (entry.getValue() == null) {
				throw new DDNotDefinedException("For " + entry.getKey());
			}
			
			this.oppObsDDDef = "" + this.newLine;
			this.oppObsDDDef += DDTools.defineDDInSPUDD(entry.getKey(), entry.getValue());
			this.oppObsDDDef += this.newLine;
		}
	}
	
	// ------------------------------------------------------------------------------
	
	public void makeAll() {
		this.initializationDriver();
		this.setOppPolicyDD();
		this.setOppObsDDs();
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
