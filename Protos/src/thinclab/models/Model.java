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
public interface Model {

	// for now, Model is a type which has getters for state vars and their indices
	public List<Integer> i_S();

	public List<String> S();
	
	default String getName() {
		return "Unnamed Model";
	}
}
