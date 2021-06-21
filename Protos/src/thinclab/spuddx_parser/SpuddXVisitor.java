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
	 * Visit a parse tree produced by {@link SpuddXParser#var_decls}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_decls(SpuddXParser.Var_declsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#model_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModel_decl(SpuddXParser.Model_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#pomdp_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPomdp_decl(SpuddXParser.Pomdp_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#var_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_list(SpuddXParser.Var_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#actions_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActions_list(SpuddXParser.Actions_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#agent_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAgent_name(SpuddXParser.Agent_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#dd_decls}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDd_decls(SpuddXParser.Dd_declsContext ctx);
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
	 * Visit a parse tree produced by {@link SpuddXParser#dd_ref}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDd_ref(SpuddXParser.Dd_refContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#dd_child}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDd_child(SpuddXParser.Dd_childContext ctx);
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
	 * Visit a parse tree produced by {@link SpuddXParser#rv_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRv_decl(SpuddXParser.Rv_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#env_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnv_def(SpuddXParser.Env_defContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#actiondbn_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActiondbn_def(SpuddXParser.Actiondbn_defContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#actions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActions(SpuddXParser.ActionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#cpd_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCpd_def(SpuddXParser.Cpd_defContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#dd_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDd_name(SpuddXParser.Dd_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#variable_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_name(SpuddXParser.Variable_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#var_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_value(SpuddXParser.Var_valueContext ctx);
}