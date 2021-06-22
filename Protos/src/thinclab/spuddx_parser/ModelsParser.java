/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.spuddx_parser;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.models.DBN;
import thinclab.models.Model;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class ModelsParser extends SpuddXBaseVisitor<HashMap<String, Model>> {
	
	private HashMap<String, DD> declaredDDs;
	private HashMap<String, Model> declaredModels;
	
	public ModelsParser(HashMap<String, DD> declaredDDs) {
		super();
		this.declaredDDs = declaredDDs;
	}
	
	public ModelsParser(HashMap<String, DD> declDDs, HashMap<String, Model>declModels) {
		super();
		this.declaredDDs = declDDs;
		this.declaredModels = declModels;
	}
	
	@Override
	public HashMap<String, Model> visitPOMDPDef(SpuddXParser.POMDPDefContext ctx) {
		return this.declaredModels;
	}
	
	@Override
	public HashMap<String, Model> visitDBNDef(SpuddXParser.DBNDefContext ctx) {
		
		var ddParser = new DDParser(this.declaredDDs);
		
		String modelName = ctx.dbn_def().dbn_name().getText();
		
		// Parse <varName, DD> tuples from (cpd_def)*
		var cpds = ctx.dbn_def().cpd_def().stream()
				.map(d -> new Tuple<String, DD>(d.variable_name().getText(), 
						ddParser.visit(d.dd_expr())));
		
		int[] vars = cpds.mapToInt(t -> Global.varNames.indexOf(t.first()) + 1).toArray();
		DD[] dds = cpds.map(t -> t.second()).toArray(DD[]::new);
		
		var dbn = new DBN(vars, dds);
		this.declaredModels.put(modelName, dbn);
				
		return this.declaredModels;
	}
	
	@Override
	public HashMap<String, Model> visitDomain(SpuddXParser.DomainContext ctx) {
		return this.visit(ctx);
	}
	
	public Map<String, DBN> getDBNs(SpuddXParser.DomainContext ctx) {
		var dbns = this.visit(ctx).entrySet().stream()
				.filter(e -> e.getValue() instanceof DBN)
				.collect(Collectors.toMap(e -> e.getKey(), e -> (DBN) e.getValue()));
		
		return dbns;
	}
}
