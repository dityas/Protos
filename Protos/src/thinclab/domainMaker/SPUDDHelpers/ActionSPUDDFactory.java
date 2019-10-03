package thinclab.domainMaker.SPUDDHelpers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import thinclab.ddhelpers.DDRef;
import thinclab.ddhelpers.DDTree;
import thinclab.exceptions.VariableNotFoundException;

public class ActionSPUDDFactory {
	/*
	 * Factory class for getting ActionSPUDD objects.
	 * 
	 * For getting SPUDD objects for L0 domains (POMDPs), the static methods can be used.
	 * For ActionSPUDD objects of higher level models, it may be difficult to pass all the data structures
	 * related to the model into the static methods. In such cases, the ActionSPUDDFactory can be initialized
	 * once with the data structures as args to the constructor and its class methods can be used to get
	 * ActionSPUDD objects instead. 
	 */
	
	public NextLevelVariablesContext nextLevelVariablesContext;
	public DDTree policyPrefix;
	public HashMap<String, String> oppObsForStateToDDRefMap;
	public HashMap<String, String> policyNodeToActName;
	public HashMap<String, HashMap<String, DDTree>> actToVarToDD;
	
	public ActionSPUDDFactory(
			NextLevelVariablesContext varContext,
			DDTree policyPrefix,
			HashMap<String, String> oppObsForStateToDDRefMap,
			HashMap<String, String> policyNodeToActName,
			HashMap<String, HashMap<String, DDTree>> actToVarToDD) {
		/*
		 * Just set the class attributes to the given args
		 */
		this.nextLevelVariablesContext = varContext;
		this.policyPrefix = policyPrefix;
		this.oppObsForStateToDDRefMap = oppObsForStateToDDRefMap;
		this.policyNodeToActName = policyNodeToActName;
		this.actToVarToDD = actToVarToDD;
	} // public ActionSPUDDFactory
	
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
			HashMap<String, String> oppObsForStateToDDRefMap,
			HashMap<String, String> policyNodeToActName,
			HashMap<String, HashMap<String, DDTree>> actToVarToDD) {
		/*
		 * Gets an actionSPUDD object with the opponent's state transitions applied to the
		 * respective state variables for proper actions.
		 */
		
		/*
		 * First we will get the prefixed actionSPUDD for the higher level agent.
		 * Then append the opponent's default state transitions to the
		 * children of the prefix DDs.
		 */
		ActionSPUDD actSPUDD = ActionSPUDDFactory.getPrefixedActionSPUDD(
				actName,
				varContext,
				policyPrefix,
				oppObsForStateToDDRefMap);
		
		/*
		 * Now append DDs for opponents state transitions from the lower level ActionSPUDD objects.
		 * These DDs have already been extracted in the NextLevelDomain object.
		 */
		
		/* For each state variable of the agent's model */
		String[] stateVars = varContext.getVarNames();
		for (int i=0;i < stateVars.length; i++) {
			String varName = stateVars[i];
			
			/* For each policyNode in the opponent's policy */
			for (int p=0; p < varContext.oppPolicyValNames.length; p++) {
				String policyNode = varContext.oppPolicyValNames[p];
				DDTree DDToAppend = actToVarToDD.get(
						policyNodeToActName.get(
								policyNode)).get(varName);
				
				/* Append the DD to the required child */
				try {
//					System.out.println("Variable: " + varName);
					actSPUDD.varToDDMap.get(varName).setDDAt(policyNode, DDToAppend);
				} 
				
				catch (Exception e) {
					System.err.println(e.getMessage());
					System.exit(-1);
				}
			} // for policyNodes
		} // for stateVars
		
		return actSPUDD;
	} // public static ActionSPUDD getPrefixedActSPUDDWithOppTransitions
	
	public ActionSPUDD getPrefixedActSPUDDWithOppTransitions(String actName) {
		return ActionSPUDDFactory.getPrefixedActSPUDDWithOppTransitions(
				actName, 
				this.nextLevelVariablesContext, 
				this.policyPrefix.getCopy(), 
				this.oppObsForStateToDDRefMap, 
				this.policyNodeToActName, 
				this.actToVarToDD);
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
