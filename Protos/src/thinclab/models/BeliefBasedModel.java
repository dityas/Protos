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
public interface BeliefBasedModel<B> {
	
	public B b_i();

	public B beliefUpdate(B b, int a, int[][] o); 
	public B beliefUpdate(B b, String a, List<String> o); 

}
