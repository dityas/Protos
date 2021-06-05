// Generated from Spudd.g4 by ANTLR 4.5
package thinclab.spuddx_parser;

	package thinclab.spuddx_parser;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SpuddParser}.
 */
public interface SpuddListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SpuddParser#domain}.
	 * @param ctx the parse tree
	 */
	void enterDomain(SpuddParser.DomainContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddParser#domain}.
	 * @param ctx the parse tree
	 */
	void exitDomain(SpuddParser.DomainContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddParser#state_var_decl}.
	 * @param ctx the parse tree
	 */
	void enterState_var_decl(SpuddParser.State_var_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddParser#state_var_decl}.
	 * @param ctx the parse tree
	 */
	void exitState_var_decl(SpuddParser.State_var_declContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddParser#obs_var_decl}.
	 * @param ctx the parse tree
	 */
	void enterObs_var_decl(SpuddParser.Obs_var_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddParser#obs_var_decl}.
	 * @param ctx the parse tree
	 */
	void exitObs_var_decl(SpuddParser.Obs_var_declContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddParser#rv_decl}.
	 * @param ctx the parse tree
	 */
	void enterRv_decl(SpuddParser.Rv_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddParser#rv_decl}.
	 * @param ctx the parse tree
	 */
	void exitRv_decl(SpuddParser.Rv_declContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddParser#var_values}.
	 * @param ctx the parse tree
	 */
	void enterVar_values(SpuddParser.Var_valuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddParser#var_values}.
	 * @param ctx the parse tree
	 */
	void exitVar_values(SpuddParser.Var_valuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link SpuddParser#var_value}.
	 * @param ctx the parse tree
	 */
	void enterVar_value(SpuddParser.Var_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpuddParser#var_value}.
	 * @param ctx the parse tree
	 */
	void exitVar_value(SpuddParser.Var_valueContext ctx);
}