package thinclab.domainMaker.SPUDDHelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;


public class NextLevelVariablesContext extends VariablesContext {
	
	// Variables for describing opponents policy
	public String oppPolicyName = "OPP_POLICY";
	public String[] oppPolicyValNames;
	
	/*
	 * These variables store the opponents observation variables and their
	 * values. For the agent's model, these will be included in the states so that
	 * the agent can model the opponent's observation based on his observation function
	 */
	public String[] oppObsForStateNames;
	public String[][] oppObsForStateValNames;
	
	/*
	 * These variables contain the corresponding DD names and orignal observation
	 * names for the lower level adversary. The obs names should be same as those given
	 * by the opponent's varContext.
	 */
	public String[] oppOrigObsNames;
	public String[] oppObsDDRefNames;
	
	public VariablesContext opponentsContext;
	
	/*
	 * Handles all variable naming and assignments for higher level domain
	 */

	public NextLevelVariablesContext(VariablesContext opponentsContext,
			String[] obsNames,
			String[][] obsValNames,
			String[] oppPolicyValNames) {
		/*
		 * Builds varContext for higher level adversary. Assumes that the state
		 * space stays common for both and only observation space changes
		 * 
		 * Args:
		 * 		obsNames:			L1 obsNames for agent
		 * 		obsValNames:		Values for agent's obsNames
		 * 		oppPolicyValNames:	Array of node names of opponents policy
		 * 		opponentsContext:	The varContext of lower level opponent. This will
		 * 							be used to get the state variables
		 */
		
		/*
		 * Initialize empty superclass. Doing this because the super class takes the same
		 * state vars and their values as the opponents context and we don't want to create
		 * a conflict between them for the getters. It is a messed up way of making it work
		 * I know, but I need to graduate by September.
		 */
		super();
		
		this.opponentsContext = opponentsContext;
		
		/*
		 *  Get varNames and varValNames from opponents context since these will be the
		 *  same for the domain
		 */
		
		this.varNames = this.opponentsContext.getVarNames();
		this.varValNames = this.opponentsContext.getVarValNames();
		
		// Assign agents obsNames and obsValNames
		this.obsNames = obsNames;
		this.obsValNames = obsValNames;
		this.oppPolicyValNames = oppPolicyValNames;
		
		// populate orignal obs names from the opponents variable context
		this.oppOrigObsNames = this.opponentsContext.getObsNames().clone();
		
		// populate oppObsForStateNames and DDRef names
		this.oppObsForStateNames = this.opponentsContext.getObsNames().clone();
		this.oppObsDDRefNames = this.opponentsContext.getObsNames().clone();
		for (int i = 0; i < oppObsForStateNames.length; i++) {
			oppObsForStateNames[i] = "OPP_" + oppObsForStateNames[i];
			oppObsDDRefNames[i] = "opp" + oppObsDDRefNames[i].toLowerCase();
		}
		
		// populate oppObsForStateValNames
		this.oppObsForStateValNames = this.opponentsContext.getObsValNames().clone();

		// populate state var keys
		for (int i = 0; i < this.varNames.length; i++) {
			this.varNameSet.add(this.varNames[i]);
		}

		// populate obs var keys
		for (int i = 0; i < this.obsNames.length; i++) {
			this.varNameSet.add(this.obsNames[i]);
		}
		
		for (int i = 0; i < this.oppObsForStateNames.length; i++) {
			this.varNameSet.add(this.oppObsForStateNames[i]);
		}
		
		this.varNameSet.add(this.oppPolicyName);
//		System.out.println(this.varNameSet.toString());
	} // public VariablesContext

	/**
	 * @return the varNames
	 */
	public String[] getStateVarNames() {
		List<String> varNamesList = new ArrayList<String>();
		varNamesList.addAll(Arrays.asList(this.varNames));
		varNamesList.addAll(Arrays.asList(this.oppObsForStateNames));
		varNamesList.add(this.oppPolicyName);
		return varNamesList.toArray(new String[varNamesList.size()]);
	}
	
	@Override
	public String[] getVarNames() {
		return this.varNames.clone();
	}

	/**
	 * @return the varValNames
	 */
	@Override
	public String[][] getVarValNames() {
		return this.varValNames.clone();
	}

	/**
	 * @return the obsNames
	 */
	@Override
	public String[] getObsNames() {
		return this.obsNames.clone();
	}

	/**
	 * @return the obsValNames
	 */
	@Override
	public String[][] getObsValNames() {
		return this.obsValNames.clone();
	}

	/**
	 * @return the oppPolicyName
	 */
	public String getOppPolicyName() {
		return this.oppPolicyName;
	}

	/**
	 * @return the oppPolicyValNames
	 */
	public String[] getOppPolicyValNames() {
		return oppPolicyValNames.clone();
	}

	/**
	 * @return the oppObsNames
	 */
	public String[] getOppOrigObsNames() {
		return this.oppOrigObsNames.clone();
	}

	/**
	 * @return the oppObsValNames
	 */
	public String[][] getOppOrigObsValNames() {
		return this.getOppOrigObsValNames().clone();
	}
	
	@Override
	public boolean hasVariable(String varName) {
		return this.varNameSet.contains(varName);
	}
	
	public String[] getOppObsForStateNames() {
		return this.oppObsForStateNames.clone();
	}
	
	public String[][] getOppObsForStateValNames() {
		return this.oppObsForStateValNames.clone();
	}
	
	public String getPolicyDDRefName() {
		return this.oppPolicyName.toLowerCase();
	}
 	
	/*
	 * Used to get the corresponding DDRef name for the origObsName
	 */
	public String getOppObsDDRefFromOrigObsName(String origObsName) {
		int i = ArrayUtils.indexOf(this.oppOrigObsNames, origObsName);
		return this.oppObsDDRefNames[i];
	}
	
	/*
	 * Get oppObsForStateName from oppOrigObsName
	 */
	public String getOppObsForStateNameFromOrigObsName(String origObsName) {
		int i = ArrayUtils.indexOf(this.oppOrigObsNames, origObsName);
		return this.oppObsForStateNames[i];
	}
	
}
