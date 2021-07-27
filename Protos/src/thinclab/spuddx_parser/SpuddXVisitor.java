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
	 * Visit a parse tree produced by the {@code DBNDef}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDBNDef(SpuddXParser.DBNDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OtherDefParen}
	 * labeled alternative in {@link SpuddXParser#all_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOtherDefParen(SpuddXParser.OtherDefParenContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#pomdp_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPomdp_def(SpuddXParser.Pomdp_defContext ctx);
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
	 * Visit a parse tree produced by {@link SpuddXParser#actions_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActions_list(SpuddXParser.Actions_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#dynamics}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDynamics(SpuddXParser.DynamicsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ActionRef}
	 * labeled alternative in {@link SpuddXParser#action_model}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionRef(SpuddXParser.ActionRefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ActionDef}
	 * labeled alternative in {@link SpuddXParser#action_model}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionDef(SpuddXParser.ActionDefContext ctx);
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
	 * Visit a parse tree produced by {@link SpuddXParser#env_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnv_name(SpuddXParser.Env_nameContext ctx);
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
}