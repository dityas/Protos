/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.env;

import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public interface Simulator<E extends Environment<DD>> {
	
	public Episode run(E env, DD s);

}
