package thinclab.domainMaker.SPUDDHelpers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

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
			HashMap<String, String> oppObsForStateToDDRefMap) {

		/*
		 * Makes an empty ActionSPUDD object, adds the opponent policy prefix to
		 * each state variable. For obs transitions and policy transitions, apply the DDRefs
		 */
		
		ActionSPUDD prefixedSPUDD = new ActionSPUDD(actName, varContext);
		
		// For each state variable, apply policy DD prefix
		for (int i=0; i < varContext.getVarNames().length; i++) {
			
			try {
				prefixedSPUDD.putDD(varContext.getVarNames()[i], policyPrefix.getCopy());
			} 
			
			catch (VariableNotFoundException e) {
				System.err.println(e.getMessage());
				System.exit(-1);
			}
		}
		
		// For each opp obs transition, apply the DDRef
		Iterator<Entry<String, String>> DDRefIter = 
				oppObsForStateToDDRefMap.entrySet().iterator();
		while (DDRefIter.hasNext()) {
			Entry<String, String> entry = DDRefIter.next();
			
			try {
				prefixedSPUDD.putDD(entry.getKey(), new DDRef(entry.getValue()));
			} 
			
			catch (VariableNotFoundException e) {
				System.err.println(e.getMessage());
				System.exit(-1);
			}
		} // while (DDRefIter.hasNext())
		
		// For policy transition, apply policy DD Ref
		try {
			prefixedSPUDD.putDD(
					varContext.getOppPolicyName(),
					new DDRef(varContext.getPolicyDDRefName()));
		} 
		
		catch (VariableNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		
		return prefixedSPUDD;
	} // public static ActionSPUDD getPrefixedActionSPUDD
	
	public static ActionSPUDD getPrefixedActSPUDDWithOppTransitions(
			String actName,
			NextLevelVariablesContext varContext,
			DDTree policyPrefix,
			HashMap<String, String> oppObsForStateToDDRefMap) {
		/*
		 * Gets an actionSPUDD object with the opponent's state transitions applied to the
		 * respective state variables for proper actions.
		 */
		
		/*
		 * First we will get the prefixed actionSPUDD for the higher level agent.
		 * Then append the lower level agents default state transitions to the
		 * children of the prefix DDs.
		 */
		ActionSPUDD actSPUDD = ActionSPUDDFactory.getPrefixedActionSPUDD(
				actName,
				varContext,
				policyPrefix,
				oppObsForStateToDDRefMap);
		
		return actSPUDD;
	}
	
	
//	public static ActionSPUDD makeFromLowerLevelActionSPUDD(
//			NextLevelVariablesContext varContext,
//			String actionName,
//			String[] ddVars,
//			DDTree[] dds,
//			String[] oppObsDDNames,
//			double cost) {
//		
//		ActionSPUDD actionSPUDD = new ActionSPUDD(actionName, varContext, cost);
//		
//		// Add agent DDs
//		for (int i=0; i < ddVars.length; i++) {
//			
//			try {
//				actionSPUDD.putDD(ddVars[i], dds[i]);
//			} 
//			
//			catch (VariableNotFoundException e) {
//				System.err.println(e.getMessage());
//				System.exit(-1);
//			}
//		}
//		
//		// Add opponents observation DDs
//		String[] oppObsNames = varContext.getOppObsNames();
//		
//		for (int i=0; i < oppObsNames.length; i++) {
//			
//			try {
//				actionSPUDD.putDD(oppObsNames[i], new DDRef(oppObsDDNames[i]));
//			} 
//			
//			catch (VariableNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		// Add opponents policy transition
//		try {
//			actionSPUDD.putDD(varContext.getOppPolicyName(), new DDRef(policyDDName));
//		} 
//		
//		catch (VariableNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		// Keep other DDs same
//		actionSPUDD.fillNullDDs();
//		return actionSPUDD;
//	}
}
