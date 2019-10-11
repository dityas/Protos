/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.examples;

import thinclab.ddhelpers.DDTree;
import thinclab.ddhelpers.SameDDTree;
import thinclab.domainMaker.L0Frame;
import thinclab.domainMaker.SPUDDHelpers.ActionSPUDD;
import thinclab.domainMaker.SPUDDHelpers.ActionSPUDDFactory;
import thinclab.domainMaker.SPUDDHelpers.BeliefSPUDDFactory;
import thinclab.domainMaker.SPUDDHelpers.VariablesContext;

/*
 * @author adityas
 *
 */
public class TigerL0Frame extends L0Frame {
	/*
	 * Defines the level 0 frame for the multi agent tiger problem
	 * For use with the IPOMDP solver.
	 */
	
	public TigerL0Frame() {
		
	}
	
	public void makeVarContext() {
		/*
		 * Make varContext object for the tiger problem
		 */
		String[] varNames = new String[] {"TigerLoc"};
		String[][] varValNames = new String[][] {{"TL", "TR"}};
		
		String[] obsNames = new String[] {"GrowlLoc"};
		String[][] obsValNames = new String[][] {{"GL", "GR"}};
		
		this.varContext = new VariablesContext(varNames, varValNames, obsNames, obsValNames);
	}
	
	@Override
	public void makeBeliefsSPUDD() {
		/*
		 * Initial belief about the TigerLoc state variable
		 */
		DDTree tigerLocInit = this.ddmaker.getDDTreeFromSequence(
				new String[] {"TigerLoc"}, 
				new String[][] {
					{"TL", "0.5"},
					{"TR", "0.5"}});
		
		/*
		 * Set beliefSPUDD attribute of the super class
		 */
		this.beliefSPUDD = BeliefSPUDDFactory.getBeliefSPUDD(
				this.varContext, 
				new String[] {"TigerLoc"}, 
				new DDTree[] {tigerLocInit});
	}
	
	@Override
	public void makeActionsSPUDD() {
		/*
		 * Populate transition functions and observation functions for the actions
		 */
		
		/*
		 * Action Listen
		 */
		DDTree tigerLocListenObs = this.ddmaker.getDDTreeFromSequence(
				new String[] {"TigerLoc'",
							  "GrowlLoc'"}, 
				new String[][] {
					{"TL", "GL", "0.65"},
					{"TL", "GR", "0.35"},
					{"TR", "GL", "0.35"},
					{"TR", "GR", "0.65"}
				});
		
		ActionSPUDD listenSPUDD = ActionSPUDDFactory.getActionSPUDD(
				this.varContext, 
				"LISTEN", 
				new String[] {"TigerLoc", "GrowlLoc"}, 
				new DDTree[] {new SameDDTree("TigerLoc"), tigerLocListenObs}, 1.0);
		
		this.actionSPUDDMap.put("LISTEN", listenSPUDD);
		
		// ----------------------------------------------------------------------------------
		
		/*
		 * Action Open Left
		 */
		DDTree tigerLocOL = this.ddmaker.getDDTreeFromSequence(
				new String[] {"TigerLoc", "TigerLoc'"}, 
				new String[][] {
					{"TL", "TL", "0.5"},
					{"TL", "TR", "0.5"},
					{"TR", "TL", "0.5"},
					{"TR", "TR", "0.5"}
				});
		
		DDTree tigerLocOLObs = this.ddmaker.getDDTreeFromSequence(
							new String[] {"GrowlLoc'"}, 
							new String[][] {
								{"GL", "0.5"},
								{"GR", "0.5"}
							});
		
		ActionSPUDD olSPUDD = ActionSPUDDFactory.getActionSPUDD(
				this.varContext, 
				"OPEN_LEFT", 
				new String[] {"TigerLoc", "GrowlLoc"}, 
				new DDTree[] {tigerLocOL, tigerLocOLObs}, 
				0.0);
		
		DDTree costOL = this.ddmaker.getDDTreeFromSequence(
				new String[] {"TigerLoc"}, 
				new String[][] {
					{"TL", "100"},
					{"TR", "-10"}
				});
		
		olSPUDD.setCostDD(costOL);
		
		this.actionSPUDDMap.put("OPEN_LEFT", olSPUDD);
		
		// ----------------------------------------------------------------------------------
		
		/*
		 * Action Open Right
		 */
		DDTree tigerLocOR = this.ddmaker.getDDTreeFromSequence(
				new String[] {"TigerLoc", "TigerLoc'"}, 
				new String[][] {
					{"TL", "TL", "0.5"},
					{"TL", "TR", "0.5"},
					{"TR", "TL", "0.5"},
					{"TR", "TR", "0.5"}
				});
		
		DDTree tigerLocORObs = this.ddmaker.getDDTreeFromSequence(
							new String[] {"GrowlLoc'"}, 
							new String[][] {
								{"GL", "0.5"},
								{"GR", "0.5"}
							});
		
		ActionSPUDD orSPUDD = ActionSPUDDFactory.getActionSPUDD(
				this.varContext, 
				"OPEN_RIGHT", 
				new String[] {"TigerLoc", "GrowlLoc"}, 
				new DDTree[] {tigerLocOR, tigerLocORObs}, 
				0.0);
		
		DDTree costOR = this.ddmaker.getDDTreeFromSequence(
				new String[] {"TigerLoc"}, 
				new String[][] {
					{"TL", "-10"},
					{"TR", "100"}
				});
		
		orSPUDD.setCostDD(costOR);
		
		this.actionSPUDDMap.put("OPEN_RIGHT", orSPUDD);
	}
	
	@Override
	public void makeRewardDD() {
		
	}
}
