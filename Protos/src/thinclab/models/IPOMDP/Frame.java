/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.IPOMDP;

import java.util.List;
import java.util.Map;
import thinclab.utils.Tuple3;

/*
 * @author adityas
 *
 */
public interface Frame<M> {
	
	public List<MjRepr<M>> allModels();
	
	public List<MjRepr<M>> bMj();
	
	public Map<MjRepr<M>, Integer> optAMap();
	
	public List<Tuple3<MjRepr<M>, List<Integer>, MjRepr<M>>> getTriples();

}
