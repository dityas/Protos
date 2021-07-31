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

	private final L _0;
	private final R _1;

	public Tuple(L first, R second) {

		this._0 = first;
		this._1 = second;
	}

	public L _0() {

		return this._0;
	}

	public R _1() {

		return this._1;
	}

	@Override
	public String toString() {

		var builder = new StringBuilder();
		builder.append("(").append(this._0).append(", ").append(this._1).append(")");
		
		return builder.toString();
	}
}
