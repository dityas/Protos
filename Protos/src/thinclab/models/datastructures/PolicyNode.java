/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.datastructures;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import com.google.gson.JsonObject;
import thinclab.utils.Jsonable;

/*
 * @author adityas
 *
 */
public class PolicyNode implements Jsonable {

	final public String actName;
	final public int alphaId;
	final public int actId;
	
	public PolicyNode(int alphaId, int actId, String actName) {

		this.alphaId = alphaId; 
		this.actId = actId;
		this.actName = actName;
	}
	
	@Override
	public String toString() {
		
		return "PolicyNode";
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof PolicyNode))
			return false;

		var node = (PolicyNode) obj;

		if (node.alphaId == alphaId && node.actId == actId && node.actName.contentEquals(actName))
			return true;

		return false;
	}

	@Override
	public int hashCode() {

		var builder = new HashCodeBuilder();
		builder.append(alphaId).append(actId).append(actName);

		return builder.toHashCode();
	}

	@Override
	public JsonObject toJson() {

		var json = new JsonObject();
		json.addProperty("alpha", alphaId);
		json.addProperty("actId", actId);
		json.addProperty("act", actName);
		
		return json;
	}
}
