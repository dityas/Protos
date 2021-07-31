/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models;

import java.util.List;

/*
 * @author adityas
 *
 */
public interface POSeqDecMakingModel<R> extends Model {

	// POSeqDecMakingModel is a typeclass which has getters for all constituents of
	// the POMDP tuple
	public int[] i_Om();

	public int i_A();

	public List<String> Om();

	public List<String> A();

	public R[][] O();

	public R[][] T();

	public R[] R();
}
