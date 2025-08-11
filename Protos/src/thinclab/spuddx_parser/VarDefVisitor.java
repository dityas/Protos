/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.spuddx_parser;

import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.RandomVariable;

/*
 * @author adityas
 *
 */
public class VarDefVisitor extends SpuddXBaseVisitor<RandomVariable> {

	private static final Logger LOGGER = LogManager.getLogger(VarDefVisitor.class);

	@Override
	public RandomVariable visitVar_def(SpuddXParser.Var_defContext ctx) {

		var name = ctx.var_name().IDENTIFIER().getText();
		var vals = ctx.var_value().stream().map(v -> v.IDENTIFIER().getText()).collect(Collectors.toList());

		var rv = new RandomVariable(name, vals);

		LOGGER.debug(String.format("Parsed variable %s", rv));

		return rv;
	}
/*	
	@Override
	public RandomVariable visitModelVarDef(SpuddXParser.ModelVarDefContext ctx) {
		
		var name = ctx.var_name().IDENTIFIER().getText();
		var rv = new RandomVariable(name, new ArrayList<String>(0));

		LOGGER.debug(String.format("Parsed model variable %s", rv));

		return rv;
	}
*/	
}
