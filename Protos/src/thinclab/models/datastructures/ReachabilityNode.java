/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.datastructures;

import java.util.Set;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public class ReachabilityNode {

	public final int alphaId;
	public Set<DD> beliefs;

	public ReachabilityNode(int alphaId) {

		this.alphaId = alphaId;
	}

	@Override
	public int hashCode() {

		var builder = new HashCodeBuilder();
		builder.append(alphaId);
		builder.append(beliefs);

		return builder.hashCode();
	}

	@Override
	public boolean equals(Object other) {

		if (other == this)
			return true;

		else if (!(other instanceof ReachabilityNode))
			return false;

		ReachabilityNode n = (ReachabilityNode) other;

		if (n.alphaId != alphaId)
			return false;

		if (!n.beliefs.equals(beliefs))
			return false;

		return true;
	}

}
