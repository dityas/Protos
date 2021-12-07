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
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public interface Simulator<E extends Environment<DD>> {

	public Episode run(E env, DD s,
			List<Tuple<PBVISolvablePOMDPBasedModel, SymbolicPerseusSolver<PBVISolvablePOMDPBasedModel>>> agents,
			int len);

}
