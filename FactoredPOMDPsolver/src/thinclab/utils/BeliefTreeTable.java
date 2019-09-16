/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
	
	private Logger logger = LoggerFactory.getNewLogger("BeliefTreeTable");
	
	// ----------------------------------------------------------------------------------------
	
	public BeliefTreeTable() {
		
		/* initialize tables */
		this.logger.info("Initializing local storage for belief tree");
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
			
			this.logger.info("Created table to store lower belief points");
			this.logger.info("Created triples table for belief tree");
		}
		
		catch (SQLException e) {
			this.logger.severe("Local DB connection error " + e.getMessage());
			System.exit(-1);
		}
		
		catch (Exception e) {
			this.logger.severe("Error while creating DB " + e.getMessage());
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
			this.logger.severe("While inserting into beliefs table " + e.getMessage());
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
			this.logger.severe("While inserting into beliefs table " + e.getMessage());
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
			this.logger.severe("While printing beliefs table " + e.getMessage());
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
			this.logger.severe("While printing edges table " + e.getMessage());
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
			this.logger.severe("While querying for optimal action at " + belId + " " + e.getMessage());
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
			this.logger.severe("While querying for belief_text at " + belId + " " + e.getMessage());
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
			this.logger.severe("While inserting into beliefs table " + e.getMessage());
			System.exit(-1);
		}
		
		return belIds;
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
			this.logger.severe("While getting edge triples " + e.getMessage());
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
				this.logger.severe("While back tracing edge triples " + e.getMessage());
				e.printStackTrace();
				System.exit(-1);
			}
		
		}
		
		return debugString.toArray(new String[debugString.size()]);
	}
}
