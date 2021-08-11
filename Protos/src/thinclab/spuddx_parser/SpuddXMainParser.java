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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.model_ops.belief_exploration.SSGAExploration;
import thinclab.models.DBN;
import thinclab.models.Model;
import thinclab.models.POMDP;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.spuddx_parser.SpuddXParser.DBNDefContext;
import thinclab.spuddx_parser.SpuddXParser.DDDefContext;
import thinclab.spuddx_parser.SpuddXParser.PBVISolverDefContext;
import thinclab.spuddx_parser.SpuddXParser.POMDPDefContext;
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
	private ModelsParser modelParser = new ModelsParser(this.ddParser, this.models);

	// visitor for parsing variable definitions
	private VarDefVisitor varVisitor = new VarDefVisitor();

	// solvers
	private HashMap<String, SymbolicPerseusSolver<POMDP>> pomdpSolvers = new HashMap<>(5);

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
		Model dbn = this.modelParser.visit(ctx);

		if (!(dbn instanceof DBN)) {

			LOGGER.error(String.format("%s should be a DBN but is %s", modelName, dbn.getClass().getTypeName()));
			System.exit(-1);
		}

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

	@Override
	public void enterPBVISolverDef(PBVISolverDefContext ctx) {

		String name = ctx.pbvi_solv_def().solv_name().IDENTIFIER().getText();
		String modelName = ctx.pbvi_solv_def().model_name().IDENTIFIER().getText();
		var model = this.models.get(modelName);

		if (model == null) {

			LOGGER.error(String.format("Model %s for which solver %s is defined does not exist", modelName, name));
			System.exit(-1);
		}

		if (model instanceof POMDP) {

			SymbolicPerseusSolver<POMDP> solver = new SymbolicPerseusSolver<>();
			var _model = (POMDP) model;
			
			this.pomdpSolvers.put(name, solver);
			LOGGER.debug(String.format("Parsed solver %s for POMDP %s", name, modelName));
			
			this.pomdpSolvers.get(name).solve(List.of(_model.b_i()), _model, 100, 10, new SSGAExploration<>(0.1f), AlphaVectorPolicy.fromR(_model.R()));
		}
	}

	// ----------------------------------------------------------------------------------------

	public HashMap<String, DD> getDDs() {

		return this.dds;
	}

	public Optional<Model> getModel(String modelName) {

		return Optional.ofNullable(this.models.get(modelName));
	}
	
}
