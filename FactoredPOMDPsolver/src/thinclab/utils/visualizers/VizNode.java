/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils.visualizers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/*
 * @author adityas
 *
 */
public class VizNode {
	/*
	 * Wrapper for visualizer graph nodes
	 */
	
	/* unique ID for the node */
	public int nodeId;
	
	/* data that the node displays in the visualizer */
	public String data;
	
	/* height and width for the JUNG node display */
	public int height;
	public int width;
	
	// --------------------------------------------------------------------------------------
	
	public VizNode(int id, String data) {
		/*
		 * Generic constructor to set id and data at node creation
		 */
		this.nodeId = id;
		this.data = data;
		
		/* set height */
		this.height = (int) this.data.chars().filter(c -> c == '<').count();
		
		if (this.height == 0) this.height = 1;
		
		/* set width */
		List<String> arrays = Arrays.asList(this.data.split("<br>"));
		
		this.width = 
				arrays.stream()
					.map(i -> i.length())
					.mapToInt(j -> j)
					.max()
					.orElse(this.data.length());
	}
	
	// --------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "VizNode [" + this.nodeId + " | " + this.data; 
	}
}
