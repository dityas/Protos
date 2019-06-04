package thinclab.domainMaker.SPUDDHelpers;

import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.VariableNotFoundException;

public class BeliefSPUDDFactory {

	public static BeliefSPUDD getBeliefSPUDD(VariablesContext varContext,
			String[] ddVars,
			DDTree[] dds) {

		BeliefSPUDD bSPUDD = new BeliefSPUDD(varContext);

		for (int i = 0; i < ddVars.length; i++) {

			try {
				bSPUDD.putDD(ddVars[i], dds[i]);
			}

			catch (VariableNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} // for (int i=0; i < ddVars.length; i ++)

		return bSPUDD;
	} // public static BeliefSPUDD getBeliefSPUDD

	public static BeliefSPUDD getAdjunctBeliefSPUDD(VariablesContext varContext,
			String[] ddVars,
			DDTree[] dds,
			String adjName) {

		BeliefSPUDD bSPUDD = BeliefSPUDDFactory.getBeliefSPUDD(varContext, ddVars, dds);
		bSPUDD.setAdjName(adjName);
		return bSPUDD;
	} // public static BeliefSPUDD getAdjunctBeliefSPUDD
	
	public static BeliefSPUDD getAdjunctBeliefSPUDD(BeliefSPUDD prevBSPUDD,
			String[] ddVars,
			DDTree[] dds,
			String adjName) {
		
		for (int i = 0; i < ddVars.length; i++) {

			try {
				prevBSPUDD.putDD(ddVars[i], dds[i]);
			}

			catch (VariableNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} // for (int i=0; i < ddVars.length; i ++)
		
		return prevBSPUDD;
	} // public static BeliefSPUDD getAdjunctBeliefSPUDD

}
