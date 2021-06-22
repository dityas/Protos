/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models;

import java.util.List;
import thinclab.legacy.Global;

/*
 * @author adityas
 *
 */
public class POMDP implements Model {

	public List<String> S;
	public List<String> O;
	public List<String> A;
	
	public POMDP(List<String> S, List<String> O, String A) {
		
		this.S = S;
		this.O = O;
		this.A = Global.valNames.get(Global.varNames.indexOf(A));
	}
	
	@Override
	public String toString() {
		
		var builder = new StringBuilder();
		builder.append("POMDP: [").append("\r\n");
		builder.append("S : ").append(this.S).append("\r\n");
		builder.append("O : ").append(this.O).append("\r\n");
		builder.append("A : ").append(this.A).append("\r\n");
		builder.append("]").append("\r\n");
		
		return builder.toString();
	}
	
}
