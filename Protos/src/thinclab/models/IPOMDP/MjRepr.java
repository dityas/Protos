/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.IPOMDP;

import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class MjRepr<M> extends Tuple<Integer, M> {

	final public int frame;
	final public M m;

	public MjRepr(Integer first, M second) {

		super(first, second);
		frame = this._0();
		m = this._1();
	}
}
