/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.spuddx_parser;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.RandomVariable;

/*
 * @author adityas
 *
 */
public class SpuddXParserWrapper {

	/*
	 * Wrapper for SPUDDX wrapper generated by ANTLR4. Implements the visitor
	 * methods for each statement.
	 */

	private String fileName;
	private SpuddXParser parser;
	
	private static final Logger LOGGER = 
			LogManager.getLogger(SpuddXParserWrapper.class);

	// -------------------------------------------------------------------------
	// Implementation of ANTLR4 visitors

	private class VarValueVisitor extends SpuddXBaseVisitor<String> {

		@Override
		public String visitVar_value(SpuddXParser.Var_valueContext ctx) {
			return ctx.VARVAL().getText();
		}
	}
		
	private class RvDeclVisitor extends SpuddXBaseVisitor<RandomVariable> {
		
		@Override
		public RandomVariable visitRv_decl(SpuddXParser.Rv_declContext ctx) {
			
			var varValsVisitor = new VarValueVisitor();
			
			String varName = ctx.VARNAME().toString();
			
			List<String> valNames = ctx.var_value().stream()
												   .map(varValsVisitor::visit)
												   .collect(Collectors.toList());
			
			LOGGER.debug(String.format("Parsed RV %s: %s", varName, valNames));
			
			return new RandomVariable(varName, valNames);
		}
	}
	
	private class StateVarDeclVisitor extends SpuddXBaseVisitor<List<RandomVariable>> {
		
		@Override
		public List<RandomVariable> visitState_var_decl(SpuddXParser.State_var_declContext ctx) {
			
			var rvVisitor = new RvDeclVisitor();
			
			var stateVars = ctx.rv_decl().stream()
										 .map(rvVisitor::visit)
										 .collect(Collectors.toList());
			
			LOGGER.debug(String.format("Parsed state variables %s", stateVars));
			
			return stateVars;
		}
	}
	
	private class ObsVarDeclVisitor extends SpuddXBaseVisitor<List<RandomVariable>> {
		
		@Override
		public List<RandomVariable> visitObs_var_decl(SpuddXParser.Obs_var_declContext ctx) {
			
			var rvVisitor = new RvDeclVisitor();
			
			var obsVars = ctx.rv_decl().stream()
									   .map(rvVisitor::visit)
									   .collect(Collectors.toList());
			
			LOGGER.debug(String.format("Parsed obs variables %s", obsVars));
			
			return obsVars;
		}
	}
	
	private class DomainVisitor extends SpuddXBaseVisitor<List<RandomVariable>> {
		
		@Override
		public List<RandomVariable> visitDomain(SpuddXParser.DomainContext ctx) {
			
			var sVars = new StateVarDeclVisitor().visit(ctx.state_var_decl());
			var oVars = new ObsVarDeclVisitor().visit(ctx.obs_var_decl());
			
			sVars.addAll(oVars);
			
			return sVars;
		}
	}

	// -------------------------------------------------------------------------

	public SpuddXParserWrapper(String fileName) {
		
		this.fileName = fileName;
		
		try {

			// Get tokens from lexer
			InputStream is = new FileInputStream(this.fileName);
			ANTLRInputStream antlrIs = new ANTLRInputStream(is);
			SpuddXLexer lexer = new SpuddXLexer(antlrIs);
			TokenStream tokens = new CommonTokenStream(lexer);
			
			this.parser = new SpuddXParser(tokens);
			
			var domainVisitor = new DomainVisitor();
			var vars = domainVisitor.visit(this.parser.domain());
			
		}

		catch (Exception e) {
			LOGGER.error(String.format(
					"Error while trying to parse %s: %s", this.fileName, e));
			System.exit(-1);
		}
	}
}
