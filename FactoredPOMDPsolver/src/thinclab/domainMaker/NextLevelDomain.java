package thinclab.domainMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import thinclab.domainMaker.SPUDDHelpers.ActionSPUDD;
import thinclab.domainMaker.SPUDDHelpers.NextLevelVariablesContext;
import thinclab.domainMaker.ddHelpers.DDMaker;
import thinclab.domainMaker.ddHelpers.DDTools;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.DDNotDefinedException;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.policyhelper.PolicyNode;

public abstract class NextLevelDomain extends Domain {
	/*
	 * Base class for higher level domains
	 * 
	 * Procedure for making Next Level Domain:
	 * 
	 * 1) Subclass NextLevelDoman class
	 * 		Constructor should set the this.lowerDomain variable to
	 * 		the lower domain of the opponent
	 * 
	 * 2) Implement the *makeVarContext* abstract method of the Domain
	 * 	  superclass 
	 * 		This method should set the this.nextLevelVarContext variable
	 * 		to a NextLevelVariblesContext object containing information about
	 * 		the agents and his opponents variables
	 * 
	 * 3) Implement the *setObsDDs* abstract method
	 * 		This method should populate the this.oppObs hashmap. It contains the
	 * 		transitions for opponents observations. The hashmap should be of the format
	 * 		<ddName, obsDD>
	 */
	
	// ------------------------------------------------------------------------------
	
	// Vars for storing opponent information
	
	// DDs for opponents information
	public DDTree oppPolicy;
	
	/*
	 * These following structures contain the maps for defining the opponents observation DDs
	 * The oppObsForStateDDDefMap contains map from <oppObsForStateDDRefName, oppObsForStateDD>
	 * The next map is used to get the reference names from the names given in the state vars.
	 */
	public HashMap<String, DDTree> oppObsForStateDDDefMap = new HashMap<String, DDTree>();
	public HashMap<String, String> oppObsStateToOppObsDDRef = new HashMap<String, String>();
	
	/*
	 * Maps between policy nodes and actions.
	 */
	public HashMap<String, List<String>> actionToPolicyNodeMap = 
			new HashMap<String, List<String>>();
	public HashMap<String, String> policyNodetoAction = 
			new HashMap<String, String>();
	
	/*
	 * Contains info about initial nodes in the policy graph
	 */
	public HashMap<String, Boolean> initNodesMap = new HashMap<String, Boolean>();
	
	/*
	 * Maps action names to respective state transition DDs. This is a nested hashmap of the
	 * format <action name> -> <state variable> -> <DD> .
	 *  
	 * 						Value
	 * 	Key			   ----------------------------	
	 * ------------    |						  |
	 * <actName1> |	-> |	<stateVar1>	->	<DD>  |
	 * <actName1> |	-> |	<stateVar2>	->	<DD>  |
	 * <actName1> |	-> |	<stateVar3>	->	<DD>  |
	 * <actName2> |	-> |	<stateVar1>	->	<DD>  |
	 * ------------    |	                      |
	 *  			   ----------------------------
	 * .
	 * 
	 * It might be useful to maintain this structure and populate it with the lower level 
	 * opponent's transitions for creating the ActionSPUDD of the agent
	 */
	public HashMap<String, HashMap<String, DDTree>> actToVarToDDMap = 
			new HashMap<String, HashMap<String,DDTree>>();
	
	// Parallel String arrays for storing opponent observation information
	public String[] orignalOppObsNames;
	public String[] currentOppObsNames;
	
	
	// DD names for policy DDs and obs DDs of opponent
	public String oppPolicyDDDef;
	public String oppObsDDDef;
	
	// ------------------------------------------------------------------------------
	public NextLevelVariablesContext nextLevelVarContext;
	public Domain lowerDomain;
	
	// ------------------------------------------------------------------------------
	
	/*
	 * The next level domain of the adversary will contain the observations of the lower
	 * level adversary as its state vars. These transitions have to be modelled. The
	 * following method will create a DD prefix from the opponent's policy nodes and 
	 * append respective observation functions from the lower level adversary's 
	 * ActionSPUDD objects.
	 * 
	 * Each observation var transition of the opponent should have a DD
	 * 
	 *   Eg:
	 *   
	 *   dd oppobs
	 *   (OPP_POLICY
	 *   	(node1
	 *   		<OBS DD for action repr by node 1>
	 *   	)
	 *   
	 *   	(node2
	 *   		<OBS DD for action repr by node 2>
	 *   	)
	 *   )
	 *   
	 * To do this, the oppObsForStateDDDefMap has to be populated.
	 * The following method does this.
	 */
	public void setOppObsForStateDDs() {
		
		// For each observation variable in the opponents domain
		String[] oppOrigObsNames = this.nextLevelVarContext.getOppOrigObsNames();
		for (int i=0;i < oppOrigObsNames.length; i++) {
			String obsName = oppOrigObsNames[i];
			// Create the policy node prefix
			DDTree policyNodePrefix = 
					this.ddmaker.getDDTreeFromSequence(
							new String[] {this.nextLevelVarContext.getOppPolicyName()}).getCopy();
			
			// For each ActionSPUDD
			Iterator<Entry<String, ActionSPUDD>> actSPUDDIter = 
					this.lowerDomain.actionSPUDDMap.entrySet().iterator();
			while (actSPUDDIter.hasNext()) {
				Entry<String, ActionSPUDD> entry = actSPUDDIter.next();
				ActionSPUDD theActSPUDD = entry.getValue();
				DDTree theDD = null;
				// Get the DD associated with obs var obsName for action entry.getKey()
				try {
					theDD = theActSPUDD.getDDForVar(obsName).getCopy();
				} 
				
				catch (VariableNotFoundException e) {
					System.err.println(e.getMessage());
					System.exit(-1);
				} 
				
				// append DD for each node associated with the action
				List<String> relatedNodes = this.actionToPolicyNodeMap.get(entry.getKey());
				
				if (relatedNodes == null) {
					continue;
				}
				
				Iterator<String> relatedNodesIter = relatedNodes.iterator();
				while (relatedNodesIter.hasNext()) {
					String policyNode = relatedNodesIter.next();
					
					try {
						policyNodePrefix.setDDAt(policyNode, theDD.getCopy());
						
						// Rename the origObsName to oppObsNameForState
						// The "'" is added to indicate a primed variable for the next step
						policyNodePrefix.renameVar(
								obsName + "'",
								this.nextLevelVarContext.getOppObsForStateNameFromOrigObsName(obsName) + "'");
					} 
					
					catch (Exception e) {
						System.err.println(e.getMessage());
						System.exit(-1);
					}
				} // while (relatedNodesIter.hasNext())
				
			} // while (actSPUDDIter.hasNext())
			
			// Store DDRef to DDTree mapping
			String refName = this.nextLevelVarContext.getOppObsDDRefFromOrigObsName(obsName); 
			this.oppObsForStateDDDefMap.put(
					refName, 
					policyNodePrefix);
			
			// Store oppObsNameForState to DDRef mapping
			this.oppObsStateToOppObsDDRef.put(
					this.nextLevelVarContext.getOppObsForStateNameFromOrigObsName(obsName), 
					refName);
			
		} // for (int i=0;i < oppOrigObsNames.length; i++)
	} // public void setOppObsForStateDDs()
	
	// ------------------------------------------------------------------------------
	
	/*
	 * The following method populates the actToVarToDDMap from the lowerDomain's
	 * ActionSPUDDMap
	 */
	public void populateActToVarToDDMap() {
		HashMap<String, ActionSPUDD> lowerLevelActSPUDDMap = this.lowerDomain.actionSPUDDMap;
		
		// For all actionSPUDDs in the lower domain
		Iterator<Entry<String, ActionSPUDD>> actSPUDDIter = 
				lowerLevelActSPUDDMap.entrySet().iterator();
		while (actSPUDDIter.hasNext()) {
			Entry<String, ActionSPUDD> entry = actSPUDDIter.next();
			String actName = entry.getKey();
			
			// For each variable
			HashMap<String, DDTree> varToDDMap = entry.getValue().varToDDMap;
			Iterator<Entry<String, DDTree>> varToDDIter = varToDDMap.entrySet().iterator();
			while (varToDDIter.hasNext()) {
				Entry<String, DDTree> entryVarToDD = varToDDIter.next();
				
				// if entry exists, add <state var> -> <DD> to the <action name> key
				if (this.actToVarToDDMap.containsKey(actName)) {
					this.actToVarToDDMap.get(actName).put(
							entryVarToDD.getKey(), 
							entryVarToDD.getValue());
				}
				
				// else create new hashmap and put the entry
				else {
					this.actToVarToDDMap.put(actName, new HashMap<String, DDTree>());
					this.actToVarToDDMap.get(actName).put(
							entryVarToDD.getKey(), 
							entryVarToDD.getValue());
				}
				
			} // while (varToDDIter.hasNext())
			
		} // while (actSPUDDIter.hasNext())
		
	} // public void populateActToVarToDDMap()
	
	// ------------------------------------------------------------------------------
	
	// ----------------------------------------------------------------------------
	// Initialization methods to help sub class in domain definition
	
	/*
	 *  initialize dd makers
	 */
	public void makeDDMaker() {
		this.ddmaker = new DDMaker();
		this.ddmaker.addFromNextLevelVariablesContext(this.nextLevelVarContext);
	}
	
	/*
	 *  populate policy nodes to actions and vice versa
	 */
	public void populateNodeToActionMaps() {
		/*
		 * Just iterates through the policy nodes, splits the name on "-", and extracts
		 * the action name associated with the policy node.
		 */
		String[] policyNodes = this.lowerDomain.getPolicyValNames();
		for (int i=0; i < policyNodes.length; i++) {
			String actName = policyNodes[i].split("-")[2];
			
			// Add to reverse map
			this.policyNodetoAction.put(policyNodes[i], actName);
			
			// Map action to policyNodes
			if (this.actionToPolicyNodeMap.containsKey(actName)) {
				 this.actionToPolicyNodeMap.get(actName).add(policyNodes[i]);
			}
			
			else {
				this.actionToPolicyNodeMap.put(actName, new ArrayList<String>());
				this.actionToPolicyNodeMap.get(actName).add(policyNodes[i]);
			}
		}
	}
	
	// -----------------------------------------------------------------------------------------
	/*
	 * Helper methods
	 */
	public void replaceStateTransDDForOppAction(
			String varName,
			String actionName,
			ActionSPUDD spuddToEdit,
			DDTree ddToReplace) {
		
		/*
		 * Get iterator over list of policy nodes related to the given action. Then, for each node,
		 * call ActionSPUDD.overwriteDDForNode
		 */
		
		/*
		 * In the event that the action is not present in the map, do nothing and return.
		 * Note that this is a quick fix. May have unforeseen side effects
		 */
		if (this.actionToPolicyNodeMap.containsKey(actionName)) {
			Iterator<String> nodeIter = this.actionToPolicyNodeMap.get(actionName).iterator();
			while (nodeIter.hasNext()) {
				String policyNode = nodeIter.next();
				spuddToEdit.overwriteDDForNode(varName, policyNode, ddToReplace);
			} // while node iter
		}
	}
	
	public void populateNodeInfoMaps() {
		/*
		 * Populates the information maps that contain info on which nodes are init beliefs.
		 * 
		 * Used mostly when the agent wants to create initial beliefs for the opponent's policy
		 */
		Iterator<Entry<Integer, PolicyNode>> nodeIter = 
				this.lowerDomain.policyGraph.getNodeHashMap().entrySet().iterator();
		while (nodeIter.hasNext()) {
			Entry<Integer, PolicyNode> entry = nodeIter.next();
			PolicyNode node = entry.getValue();
			this.initNodesMap.put("node-" + entry.getKey() + "-" + node.actName, node.startNode);
		}
	} // public void populateNodeInfoMaps
	
	// -----------------------------------------------------------------------------------------
	
	// Driver function for calling all initialization methods
	public void initializationDriver() {
		this.makeVarContext();
		this.makeDDMaker();
		this.populateNodeToActionMaps();
		this.populateNodeInfoMaps();
		this.populateActToVarToDDMap();
		this.setOppPolicyDD();
		this.setOppObsForStateDDs();
	}
	
	// ----------------------------------------------------------------------------
	// Override domain init writers
	
	public void writeVariablesDef() {
		/*
		 * Populates the variablesDef String in SPUDD format
		 */
		this.variablesDef = this.newLine;
		this.variablesDef += "(variables" + this.newLine;
		
		for (int v=0; v < this.nextLevelVarContext.getVarNames().length; v++) {
			this.variablesDef += "(" + this.nextLevelVarContext.getVarNames()[v] + " " 
					+ String.join(" ", this.nextLevelVarContext.getVarValNames()[v]) + ")" + this.newLine;
		}
		
		for (int v=0; v < this.nextLevelVarContext.getOppObsForStateNames().length; v++) {
			this.variablesDef += "(" + this.nextLevelVarContext.getOppObsForStateNames()[v] + " " 
					+ String.join(" ", this.nextLevelVarContext.getOppObsForStateValNames()[v]) + ")"
					+ this.newLine;
		}
		
		this.variablesDef += "(" + this.nextLevelVarContext.getOppPolicyName() + " "
				+ String.join(" ", this.nextLevelVarContext.getOppPolicyValNames()) + ")" + this.newLine;
		
		this.variablesDef += ")" + this.newLine;
	}
	
	public void writeObsDef() {
		/*
		 * Populates the obsDef String in SPUDD format
		 */
		this.obsDef = this.newLine;
		this.obsDef += "(observations" + this.newLine;
		
		for (int v=0; v < this.nextLevelVarContext.getObsNames().length; v++) {
			this.obsDef += "(" + this.nextLevelVarContext.getObsNames()[v] + " " 
					+ String.join(" ", this.nextLevelVarContext.getObsValNames()[v]) + ")" + this.newLine;
		}
		
		this.obsDef += ")" + this.newLine;
	}
	
	// ----------------------------------------------------------------------------
	
	// Set opponent policy variables
	
	public void setOppPolicyDD() {
		this.oppPolicy = this.lowerDomain.getPolicyGraphDD();
	}
	
	public void writeOppPolicyDD() {
		this.oppPolicyDDDef = "" + this.newLine;
		this.oppPolicyDDDef += DDTools.defineDDInSPUDD(
				this.nextLevelVarContext.getPolicyDDRefName(),
				this.oppPolicy);
		this.oppPolicyDDDef += this.newLine;
	}
	
	public void writeOppObsDDs() throws DDNotDefinedException {
		Iterator<Entry<String, DDTree>> obsDDs = this.oppObsForStateDDDefMap.entrySet().iterator();
		
		while (obsDDs.hasNext()) {
			Entry<String, DDTree> entry = obsDDs.next();
			
			if (entry.getValue() == null) {
				throw new DDNotDefinedException("For " + entry.getKey());
			}
			
			this.oppObsDDDef = "" + this.newLine;
			this.oppObsDDDef += DDTools.defineDDInSPUDD(entry.getKey(), entry.getValue());
			this.oppObsDDDef += this.newLine;
		}
	}
	
	// ------------------------------------------------------------------------------
	
	public void makeAll() {
		this.initializationDriver();
		this.writeVariablesDef();
		this.writeObsDef();
		this.writeOppPolicyDD();
		this.makeBeliefsSPUDD();
		this.writeBeliefs();
		this.makeActionsSPUDD();
		this.writeActions();
//		this.makeRewardDD();
		
//		this.writeBeliefs();
//		this.writeActions();
//		this.writeReward();
		
		this.domainString = "";
		this.domainString += this.variablesDef + this.newLine;
		this.domainString += this.obsDef + this.newLine;
		this.domainString += this.beliefSection + this.newLine;
		this.domainString += "unnormalized" + this.newLine;
		this.domainString += this.oppPolicyDDDef + this.newLine;
		this.domainString += this.actionSection + this.newLine;
//		this.domainString += this.rewardSection;
		this.domainString += "tolerance 0.001" + this.newLine;
		this.domainString += "discount 0.9";
	}
	
}
