/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.IPOMDP;

import java.util.HashMap;
import java.util.List;
import thinclab.legacy.DD;
import thinclab.models.Model;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class IPOMDPEC extends IPOMDP {

	public IPOMDPEC(List<String> S, List<String> O, String A, String Aj, String Mj, String EC, String Thetaj,
			List<Tuple<String, Model>> frames_j, HashMap<String, Model> dynamics, HashMap<String, DD> R, float discount,
			int H, String name) {

		super(S, O, A, Aj, Mj, EC, Thetaj, frames_j, dynamics, R, discount, H, name);
		// TODO Auto-generated constructor stub
	}

}
