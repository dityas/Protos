/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import java.util.ArrayList;
import java.util.List;

import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.ParseSPUDD;

/*
 * @author adityas
 *
 */
public class IPOMDP extends POMDP {

	private static final long serialVersionUID = 4973485302724576384L;
	
	/*
	 * The strategy level and frame ID of the frame represented by the IPOMDP object
	 */
	public int frameID;
	public int stratLevel; 
	
	public List<POMDP> lowerLevelFrames = new ArrayList<POMDP>();

	public IPOMDP(String fileName) {
		super(fileName);
	}
	
	public void initializeFromParsers(ParseSPUDD parsedFrame) {
		/*
		 * Initializes the IPOMDP from the thinclab.ipomdpsolver.IPOMDPParser object
		 */
	}

}
