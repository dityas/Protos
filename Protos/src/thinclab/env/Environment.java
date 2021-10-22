/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.env;

import java.util.List;

/*
 * @author adityas
 *
 */
public interface Environment {

	public int[] getObsForVars(int[] O);

	public int[] getStateForVars(int[] S);

	public void step(List<String> actions);
}
