/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations;

import java.util.HashMap;
import java.util.List;

import thinclab.ddinterface.DDMaker;
import thinclab.legacy.DD;
import thinclab.legacy.StateVar;

/*
 * @author adityas
 *
 */
public interface LowerLevelModel {
	
	/* 
	 * Defines the functionality that any object representing the lower level model
	 * must Implement
	 */
	
	/* Create Random variable of currently known mjs */
	public StateVar getOpponentModelStateVar(int index);
	
	/* make the factors P(Aj1|Mj), P(Aj2|Mj), ... P(Ajn|Mj) */
	public DD[] getAjGivenMj(DDMaker ddMaker, HashMap<String, List<String>> Ajs);

}
