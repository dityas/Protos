/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.spuddx_parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.models.DBN;
import thinclab.spuddx_parser.SpuddXParser.SumoutExprContext;

/*
 * @author adityas
 *
 */
public class DDParser extends SpuddXBaseVisitor<DD> {

	private HashMap<String, Object> declaredDDs;
	private static final Logger LOGGER = LogManager.getLogger(DDParser.class);

	public DDParser(HashMap<String, Object> env) {

		super();
		this.declaredDDs = env;
	}

	@Override
	public DD visitDDDecl(SpuddXParser.DDDeclContext ctx) {

		// Prepare root DD
		String varName = ctx.var_name().IDENTIFIER().getText();
		int varIndex = Global.varNames.indexOf(varName);
		var valNames = Global.valNames.get(varIndex);

		if (varIndex < 0) {

			LOGGER.error(String.format("Variable %s not found in variables list %s", varName, Global.varNames));
			LOGGER.error(String.format("While parsing %s", ctx.getText()));
			System.exit(-1);
		}

		// Prepare children
		var childNames = ctx.var_value().stream().map(v -> v.IDENTIFIER().getText()).collect(Collectors.toList());
		var childDDList = ctx.dd_expr().stream().map(this::visit).collect(Collectors.toList());
		/*
		 * if (valNames.size() != childNames.size() && valNames.size() !=
		 * childDDList.size()) {
		 * 
		 * LOGGER.error(String.format("All children for %s not defined", varName));
		 * System.exit(-1); }
		 */
		DD[] children = new DD[valNames.size()];
		for (int i = 0; i < children.length; i++)
			children[i] = DDleaf.getDD(0.0f);
		
		for (int i = 0; i < childNames.size(); i++) {

			int childIndex = valNames.indexOf(childNames.get(i));

			if (childIndex < 0 || childDDList.get(i) == null) {

				LOGGER.warn(String.format("Value for child %s not defined. Defaulting to 0", childNames.get(i)));
				children[childIndex] = DDleaf.getDD(0.0f);
				/*
				 * Instead of breaking here, assign zero and move on with your life
				 * LOGGER.error(String.format("Could not parse DD for child %s",
				 * childNames.get(i)));
				 * LOGGER.debug(String.format("Child index is %s, childNames are %s",
				 * childIndex, childNames));
				 * LOGGER.error(String.format("Error while parsing %s", ctx.getText()));
				 * System.exit(-1);
				 */
			}

			else {

				if (i >= childDDList.size() || childIndex >= children.length) {

					LOGGER.error(String.format("Trying to get index %s from childDD list %s", i, childDDList));
					LOGGER.error(String.format("List of next DD's is %s", Arrays.toString(children)));
					LOGGER.error(String.format("Val names are %s", valNames));
					LOGGER.error(String.format("Var names are %s", Global.varNames));
					LOGGER.error(String.format("Var being built %s", varName));
					LOGGER.error(String.format("Child names while parsing %s", childNames));
					LOGGER.error("Error while parsing child DD");
				}

				children[childIndex] = childDDList.get(i);
			}
		}

		//if (childDDList.size() != Global.varDomSize.get(varIndex))
		//	LOGGER.error("Error while parsing DD");

		return DDOP.reorder(DDnode.getDD(varIndex + 1, children));
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
			return (DD) this.declaredDDs.get(ddName);

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

		var varName = ctx.var_name().IDENTIFIER().getText();
		int varIndex = Global.varNames.indexOf(varName);

		if (varIndex < 0) {

			LOGGER.error(
					String.format("Could not find %s in %s while parsing %s", varName, Global.varNames, ctx.getText()));
			System.exit(-1);
		}

		return DDnode.getUniformDist(varIndex + 1);
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
	public DD visitSumoutExpr(SumoutExprContext ctx) {

		var vars = ctx.var_name().stream().map(v -> Global.varNames.indexOf(v.IDENTIFIER().getText()) + 1)
				.collect(Collectors.toList());
		var dd = this.visit(ctx.dd_expr());

		return DDOP.addMultVarElim(List.of(dd), vars);
	}

	@Override
	public DD visitMultDivExpr(SpuddXParser.MultDivExprContext ctx) {

		var left = this.visit(ctx.left);
		var right = this.visit(ctx.right);

		if (ctx.op.getText().contentEquals("*"))
			return DDOP.mult(left, right);

		else if (ctx.op.getText().contentEquals("/"))
			return DDOP.div(left, right);

		else
			return null;
	}

	@Override
	public DD visitAddSubExpr(SpuddXParser.AddSubExprContext ctx) {

		var left = this.visit(ctx.left);
		var right = this.visit(ctx.right);

		if (ctx.op.getText().contentEquals("+"))
			return DDOP.add(left, right);

		else if (ctx.op.getText().contentEquals("-"))
			return DDOP.sub(left, right);

		else
			return null;
	}

	@Override
	public DD visitNegExpr(SpuddXParser.NegExprContext ctx) {

		var term = this.visit(ctx.dd_expr());

		if (ctx.op.getText().contentEquals("+"))
			return term;

		else if (ctx.op.getText().contentEquals("-"))
			return DDOP.neg(term);

		else
			return null;
	}

}
