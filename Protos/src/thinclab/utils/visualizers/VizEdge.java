/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils.visualizers;

/*
 * @author adityas
 *
 */
public class VizEdge {
	/*
	 * Wrapper for edges in the graph visualization
	 */
	
	/* from node and to node */
	public int fromId;
	public int toId;
	
	/* label */
	public String label;
	
	// ----------------------------------------------------------------------------------------
	
	public VizEdge(int fromId, String label, int toId) {
		
		this.fromId = fromId;
		this.toId = toId;
		
		this.label = label;
	}

}
