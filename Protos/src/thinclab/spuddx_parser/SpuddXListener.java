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
	 * Enter a parse tree produced by {@link SpuddXParser#state_var_decl}.
	 * @param ctx the parse tree
	 */
	void enterState_var_decl(SpuddXParser.State_var_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#state_var_decl}.
	 * @param ctx the parse tree
	 */
	void exitState_var_decl(SpuddXParser.State_var_declContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#obs_var_decl}.
	 * @param ctx the parse tree
	 */
	void enterObs_var_decl(SpuddXParser.Obs_var_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#obs_var_decl}.
	 * @param ctx the parse tree
	 */
	void exitObs_var_decl(SpuddXParser.Obs_var_declContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#actions_decl}.
	 * @param ctx the parse tree
	 */
	void enterActions_decl(SpuddXParser.Actions_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#actions_decl}.
	 * @param ctx the parse tree
	 */
	void exitActions_decl(SpuddXParser.Actions_declContext ctx);
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
	 * Enter a parse tree produced by {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 */
	void enterDd_decl(SpuddXParser.Dd_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 */
	void exitDd_decl(SpuddXParser.Dd_declContext ctx);
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
	 * Enter a parse tree produced by {@link SpuddXParser#dd_child}.
	 * @param ctx the parse tree
	 */
	void enterDd_child(SpuddXParser.Dd_childContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#dd_child}.
	 * @param ctx the parse tree
	 */
	void exitDd_child(SpuddXParser.Dd_childContext ctx);
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
	 * Enter a parse tree produced by {@link SpuddXParser#model_defs}.
	 * @param ctx the parse tree
	 */
	void enterModel_defs(SpuddXParser.Model_defsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#model_defs}.
	 * @param ctx the parse tree
	 */
	void exitModel_defs(SpuddXParser.Model_defsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#bn_def}.
	 * @param ctx the parse tree
	 */
	void enterBn_def(SpuddXParser.Bn_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#bn_def}.
	 * @param ctx the parse tree
	 */
	void exitBn_def(SpuddXParser.Bn_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddXParser#cpd}.
	 * @param ctx the parse tree
	 */
	void enterCpd(SpuddXParser.CpdContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddXParser#cpd}.
	 * @param ctx the parse tree
	 */
	void exitCpd(SpuddXParser.CpdContext ctx);
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