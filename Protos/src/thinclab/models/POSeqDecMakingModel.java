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
	public List<Integer> i_Om();

	public List<Integer> i_Om_p();

	public List<Integer> i_S_p();

	public int i_A();

	public List<String> Om();

	public List<String> A();

	public List<List<R>> O();

	public List<List<R>> T();

	public List<R> R();

	// belief update implementations
	public R beliefUpdate(R b, int a, List<Integer> o);

	public R beliefUpdate(R b, String a, List<String> o);

	public R obsLikelihoods(R b, int a);
	
	// step to next belief
	//public void step(Set<Tuple<Integer, ReachabilityNode>> modelFilter);
	public void step();
	public R step(R b, int a, List<Integer> o);
	public R step(R b, String a, List<String> o);

}
