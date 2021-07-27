/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.spuddx_parser;

import java.util.HashMap;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.models.DBN;

/*
 * @author adityas
 *
 */
public class DDParser extends SpuddXBaseVisitor<DD> {

	private HashMap<String, DD> declaredDDs;
	private static final Logger LOGGER = LogManager.getLogger(DDParser.class); 

	public DDParser(HashMap<String, DD> declaredDDs) {

		super();
		this.declaredDDs = declaredDDs;
	}

	@Override
	public DD visitDDDecl(SpuddXParser.DDDeclContext ctx) {

		// Prepare root DD
		String varName = ctx.var_name().IDENTIFIER().getText();
		int varIndex = Global.varNames.indexOf(varName);
		var valNames = Global.valNames.get(varIndex);

		// Prepare children
		var childNames = ctx.var_value().stream().map(v -> v.IDENTIFIER().getText()).collect(Collectors.toList()); 
		var childDDList = ctx.dd_expr().stream().map(this::visit).collect(Collectors.toList());
		
		if (valNames.size() != childNames.size() &&
				valNames.size() != childDDList.size()) {
			LOGGER.error(String.format("All children for %s not defined", varName));
			System.exit(-1);
		}

		DD[] children = new DD[childDDList.size()];
		for (int i = 0; i < childNames.size(); i++) {

			int childIndex = valNames.indexOf(childNames.get(i));

			if (childIndex < 0 || childDDList.get(i) == null) {

				LOGGER.error("Could not parse DD for child " + childNames.get(i));
				LOGGER.debug(String.format("Child index is %s, childNames are %s", childIndex, childNames));
				LOGGER.debug(String.format("Child DDs are %s", childDDList));
				LOGGER.debug(String.format("Parsed DDs are %s", this.declaredDDs));
				System.exit(-1);
			}

			children[childIndex] = childDDList.get(childIndex);
		}

		if (childDDList.size() != Global.varDomSize.get(varIndex))
			LOGGER.error("Error while parsing DD");

		return OP.reorder(DDnode.getDD(varIndex + 1, children));
	}

	@Override
	public DD visitDDleaf(SpuddXParser.DDleafContext ctx) {

		return DDleaf.getDD(Float.valueOf(ctx.dd_leaf().FLOAT_NUM().getText()));
	}

	@Override
	public DD visitSameDD(SpuddXParser.SameDDContext ctx) {

		return DBN.getSameTransitionDD(ctx.same_dd_decl().var_name().getText());
	}

	@Override
	public DD visitDDRef(SpuddXParser.DDRefContext ctx) {

		String ddName = ctx.dd_ref().dd_name().IDENTIFIER().getText();

		if (this.declaredDDs.containsKey(ddName))
			return this.declaredDDs.get(ddName);

		else {

			LOGGER.error(String.format("DD named %s not defined.", ddName));
			System.exit(-1);
			return null;
		}
	}
	
	@Override
	public DD visitDDDeterministic(SpuddXParser.DDDeterministicContext ctx) {
		return DDnode.getDDForChild(ctx.var_name().getText(), ctx.var_value().getText());
	}
	
	@Override
	public DD visitDDUniform(SpuddXParser.DDUniformContext ctx) {
		return DDnode.getUniformDist(ctx.var_name().getText());
	}

	@Override
	public DD visitAtomicExpr(SpuddXParser.AtomicExprContext ctx) {

		return this.visit(ctx.dd_decl());
	}

	@Override
	public DD visitParenExpr(SpuddXParser.ParenExprContext ctx) {

		return this.visit(ctx.dd_expr());
	}

	@Override
	public DD visitMultDivExpr(SpuddXParser.MultDivExprContext ctx) {

		var left = this.visit(ctx.left);
		var right = this.visit(ctx.right);

		if (ctx.op.getText().contentEquals("*"))
			return OP.mult(left, right);

		else if (ctx.op.getText().contentEquals("/"))
			return OP.div(left, right);

		else
			return null;
	}

	@Override
	public DD visitAddSubExpr(SpuddXParser.AddSubExprContext ctx) {

		var left = this.visit(ctx.left);
		var right = this.visit(ctx.right);

		if (ctx.op.getText().contentEquals("+"))
			return OP.add(left, right);

		else if (ctx.op.getText().contentEquals("-"))
			return OP.sub(left, right);

		else
			return null;
	}

	@Override
	public DD visitNegExpr(SpuddXParser.NegExprContext ctx) {

		var term = this.visit(ctx.dd_expr());

		if (ctx.op.getText().contentEquals("+"))
			return term;

		else if (ctx.op.getText().contentEquals("-"))
			return OP.neg(term);

		else
			return null;
	}
	
}
