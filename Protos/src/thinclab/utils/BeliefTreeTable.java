/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

/*
 * @author adityas
 *
 */
public class BeliefTreeTable {
	/*
	 * In memory table to store belief tree
	 */
	
	/* DB connection object */
	public Connection storageConn;
	
	private static final Logger logger = Logger.getLogger(BeliefTreeTable.class);
	
	// ----------------------------------------------------------------------------------------
	
	public BeliefTreeTable() {
		
		/* initialize tables */
		logger.debug("Initializing local storage for belief tree");
		this.initializeLocalStorage();
	}
	
	// ----------------------------------------------------------------------------------------
	
	private void initializeLocalStorage() {
		/*
		 * Creates connection to local in memory sqlite db for storing opponent model
		 * triples.
		 */
		
		try {
			this.storageConn = DriverManager.getConnection("jdbc:sqlite::memory:");
			
			/* Create table for storing opponent Model */
			
			/* beliefs table */
			String beliefTableCreation = "CREATE TABLE IF NOT EXISTS beliefs ("
					+ "belief_id INTEGER PRIMARY KEY,"
					+ "horizon INTEGER,"
					+ "optimal_action TEXT NOT NULL,"
					+ "belief_text TEXT );";
			
			/* triples table */
			String triplesTableCreation = "CREATE TABLE IF NOT EXISTS edges ("
					+ "parent_belief_id INTEGER,"
					+ "action TEXT,"
					+ "obs TEXT NOT NULL,"
					+ "child_belief_id INTEGER,"
					+ "FOREIGN KEY (parent_belief_id) REFERENCES modelJ (belief_id),"
					+ "FOREIGN KEY (child_belief_id) REFERENCES modelJ (belief_id)"
					+ " );";
			
			Statement query = this.storageConn.createStatement();
			query.execute(beliefTableCreation);
			query.execute(triplesTableCreation);
			
			logger.debug("Created table to store lower belief points");
			logger.debug("Created triples table for belief tree");
		}
		
		catch (SQLException e) {
			logger.error("Local DB connection error " + e.getMessage());
			System.exit(-1);
		}
		
		catch (Exception e) {
			logger.error("Error while creating DB " + e.getMessage());
			System.exit(-1);
		} 
	}
	
	// -----------------------------------------------------------------------------------------
	
	public void insertNewBelief(int id, int horizon, String beliefString, String action) {
		/*
		 * Inserts new belief in the belief table
		 */
		try {
			String insertBeliefQ = "INSERT INTO beliefs "
					+ "(belief_id, horizon, optimal_action, belief_text) "
					+ "VALUES(?, ?, ?, ?)";
			
			/* Insert belief */
			PreparedStatement stmt = this.storageConn.prepareStatement(insertBeliefQ);
			stmt.setInt(1, id);
			stmt.setInt(2, horizon);
			stmt.setString(3, action);
			stmt.setString(4, beliefString);
			
			stmt.executeUpdate();
		}
		
		catch (Exception e) {
			logger.error("While inserting into beliefs table " + e.getMessage());
			System.exit(-1);
		}
	}
	
	public void insertNewEdge(int pId, int cId, String action, String obs) {
		/*
		 * Inserts new edge in the edges table
		 */
		try {
			String insertBeliefQ = "INSERT INTO edges "
					+ "(parent_belief_id, action, obs, child_belief_id) "
					+ "VALUES(?, ?, ?, ?)";
			
			/* Insert edge */
			PreparedStatement stmt = this.storageConn.prepareStatement(insertBeliefQ);
			stmt.setInt(1, pId);
			stmt.setString(2, action);
			stmt.setString(3, obs);
			stmt.setInt(4, cId);
			
			stmt.executeUpdate();
		}
		
		catch (Exception e) {
			logger.error("While inserting into beliefs table " + e.getMessage());
			System.exit(-1);
		}
	}
	
	public ResultSet getBeliefTable() {
		/*
		 * Prints the entire belief table
		 */
		ResultSet res = null;
		
		try {
			String showBeliefQ = "SELECT * FROM beliefs";
			
			/* select all */
			Statement stmt = this.storageConn.createStatement();
			res = stmt.executeQuery(showBeliefQ);
		}
		
		catch (Exception e) {
			logger.error("While printing beliefs table " + e.getMessage());
			System.exit(-1);
		}
		
		return res;
	}
	
	public ResultSet getEdgesTable() {
		/*
		 * Prints the entire edges table
		 */
		ResultSet res = null;
		
		try {
			String showEdgesQ = "SELECT * FROM edges";
			
			/* select all */
			Statement stmt = this.storageConn.createStatement();
			res = stmt.executeQuery(showEdgesQ);
		}
		
		catch (Exception e) {
			logger.error("While printing edges table " + e.getMessage());
			System.exit(-1);
		}
		
		return res;
	}
	
	public String getActionForBelief(int belId) {
		/*
		 * Look for best action at given horizon for the belief
		 */
		String action = null;
		
		try {
			String getActionQ = "SELECT optimal_action FROM beliefs WHERE "
					+ "belief_id = ?";
			
			PreparedStatement stmt = this.storageConn.prepareStatement(getActionQ);
			stmt.setInt(1, belId);
			
			ResultSet res = stmt.executeQuery();
			action = res.getString("optimal_action");
		}
		
		catch (Exception e) {
			logger.error("While querying for optimal action at " + belId + " " + e.getMessage());
			System.exit(-1);
		}
		
		return action;
	}
	
	public String getBeliefTextForBelief(int belId) {
		/*
		 * Look for belief text label for the belief
		 */
		String beliefText = null;
		
		try {
			String getBeliefTextQ = "SELECT belief_text FROM beliefs WHERE "
					+ "belief_id = ?";
			
			PreparedStatement stmt = this.storageConn.prepareStatement(getBeliefTextQ);
			stmt.setInt(1, belId);
			
			ResultSet res = stmt.executeQuery();
			beliefText = res.getString("belief_text");
		}
		
		catch (Exception e) {
			logger.error("While querying for belief_text at " + belId + " " + e.getMessage());
			System.exit(-1);
		}
		
		return beliefText;
	}
	
	public List<Integer> getBeliefIDsAtTimeSteps(int startClosed, int endOpen) {
		/*
		 * Gets the IDs of belief nodes between the given time steps.
		 */
		List<Integer> belIds = new ArrayList<Integer>();
		
		try {
			String getBeliefIdQ = "SELECT belief_id FROM beliefs WHERE "
					+ "horizon >= ? AND horizon < ?";
			
			PreparedStatement stmt = this.storageConn.prepareStatement(getBeliefIdQ);
			stmt.setInt(1, startClosed);
			stmt.setInt(2, endOpen);
			
			ResultSet res = stmt.executeQuery();
			
			/* accumulate all ids and return them */
			while (res.next()) belIds.add(res.getInt("belief_id"));
		}
		
		catch (Exception e) {
			logger.error("While inserting into beliefs table " + e.getMessage());
			System.exit(-1);
		}
		
		return belIds;
	}
	
	public List<Integer> getChildrenOf(List<Integer> parents) {
		/*
		 * Gets the immediate children of the given belief points
		 */
		HashSet<Integer> children = new HashSet<Integer>();
		
		for (Integer parent : parents) {
			
			try {
				String getBeliefIdQ = "SELECT child_belief_id FROM edges WHERE "
						+ "parent_belief_id = ?";
				
				PreparedStatement stmt = this.storageConn.prepareStatement(getBeliefIdQ);
				stmt.setInt(1, parent);
				
				ResultSet res = stmt.executeQuery();
				
				/* accumulate all ids and return them */
				while (res.next()) children.add(res.getInt("child_belief_id"));
			}
			
			catch (Exception e) {
				logger.error("While inserting into beliefs table " + e.getMessage());
				System.exit(-1);
			}
		}
		
		return new ArrayList<Integer>(children);
	}
	
	public List<Integer> getChildrenStartingFrom(List<Integer> parents, int timeSteps) {
		/*
		 * Returns all nodes from the given parents upto the given time steps.
		 */
		
		/* List of all children */
		HashSet<Integer> children = new HashSet<Integer>();
		
		/* parents currently being expanded */
		HashSet<Integer> currentParents = new HashSet<Integer>();
		currentParents.addAll(parents);
		children.addAll(parents);
		
		/* children of current parents */
		HashSet<Integer> currentChildren = new HashSet<Integer>();
		
		for (int t = 0; t < timeSteps; t++) {
			
			/* expand for all parents */
			currentChildren.addAll(
					this.getChildrenOf(
							new ArrayList<Integer>(currentParents)));
			
			/* add to children set and start expanding from children */
			children.addAll(currentChildren);
			currentParents.clear();
			currentParents.addAll(currentChildren);
			currentChildren.clear();
		}
		
		/* return children as list */
		return new ArrayList<Integer>(children);
	}
	
	public String[][] getEdgeTriplesFromBeliefId(int belId) {
		/*
		 * Fetch edges from belIds to children as String arrays
		 * 
		 * For some reason, fetching edges triples for an Array of belIds does not work using
		 * java.sql's createArrayOf function. It throws an SQLException about some driver problem.
		 * Until that is figured out, this method will only work for single belIds. This makes it
		 * a little inefficient because of the need for repeated queries.
		 * 
		 * TODO: Figure out fetching triples for a collection of belIds
		 */
		List<String[]> triples = new ArrayList<String[]>();
		
		try {
			String getEdgesQ = "SELECT parent_belief_id, action, obs, child_belief_id "
					+ "FROM edges WHERE parent_belief_id = ?";
			
			PreparedStatement stmt = this.storageConn.prepareStatement(getEdgesQ);
			
			stmt.setInt(1, belId);
			
			ResultSet res = stmt.executeQuery();
			
			/* accumulate all triples and return */
			while (res.next()) {
				List<String> triple = new ArrayList<String>();
				
				/* parent ID */
				triple.add("m" + res.getInt("parent_belief_id"));
				
				/* action */
				String action = res.getString("action");
				if (action.length() != 0) triple.add(action);
				
				/* observations */
				triple.addAll(Arrays.asList(res.getString("obs").split(",")));
				
				/* child ID */
				triple.add("m" + res.getInt("child_belief_id"));
				
				triples.add(triple.toArray(new String[triple.size()]));
			}
		}
		
		catch (Exception e) {
			logger.error("While getting edge triples " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		return triples.toArray(new String[triples.size()][]);
	}
	
	public String[] getBackTraceDebugBanner(int[] leaves) {
		/*
		 * Gets triples with leaves as child_belief_ids, re formats into human readable info.
		 */
		List<String> debugString = new ArrayList<String>();
		
		for (int child: leaves) {
		
			try {
				String getBackTraceQ = "SELECT parent_belief_id, action, obs, child_belief_id "
						+ "FROM edges WHERE child_belief_id = ?";
				
				PreparedStatement stmt = this.storageConn.prepareStatement(getBackTraceQ);
				
				stmt.setInt(1, child);
				
				ResultSet res = stmt.executeQuery();
				
				/* accumulate all triples and return */
				while (res.next()) {
					String trace = "m" + res.getInt("parent_belief_id") + " -["
							+ res.getString("action") + "]-[" + res.getString("obs")
							+ "]-> " + "m" + res.getInt("child_belief_id");
					
					debugString.add(trace);
				}
			}
			
			catch (Exception e) {
				logger.error("While back tracing edge triples " + e.getMessage());
				e.printStackTrace();
				System.exit(-1);
			}
		
		}
		
		return debugString.toArray(new String[debugString.size()]);
	}
}