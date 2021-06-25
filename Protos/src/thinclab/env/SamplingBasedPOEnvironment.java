/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.env;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.legacy.DD;
import thinclab.models.DirectedGraphicalModel;

/*
 * @author adityas
 *
 */
public class SamplingBasedPOEnvironment extends PartiallyObservableEnvironment {

	private HashMap<List<String>, DirectedGraphicalModel> dynamics = new HashMap<>(10);
	
	private static final Logger LOGGER = LogManager.getLogger(SamplingBasedPOEnvironment.class);

	public void addDynamicsForAction(List<String> actions, DirectedGraphicalModel dbn) {
		LOGGER.debug(String.format("Added dynamics for action %s", actions));
		this.dynamics.put(actions, dbn);
	}
	
	@Override
	public DD getObservationStateless(DD previousState) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DD getObservation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DD stepStateless(DD previousState) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DD step() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		
		var builder = new StringBuilder();
		builder.append("ENV: ").append("\r\n");
		
		for (var actions: this.dynamics.keySet()) {
			builder.append(this.dynamics.get(actions)).append("\r\n");
		}
		
		return builder.toString();
	}

}
