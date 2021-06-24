/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;

/*
 * @author adityas
 *
 */
public class POMDP implements Model {

	public List<String> S;
	public List<String> O;
	public List<String> A;

	public int[] Svars;
	public int[] Ovars;
	public int Avar;

	public HashMap<String, DD[]> TF = new HashMap<>(3);
	public HashMap<String, DD[]> OF = new HashMap<>(3);

	private static final Logger LOGGER = LogManager.getLogger(POMDP.class);

	public POMDP(List<String> S, List<String> O, String A, 
			HashMap<String, Model> dynamics) {

		this.S = this.sortByVarOrdering(S, Global.varNames);
		this.O = this.sortByVarOrdering(O, Global.varNames);
		this.A = Global.valNames.get(Global.varNames.indexOf(A));

		this.Svars = this.S.stream().mapToInt(s -> Global.varNames.indexOf(s) + 1).toArray();
		this.Ovars = this.O.stream().mapToInt(o -> Global.varNames.indexOf(o) + 1).toArray();
		this.Avar = Global.varNames.indexOf(A) + 1;
		
		this.initializeDynamics((HashMap<String, DBN>) dynamics.entrySet().stream()
				.filter(e -> e.getValue() instanceof DBN)
				.collect(Collectors.toMap(e -> e.getKey(), e -> (DBN) e.getValue())));
	}

	protected DD[] getTransitionFunction(DBN dbn) {

		DD[] Ta = new DD[this.Svars.length];

		for (int i = 0; i < Ta.length; i++) {

			if (dbn.cpds.containsKey(Svars[i]))
				Ta[i] = dbn.cpds.get(this.Svars[i]);

			else
				Ta[i] = DBN.getSameTransitionDD(Global.varNames.get(this.Svars[i] - 1));
		}

		return Ta;
	}

	protected DD[] getObsFunction(DBN dbn) {

		DD[] Oa = new DD[this.Ovars.length];

		for (int i = 0; i < Oa.length; i++) {

			if (dbn.cpds.containsKey(Ovars[i]))
				Oa[i] = dbn.cpds.get(this.Ovars[i]);

			else
				Oa[i] = DDnode.getUniformDist(this.Ovars[i] + (Global.NUM_VARS / 2));
		}

		return Oa;
	}

	protected void initializeDynamics(HashMap<String, DBN> dynamics) {

		dynamics.entrySet().stream().forEach(e -> {

			if (!Global.valNames.get(this.Avar - 1).contains(e.getKey())) {

				LOGGER.error(String.format("Unknown action %s", e.getKey()));
				System.exit(-1);
			}

			this.TF.put(e.getKey(), this.getTransitionFunction(e.getValue()));
			this.OF.put(e.getKey(), this.getObsFunction(e.getValue()));

		});

	}

	private List<String> sortByVarOrdering(List<String> varList, List<String> ordering) {

		var unknownVar = varList.stream().filter(v -> ordering.indexOf(v) < 0).findFirst();
		if (unknownVar.isPresent()) {

			LOGGER.error(String.format("Symbol %s is not defined in %s", unknownVar.get(), ordering));
			return null;
		}

		Collections.sort(varList, (a, b) -> ordering.indexOf(a) - ordering.indexOf(b));
		return varList;
	}

	@Override
	public String toString() {

		var builder = new StringBuilder();
		builder.append("POMDP: [").append("\r\n");
		builder.append("S : ").append(this.S).append("\r\n");
		builder.append("S vars : ").append(Arrays.toString(this.Svars)).append("\r\n");
		builder.append("O : ").append(this.O).append("\r\n");
		builder.append("O vars : ").append(Arrays.toString(this.Ovars)).append("\r\n");
		builder.append("A : ").append(this.A).append("\r\n");
		builder.append("A var : ").append(this.Avar).append("\r\n");
		builder.append("T funct. : ").append(this.TF).append("\r\n");
		builder.append("O funct. : ").append(this.OF).append("\r\n");
		builder.append("]").append("\r\n");

		return builder.toString();
	}

}
