// Generated from SpuddX.g4 by ANTLR 4.5
package thinclab.spuddx_parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SpuddXParser}.
 */
public interface SpuddXListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#domain}.
	 * @param ctx the parse tree
	 */
	void enterDomain(SpuddXParser.DomainContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#domain}.
	 * @param ctx the parse tree
	 */
	void exitDomain(SpuddXParser.DomainContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#var_defs}.
	 * @param ctx the parse tree
	 */
	void enterVar_defs(SpuddXParser.Var_defsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#var_defs}.
	 * @param ctx the parse tree
	 */
	void exitVar_defs(SpuddXParser.Var_defsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#var_def}.
	 * @param ctx the parse tree
	 */
	void enterVar_def(SpuddXParser.Var_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#var_def}.
	 * @param ctx the parse tree
	 */
	void exitVar_def(SpuddXParser.Var_defContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PreDefModel}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void enterPreDefModel(SpuddXParser.PreDefModelContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PreDefModel}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void exitPreDefModel(SpuddXParser.PreDefModelContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DDDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void enterDDDef(SpuddXParser.DDDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DDDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void exitDDDef(SpuddXParser.DDDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code POMDPDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void enterPOMDPDef(SpuddXParser.POMDPDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code POMDPDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void exitPOMDPDef(SpuddXParser.POMDPDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IPOMDPDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void enterIPOMDPDef(SpuddXParser.IPOMDPDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IPOMDPDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void exitIPOMDPDef(SpuddXParser.IPOMDPDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EnvDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void enterEnvDef(SpuddXParser.EnvDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EnvDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void exitEnvDef(SpuddXParser.EnvDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DBNDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void enterDBNDef(SpuddXParser.DBNDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DBNDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void exitDBNDef(SpuddXParser.DBNDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PBVISolverDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void enterPBVISolverDef(SpuddXParser.PBVISolverDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PBVISolverDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void exitPBVISolverDef(SpuddXParser.PBVISolverDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ModelVarInitDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void enterModelVarInitDef(SpuddXParser.ModelVarInitDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ModelVarInitDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void exitModelVarInitDef(SpuddXParser.ModelVarInitDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OtherDefParen}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void enterOtherDefParen(SpuddXParser.OtherDefParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OtherDefParen}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 */
	void exitOtherDefParen(SpuddXParser.OtherDefParenContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#pbvi_solv_def}.
	 * @param ctx the parse tree
	 */
	void enterPbvi_solv_def(SpuddXParser.Pbvi_solv_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#pbvi_solv_def}.
	 * @param ctx the parse tree
	 */
	void exitPbvi_solv_def(SpuddXParser.Pbvi_solv_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#pomdp_def}.
	 * @param ctx the parse tree
	 */
	void enterPomdp_def(SpuddXParser.Pomdp_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#pomdp_def}.
	 * @param ctx the parse tree
	 */
	void exitPomdp_def(SpuddXParser.Pomdp_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#ipomdp_def}.
	 * @param ctx the parse tree
	 */
	void enterIpomdp_def(SpuddXParser.Ipomdp_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#ipomdp_def}.
	 * @param ctx the parse tree
	 */
	void exitIpomdp_def(SpuddXParser.Ipomdp_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#env_def}.
	 * @param ctx the parse tree
	 */
	void enterEnv_def(SpuddXParser.Env_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#env_def}.
	 * @param ctx the parse tree
	 */
	void exitEnv_def(SpuddXParser.Env_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#modelvar_init_def}.
	 * @param ctx the parse tree
	 */
	void enterModelvar_init_def(SpuddXParser.Modelvar_init_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#modelvar_init_def}.
	 * @param ctx the parse tree
	 */
	void exitModelvar_init_def(SpuddXParser.Modelvar_init_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#model_init}.
	 * @param ctx the parse tree
	 */
	void enterModel_init(SpuddXParser.Model_initContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#model_init}.
	 * @param ctx the parse tree
	 */
	void exitModel_init(SpuddXParser.Model_initContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#states_list}.
	 * @param ctx the parse tree
	 */
	void enterStates_list(SpuddXParser.States_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#states_list}.
	 * @param ctx the parse tree
	 */
	void exitStates_list(SpuddXParser.States_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#obs_list}.
	 * @param ctx the parse tree
	 */
	void enterObs_list(SpuddXParser.Obs_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#obs_list}.
	 * @param ctx the parse tree
	 */
	void exitObs_list(SpuddXParser.Obs_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#action_var}.
	 * @param ctx the parse tree
	 */
	void enterAction_var(SpuddXParser.Action_varContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#action_var}.
	 * @param ctx the parse tree
	 */
	void exitAction_var(SpuddXParser.Action_varContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#action_j_var}.
	 * @param ctx the parse tree
	 */
	void enterAction_j_var(SpuddXParser.Action_j_varContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#action_j_var}.
	 * @param ctx the parse tree
	 */
	void exitAction_j_var(SpuddXParser.Action_j_varContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#model_j_var}.
	 * @param ctx the parse tree
	 */
	void enterModel_j_var(SpuddXParser.Model_j_varContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#model_j_var}.
	 * @param ctx the parse tree
	 */
	void exitModel_j_var(SpuddXParser.Model_j_varContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#ec_var}.
	 * @param ctx the parse tree
	 */
	void enterEc_var(SpuddXParser.Ec_varContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#ec_var}.
	 * @param ctx the parse tree
	 */
	void exitEc_var(SpuddXParser.Ec_varContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#actions_list}.
	 * @param ctx the parse tree
	 */
	void enterActions_list(SpuddXParser.Actions_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#actions_list}.
	 * @param ctx the parse tree
	 */
	void exitActions_list(SpuddXParser.Actions_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#frame_def}.
	 * @param ctx the parse tree
	 */
	void enterFrame_def(SpuddXParser.Frame_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#frame_def}.
	 * @param ctx the parse tree
	 */
	void exitFrame_def(SpuddXParser.Frame_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#frame_tuple}.
	 * @param ctx the parse tree
	 */
	void enterFrame_tuple(SpuddXParser.Frame_tupleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#frame_tuple}.
	 * @param ctx the parse tree
	 */
	void exitFrame_tuple(SpuddXParser.Frame_tupleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#frame_name}.
	 * @param ctx the parse tree
	 */
	void enterFrame_name(SpuddXParser.Frame_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#frame_name}.
	 * @param ctx the parse tree
	 */
	void exitFrame_name(SpuddXParser.Frame_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#dynamics}.
	 * @param ctx the parse tree
	 */
	void enterDynamics(SpuddXParser.DynamicsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#dynamics}.
	 * @param ctx the parse tree
	 */
	void exitDynamics(SpuddXParser.DynamicsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#action_model}.
	 * @param ctx the parse tree
	 */
	void enterAction_model(SpuddXParser.Action_modelContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#action_model}.
	 * @param ctx the parse tree
	 */
	void exitAction_model(SpuddXParser.Action_modelContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#initial_belief}.
	 * @param ctx the parse tree
	 */
	void enterInitial_belief(SpuddXParser.Initial_beliefContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#initial_belief}.
	 * @param ctx the parse tree
	 */
	void exitInitial_belief(SpuddXParser.Initial_beliefContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#reward}.
	 * @param ctx the parse tree
	 */
	void enterReward(SpuddXParser.RewardContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#reward}.
	 * @param ctx the parse tree
	 */
	void exitReward(SpuddXParser.RewardContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#action_reward}.
	 * @param ctx the parse tree
	 */
	void enterAction_reward(SpuddXParser.Action_rewardContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#action_reward}.
	 * @param ctx the parse tree
	 */
	void exitAction_reward(SpuddXParser.Action_rewardContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#discount}.
	 * @param ctx the parse tree
	 */
	void enterDiscount(SpuddXParser.DiscountContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#discount}.
	 * @param ctx the parse tree
	 */
	void exitDiscount(SpuddXParser.DiscountContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#reachability}.
	 * @param ctx the parse tree
	 */
	void enterReachability(SpuddXParser.ReachabilityContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#reachability}.
	 * @param ctx the parse tree
	 */
	void exitReachability(SpuddXParser.ReachabilityContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#dd_def}.
	 * @param ctx the parse tree
	 */
	void enterDd_def(SpuddXParser.Dd_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#dd_def}.
	 * @param ctx the parse tree
	 */
	void exitDd_def(SpuddXParser.Dd_defContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MultDivExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 */
	void enterMultDivExpr(SpuddXParser.MultDivExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MultDivExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 */
	void exitMultDivExpr(SpuddXParser.MultDivExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AtomicExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 */
	void enterAtomicExpr(SpuddXParser.AtomicExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AtomicExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 */
	void exitAtomicExpr(SpuddXParser.AtomicExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NegExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 */
	void enterNegExpr(SpuddXParser.NegExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NegExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 */
	void exitNegExpr(SpuddXParser.NegExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 */
	void enterParenExpr(SpuddXParser.ParenExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 */
	void exitParenExpr(SpuddXParser.ParenExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SumoutExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 */
	void enterSumoutExpr(SpuddXParser.SumoutExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SumoutExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 */
	void exitSumoutExpr(SpuddXParser.SumoutExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddSubExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 */
	void enterAddSubExpr(SpuddXParser.AddSubExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddSubExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 */
	void exitAddSubExpr(SpuddXParser.AddSubExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DDDecl}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 */
	void enterDDDecl(SpuddXParser.DDDeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DDDecl}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 */
	void exitDDDecl(SpuddXParser.DDDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DDleaf}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 */
	void enterDDleaf(SpuddXParser.DDleafContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DDleaf}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 */
	void exitDDleaf(SpuddXParser.DDleafContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SameDD}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 */
	void enterSameDD(SpuddXParser.SameDDContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SameDD}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 */
	void exitSameDD(SpuddXParser.SameDDContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DDRef}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 */
	void enterDDRef(SpuddXParser.DDRefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DDRef}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 */
	void exitDDRef(SpuddXParser.DDRefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DDDeterministic}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 */
	void enterDDDeterministic(SpuddXParser.DDDeterministicContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DDDeterministic}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 */
	void exitDDDeterministic(SpuddXParser.DDDeterministicContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DDUniform}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 */
	void enterDDUniform(SpuddXParser.DDUniformContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DDUniform}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 */
	void exitDDUniform(SpuddXParser.DDUniformContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#dd_ref}.
	 * @param ctx the parse tree
	 */
	void enterDd_ref(SpuddXParser.Dd_refContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#dd_ref}.
	 * @param ctx the parse tree
	 */
	void exitDd_ref(SpuddXParser.Dd_refContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#dd_leaf}.
	 * @param ctx the parse tree
	 */
	void enterDd_leaf(SpuddXParser.Dd_leafContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#dd_leaf}.
	 * @param ctx the parse tree
	 */
	void exitDd_leaf(SpuddXParser.Dd_leafContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#same_dd_decl}.
	 * @param ctx the parse tree
	 */
	void enterSame_dd_decl(SpuddXParser.Same_dd_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#same_dd_decl}.
	 * @param ctx the parse tree
	 */
	void exitSame_dd_decl(SpuddXParser.Same_dd_declContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#dbn_def}.
	 * @param ctx the parse tree
	 */
	void enterDbn_def(SpuddXParser.Dbn_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#dbn_def}.
	 * @param ctx the parse tree
	 */
	void exitDbn_def(SpuddXParser.Dbn_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#cpd_def}.
	 * @param ctx the parse tree
	 */
	void enterCpd_def(SpuddXParser.Cpd_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#cpd_def}.
	 * @param ctx the parse tree
	 */
	void exitCpd_def(SpuddXParser.Cpd_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#exec_block}.
	 * @param ctx the parse tree
	 */
	void enterExec_block(SpuddXParser.Exec_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#exec_block}.
	 * @param ctx the parse tree
	 */
	void exitExec_block(SpuddXParser.Exec_blockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DDExecDef}
	 * labeled alternative in {@link SpuddXParser#exec_expr}.
	 * @param ctx the parse tree
	 */
	void enterDDExecDef(SpuddXParser.DDExecDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DDExecDef}
	 * labeled alternative in {@link SpuddXParser#exec_expr}.
	 * @param ctx the parse tree
	 */
	void exitDDExecDef(SpuddXParser.DDExecDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SolvExpr}
	 * labeled alternative in {@link SpuddXParser#exec_expr}.
	 * @param ctx the parse tree
	 */
	void enterSolvExpr(SpuddXParser.SolvExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SolvExpr}
	 * labeled alternative in {@link SpuddXParser#exec_expr}.
	 * @param ctx the parse tree
	 */
	void exitSolvExpr(SpuddXParser.SolvExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PolTreeExpr}
	 * labeled alternative in {@link SpuddXParser#exec_expr}.
	 * @param ctx the parse tree
	 */
	void enterPolTreeExpr(SpuddXParser.PolTreeExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PolTreeExpr}
	 * labeled alternative in {@link SpuddXParser#exec_expr}.
	 * @param ctx the parse tree
	 */
	void exitPolTreeExpr(SpuddXParser.PolTreeExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenExecExpr}
	 * labeled alternative in {@link SpuddXParser#exec_expr}.
	 * @param ctx the parse tree
	 */
	void enterParenExecExpr(SpuddXParser.ParenExecExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenExecExpr}
	 * labeled alternative in {@link SpuddXParser#exec_expr}.
	 * @param ctx the parse tree
	 */
	void exitParenExecExpr(SpuddXParser.ParenExecExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#solv_cmd}.
	 * @param ctx the parse tree
	 */
	void enterSolv_cmd(SpuddXParser.Solv_cmdContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#solv_cmd}.
	 * @param ctx the parse tree
	 */
	void exitSolv_cmd(SpuddXParser.Solv_cmdContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#dd_list}.
	 * @param ctx the parse tree
	 */
	void enterDd_list(SpuddXParser.Dd_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#dd_list}.
	 * @param ctx the parse tree
	 */
	void exitDd_list(SpuddXParser.Dd_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#backups}.
	 * @param ctx the parse tree
	 */
	void enterBackups(SpuddXParser.BackupsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#backups}.
	 * @param ctx the parse tree
	 */
	void exitBackups(SpuddXParser.BackupsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#exp_horizon}.
	 * @param ctx the parse tree
	 */
	void enterExp_horizon(SpuddXParser.Exp_horizonContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#exp_horizon}.
	 * @param ctx the parse tree
	 */
	void exitExp_horizon(SpuddXParser.Exp_horizonContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#env_name}.
	 * @param ctx the parse tree
	 */
	void enterEnv_name(SpuddXParser.Env_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#env_name}.
	 * @param ctx the parse tree
	 */
	void exitEnv_name(SpuddXParser.Env_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#policy_name}.
	 * @param ctx the parse tree
	 */
	void enterPolicy_name(SpuddXParser.Policy_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#policy_name}.
	 * @param ctx the parse tree
	 */
	void exitPolicy_name(SpuddXParser.Policy_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#action_name}.
	 * @param ctx the parse tree
	 */
	void enterAction_name(SpuddXParser.Action_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#action_name}.
	 * @param ctx the parse tree
	 */
	void exitAction_name(SpuddXParser.Action_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#model_name}.
	 * @param ctx the parse tree
	 */
	void enterModel_name(SpuddXParser.Model_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#model_name}.
	 * @param ctx the parse tree
	 */
	void exitModel_name(SpuddXParser.Model_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#dd_name}.
	 * @param ctx the parse tree
	 */
	void enterDd_name(SpuddXParser.Dd_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#dd_name}.
	 * @param ctx the parse tree
	 */
	void exitDd_name(SpuddXParser.Dd_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#var_name}.
	 * @param ctx the parse tree
	 */
	void enterVar_name(SpuddXParser.Var_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#var_name}.
	 * @param ctx the parse tree
	 */
	void exitVar_name(SpuddXParser.Var_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#var_value}.
	 * @param ctx the parse tree
	 */
	void enterVar_value(SpuddXParser.Var_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#var_value}.
	 * @param ctx the parse tree
	 */
	void exitVar_value(SpuddXParser.Var_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#solv_name}.
	 * @param ctx the parse tree
	 */
	void enterSolv_name(SpuddXParser.Solv_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#solv_name}.
	 * @param ctx the parse tree
	 */
	void exitSolv_name(SpuddXParser.Solv_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#pol_name}.
	 * @param ctx the parse tree
	 */
	void enterPol_name(SpuddXParser.Pol_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#pol_name}.
	 * @param ctx the parse tree
	 */
	void exitPol_name(SpuddXParser.Pol_nameContext ctx);
}