/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.simulations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/*
 * @author adityas
 *
 */
public class CyberDeceptionEnvironmentConnector extends NetworkedEnvironmentConnector {
	
	/*
	 * Defines the network connector for the cyber deception domain
	 */
	
	private static final Logger LOGGER = 
			Logger.getLogger(CyberDeceptionEnvironmentConnector.class);
	
	// -----------------------------------------------------------------------------------
	
	public CyberDeceptionEnvironmentConnector(String envIP, int envPort) {
		
		super(envIP, envPort);
		LOGGER.info("Initialized CyberDeceptionEnvironmentConnector");
	}
	
	// -----------------------------------------------------------------------------------
	
	@Override
	public boolean syncEstablished(BufferedReader envInStream) {
		
		String hello = "";
		
		try {
			hello = envInStream.readLine().trim();
		} 
		
		catch (IOException e) {
			LOGGER.error("While reading from socket's instream: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		/* defender agent should try to handshake with a 'hello' */
		if (hello.contains("hello")) return true;
		else {
			LOGGER.debug("Expected 'hello', got " + hello);
			return false;
		}
	}

	@Override
	public void waitForAck(BufferedReader envInStream) {
		
		while (true) {
			
			String ack = "";
			
			try {
				ack = envInStream.readLine().trim();
			}
			
			catch (Exception e) {
				LOGGER.error("While reading stream for ack: " + e.getMessage());
				e.printStackTrace();
				System.exit(-1);
			}
			
			if (ack.contains("done")) break;

			else {
				
				LOGGER.warn("Did not get ack");
				LOGGER.debug("Retrying in 1 second");
				
				try {
					TimeUnit.SECONDS.sleep(1);
				} 
				
				catch (InterruptedException e) {
					LOGGER.error("While sleeping");
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}

	}

	@Override
	public void sendAck(PrintWriter envOutStream) {
		envOutStream.println("done");
	}

	@Override
	public String[] handleObservation(String obs) {
		return obs.split(";");
	}
	
	@Override
	public String[] step(String action) {
		/*
		 * Override this because sync is being established only once to connect to
		 * Omid's server. And the same socket is used throughout.
		 */
		
		/* send action */
		this.sendAction(action);
		
		/* get observation */
		String[] observation = this.getObservation();
		
		return observation;
	}

}
