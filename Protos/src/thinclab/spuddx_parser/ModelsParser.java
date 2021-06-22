/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.spuddx_parser;

import java.util.HashMap;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.models.DBN;
import thinclab.models.Model;
import thinclab.models.POMDP;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class ModelsParser extends SpuddXBaseVisitor<HashMap<String, Model>> {

	private HashMap<String, DD> declaredDDs;
	private HashMap<String, Model> declaredModels;
	private DDParser ddParser;

	private static final Logger LOGGER = LogManager.getLogger(ModelsParser.class);

	public ModelsParser(HashMap<String, DD> declaredDDs) {

		super();
		this.declaredDDs = declaredDDs;
		this.declaredModels = new HashMap<String, Model>(5);
		this.ddParser = new DDParser(this.declaredDDs);
	}

	public ModelsParser(HashMap<String, DD> declDDs, HashMap<String, Model> declModels) {

		super();
		this.declaredDDs = declDDs;
		this.declaredModels = declModels;
		this.ddParser = new DDParser(this.declaredDDs);
	}

	@Override
	public HashMap<String, Model> visitPOMDPDef(SpuddXParser.POMDPDefContext ctx) {

		String modelName = ctx.pomdp_def().agent_name().getText();

		var S = ctx.pomdp_def().states_list().variable_name().stream().map(s -> s.getText())
				.collect(Collectors.toList());

		var O = ctx.pomdp_def().obs_list().variable_name().stream().map(o -> o.getText()).collect(Collectors.toList());

		String A = ctx.pomdp_def().action_var().variable_name().getText();

		var pomdp = new POMDP(S, O, A);
		this.declaredModels.put(modelName, pomdp);
		LOGGER.info(String.format("Parsed POMDP %s : %s", modelName, pomdp));

		return this.declaredModels;
	}

	@Override
	public HashMap<String, Model> visitDBNDef(SpuddXParser.DBNDefContext ctx) {

		String modelName = ctx.dbn_def().dbn_name().getText();

		// Parse <varName, DD> tuples from (cpd_def)*
		var cpds = ctx.dbn_def().cpd_def().stream()
				.map(d -> new Tuple<String, DD>(d.variable_name().getText(), this.ddParser.visit(d.dd_expr())))
				.collect(Collectors.toList());

		// Split into int[] and DD[] arrays for vars and dds
		int[] vars = cpds.stream().mapToInt(t -> Global.varNames.indexOf(t.first()) + 1).toArray();
		DD[] dds = cpds.stream().map(t -> t.second()).toArray(DD[]::new);

		var dbn = new DBN(vars, dds);
		this.declaredModels.put(modelName, dbn);
		LOGGER.info(String.format("Parsed DBN: %s : %s", modelName, dbn));

		return this.declaredModels;
	}

	@Override
	public HashMap<String, Model> visitDomain(SpuddXParser.DomainContext ctx) {

		ctx.model_decl().stream().forEach(m -> this.visit(m));

		return this.declaredModels;
	}
}
