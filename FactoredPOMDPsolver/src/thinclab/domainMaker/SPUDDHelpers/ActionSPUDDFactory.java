package thinclab.domainMaker.SPUDDHelpers;

import java.util.Arrays;

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
	
	public static ActionSPUDD makeFromLowerLevelActionSPUDD(
			NextLevelVariablesContext varContext,
			String actionName,
			String[] ddVars,
			DDTree[] dds,
			String policyDDName,
			String[] oppObsDDNames,
			double cost) {
		
		ActionSPUDD actionSPUDD = new ActionSPUDD(actionName, varContext, cost);
		
		for (int i=0; i < ddVars.length; i++) {
			
			try {
				actionSPUDD.putDD(ddVars[i], dds[i]);
			} 
			
			catch (VariableNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String[] oppObsNames = varContext.getOppObsNames();
//		System.out.println(Arrays.deepToString(oppObsNames));
//		System.out.println(varContext.varNameSet.toString());
		
		for (int i=0; i < oppObsNames.length; i++) {
			
			try {
				actionSPUDD.putDD(oppObsNames[i], new DDRef(oppObsDDNames[i]));
			} 
			
			catch (VariableNotFoundException e) {
				// TODO Auto-generated catch block
//				System.out.println("Breaking here");
				e.printStackTrace();
			}
		}
		
		actionSPUDD.fillNullDDs();
		return actionSPUDD;
	}
}
