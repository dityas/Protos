/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.POMDP;

/*
 * @author adityas
 *
 */
public class OpponentModel {
	/*
	 * Defines a single interactive state <belief, frame> combination
	 */
	public DD belief;
	public POMDP frame;
	
	public OpponentModel(DD belief, POMDP frame) {
		/*
		 * Initialize from <belief, frame> combination
		 */
		this.belief = belief;
		this.frame = frame;
	}
	
	// -----------------------------------------------------------------------------

	@Override
	public String toString() {
		return "OpponentModel [belief=" + belief + ", frame=" + frame + "]\r\n";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((belief == null) ? 0 : belief.hashCode());
		result = prime * result + ((frame == null) ? 0 : frame.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OpponentModel other = (OpponentModel) obj;
		if (belief == null) {
			if (other.belief != null)
				return false;
		} else if (!belief.equals(other.belief))
			return false;
		if (frame == null) {
			if (other.frame != null)
				return false;
		} else if (!frame.equals(other.frame))
			return false;
		return true;
	}

}
