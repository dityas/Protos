package thinclab.domainMaker.SPUDDHelpers;

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
}
