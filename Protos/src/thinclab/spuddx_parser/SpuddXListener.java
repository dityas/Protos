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