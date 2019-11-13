package thinclab.representations.policyrepresentations;

import java.io.Serializable;
import java.util.*;

import thinclab.decisionprocesses.POMDP;
import thinclab.legacy.DD;

public class PolicyNode implements Serializable {
	
	private static final long serialVersionUID = -5523013082898050215L;
	
	int alphaId=-1;
	int actId = 1;
	
	public String actName = "";
	public String sBelief = "";
	public DD belief;

	public int id = -1;
	public int H = -1;
	
	public HashMap<String, HashMap<String, Float>> factoredBelief;

	public boolean startNode = false;
	public POMDP p;
	
	// ------------------------------------------------------------------------------------
	/*
	 * Constructors
	 */
	
	public PolicyNode() {
		
	}
	
	public PolicyNode(int id, int timeStep, String sBelief, String action) {
		/*
		 * Constructor for using PolicyNode objects as place holders
		 * 
		 * Currently being used with BeliefGraph backend in OpponentModel objects
		 */
		this.id = id;
		this.H = timeStep;
		this.sBelief = sBelief;
		this.actName = action;
	}
	
	// ------------------------------------------------------------------------------------
	
	public void setId(int id) {
		/*
		 * Setter for id
		 */
		this.id = id;
	}
	
	public void setStartNode() {
		/*
		 * Marks the node as a start node
		 */
		this.startNode = true;
	}
	
	// -------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return "PolicyNode \t [ID = " + this.id
				+ " \t level=" + this.H 
				+ " \t action=" + this.actName 
				+ " \t belief=" + this.factoredBelief
				+ " \t belief=" + this.sBelief + "]\r\n";
	}

}
