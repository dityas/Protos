/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.legacy.NextBelState;

/*
 * @author adityas
 *
 */
public class CacheDB {
	/*
	 * In memory table to store belief tree
	 */
	
	/* DB connection object */
	public Connection storageConn;
	
	private String dbDir = null;
	
	private static final Logger logger = Logger.getLogger(CacheDB.class);
	
	// ----------------------------------------------------------------------------------------
	
	public CacheDB(String fileName) {
		
		/* initialize tables */
		logger.debug("Initializing local storage for belief tree");
		this.dbDir = fileName;
		this.initializeLocalStorage();
	}
	
	// ----------------------------------------------------------------------------------------
	
	private void initializeLocalStorage() {
		/*
		 * Creates connection to local in memory sqlite db for storing opponent model
		 * triples.
		 */
		
		try {
//			this.storageConn = DriverManager.getConnection("jdbc:sqlite::memory:");
			this.storageConn = DriverManager.getConnection("jdbc:sqlite:" + this.dbDir);
			
			/* Create table for storing opponent Model */
			
			/* beliefs table */
			String beliefTableCreation = "CREATE TABLE IF NOT EXISTS nz_cache ("
					+ "belief_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "nz_prime BLOB);";
			
			Statement query = this.storageConn.createStatement();
			query.execute(beliefTableCreation);
			
			logger.debug("Created table to store NZ Primes");
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
	
	public void insertNewNZPrime(int belief_id, HashMap<String, NextBelState> nzPrime) {
		/*
		 * Inserts new belief in the belief table
		 */
		try {
			String insertBeliefQ = "INSERT INTO nz_cache "
					+ "(belief_id, nz_prime) "
					+ "VALUES(?, ?)";
			
			ByteArrayOutputStream bArrayOutStream = new ByteArrayOutputStream();
			ObjectOutputStream outStream = new ObjectOutputStream(bArrayOutStream);
			outStream.writeObject(nzPrime);
			outStream.flush();
			
			byte[] serializedNZPrimes = bArrayOutStream.toByteArray();
			
			outStream.close();
			bArrayOutStream.close();
			
			/* Insert belief */
			PreparedStatement stmt = this.storageConn.prepareStatement(insertBeliefQ);
			stmt.setInt(1, belief_id);
			stmt.setBytes(2, serializedNZPrimes);
			stmt.executeUpdate();
		}
		
		catch (Exception e) {
			logger.error("While inserting into nz_cache table " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public ResultSet getTable() {
		/*
		 * Prints the entire belief table
		 */
		ResultSet res = null;
		
		try {
			String showBeliefQ = "SELECT * FROM nz_cache";
			
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
	
	public HashMap<String, NextBelState> getNZPrime(int belief_id) {
		/*
		 * Deserialize given object from the DB and return
		 */
		
		HashMap<String, NextBelState> deserializedObj = null;
		
		try {
			String getNZPrimeQuery = "SELECT nz_prime FROM nz_cache WHERE belief_id=?";
			
			/* select all */
			PreparedStatement stmt = this.storageConn.prepareStatement(getNZPrimeQuery);
			stmt.setInt(1, belief_id);
			
			ResultSet res = stmt.executeQuery();
			
			while (res.next()) {
				byte[] bytesIn = res.getBytes("nz_prime");
				ByteArrayInputStream byteInStream = new ByteArrayInputStream(bytesIn);
				ObjectInputStream objInStream = new ObjectInputStream(byteInStream);
				
				deserializedObj = (HashMap<String, NextBelState>) objInStream.readObject();
				
				objInStream.close();
				byteInStream.close();
			}
		}
		
		catch (Exception e) {
			logger.error("While printing beliefs table " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		return deserializedObj;
	}
	
	public void clearDB() {
		/*
		 * Delete all entries in the table 
		 */
		
		try {
			String deleteEntriesQ = "DELETE FROM nz_cache";
			
			/* select all */
			PreparedStatement stmt = this.storageConn.prepareStatement(deleteEntriesQ);
			stmt.executeUpdate();
			
			logger.debug("Deleted all entries from nz_prime CacheDB");
		}
		
		catch (Exception e) {
			logger.error("While printing beliefs table " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
}
