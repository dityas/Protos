package thinclab.domainMaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import thinclab.domainMaker.SPUDDHelpers.ActionSPUDD;
import thinclab.domainMaker.SPUDDHelpers.ActionSPUDDFactory;
import thinclab.domainMaker.SPUDDHelpers.NextLevelVariablesContext;
import thinclab.domainMaker.ddHelpers.DDTree;

public class DefenderL1Domain extends NextLevelDomain {
	
	// Set lowerDomain variable (Step 1) 
	public DefenderL1Domain(Domain lowerDomain) {
		this.lowerDomain = lowerDomain;
	}
	
	// Implement abstract method makeVarContext (Step 2)
	public void makeVarContext() {
		String[] obsNames = 
				new String[] {"DATA_ACCESSED",
						"PRIVS",
						"NET_TX"};
		
		String[][] obsValNames = 
				new String[][] {
						{"c", "nc", "fake", "none"},
						{"user", "admin"},
						{"high", "low"}
						};
						
		this.nextLevelVarContext = new NextLevelVariablesContext(
				this.lowerDomain.getVarContext(), 
				obsNames, 
				obsValNames, 
				this.lowerDomain.getPolicyValNames());
	}
		
	public void makeRewardDD() {
		
	}
	
	public void makeActionsSPUDD() {
		
		ActionSPUDD nopSPUDD = ActionSPUDDFactory.makeFromLowerLevelActionSPUDD(
				this.nextLevelVarContext, 
				"NOP", 
				new String[] {}, 
				new DDTree[] {}, 
				"oppPolicy",
				new String[] {"oppObs"}, 0.1);
		
		this.actionSPUDDMap.put("NOP", nopSPUDD);
		
	}
	
	public void makeBeliefsSPUDD() {
		
	}

}
