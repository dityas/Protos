/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

/*
 * @author adityas
 *
 */
public class MultiFrameMJ {
	
	/*
	 * Mj abstraction for IPOMDPs with multiple lower level frames
	 */
	
	/* store a list of all Mjs */
	public List<MJ> MJs = new ArrayList<MJ>();
	
	/* keep track of current time step */
	public int T = 0;
	
	private static final Logger LOGGER = Logger.getLogger(MultiFrameMJ.class);
	
	// -----------------------------------------------------------------------------------
	
	public MultiFrameMJ(Collection<MJ> MJs) {
		/*
		 * Set required attributes
		 */
		
		/* set MJs */
		this.MJs.addAll(MJs);
		LOGGER.debug("Multi frame MJ initialized");
		
		/* build initial step for all MJs */
		for (MJ mj : this.MJs)
			mj.buildTree();
	}

	// -----------------------------------------------------------------------------------
	
	
	
}
