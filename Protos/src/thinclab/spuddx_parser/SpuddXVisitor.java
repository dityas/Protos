// Generated from SpuddX.g4 by ANTLR 4.5
package thinclab.spuddx_parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SpuddXParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SpuddXVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#domain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDomain(SpuddXParser.DomainContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#var_defs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_defs(SpuddXParser.Var_defsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#var_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_def(SpuddXParser.Var_defContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PreDefModel}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreDefModel(SpuddXParser.PreDefModelContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DDDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDDDef(SpuddXParser.DDDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code POMDPDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPOMDPDef(SpuddXParser.POMDPDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IPOMDPDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIPOMDPDef(SpuddXParser.IPOMDPDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EnvDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnvDef(SpuddXParser.EnvDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DBNDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDBNDef(SpuddXParser.DBNDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PBVISolverDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPBVISolverDef(SpuddXParser.PBVISolverDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ModelVarInitDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelVarInitDef(SpuddXParser.ModelVarInitDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OtherDefParen}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOtherDefParen(SpuddXParser.OtherDefParenContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#pbvi_solv_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPbvi_solv_def(SpuddXParser.Pbvi_solv_defContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#pomdp_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPomdp_def(SpuddXParser.Pomdp_defContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#ipomdp_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIpomdp_def(SpuddXParser.Ipomdp_defContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#env_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnv_def(SpuddXParser.Env_defContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#modelvar_init_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelvar_init_def(SpuddXParser.Modelvar_init_defContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#model_init}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModel_init(SpuddXParser.Model_initContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#states_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStates_list(SpuddXParser.States_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#obs_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObs_list(SpuddXParser.Obs_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#action_var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_var(SpuddXParser.Action_varContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#action_j_var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_j_var(SpuddXParser.Action_j_varContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#model_j_var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModel_j_var(SpuddXParser.Model_j_varContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#ec_var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEc_var(SpuddXParser.Ec_varContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#actions_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActions_list(SpuddXParser.Actions_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#frame_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFrame_def(SpuddXParser.Frame_defContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#frame_tuple}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFrame_tuple(SpuddXParser.Frame_tupleContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#frame_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFrame_name(SpuddXParser.Frame_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#dynamics}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDynamics(SpuddXParser.DynamicsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#action_model}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_model(SpuddXParser.Action_modelContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#initial_belief}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitial_belief(SpuddXParser.Initial_beliefContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#reward}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReward(SpuddXParser.RewardContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#action_reward}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_reward(SpuddXParser.Action_rewardContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#discount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDiscount(SpuddXParser.DiscountContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#reachability}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReachability(SpuddXParser.ReachabilityContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#dd_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDd_def(SpuddXParser.Dd_defContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MultDivExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultDivExpr(SpuddXParser.MultDivExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AtomicExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomicExpr(SpuddXParser.AtomicExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NegExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegExpr(SpuddXParser.NegExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpr(SpuddXParser.ParenExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SumoutExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSumoutExpr(SpuddXParser.SumoutExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AddSubExpr}
	 * labeled alternative in {@link SpuddXParser#dd_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddSubExpr(SpuddXParser.AddSubExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DDDecl}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDDDecl(SpuddXParser.DDDeclContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DDleaf}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDDleaf(SpuddXParser.DDleafContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SameDD}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSameDD(SpuddXParser.SameDDContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DDRef}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDDRef(SpuddXParser.DDRefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DDDeterministic}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDDDeterministic(SpuddXParser.DDDeterministicContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DDUniform}
	 * labeled alternative in {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDDUniform(SpuddXParser.DDUniformContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#dd_ref}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDd_ref(SpuddXParser.Dd_refContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#dd_leaf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDd_leaf(SpuddXParser.Dd_leafContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#same_dd_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSame_dd_decl(SpuddXParser.Same_dd_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#dbn_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDbn_def(SpuddXParser.Dbn_defContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#cpd_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCpd_def(SpuddXParser.Cpd_defContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#exec_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExec_block(SpuddXParser.Exec_blockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DDExecDef}
	 * labeled alternative in {@link SpuddXParser#exec_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDDExecDef(SpuddXParser.DDExecDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SolvExpr}
	 * labeled alternative in {@link SpuddXParser#exec_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSolvExpr(SpuddXParser.SolvExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PolTreeExpr}
	 * labeled alternative in {@link SpuddXParser#exec_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPolTreeExpr(SpuddXParser.PolTreeExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenExecExpr}
	 * labeled alternative in {@link SpuddXParser#exec_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExecExpr(SpuddXParser.ParenExecExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#solv_cmd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSolv_cmd(SpuddXParser.Solv_cmdContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#dd_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDd_list(SpuddXParser.Dd_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#backups}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBackups(SpuddXParser.BackupsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#exp_horizon}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp_horizon(SpuddXParser.Exp_horizonContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#env_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnv_name(SpuddXParser.Env_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#policy_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPolicy_name(SpuddXParser.Policy_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#action_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_name(SpuddXParser.Action_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#model_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModel_name(SpuddXParser.Model_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#dd_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDd_name(SpuddXParser.Dd_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#var_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_name(SpuddXParser.Var_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#var_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_value(SpuddXParser.Var_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#solv_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSolv_name(SpuddXParser.Solv_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#pol_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPol_name(SpuddXParser.Pol_nameContext ctx);
}