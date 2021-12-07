/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.env;

import java.util.List;
import thinclab.legacy.DD;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public interface Environment<S extends DD> {

	public void init(S s);
	
	public Tuple<List<Integer>, List<Integer>> step(List<Tuple<Integer, Integer>> actions);
}
