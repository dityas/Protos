/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.spuddx_parser;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.models.DBN;
import thinclab.models.Model;
import thinclab.models.POMDP;
import thinclab.spuddx_parser.SpuddXParser.DBNDefContext;
import thinclab.spuddx_parser.SpuddXParser.DDDefContext;
import thinclab.spuddx_parser.SpuddXParser.Dbn_defContext;
import thinclab.spuddx_parser.SpuddXParser.POMDPDefContext;
import thinclab.spuddx_parser.SpuddXParser.Pomdp_defContext;
import thinclab.spuddx_parser.SpuddXParser.PreDefModelContext;
import thinclab.spuddx_parser.SpuddXParser.Var_defsContext;

/*
 * @author adityas
 *
 */
public class SpuddXMainParser extends SpuddXBaseListener {

	/*
	 * Parses and runs the SPUDDX domain
	 */

	// parsed DDs
	private HashMap<String, DD> dds = new HashMap<>(10);
	private DDParser ddParser = new DDParser(this.dds);

	// parsed Models
	private HashMap<String, Model> models = new HashMap<>(10);
	private ModelsParser modelParser = new ModelsParser(this.ddParser);

	// visitor for parsing variable definitions
	private VarDefVisitor varVisitor = new VarDefVisitor();

	private String fileName;
	private SpuddXParser parser;

	private static final Logger LOGGER = LogManager.getLogger(SpuddXMainParser.class);

	// ---------------------------------------------------------------------------------

	public SpuddXMainParser(String fileName) {

		this.fileName = fileName;

		try {

			// Get tokens from lexer
			InputStream is = new FileInputStream(this.fileName);
			ANTLRInputStream antlrIs = new ANTLRInputStream(is);
			SpuddXLexer lexer = new SpuddXLexer(antlrIs);
			TokenStream tokens = new CommonTokenStream(lexer);

			this.parser = new SpuddXParser(tokens);

		}

		catch (Exception e) {

			LOGGER.error(String.format("Error while trying to parse %s: %s", this.fileName, e));
			System.exit(-1);
		}

	}

	public void run() {

		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(this, this.parser.domain());
	}

	@Override
	public void exitVar_defs(Var_defsContext ctx) {

		// get all variable definitions
		var variables = ctx.var_def().stream().map(v -> varVisitor.visit(v)).collect(Collectors.toList());
		LOGGER.info(String.format("Parsed variables: %s", variables));

		Global.primeVarsAndInitGlobals(variables);

		super.exitVar_defs(ctx);
	}

	@Override
	public void enterDDDef(DDDefContext ctx) {

		String ddName = ctx.dd_def().dd_name().IDENTIFIER().getText();
		DD dd = this.ddParser.visit(ctx.dd_def().dd_expr());

		this.dds.put(ddName, dd);
		LOGGER.debug(String.format("Parsed DD %s", ddName));

		super.enterDDDef(ctx);
	}

	@Override
	public void enterDBNDef(DBNDefContext ctx) {

		String modelName = ctx.dbn_def().model_name().IDENTIFIER().getText();
		DBN dbn = this.parseDBN(ctx);

		this.models.put(modelName, dbn);
		LOGGER.debug(String.format("Parsed DBN %s", modelName));

		super.enterDBNDef(ctx);
	}

	@Override
	public void enterPOMDPDef(POMDPDefContext ctx) {

		String modelName = ctx.pomdp_def().model_name().IDENTIFIER().getText();
		Model pomdp = this.modelParser.visit(ctx);

		this.models.put(modelName, pomdp);
		LOGGER.debug(String.format("Parsed POMDP %s", modelName));

		super.enterPOMDPDef(ctx);
	}

	// ----------------------------------------------------------------------------------------

	public DBN parseDBN(DBNDefContext ctx) {

		return this.parseDBN(ctx.dbn_def());
	}

	public DBN parseDBN(Dbn_defContext ctx) {

		HashMap<Integer, DD> cpds = new HashMap<>(5);

		// <var, DD> hashmap entries from (cpd_def)*
		ctx.cpd_def().stream().forEach(
				d -> cpds.put(Global.varNames.indexOf(d.var_name().getText()) + 1, this.ddParser.visit(d.dd_expr())));

		var dbn = new DBN(cpds);
		return dbn;
	}

	public POMDP parsePOMDP(POMDPDefContext ctx) {

		return this.parsePOMDP(ctx.pomdp_def());
	}

	public POMDP parsePOMDP(Pomdp_defContext ctx) {

		var S = ctx.states_list().var_name().stream().map(s -> s.getText()).collect(Collectors.toList());

		var O = ctx.obs_list().var_name().stream().map(o -> o.getText()).collect(Collectors.toList());

		String A = ctx.action_var().var_name().getText();

		HashMap<String, Model> dynamics = new HashMap<>(5);
		HashMap<String, DD> R = new HashMap<>(5);

		ctx.dynamics().action_model().stream().forEach(a ->
			{

				String actionName = a.action_name().IDENTIFIER().getText();
				DBN actModel = null;

				// DBN reference is given
				if (a.all_def() instanceof PreDefModelContext) {

					String name = ((PreDefModelContext) a.all_def()).model_name().IDENTIFIER().getText();
					Model model = this.models.get(name);

					if (!(model instanceof DBN)) {

						LOGGER.error(String.format("%s in POMDP is not of type DBN", name));
						System.exit(-1);
					}
					actModel = (DBN) model;

				}

				// DBN is defined in place
				else if (a.all_def() instanceof DBNDefContext)
					actModel = this.parseDBN((DBNDefContext) a.all_def());

				else {
					LOGGER.error(String.format("Could not parse %s because it is not a DBN.", a.getText()));
					System.exit(-1);
				}

				dynamics.put(actionName, actModel);
			});

		ctx.reward().action_reward().stream().forEach(ar ->
			{

				R.put(ar.action_name().getText(), this.ddParser.visit(ar.dd_expr()));
			});

		DD b = this.ddParser.visit(ctx.initial_belief().dd_expr());
		float discount = Float.valueOf(ctx.discount().FLOAT_NUM().getText());

		var pomdp = new POMDP(S, O, A, dynamics, R, b, discount);
		return pomdp;
	}

	// ----------------------------------------------------------------------------------------

	public HashMap<String, DD> getDDs() {

		return this.dds;
	}
}
