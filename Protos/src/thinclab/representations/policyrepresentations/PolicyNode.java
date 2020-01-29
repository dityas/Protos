package thinclab.representations.policyrepresentations;

import java.io.Serializable;
import java.util.*;

import thinclab.decisionprocesses.POMDP;
import thinclab.legacy.DD;

public class PolicyNode implements Serializable {
	
	private static final long serialVersionUID = -5523013082898050215L;
	
	int alphaId=-1;
//	int actId = 1;
	
	private String actName = "";
	private String sBelief = "";
	private DD belief;

	private int id = -1;
	private int H = -1;

	private boolean startNode = false;
//	public POMDP p;
	
	// ------------------------------------------------------------------------------------
	/*
	 * Constructors
	 */
	
	public PolicyNode() {
		
	}
	
	// ------------------------------------------------------------------------------------
	
	public String getActName() {
		return actName;
	}

	public void setActName(String actName) {
		this.actName = actName;
	}

	public String getsBelief() {
		return sBelief;
	}

	public void setsBelief(String sBelief) {
		this.sBelief = sBelief;
	}

	public DD getBelief() {
		return belief;
	}

	public void setBelief(DD belief) {
		this.belief = belief;
	}

	public int getH() {
		return H;
	}

	public void setH(int h) {
		H = h;
	}

	public boolean isStartNode() {
		return startNode;
	}

	public void setStartNode(boolean startNode) {
		this.startNode = startNode;
	}

	public int getId() {
		return id;
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
		return "PolicyNode [ID = " + this.id
				+ " level=" + this.H 
				+ " action=" + this.actName
				+ " belief=" + this.sBelief + "]";
	}

}
