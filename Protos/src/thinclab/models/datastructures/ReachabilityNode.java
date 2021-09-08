/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.datastructures;

import java.util.Collection;
import java.util.HashSet;
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
	public final int i_a;
	public int h = -1;

	public ReachabilityNode(int alphaId, int actId) {

		this.alphaId = alphaId;
		this.i_a = actId;
		beliefs = new HashSet<DD>(5);
	}

	public ReachabilityNode(int alphaId, int actId, Collection<DD> beliefPoints) {

		this(alphaId, actId);
		beliefs.addAll(beliefPoints);
	}

	@Override
	public int hashCode() {

		var builder = new HashCodeBuilder();
		builder.append(alphaId);
		builder.append(i_a);
		builder.append(beliefs);
		builder.append(h);

		return builder.hashCode();
	}

	@Override
	public boolean equals(Object other) {

		if (other == this)
			return true;

		else if (!(other instanceof ReachabilityNode))
			return false;

		ReachabilityNode n = (ReachabilityNode) other;

		if (n.alphaId != alphaId || n.i_a != i_a || n.h != h)
			return false;

		if (!n.beliefs.equals(beliefs))
			return false;

		return true;
	}

	@Override
	public String toString() {

		var builder = new StringBuilder();
		builder.append("ReachabilityNode [").append("alphaId = ").append(alphaId).append(" [b] = ").append(beliefs)
				.append("]");
		
		return builder.toString();
	}

}
