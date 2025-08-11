/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/*
 * @author adityas
 *
 */
public class Tuple3<A, B, C> {

	private final A _0;
	private final B _1;
	private final C _2;

	private final int hash;

	public Tuple3(A a, B b, C c) {

		this._0 = a;
		this._1 = b;
		this._2 = c;
		
		var builder = new HashCodeBuilder().append(_0).append(_1).append(_2);
		this.hash = builder.toHashCode();

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
	public int hashCode() {

		return this.hash;
	}

	@Override
	public boolean equals(Object other) {

		if (this == other)
			return true;

		if (!(other instanceof Tuple3<?, ?, ?>))
			return false;

		Tuple3<A, B, C> _other = (Tuple3<A, B, C>) other;

		if (!_0.equals(_other._0()))
			return false;

		if (!_1.equals(_other._1()))
			return false;

		if (!_2.equals(_other._2()))
			return false;

		return true;
	}

	@Override
	public String toString() {

		var builder = new StringBuilder();
		builder.append("(").append(this._0).append(", ").append(this._1).append(", ").append(this._2).append(")");

		return builder.toString();
	}

}
