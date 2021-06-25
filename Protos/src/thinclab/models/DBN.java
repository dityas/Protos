/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models;

import java.util.HashMap;
import java.util.stream.IntStream;
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
public class DBN extends DirectedGraphicalModel {

	private static final Logger LOGGER = LogManager.getLogger(DBN.class);
	
	public DBN(HashMap<Integer, DD> cpds) {
		this.cpds = cpds;
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
		
		builder.append("DBN : [\r\n");
		this.cpds.entrySet().stream()
			.forEach(e -> builder.append(Global.varNames.get(e.getKey() - 1))
					.append(" : ").append(e.getValue()).append("\r\n"));
		
		builder.append("]\r\n");
		
		return builder.toString();
	}

}
