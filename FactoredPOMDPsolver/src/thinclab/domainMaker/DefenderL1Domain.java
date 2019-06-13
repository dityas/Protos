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
				this.lowerDomain.getPolicyValNames(),
				this.lowerDomain.getObsNames(), 
				this.lowerDomain.getObsValNames());
	}
	
	// --------------------------------------------------------------------------------------
	// Implement abstract method to populate the oppObs HashMap
	public void setOppObsDDs() {
		String[] oppObsNames = this.lowerDomain.varContext.getObsNames();
		System.out.println(Arrays.toString(oppObsNames));
		for (int i = 0; i < oppObsNames.length; i++) {
			this.setOppObsDDs(oppObsNames[i]);
		}
	}
	
	public void setOppObsDDs(String obsName) {
		
		HashMap<String, ActionSPUDD> opponentActSPUDDs = this.lowerDomain.actionSPUDDMap;
		HashMap<String, DDTree> nodeToObsDDMap = new HashMap<String, DDTree>();
		
		/*
		 * Access the ActionSPUDD object of the specific action corresponding to the
		 * policy node and get the DD for the obsName observation. Repeat for all
		 * policy nodes. This should really be in a seperate helper class or something
		 */
		String[] oppPolicyNodes = this.lowerDomain.getPolicyValNames();
		for (int i = 0; i < oppPolicyNodes.length; i++) {
			String node = oppPolicyNodes[i];
			String actName = node.split("-")[2];
//			System.out.println("Try to get " + obsName + " from " + opponentActSPUDDs.get(actName).varToDDMap);
			nodeToObsDDMap.put(node, opponentActSPUDDs.get(actName).varToDDMap.get(obsName));
		}
		
		/*
		 * Make prefix DD for policy variable
		 */
		DDTree obsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {this.nextLevelVarContext.getOppPolicyName()});
		
		/*
		 * Append the observation DDs to the respective policy nodes
		 */
		Iterator<Entry<String, DDTree>> nodeToDDIter = nodeToObsDDMap.entrySet().iterator();
		while (nodeToDDIter.hasNext()) {
			Entry<String, DDTree> entry = nodeToDDIter.next();
			System.out.println(entry);
			try {
				DDTree dd = entry.getValue().getCopy();
				dd.renameVar(obsName + "'", (this.nextLevelVarContext.oppObsNames[0] + "'"));
				obsDD.setDDAt(entry.getKey(), dd);
			}
			
			catch (Exception e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}			
		}
		
//		this.oppObs.put(this.nextLevelVarContext.getOppObsDDName(obsName), obsDD);

		/*
		 * Create reference for oppObs variable transitions
		 */
		this.oppObs.put(this.getObsDDRefName(obsName), obsDD);
	}
	
	// --------------------------------------------------------------------------------------
	
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
