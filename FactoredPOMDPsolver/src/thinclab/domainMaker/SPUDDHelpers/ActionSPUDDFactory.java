package thinclab.domainMaker.SPUDDHelpers;

import java.util.Arrays;
import java.util.HashMap;

import thinclab.domainMaker.ddHelpers.DDRef;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.VariableNotFoundException;

public class ActionSPUDDFactory {
	/*
	 * Factory for getting ActionSPUDD objects
	 */
	
	public static ActionSPUDD getActionSPUDD(VariablesContext varContext,
								 			 String actionName,
								 			 String[] ddVars,
								 			 DDTree[] dds,
								 			 double cost) {
		ActionSPUDD actionSPUDD = new ActionSPUDD(actionName,
												  varContext,
												  cost);
		
		for (int i=0; i < ddVars.length; i++) {
			try {
				actionSPUDD.putDD(ddVars[i], dds[i]);
			} 
			catch (VariableNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		actionSPUDD.fillNullDDs();
		return actionSPUDD;
	} // public static ActionSPUDD getActionSPUDD
	
	public static ActionSPUDD getPrefixedActionSPUDD(
			String actName,
			NextLevelVariablesContext varContext,
			DDTree policyPrefix,
			HashMap<String, DDRef> oppObsMap) {

		/*
		 * Makes an empty ActionSPUDD object, adds the opponent policy prefix to
		 * each state variable except policy transitions and opp observation
		 * transitions.
		 */
		
		ActionSPUDD prefixedSPUDD = new ActionSPUDD(actName, varContext);
		
		// Prefix all state variables
		for (int i=0; i < varContext.getVarNames().length; i++) {
			
			try {
				prefixedSPUDD.putDD(varContext.getVarNames()[i], policyPrefix.getCopy());
			} 
			
			catch (VariableNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Apply oppObs transitions
		
		return prefixedSPUDD;
	}
	
	
	public static ActionSPUDD makeFromLowerLevelActionSPUDD(
			NextLevelVariablesContext varContext,
			String actionName,
			String[] ddVars,
			DDTree[] dds,
			String policyDDName,
			String[] oppObsDDNames,
			double cost) {
		
		ActionSPUDD actionSPUDD = new ActionSPUDD(actionName, varContext, cost);
		
		// Add agent DDs
		for (int i=0; i < ddVars.length; i++) {
			
			try {
				actionSPUDD.putDD(ddVars[i], dds[i]);
			} 
			
			catch (VariableNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Add opponents observation DDs
		String[] oppObsNames = varContext.getOppObsNames();
		
		for (int i=0; i < oppObsNames.length; i++) {
			
			try {
				actionSPUDD.putDD(oppObsNames[i], new DDRef(oppObsDDNames[i]));
			} 
			
			catch (VariableNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Add opponents policy transition
		try {
			actionSPUDD.putDD(varContext.getOppPolicyName(), new DDRef(policyDDName));
		} 
		
		catch (VariableNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Keep other DDs same
		actionSPUDD.fillNullDDs();
		return actionSPUDD;
	}
}
