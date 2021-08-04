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
public class Tuple<L, R> {

	private final L _0;
	private final R _1;

	private final int hash;

	public Tuple(L first, R second) {

		this._0 = first;
		this._1 = second;

		var builder = new HashCodeBuilder().append(_0).append(_1);
		this.hash = builder.build();
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

	@Override
	public int hashCode() {

		return this.hash;
	}

	@Override
	public boolean equals(Object other) {

		if (this == other)
			return true;

		if (!(other instanceof Tuple<?, ?>))
			return false;

		Tuple<L, R> _other = (Tuple<L, R>) other;
		if (!_0.equals(_other._0()))
			return false;
		if (!_1.equals(_other._1()))
			return false;

		return true;
	}
}
