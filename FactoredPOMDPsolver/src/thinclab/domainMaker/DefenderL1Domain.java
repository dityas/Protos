package thinclab.domainMaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	
	public void makeRewardDD() {
		
	}
	
	public void makeActionsSPUDD() {
		
	}
	
	public void makeBeliefsSPUDD() {
		
	}

}
