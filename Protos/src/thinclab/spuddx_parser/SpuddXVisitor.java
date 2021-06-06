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
	 * Visit a parse tree produced by {@link SpuddXParser#rv_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRv_decl(SpuddXParser.Rv_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link SpuddXParser#var_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_value(SpuddXParser.Var_valueContext ctx);
}