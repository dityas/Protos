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

/*
 * @author adityas
 *
 */
public class ModelsParser extends SpuddXBaseVisitor<Model> {

	private HashMap<String, Model> declaredModels;
	private DDParser ddParser;

	private static final Logger LOGGER = LogManager.getLogger(ModelsParser.class);

	public ModelsParser(DDParser ddParser, HashMap<String, Model> declaredModels) {

		super();
		this.ddParser = ddParser;
		this.declaredModels = declaredModels;
	}

	@Override
	public Model visitOtherDefParen(SpuddXParser.OtherDefParenContext ctx) {

		return this.visit(ctx.all_def());
	}
	
	@Override
	public Model visitPOMDPDef(SpuddXParser.POMDPDefContext ctx) {

		var S = ctx.pomdp_def().states_list().var_name().stream().map(s -> s.getText())
				.collect(Collectors.toList());

		var O = ctx.pomdp_def().obs_list().var_name().stream().map(o -> o.getText()).collect(Collectors.toList());

		String A = ctx.pomdp_def().action_var().var_name().getText();

		HashMap<String, Model> dynamics = new HashMap<>(5);
		HashMap<String, DD> R = new HashMap<>(5);

		ctx.pomdp_def().dynamics().action_model().stream()
				.forEach(a -> dynamics.put(a.action_name().getText(), this.visit(a.all_def())));

		ctx.pomdp_def().reward().action_reward().stream().forEach(ar -> {

			R.put(ar.action_name().getText(), this.ddParser.visit(ar.dd_expr()));
		});

		DD b = this.ddParser.visit(ctx.pomdp_def().initial_belief().dd_expr());
		float discount = Float.valueOf(ctx.pomdp_def().discount().FLOAT_NUM().getText());

		var pomdp = new POMDP(S, O, A, dynamics, R, b, discount);

		return pomdp;
	}

	@Override
	public Model visitDBNDef(SpuddXParser.DBNDefContext ctx) {

		HashMap<Integer, DD> cpds = new HashMap<>(5);

		// <var, DD> hashmap entries from (cpd_def)*
		ctx.dbn_def().cpd_def().stream().forEach(d -> cpds.put(Global.varNames.indexOf(d.var_name().getText()) + 1,
				this.ddParser.visit(d.dd_expr())));

		var dbn = new DBN(cpds);

		return dbn;
	}

	@Override
	public Model visitPreDefModel(SpuddXParser.PreDefModelContext ctx) {

		var name = ctx.model_name().IDENTIFIER().getText();

		if (this.declaredModels.containsKey(name))
			return this.declaredModels.get(name);

		else {

			LOGGER.error(String.format("Model %s is not defined", name));
			System.exit(-1);
			return null;
		}
	}
}

