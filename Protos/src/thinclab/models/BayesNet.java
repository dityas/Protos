/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public class BayesNet implements Model {

	/*
	 * Simple BayesNet model
	 */
	
	private HashMap<String, DD> cpds = new HashMap<>(10);
	private static final Logger LOGGER = LogManager.getLogger(BayesNet.class);
	
	public BayesNet(HashMap<String, DD> cpds) {
		this.cpds = cpds;
		LOGGER.info(String.format("BN initialzed with CPDs %s", this.cpds));
	}
	
	public void addCPD(String varName, DD cpd) {
		this.cpds.put(varName, cpd);
	}
	
	public DD getCPDForVar(String varName) {
		return this.cpds.get(varName);
	}
	
	@Override
	public String toString() {
		return this.cpds.toString();
	}
}
