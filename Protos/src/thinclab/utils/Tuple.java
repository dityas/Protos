/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils;

/*
 * @author adityas
 *
 */
public class Tuple<L, R> {
	
	private L first;
	private R second;
	
	public Tuple(L first, R second) {
		this.first = first;
		this.second = second;
	}
	
	public L first() {
		return this.first;
	}
	
	public R second() {
		return this.second;
	}

}
