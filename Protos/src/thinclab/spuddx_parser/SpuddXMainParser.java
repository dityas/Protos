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
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.env.Environment;
import thinclab.env.PartiallyObservableEnv;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.model_ops.belief_exploration.PolicyTreeExpansion;
import thinclab.models.DBN;
import thinclab.models.Model;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.POMDP;
import thinclab.models.IPOMDP.IPOMDP;
import thinclab.models.IPOMDP.MjRepr;
import thinclab.models.datastructures.ModelGraph;
import thinclab.models.datastructures.ReachabilityNode;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.policy.Policy;
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.spuddx_parser.SpuddXParser.DBNDefContext;
import thinclab.spuddx_parser.SpuddXParser.DDDefContext;
import thinclab.spuddx_parser.SpuddXParser.DDExecDefContext;
import thinclab.spuddx_parser.SpuddXParser.EnvDefContext;
import thinclab.spuddx_parser.SpuddXParser.IPOMDPDefContext;
import thinclab.spuddx_parser.SpuddXParser.Modelvar_init_defContext;
import thinclab.spuddx_parser.SpuddXParser.PBVISolverDefContext;
import thinclab.spuddx_parser.SpuddXParser.POMDPDefContext;
import thinclab.spuddx_parser.SpuddXParser.ParenExecExprContext;
import thinclab.spuddx_parser.SpuddXParser.PolTreeExprContext;
import thinclab.spuddx_parser.SpuddXParser.SolvExprContext;
import thinclab.spuddx_parser.SpuddXParser.Var_defsContext;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class SpuddXMainParser extends SpuddXBaseListener {

    /*
     * Parses and runs the SPUDDX domain
     */

    // all definitions
    private HashMap<String, Object> envMap = new HashMap<>();

    // parsed DDs
    private HashMap<String, DD> dds = new HashMap<>(10);
    private DDParser ddParser = new DDParser(this.dds);

    // parsed Models
    private HashMap<String, Model> models = new HashMap<>(10);
    private ModelsParser modelParser = new ModelsParser(this.ddParser, this.models);

    // parsed environments
    public HashMap<String, Environment<DD>> envs = new HashMap<>(10);

    // visitor for parsing variable definitions
    private VarDefVisitor varVisitor = new VarDefVisitor();

    // solvers
    private HashMap<String, SymbolicPerseusSolver<? extends PBVISolvablePOMDPBasedModel>> solvers = new HashMap<>(5);

    // policies
    private HashMap<String, Policy<DD>> policies = new HashMap<>(5);

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

            LOGGER.error(String.format("Error while trying to parse %s: %s", 
                        this.fileName, e));
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

        int totalStates = IntStream.range(0, Global.NUM_VARS / 2)
            .mapToObj(i -> Global.valNames.get(i).size())
            .reduce(1, (p, q) -> p * q);

        LOGGER.info(String.format("Domain has %s random variables representing approx. %s initial states", 
                    Global.NUM_VARS / 2, totalStates));

        super.exitVar_defs(ctx);
    }

    @Override
    public void enterModelvar_init_def(Modelvar_init_defContext ctx) {

        String varName = ctx.var_name(0).IDENTIFIER().getText();
        int varIndex = Global.varNames.indexOf(varName);

        String frameVarName = ctx.var_name(1).IDENTIFIER().getText();
        int frameVarIndex = Global.varNames.indexOf(frameVarName);

        if (varIndex < 0)
            this.errorAndExit(String.format("Variable %s does not exist", varName));

        if (frameVarIndex < 0)
            this.errorAndExit(String.format("Frame variable %s does not exist", frameVarName));

        var models = ctx.model_init().stream()
            .map(t -> Tuple.of(t.frame_name().var_value().IDENTIFIER().getText(),
                        t.var_value().IDENTIFIER().getText(), this.ddParser.visit(t.dd_expr())))
            .map(t -> Tuple.of(Global.valNames.get(frameVarIndex).indexOf(t._0()), t._1(), t._2()))
            .collect(Collectors.toList());

        models.stream().forEach(m -> {

            if(!Global.modelVars.containsKey(varName))
                Global.modelVars.put(varName, new HashMap<>());

            var modelDict = Global.modelVars.get(varName);
            var model = new MjRepr<>(m._0(), ReachabilityNode.getStartNode(-1, m._2()));
            if (!modelDict.containsKey(model))
                modelDict.put(model, m._1());
        });

        LOGGER.info(String.format("Model variable %s initialized to %s", varName, Global.modelVars.get(varName)));
        super.enterModelvar_init_def(ctx);
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
        LOGGER.debug(String.format("Parsing DBN %s", modelName));
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

        if (this.models.containsKey(modelName)) {

            LOGGER.error(String.format("A model named %s has been defined previously.", modelName));
            LOGGER.error(String.format("Error while parsing %s", ctx.getText()));
            System.exit(-1);
        }

        Model pomdp = this.modelParser.visit(ctx);
        ((POMDP) pomdp).name = modelName;

        this.models.put(modelName, pomdp);
        LOGGER.debug(String.format("Parsed POMDP %s", modelName));
        LOGGER.info(String.format("POMDP %s has %s state variables representing a total of %s states",
                    modelName, ((POMDP) pomdp).i_S().size(), 
                    pomdp.i_S().stream()
                    .map(i -> Global.valNames.get(i - 1).size())
                    .reduce(1, (p, q) -> p * q)));

        LOGGER.info(String.format("POMDP %s has %s obs variables representing a total of %s obs",
                    modelName, ((POMDP) pomdp).i_Om().size(), 
                    ((POMDP) pomdp).i_Om().stream()
                    .map(i -> Global.valNames.get(i - 1).size())
                    .reduce(1, (p, q) -> p * q)));

        super.enterPOMDPDef(ctx);
    }

    @Override
    public void enterIPOMDPDef(IPOMDPDefContext ctx) {

        String modelName = ctx.ipomdp_def().model_name().IDENTIFIER().getText();

        if (this.models.containsKey(modelName)) {

            LOGGER.error(String.format("A model named %s has been defined previously.", modelName));
            LOGGER.error(String.format("Error while parsing %s", ctx.getText()));
            System.exit(-1);
        }

        Model ipomdp = this.modelParser.visit(ctx);
        ((IPOMDP) ipomdp).name = modelName;

        this.models.put(modelName, ipomdp);
        LOGGER.debug(String.format("Parsed IPOMDP %s", modelName));

        super.enterIPOMDPDef(ctx);
    }

    @Override
    public void enterPBVISolverDef(PBVISolverDefContext ctx) {

        String name = ctx.pbvi_solv_def().solv_name().IDENTIFIER().getText();

        if (ctx.pbvi_solv_def().POMDP() != null) {

            SymbolicPerseusSolver<POMDP> solver = new SymbolicPerseusSolver<>();

            this.solvers.put(name, solver);
            LOGGER.debug(String.format("Parsed solver %s for POMDPs", name));
        }

        else if (ctx.pbvi_solv_def().IPOMDP() != null) {

            SymbolicPerseusSolver<IPOMDP> solver = new SymbolicPerseusSolver<>();

            this.solvers.put(name, solver);
            LOGGER.debug(String.format("Parsed IPOMDP solver %s", name));
        }

        super.enterPBVISolverDef(ctx);
    }

    @Override
    public void enterSolvExpr(SolvExprContext ctx) {

        String solverName = ctx.solv_cmd().solv_name().getText();
        String modelName = ctx.solv_cmd().model_name().getText();
        int backups = Integer.valueOf(ctx.solv_cmd().backups().getText());
        int expHorizon = Integer.valueOf(ctx.solv_cmd().exp_horizon().getText());
        List<DD> dds = ctx.solv_cmd().dd_list().dd_expr().stream().map(this.ddParser::visit)
            .collect(Collectors.toList());

        String policyName = ctx.policy_name().IDENTIFIER().getText();

        var model = this.models.get(modelName);
        if (model instanceof POMDP) {

            POMDP _model = (POMDP) model;

            if (!this.solvers.containsKey(solverName)) {

                LOGGER.error(String.format("Solver %s for POMDP %s is not defined", solverName, modelName));
                System.exit(-1);
            }

            SymbolicPerseusSolver<POMDP> solver = (SymbolicPerseusSolver<POMDP>) this.solvers.get(solverName);
            var policy = solver.solve(dds, _model, backups, expHorizon, AlphaVectorPolicy.fromR(_model.R()));

            this.policies.put(policyName, policy);
            LOGGER.info(String.format("Solved %s and stored policy %s", modelName, policyName));

        }

        else if (model instanceof IPOMDP) {

            IPOMDP _model = (IPOMDP) model;

            if (!this.solvers.containsKey(solverName)) {

                LOGGER.error(String.format("Solver %s for IPOMDP %s is not defined", solverName, modelName));
                System.exit(-1);
            }

            SymbolicPerseusSolver<IPOMDP> solver = (SymbolicPerseusSolver<IPOMDP>) this.solvers.get(solverName);
            var policy = solver.solve(dds, _model, backups, _model.H, AlphaVectorPolicy.fromR(_model.R()));

            this.policies.put(policyName, policy);
            LOGGER.info(String.format("Solved %s and stored policy %s", modelName, policyName));
        }

        super.enterSolvExpr(ctx);
    }

    @Override
    public void enterPolTreeExpr(PolTreeExprContext ctx) {

        List<DD> dds = ctx.dd_list().dd_expr().stream().map(this.ddParser::visit).collect(Collectors.toList());
        String modelName = ctx.model_name().IDENTIFIER().getText();
        String policyName = ctx.policy_name().IDENTIFIER().getText();
        int expHorizon = Integer.valueOf(ctx.exp_horizon().FLOAT_NUM().getText());

        if (!this.models.containsKey(modelName)) {

            LOGGER.error(String.format(
                        "Model %s not declared anywhere. How will I generate a policy tree for a model that does not even exist?"));
            System.exit(-1);
        }

        if (!(this.models.get(modelName) instanceof PBVISolvablePOMDPBasedModel)) {

            LOGGER.error(String.format("Model %s doesn't look like a POMDP or an IPOMDP"));
            System.exit(-1);
        }

        if (!this.policies.containsKey(policyName)) {

            LOGGER.error(String.format("Policy %s does not exist. Might be a typo."));
            System.exit(-1);
        }

        if (!(this.policies.get(policyName) instanceof AlphaVectorPolicy)) {

            LOGGER.error(String.format("Policy %s does not look like an alpha vector policy", policyName));
            System.exit(-1);
        }

        var _model = (PBVISolvablePOMDPBasedModel) this.models.get(modelName);
        var policy = (AlphaVectorPolicy) this.policies.get(policyName);

        var initNodes = dds.stream()
            .map(d -> ReachabilityNode.getStartNode(policy.getBestActionIndex(d, _model.i_S()), d))
            .collect(Collectors.toList());

        var modelGraph = ModelGraph.fromDecMakingModel(_model);
        var expStrat =  /* new MjSpaceExpansion<>(); */ new PolicyTreeExpansion<>();

        modelGraph = expStrat.expand(initNodes, modelGraph, _model, expHorizon, policy);
        LOGGER.info(String.format("Made policy tree for model %s till time step %s", modelName, expHorizon));

        String fileName = new StringBuilder().append(modelName).append("_").append(policyName).append(".poltree")
            .toString();

        try (PrintWriter out = new PrintWriter(fileName)) {

            var builder = new StringBuilder();
            builder.append("digraph G {\r\n");
            //builder.append("layout=neato;\r\n");
            builder.append("overlap=false;\r\n");
            builder.append("sep=1.0;\r\n");
            builder.append("splines=true;\r\n");
            builder.append(ModelGraph.toDot(modelGraph, _model));
            builder.append("}\r\n");
            out.write(builder.toString());
            LOGGER.info(String.format("Wrote policy tree to %s", fileName));
        }

        catch (Exception e) {

            LOGGER.error(String.format("While writing policy tree for model %s to file", modelName));
            LOGGER.error(String.format("Model graph is %s", modelGraph));
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void enterEnvDef(EnvDefContext ctx) {


        var envName = ctx.env_def().env_name().IDENTIFIER().getText();

        LOGGER.info(String.format("Parsing environment %s", envName));

        var S = ctx.env_def().states_list().var_name().stream().map(s -> s.getText()).collect(Collectors.toList());
        var O = ctx.env_def().obs_list().var_name().stream().map(s -> s.getText()).collect(Collectors.toList());

        DBN dynamics = (DBN) modelParser.visit(ctx.env_def().dbn_def());
        var env = new PartiallyObservableEnv(S, O, dynamics, null);
        env.name = envName;

        envs.put(envName, env);

        LOGGER.info(String.format("Parsed env %s", env));

        super.enterEnvDef(ctx);
    }

    @Override
    public void enterParenExecExpr(ParenExecExprContext ctx) {

        super.enterParenExecExpr(ctx);
    }

    @Override
    public void enterDDExecDef(DDExecDefContext ctx) {

        this.enterDd_def(ctx.dd_def());
        super.enterDDExecDef(ctx);
    }

    private void errorAndExit(String message) {

        LOGGER.error(message);
        System.exit(-1);
    }

    // ----------------------------------------------------------------------------------------

    public HashMap<String, DD> getDDs() {

        return this.dds;
    }

    public Optional<Model> getModel(String modelName) {

        return Optional.ofNullable(this.models.get(modelName));
    }

}
