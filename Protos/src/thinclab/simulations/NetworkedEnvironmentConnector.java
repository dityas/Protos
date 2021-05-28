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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * @author adityas
 *
 */
abstract public class NetworkedEnvironmentConnector implements ExternalEnvironmentConnector {

	/*
	 * Handles interactions with an environment accessible over the network
	 * 
	 */

	private String envIP;
	private int envPort;

	private Socket envConnectorSocket;
	private PrintWriter envOutStream;
	private BufferedReader envInStream;

	private static final Logger LOGGER = LogManager.getLogger(NetworkedEnvironmentConnector.class);

	// ------------------------------------------------------------------------------

	public NetworkedEnvironmentConnector(String envIP, int envPort) {

		this.envIP = envIP;
		this.envPort = envPort;
	}

	// ------------------------------------------------------------------------------

	public void createEnvStreams() throws Exception {
		/*
		 * Connects to the environment and returns the socket
		 */

		this.envConnectorSocket = new Socket(this.envIP, this.envPort);
		this.envOutStream = new PrintWriter(this.envConnectorSocket.getOutputStream(), true);
		this.envInStream = new BufferedReader(new InputStreamReader(this.envConnectorSocket.getInputStream()));

		LOGGER.debug("Connected to " + envIP + " on port " + envPort);
	}

	// ------------------------------------------------------------------------------

	@Override
	public void establishSync() {
		/*
		 * Handshakes with the server over the network and populates the input and
		 * output streams
		 */

		/* try connecting to server */
		try {

			this.createEnvStreams();
		}

		catch (Exception e) {
			LOGGER.error("While connecting to server: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}

		/* exchange init messages or whatever to complete sync */
		while (true) {

			if (this.syncEstablished(this.envInStream)) {
				LOGGER.info("Ready to interact with env on " + envIP);
				break;
			}

			else {

				LOGGER.warn("Could not establish sync with the env.");
				LOGGER.debug("Retrying in 5 seconds");

				try {
					TimeUnit.SECONDS.sleep(5);
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
	public void sendAction(String action) {

		this.envOutStream.println(action);
		this.waitForAck(this.envInStream);
		LOGGER.info("Sent action " + action);
	}

	public void sendControlAction(String action) {

		this.envOutStream.println(action);
		LOGGER.info("Sent control action " + action);
	}

	@Override
	public String[] getObservation() {

		String[] observation = null;

		try {
			String obs = this.envInStream.readLine().trim();

			/* create observation array */
			observation = this.handleObservation(obs);
			LOGGER.info("Got observation " + Arrays.toString(observation));

			/* send ack */
			this.sendAck(this.envOutStream);
		}

		catch (IOException e) {
			LOGGER.error("While getting observations from env: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}

		if (observation == null)
			LOGGER.error("observation is null");

		return observation;
	}

	// ---------------------------------------------------------------------------------

	abstract public boolean syncEstablished(BufferedReader envInStream);

	abstract public void waitForAck(BufferedReader envInStream);

	abstract public void sendAck(PrintWriter envOutStream);

	abstract public String[] handleObservation(String obs);

	public String[] step(String action) {

		/* handshake with the env server if required */
		this.establishSync();

		/* send action */
		this.sendAction(action);

		/* get observation */
		String[] observation = this.getObservation();

		/* close the connection */
		this.closeConnection();

		return observation;
	}

	public void closeConnection() {

		try {
			this.envConnectorSocket.close();
		} catch (Exception e) {
			LOGGER.error("While closing the socket: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}

	}

}
