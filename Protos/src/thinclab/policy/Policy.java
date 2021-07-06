/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policy;

import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public interface Policy {

	public int getBestActionIndex(DD belief);
	public String getBestAction(DD belief);
}
