/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/*
 * @author adityas
 *
 */
public class PersistentStructuredTree extends StructuredTree {

	/* DB connection object */
	public Connection storageConn;
	private String dbDir = "/tmp/mj_storage.db";
	
	private static final Logger LOGGER = Logger.getLogger(PersistentStructuredTree.class);
	private static final long serialVersionUID = 5825861088529650090L;
	
	
	// -------------------------------------------------------------------------------
	
	public PersistentStructuredTree() {
		this.idToNodeMap = null;
		this.edgeMap = null;
		
		
	}
	
	private void initializeDB() {
		
		/*
		 * Open DB connection
		 */
		
		try {
			
			this.storageConn = DriverManager.getConnection("jdbc:sqlite:" + this.dbDir);
			
			/* Create table for storing opponent Model */
			
			/* beliefs table */
			String beliefTableCreation = "CREATE TABLE IF NOT EXISTS nz_cache ("
					+ "belief_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "nz_prime BLOB);";
			
			Statement query = this.storageConn.createStatement();
			query.execute(beliefTableCreation);
			
			LOGGER.debug("Created table to store NZ Primes");
		}
		
		catch (SQLException e) {
			LOGGER.error("Local DB connection error " + e.getMessage());
			System.exit(-1);
		}
		
		catch (Exception e) {
			LOGGER.error("Error while creating DB " + e.getMessage());
			System.exit(-1);
		}
		
	}
	
	private void initializeLocalStorage() {
		/*
		 * Creates connection to local in memory sqlite db for storing opponent model
		 * triples.
		 */
		
		try {
			
			this.storageConn = DriverManager.getConnection("jdbc:sqlite:" + this.dbDir);
			
			/* Create table for storing opponent Model */
			
			/* beliefs table */
			String beliefTableCreation = "CREATE TABLE IF NOT EXISTS nz_cache ("
					+ "belief_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "nz_prime BLOB);";
			
			Statement query = this.storageConn.createStatement();
			query.execute(beliefTableCreation);
			
			LOGGER.debug("Created table to store NZ Primes");
		}
		
		catch (SQLException e) {
			LOGGER.error("Local DB connection error " + e.getMessage());
			System.exit(-1);
		}
		
		catch (Exception e) {
			LOGGER.error("Error while creating DB " + e.getMessage());
			System.exit(-1);
		} 
	}
	
	private void makeIdToNodeTable() {
		
		/*
		 * Make table equivalent of idToNodeMap
		 */
		
		try {
			
			/* idToNodeTable */
			String beliefTableCreation = "CREATE TABLE IF NOT EXISTS id_to_node ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "belief BLOB,"
					+ ");";
			
			Statement query = this.storageConn.createStatement();
			query.execute(beliefTableCreation);
			
			LOGGER.debug("Created table to store NZ Primes");
		}
		
		catch (SQLException e) {
			LOGGER.error("Local DB connection error " + e.getMessage());
			System.exit(-1);
		}
		
		catch (Exception e) {
			LOGGER.error("Error while creating DB " + e.getMessage());
			System.exit(-1);
		}
		
	}
	
	private void makeEdgeTable() {
		/*
		 * Create edge table in data base
		 */
	}

}
