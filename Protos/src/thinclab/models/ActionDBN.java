/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.legacy.DD;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public class ActionDBN implements Model {

	public DD[] transition_function;
	public List<String> jointActionSequence = new ArrayList<>(5);

	private static final Logger LOGGER = LogManager.getLogger(ActionDBN.class);

	public ActionDBN(Collection<String> jointActionSequence) {
		this.jointActionSequence.addAll(jointActionSequence);
	}

	public ActionDBN(Collection<String> jointActionSequence, DD[] transitionF) {
		this.jointActionSequence.addAll(jointActionSequence);
		this.transition_function = transitionF;
	}

	public static DD getSameTransitionDD(String varName) {

		int varIndex = Global.varNames.indexOf(varName);
		int primedVarIndex = Global.varNames.indexOf(varName + "'");

		if (varIndex < 0 || primedVarIndex < 0) {
			LOGGER.error("Variable " + varName + " or its prime does not exist");
			System.exit(-1);
		}

		var children = Global.valNames.get(primedVarIndex).stream()
						.map(c -> DDnode.getDDForChild(varName + "'", c))
						.toArray(DD[]::new);

		var dd = DDnode.getDD(varIndex + 1, children);

		return OP.reorder(dd);
	}
	
	@Override
	public String toString() {
		
		var builder = new StringBuilder();
		builder.append("ActionDBN: ").append(this.jointActionSequence).append("\r\n");
		
		for (int i = 0; i < this.transition_function.length; i++)
			builder.append("var: ").append(Global.varNames.get(i))
								   .append("\t\t")
								   .append("T: ")
								   .append(this.transition_function[i])
								   .append("\r\n");
		
		return builder.toString();
	}
}
