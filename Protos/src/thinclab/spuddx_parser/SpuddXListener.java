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
	 * Enter a parse tree produced by {@link SpuddXParser#var_decls}.
	 * @param ctx the parse tree
	 */
	void enterVar_decls(SpuddXParser.Var_declsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#var_decls}.
	 * @param ctx the parse tree
	 */
	void exitVar_decls(SpuddXParser.Var_declsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code POMDPDef}
	 * labeled alternative in {@link SpuddXParser#model_decl}.
	 * @param ctx the parse tree
	 */
	void enterPOMDPDef(SpuddXParser.POMDPDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code POMDPDef}
	 * labeled alternative in {@link SpuddXParser#model_decl}.
	 * @param ctx the parse tree
	 */
	void exitPOMDPDef(SpuddXParser.POMDPDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DBNDef}
	 * labeled alternative in {@link SpuddXParser#model_decl}.
	 * @param ctx the parse tree
	 */
	void enterDBNDef(SpuddXParser.DBNDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DBNDef}
	 * labeled alternative in {@link SpuddXParser#model_decl}.
	 * @param ctx the parse tree
	 */
	void exitDBNDef(SpuddXParser.DBNDefContext ctx);
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
	 * Enter a parse tree produced by {@link SpuddXParser#var_list}.
	 * @param ctx the parse tree
	 */
	void enterVar_list(SpuddXParser.Var_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#var_list}.
	 * @param ctx the parse tree
	 */
	void exitVar_list(SpuddXParser.Var_listContext ctx);
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
	 * Enter a parse tree produced by {@link SpuddXParser#agent_name}.
	 * @param ctx the parse tree
	 */
	void enterAgent_name(SpuddXParser.Agent_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#agent_name}.
	 * @param ctx the parse tree
	 */
	void exitAgent_name(SpuddXParser.Agent_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#dd_decls}.
	 * @param ctx the parse tree
	 */
	void enterDd_decls(SpuddXParser.Dd_declsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#dd_decls}.
	 * @param ctx the parse tree
	 */
	void exitDd_decls(SpuddXParser.Dd_declsContext ctx);
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
	 * Enter a parse tree produced by {@link SpuddXParser#rv_decl}.
	 * @param ctx the parse tree
	 */
	void enterRv_decl(SpuddXParser.Rv_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#rv_decl}.
	 * @param ctx the parse tree
	 */
	void exitRv_decl(SpuddXParser.Rv_declContext ctx);
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
	 * Enter a parse tree produced by {@link SpuddXParser#dbn_name}.
	 * @param ctx the parse tree
	 */
	void enterDbn_name(SpuddXParser.Dbn_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#dbn_name}.
	 * @param ctx the parse tree
	 */
	void exitDbn_name(SpuddXParser.Dbn_nameContext ctx);
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
	 * Enter a parse tree produced by {@link SpuddXParser#variable_name}.
	 * @param ctx the parse tree
	 */
	void enterVariable_name(SpuddXParser.Variable_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#variable_name}.
	 * @param ctx the parse tree
	 */
	void exitVariable_name(SpuddXParser.Variable_nameContext ctx);
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
}