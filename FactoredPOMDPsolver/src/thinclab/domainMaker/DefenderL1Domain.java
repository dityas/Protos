package thinclab.domainMaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import thinclab.domainMaker.SPUDDHelpers.ActionSPUDD;
import thinclab.domainMaker.SPUDDHelpers.NextLevelVariablesContext;
import thinclab.domainMaker.ddHelpers.DDTree;

public class DefenderL1Domain extends NextLevelDomain {
	
	public DefenderL1Domain(Domain lowerDomain) {
		this.lowerDomain = lowerDomain;
	}

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
	
	public void setOppObsDD() {
		HashMap<String, ActionSPUDD> opponentActSPUDDs = this.lowerDomain.actionSPUDDMap;
		HashMap<String, DDTree> nodeToObsDDMap = new HashMap<String, DDTree>();
		
		String[] oppPolicyNodes = this.lowerDomain.getPolicyValNames();
		for (int i = 0; i < oppPolicyNodes.length; i++) {
			String node = oppPolicyNodes[i];
			String actName = node.split("-")[2];
			nodeToObsDDMap.put(node, opponentActSPUDDs.get(actName).varToDDMap.get("OBS"));
		}
		
		DDTree obsDD = this.ddmaker.getDDTreeFromSequence(
				new String[] {this.nextLevelVarContext.getOppPolicyName()});
		Iterator<Entry<String, DDTree>> nodeToDDIter = nodeToObsDDMap.entrySet().iterator();
		while (nodeToDDIter.hasNext()) {
			Entry<String, DDTree> entry = nodeToDDIter.next();
			try {
				DDTree dd = entry.getValue().getCopy();
				dd.renameVar("OBS'", (this.nextLevelVarContext.oppObsNames[0] + "'"));
				obsDD.setDDAt(entry.getKey(), dd);
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		this.oppObs = obsDD;
	}
	
	public void makeRewardDD() {
		
	}
	
	public void makeActionsSPUDD() {
		
	}
	
	public void makeBeliefsSPUDD() {
		
	}

}
