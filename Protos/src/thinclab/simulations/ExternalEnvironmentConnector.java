/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.simulations;

/*
 * @author adityas
 *
 */
public interface ExternalEnvironmentConnector {
	
	/*
	 * Provides an interface for agent based simulations to interact with external 
	 * environments
	 */
	
	public void establishSync();
	public void sendAction(String action);
	public String[] getObservation();

}
