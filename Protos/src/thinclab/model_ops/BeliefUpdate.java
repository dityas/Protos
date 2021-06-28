/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.model_ops;

import thinclab.legacy.DD;
import thinclab.models.Model;

/*
 * @author adityas
 *
 */
public interface BeliefUpdate<M extends Model> {
	
	public DD beliefUpdate(M model, DD b, int a, int[][] o); 

}
