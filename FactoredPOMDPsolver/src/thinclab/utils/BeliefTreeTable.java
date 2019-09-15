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
import java.util.logging.Logger;

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
					+ "belief_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "horizon INTEGER,"
					+ "optimal_action TEXT NOT NULL,"
					+ "belief_text TEXT );";
			
			/* triples table */
			String triplesTableCreation = "CREATE TABLE IF NOT EXISTS triples ("
					+ "parent_belief_id INTEGER,"
					+ "action TEXT NOT NULL,"
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
	
	public void insertNewBelief(int horizon, String beliefString, String action) {
		/*
		 * Inserts new belief in the belief table
		 */
		try {
			String insertBeliefQ = "INSERT INTO beliefs (horizon, optimal_action, belief_text) "
					+ "VALUES(?, ?, ?)";
			
			/* Insert belief */
			PreparedStatement stmt = this.storageConn.prepareStatement(insertBeliefQ);
			stmt.setInt(1, horizon);
			stmt.setString(2, action);
			stmt.setString(3, beliefString);
			
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
	
	public String getActionForBeliefAtHorizon(String belief, int horizon) {
		/*
		 * Look for best action at given horizon for the belief
		 */
		String action = null;
		
		try {
			String insertBeliefQ = "SELECT optimal_action FROM beliefs WHERE "
					+ "horizon = ? AND belief_text LIKE ?";
			
			PreparedStatement stmt = this.storageConn.prepareStatement(insertBeliefQ);
			stmt.setInt(1, horizon);
			stmt.setString(2, belief);
			
			ResultSet res = stmt.executeQuery();
			action = res.getString("optimal_action");
		}
		
		catch (Exception e) {
			this.logger.severe("While inserting into beliefs table " + e.getMessage());
			System.exit(-1);
		}
		
		return action;
	}
}
