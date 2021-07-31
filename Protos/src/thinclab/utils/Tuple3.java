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
public class Tuple3<A, B, C> {

	private final A _0;
	private final B _1;
	private final C _2;

	public Tuple3(A a, B b, C c) {

		this._0 = a;
		this._1 = b;
		this._2 = c;
	}

	public A _0() {

		return this._0;
	}

	public B _1() {

		return this._1;
	}

	public C _2() {

		return this._2;
	}

	@Override
	public String toString() {

		var builder = new StringBuilder();
		builder.append("(").append(this._0).append(", ").append(this._1).append(", ").append(this._2).append(")");

		return builder.toString();
	}

}
