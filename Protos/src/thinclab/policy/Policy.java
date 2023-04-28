/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policy;

/*
 * @author adityas
 *
 */
public interface Policy<R> {

	public int getBestActionIndex(R belief);
}
