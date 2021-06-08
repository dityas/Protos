/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
 * @author adityas
 *
 */
public class RandomVariable {
	
	private String varName;
	private List<String> valNames;
	
	public RandomVariable(String varName, List<String> valNames) {
		this.varName = varName;
		this.valNames = valNames;
	}
	
	public String getVarName() {
		return this.varName;
	}
	
	public List<String> getValNames() {
		return this.valNames;
	}
	
	@Override
	public String toString() {
		
		var builder = new StringBuilder();
		builder.append(this.varName).append(" : ")
			   .append(this.valNames);
			   
		return builder.toString();
	}
	
	public static List<RandomVariable> primeVariables(List<RandomVariable> vars) {
		
		var primedVars = vars.stream()
							 .map(v -> new RandomVariable(
									 v.getVarName() + "'", 
									 v.getValNames()))
							 .collect(Collectors.toList());
		
		var allVars = new ArrayList<RandomVariable>(vars);
		allVars.addAll(primedVars);
		
		return allVars;
	}
 }
