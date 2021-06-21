/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.spuddx_parser;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.RandomVariable;

/*
 * @author adityas
 *
 */
public class VariablesDeclarationVisitor extends SpuddXBaseVisitor<List<RandomVariable>> {

	private static final Logger LOGGER = LogManager.getLogger(VariablesDeclarationVisitor.class);

	private class VarValueVisitor extends SpuddXBaseVisitor<String> {

		@Override
		public String visitVar_value(SpuddXParser.Var_valueContext ctx) {

			return ctx.IDENTIFIER().getText();
		}
	}

	private class RvDeclVisitor extends SpuddXBaseVisitor<RandomVariable> {

		@Override
		public RandomVariable visitRv_decl(SpuddXParser.Rv_declContext ctx) {

			var varValsVisitor = new VarValueVisitor();

			String varName = ctx.variable_name().IDENTIFIER().getText();
			List<String> valNames = ctx.var_value().stream().map(varValsVisitor::visit).collect(Collectors.toList());

			LOGGER.debug(String.format("Parsed RV %s: %s", varName, valNames));

			return new RandomVariable(varName, valNames);
		}
	}

	@Override
	public List<RandomVariable> visitVar_decls(SpuddXParser.Var_declsContext ctx) {

		var rvVisitor = new RvDeclVisitor();

		var vars = ctx.rv_decl().stream().map(rvVisitor::visit).collect(Collectors.toList());

		LOGGER.debug(String.format("Parsed variables %s", vars));

		return vars;
	}

	@Override
	public List<RandomVariable> visitDomain(SpuddXParser.DomainContext ctx) {
		
		var randVars = this.visit(ctx.var_decls());
		return randVars;
	}

}
