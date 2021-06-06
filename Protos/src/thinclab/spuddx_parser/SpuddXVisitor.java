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
	 * Visit a parse tree produced by {@link SpuddXParser#state_var_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitState_var_decl(SpuddXParser.State_var_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#obs_var_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObs_var_decl(SpuddXParser.Obs_var_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#actions_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActions_decl(SpuddXParser.Actions_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#dd_decl_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDd_decl_block(SpuddXParser.Dd_decl_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#dd_decls}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDd_decls(SpuddXParser.Dd_declsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#dd_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDd_decl(SpuddXParser.Dd_declContext ctx);
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
	 * Visit a parse tree produced by {@link SpuddXParser#rv_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRv_decl(SpuddXParser.Rv_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#act_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAct_decl(SpuddXParser.Act_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#var_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_value(SpuddXParser.Var_valueContext ctx);
}