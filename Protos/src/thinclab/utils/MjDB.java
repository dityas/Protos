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
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.representations.policyrepresentations.PolicyNode;


/*
 * @author adityas
 *
 */
public class MjDB {
	
	/*
	 * Handles DB operations for persistent Mj storage
	 */
	
	public Connection storageConn;
	private File tempFile;
	private static final Logger LOGGER = Logger.getLogger(MjDB.class);
	
	// ---------------------------------------------------------------------------------
	
	public MjDB() {
		
		try {
			this.tempFile = File.createTempFile("mj_policy_nodes", ".db");
			this.tempFile.deleteOnExit();
			
			/* make tables */
			this.createTables();
			
			LOGGER.debug("Mj DB created at " + this.tempFile.getAbsolutePath());
		} 
		
		catch (Exception e) {
			LOGGER.error("While creating temp file for MjDB: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private void createTables() {
		
		/*
		 * Creates connection to the DB and creates node and edge tables
		 */
		
		try {
			
			this.storageConn = 
					DriverManager.getConnection("jdbc:sqlite:" 
							+ this.tempFile.getAbsolutePath());
			this.storageConn.setAutoCommit(false);
			
			/* Create table for storing opponent Model */
			
			/* node table */
			String nodeTable = "CREATE TABLE IF NOT EXISTS id_to_node ("
					+ "belief_id INTEGER PRIMARY KEY,"
					+ "time_step INTEGER,"
					+ "policy_node BLOB);";
			
			Statement query = this.storageConn.createStatement();
			query.execute(nodeTable);
			
			LOGGER.debug("Created table to store nodes");
			
			/* node table */
			String edgesTable = "CREATE TABLE IF NOT EXISTS edges ("
					+ "edge INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "src_id INTEGER,"
					+ "label TEXT,"
					+ "dest_id INTEGER);";
			
			query = this.storageConn.createStatement();
			query.execute(edgesTable);
			
			LOGGER.debug("Created table to store edges");
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
	
	public void commitChanges() {
		try {
			this.storageConn.commit();
		} 
		
		catch (SQLException e) {
			LOGGER.error("While commiting changes to DB");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void putNode(int id, PolicyNode node) {
		/*
		 * Inserts new Node in the id_to_node
		 */
		
		try {
			String insertNodeQ = "INSERT INTO id_to_node "
					+ "(belief_id, time_step, policy_node) "
					+ "VALUES(?, ?, ?)";
			
			ByteArrayOutputStream bArrayOutStream = new ByteArrayOutputStream();
			ObjectOutputStream outStream = new ObjectOutputStream(bArrayOutStream);
			outStream.writeObject(node);
			outStream.flush();
			
			byte[] serializedNode = bArrayOutStream.toByteArray();
			
			outStream.close();
			bArrayOutStream.close();
			
			/* Insert belief */
			PreparedStatement stmt = this.storageConn.prepareStatement(insertNodeQ);
			stmt.setInt(1, id);
			stmt.setInt(2, node.getH());
			stmt.setBytes(3, serializedNode);
			stmt.executeUpdate();
		}
		
		catch (Exception e) {
			LOGGER.error("While inserting node into id_to_node table " + e.getMessage());
			e.printStackTrace();
			this.printTables();
			System.exit(-1);
		}
	}
	
	public void putEdge(int src_id, List<String> label, int dest_id) {
		/*
		 * Inserts new Node in the id_to_node
		 */
		try {
			String insertEdgeQ = "INSERT INTO edges "
					+ "(src_id, label, dest_id) "
					+ "VALUES(?, ?, ?)";
			
			/* Insert belief */
			PreparedStatement stmt = this.storageConn.prepareStatement(insertEdgeQ);
			stmt.setInt(1, src_id);
			stmt.setString(2, String.join("|", label));
			stmt.setInt(3, dest_id);
			stmt.executeUpdate();
			
		}
		
		catch (Exception e) {
			LOGGER.error("While inserting node into id_to_node table " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public PolicyNode getNode(int belief_id) {
		/*
		 * De serialize given PolicyNode from the DB and return
		 */
		
		PolicyNode deserializedObj = null;
		
		try {
			String getPolicyNodeQ = "SELECT policy_node FROM id_to_node WHERE belief_id=?";
			
			/* select all */
			PreparedStatement stmt = this.storageConn.prepareStatement(getPolicyNodeQ);
			stmt.setInt(1, belief_id);
			
			ResultSet res = stmt.executeQuery();
			
			while (res.next()) {
				byte[] bytesIn = res.getBytes("policy_node");
				ByteArrayInputStream byteInStream = new ByteArrayInputStream(bytesIn);
				ObjectInputStream objInStream = new ObjectInputStream(byteInStream);
				
				deserializedObj = (PolicyNode) objInStream.readObject();
				
				objInStream.close();
				byteInStream.close();
			}
		}
		
		catch (Exception e) {
			LOGGER.error("While getting PolicyNode from table " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		return deserializedObj;
	}
	
	public HashMap<List<String>, Integer> getEdges(int src_id) {
		/*
		 * De serialize given PolicyNode from the DB and return
		 */
		
		HashMap<List<String>, Integer> deserializedObj = new HashMap<List<String>, Integer>();
		
		try {
			String getPolicyNodeQ = "SELECT * FROM edges WHERE src_id=?";
			
			/* select all */
			PreparedStatement stmt = this.storageConn.prepareStatement(getPolicyNodeQ);
			stmt.setInt(1, src_id);
			
			ResultSet res = stmt.executeQuery();
			
			while (res.next()) {
				List<String> obs = Arrays.asList(res.getString("label").split("\\|"));
				int dest = res.getInt("dest_id");
				
				deserializedObj.put(obs, dest);
			}
		}
		
		catch (Exception e) {
			LOGGER.error("While getting edges from table " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		return deserializedObj;
	}
	
	public void makeAllRoots() {
		/*
		 * Makes all nodes in the DB as roots for expansion
		 */
		try {
			String makeAllRootsQ = "UPDATE id_to_node SET time_step=0";
			
			PreparedStatement stmt = this.storageConn.prepareStatement(makeAllRootsQ);
			stmt.executeUpdate();
			
		}
		
		catch (Exception e) {
			LOGGER.error("While setting all nodes as roots " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	public List<Integer> getAllRoots() {
		/*
		 * Get all root nodes
		 */
		
		List<Integer> ids = new ArrayList<Integer>();
		
		try {
			String getAllRootsQ = "SELECT belief_id FROM id_to_node WHERE time_step=?";
			
			/* select all */
			PreparedStatement stmt = this.storageConn.prepareStatement(getAllRootsQ);
			stmt.setInt(1, 0);
			
			ResultSet res = stmt.executeQuery();
			
			while (res.next()) {
				ids.add(res.getInt("belief_id"));
			}
		}
		
		catch (Exception e) {
			LOGGER.error("While getting PolicyNode from table " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		return ids;
	}
	
	public List<Integer> getAllNodeIds() {
		/*
		 * Return all policy node ids
		 */
		
		ResultSet res;
		List<Integer> ids = new ArrayList<Integer>();
		
		try {
			String getNodesQ = "SELECT belief_id FROM id_to_node";
			
			/* select all */
			Statement stmt = this.storageConn.createStatement();
			res = stmt.executeQuery(getNodesQ);
			
			while (res.next()) {
				ids.add(res.getInt("belief_id"));
			}
		}
		
		catch (Exception e) {
			LOGGER.error("While getting all node ids from table " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		return ids;
	}
	
	public List<Integer> getAllEdgeIds() {
		/*
		 * Return all edge ids
		 */
		
		ResultSet res;
		List<Integer> ids = new ArrayList<Integer>();
		
		try {
			String getEdgesQ = "SELECT DISTINCT src_id FROM edges";
			
			/* select all */
			Statement stmt = this.storageConn.createStatement();
			res = stmt.executeQuery(getEdgesQ);
			
			while (res.next()) {
				ids.add(res.getInt("src_id"));
			}
		}
		
		catch (Exception e) {
			LOGGER.error("While getting all node ids from table " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		return ids;
	}
	
	public void removeNode(int id) {
		/*
		 * Delete all entries in the id_to_node table 
		 */
		
		try {
			String deleteEntriesQ = "DELETE FROM id_to_node WHERE belief_id=?";
			
			/* select all */
			PreparedStatement stmt = this.storageConn.prepareStatement(deleteEntriesQ);
			stmt.setInt(1, id);
			stmt.executeUpdate();
		}
		
		catch (Exception e) {
			LOGGER.error("While deleting from id_to_node table " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public ResultSet getNodeTable() {
		/*
		 * Prints the entire belief table
		 */
		ResultSet res = null;
		
		try {
			String showAllNodesQ = "SELECT * FROM id_to_node";
			
			/* select all */
			Statement stmt = this.storageConn.createStatement();
			res = stmt.executeQuery(showAllNodesQ);
		}
		
		catch (Exception e) {
			LOGGER.error("While printing beliefs table " + e.getMessage());
			System.exit(-1);
		}
		
		return res;
	}
	
	public ResultSet getEdgesTable() {
		/*
		 * Prints the entire belief table
		 */
		ResultSet res = null;
		
		try {
			String showAllEdgesQ = "SELECT * FROM edges";
			
			/* select all */
			Statement stmt = this.storageConn.createStatement();
			res = stmt.executeQuery(showAllEdgesQ);
		}
		
		catch (Exception e) {
			LOGGER.error("While printing edges table " + e.getMessage());
			System.exit(-1);
		}
		
		return res;
	}
	
	public void clearNodeTable() {
		/*
		 * Delete all entries in the id_to_node table 
		 */
		
		try {
			String deleteEntriesQ = "DELETE FROM id_to_node";
			
			/* select all */
			PreparedStatement stmt = this.storageConn.prepareStatement(deleteEntriesQ);
			stmt.executeUpdate();
			
			LOGGER.debug("Deleted all entries from id_to_node");
		}
		
		catch (Exception e) {
			LOGGER.error("While printing beliefs table " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void clearEdgesTable() {
		/*
		 * Delete all entries in the edges table 
		 */
		
		try {
			String deleteEntriesQ = "DELETE FROM edges";
			
			/* select all */
			PreparedStatement stmt = this.storageConn.prepareStatement(deleteEntriesQ);
			stmt.executeUpdate();
			
			LOGGER.debug("Deleted all entries from edges");
		}
		
		catch (Exception e) {
			LOGGER.error("While printing beliefs table " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void printTables() {
		/*
		 * Prints both tables to logs
		 */
		
		try {
			
			/* node table */
			ResultSet nodeRes = this.getNodeTable();
			
			LOGGER.debug("Nodes Table:");
			while(nodeRes.next()) {
				LOGGER.debug("ID: " + nodeRes.getInt("belief_id") 
					+ " T: " + nodeRes.getInt("time_step") 
					+ " NODE: " + nodeRes.getBytes("policy_node"));
			}
			
			/* edges table */
			ResultSet edgesRes = this.getNodeTable();
			
			LOGGER.debug("Edges Table:");
			while(edgesRes.next()) {
				LOGGER.debug("ID: " + edgesRes.getInt("edge") 
					+ " SRC: " + edgesRes.getInt("src_id")
					+ " LABEL: " + edgesRes.getBytes("label")
					+ " DEST: " + edgesRes.getInt("dest_id"));
			}
		}
		
		catch (Exception e) {
			LOGGER.error("Some exception while printing the table. I don't know man. I am bored");
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
